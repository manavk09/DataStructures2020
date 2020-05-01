package app;

import java.io.*;

import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	//removing any leading spaces
    	expr = expr.trim();
    	//replacing all spaced with no space
    	expr = expr.replaceAll(" ", "");
    	//removing all digits from the string;
    	String newExpr = expr.replaceAll("[0123456789]","");
    	//creating a new array with all the tokens and now we are going to start comparing them
    	ArrayList<String> tokens = new ArrayList<String>();
		StringTokenizer token = new StringTokenizer(newExpr,delims);
		while(token.hasMoreTokens()) {
			String t = token.nextToken();
			tokens.add(t);
		}//now the new array has all variable values and we can start comparing if it is to go in vars or arrays
    	for(int i = 0; i < tokens.size(); i++) {
    		String tok = tokens.get(i);
    		int len = tok.length();
    		int loc = newExpr.lastIndexOf(tok);
    		int check = len + loc;
    		//check if loc == to '['
    		if(check>=newExpr.length()) {
    			if(!vars.contains(new Variable(tok))) {
    				vars.add(new Variable(tok));
    			}
    			continue;
    		}
    		else if(newExpr.charAt(check)=='[' && !arrays.contains(new Array(tok))) {	
    			arrays.add(new Array(tok));
    		}
    		else if(newExpr.charAt(check)!='[' && !vars.contains(new Variable(tok))) {
    			vars.add(new Variable(tok));
    		}    		
    	} 	 	
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	// following line just a placeholder for compilation
        expr = expr.trim();
        expr = expr.replaceAll("\\s", "");
        Stack<Float> result = new Stack<>();
        Stack<Character> pOper = new Stack<>();
        Stack<Character> bOper = new Stack<>();
        float ans = (float) 0;     
        int i = 0;
        while(i <= expr.length()-1){
        	String pos = "";
        	char ope;
            float num = 0;
            if(Character.isAlphabetic(expr.charAt(i))){            	
                pos = pos + expr.charAt(i); 
                i=i+1;         
                if(i < expr.length()) {
                    while (Character.isAlphabetic(expr.charAt(i))) {
                        pos += expr.charAt(i);
                        i=i+1;
                        if(i > expr.length()-1) {
                            break;
                        }
                    }
                    if (i < expr.length() && expr.charAt(i) == '[') {
                    	int newStringPos = i +1;
                    	//calculate everything inside the bracket
                    	float inside = arrays.get(arrays.indexOf(new Array(pos))).values[(int) evaluate(expr.substring(newStringPos), vars, arrays)];
                        result.push(inside);
                        while(i < expr.length()){
                            if(expr.charAt(i) == '['){
                                bOper.push('[');
                                }
                            else if(expr.charAt(i) == ']' && !bOper.isEmpty()){
                                bOper.pop();
                                }
                            i++;
                            if(bOper.isEmpty()) {
                                break;
                            }
                        }
                    } else{
                    	float item = vars.get(vars.indexOf(new Variable(pos))).value;
                        result.push(item);
                    }

                } else{
                	float item = vars.get(vars.indexOf(new Variable(pos))).value;
                    result.push(item);
                }
                if(i > expr.length()-1){
                    break;
                }
                pos = "";
            }
            else if(Character.isDigit(expr.charAt(i))){
                pos = pos + expr.charAt(i); 
                i++;
                if(i < expr.length()) {
                    while (Character.isDigit(expr.charAt(i))) {
                        pos = pos + expr.charAt(i); 
                        i++;
                        if(i >= expr.length()) {
                            break;
                        }
                    }
                }
                result.push(Float.parseFloat(pos)); 
                pos = "";
            }
            else if(expr.charAt(i) == '('){
            	int newStringLoc = i + 1;
            	float inside = evaluate(expr.substring(newStringLoc), vars, arrays);
                result.push(inside);
                while(i < expr.length()){
                    if(expr.charAt(i)=='('){
                        pOper.push('(');
                        }
                    else if(expr.charAt(i)==')' && !pOper.isEmpty()){
                        pOper.pop();
                        }
                    i++;
                    if(pOper.isEmpty()) {
                        break;
                    }
                }
            }
            else if(expr.charAt(i) == ')' || expr.charAt(i) == ']'){
                while(!result.isEmpty()){
                    ans = ans + result.pop();
                }
                return ans;
            }
            else {
                ope = expr.charAt(i); 
                i++;
                if(Character.isDigit(expr.charAt(i))){
                    pos = ""; 
                    pos = pos + expr.charAt(i); 
                    i++;
                    if(i < expr.length()) {
                        while (Character.isDigit(expr.charAt(i))) {
                            pos = pos + expr.charAt(i); 
                            i++;
                            if(i >= expr.length()) {
                                break;
                            }
                        }
                    }
                    num = Float.parseFloat(pos); 
                    pos = "";
                } 
                else if(Character.isAlphabetic(expr.charAt(i))){
                    pos = pos + expr.charAt(i); 
                    i++;
                    if(i < expr.length()) {
                        while (Character.isAlphabetic(expr.charAt(i))) {
                            pos = pos + expr.charAt(i); 
                            i++;
                            if(i >= expr.length()) {
                                break;
                            }
                        }
                        if (i < expr.length() && expr.charAt(i) == '[') {
                            float insideNum = arrays.get(arrays.indexOf(new Array(pos))).values[(int) evaluate(expr.substring(i + 1), vars, arrays)];
                        	num = insideNum;
                            while(i < expr.length()){
                                if(expr.charAt(i) == '[') {
                                	char c = expr.charAt(i);
                                    bOper.push(c);
                                }
                                else if(expr.charAt(i) == (']') && !bOper.isEmpty()) {
                                    bOper.pop();
                                }
                                i++;
                                if(bOper.isEmpty()) {
                                    break;
                                }
                            }
                        } 
                        else {
                        	float insideNum = vars.get(vars.indexOf(new Variable(pos))).value;
                            num = insideNum; 
                        	pos = "";
                        }
                    } 
                    else {
                    	float insideNum = vars.get(vars.indexOf(new Variable(pos))).value; 
                        num = insideNum; 
                    	pos = "";
                    }
                    if(i > expr.length()) {
                        break;
                    }
                    pos = "";
                } 
                else if(expr.charAt(i) == '('){
                	float insidePran = evaluate(expr.substring(i+1), vars, arrays);;
                    num = insidePran;
                    while(i < expr.length()){
                        if(expr.charAt(i) == '(') {
                        	char c = expr.charAt(i);
                            pOper.push(c);
                        }
                        else if(expr.charAt(i)==')' && !pOper.isEmpty()) {
                            pOper.pop();
                        }
                        i++;
                        if(pOper.isEmpty()) {
                            break;
                        }
                    }
                } 
                else if(expr.charAt(i) == ')' || expr.charAt(i) == ']'){
                    ans = 0;
                    while(!result.isEmpty()) {
                        ans = ans + result.pop();
                    }
                    i++; 
                    return ans;
                }
                if(ope=='/') {
                	float item = result.pop()/num;
                	result.push(item);      
                }
                if(ope=='*') {
                	float item = result.pop()*num;
                	result.push(item);
                }
                if(ope=='+') {
                	result.push(num);
                }
                if(ope== '-') {
                	float item = num * -1;
                	result.push(item);
                }
            }
        }
        ans = 0;
        while(!result.isEmpty()) {
            ans = ans + result.pop();
        }
        return ans;
    }
}