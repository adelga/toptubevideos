package com.coolapps.toptube.main;


import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.coolapps.toptube.R;
import com.coolapps.toptube.script.network.AbstractImageDownloader.ProgressListener;
import com.coolapps.toptube.script.network.UrlImageDownloader;
import com.coolapps.toptube.utils.Category;
import com.coolapps.toptube.utils.Content;
import com.coolapps.toptube.utils.DataBase;
import com.coolapps.toptube.utils.Model;
import com.coolapps.toptube.utils.RefreshContentsXML;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

@SuppressLint("NewApi")
public class Main extends Activity{
	public static Activity ac;
	AdView  adView = null;

	private static final int REQ_START_STANDALONE_PLAYER = 1;
	private String[] titulos;
	private DrawerLayout NavDrawerLayout;
	private ListView NavList;
	private ArrayList<Item_objct> NavItms;
	private TypedArray NavIcons;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	NavigationAdapter NavAdapter;
	View header;
	DataBase db;
	EditText txtSearch ;
	private Category news = new Category();
	int posicionCurrent = 1;
	Context cn;
	private UrlImageDownloader mImageDownloader;
	private Model model;
	private boolean buscando;
	LinearLayout layout;
	
	//// listeners
	private static OnEventSearchListener mOnEventListener;
	private static OnBadgeUpdateListener mOnBadgeUpdateListener;

	public static void addOnSearchListener(OnEventSearchListener listener) {
		mOnEventListener = listener;
	}
    
	public static void addOnBadgeUpdateListener(OnBadgeUpdateListener listener) {
		mOnBadgeUpdateListener = listener;
	}
	
	public interface OnEventSearchListener {
		void onSearch(String text);
		// or void onEvent(); as per your need
	};
	
	public interface OnBadgeUpdateListener {
		void OnBadgeUpdate();
		// or void onEvent(); as per your need
	};

	
	
	/////// ciclo de vida de la activity principal
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		db = new DataBase(this);
		// View v= findViewById(R.id.drawer_layout);
		mImageDownloader = new UrlImageDownloader(this);
		cn = this;
		model = (Model) getIntent().getExtras().get("Model");
		posicionCurrent = (Integer) getIntent().getExtras().getInt("PosCurrent", 1);
		// /// filtrar los nuevos
		List<Content> listnewsContents = new ArrayList<Content>();
		for (int i = 0; i < model.bloq.size(); i++) {
			for (int j = 0; j < model.bloq.get(i).contents.size(); j++) {
				if (model.bloq.get(i).contents.get(j).news) {
					Content con = new Content();
					con = model.bloq.get(i).contents.get(j);
					listnewsContents.add(con);
					if(!db.isExistNews(con.url)){
						Log.e("test4","Guardando en la BD");
						db.insertnews(con.url);	
					}else{
						Log.e("test4","Existe en la BD");
					}
				}
			}
		}
		news.contents.addAll(listnewsContents);
		ac = this;
		Object restore = getLastNonConfigurationInstance();

		if (restore != null) {
			posicionCurrent = (Integer) restore;
		} else {
			//posicionCurrent = 1;
		}
		Log.w("sofiwar ", "pos onCreate " + posicionCurrent);
		navegationDrawer();
		mostrarModel();
		/////////////////////////////
	//	aDmoB(this, null,  adView, boolean flip,LinearLayout fliping)
		
		adView = new AdView(this);
		adView.setAdSize(AdSize.SMART_BANNER);
		adView.setAdUnitId(com.coolapps.toptube.utils.Configuration.ID_ADS_BANNER);

		// Add the AdView to the view hierarchy. The view will have no size
		// until the ad is loaded.
		
         Display display = Main.ac.getWindowManager().getDefaultDisplay();
		
		int width = (int) (display.getWidth()); // ((display.getWidth()*20)/100)
		int height = (int) (display.getHeight());// ((display.getHeight()*30)/100)
		LinearLayout.LayoutParams parms = null;
		Log.e("admob","LayoutParams "+height*0.1);
			if((height*0.1)>=100){
		parms = new LinearLayout.LayoutParams(width, (int) (height*0.1));
			}else {
				parms = new LinearLayout.LayoutParams(width,100);		
			}
		
	    layout = (LinearLayout) findViewById(R.id.bannermain);
	    layout.setLayoutParams(parms);
		layout.setVisibility(View.GONE);

