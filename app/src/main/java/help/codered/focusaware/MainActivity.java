package help.codered.focusaware;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.judemanutd.autostarter.AutoStartPermissionHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.app.PendingIntent.getActivity;

public class MainActivity extends AppCompatActivity {
    Button btn_update_interval,
            btn_stop_alarm,
            btn_check_autostart,
            btn_start_focus_aware;
    EditText edit_text_interval;
    public static Uri notification;
    public static Ringtone r;
    private static String TAG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TAG=getString(R.string.uuid_for_worker);
        ////////////////////////////////////////////////////////////////////////
        btn_stop_alarm=(Button)findViewById(R.id.btn_stop_alarm);
        btn_update_interval=(Button)findViewById(R.id.btn_save_interval);
        btn_check_autostart=(Button)findViewById(R.id.btn_check_autostart);
        edit_text_interval=(EditText)findViewById(R.id.edit_text_interval);
        btn_start_focus_aware=(Button)findViewById(R.id.btn_start_focus_aware);
        ////////////////////////////////////////////////////////////////////

        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        //////////sharedpreferences creation with application context////////
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.uuid_for_worker), Context.MODE_PRIVATE);
        Editor editor=sharedPref.edit();
        //////////////sharedpreferences initial values//////////

        int interval_from_sharedPref = sharedPref.getInt("interval", 15);
        boolean is_focus_aware_running = sharedPref.getBoolean("is_focus_aware_running",false );


        final int[] interval = {interval_from_sharedPref};
        edit_text_interval.setText(""+ interval[0]);

        btn_update_interval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newInterval=Integer.parseInt(edit_text_interval.getText().toString());
                edit_text_interval.setText(newInterval+"");
                editor.putInt("interval",newInterval);
                editor.commit();
                WorkManager workManager=WorkManager.getInstance(MainActivity.this);
                workManager.cancelAllWorkByTag(TAG);
                PeriodicWorkRequest saveRequest =
                    new PeriodicWorkRequest.Builder(sheduleWork.class, newInterval, TimeUnit.MINUTES)
                            .addTag(TAG)
                            .build();
                WorkManager.getInstance(MainActivity.this).enqueue(saveRequest);
                editor.putBoolean("is_focus_aware_running",true);
                editor.commit();
            }
        });

        btn_stop_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(r.isPlaying()){
                    r.stop();
                }
            }
        });

        btn_check_autostart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAutoStart(true);
            }
        });

        btn_start_focus_aware.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean is_focus_aware_running = sharedPref.getBoolean("is_focus_aware_running", false);
                int interval_from_shared_prefs = sharedPref.getInt("interval", 0);
                if(is_focus_aware_running){
                    WorkManager workManager=WorkManager.getInstance(MainActivity.this);
                    workManager.cancelAllWorkByTag(TAG);
                    editor.putBoolean("is_focus_aware_running",false);
                    btn_check_autostart.setBackgroundColor(getResources().getColor(R.color.start_btn_inactive));
                    btn_check_autostart.setText("Start Focus Aware");
                }
                else {
                    WorkManager workManager=WorkManager.getInstance(MainActivity.this);
                    workManager.cancelAllWorkByTag(TAG);
                    PeriodicWorkRequest saveRequest =
                            new PeriodicWorkRequest.Builder(sheduleWork.class, interval[0], TimeUnit.MINUTES)
                                    .addTag(TAG)
                                    .build();
                    WorkManager.getInstance(MainActivity.this).enqueue(saveRequest);
                    editor.putBoolean("is_focus_aware_running",true);
                    editor.commit();
                    btn_check_autostart.setBackgroundColor(getResources().getColor(R.color.start_btn_active));
                    btn_check_autostart.setText("Stop Focus Aware");
                }
            }
        });

        checkAutoStart(false);
    }


    private void checkAutoStart(boolean force_check){
        boolean auto_start_availability;
        boolean permission_success;
        if(force_check){
            auto_start_availability = AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(this);
            permission_success = AutoStartPermissionHelper.getInstance().getAutoStartPermission(this);
            showAutoStartToast(auto_start_availability,permission_success);
        }
        else {
            boolean checked;
            checked=getPreferences(MODE_PRIVATE).getBoolean("auto_start_check",false);
            if(!checked){
                auto_start_availability = AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(this);
                permission_success = AutoStartPermissionHelper.getInstance().getAutoStartPermission(this);
                SharedPreferences preferences = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();  // Put the values from the UI
                editor.putBoolean("auto_start_check",true);
                // Commit to storage
                editor.apply();
                showAutoStartToast(auto_start_availability,permission_success);
            }
        }
    }

    public void showAutoStartToast(boolean auto_start_availability, boolean permission_success){
        if(auto_start_availability){
            Toast.makeText(MainActivity.this, "autostart available", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(MainActivity.this, "autostart not available", Toast.LENGTH_SHORT).show();
        }
        if(permission_success){
            Toast.makeText(MainActivity.this, "Permission success", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(MainActivity.this, "Auto start permission feature not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        TAG=getString(R.string.uuid_for_worker);
    }
}