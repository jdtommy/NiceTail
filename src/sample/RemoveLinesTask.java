package sample;

import javafx.concurrent.Task;

/**
 * Created with IntelliJ IDEA.
 * User: jarad
 * Date: 8/16/13
 * Time: 1:52 PM
 */
public class RemoveLinesTask extends Task<Integer> {
    private TailingTab tailingTab;
    private int buffer;
    public static boolean lock;

    public RemoveLinesTask(TailingTab tailingTab, int buffer) {
        this.tailingTab = tailingTab;
        this.buffer = buffer;
    }

    @Override
    protected Integer call() throws Exception {
        getLock();
        int chars = 0;
        int lines = tailingTab.getTextArea().getParagraphs().size();
        int remove = lines - buffer;

        if (remove > 0) {
            for (int i = 0; i < remove; i++) {
                CharSequence line = tailingTab.getTextArea().getParagraphs().get(i);
                chars += (line.length() + 1);
            }
        }

        return chars;

    }

    @Override
    protected void succeeded() {
        super.succeeded();
        Integer value = getValue();
        System.out.println("deleting ------------ " + value);
        tailingTab.getTextArea().deleteText(0, value);

        tailingTab.end();

        lock = false;
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        lock = false;
    }

    @Override
    protected void failed() {
        super.failed();
        lock = false;
    }

    private synchronized boolean getLock(){
        try {
            while (lock) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        lock = true;
        return lock;
    }
}
