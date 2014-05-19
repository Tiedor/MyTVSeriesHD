package it.tiedor.mythetvdb.persistence;

import java.util.ArrayList;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(strict=false)
public class Data {

	@ElementList(required = false, inline=true)
	private ArrayList<Serie> series;
		
	@ElementList(required = false, inline=true)
	private ArrayList<Episode> episodes;
	
	public ArrayList<Serie> getSeries() {
		return series;
	}

	public void setSeries(ArrayList<Serie> series) {
		this.series = series;
	}

	public ArrayList<Episode> getEpisodes() {
		return episodes;
	}

	public void setEpisodes(ArrayList<Episode> episodes) {
		this.episodes = episodes;
	}
	
}
