package one.benotx.focusaware;

import android.content.Context;
import android.content.Intent;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.textfield.TextInputEditText;
import com.judemanutd.autostarter.AutoStartPermissionHelper;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    Button btn_update_interval,
            btn_stop_alarm,
            btn_check_autostart,
            btn_start_focus_aware;
    EditText edit_text_interval;
    TextInputEditText edit_text_reminder;
    public static Uri notification;
    public static Ringtone r;
    private static String TAG;
    private int interval_from_sharedPref;
    private boolean is_focus_aware_running;
    WorkManager workManager;
    TextToSpeech mTTS = null;
    private final int ACT_CHECK_TTS_DATA = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TAG=getString(R.string.uuid_for_worker);
        ////////////////////////////////////////////////////////////////////////
        btn_stop_alarm=(Button)findViewById(R.id.btn_stop_alarm);
        btn_update_interval=(Button)findViewById(R.id.btn_save_interval);
        btn_check_autostart=(Button)findViewById(R.id.btn_check_autostart);
        edit_text_interval=(EditText)findViewById(R.id.edit_text_interval_data);
        btn_start_focus_aware=(Button)findViewById(R.id.btn_start_focus_aware);
        workManager=WorkManager.getInstance(MainActivity.this);
        edit_text_reminder=(TextInputEditText) findViewById(R.id.edit_text_reminder);
        ////////////////////////////////////////////////////////////////////

        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        //////////sharedpreferences creation with application context////////
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.uuid_for_worker), Context.MODE_PRIVATE);
        Editor editor=sharedPref.edit();
        //////////////sharedpreferences initial values//////////

        interval_from_sharedPref = sharedPref.getInt("interval", 15);
        is_focus_aware_running = sharedPref.getBoolean("is_focus_aware_running",false );
        edit_text_interval.setText(""+ interval_from_sharedPref);

        btn_update_interval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newInterval=Integer.parseInt(edit_text_interval.getText().toString());
                edit_text_interval.setText(newInterval+"");
                editor.putInt("interval",newInterval);
                editor.commit();
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
                int interval_from_shared_prefs = sharedPref.getInt("interval", 15);
                Toast.makeText(MainActivity.this,is_focus_aware_running+""+interval_from_shared_prefs,Toast.LENGTH_SHORT).show();
                if(is_focus_aware_running){
                    workManager.cancelAllWorkByTag(TAG);
                    editor.putBoolean("is_focus_aware_running",false);
                    editor.commit();
                    btn_start_focus_aware.setBackgroundColor(getResources().getColor(R.color.start_btn_inactive));
                    btn_start_focus_aware.setText("Start Focus Aware");
                }
                else {
                    workManager.cancelAllWorkByTag(TAG);
                    PeriodicWorkRequest saveRequest =
                            new PeriodicWorkRequest.Builder(sheduleWork.class, interval_from_shared_prefs, TimeUnit.MINUTES)
                                    .addTag(TAG)
                                    .build();
                    WorkManager.getInstance(MainActivity.this).enqueue(saveRequest);
                    editor.putBoolean("is_focus_aware_running",true);
                    editor.commit();
                    btn_start_focus_aware.setBackgroundColor(getResources().getColor(R.color.start_btn_active));
                    btn_start_focus_aware.setText("Stop Focus Aware");
                }

//                ListenableFuture<List<WorkInfo>> workerInfo=workManager.getWorkInfosByTag( TAG);
//                Executor executor = new Executor() {
//                    @Override
//                    public void execute(Runnable command) {
//                        editor.putBoolean("",false);
//                        new Handler(Looper.getMainLooper()).post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(MainActivity.this,"Focus Aware, executor",Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                };
//                workerInfo.addListener(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            for (WorkInfo workinf:workerInfo.get()
//                            ) {
//                                if(workinf.getTags().contains(TAG)){
//                                    if(workinf.getState().isFinished()){
//                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                Toast.makeText(MainActivity.this,"Focus Aware, finished",Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                                    }
//                                }
//                            }
//                        } catch (ExecutionException e) {
//                            e.printStackTrace();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },executor);

            }
        });
        if(!is_focus_aware_running){
            btn_start_focus_aware.setBackgroundColor(getResources().getColor(R.color.start_btn_inactive));
            btn_start_focus_aware.setText("Start Focus Aware");
        }else{
            btn_start_focus_aware.setBackgroundColor(getResources().getColor(R.color.start_btn_active));
            btn_start_focus_aware.setText("Stop Focus Aware");
        }
        checkAutoStart(false);
        String val=sharedPref.getString("msg","");
        edit_text_reminder.setText(val);
        edit_text_reminder.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String msg=edit_text_reminder.getText().toString();
                editor.putString("msg",msg);
                editor.commit();
            }
        });

        // Check to see if we have TTS voice data
        Intent ttsIntent = new Intent();
        ttsIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(ttsIntent, ACT_CHECK_TTS_DATA);
        //ttsIntent.setPackage("my.tts.package");

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

    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACT_CHECK_TTS_DATA) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // Data exists, so we instantiate the TTS engine
                mTTS = new TextToSpeech(this, this);
            } else {
                // Data is missing, so we start the TTS
                // installation process
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }

    private void saySomething(String text, int qmode) {
        if (qmode == 1)
            mTTS.speak(text, TextToSpeech.QUEUE_ADD, null);
        else
            mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            if (mTTS != null) {
                int result = mTTS.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "TTS language is not supported", Toast.LENGTH_LONG).show();
                } else {
                    //saySomething("TTS is ready", 0);
                }
            }
        } else {
            Toast.makeText(this, "TTS initialization failed",
                    Toast.LENGTH_LONG).show();
        }
    }
}