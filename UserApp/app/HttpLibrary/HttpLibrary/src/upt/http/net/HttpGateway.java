package upt.http.net;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpException;
import org.apache.http.NameValuePair;

import upt.application.MainApplication;
import upt.http.util.Constants;
import upt.http.util.Logs;
import upt.http.util.Strings;
import upt.httplibrary.BuildConfig;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class HttpGateway implements Gateway {

	private final static String LOG_TAG = HttpGateway.class.getSimpleName();
	
	static private Gateway gGateway;

    private Set<Thread.DataHandler> threadDataHandlers = new HashSet<Thread.DataHandler>();

	private HttpGateway() {
		HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
	}
	
	static public synchronized Gateway getInstance() {
		if (gGateway == null) {
			gGateway = new HttpGateway();
		}
		
		return gGateway;
	}
	
	/**
	 * Mainly to facilitate testing
	 * @param gateway
	 */
	static public synchronized void setInstance(Gateway gateway) {
		gGateway = gateway;
	}

	@Override
	public <S, F> void getRequest(final Request request, final GatewayHandler<S, F> handler, final Callback<S, F> callback) {
		doRequest(request, handler, callback);
	}

	@Override
	public <S, F> void postRequest(final PostRequest request, final GatewayHandler<S, F> handler, final Callback<S, F> callback) {
		doRequest(request, handler, callback);
	}

	private <S, F> void doRequest(final Request request, final GatewayHandler<S, F> handler, final Callback<S, F> callback) {
		if (informRequestCanceled(callback))
			return;
 		
		final Map<Thread.DataHandler, Thread.Data> dataMap = new HashMap<Thread.DataHandler, Thread.Data>();
		for (Thread.DataHandler h : getThreadDataHandlers()) {
			dataMap.put(h, h.getDataInCallerThread());
		}
		
		ExecutorServiceManager.getInstance().get(request.getType()).execute(new Runnable() {
			
			@Override
			public void run() {
				for (Thread.DataHandler h : dataMap.keySet()) {
					h.setDataInWorkerThread(dataMap.get(h));
				}
				
				doRequestAsync(request, handler, callback);
			}
		});
	}
	
	private <S, F> void doRequestAsync(Request request, GatewayHandler<S, F> handler, Callback<S, F> callback) {
		
		final UiCallback<S, F> uiCallback = new UiCallback<S, F>(callback);
		
		// check abort at start of thread run
		if (informRequestCanceled(uiCallback))
			return;
		
		try {
			if (returnCachedData(handler, uiCallback)) {
				Logs.info(LOG_TAG, "getRequest - return cached data by : " + handler.toString());
				return;
			}
		} catch (RuntimeException e) {
			Logs.error(LOG_TAG, "doRequestAsync - returnCachedData", e);
			informFailure(uiCallback, handler.translateGatewayError(new GatewayError(Constants.CLIENT_ERROR_WITH_MESSAGE, e)));
            return;
		}

		// check again after cache check
		if (informRequestCanceled(uiCallback))
			return;

		if (!isInternetAvailable()) {
			Logs.error(LOG_TAG, "getRequest - no internet");
			informFailure(uiCallback, handler.translateGatewayError(new GatewayError(Constants.NO_INTERNET, null)));
            return;
		}

		// inform request send
		uiCallback.onRequestSend();
		
		boolean succeeded = false;
		S successData = null;
		HttpURLConnection urlConnection = null;
		InputStream logInputStream = null;
		
		try {
			URL url = new URL(request.getBasePath() + formOueryParams(request.getQueryParams()));
			request.setUrlString(url.toString());
			
			if (request instanceof PostRequest) {
				Logs.info(LOG_TAG, "getRequest - POST url : " + request.getUrlString());
				PostRequest postRequest = (PostRequest)request;
				urlConnection = (HttpURLConnection) openPostConnection(url, postRequest);

				// check after open connection
				if (informRequestCanceled(uiCallback))
					return;
				
				urlConnection.setDoOutput(true);
				urlConnection.setRequestMethod("POST");
				
				String postBody = postRequest.getBody();
				if (Strings.isNotEmpty(postBody)) {
					
					String contentType = postRequest.getContentType();
					if (Strings.isNotEmpty(contentType)) {
						Logs.info(LOG_TAG, "getRequest - POST content type : " + contentType);
						urlConnection.setRequestProperty("Content-Type", contentType);
					}
					
					Logs.info(LOG_TAG, "getRequest - POST body : " + postBody);
					urlConnection.getOutputStream().write(postBody.getBytes());
					urlConnection.getOutputStream().flush();
					
					// check after post body
					if (informRequestCanceled(uiCallback))
						return;
				}
				
			} else {
				Logs.info(LOG_TAG, "getRequest - GET url : " + request.getUrlString());
				urlConnection = (HttpURLConnection) openGetConnection(url, request);
				
				// check after open connection
				if (informRequestCanceled(uiCallback))
					return;
			}
			
			int httpResponseCode = urlConnection.getResponseCode();
			
			GatewayError gatewayError = Errors.makeGatewayError(httpResponseCode);
			
			if (gatewayError != null) {
				Logs.error(LOG_TAG, "getRequest: http response code = " + httpResponseCode + 
						", status code = " + gatewayError.getStatusCode());
				informFailure(uiCallback, handler.translateGatewayError(gatewayError));
				return;
			}
			
			InputStream inputStream;
			if (httpResponseCode == Constants.HTTP_STATUS_401 || httpResponseCode == Constants.HTTP_STATUS_403 ||
					httpResponseCode == Constants.HTTP_STATUS_400) {
				inputStream = urlConnection.getErrorStream();
				
				Logs.error(LOG_TAG, "getRequest: http response code = " + httpResponseCode + " and return error stream");
				informFailure(uiCallback, handler.parseRequestFailure(inputStream));
				return;
			} 
			
			// check before parsing and caching the data
			if (informRequestCanceled(uiCallback))
				return;
			
			Logs.info(LOG_TAG, "getRequest: http response code = " + httpResponseCode + " and return data stream");
			inputStream = urlConnection.getInputStream();
			
			// parse data
			if (request.getType() == RequestType.API) {
				logInputStream = logResponse(request.getUrlString(), inputStream);
				successData = handler.parseRequestSuccess(logInputStream);
			} else
				successData = handler.parseRequestSuccess(inputStream);
			
			// cache data
			handler.setCachedData(successData);
			
			// check before returning the data
			if (informRequestCanceled(uiCallback))
				return;
			
			succeeded = true;
			
		} catch (UnknownHostException e) {
			Logs.error(LOG_TAG, "getRequest", e);
			informFailure(uiCallback, handler.translateGatewayError(new GatewayError(Constants.UNKNOWN_HOST_EXCEPTION, e)));
			
		} catch (IOException e) {
			Logs.error(LOG_TAG, "getRequest", e);
			informFailure(uiCallback, handler.translateGatewayError(new GatewayError(Constants.IOEXCEPTION, e)));
			
		} catch (RuntimeException e) {
			String message = e.getMessage();
			if (message != null && message.contains("authentication challenge")) {
				Logs.error(LOG_TAG, "getRequest - authentication challenge", e);
				informFailure(uiCallback, handler.translateGatewayError(new GatewayError(Constants.HTTP_UNAUTHORIZED_CONN, e)));
			} else {
				Logs.error(LOG_TAG, "getRequest", e);
				informFailure(uiCallback, handler.translateGatewayError(new GatewayError(Constants.HTTP_GENERIC_EXCEPTION, e)));
			}
			
		} finally {
			
			if (urlConnection != null)
				urlConnection.disconnect();
			
			if (logInputStream != null) {
				try {
					logInputStream.close();
				} catch (IOException e) {
					Logs.error(LOG_TAG, "getRequest - close logInputStream", e);
				}
			}
		}
		
		// inform success after closing the connection 
		if (succeeded) {
        	informSuccess(uiCallback, successData);
		}
	}
	
	@Override
	public URLConnection openGetConnection(URL url, Request request) throws IOException {
		return url.openConnection();
	}

	@Override
	public URLConnection openPostConnection(URL url, PostRequest request) throws IOException {
		return url.openConnection();
	}
	
	private <S, F> boolean returnCachedData(GatewayHandler<S, F> handler, Callback<S, F> callback) {
		S cachedData = handler.getCachedData();
		if (cachedData == null)
			return false;
		
		callback.onSuccess(cachedData);
		callback.onComplete();
		return true;
	}
	
	static private <S, F> boolean informRequestCanceled(Callback<S, F> callback) {
		RequestController requestController = callback.getRequestController();
		if (requestController != null && requestController.isCanceled()) {
			Logs.info(LOG_TAG, "getRequest - request canceled");
			callback.onCancel();
			return true;
		}
		
		return false;
	}

	static public <S, F> void informFailure(Callback<S, F> callback, F error) {
		if (informRequestCanceled(callback))
			return;
		
		callback.onFailure(error);
		callback.onComplete();
	}

	static public <S, F> void informSuccess(Callback<S, F> callback, S success) {
		if (informRequestCanceled(callback))
			return;
		
		callback.onSuccess(success);
		callback.onComplete();
	}
	
	
	private String formOueryParams(List<NameValuePair> paramList) {
		if (paramList == null || paramList.size() <= 0)
			return "";

		StringBuilder buf = null; 
		for (NameValuePair pair : paramList) {
			if (buf == null)
				buf = new StringBuilder("?");
			else 
				buf.append("&");
			
			buf.append(pair.getName()).append("=").append(pair.getValue());
		}
		
		return buf.toString();
	}

	private boolean isInternetAvailable() {
		MainApplication app = MainApplication.getInstance();
		ConnectivityManager cm = (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			// checking for active network info
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
			if (networkInfo != null) {
				return networkInfo.isConnectedOrConnecting();
			}
		}
		
		return false;
	}
	
	static private final int MESSAGE_LIMIT = 3072;	// 1024 x 3
    private InputStream logResponse(String reqString, InputStream orig) throws IOException {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(orig), 8192);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            
            String response = sb.toString();
            
            int length = response.length();
            StringBuilder jsonBuilder = new StringBuilder();
            for (int i = 0; i < length; i += MESSAGE_LIMIT) {
            	String msg;
                if (i + MESSAGE_LIMIT < length)
                	msg = response.substring(i, i + MESSAGE_LIMIT);
                else
                	msg = response.substring(i, length);
                	jsonBuilder.append(msg);
	                Logs.info(LOG_TAG, msg);
            }            
            byte[] bytes = response.getBytes();
            InputStream inputStream = new ByteArrayInputStream(bytes);
            return inputStream;
            
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
    }
    
	@Override
	public synchronized void addThreadDataHandler(Thread.DataHandler handler) {
        if (handler != null) 
            this.threadDataHandlers.add(handler);
	}

	@Override
	public synchronized void removeThreadDataHandler(Thread.DataHandler handler) {
        if (handler != null) 
            this.threadDataHandlers.remove(handler);
	}
	
	private synchronized Thread.DataHandler[] getThreadDataHandlers() {
        return this.threadDataHandlers.toArray(new Thread.DataHandler[this.threadDataHandlers.size()]);
    }
}

