import java.util.ArrayList;
import java.util.HashSet;

public class SetTransitionFunction {
    private int beginState;
    private char input;
    private HashSet<Integer> endSet;

    public SetTransitionFunction(int beginState, char input, HashSet<Integer> endSet) {
        this.beginState =beginState;
        this.input=input;
        this.endSet = endSet;
    }
    public int getBeginState() {
        return beginState;
    }

    public char getInput() {
        return input;
    }

    public HashSet<Integer> getEndSet() {
        return endSet;
    }
}
