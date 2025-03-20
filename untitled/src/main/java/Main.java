import PP.Producer;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String s;

        while (!(s = scanner.nextLine()).equals("exit")) {
            var producer = new Producer();
            producer.produce(s);
        }

    }
}
