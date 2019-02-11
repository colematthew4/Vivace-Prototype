package cole.matthew.vivace.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

import cole.matthew.vivace.Exceptions.InvalidTempoException;
import cole.matthew.vivace.Exceptions.NegativeNumberException;
import cole.matthew.vivace.R;

public final class TempoPickerPreference extends DialogPreference {
    private final String TAG = "TempoPickerPreference";
    private int _tempo;
    private int _maxTempo;
    private int _minTempo;
    private NumberPicker _tempoPicker;

    public TempoPickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
            throws NegativeNumberException, InvalidTempoException
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        setDefaults(context, attrs, defStyleAttr, defStyleRes);
    }

    public TempoPickerPreference(Context context, AttributeSet attrs, int defStyleAttr)
            throws NegativeNumberException, InvalidTempoException
    {
        this(context, attrs, defStyleAttr, 0);
    }

    public TempoPickerPreference(Context context, AttributeSet attrs)
            throws InvalidTempoException, NegativeNumberException
    {
        super(context, attrs);
        setDefaults(context, attrs);
    }

    public TempoPickerPreference(Context context)
            throws NegativeNumberException, InvalidTempoException
    {
        super(context);
        setDefaults(context, null);
    }

    /**
     * Assigns default values to the instance's private members.
     *
     * @param context The context to get the styleable attributes from.
     * @param attrs   The base set of attribute values.
     *
     * @exception NegativeNumberException When the tempo is a negative number.
     * @exception InvalidTempoException   When the tempo is less than the minimum tempo or greater than the maximum tempo.
     */
    private void setDefaults(Context context, AttributeSet attrs)
            throws NegativeNumberException, InvalidTempoException
    {
        setDefaults(context, attrs, 0, 0);
    }

    /**
     * @param context      The context to get the styleable attributes from.
     * @param attrs        The base set of attribute values.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource that supplies defaults values for the TypedArray. Can be 0 to not look for defaults.
     * @param defStyleRes  A resource identifier of a style resource that supplies default values for the TypedArray, used only if defStyleAttr is 0 or can not be found in the theme. Can be 0 to not look for defaults.
     *
     * @exception NegativeNumberException When the tempo is a negative number.
     * @exception InvalidTempoException   When the tempo is less than the minimum tempo or greater than the maximum tempo.
     */
    private void setDefaults(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
            throws NegativeNumberException, InvalidTempoException
    {
        setDialogLayoutResource(R.layout.tempo_picker);
        setDialogIcon(R.drawable.ic_tempo_marker);
        setDialogTitle("Pick the Tempo");

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TempoPickerPreference, defStyleAttr, defStyleRes);
        try {
            setMaxTempo(typedArray.getInteger(R.styleable.TempoPickerPreference_maxTempo, 218));
            setMinTempo(typedArray.getInteger(R.styleable.TempoPickerPreference_minTempo, 40));
        } finally {
            typedArray.recycle();
        }
    }

    /**
     * Gets the maximum tempo a recording can be processed at.
     *
     * @return The maximum tempo.
     */
    public int getMaxTempo() {
        return _maxTempo;
    }

    /**
     * Sets the maximum tempo a recording can be processed at.
     *
     * @param tempo The new maximum tempo
     *
     * @exception NegativeNumberException When the new tempo is a negative number.
     * @exception InvalidTempoException   When the new tempo is less than the minimum tempo.
     */
    public void setMaxTempo(int tempo)
            throws NegativeNumberException, InvalidTempoException
    {
        if (tempo < 0) {
            throw new NegativeNumberException(tempo);
        } else if (tempo < _minTempo) {
            throw new InvalidTempoException(tempo);
        }

        _maxTempo = tempo;
        persistData(tempo);
    }

    /**
     * Gets the minimum tempo a recording can be processed at.
     *
     * @return The minimum tempo.
     */
    public int getMinTempo() {
        return _minTempo;
    }

    /**
     * Sets the minimum tempo a recording can be processed at.
     *
     * @param tempo The new minimum tempo
     *
     * @exception NegativeNumberException When the new tempo is a negative number.
     * @exception InvalidTempoException   When the new tempo is greater than the maximum tempo.
     */
    public void setMinTempo(int tempo)
            throws NegativeNumberException, InvalidTempoException
    {
        if (tempo < 0) {
            throw new NegativeNumberException(tempo);
        } else if (tempo > _maxTempo) {
            throw new InvalidTempoException(tempo);
        }

        _minTempo = tempo;
        persistData(_minTempo);
    }

    /**
     * Gets the current tempo a recording is to be processed at.
     *
     * @return The current tempo.
     */
    public int getTempo() {
        return _tempo;
    }

    /**
     * Sets the current tempo a recording is to be processed at.
     *
     * @param tempo The new current tempo
     *
     * @exception InvalidTempoException When the tempo is less than the minimum tempo or greater than the maximum tempo.
     */
    public void setTempo(int tempo)
            throws InvalidTempoException
    {
        if (tempo < _minTempo || tempo > _maxTempo) {
            throw new InvalidTempoException(tempo);
        }

        _tempo = tempo;
        setSummary(_tempo + " BPM");
        persistData(_tempo);
    }

    /**
     * Attempts to persist a value.
     *
     * @param value The value to persist.
     */
    private void persistData(int value) {
        persistInt(value);
        notifyChanged();
    }

    /** {@inheritDoc} */
    @Override
    protected View onCreateDialogView() {
        View view = super.onCreateDialogView();
        _tempoPicker = view.findViewById(R.id.tempoPicker);
        _tempoPicker.setMaxValue(_maxTempo);
        _tempoPicker.setMinValue(_minTempo);
        _tempoPicker.setValue(_tempo);

        return view;
    }

    /** {@inheritDoc} */
    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        builder.setPositiveButton("Select", (dialog, which) -> {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                try {
                    setTempo(_tempoPicker.getValue());
                } catch (InvalidTempoException e) {
                    Log.e(TAG, "Line " + e.getStackTrace()[0].getLineNumber() + " - " + e.getMessage());
                    // TODO: show error message?
                }
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, 120);
    }

    /** {@inheritDoc} */
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        try {
            setTempo(restorePersistedValue ? getPersistedInt(_tempo) : defaultValue != null ? (int)defaultValue : 120);
        } catch (InvalidTempoException e) {
            Log.e(TAG, "Line " + e.getStackTrace()[0].getLineNumber() + " - " + e.getMessage());
        }
    }
}
