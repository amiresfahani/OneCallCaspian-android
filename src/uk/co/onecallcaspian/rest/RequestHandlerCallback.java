package uk.co.onecallcaspian.rest;

import org.apache.http.HttpResponse;

public interface RequestHandlerCallback<T> {
	public void requestHandlerDone(T data);
	public void requestHandlerError(String reason);
}
