import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class output {
    public void writeData(String outFile, String outText) {
        try {
            Files.writeString(Path.of(outFile), outText, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo escribir el archivo de salida", exception);
        }
    }
}
