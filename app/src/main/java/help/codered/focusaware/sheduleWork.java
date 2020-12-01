package help.codered.focusaware;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static help.codered.focusaware.MainActivity.r;

public class sheduleWork extends Worker {
    public sheduleWork(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {

        // Do the work here--in this case, upload the images.
       // uploadImages();
        r.play();
//        Button btn_stop_alarm=(Button) ((Activity)getApplicationContext()).findViewById(R.id.btn_stop_alarm);
//        btn_stop_alarm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(r.isPlaying()){
//                    r.stop();
//                }
//            }
//        });
        // Indicate whether the work finished successfully with the Result
        return Result.success();
    }
}