		layout.addView(adView);
		   Bundle bundle = new Bundle();
		    bundle.putString("color_bg", com.coolapps.toptube.utils.Configuration.color_bg_ADS);
		    bundle.putString("color_bg_top", com.coolapps.toptube.utils.Configuration.color_bg_top_ADS);
		    bundle.putString("color_border", com.coolapps.toptube.utils.Configuration.color_border_ADS);
		    bundle.putString("color_link", com.coolapps.toptube.utils.Configuration.color_link_ADS);
		    bundle.putString("color_text", com.coolapps.toptube.utils.Configuration.color_text_ADS);
		    bundle.putString("color_url", com.coolapps.toptube.utils.Configuration.color_url_ADS);
	      
		    AdMobExtras extras = new AdMobExtras(bundle);
		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				.addTestDevice("45CEFC883F30B8758E2C39EFC8C549C6").addNetworkExtras(extras).build();

		// Start loading the ad in the background.
		adView.loadAd(adRequest);
		adView.setAdListener( new AdListener() {

			@Override
			public void onAdClosed() {
				// TODO Auto-generated method stub
				Log.e("admob","onAdClosed");
				super.onAdClosed();
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {
				// TODO Auto-generated method stub
				Log.e("admob","onAdFailedToLoad");
				layout.setVisibility(View.GONE);
				super.onAdFailedToLoad(errorCode);
			}

			@Override
			public void onAdLeftApplication() {
				// TODO Auto-generated method stub
				Log.e("admob","onAdLeftApplication");
				super.onAdLeftApplication();
			}

			@Override
			public void onAdLoaded() {
				// TODO Auto-generated method stub
				Log.e("admob","onAdLoaded");
				layout.setVisibility(View.VISIBLE);
				super.onAdLoaded();
			}

			@Override
			public void onAdOpened() {
				// TODO Auto-generated method stub
				Log.e("admob","onAdOpened");
				super.onAdOpened();
			}
			
		});
	}

