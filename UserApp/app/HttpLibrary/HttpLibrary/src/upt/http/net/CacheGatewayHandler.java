package upt.http.net;

import java.io.IOException;
import java.io.InputStream;

public abstract class CacheGatewayHandler<S, F> implements GatewayHandler<S, F> {
	
	final public S parseRequestSuccess(InputStream inputStream) throws IOException {
		return null;
	}
	
	final public F parseRequestFailure(InputStream inputStream) throws IOException {
		return null;
	}
	
	public abstract F translateGatewayError(GatewayError error);
	
	public abstract S getCachedData();
	
	final public void setCachedData(S data) {
	}
}
