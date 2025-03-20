package PP.InputParse;

import PP.ResultTypes.Answer;

import java.util.Arrays;

public class InputParser implements IInputParser {

    private final String[] input;

    public InputParser(String input) {
        this.input = input.split(" ");
    }

    @Override
    public Answer<String> getConfig() {
        if (input.length != 2) {
            return new Answer<>("FAIL", "");
        }
        return new Answer<>("SUCCESS", input[0]);
    }

    @Override
    public Answer<Integer> getNumberConfig() {
        if (input.length != 2) {
            return new Answer<>("FAIL", -1);
        }

        return new Answer<>("SUCCESS", Integer.valueOf(input[1]));
    }
}
