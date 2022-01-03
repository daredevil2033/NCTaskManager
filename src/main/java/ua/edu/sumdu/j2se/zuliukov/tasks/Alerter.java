package ua.edu.sumdu.j2se.zuliukov.tasks;

import org.slf4j.Logger;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

public class Alerter implements Runnable {
    private final Logger logger;
    private final ArrayTaskList taskList;

    public Alerter(Logger logger, ArrayTaskList taskList) {
        this.logger = logger;
        this.taskList = taskList;
    }

    @Override
    public void run() {
        while (true){
            try {
                LocalDateTime n = LocalDateTime.now().withNano(0);
                Set<Map.Entry<LocalDateTime, Set<Task>>> entries = Tasks.calendar(taskList, n.minusSeconds(1), n.plusSeconds(1)).entrySet();
                if (!entries.isEmpty()) {
                    Map.Entry<LocalDateTime, Set<Task>> entry = entries.iterator().next();
                    if (entry.getKey().isEqual(n)) {
                        if (SystemTray.isSupported()) {
                            String msg = "";
                            int i = 0;
                            for (Task t : entry.getValue()) {
                                msg = msg.concat("[" + i + "] " + t.getTitle() + "\n");
                                i++;
                            }
                            displayTray(entry.getKey(), msg);
                        } else {
                            logger.error("System tray not supported!");
                        }
                    }
                }
                Thread.sleep(1000);
            } catch (InstantiationException | IllegalAccessException | AWTException | InterruptedException e) {
                logger.error(e.getMessage());
                logger.trace(e.getMessage(), e);
            }
        }
    }

    public void displayTray(LocalDateTime ldt, String msg) throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("System tray icon demo");
        tray.add(trayIcon);
        trayIcon.displayMessage(String.valueOf(ldt), msg, TrayIcon.MessageType.INFO);
    }
}
