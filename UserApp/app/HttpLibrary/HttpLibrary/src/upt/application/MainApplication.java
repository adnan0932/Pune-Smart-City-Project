package upt.application;

import android.app.Application;
import android.os.Handler;

public class MainApplication extends Application {
	/** The application context. */
	private static MainApplication gApplication;

	/** The handler. */
	private Handler handler = new Handler();

	public MainApplication() {
		gApplication = this;
	}
	
	public static MainApplication getInstance() {
		return gApplication;
	}

	public Handler getHandler() {
		return this.handler;
	}
}
