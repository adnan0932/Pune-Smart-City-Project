package upt.http.net;

import java.util.List;

import org.apache.http.NameValuePair;

public class CacheRequest extends Request {
	
	public CacheRequest() {
		setUrlString("cache-request");
	}
	
	@Override
	final public RequestType getType() { 
		return RequestType.DATABASE; 
	}

	@Override
	final public String getBasePath() {
		return null;
	}
	
	@Override
	final public List<NameValuePair> getQueryParams()  { 
		return null; 
	}
}
