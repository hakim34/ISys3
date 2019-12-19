import java.io.*;

public class Test {
    public static void main(String[] args) throws IOException {
        NextMove.nextMoveFrequency("src/main/resources/Marlin_train.txt");
        NextMove.nextMoveFrequency("src/main/resources/Sailfish_train.txt");

        NextMove.nextMoveOf("src/main/resources/Marlin_train.txt", "bill_use");
        NextMove.nextMoveOf("src/main/resources/Sailfish_train.txt", "bill_use");
    }
}

