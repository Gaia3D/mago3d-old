package gaia3d.weather.util;

import java.math.BigDecimal;
import java.math.MathContext;

public class WindImageUtil {

    /**
     * float(4byte)값을 R,G 밴드(int[2])로 변경
     * @param value float 값
     * @return 변환된 배열
     */
    public static int[] floatTo2Bands(float value) {
        int[] bands = new int[2];
        BigDecimal bitSize = new BigDecimal(255);
        BigDecimal floatValue = new BigDecimal(value);
        // band0 = value / 255
        BigDecimal band0 = floatValue.divide(bitSize, MathContext.DECIMAL64);
        // band1 = band0 - (band0의 정수 부분) : band0의 소수 부분
        BigDecimal band1 = band0.subtract(new BigDecimal(band0.intValue()), MathContext.DECIMAL64);

        bands[0] = band0.intValue();    // band0의 정수 부분
        bands[1] = band1.multiply(bitSize).intValue();  // (band1 * 255)의 정수 부분
        return bands;
    }

    /**
     * R(255),G(255) 밴드를 float 값으로 변경
     * @param bands R, G 밴드 값
     * @return 실제 float 값
     */
    public static float bandsToFloat(int[] bands) {
        BigDecimal bitSize = new BigDecimal(255);
        // band0 * 255 + band1
        return new BigDecimal(bands[0])
                .multiply(bitSize)
                .add(new BigDecimal(bands[1])).floatValue();
    }

    /**
     * 값과 값의 min, max를 통해 size로 정규화
     * @param min 최소값
     * @param max 최대값
     * @param value 실제 값
     * @param size 정규화 크기
     * @return 정규화 된 값
     */
    public static float normalize(float min, float max, float value, int size) {
        // range = max - min
        BigDecimal range = new BigDecimal(max).subtract(new BigDecimal(min));
        // normalize = (value - min) / (max - min) * size
        return (new BigDecimal(value)
                .subtract(new BigDecimal(min)))
                .divide(range, MathContext.DECIMAL64)
                .multiply(new BigDecimal(size)).floatValue();
    }

    /**
     * min, max, size를 통해 정규화된 값을 실제값으로 변환
     * @param min 최소값
     * @param max 최대값
     * @param value 정규화 값
     * @param size 정규화 크기
     * @return 실제 값
     */
    public static float denormalize(float min, float max, float value, int size) {
        // range = max - min
        BigDecimal range = new BigDecimal(max).subtract(new BigDecimal(min));
        // denormalize = value / size * (max - min) + min
        return new BigDecimal(value)
                .divide(new BigDecimal(size), MathContext.DECIMAL64)
                .multiply(range)
                .add(new BigDecimal(min)).floatValue();
    }

}
