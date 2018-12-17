package cole.matthew.vivace.Helpers;

public class ArrayUtils {
    /**
     * Copies data from the source array to a new array. The amount is the minimum of the length
     * provided or the length of the source array.
     *
     * @param src    The source array
     * @param length The amount of data to copy
     *
     * @return A new array of data.
     */
    public static double[] copy(double[] src, int length) {
        final double[] output = new double[length];
        System.arraycopy(src, 0, output, 0, Math.min(length, src.length));
        return output;
    }

    /**
     * Determines if an array's length is a power of two.
     *
     * @param length The length of the array.
     *
     * @return True if the array's length is a power of two, false if not.
     */
    public static boolean isPowerOfTwo(int length) {
        return (length > 0) && ((length & (length - 1)) == 0);
    }
}
