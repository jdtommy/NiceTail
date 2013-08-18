package sample;

import javafx.event.Event;
import javafx.event.EventHandler;

/**
 * Created with IntelliJ IDEA.
 * User: jarad
 * Date: 8/17/13
 * Time: 11:23 AM
 */
public class TabEventMonitor implements EventHandler<Event> {
    private Controller controller;
    private TailingTabbedFile tailingTabbedFile;

    public TabEventMonitor(Controller controller, TailingTabbedFile tailingTabbedFile) {
        this.controller = controller;
        this.tailingTabbedFile = tailingTabbedFile;
    }

    @Override
    public void handle(Event event) {
        tailingTabbedFile.stopExecutors();
        controller.removedTabbedFile(tailingTabbedFile);
    }
}
