import java.io.*;
import java.util.*;

public class nfa{
	public static void main(String [] args){	
		String input = args[0];
		String output = args[1];
		File outputFile = new File(output);
		int numStates;
		String [] alph;
		int numSymbols;
		String tempVal;
		ArrayList<Integer>[][] states;
		String trans;
		String cur;
		String [] temp;
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
		numSymbols = tempVal.length();
		alph = new String[numSymbols + 1];
		for(int i = 0; i<numSymbols; i++){
			alph[i] = tempVal.substring(i, i + 1);
		}
		alph[numSymbols] = "e";
		states = (ArrayList<Integer>[][]) new ArrayList[numStates][numSymbols + 1];
		for(int x = 0; x<numStates; x++){
			for(int y = 0; y < numSymbols + 1; y++){
				states[x][y] = new ArrayList<>();
			}
		}
		trans = scanner.nextLine();
		while(scanner.hasNextLine()){
			if(trans.length()<2){
				break;
			}
			temp = trans.split(" ");
			qCur = Integer.parseInt(temp[0]);
			cur = temp[1].substring(1, 2);
			qNext = Integer.parseInt(temp[2]);
			trans = scanner.nextLine();
			boolean found = true;
			int index = 0;
			while (found){
				if(alph[index].compareTo(cur) == 0){
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
		temp = trans.split(" ");
		int numAccept = temp.length;
		int [] qAccepts = new int[numAccept];
		for(int i = 0; i < temp.length; i++){
			qAccepts[i] = Integer.parseInt(temp[i]);
		}
        ArrayList<dfa> finalDfa = new ArrayList<>();
        ArrayList<Integer> start = availStates(states, startState, numSymbols, numSymbols);
        start.add(startState);
        dfa stat = new dfa(start);
        Queue<dfa> q = new LinkedList<>();
        q.add(stat);
        finalDfa.add(stat);
        dfa timp;
        int emptySetCount = 0;
        while(!q.isEmpty()){
            timp = new dfa(q.remove().states);
            for(int i = 0; i < numSymbols; i++){
                ArrayList<Integer> allTheNfa = new ArrayList<>();
                ArrayList<Integer>[] a = (ArrayList<Integer>[]) new ArrayList[timp.size];
                for(int j = 0; j < timp.states.size(); j++){
                    a[j] = availStates(states, timp.states.get(j), i, numSymbols);

                }
                for (ArrayList<Integer> res : a) {
                    for (Integer cru : res) {
                        if (!allTheNfa.contains(cru)) {
                            allTheNfa.add(cru);
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
                if(!seen(finalDfa, allTheNfa)){
                    dfa e = new dfa(allTheNfa);
                    q.add(e);
                    finalDfa.add(e);
                }
            }
        }
        int [][] dfaState = new int [finalDfa.size()][numSymbols];
        for(int i = 0; i < finalDfa.size(); i++){
            for(int j = 0; j < numSymbols; j++){
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
        for(int i = 0; i < numSymbols; i++){
            for(int j = 0; j < finalDfa.size(); j++){
                tempState = (ArrayList<Integer>[]) new ArrayList[finalDfa.get(j).size];
                for(int k = 0; k < tempState.length; k++){
                    tempState[k] = availStates(states, finalDfa.get(j).states.get(k), i, numSymbols);

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
                    if (comp(finStat, poss)) {
                        break;
                    }
                    count++;
                }
                dfaState[j][i] = count;
            }
        }
		PrintWriter print = null;
		try{
			print = new PrintWriter(outputFile);
		}
		catch(IOException e){
			System.out.println(e.getMessage());
		}
        assert print != null;
        print.println(dfaState.length);
		for(int i = 0; i < alph.length - 1; i++){
			print.print(alph[i]);
		}
		print.println();
		for(int j = 0; j < finalDfa.size(); j++){
			for(int k = 0; k < numSymbols; k++){
				if(dfaState[j][k] != -1){
					print.println((j + 1) + " "  + "'" + alph[k] + "'" + " " + (dfaState[j][k] + 1));
				}
			}
		}
		print.println(1);
		for(int g = 0; g<finalDfa.size(); g++){
			if(inAccept(finalDfa.get(g), qAccepts)){
				print.print((g + 1) + " ");
			}
		}
		print.close();

	}
	private static boolean seen(ArrayList<dfa> d, ArrayList<Integer> allTheNfa){
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
	private static ArrayList<Integer> availStates(ArrayList<Integer>[][] N, int currentState, int symbolIndex, int numLetters){
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
	private static boolean comp(dfa a, dfa b){
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