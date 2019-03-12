import java.util.Arrays;

/**
 * @author voidChen
 * @date 2019/2/27 18:46
 */
public class ByteUtil {
    /**
     * 将byte转换为一个长度为8的byte数组，数组每个值代表bit
     */
    public static byte[] getBooleanArray(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte)(b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }
    /**
     * 把byte转为字符串的bit
     */
    public static String byteToBit(byte b) {
        return ""
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
    }

    /**
     * int到byte[]
     * @param i
     * @return
     */
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        //由高位到低位
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }

    public static void main(String[] args) {
//        test();
        int a = 255;
        byte[] b = intToByteArray(a);

        for(byte i : b)
            System.out.println(byteToBit(i));
//            System.out.println(Integer.toBinaryString(i));

        a = a >> 8;
        b = intToByteArray(a);

        for(byte i : b)
            System.out.println(byteToBit(i));

        System.out.println(b[3]);
    }


    public static void test(){
        byte b = 0x35; // 0011 0101
        // 输出 [0, 0, 1, 1, 0, 1, 0, 1]
        System.out.println(Arrays.toString(getBooleanArray(b)));
        // 输出 00110101
        System.out.println(byteToBit(b));
        // JDK自带的方法，会忽略前面的 0
        System.out.println(Integer.toBinaryString(0x35));
    }

}
