package upt.http.net;

import java.util.List;

import org.apache.http.NameValuePair;

public class LocalRequest extends Request {
	
	public LocalRequest() {
		setUrlString("local-request");
	}
	
	@Override
	final public RequestType getType() { 
		return RequestType.LOCAL; 
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
