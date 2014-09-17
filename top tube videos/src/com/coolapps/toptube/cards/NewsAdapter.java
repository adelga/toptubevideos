package com.coolapps.toptube.cards;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.coolapps.toptube.aphid.utils.AphidLog;
import com.coolapps.toptube.aphid.utils.IO;
import com.coolapps.toptube.aphid.utils.UI;
import com.coolapps.toptube.cards.RowItem;
import com.coolapps.toptube.main.Item_objct;
import com.coolapps.toptube.main.Main;
import com.coolapps.toptube.script.network.UrlImageDownloader;
import com.coolapps.toptube.script.network.AbstractImageDownloader.ProgressListener;
import com.coolapps.toptube.utils.Configuration;
import com.coolapps.toptube.utils.DataBase;
import com.coolapps.toptube.utils.YTPlayer;
import com.coolapps.toptube.*;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	public LinearLayout lprogress,ladview;
	private int repeatCount = 1;
	private UrlImageDownloader mImageDownloader;
	private List<RowItem> lista;
	public YTPlayer youtube;
	YouTubeThumbnailLoader youTubeThumbnail;
	YouTubeThumbnailView youTubeThumbnailView;
	DataBase db;
	ImageView type, favorit, share, photo;
	TextView title, text;
    Context cnt;
	public NewsAdapter(Context context, List<RowItem> items) {
		inflater = LayoutInflater.from(context);
		lista = items;
		mImageDownloader = new UrlImageDownloader(Main.ac);
		db = new DataBase(context);
		cnt=context;
	}

	@Override
	public int getCount() {
		return lista.size() * repeatCount;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View layout = convertView;
		if (convertView == null) {
			layout = inflater.inflate(R.layout.home_item, null);
			AphidLog.d("created new view from adapter: %d", position);
		} 
		
//		AdView adView = UI.<AdView>findViewById(layout,R.id.adView);
//		  adView.setAdSize(AdSize.SMART_BANNER);
//		    adView.setAdUnitId(Configuration.ID_ADS);
//		AdRequest adRequest = new AdRequest.Builder()
//		.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//		.addTestDevice("45CEFC883F30B8758E2C39EFC8C549C6")
//		.build();
//		adView.loadAd(adRequest);
		
		lprogress = UI.<LinearLayout> findViewById(layout, R.id.lprogress);
		title = UI.<TextView> findViewById(layout, R.id.title);

		title.setText(AphidLog.format("%d. %s", position, lista.get(position)
				.getTitle()));
		type = UI.<ImageView> findViewById(layout, R.id.typenew);

		favorit = UI.<ImageView> findViewById(layout, R.id.favoritnew);
		favorit.setImageResource(R.drawable.ic_action_not_important);

		share = UI.<ImageView> findViewById(layout, R.id.sharenew);
		share.setImageResource(R.drawable.ic_action_share);
		// /////////////////////////////
		photo = UI.<ImageView> findViewById(layout, R.id.photo);
		try{
        db.vistoNews(lista.get(position).getImageId());
		}catch(Exception e){
			
		}
		photo.setContentDescription("" + position);
		if (lista.get(position).getType().contains("vid")) {
			Log.w("sofiwar2", "es video " + lista.get(position).getImageId()
					+ " " + position);

			type.setImageResource(R.drawable.ic_action_video);
			String thumbnail = "http://img.youtube.com/vi/"
					+ lista.get(position).getImageId() + "/0.jpg";
			Log.w("sofiwar2", thumbnail);
			mostrarImageUrl(photo, thumbnail, lprogress);
			// youtube = new YTPlayer(Main.ac, youTubeThumbnailView,
			// null, lista.get(position).getImageId());
			photo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Log.w("sofiwar2", "es video se ha pulsado el play");
					YTPlayer.OpenYT(
							lista.get(
									Integer.parseInt(arg0
											.getContentDescription().toString()))
									.getImageId(), false, 0);
				}
			});

		} else if (lista.get(position).getType().contains("im")) {
			type.setImageResource(R.drawable.ic_action_picture_1);
			Log.w("sofiwar2", "es imagen " + lista.get(position).getImageId()
					+ " " + position);
			mostrarImageUrl(photo, lista.get(position).getImageId(), lprogress);
			photo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					customDialogImage(
							lista.get(
									Integer.parseInt(arg0
											.getContentDescription().toString()))
									.getImageId(),
							arg0,
							lista.get(
									Integer.parseInt(arg0
											.getContentDescription().toString()))
									.getTitle());
					Log.w("sofiwar2", "es imagen se ha pulsado el play");
				}
			});
		}

		// ////////////////////////////////////////

		text = UI.<TextView> findViewById(layout, R.id.description);

		text.setText(lista.get(position).getDescription());
		text.setContentDescription("" + position);
		text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				customDialog(
						lista.get(
								Integer.parseInt(arg0.getContentDescription()
										.toString())).getTitle(),
						lista.get(
								Integer.parseInt(arg0.getContentDescription()
										.toString())).getDesc()
								+ " "
								+ lista.get(
										Integer.parseInt(arg0
												.getContentDescription()
												.toString()))
										.getBigdescription(), arg0);
			}
		});
		// ///////////////////Listeners
		type.setContentDescription("" + position);
		type.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}

		});
		favorit.setContentDescription("" + position);
		switchFavorit(position, favorit, false);
		favorit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int pos = Integer
						.parseInt(v.getContentDescription().toString());
				ImageView fav = (ImageView) v;
				switchFavorit(pos, fav, true);
			}
		});
		share.setContentDescription("" + position);
		share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.w("SofiWare",
						"se ha pulsado el boton " + v.getContentDescription());
				share(lista.get(
						Integer.parseInt(v.getContentDescription().toString()))
						.getImageId(),
						lista.get(
								Integer.parseInt(v.getContentDescription()
										.toString())).getTitle(),
						lista.get(
								Integer.parseInt(v.getContentDescription()
										.toString())).getType());
			}
		});

		return layout;
	}

	public void removeData(int index) {
		if (lista.size() > 1) {
			lista.remove(index);
		}
	}

	public void mostrarImageUrl(final ImageView imageView, String url,
			final LinearLayout lprogress) {

		mImageDownloader.download(url, imageView, new ProgressListener() {
			@Override
			public void onProgressUpdated(int progressPercent,
					long timeElapsedMilli) {
				Log.w("sofiware", "descargando " + progressPercent);
				if (progressPercent == 100) {
					Log.w("sofiware", "descarga Completa " + progressPercent);
					lprogress.setVisibility(View.GONE);
					imageView.setVisibility(View.VISIBLE);
				}
			}
		});

	}

	// /////////////////////////////PoPups//////////////////////////////
	public void customDialog(String title, String desc, View v) {
		LayoutInflater layoutInflater = (LayoutInflater) Main.ac
				.getBaseContext().getSystemService(
						Main.ac.LAYOUT_INFLATER_SERVICE);
		View popupView = layoutInflater.inflate(R.layout.custom, null);
		ImageView im = (ImageView) popupView.findViewById(R.id.image);
		Display display = Main.ac.getWindowManager().getDefaultDisplay();
		int width = (int) (display.getWidth() * 0.95); // ((display.getWidth()*20)/100)
		int height = (int) (display.getHeight() * 0.75);// ((display.getHeight()*30)/100)
		
		final PopupWindow popupWindow = new PopupWindow(popupView,
				android.view.WindowManager.LayoutParams.WRAP_CONTENT,android.view.WindowManager.LayoutParams.WRAP_CONTENT);

		TextView titleD = (TextView) popupView.findViewById(R.id.texttitle);
		TextView descr = (TextView) popupView
				.findViewById(R.id.textDescription);
		ImageView cancel = (ImageView) popupView.findViewById(R.id.exitImage);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
			}
		});
		titleD.setText(title);
		descr.setText(desc);
		popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

	}

	public void customDialogImage(String url, View v, String title) {
		LayoutInflater layoutInflater = (LayoutInflater) Main.ac
				.getBaseContext().getSystemService(
						Main.ac.LAYOUT_INFLATER_SERVICE);
		View popupView = layoutInflater.inflate(R.layout.custom_image, null);

		final PopupWindow popupWindow = new PopupWindow(popupView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		ImageView im = (ImageView) popupView.findViewById(R.id.image);
		Display display = Main.ac.getWindowManager().getDefaultDisplay();

		int width = (int) (display.getWidth() * 0.95); // ((display.getWidth()*20)/100)
		int height = (int) (display.getHeight() * 0.75);// ((display.getHeight()*30)/100)
		LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width,
				height);
		im.setLayoutParams(parms);
		TextView textv = (TextView) popupView.findViewById(R.id.titleImage);
		textv.setText(title);

		ImageView cancel = (ImageView) popupView.findViewById(R.id.exitImage);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
			}
		});
		LinearLayout lprogr = (LinearLayout) popupView
				.findViewById(R.id.lprogress);
		mostrarImageUrl(im, url, lprogr);
		popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

	}

	// ////////////////// switch favorit/////////////////////////////////
	public void switchFavorit(int pos, ImageView fav, boolean switsh) {
		if (switsh) {
			if (!db.isExisteFavorit(lista.get(pos).getImageId())) {
				fav.setImageResource(R.drawable.ic_action_important);

				db.insertFavorit(lista.get(pos).getImageId(), lista.get(pos)
						.getTitle(), lista.get(pos).getDescription(), lista
						.get(pos).getBigdescription(), lista.get(pos).getType());
			} else {
				fav.setImageResource(R.drawable.ic_action_not_important);
				db.deleteFavorit(lista.get(pos).getImageId());

			}
		} else {
			if (!db.isExisteFavorit(lista.get(pos).getImageId())) {
				fav.setImageResource(R.drawable.ic_action_not_important);

			} else {
				fav.setImageResource(R.drawable.ic_action_important);
			}
		}
	}

	public void share(String share, String title, String type) {
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				Configuration.SHARE);
		if (type.contains("vid")) {
			sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
					"http://www.youtube.com/watch?v=" + share);
		} else
			sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, share);
		sharingIntent.setType("text/plain");
		Main.ac.startActivity(Intent.createChooser(sharingIntent, title));
	}
}
