import java.io.*;
import java.util.*;

public class Test {
    public static void main(String[] args) throws IOException {
        String sailfishTrain = "src/main/resources/Sailfish_train.txt";
        String marlinTrain = "src/main/resources/Marlin_train.txt";
        String[] moves = {"approach", "bill_use", "prey_contact", "open_mouth", "ingest"};

        for(String move : moves) {
            System.out.println(move);
            System.out.println(nextMovesOf(readText(marlinTrain), move));
        }
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
     * @param move the movement in question
     * @return return the map of frequency of each next movement
     */
    public static Map<String, Integer> nextMovesOf(List<String[]> sequences, String move) {
        Map<String, Integer> freq = new HashMap<>();
        for(String[] sequence : sequences) {
            String curr = null;
            for(String movement : sequence) {
                if(curr != null && curr.equals(move)) {
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
}

