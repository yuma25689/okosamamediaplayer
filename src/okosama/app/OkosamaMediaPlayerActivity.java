package okosama.app;

import java.util.HashMap;
import java.util.Map.Entry;

import okosama.app.action.HideTabComponentAction;
import okosama.app.action.ShowTabComponentAction;
import okosama.app.action.TabSelectAction;
import okosama.app.adapter.AlbumListAdapter;
import okosama.app.adapter.ArtistAlbumListAdapter;
import okosama.app.adapter.PlaylistListAdapter;
import okosama.app.adapter.TrackListAdapter;
import okosama.app.factory.DroidWidgetKit;
import android.app.Activity;
import android.content.ComponentName;
//import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import okosama.app.service.MediaPlayer;
import okosama.app.service.MediaPlayer.ServiceToken;
import okosama.app.state.DisplayStateFactory;
import okosama.app.state.IDisplayState;
import okosama.app.state.absDisplayState;
import okosama.app.storage.Database;
import okosama.app.tab.*;
import okosama.app.tab.media.TabMediaSelect;
import okosama.app.widget.Button;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;

public class OkosamaMediaPlayerActivity extends Activity
implements ServiceConnection {
	
	// サービスのトークン
    private ServiceToken mToken;
    
    private static boolean externalRef = true;// false;
    public static boolean isExternalRef() {
		return externalRef;
	}
	public static void setExternalRef(boolean externalRef_) {
		boolean bChg = false;
		if( externalRef != externalRef_ )
		{
			bChg = true;
		}
		externalRef = externalRef_;
		if( bChg )
		{
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMedia();
		}
	}

	public static boolean isInternalRef() {
		return internalRef;
	}

	public static void setInternalRef(boolean internalRef_ ) {
		boolean bChg = false;
		if( internalRef != internalRef_ )
		{
			bChg = true;
		}
		internalRef = internalRef_;
		if( bChg )
		{
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMedia();
		}
	}
	private static boolean internalRef = true;
    
    
    // Adapter格納用マップ
    // ObjectにAdapterを格納する
    // うまくいくかどうか分からないが、今のところ、これをonRetainNonConfigurationInstanceで一時保存する対象にする
    // HashMap< String, Object > mapAdapter;
    // AdapterStocker adapters;
    // 暫定版
    private AlbumListAdapter albumAdp;
    // private ListView albumList;
    private ArtistAlbumListAdapter artistAdp;
    // private ExpandableListView artistList;
    private PlaylistListAdapter playlistAdp;
    //private ListView songList;
    private TrackListAdapter tracklistAdp;
    //private ListView playlistList;

	public AlbumListAdapter getAlbumAdp() {
		return albumAdp;
	}

	public void setAlbumAdp(AlbumListAdapter albumAdp) {
		this.albumAdp = albumAdp;
	}

	public ArtistAlbumListAdapter getArtistAdp() {
		return artistAdp;
	}

	public void setArtistAdp(ArtistAlbumListAdapter artistAdp) {
		this.artistAdp = artistAdp;
	}

	public PlaylistListAdapter getPlaylistAdp() {
		return playlistAdp;
	}

	public void setPlaylistAdp(PlaylistListAdapter playlistAdp) {
		this.playlistAdp = playlistAdp;
	}

	public TrackListAdapter getTrackAdp() {
		return tracklistAdp;
	}

	public void setTrackAdp(TrackListAdapter tracklistAdp) {
		this.tracklistAdp = tracklistAdp;
	}

	/**
     * 果たしてmapごと保存してうまくいくのか・・・。TODO 要確認
     */
    @Override
    public Object onRetainNonConfigurationInstance() {
    	// TODO: できたらadapterを返却
        return null;//adapters;
    }
    // TrackAdapter用？
    // editmodeかどうか。
    // preference保存対象
    private boolean editMode = false;
    
	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}
	// タブの初期化が終わったかどうか
	// onCreateとonResumeでの処理のダブりを回避する目的
	// boolean bTabInitEnd = false;
	
	public static int TIMECHAR_WIDTH = 60;
	public static int TIMECHAR_HEIGHT = 80;

	static int currentMainTabId = TabPage.TABPAGE_ID_UNKNOWN;
	static int currentSubTabId = TabPage.TABPAGE_ID_UNKNOWN;
	
	IDisplayState stateMain = null;
//	int mainTabId = TabPage.TABPAGE_ID_UNKNOWN;
	IDisplayState stateSub = null;
