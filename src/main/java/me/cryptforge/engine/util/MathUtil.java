package me.cryptforge.engine.util;

public final class MathUtil {

    private MathUtil() {}

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

}
