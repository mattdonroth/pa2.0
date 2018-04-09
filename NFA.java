import java.io.*;
import java.util.*;

public class nfa{
	public static void main(String [] args){	
		String input = args[0];
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
		int qCur;
		int startState;
		int qNext;
		Scanner scanner = null;
		File inputFile = new File(input);
		try{
			scanner = new Scanner(inputFile);
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
				states[i][j] = new ArrayList<>();
			}
		}
		trans = scanner.nextLine();
		while(scanner.hasNextLine()){
			if(trans.length()<2){
				break;
			}
			parts = trans.split(" ");
			qCur = Integer.parseInt(parts[0]);
			holder = parts[1].substring(1, 2);
			qNext = Integer.parseInt(parts[2]);
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
			states[qCur - 1][index].add(qNext);
		}
		startState = scanner.nextInt();
		scanner.nextLine();
		trans = scanner.nextLine();
		parts = trans.split(" ");
		int numAcceptStates = parts.length;
		int [] acceptStates = new int[numAcceptStates];
		for(int i = 0; i < parts.length; i++){
			acceptStates[i] = Integer.parseInt(parts[i]);
		}

        ArrayList<dfa> finalDfa = new ArrayList<>();
        ArrayList<Integer> start = nextStates(states, startState, numLetters, numLetters);
        start.add(startState);
        dfa stat = new dfa(start);
        Queue<dfa> q = new LinkedList<>();
        q.add(stat);
        finalDfa.add(stat);
        dfa temp;
        int emptySetCount = 0;
        while(!q.isEmpty()){
            temp = new dfa(q.remove().states);
            for(int i = 0; i < numLetters; i++){
                ArrayList<Integer> allTheNfa = new ArrayList<>();
                ArrayList<Integer>[] a = (ArrayList<Integer>[]) new ArrayList[temp.size];
                for(int j = 0; j < temp.states.size(); j++){
                    a[j] = nextStates(states, temp.states.get(j), i, numLetters);

                }
                for (ArrayList<Integer> res : a) {
                    for (Integer cur : res) {
                        if (!allTheNfa.contains(cur)) {
                            allTheNfa.add(cur);
                        }
                    }
                }

                if(allTheNfa.size() == 0 && emptySetCount == 0){
                    dfa empty = new dfa(allTheNfa);
                    finalDfa.add(empty);
                    emptySetCount++;
                }
                if(allTheNfa.size() == 0){
                    continue;
                }
                if(!viewed(finalDfa, allTheNfa)){
                    dfa e = new dfa(allTheNfa);
                    q.add(e);
                    finalDfa.add(e);
                }
            }
        }

        int [][] dfaState = new int [finalDfa.size()][numLetters];
        for(int i = 0; i < finalDfa.size(); i++){
            for(int j = 0; j < numLetters; j++){
                dfaState[i][j] = -1;
            }
        }
        ArrayList<Integer> [] tempState;
        int emptyDfaStateIndex = 0;
        for(int index = 0; index < finalDfa.size(); index++){
            if(finalDfa.get(index).empty){
                emptyDfaStateIndex = index;
                break;
            }
        }
        for(int i = 0; i < numLetters; i++){
            for(int j = 0; j < finalDfa.size(); j++){
                tempState = (ArrayList<Integer>[]) new ArrayList[finalDfa.get(j).size];
                for(int k = 0; k < tempState.length; k++){
                    tempState[k] = nextStates(states, finalDfa.get(j).states.get(k), i, numLetters);

                }
                ArrayList<Integer> possStates = new ArrayList<>();
                for (ArrayList<Integer> tomp : tempState) {
                    for (Integer tempTomp : tomp) {
                        if (!possStates.contains(tempTomp)) {
                            possStates.add(tempTomp);
                        }
                    }
                }
                dfa poss = new dfa(possStates);
                if(poss.empty){
                    dfaState[j][i] = emptyDfaStateIndex;
                    continue;
                }
                int count = 0;
                for (dfa finStat : finalDfa) {
                    if (compare(finStat, poss)) {
                        break;
                    }
                    count++;
                }
                dfaState[j][i] = count;
            }
        }
		PrintWriter printer = null;
		try{
			printer = new PrintWriter(outputFile);
		}
		catch(IOException e){
			System.out.println(e.getMessage());
		}
        assert printer != null;
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

	private static boolean viewed(ArrayList<dfa> d, ArrayList<Integer> allTheNfa){
		boolean ret = false;
		int count;
        for (dfa state : d) {
            count = 0;
            if (state.states.size() == allTheNfa.size()) {
                for (int j = 0; j < allTheNfa.size(); j++) {
                    for (Integer anAllTheNfa : allTheNfa) {
                        if (state.states.get(j).equals(anAllTheNfa)){
                            count++;
                        }
                    }
                }
            }
            if (count == allTheNfa.size()) {
                ret = true;
            }
        }
	return ret;
	}

	private static ArrayList<Integer> nextStates(ArrayList<Integer>[][] N, int currentState, int symbolIndex, int numLetters){
		ArrayList<Integer> nextStates = new ArrayList<>();
		int i = 0;
		while(i < N[currentState-1][symbolIndex].size()){
			if(N[currentState-1][symbolIndex].get(i) != null){
				nextStates.add(N[currentState-1][symbolIndex].get(i));
				i++;
			}
		}
		for(int j = 0; j<nextStates.size(); j++){
			if(N[nextStates.get(j)-1][numLetters-1] != null){
                nextStates.addAll(N[nextStates.get(j) - 1][numLetters]);
			}
		}
		return nextStates;
	}

	private static boolean compare(dfa a, dfa b){
		boolean ret = true;
		if(a.size == b.size){
			for(int i = 0; i < a.size; i++){
				if(!b.states.contains(a.states.get(i))){
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

	private static boolean inAccept(dfa a, int [] acceptStates){
		boolean ret = false;
		for(int i = 0; i < a.size; i++){
            for (int acceptState : acceptStates) {
                if (a.states.get(i) == acceptState) {
                    ret = true;
                }
            }
		}
		return ret;
	}
}

class dfa {
	ArrayList<Integer> states= new ArrayList<>();
	boolean empty;
	int size;

	public dfa(ArrayList<Integer> state){
        empty = state.size() == 0;
		this.states = state;
		this.size = states.size();
	}
}
