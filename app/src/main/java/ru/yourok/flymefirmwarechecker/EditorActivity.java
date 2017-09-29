package ru.yourok.flymefirmwarechecker;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stericson.RootShell.execution.Command;
import com.stericson.RootTools.RootTools;

import java.io.File;

import ru.yourok.utils.RemoveScript;
import ru.yourok.utils.Utils;

public class EditorActivity extends AppCompatActivity {

    public static final String REMOVE_SCRIPT_FNAME = "removeApps.sh";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ((TextView) findViewById(R.id.textViewWarn)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.setVisibility(View.GONE);
                return true;
            }
        });
    }

    private void ShowMsg(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(EditorActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enableButton(final boolean val) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.ButtonRun).setEnabled(val);
            }
        });
    }

    private void loadRemoveScript() {
        File script = getCacheFile(REMOVE_SCRIPT_FNAME);
        if (script == null) {
            finish();
            ShowMsg("Error open cache file");
        }

        String txt = "";
        try {
            txt = Utils.readFile(script);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (txt.isEmpty())
            txt = RemoveScript.Script;

        ((EditText) findViewById(R.id.editTextScript)).setText(txt);
    }

    private void saveRemoveScript() throws Exception {
        String txt = ((EditText) findViewById(R.id.editTextScript)).getText().toString();
        if (txt.trim().isEmpty())
            txt = RemoveScript.Script;
        Utils.writeFile(getCacheFile(REMOVE_SCRIPT_FNAME), txt);
    }

    @Override
    protected void onStart() {
        loadRemoveScript();
        super.onStart();
    }

    @Override
    protected void onStop() {
        try {
            saveRemoveScript();
        } catch (Exception e) {
            e.printStackTrace();
            ShowMsg("Error save script");
        }
        super.onStop();
    }


    public File getCacheFile(String fileName) {
        File file = null;
        try {
            file = new File(getFilesDir().getPath(), fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }


    public void onBtnRun(View view) {
        final String warn = "Everything you do, you do at your own peril and risk.\n\n" +
                "The author is not responsible for any damages that might happen during this.\n\n" +
                "Все, что вы делаете, вы делаете на свой страх и риск.\n\n" +
                "Автор не несет ответственности за любые повреждения, которые могут возникнуть во время этого.";

        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(warn)
                .setPositiveButton("I agree/Согласен", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        run();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        alertDialog.show();
    }

    private void run() {
        enableButton(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!RootTools.isRootAvailable() || !RootTools.isAccessGiven()) {
                    ShowMsg("root access denied");
                    enableButton(true);
                    return;
                }

                RootTools.debugMode = true;

                RootTools.remount("/system", "rw");
                RootTools.remount("/custom", "rw");

                try {
                    saveRemoveScript();
                } catch (Exception e) {
                    e.printStackTrace();
                    ShowMsg("Error save script: " + e.getMessage());
                    enableButton(true);
                    return;
                }

                try {
                    String cmd = "sh " + getCacheFile(REMOVE_SCRIPT_FNAME).getPath();
                    Command command = new Command(0, cmd) {
                        String output = "";

                        @Override
                        public void commandOutput(int id, String line) {
                            super.commandOutput(id, line);
                            output += line + "\n";
                        }

                        @Override
                        public void commandCompleted(int id, int exitcode) {
                            String msg = "";
                            if (exitcode != 0)
                                msg += "Have some warnings";
                            else
                                msg += "All ok";
                            if (!output.isEmpty())
                                msg += ":\n" + output;
                            final String finalMsg = msg;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((TextView) findViewById(R.id.textViewWarn)).setVisibility(View.VISIBLE);
                                    ((TextView) findViewById(R.id.textViewWarn)).setText(finalMsg);
                                }
                            });
                            enableButton(true);
                            super.commandCompleted(id, exitcode);
                        }
                    };
                    RootTools.getShell(true).add(command);
                } catch (Exception e) {
                    e.printStackTrace();
                    ShowMsg("Error run script: " + e.getMessage());
                    enableButton(true);
                }
            }
        }).start();
    }

    public void onBtnDefault(View view) {
        EditText scriptor = ((EditText) findViewById(R.id.editTextScript));
        scriptor.setText(RemoveScript.Script);
    }
}
