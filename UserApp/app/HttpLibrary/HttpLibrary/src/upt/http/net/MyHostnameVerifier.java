package upt.http.net;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class MyHostnameVerifier implements HostnameVerifier {

	/**
	 * Verifies that the specified hostname is allowed within the specified SSL
	 * session.
	 * 
	 * @param hostname
	 *            the hostname
	 * @param session
	 *            the session
	 * @return true, if successful
	 */
	@Override
	public final boolean verify(final String hostname, final SSLSession session) {
		return true;
	}

}
