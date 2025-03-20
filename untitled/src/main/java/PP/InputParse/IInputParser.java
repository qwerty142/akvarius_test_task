package PP.InputParse;

import PP.ResultTypes.Answer;

public interface IInputParser {
    public Answer<String> getConfig();
    public Answer<Integer> getNumberConfig();
}
