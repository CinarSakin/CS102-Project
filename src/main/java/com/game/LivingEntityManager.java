package com.game;

import java.util.ArrayList;
import java.util.List;

public class LivingEntityManager {

    private static final List<LivingEntity> entities = new ArrayList<>();

    public static void register(LivingEntity e) {
        entities.add(e);
    }

    public static void unregister(LivingEntity e) {
        entities.remove(e);
    }

    public static List<LivingEntity> getLivingEntities() {
        return entities;
    }

    public static void clearAll() {
        entities.clear();
    }
}