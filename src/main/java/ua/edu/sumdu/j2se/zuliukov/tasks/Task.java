package ua.edu.sumdu.j2se.zuliukov.tasks;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Cloneable, Serializable {
    private String title;
    private LocalDateTime time;
    private LocalDateTime start;
    private LocalDateTime end;
    private int interval = 0;
    private boolean active = false;
    private boolean repeated = false;

    public Task() {
    }

    public Task(String title, LocalDateTime time) throws IllegalArgumentException {
        this.title = title;
        if (time == null) throw new IllegalArgumentException("Time cannot be null");
        else this.time = time;
    }

    public Task(String title, LocalDateTime start, LocalDateTime end, int interval) throws IllegalArgumentException {
        this.title = title;
        if (start.isAfter(end)) throw new IllegalArgumentException("Start cannot be after End");
        else {
            this.start = start;
            this.end = end;
        }
        if (interval <= 0) throw new IllegalArgumentException("Interval must be greater than 0");
        else this.interval = interval;
        this.repeated = true;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getTime() {
        if (repeated) return start;
        else return time;
    }

    public void setTime(LocalDateTime time) throws IllegalArgumentException {
        if (time == null) throw new IllegalArgumentException("Time cannot be null");
        else this.time = time;
        this.repeated = false;
    }

    public LocalDateTime getStartTime() {
        if (repeated) return start;
        else return time;
    }

    public LocalDateTime getEndTime() {
        if (repeated) return end;
        else return time;
    }

    public int getRepeatInterval() {
        if (repeated) return interval;
        else return 0;
    }

    public void setTime(LocalDateTime start, LocalDateTime end, int interval) throws IllegalArgumentException {
        if (start.isAfter(end)) throw new IllegalArgumentException("Start cannot be after End");
        else {
            this.start = start;
            this.end = end;
        }
        if (interval <= 0) throw new IllegalArgumentException("Interval must be greater than 0");
        else this.interval = interval;
        this.repeated = true;
    }

    public boolean isRepeated() {
        return repeated;
    }

    public LocalDateTime nextTimeAfter(LocalDateTime current) {
        if (active) {
            if (repeated) {
                if (current.isBefore(start)) return start;
                else {
                    LocalDateTime nextTime = start.plusSeconds(interval);
                    while (nextTime.compareTo(end)<=0) {
                        if (current.isBefore(nextTime)) {
                            return nextTime;
                        }
                        nextTime = nextTime.plusSeconds(interval);
                    }
                    return null;
                }
            }
            else if (current.isBefore(time)) return time;
            else return null;
        }
        else return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        if (interval != task.interval) return false;
        if (active != task.active) return false;
        if (repeated != task.repeated) return false;
        if (!Objects.equals(title, task.title)) return false;
        if (!Objects.equals(time, task.time)) return false;
        if (!Objects.equals(start, task.start)) return false;
        return Objects.equals(end, task.end);
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + interval;
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + (repeated ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Task{");
        sb.append("title='").append(title).append('\'');
        sb.append(", time=").append(time);
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", interval=").append(interval);
        sb.append(", active=").append(active);
        sb.append(", repeated=").append(repeated);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public Task clone() {
        try {
            return (Task) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
