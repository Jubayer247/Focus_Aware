package help.codered.focusaware;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

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

        // Indicate whether the work finished successfully with the Result
        return Result.success();
    }
}
