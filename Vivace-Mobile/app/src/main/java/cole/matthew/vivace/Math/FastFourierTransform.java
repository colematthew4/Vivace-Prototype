package cole.matthew.vivace.Math;

import cole.matthew.vivace.Helpers.ArrayUtils;

public class FastFourierTransform
{
    /**
     * {@code _SUB_NUM_REAL[i]} is the real part of
     * {@code exp(- 2 * i * pi / n)}:
     * {@code _SUB_NUM_REAL[i] = cos(2 * pi/ n)}, where {@code n = 2^i}.
     */
    private static final double[] _SUB_NUM_REAL =
            {  0x1.0p0, -0x1.0p0, 0x1.1a62633145c07p-54, 0x1.6a09e667f3bcdp-1
                    , 0x1.d906bcf328d46p-1, 0x1.f6297cff75cbp-1, 0x1.fd88da3d12526p-1, 0x1.ff621e3796d7ep-1
                    , 0x1.ffd886084cd0dp-1, 0x1.fff62169b92dbp-1, 0x1.fffd8858e8a92p-1, 0x1.ffff621621d02p-1
                    , 0x1.ffffd88586ee6p-1, 0x1.fffff62161a34p-1, 0x1.fffffd8858675p-1, 0x1.ffffff621619cp-1
                    , 0x1.ffffffd885867p-1, 0x1.fffffff62161ap-1, 0x1.fffffffd88586p-1, 0x1.ffffffff62162p-1
                    , 0x1.ffffffffd8858p-1, 0x1.fffffffff6216p-1, 0x1.fffffffffd886p-1, 0x1.ffffffffff621p-1
                    , 0x1.ffffffffffd88p-1, 0x1.fffffffffff62p-1, 0x1.fffffffffffd9p-1, 0x1.ffffffffffff6p-1
                    , 0x1.ffffffffffffep-1, 0x1.fffffffffffffp-1, 0x1.0p0, 0x1.0p0
                    , 0x1.0p0, 0x1.0p0, 0x1.0p0, 0x1.0p0
                    , 0x1.0p0, 0x1.0p0, 0x1.0p0, 0x1.0p0
                    , 0x1.0p0, 0x1.0p0, 0x1.0p0, 0x1.0p0
                    , 0x1.0p0, 0x1.0p0, 0x1.0p0, 0x1.0p0
                    , 0x1.0p0, 0x1.0p0, 0x1.0p0, 0x1.0p0
                    , 0x1.0p0, 0x1.0p0, 0x1.0p0, 0x1.0p0
                    , 0x1.0p0, 0x1.0p0, 0x1.0p0, 0x1.0p0
                    , 0x1.0p0, 0x1.0p0, 0x1.0p0 };

