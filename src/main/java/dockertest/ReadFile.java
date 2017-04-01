package dockertest;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ReadFile {

   private static final String MAPPED_FILE = "/file_in_container";

   public static void main(String[] args) throws Exception {
      System.out.println("STARTED");
      try {
         File currentDir = new File(".");
         System.out.println("current directory: '" + currentDir.getCanonicalPath() + "'");

         // List the files in the current directory for a bit of context
         Stream<Path> stream = Files.list(currentDir.toPath());
         stream
            .map(Path::toFile)
            .map(file -> {
                  try {
                     return file.getCanonicalFile();
                  } catch (IOException e) {
                     throw new UncheckedIOException(e);
                  }
               })
            .forEach(System.out::println);

         // Try to read the file we expect to be mapped in
         Path mappedFilePath = Paths.get(MAPPED_FILE);

         byte[] bytesInFile = null;
         try {
            bytesInFile = Files.readAllBytes(mappedFilePath);
         } catch (IOException e) {
            System.out.println("exception reading '" + mappedFilePath + "'");
            throw e;
         }

         String content = new String(bytesInFile, StandardCharsets.UTF_8);
         System.out.println(MAPPED_FILE + ": '" + content + "'");
      } finally {
         System.out.println("FINISHED");
      }
   }
}
