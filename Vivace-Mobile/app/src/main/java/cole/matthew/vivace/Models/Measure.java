package cole.matthew.vivace.Models;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Measure
{
    private ArrayList<Note> _notes;
    private TimeSignature _timeSignature;

    public Measure(TimeSignature timeSignature)
    {
        _timeSignature = timeSignature;
        _notes = new ArrayList<>();
    }

    public float addNote(Note note)
    {
        float usedDuration = getDuration();

        if (usedDuration + note.getDuration() <= _timeSignature.getBeatsPerMeasure())
        {
            addAndResolveNote(note);
            //_notes.add(note);
            usedDuration = -1;
        }
        else
        {
            addAndResolveNote(new Note(note.getPitch(), _timeSignature.getBeatsPerMeasure() - usedDuration));
            //_notes.add(new Note(note.getPitch(), _timeSignature.getBeatsPerMeasure() - usedDuration));
            usedDuration = (usedDuration + note.getDuration()) - _timeSignature.getBeatsPerMeasure();
        }

        return usedDuration;
    }

    public void fixMeasureDurations()
    {
        Note lastNote = getLastNote();

        if (lastNote != null)
            resolveNoteDuration(lastNote);
    }

    private void addAndResolveNote(@NotNull Note note)
    {
        Note lastNote = getLastNote();

        if (lastNote != null && lastNote.getPitch().equals(note.getPitch()))
            lastNote.addDuration(note.getDuration());
        else
        {
            if (lastNote != null)
                resolveNoteDuration(lastNote);

            _notes.add(note);
        }
    }

    private void resolveNoteDuration(@NotNull Note lastNote)
    {
        float noteDuration = lastNote.getDuration();

        if (noteDuration != 0.25f && noteDuration != 0.5f && noteDuration != 0.75f &&
            noteDuration != 1.0f && noteDuration != 1.5f && noteDuration != 2.0f &&
            noteDuration != 3.0f && noteDuration != 3.5f && noteDuration != 4.0f)
        {
            if (noteDuration < 1.5f && noteDuration > 1.0f)
            {
                float newDuration = noteDuration - 1;
                lastNote.subDuration(newDuration);
                _notes.add(new Note(lastNote.getPitch(), newDuration));
            }
            else if (noteDuration < 3.0f && noteDuration > 2.0f)
            {
                float newDuration = noteDuration - 2.0f;
                lastNote.subDuration(newDuration);
                _notes.add(new Note(lastNote.getPitch(), newDuration));
            }
            else if (noteDuration < 3.5f && noteDuration > 3.0f)
            {
                float newDuration = noteDuration - 3.0f;
                lastNote.subDuration(newDuration);
                _notes.add(new Note(lastNote.getPitch(), newDuration));
            }
            else if (noteDuration < 4.0f && noteDuration > 3.5f)
            {
                float newDuration = noteDuration - 3.5f;
                lastNote.subDuration(newDuration);
                _notes.add(new Note(lastNote.getPitch(), newDuration));
            }
        }
    }

    public TimeSignature getTimeSignature()
    {
        return _timeSignature;
    }

    public ArrayList<Note> getNotes()
    {
        return _notes;
    }

    public float getDuration()
    {
        float duration = 0f;

        for (Note note : _notes)
            duration += note.getDuration();

        return duration;
    }

    /**
     * Gets the last note in the measure.
     * @return The last note of the measure, or null if there isn't one.
     */
    public Note getLastNote()
    {
        if (!_notes.isEmpty())
        {
            int lastIndex = _notes.size() - 1;
            return _notes.get(lastIndex);
        }
        else
            return null;
    }

    public String toVexFlowString()
    {
        StringBuilder string = new StringBuilder();

        for (int index = 0; index < _notes.size(); ++index)
        {
            string.append(_notes.get(index).toVexFlowString());
            if (index < _notes.size())
                string.append(' ');
        }

        return string.toString();
    }

    public String toJFuguePatternString()
    {
        StringBuilder string = new StringBuilder();

        for (int index = 0; index < _notes.size(); ++index)
        {
            string.append(_notes.get(index).toJFuguePatternString());
            if (index < _notes.size())
                string.append(' ');
        }

        return string.toString();
    }
}
