package upt.http.net;

import upt.http.util.Constants;
import upt.httplibrary.R;

public class Errors {

	static public GatewayError makeGatewayError(int httpResponseCode) {
		
		int statusCode = 0;
		
		switch(httpResponseCode) {
		case -1:
			statusCode = Constants.NO_INTERNET;
			break;

		case 500:
			// Generic "server is confused" message. It is often the result
			// of CGI programs or (heaven forbid!) servlets that crash or
			// return improperly formatted headers.
			statusCode = Constants.INTERNAL_SERVER_ERROR;
			break;
			
		case 501:
			// Server doesn't support functionality to fulfill request.
			// Used, for example, when client issues command like PUT that
			// server doesn't support.
			statusCode = Constants.NOT_IMPLEMENTED;
			break;
			
		case 502:
			// Used by servers that act as proxies or gateways; indicates
			// that initial server got a bad response from the remote
			// server.
			statusCode = Constants.BAD_GATEWAY;
			break;
			
		case 503:
			// Server cannot respond due to maintenance or overloading. For
			// example, a servlet might return this header if some thread or
			// database connection pool is currently full. Server can supply
			// a Retry-After header.
			statusCode = Constants.SERVICE_UNAVAILABLE;
			break;
			
		case 504:
			// Used by servers that act as proxies or gateways; indicates
			// that initial server didn't get a response from the remote
			// server in time. (New in HTTP 1.1)
			statusCode = Constants.GATEWAY_TIMEOUT;
			break;
			
		case 505:
			// Server doesn't support version of HTTP indicated in request
			// line. (New in HTTP 1.1)
			statusCode = Constants.HTTP_VERSION_NOT_SUPPORTED;
			break;
			
		case Constants.HTTP_STATUS_404:
			statusCode = Constants.HTTP_404;
			break;
		}
		
		if (statusCode != 0)
			return new GatewayError(statusCode, null);
		
		return null;
	}
	
	
	public static int getStatusCodeStringId(int statusCode) {
		return getStatusStringId(statusCode, StatusStringType.CODE);
	}
	
	public static int getStatusMessageStringId(int statusCode) {
		return getStatusStringId(statusCode, StatusStringType.MESSAGE);
	}
	
	
	public static int getStatusToastStringId(int statusCode) {
		return getStatusStringId(statusCode, StatusStringType.TOAST);
	}
	
	private static int getStatusStringId(int statusCode, StatusStringType type) {
		switch (statusCode) {
		// server unavailable
		case Constants.HTTP_404:
			switch (type) {
			case CODE:
				return R.string.error_code;
			case MESSAGE:
				return R.string.something_wrong;
			case TOAST:
				return R.string.connection_error;
			}
			
		case Constants.HTTP_EXCEPTION:
		case Constants.HTTP_VERSION_NOT_SUPPORTED:
		case Constants.UNKNOWN_HOST_EXCEPTION:
			switch (type) {
			case CODE:
				return R.string.http_error;
			case MESSAGE:
				return R.string.try_later;
			case TOAST:
				return R.string.connection_error;
			}
			
		case Constants.GATEWAY_TIMEOUT:
		case Constants.INTERNAL_SERVER_ERROR:
		case Constants.BAD_GATEWAY:
		case Constants.HTTP_GENERIC_EXCEPTION:
		case Constants.NO_INTERNET:
			switch (type) {
			case CODE:
				return R.string.http_error;
			case MESSAGE:
				return R.string.check_your_connection;
			case TOAST:
				return R.string.connection_error;
			}
			
		case Constants.HTTP_UNAUTHORIZED_CONN:
			switch (type) {
			case CODE:
				return R.string.authentication_error;
			case MESSAGE:
				return R.string.verify_credentials;
			case TOAST:
				return R.string.verify_credentials;
			}
			
		case Constants.SERVICE_UNAVAILABLE:
		case Constants.NOT_IMPLEMENTED:
			switch (type) {
			case CODE:
				return R.string.server_error;
			case MESSAGE:
				return R.string.try_later;
			case TOAST:
				return R.string.server_unreachable;
			}
			
		case Constants.IOEXCEPTION:
		case Constants.PARSE_EXCEPTION:
			switch (type) {
			case CODE:
				return R.string.parse_error;
			case MESSAGE:
				return R.string.try_later;
			case TOAST:
				return R.string.connection_error;
			}
		}
		
		return -1;
	}
	
	enum StatusStringType {
		CODE, MESSAGE, TOAST 
	}
}
