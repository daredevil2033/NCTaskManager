package ua.edu.sumdu.j2se.zuliukov.tasks.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.sumdu.j2se.zuliukov.tasks.Controller.Tasks;
import ua.edu.sumdu.j2se.zuliukov.tasks.Model.ArrayTaskList;
import ua.edu.sumdu.j2se.zuliukov.tasks.Model.Task;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

public class Alerter implements Runnable {
    private static final boolean noSupport = !SystemTray.isSupported();
    private final Logger logger = LoggerFactory.getLogger(Alerter.class);
    private final ArrayTaskList taskList;
    private TrayIcon trayIcon;

    public Alerter(ArrayTaskList taskList) {
        this.taskList = taskList;
        if (noSupport) {
            logger.error("System tray not supported");
        } else {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
            trayIcon = new TrayIcon(image, "NCTaskManager");
            trayIcon.setImageAutoSize(true);
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                logger.error(e.getMessage());
                logger.trace(e.getMessage(), e);
            }
        }
    }

    @Override
    public void run() {
        if (noSupport) return;
        while (true) {
            try {
                synchronized (taskList) {
                    LocalDateTime n = LocalDateTime.now().withNano(0);
                    Set<Map.Entry<LocalDateTime, Set<Task>>> entries = Tasks.calendar(taskList, n.minusSeconds(1), n).entrySet();
                    if (!entries.isEmpty()) {
                        Map.Entry<LocalDateTime, Set<Task>> entry = entries.iterator().next();
                        if (entry.getKey().isEqual(n)) {
                            String msg = "";
                            int i = 0;
                            for (Task t : entry.getValue()) {
                                msg = msg.concat("[" + i + "] " + t.getTitle() + "\n");
                                i++;
                            }
                            displayTray(entry.getKey(), msg);
                        }
                    }
                }
                Thread.sleep(1000);
            } catch (InstantiationException | IllegalAccessException | InterruptedException e) {
                logger.error(e.getMessage());
                logger.trace(e.getMessage(), e);
            }
        }
    }

    public void displayTray(LocalDateTime ldt, String msg) {
        trayIcon.displayMessage(String.valueOf(ldt), msg, TrayIcon.MessageType.NONE);
    }
}
