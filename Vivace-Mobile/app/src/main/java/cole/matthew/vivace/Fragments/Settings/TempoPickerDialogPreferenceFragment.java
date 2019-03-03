package cole.matthew.vivace.Fragments.Settings;

import android.os.Bundle;
import android.support.v14.preference.PreferenceDialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

import cole.matthew.vivace.Models.Exceptions.InvalidTempoException;
import cole.matthew.vivace.R;

public final class TempoPickerDialogPreferenceFragment extends PreferenceDialogFragment {
    private NumberPicker _tempoPicker;

    static PreferenceDialogFragment newInstance(String key) {
        final PreferenceDialogFragment fragment = new TempoPickerDialogPreferenceFragment();
        final Bundle bundle = new Bundle();
        bundle.putString(ARG_KEY, key);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Gets the preference that requested this dialog.
     * @return A {@link TempoPickerPreference}.
     */
    TempoPickerPreference getTempoPickerDialogPreference() {
        return (TempoPickerPreference)getPreference();
    }

    /** {@inheritDoc} */
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        TempoPickerPreference preference = getTempoPickerDialogPreference();
        _tempoPicker = view.findViewById(R.id.tempoPicker);
        _tempoPicker.setMaxValue(preference.getMaxTempo());
        _tempoPicker.setMinValue(preference.getMinTempo());
        _tempoPicker.setValue(preference.getTempo());
    }

    /** {@inheritDoc} */
    @Override
    public void onDialogClosed(boolean positiveResult) {
        try {
            TempoPickerPreference preference = getTempoPickerDialogPreference();
            if (positiveResult) {
                int newTempo = _tempoPicker != null ? _tempoPicker.getValue() : preference.getTempo();
                if (preference.callChangeListener(newTempo)) {
                    preference.setTempo(newTempo);
                }
            }
        } catch (InvalidTempoException e) {
            Log.e(this.getClass().getName(), "Line " + e.getStackTrace()[0].getLineNumber() + " - " + e.getMessage());
        }
    }
}
