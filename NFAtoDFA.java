import java.io.*;
import java.util.*;
public class NFAtoDFA {
	public static void main(String [] args){	
		String inputFile = args[0]; 
		String outFile = args[1];
		File outputFile = new File(outFile);
		int numStates;
		String [] alphabet;
		int numLetters;
		String holderVar;
		ArrayList<Integer>[][] states;
		String trans;
		String holder;
		String [] parts;
		int qa;
		int startState;
		int [] acceptStates = null;
		int qb;
		Scanner scanner = null;
		File input = new File(inputFile);
		try{
			scanner = new Scanner(input);
		}
		catch(IOException e){
			System.out.println(e.getMessage());
		}
		numStates = scanner.nextInt();
		scanner.nextLine();
		holderVar = scanner.nextLine();
		numLetters = holderVar.length();
		alphabet = new String[numLetters + 1];
		//Read in the alphabet to array alphabet
		for(int i = 0; i<numLetters; i++){
			alphabet[i] = holderVar.substring(i, i + 1);
		}
		alphabet[numLetters] = "e";
		states = (ArrayList<Integer>[][]) new ArrayList[numStates][numLetters + 1];
		for(int i = 0; i<numStates; i++){
			for(int j = 0; j < numLetters + 1; j++){
				states[i][j] = new ArrayList<Integer>();
			}
		}
		trans = scanner.nextLine();
		while(scanner.hasNextLine()){
			if(trans.length()<2){
				break;
			}
			parts = trans.split(" ");
			qa = Integer.parseInt(parts[0]);
			holder = parts[1].substring(1, 2);
			qb = Integer.parseInt(parts[2]);
			trans = scanner.nextLine();
			boolean found = true;
			int index = 0;
			//checks if transition is in the alphabet
			while (found){
				if(alphabet[index].compareTo(holder) == 0){
					found = false;
				}
				else{
					index++;
				}
			}
			states[qa - 1][index].add(qb);
		}
		startState = scanner.nextInt();
		scanner.nextLine();
		trans = scanner.nextLine();
		parts = trans.split(" ");
		int numAcceptStates = parts.length;
		acceptStates = new int[numAcceptStates];
		//Reads in the accept states
		for(int i = 0; i < parts.length; i++){
			acceptStates[i] = Integer.parseInt(parts[i]);
		}
		//This method returns an arraylist of dfasates
		//Each element in the arraylist is a unique state of the DFA
		ArrayList<dfaState> finalDfa = NFAConversion(startState, states, numLetters);
		int [][] dfaState = dfaConversion(finalDfa, numLetters, states);
		PrintWriter printer = null; 
		try{
			printer = new PrintWriter(outputFile);
		}
		catch(IOException e){
			System.out.println(e.getMessage());
		}
		printer.println(dfaState.length);
		for(int i = 0; i < alphabet.length - 1; i++){
			printer.print(alphabet[i]);
		}
		printer.println();
		for(int j = 0; j < finalDfa.size(); j++){
			for(int k = 0; k < numLetters; k++){
				if(dfaState[j][k] != -1){
					printer.println((j + 1) + " "  + "'" + alphabet[k] + "'" + " " + (dfaState[j][k] + 1));
				}
			}
		}
		printer.println(1);
		for(int g = 0; g<finalDfa.size(); g++){
			if(isAcceptState(finalDfa.get(g), acceptStates)){
				printer.print((g + 1) + " ");
			}
		}
		printer.close();
		//used for testing purposes
		//String fileName = "C:\\Users\\Ethan\\Desktop\\dfa6Input.txt";         
		//acceptOrReject(dfaState, acceptStates, fileName, alphabet, finalDfa);
	
	}
	//this method was made purely for testing purposes, tests input strings to see if they would be accepted or rejected
	public static void acceptOrReject(int[][]dfaState, int[]acceptStates, String fileName, String [] alphabet, ArrayList<dfaState> finalDfa){
		File test = new File(fileName);
		Scanner scanner = null;
		try{
			scanner = new Scanner(test);
		}
		catch(IOException e){
			System.out.println(e.getMessage());
		}
		int endState;
		String trans;
		while(scanner.hasNextLine()){
			trans = scanner.nextLine();
			endState = simulate(0, trans, dfaState, alphabet);
			if(isAcceptState(finalDfa.get(endState), acceptStates)){
				System.out.println("Accept");
			}
			else{
				System.out.println("Reject");
			}
		}
	}
	/*
	 * This method takes the 2D array that represents the NFA and returns an arraylist of the unique states of the DFA
	 * Each state of the DFA is numbered by its index in the arraylist
	 * Params: The start state of the NFA, the 2D array of arraylists that represents the NFA, the number of leters in the alphabet
	 */
	public static ArrayList<dfaState> NFAConversion(int startState, ArrayList<Integer>[][] states, int numLetters){
		ArrayList<dfaState> dfa = new ArrayList<dfaState>(); //array list of all the dfa states
		ArrayList<Integer> starter = new ArrayList<Integer>();
		starter = nextStates(states, startState, numLetters, numLetters);
		starter.add(startState);
		dfaState start = new dfaState(starter);//check if numLetters accounts for indexing
		Queue<dfaState> q = new LinkedList<dfaState>();
		q.add(start); //start state is already added to queue so that the queue is not empty
		dfa.add(start); //add start to list of all dfa states
		dfaState temp; 
		ArrayList<Integer> [] a = null;
		int emptySetCount = 0;
		//This queue contains dfa states, each time a new dfa state is seen, a dfastate is added
		while(!q.isEmpty()){
			temp = new dfaState(q.remove().states);
			//For each of the letters in the alphabet
			for(int i = 0; i < numLetters; i++){ 
				ArrayList<Integer> allTheNfa = new ArrayList<Integer>();
				a = (ArrayList<Integer>[]) new ArrayList[temp.size]; //array of arraylists to store the nfa states that correlate to a dfa state
				//For every NFA state in the DFA state, add the possible NFA states that it can get to with a certain symbol to an array
				//array a is an array of arraylists, each index of the array contains all of the NFA states that an NFA state can get to from symbol # i
				for(int j = 0; j < temp.states.size(); j++){
					a[j] = nextStates(states, temp.getStateNumber(j), i, numLetters); //i in this is the index of the transition
				}
				//Adds all of the arraylists of a into one arraylist called allTheNFA
				//allTheNfa contains all of the NFA states that the NFA can get to from one symbol
				for(int x = 0; x < a.length; x++){
					for(int z = 0; z < a[x].size(); z++){
						if(!allTheNfa.contains(a[x].get(z))){
							allTheNfa.add(a[x].get(z)); //gets all the nfa states that correlate to a dfa state and stores them in an array list
						}
					}
				}
				
				//If allTheNfa is empty, there are no transitions out of the DFA state for a given letter
				//Add a dfa state with empty = 1 to signify an empty DFA state
				if(allTheNfa.size() == 0 && emptySetCount == 0){
					dfaState empty = new dfaState(allTheNfa);
					dfa.add(empty);
					emptySetCount++;
					continue;
				}
				//Don't want to add more than one empty DFA state
				if(allTheNfa.size() == 0){
					continue;
				}
				//If we have seen the dfa state, we don't want to enqueue or add it to the arraylist of unique states
				if(seen(dfa, allTheNfa) == false){ 
					dfaState e = new dfaState(allTheNfa);
					q.add(e);
					dfa.add(e);
				}
			}
		}
		return dfa;
	}
	/* This method takes in an ArrayList of type dfa states which represents
	 * all the nfa states that are one dfa state called dfa, 
	 * the number of letters called numLetters and a two-dimensional
	 * array of ArrayLists that is the original NFA
	 * this method converts the NFA into a DFA
	 */
	public static int[][] dfaConversion(ArrayList<dfaState> dfa, int numLetters, ArrayList<Integer>[][] states){
		//Initializes the two dimensional array of integers that will represent the DFA
	    int [][] state = new int [dfa.size()][numLetters]; 
		//initialize all values to negative 1
		for(int i = 0; i < dfa.size(); i++){
			for(int j = 0; j < numLetters; j++){
				state[i][j] = -1;
			}
		}
		ArrayList<Integer> [] tempState; 
		int emptyDfaStateIndex = 0;
		//Find index of empty dfa state
		for(int index = 0; index < dfa.size(); index++){
			if(dfa.get(index).empty == 1){
				emptyDfaStateIndex = index;
				break;
			}
		}
		//this is used to fill in the DFA two dimensional array
		for(int i = 0; i < numLetters; i++){
			for(int j = 0; j < dfa.size(); j++){
				//tempState holds onto all the possibilities for states
				tempState = (ArrayList<Integer>[]) new ArrayList[dfa.get(j).size];	
				for(int k = 0; k < tempState.length; k++){
					tempState[k] = nextStates(states, dfa.get(j).getStateNumber(k), i, numLetters);
				}
				ArrayList<Integer> possStates = new ArrayList<Integer>(); //arrayList of possible next states
				//adds all the possible next states to possStates
				for(int f = 0; f < tempState.length; f++){
					for(int g = 0; g < tempState[f].size(); g++){
						//checks for duplicates
						if(!possStates.contains(tempState[f].get(g))){
							possStates.add(tempState[f].get(g));
						}
					}
				}
				dfaState poss = new dfaState(possStates);	
				//If there is no transition out of a DFA state, then the transition point to the empty state
				if(poss.empty == 1){
					state[j][i] = emptyDfaStateIndex;
					continue;
				}
				//counter gives the state that state j and symbol i go to 
				int counter = 0;
				for(int m = 0; m < dfa.size(); m++){
					boolean bool;
					//checks if dfa.get(m) and poss are equal
					if(compare(dfa.get(m), poss)){
						break;
					}
					counter++;
				}
				state[j][i] = counter;
			}
		}
		return state;	
	}
	/* Seen method used to check if we have seen the given state before
	 * d is an ArrayList of dfaStates that we use to hold the unique states 
	 * of the dfa that we have seen already.  allTheNfa is an ArrayList that we
	 * use to compare to all the different dfaStates in d to determine
	 * whether we have seen it before or not
	 */
	public static boolean seen(ArrayList<dfaState> d, ArrayList<Integer> allTheNfa){
		boolean ret = false;
		int count;
		//cycles through each dfaState in d
		for(int i = 0; i < d.size(); i++){
			//count tracks the number of exact matches to 
			//determine whether they are exactly the same
			count = 0;
			//checks equality
			if(d.get(i).states.size() == allTheNfa.size()){
				for(int j = 0; j< allTheNfa.size(); j++){
					for(int k = 0; k < allTheNfa.size(); k++){
						if(d.get(i).states.get(j) == allTheNfa.get(k)){
							count++;
						}
					}
				}
			}
			if(count == allTheNfa.size()){
				ret = true;
			}
		}
	return ret;
	}
	
	
	/* This method takes in N which is our constructed NFA, cuurentState which is the 
	 * current state which is an NFA state, symbolIndex which is the index of the corresponding symbol in our
	 * string array alphabet and numLetters which is the number of symbols in the alphabet
	 * This method returns an ArrayList of all the states that you can get to from currentState
	 * and symbolIndex
	 */
	public static ArrayList<Integer> nextStates(ArrayList<Integer>[][] N, int currentState, int symbolIndex, int numLetters){
		//Create an arraylist of ints to represent the states that an NFA can be in
		ArrayList<Integer> nextStates = new ArrayList<Integer>();
		int i = 0;
		//For each next state
		while(i < N[currentState-1][symbolIndex].size()){
			//If there is a next state, add it to the next state list
			if(N[currentState-1][symbolIndex].get(i) != null){
				nextStates.add(N[currentState-1][symbolIndex].get(i));
				i++;
			}
			else{
				continue;
			}
		}
		//Go through the next states and add the states that can be transitioned to with epsilons
		for(int j = 0; j<nextStates.size(); j++){
			if(N[nextStates.get(j)-1][numLetters-1] != null){
				for(int k = 0; k<N[nextStates.get(j)-1][numLetters].size(); k++){
					nextStates.add(N[nextStates.get(j)-1][numLetters].get(k));
				}
			}
		}
		return nextStates;
	}
	/* Takes in two dfaStates a and b 
	 * and returns true if they are the same
	 * and false if they are not
	 */
	public static boolean compare(dfaState a, dfaState b){
		boolean ret = true;
		//checks size
		if(a.size == b.size){
			//checks if all states in ArrayList are the same
			for(int i = 0; i < a.size; i++){
				if(b.states.contains(a.getStateNumber(i))){
					continue;
				}
				else{
					ret = false;
					break;
				}
			}
		}
		else{
			ret = false;
		}
		
		return ret;
	}
	/*Returns a boolean saying whether it is an accept state or not
	 * takes in  a dfaState and an array of all the accept states to 
	 * determine whether an accept state is in the dfaState
	 */
	public static boolean isAcceptState(dfaState a, int [] acceptStates){
		boolean ret = false;
		//cycles through the ArrayList in the dfaState and checks
		//whether it is an accept state
		for(int i = 0; i < a.size; i++){
			for(int j = 0; j < acceptStates.length; j++){
				if(a.states.get(i) == acceptStates[j]){
					ret = true;
				}
			}
		}
		return ret;
	}
	/*
	 * Used purely for testing purposes because the DFA's we created were not all 
	 * identical to yours
	 */
	public static int simulate(int startState, String trans, int [][] states, String [] alphabet){
		int currentState = startState;
		String symbol;
		//keeps the correct index for the transition
		int colIndex=0;
		//runs until there are no more characters in the string
		for(int i = 0; i<trans.length(); i++){
			symbol = trans.substring(i, i + 1);
			//gets one character of the string at s time
			for(int j = 0; j<alphabet.length; j++){
				if(symbol.equals(alphabet[j])){
					//if the symbol is in the alphabet then changes the value of colIndex
					colIndex = j;
					//changes current state to the updated state
					currentState = states[currentState][colIndex];

				}
				else{
					continue;
				}
			}
			if(currentState == -1){
				return 0;
			}
		}
		return currentState;
	}
}
/*
 * dfaState class used to hold onto all the NFA states that composed
 * a single DFA state
 */
class dfaState {
	//ArrayList of all the NFA states that composed one DFA state
	ArrayList<Integer> states = new ArrayList<Integer>();
	//1 if empty set
	int empty;
	int size;
	public dfaState(){}
	public dfaState(ArrayList<Integer> state){
		if(state.size() == 0){
			empty = 1;
		}
		else{
			empty = 0;
		}
		this.states = state;
		this.size = states.size();
	}
	//Accessory method to get the state number
	public int getStateNumber(int index){
		return states.get(index);
	}	
}