package upt.http.util;


import android.util.Log;

public class Logs {

	/**
	 * Log.
	 */
	public Logs() {
	}

	/** The Constant show. */
	private static final boolean show = true;

	/**
	 * Show log of type error.
	 * 
	 * @param tag
	 *            the tag
	 * @param string
	 *            the string
	 */
	public static void error(final String tag, final String string) {
		if (show) {
			Log.e(tag, string);
		}
	}

	public static void error(final String tag, final String string, Exception cause) {
		if (show) {
			Log.e(tag, string, cause);
		}
	}
	
	/**
	 * Show log of type information.
	 * 
	 * @param tag
	 *            the tag
	 * @param string
	 *            the string
	 */
	public static void info(final String tag, final String string) {
		if (show) {
			Log.d(tag, string);
		}
	}
}

