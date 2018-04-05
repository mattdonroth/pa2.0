import java.util.HashSet;

public class ConversionTransitionFunction {
    private HashSet<Integer> beginSet;
    private char input;
    private HashSet<Integer> endSet;
    private int beginStateDFA;
    private int endStateDFA;
    //boolean acceptState=false;

    public ConversionTransitionFunction(HashSet<Integer> beginState, char input, HashSet<Integer> endSet) {
        this.beginSet =beginState;
        this.input=input;
        this.endSet = endSet;
    }

    public ConversionTransitionFunction(int beginState, char input, HashSet<Integer> endSet) {
        beginSet =new HashSet<>();
        beginSet.add(beginState);
        this.input=input;
        this.endSet = endSet;
    }


    //getters and setters
    public HashSet<Integer> getBeginSet() {
        return beginSet;
    }

    public char getInput() {
        return input;
    }

    public HashSet<Integer> getEndSet() {
        return endSet;
    }

    public int getBeginStateDFA() {
        return beginStateDFA;
    }

    public void setBeginStateDFA(int beginStateDFA) {
        this.beginStateDFA = beginStateDFA;
    }

    public int getEndStateDFA() {
        return endStateDFA;
    }

    public void setEndStateDFA(int endStateDFA) {
        this.endStateDFA = endStateDFA;
    }
}
