package sample;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * Created with IntelliJ IDEA.
 * User: jarad
 * Date: 8/16/13
 * Time: 10:42 AM
 */
public class TailingTab {
    private Tab tab;
    private TextArea textArea;
    private HBox buttonArea;
    private boolean paused;

    public TailingTab() {
        textArea = new TextArea();

        buttonArea = new HBox();
        buttonArea.setPadding(new Insets(5, 10, 5, 10));
        buttonArea.setId("button_area");
        buttonArea.setSpacing(5);

        BorderPane pane = new BorderPane();
        pane.setCenter(textArea);
        pane.setBottom(buttonArea);

        tab = new Tab();
        tab.setContent(pane);
    }

    public Tab getTab() {
        return tab;
    }

    public void setTab(Tab tab) {
        this.tab = tab;
    }

    public TextArea getTextArea() {
        return textArea;
    }

    public void setTextArea(TextArea textArea) {
        this.textArea = textArea;
    }

    public void addButton(ButtonBase button) {
        buttonArea.getChildren().add(button);
    }

    public void end() {
        if (!paused) {
            textArea.end();
        }
    }

    public void pause() {
        paused = !paused;
    }

    public boolean isPaused() {
        return paused;
    }
}
