package upt.http.net;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;


public interface Gateway {
	
	/**
	 * Submit a GET request
	 * @param request
	 * @param handler to handle response and cache
	 * @param callback to listen for result
	 */
	public <S, F> void getRequest(Request request, GatewayHandler<S, F> handler, Callback<S, F> callback);

	/**
	 * Submit a POST request
	 * 
	 * @param request
	 * @param handler to handle response and cache
	 * @param callback to listen for result
	 */
	public <S, F> void postRequest(PostRequest request, GatewayHandler<S, F> handler, Callback<S, F> callback);

	/**
	 * Open a GET connection.  This method mainly to facilitate testing 
	 * 
	 * @param url
	 * @param request
	 * @return
	 */
	public URLConnection openGetConnection(URL url, Request request) throws IOException;

	
	/**
	 * Open a POST connection.  This method mainly to facilitate testing 
	 * 
	 * @param url
	 * @param request
	 * @return
	 */
	public URLConnection openPostConnection(URL url, PostRequest request) throws IOException;

	/**
	 * Add Thread.Data handler.
	 * 
	 * @param privider
	 */
	public void addThreadDataHandler(Thread.DataHandler handler);

	/**
	 * Remove Thread.Data handler.
	 * 
	 * @param privider
	 */
	public void removeThreadDataHandler(Thread.DataHandler handler);
	
	/**
	 * Data to be passed from the caller (most probably UI) thread to the worker (http request) thread
	 *
	 */
	public static interface Thread {
		public static interface Data {
		}
		
		public static interface DataHandler {
			public Data getDataInCallerThread();
			public void setDataInWorkerThread(Data data);
		}
	}
}
