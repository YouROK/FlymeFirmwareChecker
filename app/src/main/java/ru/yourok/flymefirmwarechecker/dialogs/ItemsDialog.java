package ru.yourok.flymefirmwarechecker.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by yourok on 21.09.17.
 */

public class ItemsDialog {
    AlertDialog alertDialog;
    String item;
    Runnable runnable;

    public ItemsDialog(Context context, int resStringArray) {
        this(context, context.getResources().getStringArray(resStringArray));
    }

    public ItemsDialog(Context context, final String[] items) {
        alertDialog = new AlertDialog.Builder(context)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        item = items[which];
                        runnable.run();
                    }
                })
                .create();
    }

    public ItemsDialog show(Runnable runnable) {
        this.runnable = runnable;
        alertDialog.show();
        return this;
    }

    public String getItem() {
        return item;
    }
}
