package com.coolapps.toptube.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.coolapps.toptube.utils.XmlDownloader.OnDownloadedListener;

public class RefreshContentsXML {
	public Model model = new Model();

	private TextView cargar;
	private XMLReader xr;
	ProgressBar barra;
	Handler mhandler;
	private XmlParser myHandler;
	private String tag = "RefrechContentsYoutube";
	private ParserXMLAsync tarea1;
	private ParserJSONYOUTAsync tarea2;
	// private static final String CONFIG_PATH = Environment
	// .getExternalStorageDirectory() + "/Download/Videos.xml";
	// private static final String CONFIG_PATH = get;
	public XmlDownloader xmlD;
	public Context cntx;
	String[] categorys ;
	ArrayList<Category> categorias = new ArrayList<Category>();
	protected static final int ERROR = 25;

	public RefreshContentsXML() {
	}

	public void refreshContentsXML(Context context, TextView textview,
			ProgressBar barra, Handler handler) {
		cntx = context;
		this.barra = barra;
		cargar = textview;
		this.mhandler = handler;
		xmlD = new XmlDownloader(cntx, barra);

		xmlD.addOnDownloadedListener(new OnDownloadedListener() {

			@Override
			public void OnDownloaded(String succes) {
				// TODO Auto-generated method stub
				Log.i("test", "OnDownloadedListener " + succes);
				File fichero = new File(Configuration.URI_STORAGE);
				if (succes.contains("succes") || fichero.exists()) {
					tarea1 = new ParserXMLAsync();
					tarea1.execute();
				} else {
					Toast.makeText(cntx.getApplicationContext(),
							Configuration.MESSAGE_ERROR_NOT_DOWNLOADED,
							Toast.LENGTH_SHORT).show();
					mhandler.sendEmptyMessage(0);
				}
			}
		});

	}

	private class ParserXMLAsync extends AsyncTask<Void, Integer, Boolean> {
		int progress = 0;

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			readXML();

			for (int i = 0; i < model.bloq.size(); i++) {
				for (int j = 0; j < model.bloq.get(i).contents.size(); j++) {
					try {
						Thread.sleep(3);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					progress = progress
							+ ((50 / (model.bloq.get(i).contents.size() * (model.bloq
									.size()))));

					publishProgress(progress);
				}
			}
			return true;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			int progreso = values[0].intValue();
			if (barra != null) {
				barra.setProgress(progreso);
			}

		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (barra != null)
				barra.setProgress(100);
			mhandler.sendEmptyMessage(0);
		}
	}

