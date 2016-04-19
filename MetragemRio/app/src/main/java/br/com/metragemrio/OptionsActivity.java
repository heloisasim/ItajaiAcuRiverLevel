package br.com.metragemrio;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.google.android.gms.analytics.HitBuilders;
import com.parse.ParseInstallation;
import com.parse.ParsePush;

import java.util.List;

public class OptionsActivity extends AppCompatActivity {

    private RadioButton mRadioBtNotification;
    private RadioButton mRadioBtNoNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_dialog);
        getSupportActionBar().setTitle("Configurações");
        Button button = (Button) findViewById(R.id.button);
        mRadioBtNotification = (RadioButton) findViewById(R.id.receive_notification);
        mRadioBtNoNotification = (RadioButton) findViewById(R.id.no_receive_notification);

        setActionBarColor();

        Spinner spinner = (Spinner) findViewById(R.id.spinner_level);
        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mRadioBtNotification.setChecked(true);
                return false;
            }
        });

        SharedPreferences settings = getSharedPreferences("settings", Context.MODE_PRIVATE);
        String level = settings.getString("level", "");
        if (!TextUtils.isEmpty(level)) {
            if (level.equals("0")) {
                mRadioBtNoNotification.setChecked(true);
            } else {
                String[] split = level.split("\\.");
                spinner.setSelection(Integer.parseInt(split[0]) - 6);
            }
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String levelSelected = "0";
                if (mRadioBtNotification.isChecked()) {
                    Spinner spinner = (Spinner) findViewById(R.id.spinner_level);
                    String selected = (String) spinner.getSelectedItem();
                    String[] split = selected.split("\\.");
                    if (split.length > 1)
                        levelSelected = split[0];
                }

                AppApplication.getTracker().setScreenName("OptionsActivity");
                AppApplication.getTracker().send(new HitBuilders.EventBuilder().setCategory("configValue")
                        .setAction("configValue").setLabel("level_" + levelSelected).build());

                // Antes de se inscrever no canal, remove o canal antigo
                List<String> subscribedChannels = ParseInstallation.getCurrentInstallation().getList("channels");
                for (String channel : subscribedChannels)
                    ParsePush.unsubscribeInBackground(channel);

                if (!levelSelected.equals("0"))
                    ParsePush.subscribeInBackground("level_" + levelSelected);

                SharedPreferences settings = getSharedPreferences("settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = settings.edit();
                edit.putString("level", levelSelected);
                edit.commit();

                Intent intent = getIntent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void setActionBarColor() {
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.green));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
    }

    @Override
    public void onBackPressed() {
        AppApplication.getTracker().setScreenName("OptionsActivity");
        AppApplication.getTracker().send(new HitBuilders.EventBuilder().setCategory("onBackPressed")
                .setAction("onBackPressed").setLabel("onBackPressed").build());
        super.onBackPressed();
    }
}
