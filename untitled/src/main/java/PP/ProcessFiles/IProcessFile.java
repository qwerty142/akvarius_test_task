package PP.ProcessFiles;

import PP.ResultTypes.Answer;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

public interface IProcessFile {
    public Answer<String> getResult() throws IOException;
}
