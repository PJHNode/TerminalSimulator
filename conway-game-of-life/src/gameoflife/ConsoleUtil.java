package gameoflife;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public final class ConsoleUtil {

    private ConsoleUtil() {
    }

    // On Windows, the console codepage (not just System.out) must be switched to UTF-8,
    // otherwise block/dash characters render as garbled multi-byte text regardless of
    // PrintStream encoding.
    //
    // stdout/stderr must be INHERIT (not a pipe): if every stream is a pipe, the JVM
    // launches the chcp helper under a hidden, unattached console (CREATE_NO_WINDOW), so
    // chcp ends up changing that invisible console's codepage instead of the one actually
    // on screen. stdin is pointed at the NUL device instead of being inherited, so the
    // helper can't consume any of the real input stream (which would otherwise risk
    // eating the first bytes typed/piped into an interactive program).
    public static void configureUtf8Console() {
        if (System.getProperty("os.name", "").toLowerCase().contains("win")) {
            try {
                new ProcessBuilder("cmd.exe", "/c", "chcp", "65001")
                        .redirectInput(new File("NUL"))
                        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                        .redirectError(ProcessBuilder.Redirect.INHERIT)
                        .start()
                        .waitFor();
            } catch (Exception e) {
                // Console codepage could not be changed; continue with the current one.
            }
        }
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
    }
}
