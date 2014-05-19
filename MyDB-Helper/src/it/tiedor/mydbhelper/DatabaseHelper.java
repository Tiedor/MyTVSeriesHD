package it.tiedor.mydbhelper;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import it.tiedor.mydb.R;
import it.tiedor.mydbhelper.persistence.Action;
import it.tiedor.mydbhelper.persistence.Episode;
import it.tiedor.mydbhelper.persistence.Season;
import it.tiedor.mydbhelper.persistence.Serie;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private final String LOG_TAG 	= "MyTVSeriesHD" + " - " + getClass().getSimpleName();

	// name of the database file for your application -- change to something appropriate for your app
	private static final String DATABASE_NAME = "myTvSeriesHD.db";
	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION = 2;

	// the DAO object we use to access the Serie table
	private Dao<Serie, Long> serieDao = null;
	// the DAO object we use to access the Episode table
	private Dao<Episode, Long> episodeDao = null;
	// the DAO object we use to access the Season table
	private Dao<Season, Long> seasonDao = null;
	// the DAO object we use to access the Action table
	private Dao<Action, Long> actionDao = null;

	private RuntimeExceptionDao<Serie, Long> serieRuntimeDao = null;
	private RuntimeExceptionDao<Episode, Long> episodeRuntimeDao = null;
	private RuntimeExceptionDao<Season, Long> seasonRuntimeDao = null;
	private RuntimeExceptionDao<Action, Long> actionRuntimeDao = null;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
	}

	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, Serie.class);
			TableUtils.createTable(connectionSource, Episode.class);
			TableUtils.createTable(connectionSource, Season.class);
			TableUtils.createTable(connectionSource, Action.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.createTable(connectionSource, Action.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the Database Access Object (DAO) for our Serie class. It will create it or just give the cached
	 * value.
	 */
	public Dao<Serie, Long> getSerieDao() throws SQLException {
		if (serieDao == null) {
			serieDao = getDao(Serie.class);
		}
		return serieDao;
	}

	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our Serie class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<Serie, Long> getSerieRuntimeDao() {
		if (serieRuntimeDao == null) {
			serieRuntimeDao = getRuntimeExceptionDao(Serie.class);
		}
		return serieRuntimeDao;
	}

	/**
	 * Returns the Database Access Object (DAO) for our Episode class. It will create it or just give the cached
	 * value.
	 */
	public Dao<Episode, Long> getEpisodeDao() throws SQLException {
		if (episodeDao == null) {
			episodeDao = getDao(Episode.class);
		}
		return episodeDao;
	}

	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our Episode class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<Episode, Long> getEpisodeRuntimeDao() {
		if (episodeRuntimeDao == null) {
			episodeRuntimeDao = getRuntimeExceptionDao(Episode.class);
		}
		return episodeRuntimeDao;
	}

	/**
	 * Returns the Database Access Object (DAO) for our Season class. It will create it or just give the cached
	 * value.
	 */
	public Dao<Season, Long> getSeasonDao() throws SQLException {
		if (seasonDao == null) {
			seasonDao = getDao(Season.class);
		}
		return seasonDao;
	}

	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our Season class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<Season, Long> getSeasonRuntimeDao() {
		if (seasonRuntimeDao == null) {
			seasonRuntimeDao = getRuntimeExceptionDao(Season.class);
		}
		return seasonRuntimeDao;
	}

	/**
	 * Returns the Database Access Object (DAO) for our Action class. It will create it or just give the cached
	 * value.
	 */
	public Dao<Action, Long> getActionDao() throws SQLException {
		if (actionDao == null) {
			actionDao = getDao(Action.class);
		}
		return actionDao;
	}

	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our Serie class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<Action, Long> getActionRuntimeDao() {
		if (actionRuntimeDao == null) {
			actionRuntimeDao = getRuntimeExceptionDao(Action.class);
		}
		return actionRuntimeDao;
	}

	public Season getSeason(long serie) throws SQLException {
		QueryBuilder<Season, Long> queryBuilder = getSeasonDao().queryBuilder();
		queryBuilder.where().eq("SERIE_ID", serie);
		PreparedQuery<Season> preparedQuery = queryBuilder.prepare();

		return getSeasonDao().queryForFirst(preparedQuery);
	}

	public List<Season> getSeasons(long serie) throws SQLException {
		QueryBuilder<Season, Long> queryBuilder = getSeasonDao().queryBuilder();
		queryBuilder.where().eq("SERIE_ID", serie);
		queryBuilder.orderBy("season", false);
		PreparedQuery<Season> preparedQuery = queryBuilder.prepare();

		return getSeasonDao().query(preparedQuery);
	}

	public Season getSeason(int season, long serie) throws SQLException {
		QueryBuilder<Season, Long> queryBuilder = getSeasonDao().queryBuilder();
		Where<Season, Long> where = queryBuilder.where();
		where.eq("season", season);
		where.and();
		where.eq("SERIE_ID", serie);
		PreparedQuery<Season> preparedQuery = queryBuilder.prepare();

		return getSeasonDao().queryForFirst(preparedQuery);
	}

	public ArrayList<Episode> getSeriesEpisodes(long serieId) throws SQLException{
		QueryBuilder<Episode, Long> queryBuilder = getEpisodeDao().queryBuilder();
		queryBuilder.where().in("SEASON_ID", getSeasons(serieId));
		PreparedQuery<Episode> preparedQuery = queryBuilder.prepare();
		return new ArrayList<Episode>(getEpisodeDao().query(preparedQuery));
	}

	public ArrayList<Episode> getSeasonsEpisodes(long seasonId) throws SQLException{
		QueryBuilder<Episode, Long> queryBuilder = getEpisodeDao().queryBuilder();
		queryBuilder.where().eq("SEASON_ID", seasonId);
		queryBuilder.orderBy("airTime", true);
		PreparedQuery<Episode> preparedQuery = queryBuilder.prepare();
		Log.d(LOG_TAG, "Query: "+preparedQuery.getStatement());
		return new ArrayList<Episode>(getEpisodeDao().query(preparedQuery));
	}

	public void saveSeries(Map<Serie, ArrayList<Episode>> mappa) throws SQLException{
		for(Serie serie : mappa.keySet()){
			for(Episode episode : mappa.get(serie)){
				getEpisodeDao().createIfNotExists(episode);
			}
			getSerieDao().createIfNotExists(serie);
		}
	}

	public Episode getNextEpisode(long serieId) throws SQLException{
		QueryBuilder<Episode, Long> queryBuilder = getEpisodeDao().queryBuilder();
		Where<Episode, Long> where = queryBuilder.where();
		where.gt("airTime", Calendar.getInstance().getTime());
		where.and();
		where.in("SEASON_ID", getSeasons(serieId));
		queryBuilder.orderBy("airTime", true);
		PreparedQuery<Episode> preparedQuery = queryBuilder.prepare();

		Log.d(LOG_TAG, "Lancio la seguente query: "+preparedQuery.getStatement());

		return getEpisodeDao().queryForFirst(preparedQuery);
	}

	public boolean setSeasonFavourite(long seasonId) throws SQLException{
		Season season = getSeasonDao().queryForId(seasonId);

		boolean fav = !season.isToFollow();

		season.setToFollow(!season.isToFollow());
		getSeasonDao().update(season);
		return fav;
	}

	public int getAllSeriesCount(){
		int total = 0;

		try{
			QueryBuilder<Serie, Long> queryBuilder = getSerieDao().queryBuilder();
			total = (int) getSerieDao().countOf(queryBuilder.setCountOf(true).prepare());
		}catch(Exception e){}

		return total;
	}

	public ArrayList<Serie> getAllSeries() throws SQLException, ParseException{
		ArrayList<Serie> series = new ArrayList<Serie>();
		List<Long> ids = new ArrayList<Long>();
		GenericRawResults<String[]> rawResults = getSerieDao().queryRaw("select s.showId, s.showName, s.showBannerUrl, e.episodeId, e.episodeName, e.airTime " +
				"from serie s join season se on s.showID = se.serie_id join episode e on se.seasonId = e.season_id " +
				"where e.airTime = (select min(e2.airTime) from episode e2 join season se2 on se2.seasonId = e2.season_id where e2.airtime > date('now') and s.showID = se2.serie_id and e2.episode != 0) and e.episode != 0 and se.toFollow = 1 " +
				"order by e.airTime asc");
		List<String[]> results = rawResults.getResults();
		Episode e;
		Serie serie;
		for(String[] array : results){
			serie = new Serie();
			serie.setShowID(Long.parseLong(array[0]));

			if(series.contains(serie)){
				serie = series.get(series.indexOf(serie));
				e = serie.getNextAirEpisode();
				e.setEpisodeName(e.getEpisodeName()+" + "+array[4]);
			}else{
				serie.setShowName(array[1]);
				serie.setShowBannerUrl(array[2]);
				e = new Episode();
				//e.setEpisodeId(Long.parseLong(array[3]));
				e.setEpisodeName(array[4]);
				e.setAirTime(new SimpleDateFormat("yyyy-MM-dd").parse(array[5]));
				serie.setNextAirEpisode(e);
				series.add(serie);
				ids.add(serie.getShowID());
			}
		}

		QueryBuilder<Serie, Long> queryBuilder = getSerieDao().queryBuilder();
		Where<Serie, Long> where = queryBuilder.where();
		where.notIn("showId", ids);
		queryBuilder.orderBy("status", true);
		queryBuilder.orderBy("showName", true);
		PreparedQuery<Serie> preparedQuery = queryBuilder.prepare();

		series.addAll(getSerieDao().query(preparedQuery));

		return series;
	}

	public void updateSerie(Serie serie) throws SQLException{
		Log.d(LOG_TAG, "Aggiorno la serie ["+serie.getShowName()+"]");
		getSerieDao().update(serie);
	}

	public void updateSeason(Season season) throws SQLException{
		Log.d(LOG_TAG, "Aggiorno la Stagione ["+season.getSaesonImageUrl()+"]");
		getSeasonDao().update(season);
	}

	public void updateEpisode(Episode episode) throws SQLException{
		Log.d(LOG_TAG, "Aggiorno l'episodio ["+episode.getEpisodeName()+"], isDownloading ["+episode.isDownloading()+"]");
		getEpisodeDao().update(episode);
	}

	public ArrayList<Season> getSeasonToDownload() throws SQLException{
		Log.d(LOG_TAG, "Cerco gli episodi che sono disponibili al download");

		QueryBuilder<Episode, Long> episodeQueryBuilder = getEpisodeDao().queryBuilder();
		Where<Episode, Long> episodeWhere = episodeQueryBuilder.where();
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.DATE, -1);
		episodeWhere.and(episodeWhere.lt("airTime", tomorrow.getTime()), episodeWhere.eq("downloading", false));

		QueryBuilder<Season, Long> seasonQueryBuilder = getSeasonDao().queryBuilder();
		Where<Season, Long> where = seasonQueryBuilder.where();
		where.eq("toFollow", true);

		PreparedQuery<Season> preparedQuery = seasonQueryBuilder.distinct().join(episodeQueryBuilder).prepare();

		Log.d(LOG_TAG, "Eseguo la query: "+preparedQuery.getStatement());

		ArrayList<Season> listastSeasons = new ArrayList(getSeasonDao().query(preparedQuery));

		return listastSeasons;
	}

	public ArrayList<Episode> getEpisodesToDownload(long seasonId) throws SQLException{
		Log.d(LOG_TAG, "Cerco gli episodi che sono disponibili al download");

		QueryBuilder<Episode, Long> episodeQueryBuilder = getEpisodeDao().queryBuilder();
		Where<Episode, Long> episodeWhere = episodeQueryBuilder.where();
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.DATE, -1);
		episodeWhere.and(episodeWhere.lt("airTime", tomorrow.getTime()), episodeWhere.eq("downloading", false), episodeWhere.eq("SEASON_ID", seasonId));

		PreparedQuery<Episode> preparedQuery = episodeQueryBuilder.prepare();

		Log.d(LOG_TAG, "Eseguo la query: "+preparedQuery.getStatement());

		ArrayList<Episode> listaEpisodi = new ArrayList(getEpisodeDao().query(preparedQuery));

		return listaEpisodi;
	}

	public ArrayList<Episode> getEpisodesToDownload() throws SQLException{
		Log.d(LOG_TAG, "Cerco gli episodi che sono disponibili al download");

		QueryBuilder<Episode, Long> episodeQueryBuilder = getEpisodeDao().queryBuilder();
		Where<Episode, Long> episodeWhere = episodeQueryBuilder.where();
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.DATE, -1);
		episodeWhere.and(episodeWhere.lt("airTime", tomorrow.getTime()), episodeWhere.eq("downloading", false));

		QueryBuilder<Season, Long> seasonQueryBuilder = getSeasonDao().queryBuilder();
		Where<Season, Long> where = seasonQueryBuilder.where();
		where.eq("toFollow", true);

		PreparedQuery<Episode> preparedQuery = episodeQueryBuilder.join(seasonQueryBuilder).prepare();

		Log.d(LOG_TAG, "Eseguo la query: "+preparedQuery.getStatement());

		ArrayList<Episode> listaEpisodi = new ArrayList(getEpisodeDao().query(preparedQuery));

		return listaEpisodi;
	}

	public ArrayList<Season> getSeasonInDownload() throws SQLException{
		Log.d(LOG_TAG, "Cerco gli episodi che sono in download");

		QueryBuilder<Episode, Long> episodeQueryBuilder = getEpisodeDao().queryBuilder();
		Where<Episode, Long> episodeWhere = episodeQueryBuilder.where();
		episodeWhere.and(episodeWhere.le("airTime", Calendar.getInstance().getTime()), episodeWhere.eq("downloading", true), episodeWhere.eq("download", false));

		QueryBuilder<Season, Long> seasonQueryBuilder = getSeasonDao().queryBuilder();
		Where<Season, Long> where = seasonQueryBuilder.where();
		where.eq("toFollow", true);

		PreparedQuery<Season> preparedQuery = seasonQueryBuilder.distinct().join(episodeQueryBuilder).prepare();

		Log.d(LOG_TAG, "Eseguo la query: "+preparedQuery.getStatement());

		ArrayList<Season> listaEpisodi = new ArrayList(getSeasonDao().query(preparedQuery));

		return listaEpisodi;
	}

	public ArrayList<Episode> getEpisodesInDownload(long seasonId) throws SQLException{
		Log.d(LOG_TAG, "Cerco gli episodi che sono in download");

		QueryBuilder<Episode, Long> episodeQueryBuilder = getEpisodeDao().queryBuilder();
		Where<Episode, Long> episodeWhere = episodeQueryBuilder.where();
		episodeWhere.and(episodeWhere.le("airTime", Calendar.getInstance().getTime()), episodeWhere.eq("downloading", true), episodeWhere.eq("download", false)
				, episodeWhere.eq("SEASON_ID", seasonId));

		PreparedQuery<Episode> preparedQuery = episodeQueryBuilder.prepare();

		Log.d(LOG_TAG, "Eseguo la query: "+preparedQuery.getStatement());

		ArrayList<Episode> listaEpisodi = new ArrayList(getEpisodeDao().query(preparedQuery));

		return listaEpisodi;
	}

	public ArrayList<Episode> getEpisodesInDownload() throws SQLException{
		Log.d(LOG_TAG, "Cerco gli episodi che sono in download");

		QueryBuilder<Episode, Long> episodeQueryBuilder = getEpisodeDao().queryBuilder();
		Where<Episode, Long> episodeWhere = episodeQueryBuilder.where();
		episodeWhere.and(episodeWhere.le("airTime", Calendar.getInstance().getTime()), episodeWhere.eq("downloading", true), episodeWhere.eq("download", false));

		QueryBuilder<Season, Long> seasonQueryBuilder = getSeasonDao().queryBuilder();
		Where<Season, Long> where = seasonQueryBuilder.where();
		where.eq("toFollow", true);

		PreparedQuery<Episode> preparedQuery = episodeQueryBuilder.join(seasonQueryBuilder).prepare();

		Log.d(LOG_TAG, "Eseguo la query: "+preparedQuery.getStatement());

		ArrayList<Episode> listaEpisodi = new ArrayList(getEpisodeDao().query(preparedQuery));

		return listaEpisodi;
	}

	public ArrayList<Season> getSeasonToSub() throws SQLException{
		Log.d(LOG_TAG, "Cerco gli episodi che potrebbero aver ricevuto i sottotitoli");

		QueryBuilder<Episode, Long> episodeQueryBuilder = getEpisodeDao().queryBuilder();
		Where<Episode, Long> episodeWhere = episodeQueryBuilder.where();
		episodeWhere.and(episodeWhere.le("airTime", Calendar.getInstance().getTime()), 
				/*episodeWhere.eq("downloading", true), episodeWhere.eq("download", true),*/
				episodeWhere.eq("sub", false), episodeWhere.isNotNull("itasaSubUrl"), episodeWhere.ne("itasaSubUrl", ""));

		QueryBuilder<Season, Long> seasonQueryBuilder = getSeasonDao().queryBuilder();
		Where<Season, Long> where = seasonQueryBuilder.where();
		where.eq("toFollow", true);

		PreparedQuery<Season> preparedQuery = seasonQueryBuilder.distinct().join(episodeQueryBuilder).prepare();

		Log.d(LOG_TAG, "Eseguo la query: "+preparedQuery.getStatement());

		ArrayList<Season> listaEpisodi = new ArrayList(getSeasonDao().query(preparedQuery));

		return listaEpisodi;
	}

	public ArrayList<Episode> getEpisodesToSub(long seasonId) throws SQLException{
		Log.d(LOG_TAG, "Cerco gli episodi che potrebbero aver ricevuto i sottotitoli");

		QueryBuilder<Episode, Long> episodeQueryBuilder = getEpisodeDao().queryBuilder();
		Where<Episode, Long> episodeWhere = episodeQueryBuilder.where();
		episodeWhere.and(episodeWhere.le("airTime", Calendar.getInstance().getTime()), 
				/*episodeWhere.eq("downloading", true), episodeWhere.eq("download", true),*/
				episodeWhere.eq("sub", false), episodeWhere.isNotNull("itasaSubUrl"), episodeWhere.ne("itasaSubUrl", ""), 
				episodeWhere.eq("SEASON_ID", seasonId));

		PreparedQuery<Episode> preparedQuery = episodeQueryBuilder.prepare();

		Log.d(LOG_TAG, "Eseguo la query: "+preparedQuery.getStatement());

		ArrayList<Episode> listaEpisodi = new ArrayList(getEpisodeDao().query(preparedQuery));

		return listaEpisodi;
	}

	public ArrayList<Episode> getEpisodesToSub() throws SQLException{
		Log.d(LOG_TAG, "Cerco gli episodi che potrebbero aver ricevuto i sottotitoli");

		QueryBuilder<Episode, Long> episodeQueryBuilder = getEpisodeDao().queryBuilder();
		Where<Episode, Long> episodeWhere = episodeQueryBuilder.where();
		episodeWhere.and(episodeWhere.le("airTime", Calendar.getInstance().getTime()), 
				episodeWhere.eq("downloading", true), episodeWhere.eq("download", true),
				episodeWhere.eq("sub", false), episodeWhere.isNotNull("itasaSubUrl"), episodeWhere.ne("itasaSubUrl", ""));

		QueryBuilder<Season, Long> seasonQueryBuilder = getSeasonDao().queryBuilder();
		Where<Season, Long> where = seasonQueryBuilder.where();
		where.eq("toFollow", true);

		PreparedQuery<Episode> preparedQuery = episodeQueryBuilder.join(seasonQueryBuilder).prepare();

		Log.d(LOG_TAG, "Eseguo la query: "+preparedQuery.getStatement());

		ArrayList<Episode> listaEpisodi = new ArrayList(getEpisodeDao().query(preparedQuery));

		return listaEpisodi;
	}

	public ArrayList<Episode> getEpisodesToSub4Task() throws SQLException{
		Log.d(LOG_TAG, "Cerco gli episodi che potrebbero aver ricevuto i sottotitoli");

		QueryBuilder<Episode, Long> episodeQueryBuilder = getEpisodeDao().queryBuilder();
		Where<Episode, Long> episodeWhere = episodeQueryBuilder.where();
		episodeWhere.and(episodeWhere.le("airTime", Calendar.getInstance().getTime()), episodeWhere.or(episodeWhere.isNull("itasaSubUrl"), episodeWhere.eq("itasaSubUrl", "")));

		QueryBuilder<Season, Long> seasonQueryBuilder = getSeasonDao().queryBuilder();
		Where<Season, Long> where = seasonQueryBuilder.where();
		where.eq("toFollow", true);

		PreparedQuery<Episode> preparedQuery = episodeQueryBuilder.join(seasonQueryBuilder).prepare();

		Log.d(LOG_TAG, "Eseguo la query: "+preparedQuery.getStatement());

		ArrayList<Episode> listaEpisodi = new ArrayList(getEpisodeDao().query(preparedQuery));

		return listaEpisodi;
	}

	public ArrayList<Season> getSeasonToView() throws SQLException{
		Log.d(LOG_TAG, "Cerco gli episodi che sono disponibili al download");

		QueryBuilder<Episode, Long> episodeQueryBuilder = getEpisodeDao().queryBuilder();
		Where<Episode, Long> episodeWhere = episodeQueryBuilder.where();
		episodeWhere.and(episodeWhere.le("airTime", Calendar.getInstance().getTime()), 
				episodeWhere.eq("downloading", true), episodeWhere.eq("download", true),
				episodeWhere.eq("sub", true), episodeWhere.eq("view", false));

		QueryBuilder<Season, Long> seasonQueryBuilder = getSeasonDao().queryBuilder();
		Where<Season, Long> where = seasonQueryBuilder.where();
		where.eq("toFollow", true);
		seasonQueryBuilder.orderBy("SERIE_ID", true);


		PreparedQuery<Season> preparedQuery = seasonQueryBuilder.distinct().join(episodeQueryBuilder).prepare();

		Log.d(LOG_TAG, "Eseguo la query: "+preparedQuery.getStatement());

		ArrayList<Season> listaEpisodi = new ArrayList(getSeasonDao().query(preparedQuery));

		return listaEpisodi;
	}

	public ArrayList<Episode> getEpisodesToView() throws SQLException{
		Log.d(LOG_TAG, "Cerco gli episodi che sono disponibili al download");

		QueryBuilder<Episode, Long> episodeQueryBuilder = getEpisodeDao().queryBuilder();
		Where<Episode, Long> episodeWhere = episodeQueryBuilder.where();
		episodeWhere.and(episodeWhere.le("airTime", Calendar.getInstance().getTime()), 
				episodeWhere.eq("downloading", true), episodeWhere.eq("download", true),
				episodeWhere.eq("sub", true), episodeWhere.eq("view", false));

		QueryBuilder<Season, Long> seasonQueryBuilder = getSeasonDao().queryBuilder();
		Where<Season, Long> where = seasonQueryBuilder.where();
		where.eq("toFollow", true);

		PreparedQuery<Episode> preparedQuery = episodeQueryBuilder.join(seasonQueryBuilder).prepare();

		Log.d(LOG_TAG, "Eseguo la query: "+preparedQuery.getStatement());

		ArrayList<Episode> listaEpisodi = new ArrayList(getEpisodeDao().query(preparedQuery));

		return listaEpisodi;
	}

	public ArrayList<Episode> getEpisodesToView(long seasonId) throws SQLException{
		Log.d(LOG_TAG, "Cerco gli episodi che sono disponibili al download");

		QueryBuilder<Episode, Long> episodeQueryBuilder = getEpisodeDao().queryBuilder();
		Where<Episode, Long> episodeWhere = episodeQueryBuilder.where();
		episodeWhere.and(episodeWhere.le("airTime", Calendar.getInstance().getTime()), 
				episodeWhere.eq("downloading", true), episodeWhere.eq("download", true),
				episodeWhere.eq("sub", true), episodeWhere.eq("view", false), episodeWhere.eq("SEASON_ID", seasonId));

		PreparedQuery<Episode> preparedQuery = episodeQueryBuilder.prepare();

		Log.d(LOG_TAG, "Eseguo la query: "+preparedQuery.getStatement());

		ArrayList<Episode> listaEpisodi = new ArrayList(getEpisodeDao().query(preparedQuery));

		return listaEpisodi;
	}

	public Season getLastSeason(Long serieId) throws SQLException{
		QueryBuilder<Season, Long> queryBuilder = getSeasonDao().queryBuilder();
		Where<Season, Long> where = queryBuilder.where();
		where.eq("SERIE_ID", serieId);
		queryBuilder.orderBy("season", false);

		PreparedQuery<Season> preparedQuery = queryBuilder.prepare();

		return getSeasonDao().queryForFirst(preparedQuery);
	}

	public ArrayList<Episode> getEpisodeToUpdate() throws SQLException{
		QueryBuilder<Episode, Long> episodeQueryBuilder = getEpisodeDao().queryBuilder();
		Where<Episode, Long> episodeWhere = episodeQueryBuilder.where();

		episodeWhere.or(
				episodeWhere.eq("EPISODENAME", ""), 
				episodeWhere.eq("EPISODENAME", "TBA"),
				episodeWhere.eq("EPISODEIMAGEURL", ""),
				episodeWhere.isNull("AIRTIME"));

		PreparedQuery<Episode> preparedQuery = episodeQueryBuilder.prepare();

		Log.d(LOG_TAG, "Eseguo la query: "+preparedQuery.getStatement());

		ArrayList<Episode> listaEpisodi = new ArrayList(getEpisodeDao().query(preparedQuery));

		return listaEpisodi;
	}

	public Episode setEpisodeToDownload(long episodeId) throws SQLException{
		Episode episode = getEpisodeDao().queryForId(episodeId);
		episode.setDownloading(false);
		episode.setDownload(false);
		episode.setSub(false);
		episode.setView(false);
		updateEpisode(episode);

		Log.d(LOG_TAG, "Aggiorno l'episodio con id["+episodeId+"] da Scaricare");

		return episode;
	}

	public Episode setEpisodeDownloading(long episodeId) throws SQLException{
		Episode episode = getEpisodeDao().queryForId(episodeId);
		episode.setDownloading(true);
		episode.setDownload(false);
		//episode.setSub(false);
		episode.setView(false);
		updateEpisode(episode);

		Log.d(LOG_TAG, "Aggiorno l'episodio con id["+episodeId+"] in Download");

		return episode;
	}

	public Episode setEpisodeDownloaded(long episodeId) throws SQLException{
		Episode episode = getEpisodeDao().queryForId(episodeId);
		episode.setDownloading(true);
		episode.setDownload(true);
		//episode.setSub(false);
		episode.setView(false);
		updateEpisode(episode);

		Log.d(LOG_TAG, "Aggiorno l'episodio con id["+episodeId+"] come Scaricato");

		return episode;
	}

	public Episode setEpisodeJustSubbed(long episodeId) throws SQLException{
		Episode episode = getEpisodeDao().queryForId(episodeId);
		episode.setSub(true);
		updateEpisode(episode);

		Log.d(LOG_TAG, "Aggiorno l'episodio con id["+episodeId+"] come Sottotitolato");

		return episode;
	}

	public Episode setEpisodeSubbed(long episodeId) throws SQLException{
		Episode episode = getEpisodeDao().queryForId(episodeId);
		episode.setDownloading(true);
		episode.setDownload(true);
		episode.setSub(true);
		episode.setView(false);
		updateEpisode(episode);

		Log.d(LOG_TAG, "Aggiorno l'episodio con id["+episodeId+"] come Sottotitolato");

		return episode;
	}

	public Episode setEpisodeViewed(long episodeId) throws SQLException{
		Episode episode = getEpisodeDao().queryForId(episodeId);
		episode.setDownloading(true);
		episode.setDownload(true);
		episode.setSub(true);
		episode.setView(true);
		updateEpisode(episode);

		Log.d(LOG_TAG, "Aggiorno l'episodio con id["+episodeId+"] come Visto");

		return episode;
	}

	public Episode getLastEpisode() throws SQLException{
		QueryBuilder<Episode, Long> episodeQueryBuilder = getEpisodeDao().queryBuilder();
		Where<Episode, Long> episodeWhere = episodeQueryBuilder.where();

		episodeWhere.and(episodeWhere.le("airTime", Calendar.getInstance().getTime()), 
				episodeWhere.eq("downloading", true), episodeWhere.eq("download", true),
				episodeWhere.eq("sub", true), episodeWhere.eq("view", false));

		episodeQueryBuilder.orderBy("airTime", true);

		PreparedQuery<Episode> preparedQuery = episodeQueryBuilder.prepare();

		Log.d(LOG_TAG, "Eseguo la query: "+preparedQuery.getStatement());

		return getEpisodeDao().queryForFirst(preparedQuery);
	}
	
	public Episode getLastViewEpisode() throws SQLException, NullPointerException{
		QueryBuilder<Action, Long> actionQueryBuilder = getActionDao().queryBuilder();
		Where<Action, Long> actionWhere = actionQueryBuilder.where();

		actionWhere.and(
				actionWhere.le("insertDate", Calendar.getInstance().getTime()), 
				actionWhere.eq("actionType", Action.STARTVIEW), 
				actionWhere.notIn("episode_id", getLastCompletedViewEpisodeIds()));

		actionQueryBuilder.orderBy("insertDate", true);

		PreparedQuery<Action> preparedQuery = actionQueryBuilder.prepare();

		Log.d(LOG_TAG, "Eseguo la query: "+preparedQuery.getStatement());

		return getActionDao().queryForFirst(preparedQuery).getEpisode();
	}
	
	public Long[] getLastCompletedViewEpisodeIds() throws SQLException{
		List<Long> ids = new ArrayList<Long>();
		GenericRawResults<String[]> rawResults = getSerieDao().queryRaw("select episode_id from action where actionType="+Action.ENDVIEW);
		List<String[]> results = rawResults.getResults();
						
		for(String[] array : results){
			ids.add(Long.parseLong(array[0]));
		}
		
		return ids.toArray(new Long[0]);
	}
	
	public Episode getFavouriteEpisodes() throws SQLException{
		List<Long> ids = new ArrayList<Long>();
		String subquery = ("select julianday(max(b.insertDate)) from action b where b.episode_id and b.actiontype in ("+Action.DOWNLOADSUB+", "+Action.ENDDOWNLOAD+")");
		GenericRawResults<String[]> rawResults = getSerieDao().queryRaw("select (julianday(a.insertDate)- ("+subquery+")) as t, s.serie_id, e.episodeId " +
				"from action a, episode e, season s " +
				"where a.actionType = "+Action.STARTVIEW+" " +
				"and e.episodeId=a.episode_id "+
				"and e.season_id = s.seasonId " +
				"group by s.serie_id");
		
		List<String[]> results = rawResults.getResults();
						
		Map<Long, List<Double>> values = new HashMap<Long, List<Double>>();
		
		for(String[] array : results){
			
			Double diff = Double.valueOf(array[0]);
			
			if(diff < 0)
				continue;
			
			Long serieId = Long.parseLong(array[1]);
			Long episodeId = Long.parseLong(array[2]);
			
			if(values.containsKey(serieId))
				values.get(serieId).add(diff);
			else{
				List<Double> lista = new ArrayList<Double>();
				lista.add(diff);
				values.put(serieId, lista);
			}
		}
		
		Map<Double, Long> mapparesult = new TreeMap<Double, Long>();
		
		for(Long id : values.keySet()){
			
			Double sumDiff = 0.0;
			
			for(Double diff : values.get(id))
				sumDiff += diff;
			
			sumDiff = sumDiff/values.get(id).size();
			
			mapparesult.put(sumDiff, id);
			
			/*if(minDiff>sumDiff || minDiff == 0.0){
				minDiff = sumDiff;
				minSerieId = id;
			}*/
		}
		
		
				
		Episode episode = null;
		int i = 0;
		
		for(Entry<Double, Long> entry : mapparesult.entrySet()){
			episode = getLastEpisode(entry.getValue());
		
			if(episode != null){
				Log.d(LOG_TAG, "La serie preferita è: "+getSerieDao().queryForId(entry.getValue()).getShowName());
				Log.d(LOG_TAG, "L'episodio da vedere della serie preferita è: "+episode.getEpisodeName());
				return episode;
			}
		}
		
		return null;
	}
	
	public Episode getLastEpisode(long serieId) throws SQLException{
		QueryBuilder<Episode, Long> episodeQueryBuilder = getEpisodeDao().queryBuilder();
		Where<Episode, Long> episodeWhere = episodeQueryBuilder.where();

		episodeWhere.and(episodeWhere.le("airTime", Calendar.getInstance().getTime()), 
				episodeWhere.eq("downloading", true), episodeWhere.eq("download", true),
				episodeWhere.eq("sub", true), episodeWhere.eq("view", false), 
				episodeWhere.in("season_id", getSeasons(serieId)));

		episodeQueryBuilder.orderBy("airTime", true);

		PreparedQuery<Episode> preparedQuery = episodeQueryBuilder.prepare();

		Log.d(LOG_TAG, "Eseguo la query: "+preparedQuery.getStatement());

		return getEpisodeDao().queryForFirst(preparedQuery);
	}

	public Action insertNewAction(Date insertDate, int actionType, Episode episode,
			String fileSourceName, String fileDestinationName) throws SQLException{
		
		Action action = new Action(insertDate, actionType, episode, fileSourceName, fileDestinationName);
		
		action = getActionDao().createIfNotExists(action);
				
		return action;
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		serieDao = null;
		serieRuntimeDao = null;
		episodeDao = null;
		episodeRuntimeDao = null;
	}
}
