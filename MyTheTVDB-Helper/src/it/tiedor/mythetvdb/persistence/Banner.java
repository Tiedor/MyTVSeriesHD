package it.tiedor.mythetvdb.persistence;

import it.tiedor.mythetvdb.MyConstants;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="Banner", strict=false)
public class Banner {

	@Element(required = true)
	private String id;
	
	@Element(required = true)
	private String BannerPath;
	
	@Element(required = true)
	private String BannerType;
	
	@Element(required = false)
	private String BannerType2;
	
	@Element(required = false)
	private String Language;
	
	@Element(required = false)
	private String ThumbnailPath;
	
	@Element(required = false)
	private String VignettePath;
	
	private int bannerSeason;
	private int bannerSeasonVersion = -1;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBannerPath() {
		if(this.BannerPath == null || this.BannerPath.equals("") || this.BannerPath.equals("null"))
			return null;
		return MyConstants.BANNERSURI+this.BannerPath;
	}

	public void setBannerPath(String bannerPath) {
		BannerPath = bannerPath;
	}

	public String getBannerType() {
		return BannerType;
	}

	public void setBannerType(String bannerType) {
		BannerType = bannerType;
	}

	public String getBannerType2() {
		return BannerType2;
	}

	public void setBannerType2(String bannerType2) {
		BannerType2 = bannerType2;
	}

	public String getLanguage() {
		return Language;
	}

	public void setLanguage(String language) {
		Language = language;
	}

	public String getThumbnailPath() {
		return ThumbnailPath;
	}

	public void setThumbnailPath(String thumbnailPath) {
		ThumbnailPath = thumbnailPath;
	}

	public String getVignettePath() {
		return VignettePath;
	}

	public void setVignettePath(String vignettePath) {
		VignettePath = vignettePath;
	}
	
	public int getBannerSeason() {
		return bannerSeason;
	}

	public void setBannerSeason(int bannerSeason) {
		this.bannerSeason = bannerSeason;
	}

	public int getBannerSeasonVersion() {
		return bannerSeasonVersion;
	}

	public void setBannerSeasonVersion(int bannerSeasonVersion) {
		this.bannerSeasonVersion = bannerSeasonVersion;
	}

	public boolean isVersionLast(int otherVersion){
		return this.bannerSeasonVersion>otherVersion;
	}
}
