package gaia3d.support;


import gaia3d.domain.MaskingType;
import static gaia3d.domain.MaskingType.*;

public class MaskingSupport {

    private static final String MASKING_STRING = "**********";

    public static String getMasking(String value) {
        if(value == null || "".equals(value)) {
            return "";
        }
        return getMasking(value, DEFAULT);
    }

    public static String getMasking(String value, String type) {
        return getMasking(value, MaskingType.valueOf(type));
    }

    public static String getMasking(String value, MaskingType type) {
        if(value == null || "".equals(value)) {
            return "";
        }

        int start = 0;
        int end = 0;
        if(type == DEFAULT) {
            start = value.length() - 1;
            end = value.length();
        } else if(type == PHONE) {
            start = value.length() - 1;
            end = value.length();
        } else if(type == EMAIL) {
            String[] data = value.split("@");
            start = data[0].length() - 1;
            end = data[0].length();
        }

        return getMasking(value, type, start, end , 1);
    }

    /**
     * 기본값은 끝에 한자리 * 처리
     * @param value
     * @param type 데이터 타입
     * @param end 마스킹 종료 위치
     * @param start	마스킹 시작 위치
     * @param maskingLength 마스킹(*) 갯수
     * @return
     */
    public static String getMasking(String value, MaskingType type, int start, int end, int maskingLength) {

        if(value == null || "".equals(value)) {
            return "";
        }

        if(end == value.length()) {
            value = value.substring(0, start) + MASKING_STRING.substring(0, maskingLength);
        } else {
            value = value.substring(0, start) + MASKING_STRING.substring(0, maskingLength) + value.substring(end);
        }

        return value;
    }
}
