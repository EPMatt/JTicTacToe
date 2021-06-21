package common;

public class Utils {
    public static byte[] toByteArray(int n) {
        return new byte[]{(byte) (n >>> 24), (byte) (n >>> 16), (byte) (n >>> 8), (byte) n};
    }

    public static int byteArrayToInt(byte[] b) {
        return (b[0] << 24)
                | (b[1] << 16)
                | (b[2] << 8)
                | (b[3] << 0);
    }
}
