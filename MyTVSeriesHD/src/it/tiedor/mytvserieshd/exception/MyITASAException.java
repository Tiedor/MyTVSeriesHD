package it.tiedor.mytvserieshd.exception;

public class MyITASAException extends Exception {

	public MyITASAException(String message){
		super(message);
	}
	
	public MyITASAException(String message, Throwable e){
		super(message, e);
	}
}
