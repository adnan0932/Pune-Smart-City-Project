package upt.http.net;

public class Callback<S, F> {
	
	private RequestController requestController;
	
	public Callback() {
	}
	
	public Callback(RequestController requestController) {
		this.requestController = requestController;
	}
	
	public void onSuccess(S successObj) {
	}
	
	public void onFailure(F failureObj) {
	}
	
	// call before the http request is sent
	public void onRequestSend() {
	}
	
	// call if request is cancelled; onComplete() will NOT be called.
	public void onCancel() {} {
	}
	
	// call after onSuccess()/onFailure()
    public void onComplete() {
    }
    	
	public RequestController getRequestController() { 
		return requestController; 
	}
	
    /**
     * Chain a callback.
     * 
     * @param next callback to be chained.
     * 
     * @return
     */
    final public Callback<S, F> andThenCall(Callback<S, F> nextCallback) {
    	
        return new LinkCallback<S, F>(this, nextCallback);
    }
    
    static class LinkCallback<S, F> extends Callback<S, F> {
        
        private Callback<S, F> current;
        private Callback<S, F> next;

        LinkCallback(Callback<S, F> currentCallback, Callback<S, F> nextCallback) {
            this.current = currentCallback;
            this.next = nextCallback;
        }
        
        @Override
        public void onSuccess(S successObject) {
            this.current.onSuccess(successObject);
            this.next.onSuccess(successObject);
        }

        @Override 
        public void onFailure(F failureObject) {
            this.current.onFailure(failureObject);
            this.next.onFailure(failureObject);
        }

        @Override
        public void onComplete() {
            this.current.onComplete();
            this.next.onComplete();
        }
        
        @Override
        public void onRequestSend() {
            this.current.onRequestSend();
            this.next.onRequestSend();
        }
        
        @Override
        public void onCancel() {
            this.current.onCancel();
            this.next.onCancel();
        }

		@Override
		public RequestController getRequestController() {
			return this.current.getRequestController();
		}
    }
}
