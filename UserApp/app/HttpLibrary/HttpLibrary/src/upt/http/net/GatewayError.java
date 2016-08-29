package upt.http.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import upt.http.util.Logs;

public class GatewayError {

	private final static String LOG_TAG = GatewayError.class.getSimpleName();
	
	private int statusCode;
	private Exception cause;

	static public GatewayError make(int statusCode, InputStream inputStream) {
		String msg;
		try {
			msg = inputStreamToString(inputStream);
		} catch (IOException e) {
			msg = null;
			Logs.error(LOG_TAG, "fail to retrieve error string");
		}
		
		return new GatewayError(statusCode, new Exception(statusCode + " "+ msg));
	}
	
	public GatewayError(int statusCode, Exception exception) {
		super();
		this.statusCode = statusCode;
		this.cause = exception;
	}

	public int getStatusCode() {
		return statusCode;
	}
	
	public Exception getCause() {
		return cause;
	}
	
    static private String inputStreamToString(InputStream orig) throws IOException {
        BufferedReader bufferedReader = null;
        try {
            StringBuilder sb = new StringBuilder();
            bufferedReader = new BufferedReader(new InputStreamReader(orig), 8192);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            
            return sb.toString();
            
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
    }
}
