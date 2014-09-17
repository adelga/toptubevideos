package com.coolapps.toptube.main;



import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coolapps.toptube.cards.NewsAdapter;
import com.coolapps.toptube.cards.RowItem;
import com.coolapps.toptube.flip.FlipViewController;
import com.coolapps.toptube.main.Main.OnEventSearchListener;
import com.coolapps.toptube.utils.Category;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;

/*  Fragment para seccion perfil */ 
@SuppressLint("NewApi")
public class HomeFragment extends Fragment{
	FlipViewController  flipView;
	public Category category;
	private InterstitialAd interstitial;
	
	NewsAdapter adapter;
	ArrayList<RowItem> rowItems;
	
    public HomeFragment(){
    	Main.addOnSearchListener(new OnEventSearchListener() {
			
			@Override
			public void onSearch(String text) {
				// TODO Auto-generated method stub
				Log.w("test3","search:"+text+":");
				//search(text);
			}
		});
    	
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    try{
	    	Log.i("sofiware","onCreate Freamgent"); 
	    this.category=   (Category) getArguments().getSerializable("news");
	    
	    }catch(Exception e){
	    	   e.printStackTrace();
	       }
	} 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        flipView = new FlipViewController(Main.ac, FlipViewController.HORIZONTAL);
   	 rowItems = new ArrayList<RowItem>();
	 
	 
	 
     //Populate the List
     for (int i = 0; i < category.contents.size(); i++) {
         RowItem item = new RowItem(category.contents.get(i).url, category.contents.get(i).title, category.contents.get(i).smalltext,category.contents.get(i).bigtext,category.contents.get(i).type,category.playlist,category.contents.get(i).news);
         rowItems.add(item);
     }
        adapter = new NewsAdapter(Main.ac,rowItems);
        flipView.setAdapter(adapter);
        //View rootView = inflater.inflate(R.layout.home, container, false);
        View rootView =  flipView;
        admobIntr();
        return rootView;
    }
    
    @Override
	public void onResume() {
      super.onResume();
      flipView.onResume();
//      if (adView != null) {
//	      adView.resume();
//	    }
    }

    @Override
	public void onPause() {
      super.onPause();
      flipView.onPause();
//      if (adView != null) {
//	      adView.pause();
//	    }
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
			 adapter = new NewsAdapter(Main.ac,listaAux);
		        flipView.setAdapter(adapter);

		} else {
			Log.w("test3", "is empty " + listaAux1.size());
			Log.w("test3", "search::" + textSearch + "::");
		
			 adapter = new NewsAdapter(Main.ac,listaAux1);
		        flipView.setAdapter(adapter);
		}

	}
    
	
	

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
		super.onDestroy();
	} 
	public void admobIntr(){
		// Create the interstitial.
	    interstitial = new InterstitialAd(getActivity());
	    interstitial.setAdUnitId(com.coolapps.toptube.utils.Configuration.ID_ADS_INTERST);
       
   
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
	    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)       // Emulator
	    .addTestDevice("45CEFC883F30B8758E2C39EFC8C549C6") 
	    .tagForChildDirectedTreatment(true)
	        .addNetworkExtras(extras).build();
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
	
	
	 public void displayInterstitial() {
		  Log.w("test","displayInterstitial");
	    if (interstitial.isLoaded()) {
	    	Log.w("test","displayInterstitial Loaded");
	      interstitial.show();
	    }
	  }
    
}