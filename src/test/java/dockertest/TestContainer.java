package dockertest;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.output.WaitingConsumer;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class TestContainer {

   private static final Logger logger = LoggerFactory.getLogger(TestContainer.class);

   private static final File testFile = new File("some_file");

   private static final String testFilePath = testFile.getAbsolutePath();

   public static GenericContainer<?> testContainer  =
      new GenericContainer<>("docker-mapped-file:latest")
         .withFileSystemBind(testFilePath, "/file_in_container",BindMode.READ_ONLY);

   @Before
   public void setUp() throws Exception {
      // verify that the test file exists, and is a file
      assertTrue(testFile.exists());
      assertTrue(testFile.isFile());
      String content = new String(Files.readAllBytes(Paths.get(testFilePath)), StandardCharsets.UTF_8);
      logger.info("outside container: '" + testFilePath + "': '" + content + "'");
   }

   @Test
   public void testFileInContainer() throws Exception {
      logger.info("test begin: '" + testFilePath + "'");
      testContainer.start();

      // Wait until the container is done with its work.  Inspired by
      // https://www.testcontainers.org/usage/options.html#following-container-output
      WaitingConsumer waitingConsumer = new WaitingConsumer();
      Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
      Consumer<OutputFrame> composedConsumer = waitingConsumer.andThen(logConsumer);
      testContainer.followOutput(composedConsumer);

      waitingConsumer.waitUntil(frame -> frame.getUtf8String().contains("FINISHED"), 5, TimeUnit.SECONDS);
      logger.info("test end");
   }
}
