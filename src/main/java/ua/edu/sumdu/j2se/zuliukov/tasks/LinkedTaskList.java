package ua.edu.sumdu.j2se.zuliukov.tasks;

public class LinkedTaskList extends AbstractTaskList{
    class Node{
        Task data;
        Node next;

        public Node(Task data) {
            this.data = data;
            this.next = null;
        }
    }
    private Node head = null;
    //private Node tail = null;

    public void add(Task task) throws IllegalArgumentException {
        if(task==null)throw new IllegalArgumentException("Task cannot be null");
        else {
            Node newNode = new Node(task);
            if (head == null) head = newNode;
            //else tail.next = newNode;
            Node iter = head;
            for (int i = 0; i < size - 1; i++) {
                iter = iter.next;
            }
            iter.next = newNode;
            //tail = newNode;
            size++;
        }
    }

    public boolean remove(Task task){
        if(head.data==task){
            head=head.next;
            size--;
            return true;
        }
        Node previous = head;
        while(previous.next!=null){
            if(previous.next.data==task){
                //if(previous.next.next==null)tail=previous;
                previous.next=previous.next.next;
                size--;
                //if(previous.next==null)tail=previous;
                return true;
            }
            previous=previous.next;
        }
        return false;
    }

    public int size(){
        return size;
    }

    public Task getTask(int index) throws IndexOutOfBoundsException {
        if(index<0||index>size-1)throw new IndexOutOfBoundsException();
        else {
            if(index==0)return head.data;
            //if(index==size-1)return tail.data;
            Node iter = head;
            for (int i = 0; i < index; i++) {
                iter=iter.next;
            }
            return iter.data;
        }
    }

    public LinkedTaskList() {
        super(0,ListTypes.types.LINKED);
    }
}
