package upt.http.net;

import java.io.IOException;
import java.io.InputStream;

/**
 * NOTE:
 * 
 * All callback API: parseRequestSuccess(), parseRequestFailure(), informError(), getCachedData(), setCachedData()
 * are invoked on NON main/UI thread.
 *  
 */
public interface GatewayHandler<S, F> {
	
	public S parseRequestSuccess(InputStream inputStream) throws IOException;
	
	public F parseRequestFailure(InputStream inputStream) throws IOException;
	
	public F translateGatewayError(GatewayError error);
	
	public S getCachedData();
	
	public void setCachedData(S data);
}
