package it.tiedor.mydbhelper.persistence;

import java.io.Serializable;
import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

public class Serie implements Serializable{

	private static final long serialVersionUID = -6374526116846616443L;
	
	@DatabaseField(id = true)
    long showID;
	@DatabaseField
	Long itasaId;
    @DatabaseField(index = true)
    String showName;
    @DatabaseField
    String showBannerUrl;
    @DatabaseField
    String showPosterUrl;
    @DatabaseField
    String status;
    @DatabaseField
    String dayOfWeekAirs;
    @DatabaseField
    String timeAirs;
    @DatabaseField
    private String network;
    @DatabaseField
    private String overview;
    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date lastUpdate;

    private Episode nextAirEpisode;
    
    public Serie() {}

	public Serie(long showID, String showName, String showBannerUrl,
			String showPosterUrl, String status, String dayOfWeekAirs, String timeAirs,
			String overview, String network, Date lastUpdated) {
		super();
		this.showID = showID;
		
		this.showName = showName;
		this.showBannerUrl = showBannerUrl;
		this.showPosterUrl = showPosterUrl;
		this.status = status;
		this.dayOfWeekAirs = dayOfWeekAirs;
		this.timeAirs = timeAirs;
		this.overview = overview;
		this.network = network;
		this.lastUpdate = lastUpdated;
	}

	public long getShowID() {
		return showID;
	}

	public void setShowID(long showID) {
		this.showID = showID;
	}

	public Long getItasaId() {
		return itasaId;
	}

	public void setItasaId(long itasaId) {
		this.itasaId = itasaId;
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public String getShowBannerUrl() {
		return showBannerUrl;
	}

	public void setShowBannerUrl(String showBannerUrl) {
		this.showBannerUrl = showBannerUrl;
	}

	public String getShowPosterUrl() {
		return showPosterUrl;
	}

	public void setShowPosterUrl(String showPosterUrl) {
		this.showPosterUrl = showPosterUrl;
	}

	public String getBannerURI() {
		return showBannerUrl;
	}

	public String getPosterURI() {
		return showPosterUrl;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDayOfWeekAirs() {
		return dayOfWeekAirs;
	}

	public void setDayOfWeekAirs(String dayOfWeekAirs) {
		this.dayOfWeekAirs = dayOfWeekAirs;
	}

	public String getTimeAirs() {
		return timeAirs;
	}

	public void setTimeAirs(String timeAirs) {
		this.timeAirs = timeAirs;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public Date getLastUpdated() {
		return lastUpdate;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdate = lastUpdated;
	}

	public Episode getNextAirEpisode() {
		return nextAirEpisode;
	}

	public void setNextAirEpisode(Episode nextAirEpisode) {
		this.nextAirEpisode = nextAirEpisode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (showID ^ (showID >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Serie other = (Serie) obj;
		if (showID != other.showID)
			return false;
		return true;
	}
}
