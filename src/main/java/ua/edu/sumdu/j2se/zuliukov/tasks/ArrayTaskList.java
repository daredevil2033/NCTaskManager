package ua.edu.sumdu.j2se.zuliukov.tasks;

import java.util.Iterator;

public class ArrayTaskList extends AbstractTaskList implements Cloneable {
    private Task[] tasks;

    public void add(Task task) throws IllegalArgumentException {
        if (task == null) throw new IllegalArgumentException("Task cannot be null");
        else {
            if (size == 0) {
                tasks = new Task[++size];
            } else {
                Task[] temp = tasks;
                tasks = new Task[++size];
                System.arraycopy(temp, 0, tasks, 0, size - 1);
            }
            tasks[size - 1] = task;
        }
    }

    public boolean remove(Task task) {
        for (int i = 0; i < size; i++) {
            if (tasks[i].equals(task)) {
                if (size == 1) {
                    tasks = null;
                    size--;
                } else {
                    Task[] temp = tasks;
                    tasks = new Task[--size];
                    System.arraycopy(temp, 0, tasks, 0, i);
                    System.arraycopy(temp, i + 1, tasks, i, size - i);
                }
                return true;
            }
        }
        return false;
    }

    public int size() {
        return size;
    }

    public Task getTask(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index > size - 1) throw new IndexOutOfBoundsException();
        else return tasks[index];
    }

    public ArrayTaskList() {
        super(0, ListTypes.types.ARRAY);
    }

    class ArrayTaskListIterator implements Iterator<Task> {
        int current;

        public ArrayTaskListIterator() {
            current = -1;
        }

        @Override
        public boolean hasNext() {
            return current != size - 1;
        }

        @Override
        public Task next() {
            return tasks[++current];
        }

        @Override
        public void remove() {
            if (current == -1) throw new IllegalStateException();
            else {
                if (size == 1) {
                    tasks = null;
                    size--;
                } else {
                    Task[] temp = tasks;
                    tasks = new Task[--size];
                    System.arraycopy(temp, 0, tasks, 0, current);
                    System.arraycopy(temp, current + 1, tasks, current, size - current);
                }
                current--;
            }
        }
    }

    @Override
    public Iterator<Task> iterator() {
        return new ArrayTaskListIterator();
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null || getClass() != otherObject.getClass()) return false;
        ArrayTaskList other = (ArrayTaskList) otherObject;
        if (other.size() != size) return false;
        boolean eq = true;
        for (int i = 0; i < size; i++) {
            if (!other.getTask(i).equals(tasks[i])) {
                eq = false;
                break;
            }
        }
        return eq;
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (int i = 0; i < size; i++) {
            result += tasks[i].hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ArrayTaskList{");
        sb.append("size=").append(size);
        sb.append(", type=").append(type);
        sb.append(", tasks=");
        for (int i = 0; i < size; i++) {
            sb.append(tasks[i]);
            if (i != size - 1) sb.append(", ");
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public ArrayTaskList clone() {
        ArrayTaskList clone = new ArrayTaskList();
        for (int i = 0; i < size; i++) {
            clone.add(this.tasks[i].clone());
        }
        return clone;
    }
}