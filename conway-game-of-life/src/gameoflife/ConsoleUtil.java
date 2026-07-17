package gameoflife;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public final class ConsoleUtil {

    private ConsoleUtil() {
    }

    // On Windows, the console codepage (not just System.out) must be switched to UTF-8,
    // otherwise block/dash characters render as "?" regardless of PrintStream encoding.
    //
    // The chcp process must NOT inherit stdin: inheritIO() shares the real console input
    // handle, and a stray read by the child can silently eat the first bytes the program
    // was meant to read (only surfaces once something actually reads stdin afterwards).
    public static void configureUtf8Console() {
        if (System.getProperty("os.name", "").toLowerCase().contains("win")) {
            try {
                new ProcessBuilder("cmd.exe", "/c", "chcp", "65001")
                        .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                        .redirectError(ProcessBuilder.Redirect.DISCARD)
                        .start()
                        .waitFor();
            } catch (Exception e) {
                // Console codepage could not be changed; continue with the current one.
            }
        }
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
    }
}
