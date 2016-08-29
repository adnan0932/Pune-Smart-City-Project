import upt.http.httpservice.SampleService;
import upt.http.net.ActivityRequestController;
import upt.http.net.Callback;
import upt.http.net.GatewayError;
import upt.http.net.RequestController;
import android.app.Activity;
import android.os.Bundle;


public class SampleActivity extends Activity implements RequestController.Provider  {

	private ActivityRequestController requestController = new ActivityRequestController();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// service call
		SampleService.getInstance().callThisMethod("someData", new Callback<String, GatewayError>() {

			@Override
			public void onSuccess(String successObj) {
				// ta daa .. got the result
				
			}

			@Override
			public void onFailure(GatewayError failureObj) {
				// opps something failed
			}

			@Override
			public void onRequestSend() {
				super.onRequestSend();
			}

			@Override
			public void onCancel() {
				super.onCancel();
			}

			@Override
			public void onComplete() {
				super.onComplete();
			}

			@Override
			public RequestController getRequestController() {
				return requestController;
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		requestController.onStart(this);
	}
	
	@Override
	protected void onPause() {
		requestController.onPause(this);
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		requestController.onStop(this);
		super.onStop();
	}
	@Override
	public RequestController getRequestController() {
		return requestController;
	}
}
