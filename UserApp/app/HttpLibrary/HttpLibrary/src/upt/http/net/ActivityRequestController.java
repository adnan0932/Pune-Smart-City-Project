package upt.http.net;

import android.app.Activity;

public class ActivityRequestController extends RequestController {
	
	public void onStart(Activity activity) {
		resume();
	}
	
	public void onPause(Activity activity) {
		if (activity.isFinishing())
			cancel();
	}

	public void onStop(Activity activity) {
		cancel();
	}
}
