package ua.edu.sumdu.j2se.zuliukov.tasks;

import java.io.Serializable;
import java.util.stream.Stream;

public abstract class AbstractTaskList implements Iterable<Task>, Serializable {
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
}
