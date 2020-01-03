import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

public class Test {
    public static String[] moves = {"approach", "bill_use", "prey_contact", "open_mouth", "ingest"};
    public static String[] nextMoves = {"bill_use", "prey_contact", "open_mouth", "ingest", "leave"};

    public static void main(String[] args) throws IOException {
        String sailfishTrain = "src/main/resources/Sailfish_train.txt";
        String marlinTrain = "src/main/resources/Marlin_train.txt";
        //LinkedHashMap is used to maintain the insertion order
        Map<String, Map<String, Integer>> nextMoveFrequencies = new LinkedHashMap<>();

        for(String move : moves) {
            System.out.println(move);
            Map<String, Integer> freq = nextMoveFrequencyOf(readText(marlinTrain), move);
            System.out.println(freq);
            nextMoveFrequencies.put(move, freq);
        }

        double[][] probability = markovMatrix(nextMoveFrequencies);
        System.out.println(Arrays.deepToString(probability).replace("],", " \n"));

        String[] sequenceTest = {"approach", "bill_use", "prey_contact", "open_mouth", "leave"};
        System.out.println(sequenceProbability(sequenceTest, probability));
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

    /**
     * Calculates the probability of a particular movement sequence using a given probability matrix
     * @param sequence the sequence in question
     * @param probabilityMatrix the matrix according to the first degree of markov chain
     * @return the probability
     */
    public static double sequenceProbability(String[] sequence, double[][] probabilityMatrix) {
        double probability = 1.0;
        String prev = null;
        for(String movement : sequence) {
            if(prev != null) {
                String finalPrev = prev;
                int index = IntStream.range(0, moves.length).filter(i -> moves[i].equals(finalPrev)).findFirst().orElse(-1);
                int index2 = IntStream.range(0, nextMoves.length).filter(i -> nextMoves[i].equals(movement)).findFirst().orElse(-1);
                probability *= probabilityMatrix[index][index2];
            }
            prev = movement;
        }
        return probability;
    }
}

