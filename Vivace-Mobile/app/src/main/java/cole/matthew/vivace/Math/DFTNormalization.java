package cole.matthew.vivace.Math;

/**
 * Defines the various types of normalizations that can be applied to discrete Fourier transforms.
 */
public enum DFTNormalization
{
    /**
     * This normalization convention is defined as:
     * <ul><li>
     *     Forward: y<sub>n</sub> = &sum;<sub>k=0</sub><sup>N-1</sup> x<sub>k</sub> exp(-2&pi;*i*n*k/N),
     *   </li>
     *   <li>
     *     Inverse: x<sub>k</sub> = N<sup>-1</sup>&sum;<sub>n=0</sub><sup>N-1</sup> y<sub>n</sub> exp(2&pi;*i*n*k/N),
     * </li></ul>
     * where N is the size of the audio data sample.
     */
    STANDARD,

    /**
     * This normalization convention is defined as:
     * <ul>
     *     <li>
     *         Forward: y<sub>n</sub> = (1 / &radic;N) &sum;<sub>k=0</sub><sup>N-1</sup> x<sub>k</sub> exp(-2&pi;i n k / N),
     *     </li>
     *     <li>
     *         Inverse: x<sub>k</sub> = (1 / &radic;N) &sum;<sub>n=0</sub><sup>N-1</sup> y<sub>n</sub> exp(2&pi;i n k / N),
     *     </li>
     * </ul>
     */
    UNITARY
}
