package com.coolapps.toptube.utils;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;


public class Configuration {
	// //////////////////Message//////////////////////////////////////////
	public static String SHARE = "https://play.google.com/store/apps/details?id=com.coolapps.toptube"+" Descarga la aplicación para ver mas contenido ";
	public static String URL = "https://www.dropbox.com/s/svo75khujobnyg8/Fashion.xml?dl=1";
	public static String MESSAGE_REFRESH = "manten pulsado el boton para refrescar";
	public static String MESSAGE_ERROR_NOT_DOWNLOADED = "no se puede descargar el contenido";
	// ///////////////////////Category///////////////////////////
	public static String BACKGROUNG_COLOR_CATEGORY = "#FA5858";
	public static String TEXT_COLOR_CATEGORY = "#FFFFFF";
	// ////////////////////////Configuration of ADS///////////////
	public static String color_bg_ADS = "#FF0040";
	public static String color_bg_top_ADS = "#FF0040";
	public static String color_border_ADS = "#FF0040";
	public static String color_link_ADS = "#FFFFFF";
	public static String color_text_ADS = "#FFFFFF";
	public static String color_url_ADS = "#FFFFFF";
	
	public static String ID_ADS_BANNER = "ca-app-pub-8534415356722864/1262651230";
	public static String ID_ADS_INTERST ="ca-app-pub-8534415356722864/2739384438";
	// /////////////////////Configuration URI Storage//////////////
	public static String URI_STORAGE = Environment
			.getExternalStorageDirectory() + "/Fashion.xml";
	public static int publishedIN=(1000*60*60*24*8);   // hace cuando se publicó el video en milisegundos

	// //////////////////////////////////////////
	public static String[] URLS = {
			"http://gdata.youtube.com/feeds/api/videos?max-results=50&alt=json&orderby=published&q=music",
			"http://gdata.youtube.com/feeds/api/videos?max-results=50&alt=json&orderby=viewCount&q=music",
			"http://gdata.youtube.com/feeds/api/videos?max-results=50&alt=json&q=music"
			};
	public static String[] URLS_EN = {
		"http://gdata.youtube.com/feeds/api/videos?max-results=50&alt=json&orderby=published&q=music",
		"http://gdata.youtube.com/feeds/api/videos?max-results=50&alt=json&orderby=viewCount&q=music",
		"http://gdata.youtube.com/feeds/api/videos?max-results=50&alt=json&q=music"
		};
	public static String[] NAME = { "Recientes", "Más vistos", "Mejor valorados" };
	public static String[] NAME_EN = { "Recents", "Most viewer", "Most rate" };
	public static boolean XMLREADER = false;
	public static String Mainurl = "";
    public static int TIMER_NOTIFICATION=60000 *60*2;
}
