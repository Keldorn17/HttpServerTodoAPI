package com.keldorn.util.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.keldorn.domain.enums.Priority;

import java.io.IOException;

public class PriorityAdapter extends TypeAdapter<Priority> {
    @Override
    public void write(JsonWriter jsonWriter, Priority priority) throws IOException { // serialize
        if (priority == null) {
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.value(priority.ordinal());
    }

    @Override
    public Priority read(JsonReader jsonReader) throws IOException { // deserialize
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }
        int ordinal = jsonReader.nextInt();
        return Priority.values()[ordinal];
    }
}
