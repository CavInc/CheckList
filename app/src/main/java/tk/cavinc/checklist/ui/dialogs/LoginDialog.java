package tk.cavinc.checklist.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import tk.cavinc.checklist.R;

/**
 * Created by cav on 13.08.18.
 */

public class LoginDialog extends DialogFragment {

    private OnLoginDialogListener mOnLoginDialogListener;

    private EditText mLogin;
    private EditText mPasswd;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.login_dialog, null);

        mLogin = v.findViewById(R.id.login_login);
        mPasswd = v.findViewById(R.id.login_passwd);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle("Вход в Yandex Disk")
                .setView(v)
                .setNegativeButton(R.string.dialog_close,null)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mOnLoginDialogListener != null) {
                            mOnLoginDialogListener.onLogin(mLogin.getText().toString(),mPasswd.getText().toString());
                        }

                    }
                });
        return builder.create();
    }

    public void setOnLoginDialogListener(OnLoginDialogListener onLoginDialogListener) {
        mOnLoginDialogListener = onLoginDialogListener;
    }

    public interface OnLoginDialogListener{
        void onLogin(String login,String pass);
    }
}