//	int subTabId = TabPage.TABPAGE_ID_UNKNOWN;
	public static final String tabNameMain = "maintab";
	public static final String tabNameMedia = "mediatab";
	public static int getCurrentSubTabId()
	{
		return currentSubTabId;
	}
	
	LinearLayout pageContainer = null;
	RelativeLayout componentContainer = null;
	
	private static String dispIdKey = "_displayId";
	
	public static DisplayInfo dispInfo = DisplayInfo.getInstance();
	
	// Actions
//	public static HideTabComponentAction hideWidgetAction = HideTabComponentAction.getInstance();
//	public static ShowTabComponentAction showWidgetAction = ShowTabComponentAction.getInstance();
	
	public static ResourceAccessor res = null;
	public static ResourceAccessor getResourceAccessor()
	{
		return res;
	}
	private Tab tab = null;
	static private Tab tabMedia = null;
	public static Tab createMediaTab(LinearLayout ll, RelativeLayout rl)
	{
		tabMedia = new TabMediaSelect(ll, rl);
		tabMedia.create();
		return tabMedia;
	}
//	public static Tab getMediaTab()
//	{
//		return tabMedia;
//	}
	
	// 初期化時に、スクリーンサイズ取得にスレッドが必要になるため、スレッドとの同期が必要に・・・
	static private Handler handler;
	public static Handler getHandler()
	{
		return handler;
	}
	boolean bInitEnd = false;
	
	private static HashMap<String,Integer> tabCurrentDisplayIdMap = new HashMap<String,Integer>();
	/**
	 * 現在の画面IDを設定する
	 * 今のところ、画面IDというのは、タブIDに等しい
	 * また、この値は、アクティビティにあるが、アプリケーション全体で利用する感じのものである。
	 * とりあえずstaticにしておくが、クラスを移動してもいいかもしれない
	 * @param iDispId
	 */
	public static void setCurrentDisplayId( String name, int iDispId )
	{
		tabCurrentDisplayIdMap.put( name + dispIdKey, iDispId );		
	}
	public static int getCurrentDisplayId( String name )
	{
		if( tabCurrentDisplayIdMap.containsKey( name + dispIdKey ))
		{
			return tabCurrentDisplayIdMap.get( name + dispIdKey );
		}
		else
		{
			return TabPage.TABPAGE_ID_NONE;
		}
	}	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Database.setActivity( this );
        // ボリュームを音楽用に設定する
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
        // Debug出力
//        String log;
//        log = String.valueOf(MediaPlayer.getServiceConnectionCount());
//        Toast.makeText(this, log, Toast.LENGTH_LONG).show();
//       
        
        // リソースの情報を設定する(ここで設定後、二度と設定し直さないのはヤバい気もする
        ResourceAccessor.CreateInstance(this);
        res = ResourceAccessor.getInstance();
        // DroidWidgetKitの設定
        DroidWidgetKit.getInstance().setActivity(this);
        // レイアウトの取得
        pageContainer = (LinearLayout)findViewById(R.id.main_linearlayout);
        componentContainer = (RelativeLayout)findViewById(R.id.main_relativelayout);
        // タブの表示切り替え用の設定
        HideTabComponentAction.getInstance().setTabLayout(componentContainer);
        ShowTabComponentAction.getInstance().setTabLayout(componentContainer);

        // サービスへの接続を開始
        if( 0 == MediaPlayer.getServiceConnectionCount() )
        {
        	mToken = MediaPlayer.bindToService(this, this);
        }      
//        if (savedInstanceState != null) {
//        	// 取得できなければ-1を返却する(=TABPAGE_ID_UNKNOWN)
//        	currentMainTabId = savedInstanceState.getInt(tabNameMain + dispIdKey);
//        	currentSubTabId = savedInstanceState.getInt(tabNameMedia + dispIdKey);
//        }
        // クリアしないと、メディアタブの内容とプレイタブの内容が重なってしまう・・・なぜだろう？
        // setTabSelection( TabPage.TABPAGE_ID_MEDIA, TabPage.TABPAGE_ID_NONE );
        // setTabSelection( TabPage.TABPAGE_ID_NONE, TabPage.TABPAGE_ID_NONE );
        // ここで入れたら駄目
