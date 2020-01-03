import java.io.*;
import java.util.*;

public class Test {
    public static void main(String[] args) throws IOException {
        String sailfishTrain = "src/main/resources/Sailfish_train.txt";
        String marlinTrain = "src/main/resources/Marlin_train.txt";
        String[] moves = {"approach", "bill_use", "prey_contact", "open_mouth", "ingest"};

        for(String move : moves) {
            System.out.println(move);
            System.out.println(nextMoveFrequencyOf(readText(marlinTrain), move));
        }

        Map<String, Map<String, Integer>> nextMoveFrequencies = new HashMap<>();
        for(String move : moves) {
            nextMoveFrequencies.put(move, nextMoveFrequencyOf(readText(marlinTrain), move));
        }
        double[][] probability = markovMatrix(nextMoveFrequencies);
        System.out.println(Arrays.deepToString(probability).replace("],", "\\\n"));
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
        String[] nextMoves = {"bill_use", "prey_contact", "open_mouth", "ingest", "leave"};
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
                    probability[i][i2++] = (double) matrix.get(key).get(move)/total;
                } else {
                    probability[i][i2++] = 0.0;
                }
            }
            i++;
        }
        return probability;
    }
}

