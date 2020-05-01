package friends;

import java.util.ArrayList;

import structures.Queue;
import structures.Stack;

public class Friends {

	/**
	 * Finds the shortest chain of people from p1 to p2.
	 * Chain is returned as a sequence of names starting with p1,
	 * and ending with p2. Each pair (n1,n2) of consecutive names in
	 * the returned chain is an edge in the graph.
	 * 
	 * @param g Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null or empty array list if there is no
	 *         path from p1 to p2
	 */
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) {
		
		/** COMPLETE THIS METHOD **/
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE COMPILER HAPPY
		// CHANGE AS REQUIRED FOR YOUR IMPLEMENTATION
		//this will contain the path that is the shortest between p1 and p2
		ArrayList<String> resultPath = new ArrayList<String>();
		// if either p1 or p2 has no friends then shortestChain is immediately 0
		if(g.members[g.map.get(p2)].first==null || g.members[g.map.get(p1)].first == null){
			return resultPath;
		}
		//this will store the length of the members
		int memLen = g.members.length;
		//this will keep track of all of the people we visited
		boolean[] visited = new boolean[memLen];
		//this will store all the names we visit, to help use traverse back
		Person[] pathName = new Person[memLen];
		//this is queue for the bfs
		Queue<Person> q = new Queue<>();		
		// Starting bfs
		//enqueue person 1
		q.enqueue(g.members[g.map.get(p1)]); 
		//mark that person as visited
		visited[g.map.get(p1)] = true;	
		//run bfs on the neighbors now
		while(!q.isEmpty()){
			//current person 
			Person curPerson = q.dequeue();
			//mark that person as visited so we don't come back
			visited[g.map.get(curPerson.name)] = true; 
			//check all the neighbor of the cur person
			for(Friend neighbor = curPerson.first; neighbor!=null; neighbor = neighbor.next){	
				// take the friend
				Person friend = g.members[neighbor.fnum];
				//if the friend is not visited visit it and enqueue		
				if(!visited[g.map.get(friend.name)]){
					visited[g.map.get(friend.name)] = true;
					q.enqueue(friend);
							
					//now we want to store the friend that will help us later come back and get the path	
					pathName[g.map.get(friend.name)] = curPerson;
							
					// If we reach p2 now its time to back track and get our path
					if(friend.name.equals(p2)){
						// now we go back from the friend// to person that brought us to friend until we reach p1
						while(!friend.name.equals(p1)){
								resultPath.add(0, friend.name);
								friend = pathName[g.map.get(friend.name)];
						}
								
						resultPath.add(0, friend.name);
						return resultPath;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null or empty array list if there is no student in the
	 *         given school
	 */
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {
		/** COMPLETE THIS METHOD **/
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE COMPILER HAPPY
		// CHANGE AS REQUIRED FOR YOUR IMPLEMENTATION
		//this will store all the cliques
		ArrayList<ArrayList<String>> resultCliques = new ArrayList<>(); 
		//length of the members
		int memLen = g.members.length;
        //now we want to go through each memeber and once we find school we start bfs there
		for(Person person : g.members){
			//school of the person
			String sch = person.school;
            //if the person school is the school given we start bfs
			if(sch!=null && sch.equals(school)){
               //this is so we can check if the name is any other cliques so we dont add
				boolean inClique = false;

                if(resultCliques.size() == 0) {
                    resultCliques.add(bfsCliques(g, school, person,memLen));
                }

                //now we want to go through the temp clique and see if the names are in the master
                for(ArrayList<String> temp : resultCliques) {
                    //if the name is in the temp then we set the contained to true
                	if(temp.contains(person.name)) {
                        inClique = true;
                	}
                }
                //if the name isnt in any clique we can add
                if(inClique==false) {
                    resultCliques.add(bfsCliques(g, school, person,memLen));
                }
            }
        }
        return resultCliques;
    }

	private static ArrayList<String> bfsCliques(Graph g, String school, Person person, int memLen)
	    {
	       //we are creating list of cliques
			ArrayList<String> bfsClique = new ArrayList<>();
			bfsClique.add(person.name);
			//creating a visited array to keep track of all the people visited
	        boolean visited[] = new boolean[memLen];
	        //in order to run bfs we need a queue
	        Queue<Person> q = new Queue<>();
	        //starting bfs
	        q.enqueue(person);
	        //get the index of the person and then mark it as visited
	        int ind = g.map.get(person.name);
	        visited[ind] = true; 
	        //run until we have visited all the friends
	        while(!q.isEmpty()){
	            //current person
	        	Person curPerson = q.dequeue();
	            //go to all of the neighbors and mark them as visited and check for school
	        	for(Friend neighbor = curPerson.first; neighbor!=null; neighbor = neighbor.next){
	                //friend of the current person
	                Person friend = g.members[neighbor.fnum];
	                String friendSchool = friend.school;
	                String friendName = friend.name;
	        
	                if(!(friendSchool!=null && friendSchool.equals(school))){
	                    continue;
	                }
	                //if not visited go to the friend and enqueue it
	                if(!visited[neighbor.fnum]){
	                    visited[neighbor.fnum] = true;
	                    q.enqueue(friend);
	                    bfsClique.add(friendName);
	                }
	            }
	        }

	        return bfsClique;
	    }
	
	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null or empty array list if there are no connectors.
	 */
	public static ArrayList<String> connectors(Graph g) {
		/** COMPLETE THIS METHOD **/
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE COMPILER HAPPY
		// CHANGE AS REQUIRED FOR YOUR IMPLEMENTATION
			boolean visited[] = new boolean[g.members.length];
			ArrayList<String> resultConnectors = new ArrayList<String>();
			
			int[] dnum = new int[g.members.length];
			int[] low = new int[g.members.length];
			int[] backTrack = new int[g.members.length];
			int found = 0;
			int backLen = backTrack.length;
			for(int i=0; i<backLen; i++) {
				backTrack[i] = Integer.MAX_VALUE;
			}	
			for(int i=0; i < g.members.length; i++){
				if(!visited[i]) {
					found = dfsConnector(g, resultConnectors, visited, dnum, low, backTrack, i, found);
				}
			}
			
			return resultConnectors;
	}

		private static int dfsConnector(Graph g, ArrayList<String> resultConnectors,boolean[] visited, int[] dnum, int[] low, int[] backTrack, int start, int found){
			// Start dfs with start
			visited[start] = true;
			
			// found is the dfs number, each time a new node is found, found gets incremented by 1
			found = found + 1;
			dnum[start] = found;
			low[start] = found;
			
			int children = 0;
			
			// Visit each neighbor:
			for(Friend neighbor = g.members[start].first; neighbor!=null; neighbor = neighbor.next) { 
				int neigh = neighbor.fnum;
				
				if(!visited[neigh]){
					visited[neigh] = true;
					children = children + 1;
					backTrack[neigh] = start;
					
					// recursively call Dfs
					found = dfsConnector(g, resultConnectors, visited, dnum, low, backTrack, neigh, found);
					
					
					low[start] = Math.min(low[start], low[neigh]);
					
					// start is variable that we passed, and start is a connector if and only if start is root and has more than 1 child or start is not root
					if((backTrack[start] == Integer.MAX_VALUE) && children > 1){
						String curName = g.members[start].name;
						if(!resultConnectors.contains(curName)) {
							resultConnectors.add(curName);
						}
					}
					
					if((backTrack[start] != Integer.MAX_VALUE) && low[neigh] >= dnum[start]){
						String curName = g.members[start].name;
						if(!resultConnectors.contains(curName)) {
							resultConnectors.add(curName);
						}
					}
					
				}
				else if(neigh != backTrack[start]) {
					low[start] = Math.min(low[start], dnum[neigh]); 
				}
			}
			return found;
		}
}
