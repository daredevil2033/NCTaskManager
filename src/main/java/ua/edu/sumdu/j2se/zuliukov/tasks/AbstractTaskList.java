package ua.edu.sumdu.j2se.zuliukov.tasks;

public abstract class AbstractTaskList implements Iterable<Task> {
    protected int size;
    protected ListTypes.types type;

    public abstract void add(Task task) throws IllegalArgumentException;

    public abstract boolean remove(Task task);

    public abstract int size();

    public abstract Task getTask(int index) throws IndexOutOfBoundsException;

    public AbstractTaskList(int size, ListTypes.types type) {
        this.size = size;
        this.type = type;
    }

    public AbstractTaskList incoming(int from, int to) throws IllegalArgumentException {
        if (from < 0) throw new IllegalArgumentException("From cannot be less than 0");
        if (to < 0) throw new IllegalArgumentException("To cannot be less than 0");
        AbstractTaskList tempTaskList = TaskListFactory.createTaskList(type);
        for (int i = 0; i < size; i++) {
            Task temp = getTask(i);
            if (temp.nextTimeAfter(from) != -1 && temp.nextTimeAfter(from) <= to) tempTaskList.add(temp);
        }
        return tempTaskList;
    }
}