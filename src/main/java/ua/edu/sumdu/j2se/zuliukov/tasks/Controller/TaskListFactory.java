package ua.edu.sumdu.j2se.zuliukov.tasks.Controller;

import ua.edu.sumdu.j2se.zuliukov.tasks.Model.AbstractTaskList;
import ua.edu.sumdu.j2se.zuliukov.tasks.Model.ArrayTaskList;
import ua.edu.sumdu.j2se.zuliukov.tasks.Model.LinkedTaskList;
import ua.edu.sumdu.j2se.zuliukov.tasks.Model.ListTypes;

public class TaskListFactory {
    public static AbstractTaskList createTaskList(ListTypes.types type) throws IllegalArgumentException {
        switch (type) {
            case ARRAY:
                return new ArrayTaskList();
            case LINKED:
                return new LinkedTaskList();
            default:
                throw new IllegalArgumentException("Missing type");
        }
    }
}
