package upt.http.net;

import upt.application.MainApplication;
import android.os.Handler;

public class UiCallback<S, F> extends Callback<S, F> {
	// Pass the application's context
	Handler handler = MainApplication.getInstance().getHandler();
	Callback<S, F> wrappee;
	
	public UiCallback(Callback<S, F> wrappee) {
		this.wrappee = wrappee;
	}
	
	public void onSuccess(final S successObj) {
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				if (!isCanceled())
					wrappee.onSuccess(successObj);
				
			}
		});
	}
	
	public void onFailure(final F failureObj) {
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				if (!isCanceled())
					wrappee.onFailure(failureObj);
				
			}
		});
	}
	
	public void onRequestSend() {
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				if (!isCanceled())
					wrappee.onRequestSend();
			}
		});
	}
	
	public void onCancel() {
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				// no need to check cancellation for onCancel event
				wrappee.onCancel();
			}
		});
		
	}
	
    public void onComplete() {
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				if (!isCanceled())
					wrappee.onComplete();
				else	// inform cancel if haven't done so upon the last callback event
					wrappee.onCancel();		 
			}
		});
    	
    }
    	
    public RequestController getRequestController() { 
		return wrappee.getRequestController(); 
	}
    
    private boolean isCanceled() {
    	RequestController controller = getRequestController();
    	return controller != null && controller.isCanceled();
    }
}
