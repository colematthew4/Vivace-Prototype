package cole.matthew.vivace;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import java.util.Arrays;

/**
 * <p>
 *     This is an extension of the DialogFragment that shows the different Time Signatures that Vivace
 *     can create sheet music for.
 * </p>
 * <p>
 *     NOTE: In order for this class to be used, the calling Activity MUST inherit the
 *     {@link NoticeTimeSignDialogListener} interface, or it will cause a runtime exception
 *     (see {@link #onAttach(Context)}).
 * </p>
 */
public class TimeSignPickerFragment extends DialogFragment
{
    public interface NoticeTimeSignDialogListener
    {
        /**
         * A callback method for receiving the
         * {@link android.app.AlertDialog.Builder#setPositiveButton(CharSequence, DialogInterface.OnClickListener)}
         * event that allows the hosting activity to do things with the value that was selected, in this
         * case the time signature of the music.
         *
         * @param timeSign The tempo that was selected.
         */
        void onTimeSignDialogPositiveClick(String timeSign);

        /**
         * A callback method for receiving the
         * {@link android.app.AlertDialog.Builder#setNegativeButton(CharSequence, DialogInterface.OnClickListener)}
         * event that allows the hosting activity to do things with the value that was selected, in this
         * case the time signature of the music.
         *
         * @param timeSign The tempo that was selected.
         */
        void onTimeSignDialogNegativeClick(String timeSign);
    }

    private final String TAG = "TempoPickerFragment";
    private NoticeTimeSignDialogListener _noticeDialogListener;
    private String _timeSignature;
    private String[] TimeSignatures;
    private DialogInterface.OnClickListener _onClickListener = new DialogInterface.OnClickListener()
    {
        /** {@inheritDoc} */
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            switch (which)
            {
                case DialogInterface.BUTTON_POSITIVE:
                    _noticeDialogListener.onTimeSignDialogPositiveClick(_timeSignature);
                    dialog.dismiss();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    _noticeDialogListener.onTimeSignDialogNegativeClick(_timeSignature);
                    dialog.cancel();
                    break;
                default:
                    break;
            }
        }
    };

    /** {@inheritDoc} */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Bundle arguments = savedInstanceState;
        if (arguments == null)
            arguments = getArguments();

        TimeSignatures = getResources().getStringArray(R.array.timeSignatures);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.time_signature_picker, null);

        NumberPicker timeSignPicker = view.findViewById(R.id.timeSignPicker);
        timeSignPicker.setMaxValue(TimeSignatures.length - 1);
        timeSignPicker.setMinValue(0);
        timeSignPicker.setDisplayedValues(TimeSignatures);
        timeSignPicker.setValue(Arrays.asList(TimeSignatures).indexOf(arguments.getString("TimeSignValue")));
        timeSignPicker.setOnScrollListener(new NumberPicker.OnScrollListener()
        {
            @Override
            public void onScrollStateChange(NumberPicker view, int scrollState)
            {
                _timeSignature = TimeSignatures[view.getValue()];
            }
        });

        return builder.setView(view)
                      .setTitle("Pick the Time Signature")
                      .setPositiveButton("Select", _onClickListener)
                      .setNegativeButton("Cancel", _onClickListener)
                      .create();
    }

    /** {@inheritDoc} */
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        try
        {
            _noticeDialogListener = (NoticeTimeSignDialogListener)context;
        }
        catch (ClassCastException e)    // The activity doesn't implement the interface, throw exception
        {
            String message = context.toString() + " must implement NoticeTempoDialogListener";
            Log.d(TAG, "onAttach: " + message);
            throw new ClassCastException(message);
        }
    }
}
