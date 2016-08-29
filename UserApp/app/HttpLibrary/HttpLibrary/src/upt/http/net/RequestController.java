package upt.http.net;

public class RequestController {
	
	private volatile boolean canceled;
	
	public void resume() {
		this.canceled = false;
	}
	
	public void cancel() {
		this.canceled = true;
	}
	
	public boolean isCanceled() { 
		return this.canceled; 
	}
	
	static public interface Provider {
		public RequestController getRequestController();
	}
}
