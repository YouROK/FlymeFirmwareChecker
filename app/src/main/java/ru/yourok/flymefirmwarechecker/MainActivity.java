package ru.yourok.flymefirmwarechecker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import ru.yourok.utils.Params;
import ru.yourok.utils.RequestManager;
import ru.yourok.utils.SystemParams;

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

    private void setEditText(int id, String val) {
        if (id == R.id.checkBoxRoot)
            ((CheckBox) findViewById(id)).setChecked(val.equals("1"));
        else
            ((EditText) findViewById(id)).setText(val);
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
            RequestManager conn = new RequestManager(getEditText(R.id.editTextHost));
            String sparams = params.toString();
            String sign = SystemParams.getSign(sparams);
            String body = "unitType=0&sys=" + sparams + "&sign=" + sign;
            conn.sendRequest(body);

            String resp = conn.recvResponce();

            JSONObject json = new JSONObject(resp);
            Log.i("FlymeFirmwareChecker", json.toString(1));

            if (json.has("code") && !json.getString("code").equals("200")) {
                ShowMsg(json.getString("message"));
            }
            update = json;
        } catch (Exception e) {
            e.printStackTrace();
            ShowMsg(e.getMessage());
        }
        UpdateView();
    }

    private void UpdateView() {
        try {
            String upd = "";
            if (update != null) {
                if (update.has("reply")) {
                    JSONObject jnew = update.getJSONObject("reply").getJSONObject("value").getJSONObject("new");
                    upd = jnew.getString("systemVersion") + " " + jnew.getString("fileSize") + " " + jnew.getString("releaseDate");
                } else if (update.has("code")) {
                    upd = update.getString("message");
                }
            }
            ((TextView) findViewById(R.id.textViewUpdate)).setText(upd);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
