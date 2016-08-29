package upt.http.net;

import java.util.List;

import org.apache.http.NameValuePair;

public abstract class Request {

	private String urlString;

	public RequestType getType() { 
		return RequestType.LOCAL; 
	}
	
	// return request base URL 
	public abstract String getBasePath();
	
	public List<NameValuePair> getQueryParams()  { 
		return null; 
	}
	
	final public String getUrlString()  { 
		return urlString; 
	}
	
	void setUrlString(String urlString) {
		this.urlString = urlString;
	}
}
