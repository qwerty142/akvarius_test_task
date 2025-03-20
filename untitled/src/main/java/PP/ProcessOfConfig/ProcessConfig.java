package PP.ProcessOfConfig;

import PP.ResultTypes.Action;
import PP.ResultTypes.Answer;
import PP.ResultTypes.Config;
import PP.ResultTypes.Mode;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessConfig implements IProcessConfig {

    private final String configPath;

    public ProcessConfig(String configPath) {
        this.configPath = configPath;
    }

    @Override
    public Answer<Config> getConfig() {

        Config config = null;

        try (BufferedReader br = new BufferedReader(new FileReader(configPath))) {

            String line;
            Map<String, String> configMap = new HashMap<>();

            while ((line = br.readLine()) != null) {
                line = line.trim();

                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    configMap.put(parts[0].trim(), parts[1].trim());
                }

                if (configMap.size() == 4) {

                    int id = Integer.parseInt(configMap.get("#id"));
                    String mode = configMap.get("#mode");
                    List<String> paths = Arrays.asList(configMap.get("#path").split(","));
                    String action = configMap.get("#action");

                    config = new Config(id, Mode.valueOf(mode), paths, Action.valueOf(action));
                    configMap.clear();
                    break;
                }
            }

        } catch (IOException e) {
            return new Answer<>("FAIL", null);
        }

        if (config == null || config.containsNull()) {
            return new Answer<>("FAIL", null);
        }

        return new Answer<>("SUCCESS", config);
    }
}
