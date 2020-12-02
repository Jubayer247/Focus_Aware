package help.codered.focusaware;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static help.codered.focusaware.MainActivity.r;
import static help.codered.focusaware.R.string.uuid_for_worker;

public class sheduleWork extends Worker {
    private Context context;

    private TextToSpeech textToSpeechSystem;

    @Override
    public void onStopped() {
        super.onStopped();
        SharedPreferences sharedPreferences=context.getSharedPreferences(context.getString(uuid_for_worker),Context.MODE_PRIVATE );
        Editor editor=sharedPreferences.edit();
        editor.putBoolean("is_focus_aware_running",false);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,"Focus Aware, Stopped",Toast.LENGTH_SHORT).show();
            }
        });
    }

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
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context,"Focus Aware, TTS-S",Toast.LENGTH_SHORT).show();
                                }
                            });


                        }

                        @Override
                        public void onDone(String utteranceId) {
                            r.play();
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context,"Focus Aware, TTS-D",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onError(String utteranceId) {
                            r.play();
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context,"Focus Aware, TTS-E",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    };

                    SharedPreferences sharedPreferences= context.getSharedPreferences(context.getString(uuid_for_worker),Context.MODE_PRIVATE );
                    String value=sharedPreferences.getString("msg","");

                    textToSpeechSystem.setOnUtteranceProgressListener(utteranceProgressListener);
                    textToSpeechSystem.setSpeechRate(.9f);
                    String mUtteranceId=NotificationID.getID()+"";
                    String currentTime = new SimpleDateFormat("HH mm a", Locale.getDefault()).format(new Date());
                    if(!(value.equalsIgnoreCase("") || value.equalsIgnoreCase("NA"))){
                        currentTime+="   "+value;
                    }
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context,"Focus Aware",Toast.LENGTH_SHORT).show();
                        }
                    });
                    textToSpeechSystem.speak(currentTime, TextToSpeech.QUEUE_FLUSH, null,mUtteranceId );
                }
            }
        });
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,"Focus Aware",Toast.LENGTH_SHORT).show();
            }
        });


        return Result.success();
    }
}
