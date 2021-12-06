package ua.edu.sumdu.j2se.zuliukov.tasks;

import java.util.stream.Stream;

public abstract class AbstractTaskList implements Iterable<Task> {
    protected int size;
    protected ListTypes.types type;

    public AbstractTaskList(int size, ListTypes.types type) {
        this.size = size;
        this.type = type;
    }

    public abstract void add(Task task) throws IllegalArgumentException;

    public abstract boolean remove(Task task);

    public abstract int size();

    public abstract Task getTask(int index) throws IndexOutOfBoundsException;

    public abstract Stream<Task> getStream();

    public final AbstractTaskList incoming(int from, int to) throws IllegalArgumentException {
        if (from < 0) throw new IllegalArgumentException("From cannot be less than 0");
        if (to < 0) throw new IllegalArgumentException("To cannot be less than 0");
        AbstractTaskList tempTaskList = TaskListFactory.createTaskList(type);
        getStream().filter(task -> (task.nextTimeAfter(from) != -1 && task.nextTimeAfter(from) <= to)).forEach(tempTaskList::add);
        return tempTaskList;
    }
}