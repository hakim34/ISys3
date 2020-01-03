import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Evaluator {
    public static String[] moves = {"approach", "bill_use", "prey_contact", "open_mouth", "ingest"};
    public static String[] nextMoves = {"bill_use", "prey_contact", "open_mouth", "ingest", "leave"};

    public double[][] sailFishMatrix;
    public double[][] marlinMatrix;

    public Evaluator(double[][] matrix, double[][] matrix2){
        this.sailFishMatrix = matrix;
        this.marlinMatrix = matrix2;
    }

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
