package com.mcp.comms.memory;

import java.util.ArrayList;
import java.util.List;

public class MemoryUtils {

    public static List<Double> generateEmbedding(String text) {
        List<Double> embedding = new ArrayList<>();
        for (int i = 0; i < 128; i++) {
            embedding.add((double) text.hashCode() % 100 / 100.0);
        }
        return embedding;
    }

    public static List<Double> convertFloatListToDouble(List<Float> floats) {
        List<Double> doubles = new ArrayList<>();
        for (Float f : floats) {
            doubles.add(f.doubleValue());
        }
        return doubles;
    }
}
