package it.tiedor.mythetvdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import it.tiedor.mythetvdb.internal.TheTVDBParser;
import it.tiedor.mythetvdb.persistence.Banner;
import it.tiedor.mythetvdb.persistence.Data;
import it.tiedor.mythetvdb.persistence.Episode;
import it.tiedor.mythetvdb.persistence.Serie;

public class TheTVDBApi extends TheTVDBParser{

	private final TheTVDBParser parser = new TheTVDBParser();
	private final String LOG_TAG = MyConstants.LOG_TAG + this.getClass().getName();
	
	public ArrayList<Serie> searchSeries(String name) throws Exception{
		ArrayList<Serie> series = new ArrayList<Serie>(); 
		ArrayList<Serie> tmpSeries = parser.searchSeries(name);
		
		if(tmpSeries.size()<=0)
			throw new Exception("Nessuna serie trovata");
		
		for(Serie serie : tmpSeries){
			Serie tmpSerie = parser.getSerie(serie.getId());
			series.add(tmpSerie);
		}
		
		return series;
	}
	
	public Data getSerieWithEpisodes(Long serieId) throws Exception{
		
		return parser.getSerieWithEpisodes(serieId);
	}
	
	public ArrayList<Episode> getEpisodes(long serieId) throws Exception{
		return parser.getEpisodes(Long.toString(serieId));
	}
	
	public ArrayList<Banner> getBanners(long serieId) throws Exception{
				
		return this.parser.getBanners(Long.toString(serieId));
	}
	
	public Map<String, Banner> getSeasonBanners(long serieId) throws Exception{
		
		Map<String, Banner> bannersMap = new HashMap<String, Banner>();
		
		String[] bannerPathArray;
		String bannerSeason;
		String bannerSeasonVersion;
		
		for(Banner banner : getBanners(serieId)){
			if(banner.getBannerType().equals("season") && (banner.getLanguage().equals("en") || banner.getLanguage().equals("it"))){
				Log.d(LOG_TAG, "Banner path: "+banner.getBannerPath());
				bannerPathArray = banner.getBannerPath().substring(0,  banner.getBannerPath().lastIndexOf(".")).split("-");
				if(bannerPathArray.length>1 && !(bannerSeason=bannerPathArray[1]).equals("0")){
					Log.d(LOG_TAG, "Banner Season: "+bannerSeason);
					banner.setBannerSeason(Integer.parseInt(bannerSeason));
					if(bannerPathArray.length>2 && (bannerSeasonVersion = bannerPathArray[2]) != null){
						Log.d(LOG_TAG, "Banner Season Version: "+bannerSeasonVersion);
						banner.setBannerSeasonVersion(Integer.parseInt(bannerSeasonVersion));
					}
					if(bannersMap.containsKey(bannerSeason)){
						Log.d(LOG_TAG, "Stagione già in Mappa faccio il controllo tra le versioni["+bannersMap.get(bannerSeason).getBannerSeasonVersion()+"] ");
						if(!bannersMap.get(bannerSeason).isVersionLast(banner.getBannerSeasonVersion())){
							bannersMap.remove(bannerSeason);
							bannersMap.put(bannerSeason, banner);
						}
					}else{
						Log.d(LOG_TAG, "Stagione non ancora in mappa, la aggiungo");
						bannersMap.put(bannerSeason, banner);
					}
				}
				
			}
		}
		
		
		for(String key : bannersMap.keySet()){
			Log.d(LOG_TAG, "["+key+"] -> ["+bannersMap.get(key).getBannerPath()+"]");
		}
		
		return bannersMap;
	}

	public Data getDailyUpdates() throws Exception{
		return parser.getDailyUpdates();
	}

	public Episode getEpisode(long episodeId) throws Exception{
				
		return parser.getEpisode(episodeId);
	}
}
