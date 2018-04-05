import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main (String args[])
    {
        //get input and output file names from command line
        //catch if they were not entered
        String inputFile=null;
        String outputFile=null;
        try {
            inputFile = args[0];
            outputFile= args[1];
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            System.out.println("Enter input & output file names to program.");
            System.exit(0);
        }

        //set up stream with input file
        Scanner inStream=null;
        try
        {
            //removed addition of .txt
            inStream=getInputFile(inputFile);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found.");
            System.exit(0);
        }

        //set up stream with output file
        PrintWriter outStream=null;
        try
        {
            outStream=getOutputFile(outputFile);
        }
        catch (IOException e)
        {
            System.out.println("Output file invalid.");
            System.exit(0);
        }

        //set up the NFA
        NFA nfa=null;
        try
        {
            nfa=setupNFA(inStream);
        }
        catch (InputMismatchException e)
        {
            System.out.println("Input data invalid.");
            System.exit(0);
        }

        //convert NFA to DFA
        DFA dfa=nfa.convertToDFA();
        //print DFA descriptions to output file
        dfa.printDescription(outStream);
    }


    private static NFA setupNFA(Scanner inStream) {
        //get all parameters for a new NFA
        int numberStates=inStream.nextInt();
        char[] alphabet=inStream.next().toCharArray();
        //e character cannot be present, reserved for epsilon transitions
        for (char anAlphabet : alphabet) {
            if (anAlphabet == 'e') {
                System.out.println("Invalid alphabet input");
                System.exit(0);
            }
        }
        //clear line
        inStream.nextLine();

        //regex to get transition functions
        //must be valid inputs, but we can assume correct input
        ArrayList<TransitionFunction> transitionFunctions=new ArrayList<>();
        String nl=inStream.nextLine();
        while (nl.matches("[0-9]+\\s'.'\\s[0-9]+"))
        {
            //regex to get start state
            Pattern p1= Pattern.compile("^[0-9]+");
            Matcher matcher1=p1.matcher(nl);
            matcher1.find();
            int beginState=Integer.parseInt(matcher1.group());
            //int beginState= Integer.parseInt(nl.substring(0,1));
            //regex to get transition char
            Pattern p2=Pattern.compile("'.'");
            Matcher matcher2=p2.matcher(nl);
            matcher2.find();
            char input=matcher2.group().charAt(1);
            //char input=nl.charAt(3);
            //regex to get end state
            Pattern p3=Pattern.compile("'\\s[0-9]+");
            Matcher matcher3=p3.matcher(nl);
            matcher3.find();
            String temp=matcher3.group().substring(2);
            int endState=Integer.parseInt(temp);
            //int endState= Integer.parseInt(nl.substring(6,7));
            //add this to transition function data structure to later get turned into a hash map for testing
            transitionFunctions.add(new TransitionFunction(beginState, input, endState));
            nl=inStream.nextLine();
        }

        nl=inStream.nextLine();
        //blank line needs to be accounted for?
        int startState=Integer.parseInt(nl);

        ArrayList<Integer> acceptStates=new ArrayList<>();
        //split line of integers that make up accept states with space character
        String[] setAcceptStates=inStream.nextLine().split(" ");
        for (String setAcceptState : setAcceptStates) {
            acceptStates.add(Integer.valueOf(setAcceptState));
        }

        //close the stream
        inStream.close();
        //return NFA built from data from file
        return new NFA(numberStates, alphabet, transitionFunctions, startState, acceptStates);
    }

    private static Scanner getInputFile(String inputFile) throws FileNotFoundException{
        return new Scanner(new File(inputFile));

    }

    private static PrintWriter getOutputFile(String outputFile) throws IOException {
        return new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
    }
}
