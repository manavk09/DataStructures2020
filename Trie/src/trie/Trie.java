package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie. 
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	// prevent instantiation
	private Trie() { }
	
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	public static TrieNode buildTrie(String[] allWords) {
		/** COMPLETE THIS METHOD **/		
		// FOLLOWING LINE IS A PLACEHOLDER TO ENSURE COMPILATION
		// MODIFY IT AS NEEDED FOR YOUR IMPLEMENTATION
		//initializing a root which will always be null and you start to branch off from here
		TrieNode root = new TrieNode(null, null, null);
		//this will be when there is nothing in the array 
		if(allWords.length < 1) {
			return root;
		}
		//initializing the first child as it will be the first word in the array(always)
		root.firstChild = new TrieNode(new Indexes(0,(short)(0),(short)(allWords[0].length() - 1)), null, null);
		/*
		 * Iterate through the array one by one
		 * compare the prefix of the word at i with the words in the tree already
		 * if full match
		 * 	go to the child
		 * if partial match 
		 * break and add it there 
		 */
		for(int i = 1; i < allWords.length; i++) {
			/*
			 * creating a ptr to go through the tree
			 * creating a prev that will lag 1 behind ptr
			 * creating an int that will hold value of the common prefix indexes
			 * creating variables for all the indexes
			 */
			TrieNode ptr = root.firstChild;
			TrieNode prev = root.firstChild;
			int prefixInd = 0;
			int startInd = 0;
			int endInd = 0; 
			int wordInd = 0;
			//takes in the word we are at in the array(insetWord)
			String InsertWord = allWords[i];
			//start to itterate through the tree to compare
			while(ptr != null) {
				/*
				 * hold the indexes of the current node for comparison
				 */
				startInd = ptr.substr.startIndex;
				endInd = ptr.substr.endIndex;
				wordInd = ptr.substr.wordIndex;
				//if the word word length is less than start index we are at right now, that means that we need to move across
				if(startInd > InsertWord.length()) {
					prev = ptr;
					ptr = ptr.sibling;
					continue;
				}
				//get the prefix that the current node word and the insert word have in common
				String curNodeWord = allWords[wordInd].substring(startInd, endInd+1);
				String prefix = commonPrefix(curNodeWord, InsertWord.substring(startInd));
				//capture all the indexes until they stop matching
				prefixInd = prefix.length()-1;
				if(prefixInd >= 0) {
					prefixInd = prefixInd + startInd;
				}
				//there is no match and we move accross the tree (same level)
				if(prefixInd < 0) {
					prev = ptr;
					ptr = ptr.sibling;
				}
				//there is a full match so now we go down a level and compare
				else if(prefixInd == endInd){
						prev = ptr;
						ptr = ptr.firstChild;
					}
				//there is only a partial match so we break out
				else if (prefixInd < endInd){ 
						prev = ptr;
						break;
					}
				}
			//this will only execute if the word had no matching prefix so we jsut add a sibling
			if(ptr == null) {
				prev.sibling = new TrieNode(new Indexes(i, (short)prev.substr.startIndex, (short)(InsertWord.length()-1)), null, null);
			} else {
				//storing the current node so we don't lose everything under it
				TrieNode currFirstChild = prev.firstChild;
				//creating indexes of the word that we split into a prefix
				Indexes currWordNewIndexes = new Indexes(prev.substr.wordIndex, (short)(prefixInd+1), prev.substr.endIndex);
				//changing the node to  stop at the prefix end
				prev.substr.endIndex = (short)prefixInd;
				//completing the parent word that was changed
				prev.firstChild = new TrieNode(currWordNewIndexes, null, null);
				prev.firstChild.firstChild = currFirstChild;
				//adding the new word
				prev.firstChild.sibling = new TrieNode(new Indexes((short)i, (short)(prefixInd+1), (short)(InsertWord.length()-1)), null, null);
			}
		}
		
		return root;
	}
		
	private static TrieNode bestMatch(String Prefix, TrieNode subtreeRoot, String[] allWords) {
				///if the subtree has no children we send it back
				/*
				 * 
				 */
				TrieNode ptr = subtreeRoot;
				
				if(ptr!=null) {
				
				String curNodeWord = allWords[ptr.substr.wordIndex].substring(ptr.substr.startIndex);
				String prefix = commonPrefix(Prefix,curNodeWord);
				
				if(ptr.sibling == null && ptr.firstChild == null ) {
					return ptr;
				}
				if(prefix.length()>0 && prefix.length()==Prefix.length()) {
					return bestMatch(curNodeWord.substring(prefix.length()),ptr.firstChild,allWords);
				}
				else if(prefix.length()<= 0 ) {
					return bestMatch(Prefix,ptr.sibling,allWords);
				
				}
			}
				return ptr;
		}


	private static String commonPrefix(String word1, String word2) {
		String  prefix = new String("");
		//going through the words to find the prefix
		for(int i = 0; i < word1.length(); i++) {
			//comparing the  characters to figure out the prefix
			if(word1.charAt(i)==word2.charAt(i)) {
				prefix += word1.charAt(i);
			}
			else {
				break;
			}
		}

		return  prefix;
	}

	
	

	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root,
										String[] allWords, String prefix) {
		/** COMPLETE THIS METHOD **/
		
		// FOLLOWING LINE IS A PLACEHOLDER TO ENSURE COMPILATION
		// MODIFY IT AS NEEDED FOR YOUR IMPLEMENTATION
		if(root == null) {
			return null;
		}
		ArrayList<TrieNode> completeList = new ArrayList<>();
		TrieNode ptr = root;
		
		while(ptr != null) {
			
			if(ptr.substr != null) { 
				System.out.println(ptr);
				String word = allWords[ptr.substr.wordIndex];
				String curNode = word.substring(0, ptr.substr.endIndex+1);
				if(word.startsWith(prefix) || prefix.contains(curNode)) {
					if(ptr.firstChild == null) { 
						completeList.add(ptr);
						ptr = ptr.sibling;
					} else { 
						completeList.addAll(completionList(ptr.firstChild, allWords, prefix));
						ptr = ptr.sibling;
						
					}
				} else {
					ptr = ptr.sibling;
				}
			}
			else {
				ptr = ptr.firstChild;
			}
			
		}
		if(completeList.isEmpty()) {
			return null;
		}
		else {
		
		return completeList;
	}
}
	
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }
