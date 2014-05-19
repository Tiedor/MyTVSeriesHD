package it.tiedor.mytvserieshd.exception;

public class MyNoNeedToSyncException extends Exception {

	public MyNoNeedToSyncException(String message){
		super(message);
	}
	
	public MyNoNeedToSyncException(String message, Throwable e){
		super(message, e);
	}
}
