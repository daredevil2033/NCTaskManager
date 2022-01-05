package ua.edu.sumdu.j2se.zuliukov.tasks.Model;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class LinkedTaskList extends AbstractTaskList implements Cloneable {
    private Node head = null;

    public LinkedTaskList() {
        super(0, ListTypes.types.LINKED);
    }

    public void add(Task task) throws IllegalArgumentException {
        if (task == null) throw new IllegalArgumentException("Task cannot be null");
        else {
            Node newNode = new Node(task);
            if (head == null) head = newNode;
            Node iter = head;
            for (int i = 0; i < size - 1; i++) {
                iter = iter.next;
            }
            iter.next = newNode;
            size++;
        }
    }

    public boolean remove(Task task) {
        if (head.data.equals(task)) {
            head = head.next;
            size--;
            return true;
        }
        Node previous = head;
        while (previous.next != null) {
            if (previous.next.data.equals(task)) {
                previous.next = previous.next.next;
                size--;
                return true;
            }
            previous = previous.next;
        }
        return false;
    }

    public int size() {
        return size;
    }

    public Task getTask(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index > size - 1) throw new IndexOutOfBoundsException();
        else {
            if (index == 0) return head.data;
            Node iter = head;
            for (int i = 0; i < index; i++) {
                iter = iter.next;
            }
            return iter.data;
        }
    }

    @Override
    public Iterator<Task> iterator() {
        return new LinkedTaskListIterator();
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null || getClass() != otherObject.getClass()) return false;
        LinkedTaskList tasks = (LinkedTaskList) otherObject;
        if (tasks.size() != size) return false;
        boolean eq = true;
        Iterator<Task> i = iterator();
        Iterator<Task> io = tasks.iterator();
        while (i.hasNext()) {
            Task t = i.next();
            Task to = io.next();
            if (!to.equals(t)) {
                eq = false;
                break;
            }
        }
        return eq;
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (Task t : this) {
            result += t.hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LinkedTaskList{");
        sb.append("size=").append(size);
        sb.append(", type=").append(type);
        sb.append(", tasks=");
        for (Iterator<Task> i = iterator(); i.hasNext(); ) {
            sb.append(i.next());
            if (i.hasNext()) sb.append(", ");
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public LinkedTaskList clone() {
        LinkedTaskList clone = new LinkedTaskList();
        for (Task task : this) {
            clone.add(task.clone());
        }
        return clone;
    }

    @Override
    public Stream<Task> getStream() {
        return StreamSupport.stream(spliterator(), false);
    }

    class Node {
        Task data;
        Node next;

        public Node(Task data) {
            this.data = data;
            this.next = null;
        }
    }

    class LinkedTaskListIterator implements Iterator<Task> {
        Node last;
        Node next;

        public LinkedTaskListIterator() {
            last = null;
            next = head;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Task next() {
            Task current = next.data;
            if (next != head) {
                if (last != null) {
                    if (last.next != next) last = last.next;
                } else last = head;
            }
            next = next.next;
            return current;
        }

        @Override
        public void remove() {
            if (next == head) throw new IllegalStateException();
            else {
                if (last != null) last.next = next;
                else head = next;
                size--;
            }
        }
    }
}
