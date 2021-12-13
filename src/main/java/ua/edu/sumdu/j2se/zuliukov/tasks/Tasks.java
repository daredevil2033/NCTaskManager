package ua.edu.sumdu.j2se.zuliukov.tasks;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.StreamSupport;

public class Tasks {
    public static Iterable<Task> incoming(Iterable<Task> tasks, LocalDateTime start, LocalDateTime end) throws InstantiationException, IllegalAccessException {
        Iterable<Task> taskIterable = tasks.getClass().newInstance();
        if (taskIterable instanceof AbstractTaskList) {
            StreamSupport.stream(tasks.spliterator(), false)
                    .filter(task -> (task.nextTimeAfter(start) != null && task.nextTimeAfter(start).compareTo(end) <= 0))
                    .forEach(((AbstractTaskList)taskIterable)::add);
        } else {
            StreamSupport.stream(tasks.spliterator(), false)
                    .filter(task -> (task.nextTimeAfter(start) != null && task.nextTimeAfter(start).compareTo(end) <= 0))
                    .forEach(((Collection<Task>)taskIterable)::add);
        }
        return taskIterable;
    }
    public static SortedMap<LocalDateTime, Set<Task>> calendar(Iterable<Task> tasks, LocalDateTime start, LocalDateTime end) throws InstantiationException, IllegalAccessException {
        Iterable<Task> taskIterable = incoming(tasks,start,end);
        SortedMap<LocalDateTime, Set<Task>> dateTimeSetSortedMap = new TreeMap<>();
        Set<Task> taskSet;
        for(Task t:taskIterable){
            if(t.isRepeated()){
                LocalDateTime nextTime = t.nextTimeAfter(start);
                while (nextTime.compareTo(end)<=0) {
                    taskSet = new HashSet<>();
                    if(dateTimeSetSortedMap.containsKey(nextTime)){
                        taskSet = dateTimeSetSortedMap.get(nextTime);
                    }
                    taskSet.add(t);
                    dateTimeSetSortedMap.put(nextTime,taskSet);
                    nextTime = nextTime.plusSeconds(t.getRepeatInterval());
                }
            }
            else{
                taskSet = new HashSet<>();
                if(dateTimeSetSortedMap.containsKey(t.getTime())){
                    taskSet = dateTimeSetSortedMap.get(t.getTime());
                }
                taskSet.add(t);
                dateTimeSetSortedMap.put(t.getTime(),taskSet);
            }
        }
        return dateTimeSetSortedMap;
    }
}
