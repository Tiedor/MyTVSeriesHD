package it.tiedor.mythetvdb.persistence;

import it.tiedor.mythetvdb.MyConstants;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="Episode", strict=false)
public class Episode{

	@Element(required = false)
	private long id;

	private long itasaId;

	@Element(required = false, name = "EpisodeName")
	private String name;

	@Element(required = false, name = "EpisodeNumber")
	private String number;

	@Element(required = false)
	private String FirstAired;

	@Element(required = false, name = "SeasonNumber")
	private String season;

	@Element(required = false, name = "Overview")
	private String overview;

	@Element(required = false, name = "lastupdated")
	private Long lastUpdated;
	
	@Element(required = false, name = "filename")
	private String filename;

	@Element(required = false, name = "time")
	private Long time;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getItasaId() {
		return itasaId;
	}

	public void setItasaId(long itasaId) {
		this.itasaId = itasaId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getFirstAired() {
		return FirstAired;
	}

	public void setFirstAired(String firstAired) {
		FirstAired = firstAired;
	}

	public String getSeason() {
		return season;
	}

	public void setSeason(String season) {
		this.season = season;
	}

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public Long getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getFilename() {
		if(this.filename == null || this.filename.equals("") || this.filename.equals("null"))
			return null;
		return MyConstants.BANNERSURI+this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}
	
}
