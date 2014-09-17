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

import com.coolapps.toptube.R;
import com.coolapps.toptube.cards.MIMEAdapter;
import com.coolapps.toptube.cards.RowItem;
import com.coolapps.toptube.main.Main.OnEventSearchListener;
import com.coolapps.toptube.utils.Category;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;


@SuppressLint({ "ValidFragment", "NewApi" })
public class MultimediaFragment extends Fragment implements
		OnEventSearchListener {
	private List<RowItem> rowItems;
	public Activity ac;
	public Category category;
	MIMEAdapter adapter;
	ListView lv;
	Timer t = new Timer();
	private InterstitialAd interstitial;

	public MultimediaFragment() {
		Main.addOnSearchListener(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Log.i("sofiware", "onCreate Freamgent");
			this.category = (Category) getArguments().getSerializable(
					"category");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Bundle args = getArguments();
		// this.category= (Category) args.getSerializable("categroy");

		if (this.category != null) {
			Log.i("sofiware", "FUNCIONA");
		} else {
			Log.i("sofiware", "no hay datos");
		}

		View rootView = inflater.inflate(R.layout.cards_main, container, false);
		lv = (ListView) rootView.findViewById(R.id.myList);
		rowItems = new ArrayList<RowItem>();

		// Populate the List
		for (int i = 0; i < category.contents.size(); i++) {
			RowItem item = new RowItem(category.contents.get(i).url,
					category.contents.get(i).title,
					category.contents.get(i).smalltext,
					category.contents.get(i).bigtext,
					category.contents.get(i).type, category.playlist,
					category.contents.get(i).news);
			rowItems.add(item);
		}

		// Set the adapter on the ListView
		if (rowItems.size() > 0) {

			adapter = new MIMEAdapter(getActivity().getApplicationContext(),
					R.layout.cards_item, rowItems, false);

			lv.setAdapter(adapter);
		}
		admobIntr();
		return rootView;
	}

	
	@Override
	public void onSearch(String text) {
		// TODO Auto-generated method stub
		Log.w("test1", "onSearch yes " + text);

		search(text);
	}

	public void search(String textSearch) {
		List<RowItem> listaAux = new ArrayList<RowItem>();
		List<RowItem> listaAux1 = new ArrayList<RowItem>();
		for (int i = 0; i < category.contents.size(); i++) {
			RowItem item = new RowItem(category.contents.get(i).url,
					category.contents.get(i).title,
					category.contents.get(i).smalltext,
					category.contents.get(i).bigtext,
					category.contents.get(i).type, category.playlist,
					category.contents.get(i).news);
			listaAux1.add(item);
		}
		if (!textSearch.equals("")) {

			Log.w("test3", "is not. empty " + listaAux1.size());
			Log.w("test3", "search::" + textSearch + "::");
			for (int i = 0; i < listaAux1.size(); i++) {
				if (listaAux1.get(i).getTitle().contains(textSearch)) {
					listaAux.add(listaAux1.get(i));
					Log.w("test1", "encontrado " + listaAux1.get(i).getTitle());

				}
			}
			Log.w("test3", "size  list selected" + listaAux.size());
			adapter.clear();
			adapter.addAll(listaAux);
			adapter.notifyDataSetChanged();

		} else {
			Log.w("test3", "is empty " + listaAux1.size());
			Log.w("test3", "search::" + textSearch + "::");
			adapter.clear();
			adapter.addAll(listaAux1);
			adapter.notifyDataSetChanged();

		}

	}

	public void admobIntr() {
		// Create the interstitial.
		interstitial = new InterstitialAd(getActivity());
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
		try {
			t.schedule(new TimerC(), 30000);
		} catch (Exception e) {

		}
	}

	
	public class TimerC extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.w("randomNum", "run executed");
			Random rn = new Random();
			int range = 1000 - 0 + 1;
			int randomNum = rn.nextInt(range);
          
			if (randomNum <100) {
				Log.w("randomNum", "randomNum " + randomNum);
				hand.sendEmptyMessage(0);
				t.cancel();
			} else {
				try {
					Log.w("randomNum", "randomNum " + randomNum);
					t.schedule(new TimerC(), 30000);
				} catch (Exception e) {

				}
			}
		}

	}

	handler hand = new handler();

	public class handler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Log.w("randomNum", "handleMessage ");
			displayInterstitial();
			super.handleMessage(msg);
		}
	}

	public void displayInterstitial() {
		Log.w("test", "displayInterstitial");
		if (interstitial.isLoaded()) {
			Log.w("test", "displayInterstitial Loaded");
			interstitial.show();
		}
	}
}
