package cole.matthew.vivace.Helpers;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class DiscreteFourierTransform
{
    /** A list of human-readable musical notes. */
    private static String[] notes = { "A", "Bb", "B", "C", "C#", "D", "Eb", "E", "F", "F#", "G", "G#" };

    /**
     * Processes which musical pitches have been picked up by the microphone.
     *
     * @param results The array of frequencies received by the microphone.
     * @param sampleRate The sample rate of the microphone in Hertz.
     * @param numSamples The number of samples received by the microphone.
     * @param sigma A value which helps filter background pitches picked up by the microphone.
     * @return A HashMap of notes and their frequencies picked up by the microphone
     */
    public static HashMap<String, Float> processAudio(@NotNull double results[], float sampleRate, int numSamples, int sigma)
    {
        double average = 0;
        for (double result : results)
            average += result;

        average = average / results.length;

        double sums = 0;
        for (double result : results)
            sums += (result - average) * (result - average);

        double stdev = Math.sqrt(sums / (results.length - 1));

        HashMap<String, Float> found = new HashMap<>();
        double max = Integer.MIN_VALUE;
        int maxF = -1;
        for (int f = 0; f < results.length / 2; f++)
        {
            if (results[f] > average + sigma * stdev)
            {
                if (results[f] > max)
                {
                    max = results[f];
                    maxF = f;
                }
            }
            else
            {
                if (maxF != -1)
                {
                    float freq = maxF * sampleRate / numSamples;
                    String closestKey = closestKey(freq);
                    if (closestKey != null)
                        found.put(closestKey, freq);

                    max = Integer.MIN_VALUE;
                    maxF = -1;
                }
            }
        }

        return found;
    }

    /**
     * Converts the frequency into a human-readable string representing the note on a piano.
     *
     * @param freq The frequency to convert to a string
     * @return A string representing the note on a piano.
     */
    private static String closestKey(double freq)
    {
        String result = null;
        int key = closestKeyIndex(freq);

        if (key > 0)
        {
            int range = 1 + (key - 1) / notes.length;
            result = notes[(key - 1) % notes.length] + range;
        }

        return result;
    }

    /**
     * Takes a frequency and returns the corresponding key number on a piano in the range 1-88. This
     * formula is derived from the logarithmic nature of the frequency.
     *
     * @param freq The frequency to get the key number for.
     * @return A number between 1 and 88 identifying the key number of the note.
     */
    private static int closestKeyIndex(double freq)
    {
        return 1 + (int)((12 * Math.log(freq / 440) / Math.log(2) + 49) - 0.5);
    }
}
