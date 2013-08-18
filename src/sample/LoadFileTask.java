package sample;

import javafx.concurrent.Task;
import javafx.scene.Cursor;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: jarad
 * Date: 8/16/13
 * Time: 1:44 PM
 */
class LoadFileTask extends Task<String> {
    private TailingTabbedFile tailingTabbedFile;
    private ScheduledExecutorService executor;

    public LoadFileTask(TailingTabbedFile tailingTabbedFile, ScheduledExecutorService executor) {
        this.tailingTabbedFile = tailingTabbedFile;
        this.executor = executor;
    }

    @Override
    protected String call() throws Exception {
        try (BufferedReader reader = Files.newBufferedReader(tailingTabbedFile.getFile(), Charset.forName("US-ASCII"))) {
            char[] chars = new char[100000];
            int read;
            while ((read = reader.read(chars)) > 0) {
                String value;
                if (read < 100000) {
                    value = String.copyValueOf(chars, 0, read);
                } else {
                    value = new String(chars);
                }

                tailingTabbedFile.appendText(value);
            }

            tailingTabbedFile.setLastReadIndex(Files.size(tailingTabbedFile.getFile()) - 1);

        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void done() {
        super.done();
        tailingTabbedFile.getTab().getTab().getTabPane().setCursor(Cursor.DEFAULT);
        tailingTabbedFile.setTask(executor.scheduleWithFixedDelay(tailingTabbedFile, 1000, 500, TimeUnit.MILLISECONDS));
    }
}
