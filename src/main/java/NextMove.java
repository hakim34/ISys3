import java.io.*;
import java.util.*;

public class NextMove {
    static public Map<String, Integer> nextMoveFrequency(String text) throws IOException {
        File file = new File(text);
        BufferedReader br = new BufferedReader(new FileReader(file));

        List<String[]> movements = new ArrayList<>();
        String[] move;
        String st;
        while ((st = br.readLine()) != null) {
            move = st.split(" ");
            movements.add(move);
            //System.out.println(Arrays.toString(move));
        }

        Map<String, Integer> freq = new HashMap<>();
        for(String[] movement : movements) {
            if(!freq.containsKey(movement[1])) {
                freq.put(movement[1], 1);
            } else {
                freq.replace(movement[1], freq.get(movement[1])+1);
            }
        }

        System.out.println(freq);
        return freq;
    }
}
