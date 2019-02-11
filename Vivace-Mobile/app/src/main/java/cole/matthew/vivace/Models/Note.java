package cole.matthew.vivace.Models;

public class Note {
    private float _duration;
    private String _pitch;
    private boolean _isRest;

    /**
     * Creates an instance of a note with the given pitch and duration.
     *
     * @param pitch    The pitch of the note.
     * @param duration The duration of the note in fractions of a seconds.
     */
    public Note(String pitch, float duration) {
        this(pitch, duration, false);
    }

    /**
     * Creates an instance of a note with the given pitch and duration.
     *
     * @param pitch    The pitch of the note.
     * @param duration The duration of the note in fractions of a second.
     */
    public Note(String pitch, double duration) {
        this(pitch, duration, false);
    }

    /**
     * Creates an instance of a note with the given pitch and duration.
     *
     * @param pitch    The pitch of the note.
     * @param duration The duration of the note in fractions of a second.
     * @param isRest   Whether the note is a rest.
     */
    public Note(String pitch, float duration, boolean isRest) {
        _pitch = pitch;
        _duration = duration;
        _isRest = isRest;
    }

    /**
     * Creates and instance of a note with the given pitch and duration.
     *
     * @param pitch    The pitch of the note.
     * @param duration The duration of the note in fractions of a second.
     * @param isRest   Whether the note is a rest.
     */
    public Note(String pitch, double duration, boolean isRest) {
        _pitch = pitch;
        _duration = (float)duration;
        _isRest = isRest;
    }

    /**
     * Gets the pitch and octave of the note.
     *
     * @return The pitch and octave.
     */
    public String getPitch() {
        return _pitch;
    }

    /**
     * Gets the pitch of the note in VexFlow notation.
     *
     * @return The pitch and octave in a format usable by VexFlow.
     */
    public String getVexFlowKey() {
        int pos = (_pitch.length() + 1) / 2;
        return _pitch.substring(0, pos) + "/" + _pitch.substring(pos);
    }

    /**
     * Gets the duration of the note.
     *
     * @return The duration in fractions of a second.
     */
    public float getDuration() {
        return _duration;
    }

    /**
     * Adds to the duration of the note.
     *
     * @param duration The duration to add in fractions of a second.
     */
    public void addDuration(float duration) {
        _duration += duration;
    }

    /**
     * Adds to the duration of the note.
     *
     * @param duration The duration to add in fractions of a second.
     */
    public void addDuration(double duration) {
        _duration += duration;
    }

    /**
     * Subtracts from the duration of the note.
     *
     * @param duration The duration to remove in fractions of a second.
     */
    public void subDuration(float duration) {
        _duration -= duration;
    }

    /**
     * Subtracts from the duration of the note.
     *
     * @param duration The duration to remove in fractions of a second.
     */
    public void subDuration(double duration) {
        _duration -= duration;
    }

    /**
     * Determines if a note is a rest.
     *
     * @return True if a rest, false if not.
     */
    public boolean isRest() {
        return _isRest;
    }

    //    /** {@inheritDoc} */
    //    @Override
    //    @NonNull
    //    public String toString() {
    //        StringBuilder string = new StringBuilder(_pitch).append('/');
    //
    //        if (_duration == 0.25f)
    //            string.append(16);
    //        else if (_duration == 0.5f)
    //            string.append(8);
    //        else if (_duration == 0.75f)
    //            string.append("8d");
    //        else if (_duration == 1.0f)
    //            string.append('q');
    //        else if (_duration == 1.5f)
    //            string.append("4d");
    //        else if (_duration == 1.75f)
    //            string.append("4dd");
    //        else if (_duration == 2.0f)
    //            string.append('h');
    //        else if (_duration == 3.0f)
    //            string.append("hd");
    //        else if (_duration == 3.5f)
    //            string.append("hdd");
    //        else if (_duration == 4.0f)
    //            string.append('w');
    //
    //        if (_isRest)
    //            string.append('r');
    //
    //        return string.toString();
    //    }

    /**
     * Gets a VexFlow-compatible representation of the note.
     *
     * @return The note as a string.
     */
    public String toVexFlowString() {
        return _pitch + "/" + getDurationVexFlowString();
    }

    /**
     * Gets the VexFlow-compatible duration of the note.
     *
     * @return The duration of the note as a string. To be used when working with VexFlow.
     */
    public String getDurationVexFlowString() {
        StringBuilder string = new StringBuilder();

        if (_duration == 0.25f) {
            string.append(16);
        } else if (_duration == 0.5f) {
            string.append(8);
        } else if (_duration == 0.75f) {
            string.append("8d");
        } else if (_duration == 1.0f) {
            string.append('q');
        } else if (_duration == 1.5f) {
            string.append("4d");
        } else if (_duration == 1.75f) {
            string.append("4dd");
        } else if (_duration == 2.0f) {
            string.append('h');
        } else if (_duration == 3.0f) {
            string.append("hd");
        } else if (_duration == 3.5f) {
            string.append("hdd");
        } else if (_duration == 4.0f) {
            string.append('w');
        }

        if (_isRest) {
            string.append('r');
        }

        return string.toString();
    }

    /**
     * Gets the JFugue-compatible representation of the note.
     *
     * @return The note as a string.
     */
    public String toJFuguePatternString() {
        StringBuilder string = new StringBuilder();

        if (_isRest) {
            string.append("R");
        } else {
            string.append(_pitch);
        }

        if (_duration == 0.25f) {
            string.append('s');
        } else if (_duration == 0.5f) {
            string.append('i');
        } else if (_duration == 0.75f) {
            string.append("i.");
        } else if (_duration == 1.0f) {
            string.append('q');
        } else if (_duration == 1.5f) {
            string.append("q.");
        } else if (_duration == 1.75f) {
            string.append("q..");
        } else if (_duration == 2.0f) {
            string.append('h');
        } else if (_duration == 3.0f) {
            string.append("h.");
        } else if (_duration == 3.5f) {
            string.append("h..");
        } else if (_duration == 4.0f) {
            string.append('w');
        }

        return string.toString();
    }
}
