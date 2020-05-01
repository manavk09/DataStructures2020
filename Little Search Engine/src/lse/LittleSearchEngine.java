package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/
		HashMap<String,Occurrence> keyMap = new HashMap<String,Occurrence>();
		Scanner scan = new Scanner(new File(docFile));
		while (scan.hasNext()) {
			String words = scan.nextLine();
			if (!words.trim().isEmpty() && !(words == null)){	
				String[] doc = words.split(" "); 
				for (int i = 0; i < doc.length; i++){
					String keyWord = getKeyword(doc[i]);
					if (keyWord != null) {
						if (keyMap.containsKey(keyWord)){
							Occurrence temp = keyMap.get(keyWord);
							temp.frequency++; 
							keyMap.put(keyWord, temp); 
						}
						else{
							Occurrence occ = new Occurrence (docFile, 1); 
							keyMap.put(keyWord, occ); 
						}
					}	
				}
			}
		}
		scan.close();
		return keyMap; 
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		/** COMPLETE THIS METHOD **/
		for(String word : kws.keySet()) {
			if(!(keywordsIndex.containsKey(word))) {
				ArrayList<Occurrence> occur = new ArrayList<Occurrence>();
				Occurrence key = kws.get(word); 
				occur.add(key);
				insertLastOccurrence(occur);
				keywordsIndex.put(word,occur);
			}
			else {
				ArrayList<Occurrence> occur = keywordsIndex.get(word);
				Occurrence key = kws.get(word);
				occur.add(key);
				insertLastOccurrence(occur);
				keywordsIndex.put(word, occur);	
			}	
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		/** COMPLETE THIS METHOD **/
		
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		if(word.equals("")) {
			return null;
		}
		//Changing the word to lower case;
		String lowerCase = word.toLowerCase();
		/*
		 * going to go through the word to check for punch
		 * if punch found and there is a letter next to it then continue
		 * else
		 * get the substring
		 */
		ArrayList<String> punctuation = new ArrayList<String>();
		punctuation.add(".");
		punctuation.add(",");
		punctuation.add("?");
		punctuation.add(":");
		punctuation.add(";");
		punctuation.add("!");
		int endIndex = 0;
		for(int i = 0; i < lowerCase.length(); i++) {
			String ch = Character.toString(lowerCase.charAt(i));
			if(i == lowerCase.length()-1) {
				if(punctuation.contains(ch)) {
					endIndex = i;
					break;
				}
			}
			else {
				if(punctuation.contains(ch) && !Character.isLetter(lowerCase.charAt(i+1))) {
					endIndex = i;
					break;
				}
			}
		}
		String newWord = lowerCase.substring(0,endIndex);
		if(endIndex == 0) {
			newWord = lowerCase.substring(0);
		}
		for(int i = 0; i<newWord.length();i++) {
			String ch = Character.toString(newWord.charAt(i));
			if(punctuation.contains(ch)||!Character.isLetter(newWord.charAt(i))) {
				return null;
			}
		}
		if(noiseWords.contains(newWord)) {
			return null;
		}
		return newWord;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/
		
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		if(occs.size()<2) {
			return null;
		}
		//create the mid arrayList to return
		ArrayList<Integer> mid = new ArrayList<Integer>();
		//item that we are finding the right place to insert which is stored at the end of the list occs
		Occurrence item = occs.remove(occs.size()-1);
		//this case covers the possibility of only having two items in the initial list
		if(occs.size()==1) {
			mid.add(0);
			if(occs.get(occs.size()-1).frequency<item.frequency) {
				occs.add(0,item);
			}
			else {
				occs.add(item);
			}
			return mid;
		}
		//if the initial list has more than 2 items we perform binary search to find the right place to insert
		//our binary search will be for decreasing order
		int low = 0;
		int high = occs.size()-1;
		int middle = (low+high)/2;
		while(low<=high) {
			//we need to make sure that mid is changing when we are iterating
			middle = (low+high)/2;
			mid.add(middle);
			if(occs.get(middle).frequency == item.frequency) {
				break;
			}
			//when frequency of the middle is greater than the insert freq, we need to move low 1+mid;
			else if(occs.get(middle).frequency > item.frequency) {
				low = middle + 1;
			}
			else {
				high = middle -1;
			}
			
		}
		//when the values come out of the loop there is a possiblity that high is less than low, so in that case we insert at low
		if(low>high) {
			occs.add(low,item);
		}
		else {
			occs.add(middle,item);
		}
		return mid;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		/** COMPLETE THIS METHOD **/
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		//change the keywords to all lowercase
		kw1.toLowerCase();
		kw2.toLowerCase();
		//creating a docList
		ArrayList<String> docList = new ArrayList<String>();
		//creating a list to hold all the occurences 
		ArrayList<Occurrence> keyword1 = keywordsIndex.get(kw1);
		ArrayList<Occurrence> keyword2 = keywordsIndex.get(kw2);
		//now if they are both empty, you can return empty docList
		if(keyword1 == null && keyword2 == null){
			return docList;
		}
		//creating a master occurrence list
		ArrayList<Occurrence> ocur = new ArrayList<Occurrence>();
		//adding all the elements from 1 to the ocur
		if(keyword1 != null){
			ocur.addAll(keyword1);
		}
		//adding all the elements from 2 to the ocur
		if(keyword2 != null){
			ocur.addAll(keyword2);
		}
		
		int freq = 0;
		int toRemove = 0;
		String doc = "";
		/*
		 * loop through till we hit the required 5 doc list length
		 * break the loop if there is nothing left in the occur list
		 * get the biggest frequency in the occur list
		 * check if it is in the docList already
		 * if not add and remove it from occur
		 * if it is, don't add it but still remove it from occur
		 */
		while(docList.size()<5){
			//going to run through the occur, list to get the biggest frequency
			for(int k = 0; k < ocur.size(); k++){
				//whenever it finds a higher frequency change doc and freq to that 
				if(ocur.get(k).frequency > freq){
					freq = ocur.get(k).frequency;
					doc = ocur.get(k).document;
					toRemove = k;
				}	
			}
			//check if the doc is already in the docList, if not add it 
			if(!docList.contains(doc)){
				docList.add(doc);
			}
			/*
			 * reset frequency 
			 * remove the doc from occurs
			 */
			freq = 0;
			ocur.remove(toRemove);
			//if there is nothing left in occurs list then break out of the loop
			if(ocur.size() == 0){
				break;
			}
		}
		//return all of the documents
		return docList;
	}

}