	// construir los layouts y el navigatio drawer
	public void navegationDrawer() {
		// Drawer Layout

		NavDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		// Lista
		NavList = (ListView) findViewById(R.id.lista);
		NavList.setBackgroundColor(Color.parseColor(com.coolapps.toptube.utils.Configuration.BACKGROUNG_COLOR_CATEGORY));
		// Declaramos el header el cual sera el layout de header.xml
		header = getLayoutInflater().inflate(R.layout.header, null);
		header.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.w("sofiware",
						" se ha pulsado esto "
								+ Integer.parseInt(arg0.getContentDescription()
										.toString()));
				MostrarFragment(Integer.parseInt(arg0.getContentDescription()
						.toString()));
			}
		});
		// Establecemos header
		NavList.addHeaderView(header);
		// Tomamos listado de imgs desde drawable
		//NavIcons = getResources().obtainTypedArray(R.array.navigation_iconos);
		// NavIcons = new Ty
		// Tomamos listado de titulos desde el string-array de los recursos
		// @string/nav_options
		// titulos = getResources().getStringArray(R.array.nav_options);
		// Listado de titulos de barra de navegacion
		NavItms = new ArrayList<Item_objct>();
		// Agregamos objetos Item_objct al array
		// Perfil
		NavItms.add(new Item_objct("home", R.drawable.ic_action_home,
				"http://www.homestagingburgos.com/imagenes/home.jpg",news.contents.size()));
		Log.e("test11","el tamaño"+news.contents.size());
		for (int i = 0; i < model.bloq.size(); i++) {
			int icon = 0;
			if (model.bloq.get(i).type.contains("favorit")) {
				icon = R.drawable.ic_action_favorite;
			} else if (model.bloq.get(i).type.contains("video")) {
				icon = R.drawable.ic_action_video_1;
			} else if (model.bloq.get(i).type.contains("image")) {
				icon = R.drawable.ic_action_picture;
			} else if (model.bloq.get(i).type.contains("contact")) {
				icon = R.drawable.ic_action_email;
			}
			Log.e("test11","el tamaño"+model.bloq.get(i).contents.size());
			NavItms.add(new Item_objct(model.bloq.get(i).tag, icon, model.bloq
					.get(i).icon,model.bloq.get(i).contents.size()));
		}

		// Declaramos y seteamos nuestro adaptador al cual le pasamos el array
		// con los titulos
		NavAdapter = new NavigationAdapter(this, NavItms);
		NavList.setAdapter(NavAdapter);
		// Siempre vamos a mostrar el mismo titulo
		mTitle = mDrawerTitle = getTitle();

		// Declaramos el mDrawerToggle y las imgs a utilizar
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		NavDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* Icono de navegacion */
		R.string.app_name, /* "open drawer" description */
		R.string.hello_world /* "close drawer" description */
		) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				Log.e("Cerrado completo", "!!");
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
				try{
					mOnBadgeUpdateListener.OnBadgeUpdate();
				}catch(Exception e){
					
				}
				
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				Log.e("Apertura completa", "!!");
				invalidateOptionsMenu(); // creates call to
				try{
					mOnBadgeUpdateListener.OnBadgeUpdate();
				}catch(Exception e){
					
				}							// onPrepareOptionsMenu()
			}
		};

		// Establecemos que mDrawerToggle declarado anteriormente sea el
		// DrawerListener
		NavDrawerLayout.setDrawerListener(mDrawerToggle);
		// Establecemos que el ActionBar muestre el Boton Home
		try{
	    getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.backgraund_actionbar));
       }catch(Exception e){
			e.printStackTrace();
		}
		// Establecemos la accion al clickear sobre cualquier item del menu.
		// De la misma forma que hariamos en una app comun con un listview.
		NavList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				posicionCurrent = position;
				Log.w("sofiwar ", "pos onItemSelec " + posicionCurrent);
				MostrarFragment(position);
				
			}
		});
		
		// Cuando la aplicacion cargue por defecto mostrar la opcion Home
		Log.w("sofiwar ", "pos MostrarFragments " + posicionCurrent);
		MostrarFragment(posicionCurrent);
	}

	/*
	 * Pasando la posicion de la opcion en el menu nos mostrara el Fragment
	 * correspondiente
	 */
	private void MostrarFragment(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;

		switch (position) {
		case 1:
			// fragment = new HomeFragment();
			fragment = newInstanceHome(news);
			break;
		default:
			// si no esta la opcion mostrara un toast y nos mandara a Home
			// Toast.makeText(
			// getApplicationContext(),
			// "Opcion " + model.bloq.get(position - 1).tag
			// + "no disponible!", Toast.LENGTH_SHORT).show();
			// fragment = new HomeFragment();
			// position = 1;
			if (model.bloq.get(position - 2).type.contains("favorit")) {
				fragment = new FavoritFragment();
			} else if (model.bloq.get(position - 2).type.contains("video")
					|| model.bloq.get(position - 2).type.contains("image")) {
				// fragment = new
				// MultimediaFragment(model.bloq.get(position-2));
				fragment = newInstanceMulti(model.bloq.get(position - 2));
			} else if (model.bloq.get(position - 2).type.contains("contact")) {
				fragment = new ProfileFragment();
			}

			break;
		}
		// Validamos si el fragment no es nulo
		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment).commit();

			NavList.setItemChecked(position, true);
			NavList.setSelection(position);
			
			// Cambiamos el titulo en donde decia "
			setTitle(NavItms.get(position - 1).getTitulo());
			ImageView icono = (ImageView) header.findViewById(R.id.backgrd);
			header.setContentDescription("" + position);
			mostrarImageUrl(icono, NavItms.get(position - 1).getImage(), null);
			// Cerramos el menu deslizable
			NavDrawerLayout.closeDrawer(NavList);
		} else {
			// Si el fragment es nulo mostramos un mensaje de error.
			Log.e("Error  ", "MostrarFragment" + position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		buscando=false;
		getMenuInflater().inflate(R.menu.main, menu);
		/** Get the action view of the menu item whose id is search */
		View v = (View) menu.findItem(R.id.action_websearch).getActionView();
        Log.e("test5","onCreateOptionsMenu ");
		/** Get the edit text from the action view */
	    txtSearch = (EditText) v.findViewById(R.id.txt_search);
		txtSearch.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stu
				Log.d("test5","se ha cambiado el texto "+s.toString()+" "+buscando);
				if(buscando){
					try{
					mOnEventListener.onSearch(s.toString());
					}catch(Exception e){
						
					}
					}
			}
		});	
	 
//		View vRefresh = (View) menu.findItem(R.id.action_refresh);
//		vRefresh.setOnLongClickListener(new OnLongClickListener() {
//			
//			@Override
//			public boolean onLongClick(View arg0) {
//				// TODO Auto-generated method stub
//				Log.e("test","se ha pulsado el onLongClick");
//				return false;
//			}
//		});
		
