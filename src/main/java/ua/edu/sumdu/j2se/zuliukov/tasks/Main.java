package ua.edu.sumdu.j2se.zuliukov.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Scanner;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);
    private static Scanner sc = new Scanner(System.in);
    private static File file = new File("db");
    private static ArrayTaskList taskList = new ArrayTaskList();

    public static void main(String[] args) {
        ConsoleAPI capi = new ConsoleAPI(logger,sc,file,taskList);
        capi.readFile();
        Thread t  = new Thread(new Alerter(logger,taskList));
        t.setDaemon(true);
        t.setName("alerter");
        t.start();
        logger.info("Type help to see available commands");
        while (true) {
            switch (capi.inputLine()) {
                case "exit":
                    capi.writeBinary();
                    System.exit(0);
                case "save":
                    capi.writeBinary();
                    break;
                case "snap":
                    capi.writeJSON();
                    break;
                case "edit":
                    t.suspend();
                    capi.editTasks();
                    t.resume();
                    break;
                case "remind":
                    capi.remind();
                    break;
                case "help":
                    capi.help();
                    break;
                case "calendar":
                    capi.calendar();
                    break;
                default:
                    logger.error("Wrong input");
            }
        }
    }
}
