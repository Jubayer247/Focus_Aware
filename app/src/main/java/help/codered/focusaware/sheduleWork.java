package help.codered.focusaware;


import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import static help.codered.focusaware.MainActivity.r;
public class sheduleWork extends Worker {
    private Context context;

    private TextToSpeech textToSpeechSystem;

    public sheduleWork(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context=context;
    }

    @Override
    public Result doWork() {
        textToSpeechSystem = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    UtteranceProgressListener utteranceProgressListener=new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                            Toast.makeText(context,"Focus Aware, TTS-S",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            r.play();
                            Toast.makeText(context,"Focus Aware, TTS-D",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String utteranceId) {
                            r.play();
                            Toast.makeText(context,"Focus Aware, TTS-E",Toast.LENGTH_SHORT).show();
                        }
                    };
                    textToSpeechSystem.setOnUtteranceProgressListener(utteranceProgressListener);
                    textToSpeechSystem.setSpeechRate(.9f);
                    String mUtteranceId=NotificationID.getID()+"";
                    String currentTime = new SimpleDateFormat("HH mm a", Locale.getDefault()).format(new Date());
                    textToSpeechSystem.speak(currentTime, TextToSpeech.QUEUE_FLUSH, null,mUtteranceId );
                }
            }
        });
        Toast.makeText(context,"Focus Aware",Toast.LENGTH_SHORT).show();
        return Result.success();
    }
}
