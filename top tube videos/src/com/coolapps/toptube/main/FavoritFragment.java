package com.coolapps.toptube.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.coolapps.toptube.cards.MIMEAdapter;
import com.coolapps.toptube.cards.RowItem;
import com.coolapps.toptube.utils.Category;
import com.coolapps.toptube.utils.DataBase;
import com.coolapps.toptube.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;



@SuppressLint("NewApi")
public class FavoritFragment extends Fragment {
	private List<RowItem> rowItems;
	private InterstitialAd interstitial;
	public Activity ac;
	DataBase db;
	public static Category category;

	public FavoritFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {

			db = new DataBase(Main.ac);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.cards_main, container, false);

		ListView lv = (ListView) rootView.findViewById(R.id.myList);
		rowItems = new ArrayList<RowItem>();

		// Populate the List
		rowItems = db.selectFavorit();
		for (int i = 0; i < rowItems.size(); i++) {
			Log.i("sofiware",
					"lista antes de mostrar favoritos "
							+ rowItems.get(i).getBigdescription() + " "
							+ rowItems.get(i).getTitle() + " "
							+ rowItems.get(i).getImageId() + " "
							+ rowItems.get(i).getType());
		}

		// Set the adapter on the ListView
		if (rowItems.size() > 0) {
			MIMEAdapter adapter = new MIMEAdapter(getActivity()
					.getApplicationContext(), R.layout.favorit_item, rowItems,
					true);
			lv.setAdapter(adapter);
		}
		admobIntr();
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {

		super.onPause();
	}

	@Override
	public void onDestroy(){
		// TODO Auto-generated method stub

		super.onDestroy();
	}

	public void admobIntr(){
		// Create the interstitial.
		interstitial = new com.google.android.gms.ads.InterstitialAd(getActivity());
		interstitial
				.setAdUnitId(com.coolapps.toptube.utils.Configuration.ID_ADS_INTERST);

		// Create ad request.
		// AdRequest adRequest = new AdRequest.Builder().build();
		Bundle bundle = new Bundle();
		bundle.putString("color_bg", "CCCCCC");
		bundle.putString("color_bg_top", "EEEEEE");
		bundle.putString("color_border", "FFFFFF");
		bundle.putString("color_link", "00cccc");
		bundle.putString("color_text", "808080");
		bundle.putString("color_url", "008000");

		AdMobExtras extras = new AdMobExtras(bundle);
		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
		// Emulator
				.addTestDevice("45CEFC883F30B8758E2C39EFC8C549C6")
				.tagForChildDirectedTreatment(true).addNetworkExtras(extras)
				.build();
		// Begin loading your interstitial.
		interstitial.loadAd(adRequest);
		   Timer t= new Timer();  
		   t.schedule(new TimerC(),30000);
			
		}
		
	
	public class TimerC extends TimerTask{

		@Override
		public void run(){
			// TODO Auto-generated method stub
			 Log.w("randomNum","run executed");
			 Random rn = new Random();
			    int range = 1000 - 0 + 1;
			    int randomNum =  rn.nextInt(range);
			    
			    if(randomNum<100){
			     Log.w("randomNum","randomNum "+randomNum);
			     hand.sendEmptyMessage(0);
			    }
		}
		
		
		
	}
	handler hand= new handler();
	public class handler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			   Log.w("randomNum","handleMessage ");
			 displayInterstitial();
			super.handleMessage(msg);
		}
	}
	
		
	public void displayInterstitial(){
		Log.w("test", "displayInterstitial");
		if (interstitial.isLoaded()) {
			Log.w("test", "displayInterstitial Loaded");
			interstitial.show();
		}
	}

}