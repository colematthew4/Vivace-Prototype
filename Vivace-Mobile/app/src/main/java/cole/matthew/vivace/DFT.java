package cole.matthew.vivace;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DFT
{
    public static List<Float> process(@NotNull double results[], float sampleRate, int numSamples, int sigma)
    {
        double average = 0;
        for (double result : results)
            average += result;

        average = average / results.length;

        double sums = 0;
        for (double result : results)
            sums += (result - average) * (result - average);

        double stdev = Math.sqrt(sums / (results.length - 1));

        ArrayList<Float> found = new ArrayList<>();
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
                    found.add(maxF * sampleRate / numSamples);
                    max = Integer.MIN_VALUE;
                    maxF = -1;
                }
            }
        }

        return found;
    }
}
