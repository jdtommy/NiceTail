package sample;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.prefs.Preferences;

public class Controller {
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
    private List<TailingTabbedFile> tabList = new ArrayList<>();

    @FXML private TabPane tabs;

    public void addFile(ActionEvent actionEvent) {
        TailingTab tab = new TailingTab();
        String fileName = getFile();
        tabs.getTabs().add(tab.getTab());

        TailingTabbedFile tailingTabbedFile = new TailingTabbedFile(fileName, tab);
        tabList.add(tailingTabbedFile);
        startTailing(tailingTabbedFile);
        tab.getTab().setOnClosed(new TabEventMonitor(this, tailingTabbedFile));

    }

    private String getFile() {
        FileChooser chooser = new FileChooser();
        Preferences userPreferences = Preferences.userNodeForPackage(getClass());

        String lastLocation = userPreferences.get(ApplicationPreferences.LAST_FILE.toString(), "");
        File lastFile = new File(lastLocation);
        if (lastFile.exists() && lastFile.isDirectory()){
            chooser.setInitialDirectory(lastFile);
        }

        File file = chooser.showOpenDialog(tabs.getScene().getWindow());
        String absolutePath = file.getAbsolutePath();
        userPreferences.put(ApplicationPreferences.LAST_FILE.toString(), file.getParent());

        return absolutePath;
    }

    private Task startTailing(TailingTabbedFile tab) {
        Task task = new LoadFileTask(tab, executor);
        tab.getTab().getTab().getTabPane().setCursor(Cursor.WAIT);
        new Thread(task).start();
        return task;
    }

    public void stopExecutors(){
        executor.shutdown();
        for (TailingTabbedFile tailingTabbedFile : tabList) {
            tailingTabbedFile.stopExecutors();
        }
    }

    public void removedTabbedFile(TailingTabbedFile tailingTabbedFile) {
        tabList.remove(tailingTabbedFile);
        tailingTabbedFile.getTask().cancel(false);
    }

    public void addListenersToVisibleComponents() {

    }
}
