package it.tiedor.torrentdownloader;

import java.net.URI;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class TorrentHelper {

	public static String downloadTorrent(String myurl) throws Exception{
			
		Document dom = null;
		
		DefaultHttpClient mHttpClient = new DefaultHttpClient();
		System.out.println(myurl);
		HttpGet mHttpGet = new HttpGet(myurl);
		HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
		if (mHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity entity = mHttpResponse.getEntity();
			if ( entity != null) {        
				dom= Jsoup.parse(entity.getContent(), "UTF-8", myurl);
			}
		}

		if(dom == null)
			throw new MyTorrentException("Impossibile trovare l'episodio cercato");
		
		String magnet = dom.select("a.imagnet").get(0).attr("href");
		
		return magnet;
	}

}
