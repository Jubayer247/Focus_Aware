package help.codered.focusaware;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.content.SharedPreferences.Editor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    Button btn_update_interval,btn_stop_alarm;
    EditText edit_text_interval;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ////////////////////////////////////////////////////////////////////////
        btn_stop_alarm=(Button)findViewById(R.id.btn_stop_alarm);
        btn_update_interval=(Button)findViewById(R.id.btn_save_interval);

        ////////////////////////////////////////////////////////////////////
        edit_text_interval=(EditText)findViewById(R.id.edit_text_interval);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();

//        PeriodicWorkRequest saveRequest =
//                new PeriodicWorkRequest.Builder(sheduleWork.class, 15, TimeUnit.MINUTES)
//                        // Constraints
//                        .build();
//
//        WorkManager.getInstance(this).enqueue(saveRequest);
//
        Editor editor= (Editor) getPreferences(MODE_PRIVATE);
        final int[] interval = {getPreferences(MODE_PRIVATE).getInt("interval", 0)};
        if(interval[0] ==0){
            editor.putInt("interval",15);
            editor.commit();
        }
        interval[0] =getPreferences(MODE_PRIVATE).getInt("interval",0);
        edit_text_interval.setText(""+ interval[0]);


        btn_update_interval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newInterval=edit_text_interval.getText().toString();
                edit_text_interval.setText(newInterval);
                editor.putInt("interval",Integer.parseInt(newInterval));
                editor.commit();
            PeriodicWorkRequest saveRequest =
                    new PeriodicWorkRequest.Builder(sheduleWork.class, 15, TimeUnit.MINUTES)
                            // Constraints
                            .build();

            WorkManager.getInstance(MainActivity.this).enqueue(saveRequest);
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

    }

}