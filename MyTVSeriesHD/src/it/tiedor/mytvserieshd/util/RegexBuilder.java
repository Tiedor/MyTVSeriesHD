package it.tiedor.mytvserieshd.util;

import it.tiedor.mydbhelper.persistence.Episode;
import it.tiedor.mytvserieshd.MyConstants;

import java.util.Locale;

import android.util.Log;

public class RegexBuilder {

	private final static String LOG_TAG = MyConstants.LOG_TAG + " - " + RegexBuilder.class.getSimpleName();

	public static String createRegex(String seriesName, Episode episode, boolean full){

		seriesName = (seriesName.contains("(") ? seriesName.substring(0, seriesName.indexOf("(")) : seriesName).trim();
		seriesName = seriesName.replace(".", "(.)?").replace("'", ".*");

		Integer epSeason = episode.getSeason().getSeason();
		Integer epNumber = episode.getEpisode();

		if(seriesName.toLowerCase(Locale.ITALY).contains("american dad") || seriesName.toLowerCase(Locale.ITALY).contains("american.dad")){
			epSeason = epSeason-1;
			seriesName = seriesName.replace("!", "");
		}

		Log.d(LOG_TAG, "EpisodeListActivity - createRegex - Series Name: "+seriesName);

		String regex = "(?i).*"+seriesName.replace(" ", ".")+"." +
				(full ? "*" : "") +
				"(s)?(0)?"+epSeason+"(.)?(e)?(0)?"+epNumber+".*";

		return regex;
	}
}
