package it.tiedor.mythetvdb.persistence;


import it.tiedor.mythetvdb.MyConstants;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.util.Log;

@Root(name="Series", strict=false)
public class Serie{

	@Element(required = false)
	private String id;

	@Element(required = false, name = "SeriesName")
	private String name;

	@Element(required = false, name = "Status")
	private String status;

	@Element(required = false, name = "Overview")
	private String overview;

	@Element(required = false, name = "Network")
	private String network;

	@Element(required = false, name = "Airs_DayOfWeek")
	private String dayOfWeekAirs;

	@Element(required = false, name = "Airs_Time")
	private String timeAirs;

	@Element(required = false, name = "FirstAired")
	private String firstAired;

	@Element(required = false, name = "banner")
	private String banner;

	@Element(required = false, name = "poster")
	private String poster;

	@Element(required = false, name = "fanart")
	private String fanart;

	@Element(required = false, name = "lastupdated")
	private Long lastUpdated;
	
	@Element(required = false, name = "time")
	private Long time;

	public Serie() {}

	public Serie(String id, String name, String status,
			String overview, String network, String dayOfWeekAirs,
			String timeAirs, String firstAired,
			String banner, String poster, String fanart, Long lastUpdated) {
		super();
		this.id = id;
		this.name = name;
		this.status = status;
		this.overview = overview;
		this.network = network;
		this.dayOfWeekAirs = dayOfWeekAirs;
		this.timeAirs = timeAirs;
		this.firstAired = firstAired;
		this.banner = banner;
		this.poster = poster;
		this.fanart = fanart;
		this.lastUpdated = lastUpdated;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
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

	public String getFirstAired() {
		return firstAired;
	}

	public void setFirstAired(String firstAired) {
		this.firstAired = firstAired;
	}

	public String getBanner() {
		return banner;
	}

	public void setBanner(String banner) {
		this.banner = banner;
	}

	public String getBannerURI(){
		if(this.banner == null || this.banner.equals("") || this.banner.equals("null"))
			return null;
		return MyConstants.BANNERSURI+this.banner;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public String getPosterURI(){
		if(this.poster == null || this.poster.equals("") || this.poster.equals("null"))
			return null;
		return MyConstants.BANNERSURI+this.poster;
	}

	public String getFanart() {
		return fanart;
	}

	public void setFanart(String fanart) {
		this.fanart = fanart;
	}

	public Long getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}
}