//		txtSearch.setOnEditorActionListener(new OnEditorActionListener() {
//
//			@Override
//			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
//			
//			String text="";
//				try{
//				
//			    text=arg0.getText().toString();
//				}catch(Exception e){
//				e.printStackTrace();	
//				}
//				mOnEventListener.onSearch(text);
//				return false;
//			}
//		});

		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = NavDrawerLayout.isDrawerOpen(NavList);
		menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		menu.findItem(R.id.action_overflow).setVisible(!drawerOpen);
		menu.findItem(R.id.action_refresh).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	
	
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		Log.w("test","onMenuItemSelected "+item.getItemId()+" "+featureId);
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)){
			Log.e("mDrawerToggle pushed", "x");
		
			return true;
		}

		// Handle action buttons
		
		switch (item.getItemId()){
		case R.id.action_websearch:
			// create intent to perform web search for this planet
			 Log.e("test5","pulsado el search "+buscando);
             buscando=true;
			/** Setting an action listener */

			return true;
		case R.id.action_refresh:
			// create intent to perform web search for this planet
			Log.w("test","onOptionsItemSelected  action_refresh");
			refresh();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}



	public static final MultimediaFragment newInstanceMulti(Category catg){
		MultimediaFragment fragment = new MultimediaFragment();
		Bundle bundle = new Bundle(2);
		bundle.putSerializable("category", catg);
		bundle.putString("test", "testtest");
		fragment.setArguments(bundle);
		fragment.setRetainInstance(true);
		return fragment;
	}

	public static final HomeFragment newInstanceHome(Category catg) {
		HomeFragment fragment = new HomeFragment();
		Bundle bundle = new Bundle(2);
		bundle.putSerializable("news", catg);
		bundle.putString("test", "testtest");
		fragment.setArguments(bundle);
		fragment.setRetainInstance(true);
		return fragment;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (adView != null) {
		      adView.destroy();
		    }
		 		super.onDestroy();
	}

	@Override
	public Object onRetainNonConfigurationInstance(){
		int var = posicionCurrent;
		Log.w("sofiwar ", "pos onRetainNonConfigurationInstance "
				+ posicionCurrent);
		return var;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.w("sofiwar ", "pos onActivityResult ");
		if (requestCode == REQ_START_STANDALONE_PLAYER
				&& resultCode != RESULT_OK) {
			YouTubeInitializationResult errorReason = YouTubeStandalonePlayer
					.getReturnedInitializationResult(data);
			if (errorReason.isUserRecoverableError()) {
				errorReason.getErrorDialog(this, 0).show();
			}else{
				// String errorMessage =
				// String.format(getString(R.string.error_player),
				// errorReason.toString());
				Toast.makeText(this, "hay algun tipo de error",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public void mostrarImageUrl(final ImageView imageView, String url,
			final LinearLayout lprogress){
		mImageDownloader.download(url, imageView, new ProgressListener() {
			@Override
			public void onProgressUpdated(int progressPercent,
					long timeElapsedMilli) {
				Log.w("sofiware", "descargando " + progressPercent);
				if (progressPercent == 100) {
					Log.w("sofiware", "descarga Completa " + progressPercent);

					imageView.setVisibility(View.VISIBLE);
				}
			}
		});

	}
	
	public void mostrarModel(){
		for (int i = 0; i < model.bloq.size(); i++) {
			for (int j = 0; j < model.bloq.get(i).contents.size(); j++) {

				Log.w("test", " categroy: " + i + " ;"
						+ model.bloq.get(i).contents.get(j).favorit
						+ " email: " + model.bloq.get(i).contents.get(j).email
						+ " " + model.bloq.get(i).contents.get(j).index + " "
						+ model.bloq.get(i).contents.get(j).smalltext + " "
						+ model.bloq.get(i).contents.get(j).bigtext + " "
						+ model.bloq.get(i).contents.get(j).title + " "
						+ model.bloq.get(i).tag + " " + model.bloq.get(i).icon);
				Log.w("Mostrando lista",
						"-----------------------------------------------------------------------");
			}
		}
	}
	
	
	
	
	///////////////////////////////////////// refresh/////////////////////////////
	RefreshContentsXML refresh = new RefreshContentsXML();
	
	public void refresh(){
	  if(com.coolapps.toptube.utils.Configuration.XMLREADER)
		refresh.refreshContentsXML(this, null, null,mHandler);	
	  else refresh.RefrechContentsYoutube(this, null,mHandler);	
	}
	
	public Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			    Log.w("test", "Enviando la activity");
				Intent i= new Intent(Main.this, Main.class);
				i.putExtra("Model", refresh.model);
				if(refresh.model.bloq.size()>0){
				i.putExtra("PosCurrent", posicionCurrent);
				}else i.putExtra("PosCurrent", 1);
				startActivity(i);
				finish();
			
		}	
		};

		 @Override
		  public void onResume() {
		    super.onResume();
		    if (adView != null) {
		      adView.resume();
		    }
		  }

		  @Override
		  public void onPause() {
		    if (adView != null) {
		      adView.pause();
		    }
		    super.onPause();
		  }

		@Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			super.onBackPressed();
		}

	
	
}
