import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;

public class NFA {
    private int numberStates;
    //we can assume input is correct so we don't need to double check with alphabet
    private char[] alphabet;
    private ArrayList<TransitionFunction> transitionFunctions;
    private HashMap<String, HashSet<Integer>> transitionMap;
    private int startState;
    private ArrayList<Integer> acceptStates;

    public NFA(int numberStates, char[] alphabet, ArrayList<TransitionFunction> transitionFunctions, int startState, ArrayList<Integer> acceptStates) {
        this.numberStates = numberStates;
        this.alphabet = alphabet;
        this.transitionFunctions = transitionFunctions;
        this.startState = startState;
        this.acceptStates = acceptStates;
    }

    public DFA convertToDFA() {
        //converts NFA to DFA
        //create DFA
        DFA dfa=new DFA();
        //set alphabet, will remain the same
        dfa.setAlphabet(alphabet);

        //transitions with same begin state and input made into one transition function with a set for end state
        ArrayList<SetTransitionFunction> setTransitionFunctions=new ArrayList<>();
        //going through this original transition function array list and populating new set one
        while (!transitionFunctions.isEmpty())
        {
            //System.out.println("while transitionFunctions is not empty");
            ArrayList<TransitionFunction> remove=new ArrayList<>();
            HashSet<Integer> endSet=new HashSet<>();
            //add end state of reference transition no matter what
            endSet.add(transitionFunctions.get(0).getEndState());
            //remove reference transition no matter what
            remove.add(transitionFunctions.get(0));
            int referenceBeginState=transitionFunctions.get(0).getBeginState();
            char referenceInput=transitionFunctions.get(0).getInput();
            //for loop won't go through if size is 1
            for (int i=1;i<transitionFunctions.size();i++)
            {
                if (referenceBeginState==transitionFunctions.get(i).getBeginState() && referenceInput==transitionFunctions.get(i).getInput())
                {
                    endSet.add(transitionFunctions.get(i).getEndState());
                    remove.add(transitionFunctions.get(i));
                }
            }
            //add new transition
            SetTransitionFunction setTransitionFunction =new SetTransitionFunction(referenceBeginState, referenceInput, endSet);
            setTransitionFunctions.add(setTransitionFunction);

            //remove independent ones that we converged from old transition function array list
            transitionFunctions.removeAll(remove);
        }

        //map all the transitions
        transitionMap=new HashMap<>();
        for (SetTransitionFunction transition: setTransitionFunctions)
        {
            transitionMap.put(transition.getBeginState()+""+transition.getInput(),transition.getEndSet());
        }

        LinkedBlockingQueue<HashSet<Integer>> q=new LinkedBlockingQueue<>();

        //array list of transitions with sets for start and end states
        ArrayList<ConversionTransitionFunction> conversionTransitionFunctions=new ArrayList<>();

        //create loop from new reject state to itself, in case the NFA doesn't have a transition for each symbol out of every state
        for (char c:alphabet) {
            HashSet<Integer> temp=new HashSet<>();
            temp.add(-1);
            conversionTransitionFunctions.add(new ConversionTransitionFunction(temp, c, temp));
        }

        //store set of states NFA can be in in start state of DFA
        HashSet<Integer> beginSet=new HashSet<>();
        beginSet.add(startState);
        //add all states that can be reached by epsilon transition, e-closure
        beginSet=getEpsilonTransitions(beginSet);

        /*HashSet<Integer> epsilonTransitions=transitionMap.get(startState+""+'e');
        if (epsilonTransitions!=null) {
            if (beginSet.addAll(epsilonTransitions)) {
                //beginSet.addAll(epsilonTransitions);
                boolean eClosure = false;
                while (!eClosure) {
                    //System.out.println("while not e-closure");
                    HashSet<Integer> newSet = new HashSet<>();
                    for (int state : epsilonTransitions) {
                        HashSet<Integer> temp = transitionMap.get(state+""+'e');
                        if (temp != null) {
                            //newSet.addAll(temp);
                            if (beginSet.addAll(temp)) {
                                newSet.addAll(temp);
                                eClosure = false;
                            }
                            else
                                eClosure = true;
                        }
                        else
                        {
                            eClosure=true;
                        }
                    }
                    if (!eClosure) {
                        epsilonTransitions = newSet;
                    }
                }
            }
        }*/

        //HashSet<Integer> startSet=beginSet;

        //get transitions from this beginSet, add them to conversionTransitionFunctions, put their end sets in q to get transitions for those end sets
        getTransitions(q, conversionTransitionFunctions, beginSet);

        //before transitions out of every created set of states are made
        while (!q.isEmpty()) {
            //System.out.println("while q is not empty");
            HashSet<Integer> newBeginSet=q.poll();
            //check if the set we dequeue is equal to beginSet of a transition we have already completed
            boolean setExists=false;
            for (ConversionTransitionFunction currentTransition:conversionTransitionFunctions) {
                if (currentTransition.getBeginSet().equals(newBeginSet))
                {
                    setExists=true;
                }
            }

            if (!setExists)
            {
                getTransitions(q,conversionTransitionFunctions,newBeginSet);
            }
        }

        //creation of transitions done
        //these transitions' states are sets that the NFA can be in, convert label to arbitrary number to represent set
        ArrayList<TransitionFunction> transitionFunctionsDFA=getDFATransitions(conversionTransitionFunctions, dfa, beginSet);
        dfa.setTransitionFunctions(transitionFunctionsDFA);
        return dfa;
    }

