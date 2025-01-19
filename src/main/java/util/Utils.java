package util;

public class Utils {
    public static String byteToString(byte value){
        StringBuilder binary = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            binary.append((value >> i) & 1);
        }
        return binary.toString();
    }
}
