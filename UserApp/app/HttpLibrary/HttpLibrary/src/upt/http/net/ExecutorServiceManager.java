package upt.http.net;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceManager {

	private Map<RequestType, ExecutorService> executorServiceMap = new HashMap<RequestType, ExecutorService>();
	
	static private ExecutorServiceManager gManager;
	
	private ExecutorServiceManager() {}
	
	static public synchronized ExecutorServiceManager getInstance() {
		if (gManager == null) {
			gManager = new ExecutorServiceManager();
		}
		
		return gManager;
	}
	
	public synchronized ExecutorService get(RequestType requestType) {
		if (requestType == null)
			requestType = RequestType.LOCAL;
		
		ExecutorService service = executorServiceMap.get(requestType);
		if (service == null) {
			service = Executors.newFixedThreadPool(requestType.getThreadPoolSize());
			executorServiceMap.put(requestType, service);
		}
		
		return service;
	}
}