    private HashSet<Integer> getEpsilonTransitions(HashSet<Integer> beginSet) {
        //gets the epsilon transitions for given set
        HashSet<Integer> epsilonEndSet=new HashSet<Integer>();
        for (int state:beginSet) {
            HashSet<Integer> temp=transitionMap.get(state+"e");
            if (temp!=null)
            {
                epsilonEndSet.addAll(temp);
            }
        }
        if (beginSet.addAll(epsilonEndSet))
        {
            beginSet=getEpsilonTransitions(beginSet);
        }
        return beginSet;
    }

    private ArrayList<TransitionFunction> getDFATransitions(ArrayList<ConversionTransitionFunction> conversionTransitionFunctions, DFA dfa, HashSet<Integer> startSet) {
        //create array list TransitionFunctions for DFA from array list of ConversionTransitionFunctions
        ArrayList<ConversionTransitionFunction> updatedTransitions=new ArrayList<>();

        int numberStatesDFA=0;
        int stateNumberDFA=1;
        HashSet<Integer> acceptStatesDFA=new HashSet<>();
        int startStateDFA=-1;
        //set DFA state for all begin sets
        while (!conversionTransitionFunctions.isEmpty())
        {
            //System.out.println("while conversionTransitionFunctions is not empty");
            ArrayList<ConversionTransitionFunction> remove=new ArrayList<>();
            HashSet<Integer> referenceBeginSet= conversionTransitionFunctions.get(0).getBeginSet();
            conversionTransitionFunctions.get(0).setBeginStateDFA(stateNumberDFA);
            //remove reference transition no matter what
            remove.add(conversionTransitionFunctions.get(0));
            //check if conversionTransitionFunction is a start state or contains an accept state
            if (conversionTransitionFunctions.get(0).getBeginSet().equals(startSet))
            {
                startStateDFA=stateNumberDFA;
            }
            for (int state:conversionTransitionFunctions.get(0).getBeginSet()) {
                for (int acceptState: acceptStates) {
                    if (state==acceptState)
                        acceptStatesDFA.add(stateNumberDFA);
                }
            }
            //for loop won't go through if size is 1
            for (int i = 1; i< conversionTransitionFunctions.size(); i++)
            {
                if (referenceBeginSet.equals(conversionTransitionFunctions.get(i).getBeginSet()))
                {
                    conversionTransitionFunctions.get(i).setBeginStateDFA(stateNumberDFA);
                    remove.add(conversionTransitionFunctions.get(i));
                }
            }

            updatedTransitions.addAll(remove);
            conversionTransitionFunctions.removeAll(remove);

            numberStatesDFA++;
            stateNumberDFA++;
        }
        dfa.setNumberStates(numberStatesDFA);
        ArrayList<Integer> listAcceptStates=new ArrayList<>(acceptStatesDFA);
        dfa.setAcceptStates(listAcceptStates);
        dfa.setStartState(startStateDFA);

        //set DFA state for all the end sets
        for (ConversionTransitionFunction transition:updatedTransitions)
        {
            HashSet<Integer>referenceSet=transition.getBeginSet();
            int referenceDFAState=transition.getBeginStateDFA();
            for (int i=0;i<updatedTransitions.size();i++)
            {
                if (updatedTransitions.get(i).getEndSet().equals(referenceSet))
                    updatedTransitions.get(i).setEndStateDFA(referenceDFAState);
            }
        }

        ArrayList<TransitionFunction> transitionsDFA=new ArrayList<>();
        for (ConversionTransitionFunction transition:updatedTransitions) {
            //create TransitionFunction array list for DFA
            TransitionFunction temp=new TransitionFunction(transition.getBeginStateDFA(), transition.getInput(), transition.getEndStateDFA());
            transitionsDFA.add(temp);
        }

        return transitionsDFA;
    }

