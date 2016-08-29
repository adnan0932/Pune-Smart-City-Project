package upt.http.net;


public enum RequestType {
	
	API(3), DATABASE(2), IMAGE(5), LOCAL(3);
	
    int threadPoolSize;
    
    RequestType(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }
	
    public int getThreadPoolSize() {
    	return threadPoolSize;
    }
}
