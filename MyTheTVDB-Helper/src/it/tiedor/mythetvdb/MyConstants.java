package it.tiedor.mythetvdb;

public class MyConstants {

	public static final String LOG_TAG = "MyTVSeriesHD - ";
	
	public static final String SEARCH_SERIES_KEY = "SEARCH_SERIES_KEY";
	
	public final static String APIKEY = "59119B25322212EB";
	
	public static final String SITE_URI = "http://www.thetvdb.com/";
	
	public static final String BANNERSURI = "http://www.thetvdb.com/banners/";
	
	public static final String GET_BANNERS_URI = "api/"+APIKEY+"/series/{SERIEID}/banners.xml";
	
	public static final String SEARCH_URI = "api/GetSeries.php?seriesname=";
	
	public static final String GET_SERIE_URI = "api/"+APIKEY+"/series/{SERIEID}";
	
	public static final String GET_EPISODES_URI = "api/"+APIKEY+"/series/{SERIEID}/all/en.xml";
	
	public static final String GET_EPISODE_URI = "api/"+APIKEY+"/episodes/{EPISODEID}";
	
	public static final String TAG_MAIN = "TAGMAIN";
	
	public static final String GET_DAILY_UPDATE_URI = "api/"+APIKEY+"/updates/updates_day.xml";
}
