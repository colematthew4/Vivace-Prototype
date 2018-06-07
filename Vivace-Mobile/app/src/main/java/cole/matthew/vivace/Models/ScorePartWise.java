package cole.matthew.vivace.Models;

import android.content.Context;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Set;

public class ScorePartWise
{
    public interface OnNewMeasureListener
    {
        void onNewMeasure(Measure measure);
    }

    private OnNewMeasureListener _newMeasureListener;
    private TimeSignature _timeSignature;
    private int _tempo;
    private ArrayList<Measure> _measures;
    private static ScorePartWise ourInstance;

    private ScorePartWise(Context context, TimeSignature timeSignature, int tempo)
    {
        _newMeasureListener = (OnNewMeasureListener)context;
        _timeSignature = timeSignature;
        _tempo = tempo;
        _measures = new ArrayList<>();
    }

    @Contract(pure = true)
    public static ScorePartWise getInstance()
    {
        return ourInstance;
    }

    public static ScorePartWise createInstance(Context context, TimeSignature timeSignature, int tempo)
    {
        if (ourInstance == null)
            ourInstance = new ScorePartWise(context, timeSignature, tempo);

        return ourInstance;
    }

    public void clear()
    {
        _measures.clear();
    }

    public void setTimeSignature(TimeSignature timeSignature)
    {
        _timeSignature = timeSignature;
    }

    public void setTempo(int tempo)
    {
        _tempo = tempo;
    }

    public void addNote(Note note)
    {
        int numMeasures = _measures.size() - 1;

        if (_measures.isEmpty() || _measures.get(numMeasures).getDuration() == _timeSignature.getBeatsPerMeasure())
        {
            if (!_measures.isEmpty())
            {
                _measures.get(numMeasures).fixMeasureDurations();
                _newMeasureListener.onNewMeasure(_measures.get(numMeasures));
            }

            Measure newMeasure = new Measure(_timeSignature);
            newMeasure.addNote(note);
            _measures.add(newMeasure);
        }
        else
        {
            float nextNoteDuration = _measures.get(numMeasures).addNote(note);
            while (nextNoteDuration > 0)
            {
                _measures.get(numMeasures).fixMeasureDurations();
                _newMeasureListener.onNewMeasure(_measures.get(numMeasures));

                Measure newMeasure = new Measure(_timeSignature);
                nextNoteDuration = newMeasure.addNote(new Note(note.getPitch(), nextNoteDuration));
                _measures.add(newMeasure);
            }
        }
    }

    public void addNote(Set<String> pitches, double duration)
    {
        int numMeasures = _measures.size() - 1;

        Note lastNote = _measures.get(numMeasures).getLastNote();
        String lastNotePitch = lastNote.getPitch();

        if (pitches.contains(lastNotePitch))
            addNote(new Note(lastNotePitch, duration));
        else
        {
            for (String pitch : pitches)
                addNote(new Note(pitch, duration));
        }
    }

    public String toVexFlowString()
    {
        StringBuilder string = new StringBuilder();
        //string.append(_timeSignature.toString()).append(' ').append(_tempo).append(' ');

        for (int index = 0; index < _measures.size(); ++index)
        {
            string.append(_measures.get(index).toVexFlowString());
            if (index < _measures.size())
                string.append(" | ");
        }

        return string.toString();
    }

    public String toJFuguePatternString()
    {
        StringBuilder string = new StringBuilder();
        //string.append(_timeSignature.toString()).append(' ').append(_tempo).append(' ');

        for (int index = 0; index < _measures.size(); ++index)
        {
            string.append(_measures.get(index).toJFuguePatternString());
            if (index < _measures.size())
                string.append(" | ");
        }

        return string.toString();
    }
}