    /**
     * {@code _SUB_NUM_IMAG[i]} is the imaginary part of
     * {@code exp(- 2 * i * pi / n)}:
     * {@code _SUB_NUM_IMAG[i] = -sin(2 * pi / n)}, where {@code n = 2^i}.
     */
    private static final double[] _SUB_NUM_IMAG =
            {  0x1.1a62633145c07p-52, -0x1.1a62633145c07p-53, -0x1.0p0, -0x1.6a09e667f3bccp-1
                    , -0x1.87de2a6aea963p-2, -0x1.8f8b83c69a60ap-3, -0x1.917a6bc29b42cp-4, -0x1.91f65f10dd814p-5
                    , -0x1.92155f7a3667ep-6, -0x1.921d1fcdec784p-7, -0x1.921f0fe670071p-8, -0x1.921f8becca4bap-9
                    , -0x1.921faaee6472dp-10, -0x1.921fb2aecb36p-11, -0x1.921fb49ee4ea6p-12, -0x1.921fb51aeb57bp-13
                    , -0x1.921fb539ecf31p-14, -0x1.921fb541ad59ep-15, -0x1.921fb5439d73ap-16, -0x1.921fb544197ap-17
                    , -0x1.921fb544387bap-18, -0x1.921fb544403c1p-19, -0x1.921fb544422c2p-20, -0x1.921fb54442a83p-21
                    , -0x1.921fb54442c73p-22, -0x1.921fb54442cefp-23, -0x1.921fb54442d0ep-24, -0x1.921fb54442d15p-25
                    , -0x1.921fb54442d17p-26, -0x1.921fb54442d18p-27, -0x1.921fb54442d18p-28, -0x1.921fb54442d18p-29
                    , -0x1.921fb54442d18p-30, -0x1.921fb54442d18p-31, -0x1.921fb54442d18p-32, -0x1.921fb54442d18p-33
                    , -0x1.921fb54442d18p-34, -0x1.921fb54442d18p-35, -0x1.921fb54442d18p-36, -0x1.921fb54442d18p-37
                    , -0x1.921fb54442d18p-38, -0x1.921fb54442d18p-39, -0x1.921fb54442d18p-40, -0x1.921fb54442d18p-41
                    , -0x1.921fb54442d18p-42, -0x1.921fb54442d18p-43, -0x1.921fb54442d18p-44, -0x1.921fb54442d18p-45
                    , -0x1.921fb54442d18p-46, -0x1.921fb54442d18p-47, -0x1.921fb54442d18p-48, -0x1.921fb54442d18p-49
                    , -0x1.921fb54442d18p-50, -0x1.921fb54442d18p-51, -0x1.921fb54442d18p-52, -0x1.921fb54442d18p-53
                    , -0x1.921fb54442d18p-54, -0x1.921fb54442d18p-55, -0x1.921fb54442d18p-56, -0x1.921fb54442d18p-57
                    , -0x1.921fb54442d18p-58, -0x1.921fb54442d18p-59, -0x1.921fb54442d18p-60 };

    /** The type of DFT to be performed. */
    private final DFTNormalization _normalization;

    /**
     * Creates a new instance with the specified normalization technique.
     *
     * @param normalization the normalization technique to apply
     */
    public FastFourierTransform(final DFTNormalization normalization)
    {
        _normalization = normalization;
    }

    /**
     * Returns the transform of the audio data sample.
     *
     * @param audio The audio data to be transformed
     * @param transformType The type of the transform (forward or inverse) to be performed
     * @return An array of transformed complex numbers
     * @throws AssertionError When there isn't two lines of audio data, or the amount of data is not
     * the same in both lines
     * @throws IllegalArgumentException If the amount of data isn't a power of two
     */
    public ComplexNumber[] transform(final double[] audio, final TransformType transformType)
            throws AssertionError, IllegalArgumentException
    {
        final double[][] audioData = new double[][] {
                ArrayUtils.copy(audio, audio.length),
                new double[audio.length]
        };

        transform(audioData, _normalization, transformType);
        return ComplexNumber.toArray(audioData);
    }

