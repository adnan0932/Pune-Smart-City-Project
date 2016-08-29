package upt.http.net;

public class GatewayRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GatewayRuntimeException() {
	}

	public GatewayRuntimeException(String detailMessage) {
		super(detailMessage);
	}

	public GatewayRuntimeException(Throwable throwable) {
		super(throwable);
	}

	public GatewayRuntimeException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}
}