    private void getTransitions(LinkedBlockingQueue<HashSet<Integer>> q, ArrayList<ConversionTransitionFunction> conversionTransitionFunctions, HashSet<Integer> beginSet) {
        //gets transitions from given set and enqueues endset if needed to get transitions for rest of machine
        //every DFA state needs a transition with every symbol from the alphabet
        for (char c:alphabet)
        {
            HashSet<Integer> endSet=new HashSet<>();
            //we compile the end states from each start state in the start set into an end set
            for (int state:beginSet)
            {
                HashSet<Integer> temp=transitionMap.get(state+""+c);
                if (temp!=null) {
                    endSet.addAll(temp);
                }
            }
            //if the set is empty then this is a new reject state, because the NFA didn't specify an option for the symbol
            //if end set is not empty, check for epsilon transitions to add those states as well
            if (!endSet.isEmpty()) {
                //get set of all possible states epsilon transitions can reach
                endSet=getEpsilonTransitions(endSet);


                /*HashSet<Integer> epsilonTransitionStartSet=new HashSet<>(beginSet);
                //beginSet.addAll(epsilonTransitions);
                boolean eClosure=false;
                while (!eClosure)
                {
                    //System.out.println("while not eclosure 2");
                    HashSet<Integer> newSet=new HashSet<>();
                    for (int state:epsilonTransitionStartSet) {
                        HashSet<Integer> temp=transitionMap.get(state+""+'e');
                        if (temp!=null) {
                            //newSet.addAll(temp);
                            //add these states to endSet
                            if (endSet.addAll(temp)) {
                                newSet.addAll(temp);
                                eClosure = false;
                            }
                            else
                                eClosure = true;
                        }
                        else
                        {
                            eClosure=true;
                        }
                    }
                    if (!eClosure) {
                        epsilonTransitionStartSet = newSet;
                    }
                }*/
            }
            else
            //there are no transitions for this symbol, make one to reject state
            {
                endSet.add(-1);
            }
            ConversionTransitionFunction transition=new ConversionTransitionFunction(beginSet,c,endSet);

            //check to see if a transition already exists which has the start set of the transition we just created's end set
            //if so, then we don't need to enqueue the end set, because transitions have already (or will be) been created for it
            boolean setExists=false;
            for (ConversionTransitionFunction current:conversionTransitionFunctions) {
                if (transition.getEndSet().equals(current.getBeginSet()))
                {
                    setExists=true;
                    break;
                }
            }
            if (!setExists)
            //enqueue the end set, to do a breadth-first creation of DFA transitions
            {
                try {
                    q.put(endSet);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //add transition to conversionTransitionFunctions
            conversionTransitionFunctions.add(transition);
        }
    }
}