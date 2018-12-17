package cole.matthew.vivace.Math;

public class ComplexNumber
{
    public static final ComplexNumber I = new ComplexNumber(0.0, 1.0);
    public static final ComplexNumber NaN = new ComplexNumber(Double.NaN, Double.NaN);
    public static final ComplexNumber INF = new ComplexNumber(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
    public static final ComplexNumber ONE = new ComplexNumber(1.0, 0.0);
    public static final ComplexNumber ZERO = new ComplexNumber(0.0, 0.0);

    private final double _imaginary;
    private final double _real;
    private final boolean _isNaN;
    private final boolean _isInFinite;

    /**
     * Creates a new instance of a Complex number with real and imaginary parts.
     *
     * @param real The real part
     * @param imaginary the imaginary part
     */
    public ComplexNumber(double real, double imaginary)
    {
        _real = real;
        _imaginary = imaginary;
        _isNaN = Double.isNaN(_real) || Double.isNaN(_imaginary);
        _isInFinite = !_isNaN && (Double.isInfinite(_real) || Double.isInfinite(_imaginary));
    }

    /**
     * Builds an array of {@link ComplexNumber} from a two-dimensional array of audio values.
     *
     * @param audioData The audio data to convert to complex numbers.
     * @return An array of {@link ComplexNumber} values.
     * @throws UnsupportedOperationException if the number of rows of the audio data is not two, or
     * the array is not "rectangular".
     */
    public static ComplexNumber[] toArray(double[][] audioData)
            throws UnsupportedOperationException
    {
        if (audioData.length != 2) {
            throw new UnsupportedOperationException("Not correct amount of data.");
        }

        final double[] realData = audioData[0];
        final double[] imagData = audioData[1];
        if (realData.length != imagData.length)
            throw new UnsupportedOperationException("Uneven amount of audio data.");

        final ComplexNumber[] output = new ComplexNumber[realData.length];
        for (int index = 0; index < realData.length; ++index)
            output[index] = new ComplexNumber(realData[index], imagData[index]);

        return output;
    }

    /**
     * Gets the real part of the complex number.
     * @return The real part of the complex number.
     */
    public double getReal()
    {
        return _real;
    }

    /**
     * Gets the imaginary part of the complex number.
     * @return The imaginary part of the complex number.
     */
    public double getImaginary()
    {
        return _imaginary;
    }

    /**
     * Checks if the complex number is not a number.
     * @return True if not a number, false if it is a number.
     */
    public boolean isNaN()
    {
        return _isNaN;
    }

    /**
     * Checks if the complex number is infinite.
     * @return True if infinity, false if not.
     */
    public boolean isInFinite()
    {
        return _isInFinite;
    }
}