//		SharedPreferences pref = getPreferences(MODE_PRIVATE);
//    	currentMainTabId = pref.getInt(tabNameMain + dispIdKey, TabPage.TABPAGE_ID_UNKNOWN);
//    	currentSubTabId = pref.getInt(tabNameMedia + dispIdKey, TabPage.TABPAGE_ID_UNKNOWN);

        // サイズが取得できたら、下記の処理実行されるようにする
        handler =  new Handler(){
	        //メッセージ受信
	        public void handleMessage(Message message) {   	
	        	switch( message.arg1 )
	        	{
		        	case DisplayInfo.MSG_INIT_END:
		        	{
			        	if( bInitEnd == true )
			        	{
			        	}
			        	else
			        	{
			        		// タブを作成
				            tab = new Tab(
				            	tabNameMain
				            	,pageContainer
				            	,componentContainer 
				            );
				            tab.create();
			        	}
			            // 現在選択中のタブの情報をクリアする
			            // TODO:場所微妙
			           	tabCurrentDisplayIdMap.clear();
			            // 必要であれば、設定を復元する
			            // TODO:これが現在OnCreateのタイミングなのは要検討
			            // OnResumeの方がいいかもしれない
			            // 画面移動&初期化処理
		//	           	if( bTabInitEnd == false )
		//	           	{
			    		SharedPreferences pref = getPreferences(MODE_PRIVATE);
			           	setTabSelection( pref.getInt(tabNameMain + dispIdKey, TabPage.TABPAGE_ID_UNKNOWN), 
			           			pref.getInt(tabNameMedia + dispIdKey, TabPage.TABPAGE_ID_UNKNOWN) );
			           	TabSelectAction selAct = new TabSelectAction(tab, currentMainTabId);
			           	selAct.doAction(null);
			           	TabSelectAction selActMedia = new TabSelectAction( tab.getTabPageMedia().getTabContent(), currentSubTabId );
			           	selActMedia.doAction(null);
			           	
	//	           		if( 1 == setTabSelection( currentMainTabId, currentSubTabId ) )
	//	           		{
		    	           	// updateListeners();	           			
	//	           		}
		//	           	}
			    		bInitEnd = true;
			            //bTabInitEnd = true;
			    		break;
		        	}
		        	case TabSelectAction.MSG_ID_TAB_SELECT:
		        		// タブが選択された通知
		        		// Activityのタブidを更新
		        		setTabSelection(
		        				OkosamaMediaPlayerActivity.getCurrentDisplayId(OkosamaMediaPlayerActivity.tabNameMain)		        		
		        				,OkosamaMediaPlayerActivity.getCurrentDisplayId(OkosamaMediaPlayerActivity.tabNameMedia)
		        		);
		        		// リスナを更新
		            	updateListeners();
		            	// メディアを更新
		            	reScanMedia();
		            	// 共通部分再描画
		            	updateCommonCtrls();
		        		break;
	        	}
	        };
        };
    }
        
    /**
     * ベース画像上での絶対座標を指定した位置を表すLayoutParamを作成する
     * @param left
     * @param top
     * @param width
     * @param height
     * @return LayoutParam
     */
	public static RelativeLayout.LayoutParams 
	createLayoutParamForAbsolutePosOnBk(
			int left, int top, int width, int height )
	{
		int widthCorrect = dispInfo.getCorrectionXConsiderDensity(width);
		int heightCorrect = dispInfo.getCorrectionYConsiderDensity(height);
		int xCorrect = dispInfo.getCorrectionXConsiderDensity(left);
		int yCorrect = dispInfo.getCorrectionYConsiderDensity(top);
		
		RelativeLayout.LayoutParams lp = 
				new RelativeLayout.LayoutParams(
						widthCorrect, heightCorrect);
		
        lp.topMargin = yCorrect;
        lp.leftMargin = xCorrect;
        // このアプリケーションでは、bottomとrightのmarginはゼロだが・・・。
        lp.bottomMargin = 0;
        lp.rightMargin = 0;
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        
        return lp;
	}
	RelativeLayout.LayoutParams createLayoutParamForAbsolutePos(
			int width, int height, int left, int top )
	{
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
        		(int)(width), (int)(height));
        lp.topMargin = (int)( top );
        lp.leftMargin = (int)( left );
        lp.bottomMargin = 0;
        lp.rightMargin = 0;
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        
        return lp;
	}
	@Override
    public void onSaveInstanceState(Bundle outcicle) {
		// TODO:マップをループして、全部の設定を保存
		//tabCurrentDisplayIdMap.
        //outcicle.putInt("displayid", iCurrentDisplayId);
//		for(Entry<String, Integer> e : tabCurrentDisplayIdMap.entrySet()) {
//		    outcicle.putInt( e.getKey(), e.getValue() );
//		}
        super.onSaveInstanceState(outcicle);
    }

	@Override
	protected void onPause() {
		// マップをループして、全部の設定を保存
		//tabCurrentDisplayIdMap.
        //outcicle.putInt("displayid", iCurrentDisplayId);
		Editor editor = getPreferences(MODE_PRIVATE).edit();
		for(Entry<String, Integer> e : tabCurrentDisplayIdMap.entrySet()) {
			editor.putInt( e.getKey(), e.getValue() );
		}
		editor.commit();
		
		// TODO Auto-generated method stub
		// setTabSelection( TabPage.TABPAGE_ID_NONE, TabPage.TABPAGE_ID_NONE );
		// 本当は、上記でそうなって欲しいのだが、現状そうなっていないかもしれない
		// 全てのビューをクリアしておく
		if( pageContainer.getBackground() != null )
		{
			pageContainer.getBackground().setCallback(null);
			pageContainer.setBackgroundDrawable(null);
		}
		componentContainer.removeAllViews();
		bInitEnd = false;
		// bTabInitEnd = false;
		System.gc();
		super.onPause();
	}

	@Override
	protected void onResume() {
//		if( bInitEnd == true 
//		&& bTabInitEnd == false )
//		{
			// TODO Auto-generated method stub
    	// 画面のサイズ等の情報を更新する
        dispInfo.init(this, componentContainer, handler);
		
		//setTabSelection( currentMainTabId, currentSubTabId );
//		}
		super.onResume();
	}

	/**
	 * サブタブは、状況によって変化するので注意
	 * まだこの関数は未完成（その、サブタブの選択部分)
	 * @param mainTab
	 * @param subTab
	 * @return 0:変化なし 1:変化有り -1:エラー
	 */
	int setTabSelection( int mainTab, int subTab )//, boolean bForceUpd )
	{
		IDisplayState stateMainTmp = DisplayStateFactory.createDisplayState(mainTab);
		if( stateMainTmp == null )
		{
			return -1;
		}
		IDisplayState stateSubTmp = DisplayStateFactory.createDisplayState(subTab);        		
        if( stateSubTmp == null )
        {
        	return -1;
        }
		
		int iRet = 0;
		//int iRet2 = 0;
		if( currentMainTabId != mainTab )//|| bForceUpd == true )
        {
    	   currentMainTabId = mainTab;
    	   stateMain = stateMainTmp;
    	   iRet = 1;
        }
        if( stateMain != null )
        {
        	// 画面IDから状態が取得できた
        	if( iRet == 1 )
        	{
        		//iRet2 = 
        		stateMain.ChangeDisplayBasedOnThisState(tab);
        	}
        }
        if( stateSubTmp != null )
        {
        	if( currentMainTabId == TabPage.TABPAGE_ID_MEDIA )
        	{
	        	if( currentSubTabId != subTab )
	        	{   	   
	        		currentSubTabId = subTab;
	        		stateSub = stateSubTmp;
	        		iRet = 1;
	        		// メディアタブであれば
	        		// サブ画面をロードする
	        		// 二段階に分けると二度画面更新が走るので無駄が多いと思われるが、とりあえずそれしか思いつかない
	                stateSub = DisplayStateFactory.createDisplayState(subTab);        		
	                if( stateSub != null && tabMedia != null)
	                {
	                	stateSub.ChangeDisplayBasedOnThisState(tabMedia);
	                }
	        	}
        	}
        	else
        	{
        		// MediaTabでない場合、サブTabは擬似的にNoneで更新させる
        		DisplayStateFactory.createDisplayState(TabPage.TABPAGE_ID_NONE).ChangeDisplayBasedOnThisState(tabMedia);  
        	}
        }
        if( iRet == 1 )
        {
        	Message msg = new Message();
        	msg.arg1 = TabSelectAction.MSG_ID_TAB_SELECT;
        	handler.sendMessage(msg);
        }
        return iRet;
	}
	void updateCommonCtrls()
	{
		// 共通で利用するボタンを最前面に持っていく
		if( getResourceAccessor().commonBtns != null )
		{
			for( Button btn : getResourceAccessor().commonBtns )
			{
				if( btn.getView() != null )
				{
					btn.getView().bringToFront();
				}
			}
		}
		
		// TODO:時間表時の再描画？ここは、結局別関数にする必要があると思われるが・・・
	}
	
	/**
	 * 現在の状況に合わせて、リスナを登録し直す
	 */
	void updateListeners()
	{
		if( stateMain == null 
				|| stateSub == null )
		{
			return;
		}
		int iRet = stateMain.registerReceivers(absDisplayState.STATUS_ON_CREATE);
		if( iRet == 1 )
		{
			iRet = stateSub.registerReceivers(absDisplayState.STATUS_ON_CREATE);
		}		
		if( iRet == 1 )
		{
			// Log.w("registerReceivers=1","maybe listener not registered.");
		}
		else if( iRet < 0 )
		{
			// Log.e("registerReceivers<0","Failed to register the listeners.");
		}		
	}
	/**
	 * adapterの準備が終わったときに、adapterからコールバックする感じの関数
	 */
	public void initAdapter(int id,Cursor cursor)
	{
		initAdapter(id,cursor,false);
	}
	public void initAdapter(int id,Cursor cursor, boolean isLimited)
	{
		if( cursor == null )
		{
			Log.w("Warning","cursor is null");
			// nullのままでcursor設定されても特に問題はない
			// return;
		}
		switch( id )
		{
		// TODO:nullの場合、表示するビューを変更した方がいいかもしれない
		case TabPage.TABPAGE_ID_ALBUM:
			// Listにカーソルを設定
			getAlbumAdp().changeCursor(cursor);
			break;
		case TabPage.TABPAGE_ID_ARTIST:
			// Listにカーソルを設定
			getArtistAdp().changeCursor(cursor);
			break;
		case TabPage.TABPAGE_ID_SONG:
			// Listにカーソルを設定
			getTrackAdp().changeCursor(cursor);
			break;
		case TabPage.TABPAGE_ID_PLAYLIST:
			Cursor c = null;
			c = Database.getInstance(isExternalRef()).mergedCursor(cursor, false);//createShortCut);
			// Listにカーソルを設定
			getPlaylistAdp().changeCursor(c);
			break;
		}
	}
	
	public void reScanMedia()
	{
		// 現在選択中のタブによって操作を変更
		if( currentMainTabId == TabPage.TABPAGE_ID_MEDIA )
		{
			// メディアタブならば
			// メディアを再度クエリ発行して更新する
			switch( currentSubTabId )
			{
			case TabPage.TABPAGE_ID_ALBUM:
				// Listにカーソルを設定
				Database.getInstance(externalRef).createAlbumCursor(getAlbumAdp().getQueryHandler(), null );//, null);
				break;
			case TabPage.TABPAGE_ID_ARTIST:
				// Listにカーソルを設定
				Database.getInstance(externalRef).createArtistCursor(getArtistAdp().getQueryHandler(), null);			
				break;
			case TabPage.TABPAGE_ID_SONG:
				OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.setPlaylistName( null );        	
				Database.getInstance(externalRef).createTrackCursor(getTrackAdp().getQueryHandler(), null, true );//, null, null, null);			
				break;
			case TabPage.TABPAGE_ID_PLAYLIST:
				Database.getInstance(externalRef).createPlaylistCursor(getPlaylistAdp().getQueryHandler(), null, false);						
				break;
			}
		}
		else if( currentMainTabId == TabPage.TABPAGE_ID_NOW_PLAYLIST )
		{
			// TODO: 現在、トラックと同じカーソルになっているが、考えた方がいいかもしれない
			// NOWPLAYLIST
			OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.setPlaylistName( Database.PlaylistName_NowPlaying );
			
			Database.getInstance(externalRef).createTrackCursor(getTrackAdp().getQueryHandler(), null, true );//, null, null, null);
		}
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		// MediaPlayer.unbindFromService(mToken);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// サービスの登録解除
        MediaPlayer.unbindFromService(mToken);
		super.onDestroy();
	}

	// とりあえず、必要ではない？
