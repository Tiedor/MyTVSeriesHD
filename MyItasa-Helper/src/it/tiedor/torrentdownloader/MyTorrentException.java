package it.tiedor.torrentdownloader;


public class MyTorrentException extends Exception {

	public MyTorrentException(String message){
		super(message);
	}
	
	public MyTorrentException(String message, Throwable e){
		super(message, e);
	}
}
