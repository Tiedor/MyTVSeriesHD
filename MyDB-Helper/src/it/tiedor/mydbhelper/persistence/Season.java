package it.tiedor.mydbhelper.persistence;

import com.j256.ormlite.field.DatabaseField;

public class Season {

	@DatabaseField(generatedId = true)
	private long seasonId;
	
	@DatabaseField
	private int season;

	@DatabaseField
	private String saesonImageUrl;

	@DatabaseField(foreign=true, foreignAutoRefresh = true)
    private Serie serie;
	
	@DatabaseField
	private boolean toFollow = false;
	
	public Season() {}

	public Season(int season, String saesonImageUrl, Serie serie) {
		super();
		this.season = season;
		this.saesonImageUrl = saesonImageUrl;
		this.serie = serie;
	}

	public long getSeasonId() {
		return seasonId;
	}

	public void setSeasonId(long seasonId) {
		this.seasonId = seasonId;
	}

	public int getSeason() {
		return season;
	}

	public void setSeason(int season) {
		this.season = season;
	}

	public String getSaesonImageUrl() {
		return saesonImageUrl;
	}

	public void setSaesonImageUrl(String saesonImageUrl) {
		this.saesonImageUrl = saesonImageUrl;
	}
	
	public Serie getSerie() {
		return serie;
	}

	public void setSerie(Serie serie) {
		this.serie = serie;
	}

	public boolean isToFollow() {
		return toFollow;
	}

	public void setToFollow(boolean toFollow) {
		this.toFollow = toFollow;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (seasonId ^ (seasonId >>> 32));
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
		Season other = (Season) obj;
		if (seasonId != other.seasonId)
			return false;
		return true;
	}
}