//	@Override
//	protected void onStart() {
//		// TODO Auto-generated method stub
//		super.onStart();
//	}
	
	///////////////////////// サービス用のメソッド //////////////////////////////
	/**
	 * サービス接続時
	 */
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		// TODO Auto-generated method stub
		
		if( stateMain == null )
		{
			Log.i("tabstate=null","tab state is null on the service connected.");
			return;
		}
		updateListeners();
	}

	/**
	 * サービス切断時
	 */
	@Override
	public void onServiceDisconnected(ComponentName name) {
       // Toast.makeText(this, "onServiceDisconnected:" + name, Toast.LENGTH_LONG).show();
		
		// よくわからないけど、サービス切断されたら終了する？
		finish();
	}

	
	////////////////// event ////////////////////////////
	View lastEventView = null;
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfoIn) {
		// 一応、今のところタグにリストが入っているはずなので、それで分岐する？
		lastEventView = view;
		ITabComponent lstCompo = (ITabComponent)lastEventView.getTag();//TabLeaf.TAGKEY_LISTNAME);
		if( lstCompo != null
		&& lstCompo.getBehavior() != null )
		{
			lstCompo.getBehavior().onCreateContextMenu(menu,view,menuInfoIn);
		}
	}

	    @Override
	    public boolean onContextItemSelected(MenuItem item) {
	    	if( lastEventView == null )
	    	{
	    		return true;
	    	}
			// 一応、今のところタグにリストが入っているはずなので、それで分岐する？
			ITabComponent lstCompo = (ITabComponent)lastEventView.getTag();//TabLeaf.TAGKEY_LISTNAME);
			if( lstCompo.getBehavior() != null )
			{
				lstCompo.getBehavior().onContextItemSelected(item);
			}
	    	

	        return super.onContextItemSelected(item);
	    }

	    void doSearch() {
//	        CharSequence title = null;
//	        String query = "";
//	        
//	        Intent i = new Intent();
//	        i.setAction(MediaStore.INTENT_ACTION_MEDIA_SEARCH);
//	        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//	        
//	        title = "";
//	        if (!mIsUnknownAlbum) {
//	            query = mCurrentAlbumName;
//	            i.putExtra(MediaStore.EXTRA_MEDIA_ALBUM, mCurrentAlbumName);
//	            title = mCurrentAlbumName;
//	        }
//	        if(!mIsUnknownArtist) {
//	            query = query + " " + mCurrentArtistNameForAlbum;
//	            i.putExtra(MediaStore.EXTRA_MEDIA_ARTIST, mCurrentArtistNameForAlbum);
//	            title = title + " " + mCurrentArtistNameForAlbum;
//	        }
//	        // Since we hide the 'search' menu item when both album and artist are
//	        // unknown, the query and title strings will have at least one of those.
//	        i.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Albums.ENTRY_CONTENT_TYPE);
//	        title = getString(R.string.mediasearch, title);
//	        i.putExtra(SearchManager.QUERY, query);
//
//	        startActivity(Intent.createChooser(i, title));
	    }

