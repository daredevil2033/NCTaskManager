package ua.edu.sumdu.j2se.zuliukov.tasks.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.sumdu.j2se.zuliukov.tasks.Controller.TaskIO;
import ua.edu.sumdu.j2se.zuliukov.tasks.Controller.Tasks;
import ua.edu.sumdu.j2se.zuliukov.tasks.Model.ArrayTaskList;
import ua.edu.sumdu.j2se.zuliukov.tasks.Model.Task;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.CancellationException;

public class ConsoleAPI {
    private final static Logger logger = LoggerFactory.getLogger(ConsoleAPI.class);
    private final static Scanner sc = new Scanner(System.in);
    private final File file;
    private final ArrayTaskList taskList;

    public ConsoleAPI(String path, ArrayTaskList taskList) {
        this.file = new File(path);
        this.taskList = taskList;
    }

    public void readFile() {
        try {
            try {
                TaskIO.readBinary(taskList, file);
            } catch (EOFException e) {
                logger.error("File db has been corrupted");
                if (file.delete()) logger.warn("File db was deleted");
                if (file.createNewFile()) logger.warn("New file db was created");
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
            TaskIO.writeText(taskList, "snap.json");
            logger.info("Tasks snapped");
        } catch (IOException e) {
            logger.error(e.getMessage());
            logger.trace(e.getMessage(), e);
        }
    }

    public void mainMenu() {
        while (true) {
            logger.info("Options:");
            logger.info("1 - Save current tasks to the binary file(for next use) named db");
            logger.info("2 - Save current tasks to the json file(for reading) named snap.json");
            logger.info("3 - Enter Task Editor where you can view and edit current tasks");
            logger.info("4 - Show next task in today's schedule");
            logger.info("5 - Show calendar for the specified time period");
            logger.info("Enter number (1-5 or 0 - Save and Quit):");
            try {
                switch (inputLine()) {
                    case "1":
                        writeBinary();
                        break;
                    case "2":
                        writeJSON();
                        break;
                    case "3":
                        taskEditor();
                        break;
                    case "4":
                        remind();
                        break;
                    case "5":
                        calendar();
                        break;
                    default:
                        logger.error("Wrong input");
                }
            } catch (CancellationException e) {
                writeBinary();
                logger.warn("Quitting");
                return;
            }
        }
    }

    public void taskEditor() {
        int ts;
        while (true) {
            ts = taskList.size();
            if (ts == 0) {
                logger.warn("Task list is empty");
            } else printTasks(taskList);
            logger.info("Options:");
            logger.info("1 - Create new task");
            if (ts != 0) {
                logger.info("2 - Change existing task");
                logger.info("3 - Delete existing task");
                logger.info("Enter number (1-3 or 0 - Exit to Main Menu):");
            } else logger.info("Enter number (1 or 0 - Exit to Main Menu):");
            try {
                switch (inputLine()) {
                    case "1":
                        createTask();
                        break;
                    case "2":
                        if (ts != 0) {
                            changeTask();
                            break;
                        }
                    case "3":
                        if (ts != 0) {
                            deleteTask();
                            break;
                        }
                    default:
                        logger.error("Wrong input");
                }
            } catch (CancellationException e) {
                logger.info("Exiting to Main Menu");
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
        if (Objects.equals(input, "0")) throw new CancellationException();
        return input;
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

    private void createTask() {
        synchronized (taskList) {
            Task t = new Task();
            try {
                logger.info("Enter title");
                t.setTitle(inputLine());
                logger.info("Is repeated?(Y/N)");
                inputTime(t, inputYesNo());
                logger.info("Is active?(Y/N)");
                t.setActive(inputYesNo());
                taskList.add(t);
                logger.info("Task created");
            } catch (CancellationException e) {
                logger.warn("Creation was cancelled");
            }
        }
    }

    private void changeTask() {
        synchronized (taskList) {
            while (true) {
                try {
                    logger.info("Enter index");
                    Task t = taskList.getTask(Integer.parseInt(inputLine()));
                    logger.info("Options:");
                    logger.info("1 - Change title");
                    logger.info("2 - Change time");
                    logger.info("3 - Change activity)");
                    logger.info("Enter number (1-3 or 0 - Cancel change):");
                    switch (inputLine()) {
                        case "1":
                            logger.info("Enter title");
                            t.setTitle(inputLine());
                            logger.info("Title changed");
                            return;
                        case "2":
                            logger.info("Is repeated?(Y/N)");
                            inputTime(t, inputYesNo());
                            logger.info("Time changed");
                            return;
                        case "3":
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
    }

    private void deleteTask() {
        synchronized (taskList) {
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
    }

    private void printTasks(Iterable<Task> tasks) {
        int i = 0;
        for (Task t : tasks) {
            logger.info("[" + i++ + "] " + taskToString(t));
        }
    }

    private String taskToString(Task t) {
        if (t.isRepeated())
            return t.getTitle() + " " + t.getStartTime() + " " + t.getEndTime() + " " + t.getRepeatInterval() + " " + activeToString(t.isActive());
        else return t.getTitle() + " " + t.getTime() + " " + activeToString(t.isActive());
    }

    private String activeToString(boolean a) {
        return a ? "active" : "not active";
    }
}
