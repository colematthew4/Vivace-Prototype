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

/**
 * <p>
 *     This is an extension of the DialogFragment that shows the different Time Signatures that Vivace
 *     can create sheet music for.
 * </p>
 * <p>
 *     NOTE: In order for this class to be used, the calling Activity MUST inherit the
 *     {@link NoticeTempoDialogListener} interface, or it will cause a runtime exception
 *     (see {@link #onAttach(Context)}).
 * </p>
 */
public class TempoPickerFragment extends DialogFragment
{
    public interface NoticeTempoDialogListener
    {
        /**
         * A callback method for receiving the
         * {@link android.app.AlertDialog.Builder#setPositiveButton(CharSequence, DialogInterface.OnClickListener)}
         * event that allows the hosting activity to do things with the value that was selected, in this
         * case the tempo of the music.
         *
         * @param tempo The tempo that was selected.
         */
        void onTempoDialogPositiveClick(int tempo);

        /**
         * A callback method for receiving the
         * {@link android.app.AlertDialog.Builder#setNegativeButton(CharSequence, DialogInterface.OnClickListener)}
         * event that allows the hosting activity to do things with the value that was selected, in this
         * case the tempo of the music.
         *
         * @param tempo The tempo that was selected.
         */
        void onTempoDialogNegativeClick(int tempo);
    }

    private final String TAG = "TempoPickerFragment";
    private NoticeTempoDialogListener _noticeDialogListener;
    private int _tempo;
    private DialogInterface.OnClickListener _onClickListener = new DialogInterface.OnClickListener()
    {
        /** {@inheritDoc} */
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            switch (which)
            {
                case DialogInterface.BUTTON_POSITIVE:
                    _noticeDialogListener.onTempoDialogPositiveClick(_tempo);
                    dialog.dismiss();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    _noticeDialogListener.onTempoDialogNegativeClick(_tempo);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.tempo_picker, null);

        NumberPicker tempoPicker = view.findViewById(R.id.tempoPicker);
        tempoPicker.setMaxValue(218);
        tempoPicker.setMinValue(40);
        tempoPicker.setValue(arguments.getInt("TempoValue"));
        tempoPicker.setOnScrollListener(new NumberPicker.OnScrollListener()
        {
            @Override
            public void onScrollStateChange(NumberPicker view, int scrollState)
            {
                _tempo = view.getValue();
            }
        });

        return builder.setView(view)
                      .setTitle("Pick the Tempo")
                      .setIcon(R.drawable.tempo_marker)
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
            _noticeDialogListener = (NoticeTempoDialogListener)context;
        }
        catch (ClassCastException e)    // The activity doesn't implement the interface, throw exception
        {
            String message = context.toString() + " must implement NoticeTempoDialogListener";
            Log.d(TAG, "onAttach: " + message);
            throw new ClassCastException(message);
        }
    }
}