//	    @Override
//	    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//	        switch (requestCode) {
//	            case SCAN_DONE:
//	                if (resultCode == RESULT_CANCELED) {
//	                    finish();
//	                } else {
//	                    getAlbumCursor(mAdapter.getQueryHandler(), null);
//	                }
//	                break;
//
//	            case NEW_PLAYLIST:
//	                if (resultCode == RESULT_OK) {
//	                    Uri uri = intent.getData();
//	                    if (uri != null) {
//	                        long [] list = MusicUtils.getSongListForAlbum(this, Long.parseLong(mCurrentAlbumId));
//	                        MusicUtils.addToPlaylist(this, list, Long.parseLong(uri.getLastPathSegment()));
//	                    }
//	                }
//	                break;
//	        }
//	    }



	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
//	        super.onCreateOptionsMenu(menu);
//	        menu.add(0, PARTY_SHUFFLE, 0, R.string.party_shuffle); // icon will be set in onPrepareOptionsMenu()
//	        menu.add(0, SHUFFLE_ALL, 0, R.string.shuffle_all).setIcon(R.drawable.ic_menu_shuffle);
	        return true;
	    }

	    @Override
	    public boolean onPrepareOptionsMenu(Menu menu) {
//	        MusicUtils.setPartyShuffleMenuIcon(menu);
	        return super.onPrepareOptionsMenu(menu);
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
//	        Cursor cursor;
//	        switch (item.getItemId()) {
//	            case PARTY_SHUFFLE:
//	                MusicUtils.togglePartyShuffle();
//	                break;
//
//	            case SHUFFLE_ALL:
//	                cursor = MusicUtils.query(this, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//	                        new String [] { MediaStore.Audio.Media._ID},
//	                        MediaStore.Audio.Media.IS_MUSIC + "=1", null,
//	                        MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
//	                if (cursor != null) {
//	                    MusicUtils.shuffleAll(this, cursor);
//	                    cursor.close();
//	                }
//	                return true;
//	        }
	        return super.onOptionsItemSelected(item);
	    }
	    

}