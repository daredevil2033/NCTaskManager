package ua.edu.sumdu.j2se.zuliukov.tasks;

public class Task {
    private String title;
    private int time;
    private int start;
    private int end;
    private int interval;
    private boolean active;
    private boolean repeated;

    public Task(String title, int time) {
        this.title = title;
        if(time<0)throw new IllegalArgumentException("Time elapsed cannot be less than 0");
        else this.time = time;
        this.active = false;
        this.repeated = false;
    }

    public Task(String title, int start, int end, int interval) {
        this.title = title;
        if(start<0)throw new IllegalArgumentException("Start cannot be less than 0");
        else if(end<=0)throw new IllegalArgumentException("End must be greater than 0");
        else if(start>end)throw new IllegalArgumentException("Start cannot be after End");
        else{
            this.start = start;
            this.end = end;
        }
        if(interval<=0)throw new IllegalArgumentException("Interval must be greater than 0");
        else this.interval = interval;
        this.active = false;
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

    public int getTime() {
        if(repeated) return start;
        else return time;
    }

    public void setTime(int time) {
        if(time<0)throw new IllegalArgumentException("Time elapsed cannot be less than 0");
        else this.time = time;
        this.repeated = false;
    }

    public int getStartTime() {
        if(repeated) return start;
        else return time;
    }

    public int getEndTime() {
        if(repeated) return end;
        else return time;
    }

    public int getRepeatInterval() {
        if(repeated) return interval;
        else return 0;
    }

    public void setTime(int start, int end, int interval) {
        if(start<0)throw new IllegalArgumentException("Start cannot be less than 0");
        else if(end<=0)throw new IllegalArgumentException("End must be greater than 0");
        else if(start>end)throw new IllegalArgumentException("Start cannot be after End");
        else{
            this.start = start;
            this.end = end;
        }
        if(interval<=0)throw new IllegalArgumentException("Interval must be greater than 0");
        else this.interval = interval;
        this.repeated = true;
    }

    public boolean isRepeated() {
        return repeated;
    }

    public int nextTimeAfter(int current){
        if(active){
            if (repeated) {
                if(current < start)return start;
                else {
                    int nextTime = start + interval;
                    while(nextTime < end){
                        if(current < nextTime){
                            return nextTime;
                        }
                        nextTime+=interval;
                    }
                    return -1;
                }
            }
            else if (current < time) return time;
            else return -1;
        }
        else return -1;
    }
}
