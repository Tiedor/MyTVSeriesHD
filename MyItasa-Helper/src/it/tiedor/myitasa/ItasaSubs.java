package it.tiedor.myitasa;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.HttpParams;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

public class ItasaSubs {

	private final static String LOG_TAG = "MyItasaHelper - "+ItasaSubs.class.getSimpleName();
	private final static String ITASA_URL = "https://api.italiansubs.net/api/rest/";
	private final static String apikey = "bbd30f07ae11720866a87786a52c14f0";



	public static String login(String username, String password) throws MyITASAException{

		try{

			Document dom = null;

			DefaultHttpClient mHttpClient = new DefaultHttpClient();
			HttpGet mHttpGet = new HttpGet(ITASA_URL+"users/login?apikey="+apikey+"&username="+username+"&password="+password);
			HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
			if (mHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = mHttpResponse.getEntity();
				if ( entity != null) {        
					dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(entity.getContent());
				}
			}

			if(dom == null)
				throw new MyITASAException("Impossibile effettuare la login, il dom è nullo");

			Log.d(LOG_TAG, " Ho la risposta!");
			XPathFactory factory = XPathFactory.newInstance();
			XPath xPath = factory.newXPath();

			NodeList authcode = (NodeList) xPath.evaluate("/root/data/user/authcode", dom, XPathConstants.NODESET);

			return authcode.item(0).getTextContent();

		}catch(Exception e){
			throw new MyITASAException("Impossibile effettuare la login", e);
		}
	}

	public static ArrayList<String[]> getFavouriteShows(String authcode) throws MyITASAException{
		
		ArrayList<String[]> result = new ArrayList<String[]>();
		
		try{

			Document dom = null;

			DefaultHttpClient mHttpClient = new DefaultHttpClient();
			Log.d(LOG_TAG, ITASA_URL+"myitasa/shows?apikey="+apikey+"&authcode="+authcode);
			HttpGet mHttpGet = new HttpGet(ITASA_URL+"myitasa/shows?apikey="+apikey+"&authcode="+authcode);
			HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
			if (mHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = mHttpResponse.getEntity();
				if ( entity != null) {        
					dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(entity.getContent());
				}
			}

			if(dom == null)
				throw new MyITASAException("Impossibile trovare la serie cercata");

			XPathFactory factory = XPathFactory.newInstance();
			XPath xPath = factory.newXPath();

			NodeList nl = (NodeList) xPath.evaluate("/root/data/shows/show", dom, XPathConstants.NODESET);

			for(int i = 0; i<nl.getLength(); i++){
				Node n = nl.item(i);
				String id = n.getChildNodes().item(0).getTextContent();
				String name = n.getChildNodes().item(1).getTextContent();
				result.add(new String[]{id, name});
			}

			return result;
		}catch(Exception e){
			Log.e(LOG_TAG, "Impossibile trovare la serie cercata", e);
			throw new MyITASAException("Impossibile trovare la serie cercata");
		}
	}

	public static String searchShow(String seriesName, String regex) throws MyITASAException{

		try{

			Document dom = null;

			DefaultHttpClient mHttpClient = new DefaultHttpClient();
			seriesName = URLEncoder.encode(seriesName);
			Log.d(LOG_TAG, ITASA_URL+"shows/search?apikey="+apikey+"&q="+seriesName);
			HttpGet mHttpGet = new HttpGet(ITASA_URL+"shows/search?apikey="+apikey+"&q="+seriesName);
			HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
			if (mHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = mHttpResponse.getEntity();
				if ( entity != null) {        
					dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(entity.getContent());
				}
			}

			if(dom == null)
				throw new MyITASAException("Impossibile trovare la serie cercata");

			XPathFactory factory = XPathFactory.newInstance();
			XPath xPath = factory.newXPath();

			NodeList nl = (NodeList) xPath.evaluate("/root/data/shows/show", dom, XPathConstants.NODESET);



			for(int i = 0; i<nl.getLength(); i++){
				Node n = nl.item(i);
				String id = n.getChildNodes().item(0).getTextContent();
				String name = n.getChildNodes().item(1).getTextContent();
				Log.d(LOG_TAG, name);
				if(name.equalsIgnoreCase(regex)){
					return id;
				}
			}

			Pattern pattern = Pattern.compile(regex, Pattern.UNICODE_CASE);

			for(int i = 0; i<nl.getLength(); i++){
				Node n = nl.item(i);
				String id = n.getChildNodes().item(0).getTextContent();
				String name = n.getChildNodes().item(1).getTextContent();
				Log.d(LOG_TAG, name);
				Matcher m = pattern.matcher(name);
				if(m.find()){
					return id;
				}
			}

			throw new MyITASAException("Impossibile trovare la serie cercata");
		}catch(Exception e){
			e.printStackTrace();
			throw new MyITASAException("Impossibile trovare la serie cercata");
		}

	}

	public static String searchEpisode(String showId, String regex) throws MyITASAException{

		return searchEpisode(ITASA_URL+"subtitles?show_id="+showId+"&apikey="+apikey, regex, 1);

	}

