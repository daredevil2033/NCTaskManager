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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.CancellationException;

public class ConsoleAPI {
    private final static Logger logger = LoggerFactory.getLogger(ConsoleAPI.class);
    private final static Scanner sc = new Scanner(System.in);
    private final File file;
    private final ArrayTaskList taskList;

    /**
     * @param path String filepath to the binary file
     * @param taskList ArrayTaskList to store current state of a task list
     */
    public ConsoleAPI(String path, ArrayTaskList taskList) {
        this.file = new File(path);
        this.taskList = taskList;
    }

    /**
     * Reads the taskList from the binary file
     * Path to the file is specified in the constructor
     */
    public void readFile() {
        try {
            try {
                TaskIO.readBinary(taskList, file);
            } catch (EOFException e) {
                System.err.println("Binary file has been corrupted");
                if (file.delete()) System.err.println("Binary file was deleted");
                if (file.createNewFile()) System.err.println("New binary file was created");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Writes the taskList to the binary file
     * Path to the file is specified in the constructor
     */
    public void writeBinary() {
        try {
            TaskIO.writeBinary(taskList, file);
            System.out.println("Tasks saved");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Writes the taskList to the text file snap.json
     */
    public void writeJSON() {
        try {
            TaskIO.writeText(taskList, new File("snap.json"));
            System.out.println("Tasks snapped");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Displays the Main Menu
     */
    public void mainMenu() {
        while (true) {
            System.out.println("Options:");
            System.out.println("1 - Save current tasks to the binary file(for next use) named db");
            System.out.println("2 - Save current tasks to the json file(for reading) named snap.json");
            System.out.println("3 - Enter Task Editor where you can view and edit current tasks");
            System.out.println("4 - Show next task in today's schedule");
            System.out.println("5 - Show calendar for the specified time period");
            System.out.println("Enter number (1-5 or 0 - Save and Quit):");
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
                        System.err.println("Wrong input");
                }
            } catch (CancellationException e) {
                writeBinary();
                System.out.println("Quitting");
                return;
            }
        }
    }

    /**
     * Displays the Task Editor
     * Options 2 and 3 are only available when the taskList isn't empty
     */
    public void taskEditor() {
        int ts;
        while (true) {
            ts = taskList.size();
            if (ts == 0) {
                System.out.println("Task list is empty");
            } else printTasks(taskList);
            System.out.println("Options:");
            System.out.println("1 - Create new task");
            if (ts != 0) {
                System.out.println("2 - Change existing task");
                System.out.println("3 - Delete existing task");
                System.out.println("Enter number (1-3 or 0 - Exit to Main Menu):");
            } else System.out.println("Enter number (1 or 0 - Exit to Main Menu):");
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
                        System.err.println("Wrong input");
                }
            } catch (CancellationException e) {
                System.out.println("Exiting to Main Menu");
                return;
            }
        }
    }

    /**
     * Prompts entering the time frame and displays a calendar for it
     */
    public void calendar() {
        while (true) {
            try {
                LocalDateTime start, end;
                System.out.println("Enter start");
                start = timeFormatInput();
                System.out.println("Enter end");
                end = timeFormatInput();
                SortedMap<LocalDateTime, Set<Task>> smap = Tasks.calendar(taskList, start, end);
                for (Map.Entry<LocalDateTime, Set<Task>> entry : smap.entrySet()) {
                    System.out.println(entry.getKey());
                    printTasks(entry.getValue());
                }
                return;
            } catch (DateTimeParseException e) {
                System.err.println("Invalid format");
            } catch (CancellationException e) {
                System.out.println("Input was cancelled");
                return;
            } catch (InstantiationException | IllegalAccessException e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * Displays the next task(or it's repetition) in the current day
     */
    public void remind() {
        try {
            LocalDateTime n = LocalDateTime.now();
            Set<Map.Entry<LocalDateTime, Set<Task>>> entries = Tasks.calendar(taskList, n, n.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0)).entrySet();
            if (!entries.isEmpty()) {
                Map.Entry<LocalDateTime, Set<Task>> entry = entries.iterator().next();
                System.out.println(entry.getKey());
                printTasks(entry.getValue());
            } else System.out.println("No tasks ahead");
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Forwards the input after checking it for the halt input("0")
     * If the halt input is detected throws an Exception indicating the intent to stop the current action
     * @return input String
     * @throws CancellationException when input is "0"
     */
    public String inputLine() throws CancellationException {
        String input = sc.nextLine();
        if (Objects.equals(input, "0")) throw new CancellationException();
        return input;
    }

    /**
     * Converts Y(y)/N(n) input to boolean
     * @return boolean meaning of the input
     * @throws CancellationException if the input was cancelled
     */
    private boolean inputYesNo() throws CancellationException {
        while (true) {
            String inp = inputLine();
            if (inp.equalsIgnoreCase("Y")) {
                return true;
            } else if (inp.equalsIgnoreCase("N")) {
                return false;
            } else {
                System.err.println("Invalid input");
            }
        }
    }

    /**
     * Prompts to input start and end times as well as repeat interval for the task if it is repeated
     * Or just the time otherwise
     * @param t Task to input time for
     * @param repeated indicates whether the Task is repeated
     * @throws CancellationException if the input was cancelled
     */
    private void inputTime(Task t, boolean repeated) throws CancellationException {
        while (true) {
            try {
                if (repeated) {
                    LocalDateTime start, end;
                    System.out.println("Enter start");
                    start = timeFormatInput();
                    System.out.println("Enter end");
                    end = timeFormatInput();
                    System.out.println("Enter repeat interval");
                    t.setTime(start, end, Integer.parseInt(sc.nextLine()));
                } else {
                    System.out.println("Enter time");
                    t.setTime(timeFormatInput());
                }
                return;
            } catch (DateTimeParseException e) {
                System.err.println("Invalid format");
            } catch (NumberFormatException e) {
                System.err.println("Not a number");
            }
        }
    }

    /**
     * Converts the String input into LocalDateTime value using the format uuuu-MM-dd HH:mm
     * @return LocalDateTime value of input
     * @throws CancellationException if the input was cancelled
     * @throws DateTimeParseException if the input doesn't match the format
     */
    private LocalDateTime timeFormatInput() throws CancellationException {
        String format = "uuuu-MM-dd HH:mm";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        System.out.println("Format: " + format);
        return LocalDateTime.parse(inputLine(),dtf);
    }

    /**
     * Prompts entering the characteristics of the new Task and adds it to the taskList
     */
    private void createTask() {
        synchronized (taskList) {
            Task t = new Task();
            try {
                System.out.println("Enter title");
                t.setTitle(inputLine());
                System.out.println("Is repeated?(Y/N)");
                inputTime(t, inputYesNo());
                System.out.println("Is active?(Y/N)");
                t.setActive(inputYesNo());
                taskList.add(t);
                System.out.println("Task created");
            } catch (CancellationException e) {
                System.out.println("Creation was cancelled");
            }
        }
    }

    /**
     * Changes the chosen characteristic of a chosen Task
     */
    private void changeTask() {
        synchronized (taskList) {
            while (true) {
                try {
                    System.out.println("Enter index");
                    Task t = taskList.getTask(Integer.parseInt(inputLine()));
                    System.out.println("Options:");
                    System.out.println("1 - Change title");
                    System.out.println("2 - Change time");
                    System.out.println("3 - Change activity)");
                    System.out.println("Enter number (1-3 or 0 - Cancel change):");
                    switch (inputLine()) {
                        case "1":
                            System.out.println("Enter title");
                            t.setTitle(inputLine());
                            System.out.println("Title changed");
                            return;
                        case "2":
                            System.out.println("Is repeated?(Y/N)");
                            inputTime(t, inputYesNo());
                            System.out.println("Time changed");
                            return;
                        case "3":
                            System.out.println("Is active?(Y/N)");
                            t.setActive(inputYesNo());
                            System.out.println("Activity changed");
                            return;
                    }
                } catch (IndexOutOfBoundsException e) {
                    System.err.println("Wrong index");
                } catch (CancellationException e) {
                    System.out.println("Change was cancelled");
                    return;
                }
            }
        }
    }

    /**
     * Deletes the chosen task
     */
    private void deleteTask() {
        synchronized (taskList) {
            while (true) {
                try {
                    System.out.println("Enter index");
                    taskList.remove(taskList.getTask(Integer.parseInt(inputLine())));
                    System.out.println("Task deleted");
                    return;
                } catch (IndexOutOfBoundsException e) {
                    System.err.println("Wrong index");
                } catch (NumberFormatException e) {
                    System.err.println("Not a number");
                } catch (CancellationException e) {
                    System.out.println("Deletion was cancelled");
                    return;
                }
            }
        }
    }

    /**
     * Indexes and prints the supplied tasks
     * @param tasks collection of tasks to be printed
     */
    private void printTasks(Iterable<Task> tasks) {
        int i = 0;
        for (Task t : tasks) {
            System.out.println("[" + i++ + "] " + taskToString(t));
        }
    }

    /**
     * Converts Task attributes into a compact String
     * @param t Task to be converted
     * @return compact String representation of the Task
     */
    private String taskToString(Task t) {
        if (t.isRepeated())
            return t.getTitle() + " " + t.getStartTime() + " " + t.getEndTime() + " " + t.getRepeatInterval() + " " + activeToString(t.isActive());
        else return t.getTitle() + " " + t.getTime() + " " + activeToString(t.isActive());
    }

    /**
     * Converts true/false to "active"/"not active"
     * Used for a better representation of an active attribute state
     * @param a boolean value to be converted
     * @return "active" when a==true and "not active" when a==false
     */
    private String activeToString(boolean a) {
        return a ? "active" : "not active";
    }
}
