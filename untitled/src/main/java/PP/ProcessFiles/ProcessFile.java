package PP.ProcessFiles;

import PP.ResultTypes.Answer;
import PP.ResultTypes.Config;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ProcessFile implements IProcessFile {

    private Map<String, Object> conf_inf;

    private Config config;

    public ProcessFile(Config config) {
        this.config = config;
        conf_inf = new HashMap<>();
        conf_inf.put("configFile", "asd");
        conf_inf.put("configurationId", config.id());

        Map<String, Object> confData = new HashMap<>();
        confData.put("mode", config.mode().toString());
        confData.put("path", config.paths());
        conf_inf.put("configurationData", confData);
    }

    @Override
    public Answer<String> getResult() {
        String result = "";
        try {
            result = switch (config.action()) {
                case STRING -> stringAction(config.paths());
                case COUNT -> countAction(config.paths());
                case REPLACE -> replaceAction(config.paths());
                case DUPLICATE -> duplicateAction(config.paths());
                case AMOUNTOFWORDS -> amountOfWordsAction(config.paths());
            };
        } catch (Exception e) {
            return new Answer<>("FAIl", null);
        }

        return new Answer<>("SUCCESS", result);
    }

    private String stringAction(List<String> paths) throws JsonProcessingException {
        Map<Integer, Map<Integer, String>> output = new LinkedHashMap<>();

        List<List<String>> fileContents = new ArrayList<>();
        for (String path : paths) {
            try {
                List<String> lines = Files.readAllLines(Paths.get(path));
                fileContents.add(lines);

            } catch (IOException ignored) {
                fileContents.add(new ArrayList<>());
            }
        }

        int maxLines = fileContents.stream().mapToInt(List::size).max().orElse(0);

        for (int lineIndex = 0; lineIndex < maxLines; lineIndex++) {
            Map<Integer, String> lineEntry = new LinkedHashMap<>();
            for (int fileIndex = 0; fileIndex < fileContents.size(); fileIndex++) {
                List<String> lines = fileContents.get(fileIndex);
                lineEntry.put(fileIndex + 1, lineIndex < lines.size() ? lines.get(lineIndex) : "");
            }
            output.put(lineIndex + 1, lineEntry);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("out", output);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
    }

    private String countAction(List<String> paths) throws JsonProcessingException {
        Map<Integer, List<Integer>> output = new LinkedHashMap<>();

        List<List<String>> fileContents = new ArrayList<>();
        for (String path : paths) {
            try {
                List<String> lines = Files.readAllLines(Paths.get(path));
                fileContents.add(lines);
            } catch (IOException e) {
                fileContents.add(new ArrayList<>());
            }

        }

        int maxLines = fileContents.stream().mapToInt(List::size).max().orElse(0);

        for (int lineIndex = 0; lineIndex < maxLines; lineIndex++) {
            List<Integer> wordCounts = new ArrayList<>();
            for (List<String> lines : fileContents) {
                if (lineIndex < lines.size()) {
                    wordCounts.add(lines.get(lineIndex).split("\\s+").length);
                } else {
                    wordCounts.add(0);
                }
            }
            output.put(lineIndex + 1, wordCounts);
        }

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("config", conf_inf);
        result.put("out", output);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
    }

    private String replaceAction(List<String> paths) throws IOException {
        Map<Integer, Map<Integer, String>> output = new LinkedHashMap<>();

        List<List<String>> fileContents = new ArrayList<>();
        for (String path : paths) {
            List<String> lines = Files.readAllLines(Paths.get(path));
            fileContents.add(lines);
        }

        int maxLines = fileContents.stream().mapToInt(List::size).max().orElse(0);

        for (int lineIndex = 0; lineIndex < maxLines; lineIndex++) {
            Map<Integer, String> lineEntry = new LinkedHashMap<>();
            for (int fileIndex = 0; fileIndex < fileContents.size(); fileIndex++) {
                List<String> lines = fileContents.get(fileIndex);
                if (lineIndex < lines.size()) {
                    String modifiedLine = lines.get(lineIndex)
                            .chars()
                            .filter(c -> ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')))
                            .mapToObj(c -> String.valueOf(c -'a' + 1))
                            .toString();

                    lineEntry.put(fileIndex + 1, modifiedLine);
                } else {
                    lineEntry.put(fileIndex + 1, "");
                }
            }
            output.put(lineIndex + 1, lineEntry);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("out", output);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
    }

    private String duplicateAction(List<String> paths) throws IOException {

        Map<Integer, Integer> duplicateCounts = new LinkedHashMap<>();

        int fileIndex = 1;
        for (String filePath : paths) {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            Map<String, Integer> wordCount = new HashMap<>();

            for (String line : lines) {

                String[] words = line.split("\\s+");
                for (String word : words) {
                    wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
                }
            }

            int duplicateCount = (int) wordCount.values().stream().filter(count -> count > 1).count();
            duplicateCounts.put(fileIndex++, duplicateCount);
        }

        Map<String, Object> output = new LinkedHashMap<>();

        output.put("config", conf_inf);
        output.put("out", duplicateCounts);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(output);
    }

    private String amountOfWordsAction(List<String> paths) throws IOException {
        Map<Integer, List<Long>> output = new LinkedHashMap<>();

        List<List<String>> fileContents = new ArrayList<>();
        for (String path : paths) {
            List<String> lines = Files.readAllLines(Paths.get(path));
            fileContents.add(lines);
        }

        int maxLines = fileContents.stream().mapToInt(List::size).max().orElse(0);

        for (int lineIndex = 0; lineIndex < maxLines; lineIndex++) {
            List<Long> wordCounts = new ArrayList<>();
            for (List<String> lines : fileContents) {
                if (lineIndex < lines.size()) {
                    String line = lines.get(lineIndex);
                    long count = line.trim().isEmpty() ? 0 : line.trim().split("\s+").length;
                    wordCounts.add(count);
                } else {
                    wordCounts.add(0L);
                }
            }
            output.put(lineIndex + 1, wordCounts);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("out", output);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
    }

}
