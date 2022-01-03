package ua.edu.sumdu.j2se.zuliukov.tasks;

import org.slf4j.Logger;

import java.io.EOFException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.CancellationException;

public class ConsoleAPI {
    private final Logger logger;
    private final Scanner sc;
    private final File file;
    private final ArrayTaskList taskList;

    public ConsoleAPI(Logger logger, Scanner sc, File file, ArrayTaskList taskList) {
        this.logger = logger;
        this.sc = sc;
        this.file = file;
        this.taskList = taskList;
    }

    public void readFile() {
        try {
            try {
                if (file.createNewFile()) logger.info("File was created");
                else {
                    TaskIO.readBinary(taskList, file);
                }
            } catch (EOFException e) {
                logger.error("File has been corrupted");
                if (file.delete()) logger.warn("File was deleted");
                if (file.createNewFile()) logger.warn("New file was created");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            logger.trace(e.getMessage(), e);
        }
    }

    public void writeBinary() {
        try {
            TaskIO.writeBinary(taskList, file);
            logger.info("Tasks saved");
        } catch (IOException e) {
            logger.error(e.getMessage());
            logger.trace(e.getMessage(), e);
        }
    }

    public void writeJSON() {
        try {
            TaskIO.write(taskList, new FileWriter("snap.json"));
            logger.info("Tasks snapped");
        } catch (IOException e) {
            logger.error(e.getMessage());
            logger.trace(e.getMessage(), e);
        }
    }

    public void help() {
        logger.info("edit - enter task editor where you can view and edit current tasks");
        logger.info("calendar - show calendar for the specified time period");
        logger.info("remind - show next task in today's schedule");
        logger.info("save - save current tasks to the binary file(for next use) named db");
        logger.info("snap - save current tasks to the json file(for reading) named snap.json");
        logger.info("exit - save to db and quit");
    }

    public void editTasks() {
        logger.info("Type home to exit task editor");
        while (true) {
            logger.info("Available options: view, create, change, delete");
            switch (inputLine()) {
                case "view":
                    if (printTasks(taskList) == 0) logger.warn("Task list is empty");
                    break;
                case "create":
                    createTask();
                    break;
                case "change":
                    if (printTasks(taskList) == 0) logger.warn("Task list is empty");
                    else {
                        changeTask();
                    }
                    break;
                case "delete":
                    if (printTasks(taskList) == 0) logger.warn("Task list is empty");
                    else {
                        deleteTask();
                    }
                    break;
                case "home":
                    logger.info("Exiting to main menu");
                    logger.info("Type help to see available commands");
                    return;
            }
        }
    }

    public void calendar() {
        while (true) {
            try {
                LocalDateTime start, end;
                logger.info("Enter start");
                start = timeFormatInput();
                logger.info("Enter end");
                end = timeFormatInput();
                SortedMap<LocalDateTime, Set<Task>> smap = Tasks.calendar(taskList, start, end);
                for (Map.Entry<LocalDateTime, Set<Task>> entry : smap.entrySet()) {
                    logger.info(String.valueOf(entry.getKey()));
                    printTasks(entry.getValue());
                }
                return;
            } catch (DateTimeParseException e) {
                logger.error("Invalid format");
            } catch (CancellationException e) {
                logger.warn("Input was cancelled");
                return;
            } catch (InstantiationException | IllegalAccessException e) {
                logger.error(e.getMessage());
                logger.trace(e.getMessage(), e);
            }
        }
    }

    public void remind() {
        try {
            LocalDateTime n = LocalDateTime.now();
            Set<Map.Entry<LocalDateTime, Set<Task>>> entries = Tasks.calendar(taskList, n, n.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0)).entrySet();
            if (!entries.isEmpty()) {
                Map.Entry<LocalDateTime, Set<Task>> entry = entries.iterator().next();
                logger.info(String.valueOf(entry.getKey()));
                printTasks(entry.getValue());
            } else logger.warn("No tasks ahead");
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error(e.getMessage());
            logger.trace(e.getMessage(), e);
        }
    }

