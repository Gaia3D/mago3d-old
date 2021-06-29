package gaia3d.weather.util;

public class Floats {

    public static float min(float... array) {
        //checkArgument(array.length > 0);
        if (array.length <= 0) return Float.NaN;
        float min = array[0];
        for (int i = 1; i < array.length; i++) {
            min = Math.min(min, array[i]);
        }
        return min;
    }

    public static float max(float... array) {
        //checkArgument(array.length > 0);
        if (array.length <= 0) return Float.NaN;
        float max = array[0];
        for (int i = 1; i < array.length; i++) {
            max = Math.max(max, array[i]);
        }
        return max;
    }

}
