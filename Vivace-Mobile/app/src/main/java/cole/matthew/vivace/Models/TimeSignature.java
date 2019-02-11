package cole.matthew.vivace.Models;

import android.support.annotation.NonNull;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.jetbrains.annotations.NotNull;

public class TimeSignature {
    private int _beatsPerMeasure;
    private int _beatUnit;

    /**
     * Constructs a TimeSignature with the given beats per measure and beat unit.
     *
     * @param beats    How many beats constitute one measure.
     * @param beatUnit The note value that represents one beat.
     *
     * @exception MathIllegalArgumentException beatUnit isn't a power of 2.
     */
    public TimeSignature(int beats, int beatUnit)
            throws MathIllegalArgumentException
    {
        setTimeSignature(beats, beatUnit);
    }

    /**
     * Constructs a TimeSignature with the given beats per measure and beat unit.
     *
     * @param timeSignature A string representation of the time signature. Must be in the form "%d/%d".
     *
     * @exception IllegalArgumentException The string passed in does not match the format required of a time signature.
     */
    public TimeSignature(@NotNull String timeSignature)
            throws IllegalArgumentException
    {
        if (!timeSignature.matches("\\d/\\d")) {
            throw new IllegalArgumentException(timeSignature + " does not represent a valid time signature.");
        }

        try {
            int divide = timeSignature.indexOf('/');
            int beats = Integer.valueOf(timeSignature.substring(0, divide));
            int beatUnit = Integer.valueOf(timeSignature.substring(divide + 1));
            setTimeSignature(beats, beatUnit);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            // TODO: log error?
            throw new IllegalArgumentException(timeSignature + " does not represent a valid time signature.");
        }
    }

    /**
     * Sets the values of the time signature.
     *
     * @param beats    How many beats constitute one measure.
     * @param beatUnit The note value that represents one beat.
     *
     * @exception MathIllegalArgumentException beatUnit isn't a power of 2.
     */
    private void setTimeSignature(int beats, int beatUnit)
            throws MathIllegalArgumentException
    {
        if (!ArithmeticUtils.isPowerOfTwo(beatUnit)) {
            throw new MathIllegalArgumentException(LocalizedFormats.NOT_POWER_OF_TWO_CONSIDER_PADDING, beatUnit);
        }

        _beatsPerMeasure = beats;
        _beatUnit = beatUnit;
    }

    /** Gets how many beats constitute one measure. */
    public int getBeatsPerMeasure() {
        return _beatsPerMeasure;
    }

    /** Gets the note value that represents one beat. */
    public int getBeatUnit() {
        return _beatUnit;
    }

    /** Gets a string representation of the time signature. */
    @NonNull
    public String toString() {
        return _beatsPerMeasure + "/" + _beatUnit;
    }
}
