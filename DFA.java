import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DFA {
    //would use number of states if we didnt use dynamic array list
    private int numberStates;
    //we can assume input is correct so we don't need to double check with alphabet
    private char[] alphabet;
    private ArrayList<TransitionFunction> transitionFunctions;
    private HashMap<String, Integer> transitionMap;
    private int startState;
    private ArrayList<Integer> acceptStates;
    private ArrayList<String> stringInputs;

    public DFA(int numberStates, char[] alphabet, ArrayList<TransitionFunction> transitionFunctions, int startState, ArrayList<Integer> acceptStates, ArrayList<String> stringInputs) {
        this.numberStates = numberStates;
        this.alphabet = alphabet;
        this.transitionFunctions = transitionFunctions;
        this.startState = startState;
        this.acceptStates = acceptStates;
        this.stringInputs = stringInputs;
        transitionMap=new HashMap<>();
        //map all the transitions
        for (TransitionFunction transitionFunction: transitionFunctions)
        {
            transitionMap.put(transitionFunction.getBeginState()+""+transitionFunction.getInput(),transitionFunction.getEndState());
        }
    }

    //constructor used for converting NFA to DFA. set variables as we go in NFA class
    public DFA()
    {
    }

    /*public void test(PrintWriter outStream) {
        //for each string we want to test
        for (String stringInput : stringInputs)
        {
            int currentState=startState;
            char currentChar;
            boolean accept=false;
            //iterate through the string/dfa
            for (int i=0;i<stringInput.length(); i++ )
            {
                currentChar=stringInput.charAt(i);
                //make the key out of the current state and current char
                String key=currentState+""+currentChar;
                currentState=transitionMap.get(key);
            }
            //if the end state is an accepting state then we accept else reject
            for (Integer acceptState : acceptStates) {
                if (currentState == acceptState) {
                    accept = true;
                }
            }
            if (accept)
            {
                outStream.println("Accept");
            }
            else
            {
                outStream.println("Reject");
            }
        }
        //close stream;
        outStream.close();
    }*/

    //getters and setters
    public int getNumberStates() {
        return numberStates;
    }

    public void setNumberStates(int numberStates) {
        this.numberStates = numberStates;
    }

    public char[] getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(char[] alphabet) {
        this.alphabet = alphabet;
    }

    public ArrayList<TransitionFunction> getTransitionFunctions() {
        return transitionFunctions;
    }

    public void setTransitionFunctions(ArrayList<TransitionFunction> transitionFunctions) {
        this.transitionFunctions = transitionFunctions;
    }

    public HashMap<String, Integer> getTransitionMap() {
        return transitionMap;
    }

    public void setTransitionMap(HashMap<String, Integer> transitionMap) {
        this.transitionMap = transitionMap;
    }

    public int getStartState() {
        return startState;
    }

    public void setStartState(int startState) {
        this.startState = startState;
    }

    public ArrayList<Integer> getAcceptStates() {
        return acceptStates;
    }

    public void setAcceptStates(ArrayList<Integer> acceptStates) {
        this.acceptStates = acceptStates;
    }

    public ArrayList<String> getStringInputs() {
        return stringInputs;
    }

    public void setStringInputs(ArrayList<String> stringInputs) {
        this.stringInputs = stringInputs;
    }

    public void testTransitions() {
        //test transitions, for testing purposes
        for (TransitionFunction transition:transitionFunctions) {
            System.out.print(transition.getBeginState()+" ");
            System.out.print(transition.getInput()+" ");
            System.out.println(transition.getEndState());

        }
    }

    public void printDescription(PrintWriter outStream) {
        //print out the description of DFA in accordance with project specifications
        outStream.println(numberStates);
        for (char c:alphabet) {
            outStream.print(c);
        }
        outStream.print('\n');
        for (TransitionFunction transition:transitionFunctions) {
            outStream.print(transition.getBeginState()+" '");
            outStream.print(transition.getInput()+"' ");
            outStream.println(transition.getEndState());
        }
        outStream.println(startState);
        for (int acceptState:acceptStates) {
            outStream.print(acceptState+" ");
        }
        outStream.print('\n');
        //close output stream
        outStream.close();
    }
}