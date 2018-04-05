public class TransitionFunction {
    private int beginState;
    private char input;
    private int endState;
    private boolean acceptState=false;

    public TransitionFunction(int beginState, char input, int endState) {
        this.beginState = beginState;
        this.input = input;
        this.endState = endState;
    }

    public int getBeginState() {
        return beginState;
    }

    public char getInput() {
        return input;
    }

    public int getEndState() {
        return endState;
    }

    public boolean isAcceptState() {
        return acceptState;
    }

    public void setAcceptState(boolean acceptState) {
        this.acceptState = acceptState;
    }
}

