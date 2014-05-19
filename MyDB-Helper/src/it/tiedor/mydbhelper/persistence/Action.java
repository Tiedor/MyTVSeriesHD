package it.tiedor.mydbhelper.persistence;

import java.io.Serializable;
import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

public class Action implements Serializable{

	private static final long serialVersionUID = 2268761949840618686L;

	public static final int STARTDOWNLOAD = 1;
	public static final int ENDDOWNLOAD = 2;
	public static final int FOUNDSUB = 3;
	public static final int DOWNLOADSUB = 4;
	public static final int STARTVIEW = 5;
	public static final int ENDVIEW = 6;

	@DatabaseField(generatedId=true)
	private long id;

	@DatabaseField(dataType = DataType.DATE)
	private Date insertDate;

	@DatabaseField
	private int actionType;

	@DatabaseField(foreign=true, foreignAutoCreate=true, foreignAutoRefresh=true, maxForeignAutoRefreshLevel=3)
	private Episode episode;

	@DatabaseField
	private String fileSourceName;

	@DatabaseField
	private String fileDestinationName;
	
	public Action(){}
	
	public Action(Date insertDate, int actionType, Episode episode,
			String fileSourceName, String fileDestinationName) {
		super();
		this.insertDate = insertDate;
		this.actionType = actionType;
		this.episode = episode;
		this.fileSourceName = fileSourceName;
		this.fileDestinationName = fileDestinationName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

	public int getActionType() {
		return actionType;
	}

	public void setActionType(int actionType) {
		this.actionType = actionType;
	}

	public Episode getEpisode() {
		return episode;
	}

	public void setEpisode(Episode episode) {
		this.episode = episode;
	}

	public String getFileSourceName() {
		return fileSourceName;
	}

	public void setFileSourceName(String fileSourceName) {
		this.fileSourceName = fileSourceName;
	}

	public String getFileDestinationName() {
		return fileDestinationName;
	}

	public void setFileDestinationName(String fileDestinationName) {
		this.fileDestinationName = fileDestinationName;
	}
}