	private void readXML() {
		try {
			myHandler = new XmlParser();

			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp;

			sp = spf.newSAXParser();

			xr = sp.getXMLReader();

			xr.setContentHandler(myHandler);
			// AssetManager assetMan = getAssets();
			// InputStream is = assetMan.open("Config.xml");

			xr.parse(new InputSource(new FileInputStream(new File(
					Configuration.URI_STORAGE))));

			model = myHandler.getModel();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void RefrechContentsYoutube(Context context, ProgressBar barra,
			Handler handler) {
		cntx = context;
		this.barra = barra;

		this.mhandler = handler;
		tarea2 = new ParserJSONYOUTAsync();
		tarea2.execute();

	}

	/*
	 * private class YoutubeJSONTask extends AsyncTask<String, Void, Void> {
	 * 
	 * @Override protected void onProgressUpdate(Void... values) { // TODO
	 * Auto-generated method stub int progreso = 0; if (barra != null) {
	 * barra.setProgress(progreso++); } super.onProgressUpdate(values); }
	 * 
	 * protected Void doInBackground(String... urls) { try { // creacion de la
	 * categoria favoritos Category favorit= new Category();
	 * favorit.tag="Favorit"; favorit.type="favorit"; categorias.add(favorit);
	 * for (int j = 0; j < categorys.length; j++) { Category category = new
	 * Category(); category.tag = Configuration.NAME[j]; List<Content>
	 * listConten = new ArrayList<Content>(); DefaultHttpClient httpClient = new
	 * DefaultHttpClient(); HttpGet httpGet = new HttpGet(categorys[j]);
	 * ResponseHandler<String> resHandler = new BasicResponseHandler(); String
	 * page = httpClient.execute(httpGet, resHandler); boolean finish = false;
	 * Log.v(tag, "page: " + page);
	 * 
	 * JSONObject json = new JSONObject(page) .getJSONObject("feed"); JSONArray
	 * array = json.getJSONArray("entry"); Log.d(tag, "num" + array.length());
	 * for (int i = 0; i < array.length(); i++) { Content conten = new
	 * Content(); // yyyy-MM-ddTHH:mm:ss conten.url = array .getJSONObject(i)
	 * .getString("id") .substring( array.getJSONObject(i).getString("id")
	 * .indexOf("http:"), array.getJSONObject(i).getString("id") .length() - 2);
	 * Calendar cal = Calendar.getInstance(); SimpleDateFormat fomrat = new
	 * SimpleDateFormat( "yyyy-MM-ddTHH:mm:ss");
	 * 
	 * cal.setTime(fomrat.parse(array .getJSONObject(i) .getString("published")
	 * .substring( array.getJSONObject(i) .getString("published") .indexOf("$t")
	 * + 5, array.getJSONObject(i) .getString("published") .length() - 2)));
	 * conten.date = cal;
	 * 
	 * conten.title = array .getJSONObject(i) .getString("title") .substring(
	 * array.getJSONObject(i) .getString("title") .indexOf("$t") + 5,
	 * array.getJSONObject(i) .getString("title").length() - 2); conten.bigtext
	 * = array .getJSONObject(i) .getString("content") .substring(
	 * array.getJSONObject(i) .getString("content") .indexOf("$t") + 5,
	 * array.getJSONObject(i) .getString("content").length() - 2);
	 * conten.smalltext = array .getJSONObject(i) .getString("content")
	 * .substring( array.getJSONObject(i) .getString("content") .indexOf("$t") +
	 * 5, array.getJSONObject(i) .getString("content").length() - 2)
	 * .substring(0, 19); listConten.add(conten); } category.contents =
	 * listConten; categorias.add(category); } model.bloq = categorias; } catch
	 * (ClientProtocolException e) { e.printStackTrace();
	 * 
	 * } catch (Exception e) { e.printStackTrace();
	 * 
	 * }
	 * 
	 * return null;
	 * 
	 * }
	 * 
	 * @Override protected void onPostExecute(Void result) { // TODO
	 * Auto-generated method stub if (barra != null) barra.setProgress(100);
	 * mhandler.sendEmptyMessage(0); super.onPostExecute(result); }
	 * 
	 * }
	 */

	private class ParserJSONYOUTAsync extends AsyncTask<Void, Integer, Boolean> {
		int progress = 0;

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				String locale=Locale.getDefault().getLanguage();
				boolean en=true;
				if(!locale.contains("en")){
					en=false;
				}
				if(en){
					categorys= Configuration.URLS_EN;

				}else
				categorys= Configuration.URLS;
				// creacion de la categoria favoritos
				Category favorit = new Category();
				favorit.tag = "Favorit";
				favorit.type = "favorit";
				favorit.icon= "http://www.fuett.mx/wp-content/uploads/2014/06/youtube-chains-650-430.jpg";
				categorias.add(favorit);
				for (int j = 0; j < categorys.length; j++) {
					
					Category category = new Category();
					if(en)
					category.tag = Configuration.NAME[j];
					else
						category.tag = Configuration.NAME_EN[j];
					category.type="video";
					category.icon= "https://photos-3.dropbox.com/t/0/AACBhW1DtuFeaqUF71iwGljeiRwdrlO0OwOT0Z432yNvQw/12/109734988/png/1024x768/3/1407445200/0/2/iconTube.png/zyIfVIeLdLxA0SXRTno-JTiE0MYmbtWMMvlrjnnzvLc";
					List<Content> listConten = new ArrayList<Content>();
					DefaultHttpClient httpClient = new DefaultHttpClient();
					HttpGet httpGet = new HttpGet(categorys[j]);
					ResponseHandler<String> resHandler = new BasicResponseHandler();
					String page = httpClient.execute(httpGet, resHandler);
					boolean finish = false;
					Log.v(tag, "page: " + page);

					JSONObject json = new JSONObject(page)
							.getJSONObject("feed");
					JSONArray array = json.getJSONArray("entry");
					Log.d(tag, "num" + array.length());
					for (int i = 0; i < array.length(); i++) {
						
						
						Content conten = new Content();
						// yyyy-MM-ddTHH:mm:ss
						conten.url = array
								.getJSONObject(i)
								.getString("id")
								.substring(
										array.getJSONObject(i).getString("id")
												.indexOf("http:"),
										array.getJSONObject(i).getString("id")
												.length() - 2);
						 Calendar cal = Calendar.getInstance();
						 
						 SimpleDateFormat fomrat = new SimpleDateFormat(
						 "yyyy-MM-dd HH:mm:ss");
						
						 cal.setTime(fomrat.parse(array
						 .getJSONObject(i)
						 .getString("published")
						 .substring(
						 array.getJSONObject(i)
						 .getString("published")
						 .indexOf("$t") + 5,
						 array.getJSONObject(i)
						 .getString("published")
						 .length() - 2).replace("T"," ")));
						 conten.date = cal;
						 Calendar actual= Calendar.getInstance();
						 Log.w("Calnedar","old: "+ actual.getTime().getTime());
						 Log.w("Calnedar","test: "+ cal.getTime().getTime());
                         if((actual.getTime().getTime()-cal.getTime().getTime())<Configuration.publishedIN){
                        	 conten.news=true;	 
                         }
						 
						conten.type="video";
						conten.title = array
								.getJSONObject(i)
								.getString("title")
								.substring(
										array.getJSONObject(i)
												.getString("title")
												.indexOf("$t") + 5,
										array.getJSONObject(i)
												.getString("title").length() - 2);
						conten.bigtext = array
								.getJSONObject(i)
								.getString("content")
								.substring(
										array.getJSONObject(i)
												.getString("content")
												.indexOf("$t") + 5,
										array.getJSONObject(i)
												.getString("content").length() - 2);
						conten.smalltext = array
								.getJSONObject(i)
								.getString("content")
								.substring(
										array.getJSONObject(i)
												.getString("content")
												.indexOf("$t") + 5,
										array.getJSONObject(i)
												.getString("content").length() - 2)
								.length() < 19 ? array
								.getJSONObject(i)
								.getString("content")
								.substring(
										array.getJSONObject(i)
												.getString("content")
												.indexOf("$t") + 5,
										array.getJSONObject(i)
												.getString("content").length() - 2)
								: array.getJSONObject(i)
										.getString("content")
										.substring(
												array.getJSONObject(i)
														.getString("content")
														.indexOf("$t") + 5,
												array.getJSONObject(i)
														.getString("content")
														.length() - 2)
										.substring(0, 19);
									
										conten.url=conten.url.split("/")[conten.url.split("/").length-1];
						Log.w("test_url",conten.url);
										listConten.add(conten);
					}
					try{
					category.contents.clear();
					model.bloq.clear();
					}catch(Exception e){
						
					}
					category.contents.addAll(listConten);
					categorias.add(category);
					if(j>0)progress=categorys.length/j *100;
					onProgressUpdate(progress);
				}
				model.bloq = categorias;
				return true;
			} catch (ClientProtocolException e) {
				e.printStackTrace();

			} catch (Exception e) {
				e.printStackTrace();

			}

			return false;

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			int progreso = values[0].intValue();
			if (barra != null) {
				barra.setProgress(progreso);
			}

		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (barra != null)
				barra.setProgress(100);
			if(result)
			mhandler.sendEmptyMessage(0);
			else
				mhandler.sendEmptyMessage(ERROR);
		}
	}

}
