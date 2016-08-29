package upt.http.net;

public abstract class PostRequest extends Request {
	public String getBody() {
		return null;
	}
	
	public String getContentType() {
		return "application/json";
	}
}