    public String inputLine() throws CancellationException {
        String input = sc.nextLine();
        logger.debug(input);
        if (Objects.equals(input, "cancel")) throw new CancellationException();
        return input;
    }

    private void createTask() {
        Task t = new Task();
        try {
            logger.info("Enter title");
            t.setTitle(inputLine());
            logger.info("Is repeated?(Y/N)");
            inputTime(t, inputYesNo());
            logger.info("Is active?(Y/N)");
            t.setActive(inputYesNo());
            taskList.add(t);
        } catch (CancellationException e) {
            logger.warn("Creation was cancelled");
        }
    }

    private void changeTask() {
        while (true) {
            try {
                logger.info("Enter index");
                Task t = taskList.getTask(Integer.parseInt(inputLine()));
                logger.info("Enter what to change(title/time/activity)");
                switch (inputLine()) {
                    case "title":
                        logger.info("Enter title");
                        t.setTitle(inputLine());
                        logger.info("Title changed");
                        return;
                    case "time":
                        logger.info("Is repeated?(Y/N)");
                        inputTime(t, inputYesNo());
                        logger.info("Time changed");
                        return;
                    case "activity":
                        logger.info("Is active?(Y/N)");
                        t.setActive(inputYesNo());
                        logger.info("Activity changed");
                        return;
                }
            } catch (IndexOutOfBoundsException e) {
                logger.error("Wrong index");
            } catch (CancellationException e) {
                logger.warn("Change was cancelled");
                return;
            }
        }
    }

    private void deleteTask() {
        while (true) {
            try {
                logger.info("Enter index");
                taskList.remove(taskList.getTask(Integer.parseInt(inputLine())));
                logger.info("Task deleted");
                return;
            } catch (IndexOutOfBoundsException e) {
                logger.error("Wrong index");
            } catch (NumberFormatException e) {
                logger.error("Not a number");
            } catch (CancellationException e) {
                logger.warn("Deletion was cancelled");
                return;
            }
        }
    }

    private int printTasks(Iterable<Task> tasks) {
        int i = 0;
        for (Task t : tasks) {
            logger.info("[" + i + "] " + taskToString(t));
            i++;
        }
        return i;
    }

    private String taskToString(Task t) {
        if (t.isRepeated())
            return t.getTitle() + " " + t.getStartTime() + " " + t.getEndTime() + " " + t.getRepeatInterval() + " " + activeToString(t.isActive());
        else return t.getTitle() + " " + t.getTime() + " " + activeToString(t.isActive());
    }

    private String activeToString(boolean a) {
        return a ? "active" : "not active";
    }

    private boolean inputYesNo() throws CancellationException {
        while (true) {
            String inp = inputLine();
            if (inp.equalsIgnoreCase("Y")) {
                return true;
            } else if (inp.equalsIgnoreCase("N")) {
                return false;
            } else {
                logger.error("Invalid input");
            }
        }
    }

    private void inputTime(Task t, boolean repeated) throws CancellationException {
        while (true) {
            try {
                if (repeated) {
                    LocalDateTime start, end;
                    logger.info("Enter start");
                    start = timeFormatInput();
                    logger.info("Enter end");
                    end = timeFormatInput();
                    logger.info("Enter repeat interval");
                    t.setTime(start, end, Integer.parseInt(sc.nextLine()));
                } else {
                    logger.info("Enter time");
                    t.setTime(timeFormatInput());
                }
                return;
            } catch (DateTimeParseException e) {
                logger.error("Invalid format");
            } catch (NumberFormatException e) {
                logger.error("Not a number");
            }
        }
    }

    private LocalDateTime timeFormatInput() throws CancellationException {
        logger.info("Format: uuuu-MM-ddTHH:mm:ss");
        return LocalDateTime.parse(inputLine());
    }
}
