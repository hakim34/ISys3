import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.IntStream;

public class Test {
    public static String[] moves = {"approach", "bill_use", "prey_contact", "open_mouth", "ingest"};
    public static String[] nextMoves = {"bill_use", "prey_contact", "open_mouth", "ingest", "leave"};

    public static void main(String[] args) throws IOException {
        String sailfishTrain = "src/main/resources/Sailfish_train.txt";
        String marlinTrain = "src/main/resources/Marlin_train.txt";
        String sailfishTest = "src/main/resources/Sailfish_eval.txt";
        String marlinTest = "src/main/resources/Marlin_eval.txt";

        double[][] sailFishProbability = markovMatrix(nextMoveFrequencies(sailfishTrain));
        double[][] marlinProbability = markovMatrix(nextMoveFrequencies(marlinTrain));
        System.out.println(Arrays.deepToString(sailFishProbability).replace("],", " \n"));
        System.out.println(Arrays.deepToString(marlinProbability).replace("],", "], \n"));

        Evaluator evaluator = new Evaluator(sailFishProbability, marlinProbability);
        System.out.println(evaluator.evaluate(readText(sailfishTest)));
        System.out.println(evaluator.evaluate(readText(marlinTest)));
        //marlin recognizer is worse due to every sequence of "approach leave" being considered as sailfish
    }

    /**
     * Create map of frequency of next movements of each movement
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
     * Calculates the frequency of each next movements of a given movement from a list of movement sequences
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
}

