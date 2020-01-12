import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * This class provides multiple methods to evaluate the sailfish and marlin data
 */
public class Evaluator {
    /** Array of possible movements excluding the final move "leave" */
    private static String[] moves = {"approach", "bill_use", "prey_contact", "open_mouth", "ingest"};
    /** Array of possible follow up movements excluding first move "approach" */
    private static String[] nextMoves = {"bill_use", "prey_contact", "open_mouth", "ingest", "leave"};

    private double[][] sailFishMatrix;
    private double[][] marlinMatrix;

    /**
     *  Constructor of the Evaluator class needs both matrix for use in the methods
     * @param sailFishMatrix    The matrix for the sailfish
     * @param marlinMatrix      The matrix for the marlin
     */
    public Evaluator(double[][] sailFishMatrix, double[][] marlinMatrix){
        this.sailFishMatrix = sailFishMatrix;
        this.marlinMatrix = marlinMatrix;
    }

    /**
     * Maps the number of sequences to each type of fish, that got recognized
     * @param sequences sequences that are to be analyzed
     * @return The map of the categorization
     */
    public Map<String, Integer> evaluate(List<String[]> sequences) {
        Map<String, Integer> map = new HashMap<>();
        map.put("Sailfish", 0);
        map.put("Marlin", 0);
        for(String[] sequence : sequences) {
            if (sequenceProbability(sequence, sailFishMatrix) > sequenceProbability(sequence, marlinMatrix)) {
                map.replace("Sailfish", map.get("Sailfish") + 1);
            } else {
                map.replace("Marlin", map.get("Marlin") + 1);
            }
        }
        return map;
    }

    /**
     * Calculates the probability of a particular movement sequence using a given probability matrix
     * @param sequence          The sequence in question
     * @param probabilityMatrix The matrix according to the first degree of markov chain
     * @return The probability
     */
    public static double sequenceProbability(String[] sequence, double[][] probabilityMatrix) {
        double probability = 1.;
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
