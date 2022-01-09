package ua.edu.sumdu.j2se.zuliukov.tasks;

import ua.edu.sumdu.j2se.zuliukov.tasks.Model.ArrayTaskList;
import ua.edu.sumdu.j2se.zuliukov.tasks.View.Alerter;
import ua.edu.sumdu.j2se.zuliukov.tasks.View.ConsoleAPI;

public class Main {
    public static void main(String[] args) {
        ArrayTaskList taskList = new ArrayTaskList();
        ConsoleAPI capi = new ConsoleAPI("db", taskList);
        capi.readFile();
        Thread t = new Thread(null, new Alerter(taskList), "alerter");
        t.start();
        capi.mainMenu();
        System.exit(0);
    }
}
