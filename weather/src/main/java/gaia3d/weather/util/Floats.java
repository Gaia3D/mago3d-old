package gaia3d.weather.util;

public class Floats {

    public static float min(float... array) {
        if (array.length <= 0) return Float.NaN;
        float min = array[0];
        for (int i = 1; i < array.length; i++) {
            min = Math.min(min, array[i]);
        }
        return min;
    }

    public static float max(float... array) {
        if (array.length <= 0) return Float.NaN;
        float max = array[0];
        for (int i = 1; i < array.length; i++) {
            max = Math.max(max, array[i]);
        }
        return max;
    }

    public static float min(float[][] array) {
        if (array.length <= 0) return Float.NaN;
        float min = array[0][0];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                min = Math.min(min, array[i][j]);
            }
        }
        return min;
    }

    public static float max(float[][] array) {
        if (array.length <= 0) return Float.NaN;
        float max = array[0][0];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                max = Math.max(max, array[i][j]);
            }
        }
        return max;
    }

}
