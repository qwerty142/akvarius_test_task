package PP;

import PP.InputParse.InputParser;
import PP.ProcessFiles.ProcessFile;
import PP.ProcessOfConfig.ProcessConfig;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Producer implements IProducer{

    @Override
    public void produce(String s) throws IOException {

        String desktopPath = System.getProperty("user.home") + "/Desktop/config_app";
        Files.createDirectories(Paths.get(desktopPath));


        var parser = new InputParser(s);

        var parse_result = parser.getConfig();

        if (parse_result.answer().equals("FAIL")) {
            System.out.println(parse_result.answer());
            return;
        }

        var processConfig = new ProcessConfig(parse_result.rersult());

        var config_result = processConfig.getConfig();

        if (config_result.answer().equals("FAIL")) {
            System.out.println(config_result.answer());
            return;
        }

        var processFile = new ProcessFile(config_result.rersult());

        var file_result = processFile.getResult();

        if (file_result.answer().equals("FAIL")) {
            System.out.println(file_result.answer());
            return;
        }

        System.out.println(file_result.rersult());

        int number = 1;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(desktopPath), "operation_*.txt")) {
            for (Path entry : stream) {
                String fileName = entry.getFileName().toString();
                String numberPart = fileName.replace("operation_", "").replace(".txt", "");

                int existingNumber = Integer.parseInt(numberPart);
                number = Math.max(number, existingNumber + 1);
            }
        }

        String fileExtension = "txt";
        String txtFilePath = desktopPath + "/operation_" + number + "." + fileExtension;
        Files.write(Paths.get(txtFilePath), file_result.rersult().getBytes());
    }
}
