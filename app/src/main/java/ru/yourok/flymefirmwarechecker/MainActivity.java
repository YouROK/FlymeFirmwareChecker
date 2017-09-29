package ru.yourok.flymefirmwarechecker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import ru.yourok.flymefirmwarechecker.dialogs.DateTimeDialog;
import ru.yourok.flymefirmwarechecker.dialogs.ItemsDialog;
import ru.yourok.utils.Params;
import ru.yourok.utils.RequestManager;
import ru.yourok.utils.SystemParams;
import ru.yourok.utils.Utils;

import static ru.yourok.utils.SystemParams.DEVICE_ID;
import static ru.yourok.utils.SystemParams.DEVICE_TYPE;
import static ru.yourok.utils.SystemParams.FIRMWARE;
import static ru.yourok.utils.SystemParams.IMEI;
import static ru.yourok.utils.SystemParams.ROOT;
import static ru.yourok.utils.SystemParams.SN;
import static ru.yourok.utils.SystemParams.SYSVER;
import static ru.yourok.utils.SystemParams.VERSION;

public class MainActivity extends AppCompatActivity {

    private JSONObject params;
    private JSONObject update;
    private RequestManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((CheckBox) findViewById(R.id.checkBoxInternational)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditText(R.id.editTextHost, SystemParams.getHost(((CheckBox) findViewById(R.id.checkBoxInternational)).isChecked()));
            }
        });

        ((EditText) findViewById(R.id.editTextFirmType)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final ItemsDialog itemsDialog = new ItemsDialog(MainActivity.this, R.array.firmware_type);
                itemsDialog.show(new Runnable() {
                    @Override
                    public void run() {
                        String item = itemsDialog.getItem();
                        if (item != null)
                            setEditText(R.id.editTextFirmType, item);
                    }
                });
                return true;
            }
        });

        View.OnLongClickListener cl = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                final ItemsDialog itemsDialog = new ItemsDialog(MainActivity.this, R.array.android_versions);
                itemsDialog.show(new Runnable() {
                    @Override
                    public void run() {
                        String item = itemsDialog.getItem();
                        if (item != null)
                            ((EditText) v).setText(item);
                    }
                });
                return true;
            }
        };

        ((EditText) findViewById(R.id.editTextVersion)).setOnLongClickListener(cl);
        ((EditText) findViewById(R.id.editTextFirmware)).setOnLongClickListener(cl);

        ((EditText) findViewById(R.id.editTextTimestamp)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                long timestamp = -1;
                if (!getEditText(R.id.editTextTimestamp).isEmpty())
                    timestamp = Long.parseLong(getEditText(R.id.editTextTimestamp));
                final DateTimeDialog dialog = new DateTimeDialog(MainActivity.this, timestamp);
                dialog.show().onConfirm(new Runnable() {
                    @Override
                    public void run() {
                        setEditText(R.id.editTextTimestamp, String.valueOf(dialog.getTimestamp()));
                    }
                });
                return true;
            }
        });

        ((EditText) findViewById(R.id.editTextDeviceType)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final ItemsDialog itemsDialog = new ItemsDialog(MainActivity.this, R.array.devices_id);
                itemsDialog.show(new Runnable() {
                    @Override
                    public void run() {
                        String item = itemsDialog.getItem();
                        String[] tmp = item.split("-");
                        if (tmp.length == 2)
                            item = tmp[0];
                        setEditText(R.id.editTextDeviceType, item);
                    }
                });
                return true;
            }
        });
    }

    private void ShowMsg(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        try {
            Params p = new Params(this);
            params = p.getParams();
            if (!p.getUpdate().isEmpty())
                update = new JSONObject(p.getUpdate());
            if (params == null || (params.getString(SN).isEmpty() && params.getString(IMEI).isEmpty()))
                params = new SystemParams(this).getSystemParams();
            writeToUIParams(params);
        } catch (Exception e) {
            e.printStackTrace();
            ShowMsg(e.getMessage());
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        try {
            params = readFromUIParams();
            Params p = new Params(this);
            p.setParams(params);
            if (update != null)
                p.setUpdate(update.toString());
        } catch (Exception e) {
            e.printStackTrace();
            ShowMsg(e.getMessage());
        }
        super.onStop();
    }

    private String getEditText(int id) {
        if (id == R.id.checkBoxRoot) {
            if (((CheckBox) findViewById(id)).isChecked())
                return "1";
            return "0";
        }

        return ((EditText) findViewById(id)).getText().toString();
    }

    private void setEditText(final int id, final String val) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (id == R.id.checkBoxRoot)
                    ((CheckBox) findViewById(id)).setChecked(val.equals("1"));
                else
                    ((EditText) findViewById(id)).setText(val);
            }
        });
    }

    private JSONObject readFromUIParams() throws Exception {
        String andVer = getEditText(R.id.editTextVersion);
        String timestamp = getEditText(R.id.editTextTimestamp);
        String firmtype = getEditText(R.id.editTextFirmType);
        String maskid = andVer + "-" + timestamp + "_" + firmtype;

        JSONObject basicParams = new JSONObject();
        basicParams.put(DEVICE_TYPE, getEditText(R.id.editTextDeviceType));
        basicParams.put(FIRMWARE, getEditText(R.id.editTextFirmware));
        basicParams.put(ROOT, getEditText(R.id.checkBoxRoot));
        basicParams.put(SYSVER, maskid);
        basicParams.put(VERSION, maskid);
        basicParams.put(DEVICE_ID, getEditText(R.id.editTextImei));
        basicParams.put(SN, getEditText(R.id.editTextSN));
        basicParams.put(IMEI, getEditText(R.id.editTextImei));
        return basicParams;
    }

    private void writeToUIParams(JSONObject params) throws Exception {
        String maskid = params.getString(SYSVER);
        String[] prop = maskid.split("-|_");

        setEditText(R.id.editTextDeviceType, params.getString(DEVICE_TYPE));
        setEditText(R.id.editTextFirmware, params.getString(FIRMWARE));
        if (prop.length > 2) {
            setEditText(R.id.editTextVersion, prop[0]);
            setEditText(R.id.editTextTimestamp, prop[1]);
            setEditText(R.id.editTextFirmType, prop[2]);
        }
        setEditText(R.id.checkBoxRoot, params.getString(ROOT));
        setEditText(R.id.editTextImei, params.getString(DEVICE_ID));
        setEditText(R.id.editTextSN, params.getString(SN));
        UpdateView();
    }

    public void onBtnRead(View view) {
        try {
            params = new SystemParams(this).getSystemParams();
            writeToUIParams(params);
        } catch (Exception e) {
            e.printStackTrace();
            ShowMsg(e.getMessage());
        }
    }

    public void onBtnCheck(View view) {
        try {
            ((TextView) findViewById(R.id.textViewUpdate)).setText("Requesting...");
            params = readFromUIParams();

            final RequestManager manager = new RequestManager(getEditText(R.id.editTextHost), params);
            manager.check(new Runnable() {
                @Override
                public void run() {
                    final String stat = manager.getStat();
                    update = manager.getUpdate();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (stat.isEmpty()) {
                                ShowMsg("Ok");
                                UpdateView();
                            } else
                                ((TextView) findViewById(R.id.textViewUpdate)).setText(stat);
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            ShowMsg(e.getMessage());
        }
    }

    public void onBtnFind(View view) {
        try {
            if (manager != null && manager.isFinding()) {
                manager.stop();
                ((Button) findViewById(R.id.buttonFind)).setText("Find");
                return;
            }

            ((TextView) findViewById(R.id.textViewUpdate)).setText("Requesting...");
            params = readFromUIParams();

            final RequestManager rm = new RequestManager(getEditText(R.id.editTextHost), params);
            manager = rm;
            rm.find(new Runnable() {
                        @Override
                        public void run() {
                            final String stat = rm.getStat();
                            update = rm.getUpdate();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (stat == null)
                                        ((TextView) findViewById(R.id.textViewUpdate)).setText("");
                                    else {
                                        if (stat.isEmpty()) {
                                            ShowMsg("Ok");
                                            UpdateView();
                                        } else
                                            ((TextView) findViewById(R.id.textViewUpdate)).setText(stat);
                                    }
                                    ((Button) findViewById(R.id.buttonFind)).setText("Find");
                                }
                            });

                        }
                    },
                    new Runnable() {
                        @Override
                        public void run() {
                            long timestamp = rm.getTimestamp();
                            if (timestamp != -1) {
                                setEditText(R.id.editTextTimestamp, String.valueOf(timestamp));
                            }
                        }
                    });

            if (manager.isFinding())
                ((Button) findViewById(R.id.buttonFind)).setText("Stop");
            else
                ((Button) findViewById(R.id.buttonFind)).setText("Find");

        } catch (Exception e) {
            e.printStackTrace();
            ShowMsg(e.getMessage());
        }
    }

    private void UpdateView() {
        String upd = "";
        try {
            if (update != null) {
                if (Utils.has(update, "reply", "value", "new")) {
                    JSONObject jnew = update.getJSONObject("reply").getJSONObject("value").getJSONObject("new");
                    upd = jnew.getString("systemVersion") + " " + jnew.getString("fileSize") + " " + jnew.getString("releaseDate");
                } else if (update.has("code")) {
                    upd = update.getString("message");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((TextView) findViewById(R.id.textViewUpdate)).setText(upd);
    }

    public void onBtnView(View view) {
        if (update == null) {
            ShowMsg("Nothing view");
            return;
        }
        Intent intent = new Intent(this, UpdateViewActivity.class);
        intent.putExtra("Update", update.toString());
        startActivity(intent);
    }

    public void onBtnEditor(View view) {
        Intent intent = new Intent(this, EditorActivity.class);
        startActivity(intent);
    }
}
