package upt.http.httpservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import upt.http.net.Callback;
import upt.http.net.GatewayError;
import upt.http.net.GatewayHandler;
import upt.http.net.HttpGateway;
import upt.http.net.PostRequest;
import upt.http.net.RequestType;
import upt.http.util.Constants;


public class SampleService {
final static String LOG_TAG = SampleService.class.getSimpleName();
	
	private static SampleService gService;
	
	private SampleService() {
	}
	
	public static synchronized SampleService getInstance() {
		if (gService == null) {
			gService = new SampleService();
		}
		
		return gService;
	}
	
	/**
	 * Sample method to be called and attach a call back rom where it is called
	 * 
	 * 
	 */
	
	public void callThisMethod(String someData, Callback<String, GatewayError> callback) {		
		
		SampleRequest request = new SampleRequest(someData);
		SampleRequestResponseHandler handler = new SampleRequestResponseHandler();
		
		HttpGateway.getInstance().getRequest(request, handler, callback);
	}
}

class SampleRequest extends PostRequest  {
	
	String someData;
	
	SampleRequest(String someData) {
		this.someData = someData;
	}
	
	public RequestType getType() { 
		return RequestType.API; 
	}
	
	public String getBasePath()  {
		return "http://www.google.com";
	}
	
	/**
	 * Attach params in the request
	 */
	
	public List<NameValuePair> getQueryParams()  {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		// bind data with request
		// request can be post or get
		// someData......
		
		return params;
	}
}

class SampleRequestResponseHandler implements GatewayHandler<String, GatewayError> {
	
	@Override
	public String parseRequestSuccess(InputStream inputStream) throws IOException {
		
		try {
						
			InputStreamReader in = new InputStreamReader(inputStream);
			StringBuilder results = new StringBuilder();
	        // Load the results into a StringBuilder
	        int read;
	        char[] buff = new char[1024];
	        while ((read = in.read(buff)) != -1) {
	        	results.append(buff, 0, read);
	        }

	        return results.toString();
		} catch (UnsupportedEncodingException e) {
			throw e;
		}
	}

	@Override
	public GatewayError parseRequestFailure(InputStream inputStream) throws IOException {
		GatewayError error = GatewayError.make(Constants.SERVER_ERROR_WITH_MESSAGE, inputStream);
		return error;
	}

	@Override
	public GatewayError translateGatewayError(GatewayError error) {
		return error;
	}

	@Override
	public String getCachedData() {
		return null;
	}

	@Override
	public void setCachedData(String searchResult) {
	}
}