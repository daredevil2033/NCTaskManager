package ua.edu.sumdu.j2se.zuliukov.tasks;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class TaskIO {
    private static ZoneOffset zoneOffset;

    public static void write(AbstractTaskList tasks, OutputStream out) throws IOException {
        zoneOffset = ZoneId.systemDefault().getRules().getOffset(Instant.now());
        try (DataOutputStream dataOutputStream = new DataOutputStream(out)) {
            dataOutputStream.writeInt(tasks.size());
            for (Task t : tasks) {
                dataOutputStream.writeInt(t.getTitle().length());
                dataOutputStream.writeUTF(t.getTitle());
                dataOutputStream.writeBoolean(t.isActive());
                dataOutputStream.writeInt(t.getRepeatInterval());
                if (t.isRepeated()) {
                    dataOutputStream.writeLong(t.getStartTime().toEpochSecond(zoneOffset));
                    dataOutputStream.writeInt(t.getStartTime().getNano());
                    dataOutputStream.writeLong(t.getEndTime().toEpochSecond(zoneOffset));
                    dataOutputStream.writeInt(t.getEndTime().getNano());
                } else {
                    dataOutputStream.writeLong(t.getTime().toEpochSecond(zoneOffset));
                    dataOutputStream.writeInt(t.getTime().getNano());
                }
            }
        }
    }

    public static void read(AbstractTaskList tasks, InputStream in) throws IOException {
        zoneOffset = ZoneId.systemDefault().getRules().getOffset(Instant.now());
        try (DataInputStream dataInputStream = new DataInputStream(in)) {
            int size = dataInputStream.readInt();
            int interval;
            Task task;
            for (int i = 0; i < size; i++) {
                task = new Task();
                dataInputStream.readInt();
                task.setTitle(dataInputStream.readUTF());
                task.setActive(dataInputStream.readBoolean());
                interval = dataInputStream.readInt();
                if (interval != 0)
                    task.setTime(LocalDateTime.ofEpochSecond(dataInputStream.readLong(), dataInputStream.readInt(), zoneOffset),
                            LocalDateTime.ofEpochSecond(dataInputStream.readLong(), dataInputStream.readInt(), zoneOffset),
                            interval);
                else
                    task.setTime(LocalDateTime.ofEpochSecond(dataInputStream.readLong(), dataInputStream.readInt(), zoneOffset));
                tasks.add(task);
            }
        }
    }

    public static void writeBinary(AbstractTaskList tasks, File file) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            write(tasks, fileOutputStream);
        }
    }

    public static void readBinary(AbstractTaskList tasks, File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            read(tasks, fileInputStream);
        }
    }

    public static void write(AbstractTaskList tasks, Writer out) throws IOException {
        zoneOffset = ZoneId.systemDefault().getRules().getOffset(Instant.now());
        try (JsonWriter jsonWriter = new JsonWriter(out)) {
            jsonWriter.setIndent("    ");
            jsonWriter.beginArray();
            jsonWriter.beginObject().name("list_size").value(tasks.size()).endObject();
            for (Task t : tasks) {
                jsonWriter.beginObject();
                jsonWriter.name("title_length").value(t.getTitle().length());
                jsonWriter.name("title").value(t.getTitle());
                jsonWriter.name("is_active").value(t.isActive());
                jsonWriter.name("interval").value(t.getRepeatInterval());
                if (t.isRepeated()) {
                    jsonWriter.name("start_time");
                    jsonWriter.beginObject();
                    jsonWriter.name("ISO-8601").value(t.getStartTime().toString());
                    jsonWriter.name("epoch_seconds").value(t.getStartTime().toEpochSecond(zoneOffset));
                    jsonWriter.name("nanoseconds").value(t.getStartTime().getNano());
                    jsonWriter.endObject();
                    jsonWriter.name("end_time");
                    jsonWriter.beginObject();
                    jsonWriter.name("ISO-8601").value(t.getEndTime().toString());
                    jsonWriter.name("epoch_seconds").value(t.getEndTime().toEpochSecond(zoneOffset));
                    jsonWriter.name("nanoseconds").value(t.getEndTime().getNano());
                    jsonWriter.endObject();
                } else {
                    jsonWriter.name("time");
                    jsonWriter.beginObject();
                    jsonWriter.name("ISO-8601").value(t.getTime().toString());
                    jsonWriter.name("epoch_seconds").value(t.getTime().toEpochSecond(zoneOffset));
                    jsonWriter.name("nanoseconds").value(t.getTime().getNano());
                    jsonWriter.endObject();
                }
                jsonWriter.endObject();
            }
            jsonWriter.endArray();
        }
    }

    public static void read(AbstractTaskList tasks, Reader in) throws IOException {
        zoneOffset = ZoneId.systemDefault().getRules().getOffset(Instant.now());
        try (JsonReader jsonReader = new JsonReader(in)) {
            int interval;
            long epoch;
            int nano;
            LocalDateTime start, end;
            jsonReader.beginArray();
            jsonReader.beginObject();
            jsonReader.skipValue();
            int size = jsonReader.nextInt();
            jsonReader.endObject();
            for (int i = 0; i < size; i++) {
                Task task = new Task();
                jsonReader.beginObject();
                jsonReader.skipValue();
                jsonReader.nextInt();
                jsonReader.skipValue();
                task.setTitle(jsonReader.nextString());
                jsonReader.skipValue();
                task.setActive(jsonReader.nextBoolean());
                jsonReader.skipValue();
                interval = jsonReader.nextInt();
                if (interval != 0) {
                    jsonReader.skipValue();
                    jsonReader.beginObject();
                    jsonReader.skipValue();
                    jsonReader.skipValue();
                    jsonReader.skipValue();
                    epoch = jsonReader.nextLong();
                    jsonReader.skipValue();
                    nano = jsonReader.nextInt();
                    start = LocalDateTime.ofEpochSecond(epoch, nano, zoneOffset);
                    jsonReader.endObject();
                    jsonReader.skipValue();
                    jsonReader.beginObject();
                    jsonReader.skipValue();
                    jsonReader.skipValue();
                    jsonReader.skipValue();
                    epoch = jsonReader.nextLong();
                    jsonReader.skipValue();
                    nano = jsonReader.nextInt();
                    end = LocalDateTime.ofEpochSecond(epoch, nano, zoneOffset);
                    jsonReader.endObject();
                    task.setTime(start, end, interval);
                } else {
                    jsonReader.skipValue();
                    jsonReader.beginObject();
                    jsonReader.skipValue();
                    jsonReader.skipValue();
                    jsonReader.skipValue();
                    epoch = jsonReader.nextLong();
                    jsonReader.skipValue();
                    nano = jsonReader.nextInt();
                    task.setTime(LocalDateTime.ofEpochSecond(epoch, nano, zoneOffset));
                    jsonReader.endObject();
                }
                tasks.add(task);
                jsonReader.endObject();
            }
            jsonReader.endArray();
        }
    }

    public static void writeText(AbstractTaskList tasks, File file) throws IOException {
        try (FileWriter fileWriter = new FileWriter(file)) {
            write(tasks, fileWriter);
        }
    }

    public static void readText(AbstractTaskList tasks, File file) throws IOException {
        try (FileReader fileReader = new FileReader(file)) {
            read(tasks, fileReader);
        }
    }
}
