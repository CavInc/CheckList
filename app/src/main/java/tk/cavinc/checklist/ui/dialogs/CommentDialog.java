package tk.cavinc.checklist.ui.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import tk.cavinc.checklist.R;

/**
 * Created by cav on 07.08.18.
 */

public class CommentDialog extends DialogFragment {

    private static final String MESSAGE = "MSG";
    private OnCommentDialogListener mDialogListener;
    private EditText mComment;

    private String mMessage = null;

    public static CommentDialog newInstance(String message){
        Bundle args = new Bundle();
        args.putString(MESSAGE,message);
        CommentDialog dialog = new CommentDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            mMessage = getArguments().getString(MESSAGE);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.comment_dialog, null);
        mComment = v.findViewById(R.id.cd_et);

        if (mMessage != null) {
            mComment.setText(mMessage);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle("Комментарий")
                .setView(v)
                .setNegativeButton(R.string.dialog_close,null)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mDialogListener != null) {
                            mDialogListener.onChange(mComment.getText().toString());
                        }
                    }
                });
        return builder.create();
    }

    public void setDialogListener(OnCommentDialogListener dialogListener) {
        mDialogListener = dialogListener;
    }

    public interface OnCommentDialogListener {
        public void onChange(String val);
    }
}
