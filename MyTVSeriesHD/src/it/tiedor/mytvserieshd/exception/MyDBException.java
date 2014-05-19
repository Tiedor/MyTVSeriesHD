package it.tiedor.mytvserieshd.exception;

public class MyDBException extends Exception {

	public MyDBException(String message){
		super(message);
	}
	
	public MyDBException(String message, Throwable e){
		super(message, e);
	}
}
