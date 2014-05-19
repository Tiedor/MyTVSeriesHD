package it.tiedor.mytvserieshd.exception;

public class MyMovingFileException extends Exception {

	public MyMovingFileException(String message){
		super(message);
	}
	
	public MyMovingFileException(String message, Throwable e){
		super(message, e);
	}
}