    /**
     * Computes the standard transform of the complex data in place. The input data is formatted as:
     * <ul>
     *   <li>
     *     {@code audioData[0][i]} is the real part of the {@code i}-th data point,
     *   </li>
     *   <li>
     *     {@code audioData[1][i]} is the imaginary part of the {@code i}-th data point.
     *   </li>
     * </ul>
     *
     * @param audioData The audio data to be transformed
     * @param normalization The normalization to be applied to the transformed data
     * @param transformType The type of the transform (forward or inverse) to be performed
     * @throws AssertionError When there isn't two lines of audio data, or the amount of data is not
     * the same in both lines
     * @throws IllegalArgumentException If the amount of data isn't a power of two
     */
    private static void transform(final double[][] audioData, final DFTNormalization normalization,
                                  final TransformType transformType)
            throws AssertionError, IllegalArgumentException
    {
        if (audioData.length != 2)
            throw new AssertionError("Not correct amount of data.");

        final double[] realData = audioData[0];
        final double[] imagData = audioData[1];
        if (realData.length != imagData.length)
            throw new AssertionError("Uneven amount of audio data.");

        if (!ArrayUtils.isPowerOfTwo(realData.length))
            throw new IllegalArgumentException("Audio data is not in a power of two");

        if (realData.length != 1)
        {
            if (realData.length == 2)
            {
                final double srcReal_1 = realData[0];
                final double srcImag_1 = imagData[0];
                final double srcReal_2 = realData[1];
                final double srcImag_2 = imagData[1];

                realData[0] = srcReal_1 + srcReal_2;
                imagData[0] = srcImag_1 + srcImag_2;
                realData[1] = srcReal_1 - srcReal_2;
                imagData[1] = srcImag_1 - srcImag_2;

                normalize(audioData, normalization, transformType);
            }
            else
            {
                bitReverse(realData, imagData);

                if (transformType == TransformType.INVERSE)
                {
                    for (int i = 0; i < realData.length; i += 4)
                    {
                        final int index_1 = i + 1;
                        final int index_2 = i + 2;
                        final int index_3 = i + 3;
                        final double srcReal_1 = realData[i];
                        final double srcImag_1 = imagData[i];
                        final double srcReal_2 = realData[index_1];
                        final double srcImag_2 = imagData[index_1];
                        final double srcReal_3 = realData[index_2];
                        final double srcImag_3 = imagData[index_2];
                        final double srcReal_4 = realData[index_3];
                        final double srcImag_4 = imagData[index_3];

                        // 4-term DFT
                        realData[i] = srcReal_1 + srcReal_2 + srcReal_3 + srcReal_4;
                        imagData[i] = srcImag_1 + srcImag_2 + srcImag_3 + srcImag_4;
                        realData[index_1] = srcReal_1 - srcReal_3 + (srcReal_4 - srcReal_2);
                        imagData[index_1] = srcImag_1 - srcImag_3 + (srcImag_2 - srcImag_4);
                        realData[i] = srcReal_1 - srcReal_2 + srcReal_3 - srcReal_4;
                        imagData[i] = srcImag_1 - srcImag_2 + srcImag_3 - srcImag_4;
                        realData[i] = srcReal_1 - srcReal_3 + (srcReal_2 - srcReal_4);
                        imagData[i] = srcImag_1 - srcImag_3 + (srcImag_4 - srcImag_2);
                    }
                }
                else
                {
                    for (int i = 0; i < realData.length; i += 4) {
                        final int index_1 = i + 1;
                        final int index_2 = i + 2;
                        final int index_3 = i + 3;

                        final double srcReal_1 = realData[i];
                        final double srcImag_1 = imagData[i];
                        final double srcReal_2 = realData[index_2];
                        final double srcImag_2 = imagData[index_2];
                        final double srcReal_3 = realData[index_1];
                        final double srcImag_3 = imagData[index_1];
                        final double srcReal_4 = realData[index_3];
                        final double srcImag_4 = imagData[index_3];

                        // 4-term DFT
                        realData[i] = srcReal_1 + srcReal_2 + srcReal_3 + srcReal_4;
                        imagData[i] = srcImag_1 + srcImag_2 + srcImag_3 + srcImag_4;
                        realData[index_1] = srcReal_1 - srcReal_3 + (srcImag_2 - srcImag_4);
                        imagData[index_1] = srcImag_1 - srcImag_3 + (srcReal_4 - srcReal_2);
                        realData[index_2] = srcReal_1 - srcReal_2 + srcReal_3 - srcReal_4;
                        imagData[index_2] = srcImag_1 - srcImag_2 + srcImag_3 - srcImag_4;
                        realData[index_3] = srcReal_1 - srcReal_3 + (srcImag_4 - srcImag_2);
                        imagData[index_3] = srcImag_1 - srcImag_3 + (srcReal_2 - srcReal_4);
                    }
                }

                int lastNum = 4;
                int lastLogNum = 2;
                while (lastNum < realData.length)
                {
                    int n = lastNum << 1;
                    int logN = lastLogNum + 1;
                    double subRealNum = _SUB_NUM_REAL[logN];
                    double subImagNum = _SUB_NUM_IMAG[logN];
                    if (transformType == TransformType.INVERSE)
                        subImagNum = -subImagNum;

                    // Combine even/odd transforms of size lastNum into a transform of size n (lastNum * 2)
                    for (int evenStartIndex = 0; evenStartIndex < realData.length; evenStartIndex += n)
                    {
                        int oddStartIndex = evenStartIndex + lastNum;
                        double subRealNumToReal = 1;
                        double subRealNumToImag = 0;

                        for (int i = 0; i < lastNum; ++i)
                        {
                            double grReal = realData[evenStartIndex + i];
                            double grImag = imagData[evenStartIndex + i];
                            double hrReal = realData[oddStartIndex + i];
                            double hrImag = imagData[oddStartIndex + i];

                            realData[evenStartIndex + i] = grReal + subRealNumToReal * hrReal - subRealNumToImag * hrImag;
                            imagData[evenStartIndex + i] = grImag + subRealNumToReal * hrImag + subRealNumToImag * hrReal;
                            realData[oddStartIndex + i] = grReal - (subRealNumToReal * hrReal - subRealNumToImag * hrImag);
                            imagData[oddStartIndex + i] = grImag - (subRealNumToReal * hrImag + subRealNumToImag * hrReal);

                            double nextSubRealNumToReal = subRealNumToReal * subRealNum - subRealNumToImag * subImagNum;
                            double nextSubRealNumToImag = subRealNumToReal * subImagNum + subRealNumToImag * subRealNum;
                            subRealNumToReal = nextSubRealNumToReal;
                            subRealNumToImag = nextSubRealNumToImag;
                        }
                    }

                    lastNum = n;
                    lastLogNum = logN;
                }
            }

            normalize(audioData, normalization, transformType);
        }
    }