	public static String searchEpisode(String url, String regex, int page) throws MyITASAException{

		try{

			Document dom = null;

			DefaultHttpClient mHttpClient = new DefaultHttpClient();
			HttpGet mHttpGet = new HttpGet(url+"&page="+page);
			HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
			if (mHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = mHttpResponse.getEntity();
				if ( entity != null) {        
					dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(entity.getContent());
				}
			}

			if(dom == null)
				throw new MyITASAException("Impossibile trovare l'episodio cercato");


			XPathFactory factory = XPathFactory.newInstance();
			XPath xPath = factory.newXPath();

			NodeList nl = (NodeList) xPath.evaluate("/root/data/subtitles/subtitle", dom, XPathConstants.NODESET);

			Pattern pattern = Pattern.compile(regex, Pattern.UNICODE_CASE);

			for(int i = 0; i<nl.getLength(); i++){
				Node n = nl.item(i);
				String id = n.getChildNodes().item(0).getTextContent();
				String name = n.getChildNodes().item(1).getTextContent();
				String version = n.getChildNodes().item(2).getTextContent();
				//Log.d(LOG_TAG, name+" --> "+regex);
				Matcher m = pattern.matcher(name);
				if(m.find() && version.contains("Normal")){
					return id;
				}
			}

			String next = ((NodeList)xPath.evaluate("/root/data/next", dom, XPathConstants.NODESET)).item(0).getTextContent();
			//Log.d(LOG_TAG, "Next: "+next);

			if(next != null && !next.equals("")){
				return searchEpisode(url, regex, ++page);
			}

			throw new MyITASAException("Impossibile trovare l'episodio cercato");
		}catch(Exception e){
			throw new MyITASAException("Impossibile trovare l'episodio cercato");
		}
	}

	public static String getTVDBIdFromShow(String showId) throws MyITASAException{
		Document dom = null;
		try{
			DefaultHttpClient mHttpClient = new DefaultHttpClient();

			Log.d(LOG_TAG, ITASA_URL+"shows/"+showId+"?apikey="+apikey);
			HttpGet mHttpGet = new HttpGet(ITASA_URL+"shows/"+showId+"?apikey="+apikey);
			HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
			if (mHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = mHttpResponse.getEntity();
				if ( entity != null) {        
					dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(entity.getContent());
				}
			}

			if(dom == null)
				throw new MyITASAException("Impossibile trovare la serie cercata");

			XPathFactory factory = XPathFactory.newInstance();
			XPath xPath = factory.newXPath();

			NodeList nl = (NodeList) xPath.evaluate("/root/data/show/id_tvdb", dom, XPathConstants.NODESET);

			for(int i = 0; i<nl.getLength(); i++){
				Node n = nl.item(i);
				String thetvdbid = n.getChildNodes().item(0).getTextContent();
				return thetvdbid;
			}

			throw new MyITASAException("Impossibile trovare la serie cercata");
		}catch(Exception e){
			e.printStackTrace();
			throw new MyITASAException("Impossibile trovare la serie cercata");
		}
	}

	public static String downloadEpisode(Context ctx, String url, File path) throws MyITASAException{

		try{

			CookieManager cookieManager = new CookieManager();
			CookieHandler.setDefault(cookieManager);

			org.jsoup.nodes.Document dom = null;
			Connection con;
			Map<String, String> cookies;

			DefaultHttpClient mHttpClient = new DefaultHttpClient();
			HttpGet mHttpGet = new HttpGet(url);
			HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
			if (mHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = mHttpResponse.getEntity();
				if ( entity != null) {        
					dom= Jsoup.parse(entity.getContent(), "UTF-8", "http://www.italiansubs.net/");
				}
			}

			if(dom == null)
				throw new MyITASAException("Impossibile trovare l'episodio cercato");

			dom.select("form#form-login > div > input#modlgn_username").attr("value", PreferenceManager.getDefaultSharedPreferences(ctx).getString("itasa_user", null));
			dom.select("form#form-login > div > input#modlgn_passwd").attr("value", PreferenceManager.getDefaultSharedPreferences(ctx).getString("itasa_password", null));

			FormElement formElement = (FormElement) dom.select("form#form-login").get(0);
			Connection conn = formElement.submit();
			dom = conn.execute().parse();

			String myurl = dom.select("div#remositoryfileinfo > dt > center > a").attr("href");

			if(myurl == null || myurl.equals(""))
				throw new MyITASAException("Link non trovato");

			File file = copyFromUrl(myurl, path);

			UnZip unZip = new UnZip();
			unZip.unZipIt(file, path.getAbsolutePath()+File.separator+file.getName().replace(".zip", ""));

			return path.getAbsolutePath()+File.separator+file.getName().replace(".zip", "");

		}catch(MyITASAException e){
			throw e;
		}catch(Exception e){
			throw new MyITASAException("Impossibile trovare l'episodio cercato", e);
		}
	}

	private static File copyFromUrl(String myurl, File path) throws Exception{
		byte[] buffer = new byte[1024];
		int bytesRead;
		BufferedInputStream inputStream = null;
		BufferedOutputStream outputStream = null;
		File f = null;

		try{
			URL url = new URL(myurl);
			URLConnection connection = url.openConnection();
			inputStream = new BufferedInputStream(connection.getInputStream());
			String filename = connection.getHeaderField("Content-Disposition");
			System.out.println(filename);
			f = new File(path+File.separator+connection.getHeaderField("Content-Disposition").substring(21));
			outputStream = new BufferedOutputStream(new FileOutputStream(f));
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

		}finally{
			try{
				outputStream.close();
			}catch(Exception e){}
		}

		return f;
	}
}
