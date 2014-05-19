package it.tiedor.mydbhelper.persistence;

import java.util.Calendar;
import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

public class Episode {

	@DatabaseField(id = true)
	private long episodeId;
	
    @DatabaseField(index = true)
    private String episodeName;
	
    @DatabaseField
    private int episode;
    
    @DatabaseField(foreign=true, foreignAutoCreate=true, foreignAutoRefresh=true)
    private Season season;
    
    @DatabaseField
    private String episodeImageUrl;
    
    @DatabaseField
    private String itasaSubUrl;
    
    @DatabaseField(dataType = DataType.DATE)
    private Date airTime;
    
    @DatabaseField
    private boolean view;
    
    @DatabaseField
    private boolean download;
    
    @DatabaseField
    private boolean downloading;
    
    @DatabaseField
    private boolean sub;
    
    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date lastUpdate;
    
    public Episode() {
		// TODO Auto-generated constructor stub
	}

	public Episode(long episodeId, String episodeName, int episode,
			Season season, String episodeImageUrl, String itasaSubUrl, Date airTime) {
		super();
		this.episodeId = episodeId;
		this.episodeName = episodeName;
		this.episode = episode;
		this.season = season;
		this.episodeImageUrl = episodeImageUrl;
		this.airTime = airTime;
		this.itasaSubUrl = itasaSubUrl;
		this.lastUpdate = new Date();
	}

	public long getEpisodeId() {
		return episodeId;
	}

	public void setEpisodeId(long episodeId) {
		this.episodeId = episodeId;
	}

	public String getEpisodeName() {
		return episodeName;
	}

	public void setEpisodeName(String episodeName) {
		this.episodeName = episodeName;
	}

	public int getEpisode() {
		return episode;
	}

	public void setEpisode(int episode) {
		this.episode = episode;
	}

	public Season getSeason() {
		return season;
	}

	public void setSeason(Season season) {
		this.season = season;
	}

	public String getEpisodeImageUrl() {
		return episodeImageUrl;
	}

	public void setEpisodeImageUrl(String episodeImageUrl) {
		this.episodeImageUrl = episodeImageUrl;
	}

	public String getItasaSubUrl() {
		return itasaSubUrl;
	}

	public void setItasaSubUrl(String itasaSubUrl) {
		this.itasaSubUrl = itasaSubUrl;
	}

	public Date getAirTime() {
		return airTime;
	}

	public void setAirTime(Date airTime) {
		this.airTime = airTime;
	}

	public boolean isView() {
		return view;
	}

	public void setView(boolean view) {
		this.view = view;
	}

	public boolean isDownload() {
		return download;
	}

	public boolean isDownloading() {
		return downloading;
	}

	public void setDownloading(boolean downloading) {
		this.downloading = downloading;
	}

	public void setDownload(boolean download) {
		this.download = download;
	}

	public boolean isSub() {
		return sub;
	}

	public void setSub(boolean sub) {
		this.sub = sub;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
}
