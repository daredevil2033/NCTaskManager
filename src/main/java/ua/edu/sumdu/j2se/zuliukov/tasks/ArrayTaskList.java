package ua.edu.sumdu.j2se.zuliukov.tasks;

public class ArrayTaskList extends AbstractTaskList{
    private Task[] tasks;

    public void add(Task task) throws IllegalArgumentException {
        if(task==null)throw new IllegalArgumentException("Task cannot be null");
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

    public boolean remove(Task task){
        for (int i = 0; i < size; i++) {
            if(tasks[i].equals(task)){
                if(size==1){
                    tasks=null;
                    size--;
                }
                else{
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
        if(index<0||index>size-1)throw new IndexOutOfBoundsException();
        else return tasks[index];
    }

    public ArrayTaskList() {
        super(0,ListTypes.types.ARRAY);
    }
}
