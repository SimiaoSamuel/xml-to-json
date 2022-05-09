package br.edu.cefsa;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        try {
            Path path = Path.of("output.json");
            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            Path filePath = Path.of("teste.xml");
            List<String> allLines = Files.readAllLines(filePath);
            var texto = allLines.stream().collect(Collectors.joining());
            String json = new XmlParser().toJson(texto);
            Files.writeString(path,json);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
