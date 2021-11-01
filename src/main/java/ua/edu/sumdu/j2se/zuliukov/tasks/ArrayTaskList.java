package ua.edu.sumdu.j2se.zuliukov.tasks;

public class ArrayTaskList {
    private Task[] tasks;
    private int size;

    public void add(Task task){
        if(size==0){
            tasks = new Task[++size];
            tasks[size - 1] = task;
        }
        else{
            Task[] temp = tasks;
            tasks = new Task[++size];
            System.arraycopy(temp, 0, tasks, 0, size - 1);
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

    public Task getTask(int index) {
        return tasks[index];
    }

    public ArrayTaskList incoming(int from, int to){
        ArrayTaskList result = new ArrayTaskList();
        for (int i = 0; i < size; i++) {
            if(tasks[i].nextTimeAfter(from)!=-1 && tasks[i].nextTimeAfter(from)<to)result.add(tasks[i]);
        }
        return result;
    }
}
