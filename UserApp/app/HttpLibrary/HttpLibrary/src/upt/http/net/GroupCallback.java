package upt.http.net;

import java.util.HashSet;
import java.util.Set;

public class GroupCallback<S, F> extends Callback<S, F> {

	private Set<Callback<S, F>> callbackSet = new HashSet<Callback<S, F>>();
	private boolean onRequestSendOccured;
	
	private GroupRequestController groupRequestController = new GroupRequestController();
	
	public synchronized void addCallback(Callback<S, F> c) {
		callbackSet.add(c);
		
		if (onRequestSendOccured)
			c.onRequestSend();
	}

	private synchronized Set<Callback<S, F>> cloneList() {
		return new HashSet<Callback<S,F>>(callbackSet);
	}

	private Callback<S, F> handleCanceled(Callback<S, F> c) {
		RequestController controller = c.getRequestController();
		boolean canceled = (controller != null && controller.isCanceled());
		
		if (canceled) {
			c.onCancel();
			synchronized (callbackSet) {
				callbackSet.remove(c);
			}
			return null;
		}
		
		return c;
	}
	
	@Override
	public void onSuccess(S successObj) {
		for (Callback<S, F> c : cloneList()) {
			Callback<S, F> _c = handleCanceled(c);
			if (_c != null)
				_c.onSuccess(successObj);
		}
	}

	@Override
	public void onFailure(F failureObj) {
		for (Callback<S, F> c : cloneList()) {
			Callback<S, F> _c = handleCanceled(c);
			if (_c != null)
				_c.onFailure(failureObj);
		}
	}

	@Override
	public void onRequestSend() {
		onRequestSendOccured = true;
		for (Callback<S, F> c : cloneList()) {
			Callback<S, F> _c = handleCanceled(c);
			if (_c != null)
				_c.onRequestSend();
		}
	}

	@Override
	public void onCancel() {
		for (Callback<S, F> c : cloneList())
			c.onCancel();
		
		onRequestSendOccured = false;
		synchronized (callbackSet) {
			callbackSet.clear();
		}
	}

	@Override
	public void onComplete() {
		for (Callback<S, F> c : cloneList()) {
			Callback<S, F> _c = handleCanceled(c);
			if (_c != null)
				_c.onComplete();
		}
		
		onRequestSendOccured = false;
		
		synchronized (callbackSet) {
			callbackSet.clear();
		}
	}

	@Override
	public RequestController getRequestController() {
		return groupRequestController;
	}
	
	class GroupRequestController extends RequestController {

		@Override
		public boolean isCanceled() {
			boolean allCanceled = true;
			for (Callback<S, F> c : cloneList()) {
				Callback<S, F> _c = handleCanceled(c);
				if (_c != null) {
					allCanceled = false;
				}
			}
			
			return allCanceled;
		}
	}
}