    /**
     * Performs an identical index bit reversal shuffle on the real and imaginary data. Each element
     * in the array is swapped with another elements based on the bit-reversal of the index.
     *
     * @param realData The array of real numbers
     * @param imagData The array of imaginary numbers
     * @throws AssertionError There is an uneven amount of real and imaginary data
     */
    private static void bitReverse(double[] realData, double[] imagData)
            throws AssertionError
    {
        if (realData.length != imagData.length)
            throw new AssertionError("No equal amount of real and imaginary data.");

        final int halfOfN = realData.length >> 1;

        int j = 0;
        for (int i = 0; i < realData.length; ++i)
        {
            if (i < j)
            {
                //swap indices i & j
                double temp = realData[i];
                realData[i] = realData[j];
                realData[j] = temp;

                temp = imagData[i];
                imagData[i] = imagData[j];
                imagData[j] = temp;
            }

            int k = halfOfN;
            while (k <= j && k > 0)
            {
                j -= k;
                k >>= 1;
            }

            j += k;
        }
    }

    /**
     * Applies the proper normalization to the specified transformed data.
     *
     * @param audioData The audio data to normalize.
     * @param normalization The normalization technique to use.
     * @param transformType The transform type
     * @throws AssertionError when there is not an equal amount of real and imaginary data.
     */
    private static void normalize(double[][] audioData, DFTNormalization normalization, TransformType transformType)
            throws AssertionError
    {
        final double[] realData = audioData[0];
        final double[] imagData = audioData[1];
        if (imagData.length != realData.length)
            throw new AssertionError("No equal amount of real and imaginary data.");

        switch (normalization)
        {
            case STANDARD:
                if (transformType == TransformType.INVERSE)
                {
                    final double scaleFactor = 1.0 / ((double)realData.length);
                    for (int index = 0; index < realData.length; ++index)
                    {
                        realData[index] *= scaleFactor;
                        imagData[index] *= scaleFactor;
                    }
                }

                break;
            case UNITARY:
                final double scaleFactor = 1.0 / Math.sqrt(realData.length);
                for (int index = 0; index < realData.length; ++index)
                {
                    realData[index] *= scaleFactor;
                    imagData[index] *= scaleFactor;
                }

                break;
            default:
                break;
        }
    }
}
