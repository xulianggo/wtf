package wtf.sdk;

import android.content.DialogInterface;

public abstract class WtfDialogCallback implements android.app.AlertDialog.OnClickListener {

    public abstract void onCall(DialogInterface dialog, int which);

//    //@Override
    public void onClick(DialogInterface dialog, int which) {
        this.onCall(dialog, which);
    }
}
