package gaia3d.weather;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ucar.ma2.Array;
import ucar.ma2.DataType;

public class ArrayTest {

    private Array array;

    @BeforeEach
    void setUp() {
        int row = 5, column = 8;
        array = Array.factory(DataType.FLOAT, new int[]{row, column});
        for (int i = 0; i < row * column; i++) {
            array.setFloat(i, (i + 1));
        }
    }

    @Test
    @DisplayName("원본배열")
    void origin() {
        float[][] dataArr = (float[][]) array.copyToNDJavaArray();
        print(dataArr);
    }

    @Test
    @DisplayName("Row 뒤집기 - Y축으로 회전")
    void flipRow() {
        array = array.flip(0);
        float[][] dataArr = (float[][]) array.copyToNDJavaArray();
        print(dataArr);
    }

    @Test
    @DisplayName("Column 뒤집기 - X축으로 회전")
    void flipColumn() {
        array = array.flip(1);
        float[][] dataArr = (float[][]) array.copyToNDJavaArray();
        print(dataArr);
    }

    private void print(float[][] dataArr) {
        for (int i = 0; i < dataArr.length; i++) {
            for (int j = 0; j < dataArr[i].length; j++) {
                float datum = dataArr[i][j];
                System.out.print(datum + " ");
            }
            System.out.println();
        }
    }

}
