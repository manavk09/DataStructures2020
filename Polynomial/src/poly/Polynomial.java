package poly;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class implements evaluate, add and multiply for polynomials.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param sc Scanner from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 * @return The polynomial linked list (front node) constructed from coefficients and
	 *         degrees read from scanner
	 */
	public static Node read(Scanner sc) 
	throws IOException {
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}
	
	/**
	 * Returns the sum of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list
	 * @return A new polynomial which is the sum of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node add(Node poly1, Node poly2) {
		/** COMPLETE THIS METHOD **/
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE THIS METHOD COMPILE
		// CHANGE IT AS NEEDED FOR YOUR IMPLEMENTATION
		Node ptr1 = poly1;
		Node ptr2 = poly2;
		Node head = null;
		Node ptr = null;
		while(ptr1 != null || ptr2 != null) {
			if(ptr2 == null) {
					Node deg = new Node(ptr1.term.coeff,ptr1.term.degree,null);
					if(head == null) {
						head = deg;
						ptr = head;
					}
					else {
						ptr.next = deg;
						ptr = ptr.next;
					}
					ptr1 = ptr1.next;
				}

			else if(ptr1 == null) {
				Node deg = new Node(ptr2.term.coeff,ptr2.term.degree,null);
				if(head == null) {
					head = deg;
					ptr = head;
				}	
				else {
					ptr.next = deg;
					ptr = ptr.next;
				}
			
				ptr2 = ptr2.next;
			}
	
			else if(ptr1.term.degree < ptr2.term.degree) {
				Node deg = new Node(ptr1.term.coeff,ptr1.term.degree,null);
				if(head == null) {
					head = deg;
					ptr = head;
				}
				else {
					ptr.next = deg;
					ptr = ptr.next;
				}
				ptr1 = ptr1.next;
				
			}
			
			else if(ptr1.term.degree > ptr2.term.degree) {
				Node deg = new Node(ptr2.term.coeff, ptr2.term.degree,null);
				if(head == null) {
					head = deg;
					ptr = head;
				}
				else {
					ptr.next = deg;
					ptr = ptr.next;
				}
				ptr2 = ptr2.next;
			}

			else if(ptr1.term.degree == ptr2.term.degree){
				Node deg = new Node(ptr1.term.coeff+ptr2.term.coeff, ptr1.term.degree,null);
				if( head == null) {
					head = deg;
					ptr = head;
				}
				else {
					ptr.next = deg;
					ptr = ptr.next;
				}
				ptr1 = ptr1.next;
				ptr2 = ptr2.next;
			}
		}
		
		Node nptr = head;
		Node prev = null;
		while(nptr != null && nptr.term.coeff == 0) {
			head = nptr.next;
			nptr = head;
		}
		while(nptr != null) {
			while(nptr != null && nptr.term.coeff != 0) {
				prev = nptr;
				nptr = nptr.next;
			}
			if(nptr == null) {
				break;
			}
			prev.next = nptr.next;
			nptr = prev.next;
			
		}
		return head;
	}
	
	/**
	 * Returns the product of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list)
	 * @return A new polynomial which is the product of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node multiply(Node poly1, Node poly2) {
		/** COMPLETE THIS METHOD **/
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE THIS METHOD COMPILE
		// CHANGE IT AS NEEDED FOR YOUR IMPLEMENTATION
		Node ptr1 = poly1;
		Node ptr2 = poly2;
		Node head = null;
		Node ptr = null;
		if(poly1 == null || poly2 == null) {
			return null;
		}
		while(ptr1 != null) {
			while(ptr2 != null) {
				Node deg = new Node(ptr1.term.coeff*ptr2.term.coeff, ptr1.term.degree+ptr2.term.degree, null);
				if(head == null) {
					head = deg;
					ptr = deg;
				}else {
					ptr.next = deg;
					ptr = ptr.next;
				}
				ptr2 = ptr2.next;
			}
			ptr2 = poly2;
			ptr1 = ptr1.next;
		}
		Node ptr3 = head;
		Node ptr4 = null;
		while(ptr3 != null && ptr3.next != null) {
			ptr4 = ptr3;
			while(ptr4.next != null) {
				if(ptr3.term.degree == ptr4.next.term.degree) {
					ptr3.term.coeff = ptr3.term.coeff + ptr4.next.term.coeff;
					ptr4.next = ptr4.next.next;
				}
				else {
					ptr4 = ptr4.next;
				}
			}
			
			ptr3 = ptr3.next;
		}
		Node headprev = head;
		Node headcurr = head.next;
		 do{
		        if (headcurr.term.degree < headprev.term.degree){
		            int tempdeg = headcurr.term.degree;             
		            headcurr.term.degree = headprev.term.degree;       
		            headprev.term.degree = tempdeg;                  

		            float tempcoeff = headcurr.term.coeff;
		            headcurr.term.coeff = headprev.term.coeff;
		            headprev.term.coeff = tempcoeff;

		            headprev = head;
		            headcurr = head.next;
		        } 

		        headprev = headprev.next;
		        headcurr = headcurr.next;
		    } while(headcurr!=null);
			
		 Node nptr = head;
			Node prev = null;
			while(nptr != null && nptr.term.coeff == 0) {
				head = nptr.next;
				nptr = head;
			}
			while(nptr != null) {
				while(nptr != null && nptr.term.coeff != 0) {
					prev = nptr;
					nptr = nptr.next;
				}
				if(nptr == null) {
					break;
				}
				prev.next = nptr.next;
				nptr = prev.next;
				
			}
			return head;
		
		 
	}
		
	/**
	 * Evaluates a polynomial at a given value.
	 * 
	 * @param poly Polynomial (front of linked list) to be evaluated
	 * @param x Value at which evaluation is to be done
	 * @return Value of polynomial p at x
	 */
	public static float evaluate(Node poly, float x) {
		/** COMPLETE THIS METHOD **/
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE THIS METHOD COMPILE
		// CHANGE IT AS NEEDED FOR YOUR IMPLEMENTATION
		if(poly==null) {
			return 0;
		}
		Node ptr = poly;
		float sum =(float) (ptr.term.coeff * Math.pow(x, ptr.term.degree));
		Node ptr2 = ptr.next;
		while(ptr2 != null) {
			sum =(float) (ptr2.term.coeff * (Math.pow(x,ptr2.term.degree)))+ sum;
			ptr2 = ptr2.next;
		}
		
		return sum;
	}
	
	/**
	 * Returns string representation of a polynomial
	 * 
	 * @param poly Polynomial (front of linked list)
	 * @return String representation, in descending order of degrees
	 */
	public static String toString(Node poly) {
		if (poly == null) {
			return "0";
		} 
		
		String retval = poly.term.toString();
		for (Node current = poly.next ; current != null ;
		current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}	
}