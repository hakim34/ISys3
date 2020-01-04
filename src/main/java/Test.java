import java.io.*;
import java.util.*;

public class Test {
    //because "leave" has no possible next movement, it's excluded from 'moves'"
    public static String[] moves = {"approach", "bill_use", "prey_contact", "open_mouth", "ingest"};
    //analogously "approach" has no previous movement, this helps reducing redundancy in the matrix
    public static String[] nextMoves = {"bill_use", "prey_contact", "open_mouth", "ingest", "leave"};

    public static void main(String[] args) throws IOException {
        String sailfishTrain = "src/main/resources/Sailfish_train.txt";
        String marlinTrain = "src/main/resources/Marlin_train.txt";
        String sailfishTest = "src/main/resources/Sailfish_eval.txt";
        String marlinTest = "src/main/resources/Marlin_eval.txt";

        double[][] sailFishProbability = markovMatrix(nextMoveFrequencies(sailfishTrain));
        double[][] marlinProbability = markovMatrix(nextMoveFrequencies(marlinTrain));
        printMatrix(sailFishProbability);
        printMatrix(marlinProbability);

        Evaluator evaluator = new Evaluator(sailFishProbability, marlinProbability);
        Map<String, Integer> sail = evaluator.evaluate(readText(sailfishTest));
        Map<String, Integer> marlin = evaluator.evaluate(readText(marlinTest));
        System.out.println(sail);
        System.out.println(marlin);
        //marlin recognizer is worse due to every sequence of "approach leave" being considered as sailfish

        double sailfishRecognizerPrecision = (double) sail.get("Sailfish")/(sail.get("Sailfish") + sail.get("Marlin"));
        double marlinRecognizerPrecision = (double) marlin.get("Marlin")/(marlin.get("Sailfish") + marlin.get("Marlin"));
        System.out.println("Sailfish recognizer precision   : " + sailfishRecognizerPrecision);
        System.out.println("Marlin recognizer precision     : " + marlinRecognizerPrecision);
    }

    /**
     * Reads the given text and treats each line of the text as one complete sequence and then puts the sequences
     * in a single list and returns it.
     * @param text the text in question
     * @return the list of sequences
     * @throws IOException in case the file can't be found
     */
    public static List<String[]> readText(String text) throws IOException {
        File file = new File(text);
        BufferedReader br = new BufferedReader(new FileReader(file));

        List<String[]> sequences = new ArrayList<>();
        String[] oneLine;
        String st;
        while ((st = br.readLine()) != null) {
            oneLine = st.split(" ");
            sequences.add(oneLine);
        }
        return sequences;
    }

    /**
     * Create map of frequency of different next movements of each movement
     * @param text the text containing sequences separated by a new line
     * @return the map
     * @throws IOException incase the file doesn't exist
     */
    public static Map<String, Map<String, Integer>> nextMoveFrequencies(String text) throws IOException {
        //LinkedHashMap is used to maintain the insertion order
        Map<String, Map<String, Integer>> nextMoveFrequencies = new LinkedHashMap<>();
        for(String move : moves) {
            //System.out.println(move);
            Map<String, Integer> freq = nextMoveFrequencyOf(readText(text), move);
            //System.out.println(freq);
            nextMoveFrequencies.put(move, freq);
        }
        return nextMoveFrequencies;
    }

    /**
     * Calculates the frequency of each next movements of a given movement from a list of movement [sequences]
     * @param sequences the sequences from which the frequency will be determined
     * @param nextMoveFrequencies the movement in question
     * @return return the map of frequency of each next movement
     */
    public static Map<String, Integer> nextMoveFrequencyOf(List<String[]> sequences, String nextMoveFrequencies) {
        //this won't be in order at first
        Map<String, Integer> freq = new HashMap<>();
        for(String[] sequence : sequences) {
            String curr = null;
            for(String movement : sequence) {
                if(curr != null && curr.equals(nextMoveFrequencies)) {
                    if(!freq.containsKey(movement)) {
                        freq.put(movement, 1);
                    } else {
                        freq.replace(movement, freq.get(movement)+1);
                    }
                }
                curr = movement;
            }
        }
        return freq;
    }

    /**
     * Construct probability matrix of a movement being followed by a particular move
     * @param matrix map of the grouped next move frequencies
     * @return two dimensional array of the probability
     */
    public static double[][] markovMatrix(Map<String, Map<String, Integer>> matrix) {
        double[][] probability = new double[5][5];
        int i = 0; //row number
        for(String key : matrix.keySet()) {
            int total = 0; //combined counts of occurrences for probability calculation
            for(String key2 : matrix.get(key).keySet()) {
                total += matrix.get(key).get(key2);
            }
            int i2 = 0; //column number
            for(String move : nextMoves) {
                if(matrix.get(key).containsKey(move)) {
                    probability[i][i2] = (double) matrix.get(key).get(move)/total;
                } else {
                    probability[i][i2] = 0.0;
                }
                i2++;
            }
            i++;
        }
        return probability;
    }

    /**
     * Prints out specific 2 dimensional array, including the labels of its rows and columns
     * to improve the readability. Some significant digits won't be displayed, though there will
     * be no side effects on the values itself
     * @param matrix the matrix to be printed out
     */
    public static void printMatrix(double[][] matrix) {
        System.out.print(String.format("|%-12s|", ""));
        for(String nextMove : nextMoves) {
            System.out.print(String.format("%-12s|", nextMove));
        }
        System.out.println();
        for(int i = 0; i < matrix.length; i++) {
            System.out.print(String.format("|%-12s|", moves[i]));
            for(int i2 = 0; i2 < matrix[i].length; i2++) {
                System.out.print(String.format("%.9f, ", matrix[i][i2]));
            }
            System.out.println();
        }
    }
}

