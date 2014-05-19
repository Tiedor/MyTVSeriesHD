package it.tiedor.mythetvdb.persistence;

import java.util.ArrayList;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class Banners {
	
	@ElementList(required = false, inline=true)
	private ArrayList<Banner> Banner;
	
	public ArrayList<Banner> getBanner() {
		return Banner;
	}
	
	public void setBanner(ArrayList<Banner> banner) {
		Banner = banner;
	}
}
