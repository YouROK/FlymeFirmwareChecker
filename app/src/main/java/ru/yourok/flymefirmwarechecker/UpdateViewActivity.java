package ru.yourok.flymefirmwarechecker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_view);

        Intent intent = getIntent();
        String upd = intent.getStringExtra("Update");
        try {
            JSONObject update = new JSONObject(upd);

            String releaseNotes = "<h4>Firmware</h4>";
            JSONObject jnew = update.getJSONObject("reply").getJSONObject("value").getJSONObject("new");
            releaseNotes += "<p>" + jnew.getString("systemVersion") +
                    "<br>" + jnew.getString("fileSize") +
                    "<br>" + jnew.getString("releaseDate") + "</p>" +
                    "<h4>" + jnew.getString("updateUrl") + "</h4>";
            releaseNotes += "<br>" + jnew.getString("releaseNote");
            ((TextView) findViewById(R.id.textViewUrl)).setText(jnew.getString("updateUrl"));
            ((WebView) findViewById(R.id.webView)).loadDataWithBaseURL(null, releaseNotes, "text/html", "utf-8", null);

        } catch (JSONException e) {
            e.printStackTrace();
            finish();
        }


    }
}
