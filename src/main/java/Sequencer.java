import java.io.*;
import java.util.*;

/**
 * This class uses the training data of the behaviour sequences of sailfishes and marlins to evaluate
 * if the given sequence belongs to the one or the other.
 * Afterwards the recognition precision is calculated with the evaluation data.
 */
public class Sequencer {
    /** Because "leave" has no possible next movement, it's excluded from 'moves'" */
    private static String[] moves = {"approach", "bill_use", "prey_contact", "open_mouth", "ingest"};

    /** "approach" is excluded, because it has no possible previous movement */
    private static String[] nextMoves = {"bill_use", "prey_contact", "open_mouth", "ingest", "leave"};

    /**
     * This program reads all training and evaluation data, evaluates the predicted results
     * and prints the precision to the console
     * @throws IOException When the expected files containing the training and evaluation data are not present
     */
    public static void main(String[] args) throws IOException {
        // Declare resource file paths
        String sailfishTrain = "src/main/resources/Sailfish_train.txt";
        String marlinTrain = "src/main/resources/Marlin_train.txt";
        String sailfishTest = "src/main/resources/Sailfish_eval.txt";
        String marlinTest = "src/main/resources/Marlin_eval.txt";

        double[][] sailFishProbability = markovMatrix(nextMoveFrequencies(sailfishTrain));
        double[][] marlinProbability = markovMatrix(nextMoveFrequencies(marlinTrain));

        Evaluator evaluator = new Evaluator(sailFishProbability, marlinProbability);
        Map<String, Integer> sail = evaluator.evaluate(readText(sailfishTest));
        Map<String, Integer> marlin = evaluator.evaluate(readText(marlinTest));
        System.out.println(sail);
        System.out.println(marlin);
        // Marlin recognizer is worse due to every sequence of "approach leave" being considered as sailfish

        double sailfishRecognizerPrecision = (double) sail.get("Sailfish")/(sail.get("Sailfish") + sail.get("Marlin"));
        double marlinRecognizerPrecision = (double) marlin.get("Marlin")/(marlin.get("Sailfish") + marlin.get("Marlin"));
        System.out.println("Sailfish recognizer precision   : " + sailfishRecognizerPrecision);
        System.out.println("Marlin recognizer precision     : " + marlinRecognizerPrecision);
    }

    /**
     * Reads the given text and treats each line of the text as one complete sequence and then puts the sequences
     * in a single list and returns it.
     * @param filepath      The path to the file containing the sequences
     * @return              The list of sequences
     * @throws IOException  When the file can't be found
     */
    public static List<String[]> readText(String filepath) throws IOException {
        File file = new File(filepath);
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
     * @param filepath      The path to the file containing sequences separated by a new line
     * @return              The map
     * @throws IOException  When the file doesn't exist
     */
    public static Map<String, Map<String, Integer>> nextMoveFrequencies(String filepath) throws IOException {
        //LinkedHashMap is used to maintain the insertion order
        Map<String, Map<String, Integer>> nextMoveFrequencies = new LinkedHashMap<>();
        for(String move : moves) {
            //System.out.println(move);
            Map<String, Integer> freq = nextMoveFrequencyOf(readText(filepath), move);
            //System.out.println(freq);
            nextMoveFrequencies.put(move, freq);
        }
        return nextMoveFrequencies;
    }

    /**
     * Calculates the frequency of each next movements of a given movement from a list of movement [sequences]
     * @param sequences             The sequences from which the frequency will be determined
     * @param nextMoveFrequencies   The movement in question
     * @return                      The map of frequency of each next movement
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
     * @param matrix Map of the grouped next move frequencies
     * @return       Two dimensional array of the probability
     */
    public static double[][] markovMatrix(Map<String, Map<String, Integer>> matrix) {
        double[][] probability = new double[5][5];
        int i = 0; //row number
        for(String key : matrix.keySet()) {
            int total = 0; //combined counts of occurrences for probability calculation
            for(String key2 : matrix.get(key).keySet()) {
                total += matrix.get(key).get(key2);
            }
            int j = 0; //column number
            for(String move : nextMoves) {
                if(matrix.get(key).containsKey(move)) {
                    probability[i][j] = (double) matrix.get(key).get(move)/total;
                } else {
                    probability[i][j] = 0.0;
                }
                j++;
            }
            i++;
        }
        return probability;
    }

    /**
     * Prints out specific 2 dimensional array, including the labels of its rows and columns
     * to improve the readability. Some significant digits won't be displayed, though there will
     * be no side effects on the values itself
     * @param matrix The matrix to be printed out
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

