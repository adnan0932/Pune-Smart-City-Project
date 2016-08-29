package upt.http.async;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;


/**
 * Provides the common base class for all runtime exceptions within the SDK.  These exception are created as
 * necessary and provided back to higher layers through the {@link AsyncListener} interface.
 *
 * @see com.mcdonalds.sdk.AsyncListener
 */
public class AsyncException extends RuntimeException {

    public static final String NOTIFICATION_ASYNC_ERROR = "NOTIFICATION_ASYNC_ERROR";

    public AsyncException() {
    }

    public AsyncException(String detailMessage) {
        super(detailMessage);
    }

    public AsyncException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    /**
     * Provides a mechanism to report an AsyncException on a new thread, allowing
     * the current thread to continue and giving the default uncaught exception
     * handler an opportunity to take action off of the main thread
     *
     * @param detailMessage message to report
     */
    public static void report(final String detailMessage) {
        if (!TextUtils.isEmpty(detailMessage)) {
            // TODO
        	//report(new AsyncException(detailMessage));
        }
    }

    /**
     * Provides a mechanism to report an AsyncException on a new thread, allowing
     * the current thread to continue and giving the default uncaught exception
     * handler an opportunity to take action off of the main thread
     *
     * @param exception exception to report
     */
    /*public static void report(final AsyncException exception) {

        if (exception != null) {

            final String localizedMessage = exception.getLocalizedMessage();

            if (Configuration.getSharedInstance().getBooleanForKey("log.logsToConsole")) {
                Log.e(AsyncException.class.getSimpleName(), localizedMessage, exception);
            }

            if (!TextUtils.isEmpty(localizedMessage)) {

                final Bundle extras = new Bundle();
                extras.putString(NotificationCenter.EXTRA_MESSAGE, localizedMessage);

                final NotificationCenter nc = NotificationCenter.getSharedInstance();
                if (nc != null) {
                    nc.postNotification(NOTIFICATION_ASYNC_ERROR, extras);
                }
            }
        }
    }*/
}