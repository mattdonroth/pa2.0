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
		String tempVal;
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
		tempVal = scanner.nextLine();
		numLetters = tempVal.length();
		alphabet = new String[numLetters + 1];
		for(int i = 0; i<numLetters; i++){
			alphabet[i] = tempVal.substring(i, i + 1);
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
		for(int i = 0; i < parts.length; i++){
			acceptStates[i] = Integer.parseInt(parts[i]);
		}
		ArrayList<dfaState> finalDfa = nfaConversion(startState, states, numLetters);
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
			if(inAccept(finalDfa.get(g), acceptStates)){
				printer.print((g + 1) + " ");
			}
		}
		printer.close();

	}

	public static ArrayList<dfaState> nfaConversion(int startState, ArrayList<Integer>[][] states, int numLetters){
		ArrayList<dfaState> dfa = new ArrayList<dfaState>();
		ArrayList<Integer> starter = new ArrayList<Integer>();
		starter = nextStates(states, startState, numLetters, numLetters);
		starter.add(startState);
		dfaState start = new dfaState(starter);
		Queue<dfaState> q = new LinkedList<dfaState>();
		q.add(start);
		dfa.add(start);
		dfaState temp; 
		ArrayList<Integer> [] a = null;
		int emptySetCount = 0;
		while(!q.isEmpty()){
			temp = new dfaState(q.remove().states);
			for(int i = 0; i < numLetters; i++){
				ArrayList<Integer> allTheNfa = new ArrayList<Integer>();
				a = (ArrayList<Integer>[]) new ArrayList[temp.size];
				for(int j = 0; j < temp.states.size(); j++){
					a[j] = nextStates(states, temp.states.get(j), i, numLetters);
				}
				for(int x = 0; x < a.length; x++){
					for(int z = 0; z < a[x].size(); z++){
						if(!allTheNfa.contains(a[x].get(z))){
							allTheNfa.add(a[x].get(z));
						}
					}
				}
				
				if(allTheNfa.size() == 0 && emptySetCount == 0){
					dfaState empty = new dfaState(allTheNfa);
					dfa.add(empty);
					emptySetCount++;
					continue;
				}
				if(allTheNfa.size() == 0){
					continue;
				}
				if(seen(dfa, allTheNfa) == false){
					dfaState e = new dfaState(allTheNfa);
					q.add(e);
					dfa.add(e);
				}
			}
		}
		return dfa;
	}
	public static int[][] dfaConversion(ArrayList<dfaState> dfa, int numLetters, ArrayList<Integer>[][] states){
	    int [][] state = new int [dfa.size()][numLetters];
		for(int i = 0; i < dfa.size(); i++){
			for(int j = 0; j < numLetters; j++){
				state[i][j] = -1;
			}
		}
		ArrayList<Integer> [] tempState; 
		int emptyDfaStateIndex = 0;
		for(int index = 0; index < dfa.size(); index++){
			if(dfa.get(index).empty == true){
				emptyDfaStateIndex = index;
				break;
			}
		}
		for(int i = 0; i < numLetters; i++){
			for(int j = 0; j < dfa.size(); j++){
				tempState = (ArrayList<Integer>[]) new ArrayList[dfa.get(j).size];
				for(int k = 0; k < tempState.length; k++){
					tempState[k] = nextStates(states, dfa.get(j).states.get(k), i, numLetters);
				}
				ArrayList<Integer> possStates = new ArrayList<Integer>();
				for(int f = 0; f < tempState.length; f++){
					for(int g = 0; g < tempState[f].size(); g++){
						if(!possStates.contains(tempState[f].get(g))){
							possStates.add(tempState[f].get(g));
						}
					}
				}
				dfaState poss = new dfaState(possStates);	
				if(poss.empty == true){
					state[j][i] = emptyDfaStateIndex;
					continue;
				}
				int counter = 0;
				for(int m = 0; m < dfa.size(); m++){
					boolean bool;
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
	public static boolean seen(ArrayList<dfaState> d, ArrayList<Integer> allTheNfa){
		boolean ret = false;
		int count;
		for(int i = 0; i < d.size(); i++){
			count = 0;
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
	public static ArrayList<Integer> nextStates(ArrayList<Integer>[][] N, int currentState, int symbolIndex, int numLetters){
		ArrayList<Integer> nextStates = new ArrayList<Integer>();
		int i = 0;
		while(i < N[currentState-1][symbolIndex].size()){
			if(N[currentState-1][symbolIndex].get(i) != null){
				nextStates.add(N[currentState-1][symbolIndex].get(i));
				i++;
			}
			else{
				continue;
			}
		}
		for(int j = 0; j<nextStates.size(); j++){
			if(N[nextStates.get(j)-1][numLetters-1] != null){
				for(int k = 0; k<N[nextStates.get(j)-1][numLetters].size(); k++){
					nextStates.add(N[nextStates.get(j)-1][numLetters].get(k));
				}
			}
		}
		return nextStates;
	}
	public static boolean compare(dfaState a, dfaState b){
		boolean ret = true;
		if(a.size == b.size){
			for(int i = 0; i < a.size; i++){
				if(b.states.contains(a.states.get(i))){
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

	public static boolean inAccept(dfaState a, int [] acceptStates){
		boolean ret = false;
		for(int i = 0; i < a.size; i++){
			for(int j = 0; j < acceptStates.length; j++){
				if(a.states.get(i) == acceptStates[j]){
					ret = true;
				}
			}
		}
		return ret;
	}
}

class dfaState {
	ArrayList<Integer> states= new ArrayList<Integer>();
	boolean empty;
	int size;
	public dfaState(){}

	public dfaState(ArrayList<Integer> state){
		if(state.size() == 0){
			empty = true;
		}
		else{
			empty = false;
		}
		this.states = state;
		this.size = states.size();
	}
}
