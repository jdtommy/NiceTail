package sample;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;

/**
 * Created with IntelliJ IDEA.
 * User: jarad
 * Date: 8/16/13
 * Time: 9:48 AM
 */
public class TailingTabbedFile implements Runnable {
    private Path file;
    private int frequency;
    private int buffer = 5000;
    private TailingTab tab;
    private long lastReadIndex;
    private ExecutorService executor = Executors.newFixedThreadPool(1);
    private ScheduledFuture<?> task;
    private boolean paused;

    public TailingTabbedFile(String fileName, TailingTab tab) {
        this.tab = tab;
        setupTab();
        setupFile(fileName);
        tab.getTab().setText(file.getFileName().toString());
        tab.getTab().setTooltip(new Tooltip(file.toString()));
    }

    private void setupTab() {
        final ToggleButton pauseButton = new ToggleButton("Pause Tailing");
        pauseButton.setMinWidth(110);
        pauseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                paused = !paused;
                pauseButton.setText(paused ? "Unpause Tailing" : "Pause Tailing");
            }
        });

        final Button pauseScrollButton = new Button("Pause Scrolling");
        pauseScrollButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                tab.pause();
                pauseScrollButton.setText(tab.isPaused() ? "Unpause Scrolling" : "Pause Scrolling");
            }
        });

        Button clearButton = new Button("clear");
        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent actionEvent) {
                tab.getTextArea().clear();
            }
        });

        tab.addButton(pauseButton);
//        tab.addButton(pauseScrollButton);
        tab.addButton(clearButton);

        Platform.runLater(new Runnable() {
            @Override public void run() {
                ScrollPane scrollPane = (ScrollPane) tab.getTextArea().lookup(".scroll-pane");
                scrollPane.vvalueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                        if (!pauseButton.isSelected()){
                            paused = observableValue.getValue().doubleValue() != 1;
                        }
                    }
                });
            }
        });

    }

    private void setupFile(String fileName) {
        try {
            file = Paths.get(fileName);

            if (!Files.exists(file)) {
                throw new IllegalArgumentException("File " + fileName + " does not exist");
            }
        } catch (Exception e) {
            e.printStackTrace();
            //todo handle this
        }
    }

    void appendText(final String chunk) {
        Runnable runner = new Runnable() {
            @Override
            public void run() {
                TextArea textArea = tab.getTextArea();
                removeLinesIfNeeded();
                textArea.appendText(chunk);

//                Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        tab.end();
//                    }
//                });
            }
        };

        if (Platform.isFxApplicationThread()) {
            runner.run();
        } else {
            Platform.runLater(runner);
        }
    }

    private void removeLinesIfNeeded() {
        int lines = tab.getTextArea().getParagraphs().size();

        if (lines > buffer) {
            executor.execute(new RemoveLinesTask(tab, buffer));
        }
    }

    public void setLastReadIndex(long lastReadIndex) {
        this.lastReadIndex = lastReadIndex;
    }

    public Path getFile() {
        return file;
    }

    public TailingTab getTab() {
        return tab;
    }

    private long clearArea() throws IOException {
        tab.getTextArea().clear();
        lastReadIndex = 0;
        return Files.size(file) - 1;
    }

    @Override
    public void run() {
        if (paused) {
        } else {
            try (BufferedReader reader = Files.newBufferedReader(file, Charset.forName("US-ASCII"))) {
                long lastIndex = Files.size(file) - 1;
                long charsToRead = lastIndex - lastReadIndex;

                if (charsToRead < 0) {
                    charsToRead = clearArea();
                }

                if (charsToRead > 0) {
                    final char[] chars = new char[(int) charsToRead];

                    reader.skip(lastReadIndex + 1);
                    reader.read(chars);

                    appendText(new String(chars));
                    lastReadIndex += (charsToRead);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopExecutors() {
        executor.shutdown();
    }

    public void setTask(ScheduledFuture<?> task) {
        this.task = task;
    }

    public ScheduledFuture<?> getTask() {
        return task;
    }

    public int getBuffer() {
        return buffer;
    }

    public void setBuffer(int buffer) {
        this.buffer = buffer;
    }
}
