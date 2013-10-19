package okosama.app;

import java.util.HashMap;
import java.util.Map.Entry;

//import okosama.app.action.HideTabComponentAction;
//import okosama.app.action.ShowTabComponentAction;
import okosama.app.action.TabSelectAction;
import okosama.app.adapter.AlbumListAdapter;
import okosama.app.adapter.AlbumListRawAdapter;
import okosama.app.adapter.ArtistAlbumListAdapter;
import okosama.app.adapter.PlaylistListAdapter;
import okosama.app.adapter.TrackListAdapter;
import okosama.app.adapter.TrackListRawAdapter;
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
import android.os.RemoteException;
import okosama.app.panel.PlayControlPanel;
import okosama.app.panel.SubControlPanel;
import okosama.app.panel.TimeControlPanel;
//import okosama.app.service.MediaPlaybackService;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.service.MediaPlayerUtil.ServiceToken;
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
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import okosama.app.widget.ButtonImpl;

public class OkosamaMediaPlayerActivity extends Activity
implements ServiceConnection {

	// 強制リフレッシュフラグ？
	public boolean bForceRefresh = false;
	// リフレッシュ用メッセージID
    public static final int REFRESH = 1001;
    // ポーズ中？
    private boolean paused;
//	// 時間パネル
//	static TimeControlPanel timeCP = null;
//	private void createTimeCP()
//	{
//		if( timeCP == null )
//		{
//			timeCP = new TimeControlPanel(this);
//		}
//	}
//	public TimeControlPanel getTimeCP()
//	{
//		return timeCP;
//	}
//	// Playパネル
//	static PlayControlPanel playCP = null;
//	public PlayControlPanel getPlayCP()
//	{
//		if( playCP == null )
//		{
//			playCP = new PlayControlPanel(this);
//		}
//		return playCP;
//	}	
//	// Playパネル
//	static SubControlPanel subCP = null;
//	public SubControlPanel getSubCP()
//	{
//		if( subCP == null )
//		{
//			subCP = new SubControlPanel(this);
//		}
//		return subCP;
//	}
	public void updatePlayStateButtonImage()
	{
		if( PlayControlPanel.getInstance() != null ) 
			PlayControlPanel.getInstance().setPlayPauseButtonImage();
		
		if( SubControlPanel.getInstance() != null )
		{
			SubControlPanel.getInstance().setShuffleButtonImage();
			SubControlPanel.getInstance().setRepeatButtonImage();
		}
	}
	// サービスのトークン
    private ServiceToken mToken;
    
    // 楽曲の検索に、Externalを利用
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
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMedia(ControlIDs.ID_NOT_SPECIFIED, true);
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
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMedia(ControlIDs.ID_NOT_SPECIFIED, true);
		}
	}
	private static boolean internalRef = true;
    
    
    // Adapter格納用マップ
    // ObjectにAdapterを格納する
    // うまくいくかどうか分からないが、今のところ、これをonRetainNonConfigurationInstanceで一時保存する対象にする
    // HashMap< String, Object > mapAdapter;
    // AdapterStocker adapters;
    // 暫定版
    // private AlbumListAdapter albumAdp;
	private AlbumListRawAdapter albumAdp;
    // private ListView albumList;
    private ArtistAlbumListAdapter artistAdp;
    // private ExpandableListView artistList;
    private PlaylistListAdapter playlistAdp;
    //private ListView songList;
    private TrackListRawAdapter tracklistAdp;
    //private ListView playlistList;

	public AlbumListRawAdapter getAlbumAdp() {
		return albumAdp;
	}

	public void setAlbumAdp(AlbumListRawAdapter albumAdp) {
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

	public TrackListRawAdapter getTrackAdp() {
		return tracklistAdp;
	}

	public void setTrackAdp(TrackListRawAdapter tracklistAdp) {
		this.tracklistAdp = tracklistAdp;
	}

	/**
     * 果たしてmapごと保存してうまくいくのか・・・。TODO 要確認
     */
    @Override
    public Object onRetainNonConfigurationInstance() {
    	// TODO: できたらadapterを返却
        return null;// adapters;
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
	
	public static int TIMECHAR_WIDTH = 80;
	public static int TIMECHAR_HEIGHT = 100;

	static int currentMainTabId = TabPage.TABPAGE_ID_UNKNOWN;
	static int currentSubTabId = TabPage.TABPAGE_ID_UNKNOWN;
	
	IDisplayState stateMain = null;
//	int mainTabId = TabPage.TABPAGE_ID_UNKNOWN;
	IDisplayState stateSub = null;
//	int subTabId = TabPage.TABPAGE_ID_UNKNOWN;
//	public static final String tabNameMain = "maintab";
//	public static final String tabNameMedia = "mediatab";
	public static int getCurrentSubTabId()
	{
		return currentSubTabId;
	}
	
	LinearLayout pageContainer = null;
	RelativeLayout componentContainer = null;
	
	// private static String dispIdKey = "_displayId";
	
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
	public Tab getTabMain()
	{
		return tab;
	}
	private static TabMediaSelect tabMedia = null;
	public static TabMediaSelect createMediaTab(
		LinearLayout pageContainer, ViewGroup componentContainer )
	{
		tabMedia = new TabMediaSelect( ControlIDs.TAB_ID_MEDIA, pageContainer, componentContainer );
		tabMedia.create(R.layout.tab_layout_hooter);
		return tabMedia;
	}
	public TabMediaSelect getMediaTab()
	{
		return tabMedia;
	}
	
	// 初期化時に、スクリーンサイズ取得にスレッドが必要になるため、スレッドとの同期が必要に・・・
	private static Handler handler = null;
	public Handler getHandler()
	{
		return handler;
	}
	boolean bInitEnd = false;
	
	private static HashMap<Integer,Integer> tabCurrentDisplayIdMap = new HashMap<Integer,Integer>();
	/**
	 * 現在の画面IDを設定する
	 * 今のところ、画面IDというのは、タブIDに等しい
	 * また、この値は、アクティビティにあるが、アプリケーション全体で利用する感じのものである。
	 * とりあえずstaticにしておくが、クラスを移動してもいいかもしれない
	 * @param internalID
	 * @param iDispId
	 */
	public static void setCurrentDisplayId( int internalID, int iDispId )
	{
		tabCurrentDisplayIdMap.put( internalID, iDispId );
	}
	public static int getCurrentDisplayId( int internalID )
	{
		if( tabCurrentDisplayIdMap.containsKey( internalID ))
		{
			return tabCurrentDisplayIdMap.get( internalID );
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
        
        // ビューの設定
        setContentView(R.layout.main);

        // Databaseクラスにアクティビティ格納
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
        //HideTabComponentAction.getInstance().setTabLayout(componentContainer);
        //ShowTabComponentAction.getInstance().setTabLayout(componentContainer);

        // 時間表時の初期化
		updateTimeDisplayVisible(0);
		updateTimeDisplay(0);
        
        // サービスへの接続を開始
        if( 0 == MediaPlayerUtil.getServiceConnectionCount() )
        {
        	mToken = MediaPlayerUtil.bindToService(this, this);
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
	        	switch( message.what )
        		{
	        		case REFRESH:
	        		{
		                long next = NO_REFRESH;
		                if( currentMainTabId != TabPage.TABPAGE_ID_MEDIA )
		                {
		        			if( stateMain == null )
		        			{
		        				break;
		        			}
		        			next = stateMain.updateDisplay();
		                }
		                else
		                {
		        			if( stateSub == null )
		        			{
		        				break;
		        			}
		                	next = stateSub.updateDisplay();
		                }
		                queueNextRefresh(next);
		                break;
        			}
		        	case DisplayInfo.MSG_INIT_END:
		        	{
		        		// 現状、これがOnResume時のディスプレイ初期化後に飛んでくる
			        	if( bInitEnd == true )
			        	{
			        		// もう既に初期化済ならば、何もしない？
			        	}
			        	else
			        	{
			        		TimeControlPanel.createInstance(OkosamaMediaPlayerActivity.this);
			        		PlayControlPanel.createInstance(OkosamaMediaPlayerActivity.this);
			        		SubControlPanel.createInstance(OkosamaMediaPlayerActivity.this);
				            
			        		// 初期化されていなければ、タブを作成
			        		// このアクティビティのレイアウトクラスを渡す
				            tab = new Tab(
				            	ControlIDs.TAB_ID_MAIN
				            	,pageContainer
				            	,componentContainer 
				            );
				            tab.create(R.layout.tab_layout_header);
			        	}
			    		bInitEnd = true;
			        	
			            // 時間表示等の初期化
			    		updateTimeDisplayVisible(0);
			    		updateTimeDisplay(0);
			    		updatePlayStateButtonImage();

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
			    		setCurrentDisplayId( ControlIDs.TAB_ID_MAIN, 
			    				pref.getInt( String.valueOf( ControlIDs.TAB_ID_MAIN ), TabPage.TABPAGE_ID_UNKNOWN) );
			    		setCurrentDisplayId( ControlIDs.TAB_ID_MEDIA, 
			    				pref.getInt( String.valueOf( ControlIDs.TAB_ID_MEDIA ), TabPage.TABPAGE_ID_ARTIST ) );//TabPage.TABPAGE_ID_UNKNOWN) );
//			           	setMainTabSelection(
//			           		pref.getInt(tabNameMain + dispIdKey, TabPage.TABPAGE_ID_UNKNOWN)
//			           	);
//			           	setMediaTabSelection(
//			           		pref.getInt(tabNameMedia + dispIdKey, TabPage.TABPAGE_ID_UNKNOWN)
//			           	);
			           	sendUpdateMessage(ControlIDs.TAB_ID_MAIN);
			           	sendUpdateMessage(ControlIDs.TAB_ID_MEDIA);
//			           	TabSelectAction selAct = new TabSelectAction(tab, currentMainTabId);
//			           	selAct.doAction(null);
//			           	TabSelectAction selActMedia = new TabSelectAction( tab.getTabPageMedia().getTabContent(), currentSubTabId );
//			           	selActMedia.doAction(null);
			           	if( TimeControlPanel.getInstance() != null )
			           	{
			           		TimeControlPanel.getInstance().setDurationLabel(0);
			           	}
			           	reScanMedia(ControlIDs.ID_NOT_SPECIFIED, true);
			    				           	
	//	           		if( 1 == setTabSelection( currentMainTabId, currentSubTabId ) )
	//	           		{
		    	           	// updateListeners();	           			
	//	           		}
		//	           	}
			            //bTabInitEnd = true;
			           	
			           	// 初期化時に、全てのメディアを取得する
			           	reScanMedia(TabPage.TABPAGE_ID_ALBUM);
			           	reScanMedia(TabPage.TABPAGE_ID_SONG);
			    		break;
		        	}
		        	case TabSelectAction.MSG_ID_TAB_SELECT:
		        	{
		        		// タブが選択された通知
//		        		if( message.arg2 == TabPage.TABPAGE_ID_MEDIA 
//		        		&& currentMainTabId == TabPage.TABPAGE_ID_MEDIA )
//		        		{
//		        			// メディアタブ内での処理
//		        			// 何も処理しない
//		        			break;
//		        		}
		        		if( ControlIDs.TAB_ID_MAIN == (Integer)message.obj )
		        		{
			        		// Activityのタブidを更新
			        		setMainTabSelection(
			        			OkosamaMediaPlayerActivity.getCurrentDisplayId( ControlIDs.TAB_ID_MAIN )
			        		);
		        		}
		        		else if( ControlIDs.TAB_ID_MEDIA == (Integer)message.obj )
		        		{
			        		setMediaTabSelection(
			        			OkosamaMediaPlayerActivity.getCurrentDisplayId( ControlIDs.TAB_ID_MEDIA )
			        		);
		        		}
		        		// リスナを更新
		            	updateListeners();
		            	// メディアを更新
		            	reScanMedia((Integer)message.obj,false);
		            	// 共通部分再描画
		            	updateCommonCtrls();
		            	updatePlayStateButtonImage();
		        		break;
		        	}
        		}
        	}
        };
    }
    public static final int NO_REFRESH = -10;
    public static final int DEFAULT_REFRESH_MS = 500;
    public void queueNextRefresh(long delay) {
        if (!paused && delay != NO_REFRESH) {
            Message msg = handler.obtainMessage(REFRESH);
            handler.removeMessages(REFRESH);
            handler.sendMessageDelayed(msg, delay);
        }
    }
    /**
     * ベース画像上での絶対座標を指定した位置を表すLayoutParamを作成する
     * 幅、高さはFILL_PARENT
     * @param left
     * @param top
     * @return LayoutParam
     */
	public static RelativeLayout.LayoutParams 
	createLayoutParamForAbsolutePosOnBk(
			int left, int top )
	{
		int xCorrect = dispInfo.getCorrectionXConsiderDensity(left);
		int yCorrect = dispInfo.getCorrectionYConsiderDensity(top);
		
		RelativeLayout.LayoutParams lp = 
				new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
		
        lp.topMargin = yCorrect;
        lp.leftMargin = xCorrect;
        // このアプリケーションでは、bottomとrightのmarginはゼロだが・・・。
        lp.bottomMargin = 0;
        lp.rightMargin = 0;
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        
        return lp;
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
		for(Entry<Integer, Integer> e : tabCurrentDisplayIdMap.entrySet()) {
			editor.putInt( String.valueOf( e.getKey() ), e.getValue() );
		}
		editor.commit();
		
		// TODO Auto-generated method stub
		// setTabSelection( TabPage.TABPAGE_ID_NONE, TabPage.TABPAGE_ID_NONE );
		// 本当は、上記でそうなって欲しいのだが、現状そうなっていないかもしれない
		// 全てのビューをクリアしておく
//		if( pageContainer.getBackground() != null )
//		{
//			pageContainer.getBackground().setCallback(null);
//			pageContainer.setBackgroundDrawable(null);
//		}
		componentContainer.removeAllViews();
		bInitEnd = false;
		bForceRefresh = true;
        getResourceAccessor().releaseSound();
        if( TimeControlPanel.getInstance() != null )
        {
        	TimeControlPanel.getInstance().removeViewFromParent();
        }
        if( PlayControlPanel.getInstance() != null )
        {
        	PlayControlPanel.getInstance().removeViewFromParent();
        }
        if( SubControlPanel.getInstance() != null )
        {
        	SubControlPanel.getInstance().removeViewFromParent();
        }
        
        // カーソルクローズ
        // Album
        Cursor cTmp = Database.getInstance(this).getCursor( Database.AlbumCursorName );
        if( cTmp != null && cTmp.isClosed() == false )
        {
        	cTmp.close();
        	Database.getInstance(this).setCursor(Database.AlbumCursorName, null);
        }
        // Artist
        cTmp = Database.getInstance(this).getCursor( Database.ArtistCursorName );
        if( cTmp != null && cTmp.isClosed() == false )
        {
        	cTmp.close();
        	Database.getInstance(this).setCursor(Database.ArtistCursorName, null);
        }
        // Song
        cTmp = Database.getInstance(this).getCursor( Database.SongCursorName );
        if( cTmp != null && cTmp.isClosed() == false )
        {
        	cTmp.close();
        	Database.getInstance(this).setCursor(Database.SongCursorName, null);
        }
        // Playlist
        cTmp = Database.getInstance(this).getCursor( Database.PlaylistCursorName );
        if( cTmp != null && cTmp.isClosed() == false )
        {
        	cTmp.close();
        	Database.getInstance(this).setCursor(Database.PlaylistCursorName, null);
        }
        
		// bTabInitEnd = false;
		// System.gc();
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
        getResourceAccessor().initSound();
        bForceRefresh = true;
		super.onResume();
	}

	/**
	 * サブタブは、状況によって変化するので注意
	 * まだこの関数は未完成（その、サブタブの選択部分)
	 * @param mainTab	 
	 * @param bSndChgMsg 変化があったとき、メッセージを送信するか
	 * @return 0:変化なし 1:変化有り -1:エラー
	 */
	public int setMainTabSelection( int mainTab )//, boolean bForceUpd )
	{
		IDisplayState stateMainTmp = DisplayStateFactory.createDisplayState(mainTab);
		if( stateMainTmp == null )
		{
			return -1;
		}
		
		int iRet = 0;
		//int iRet2 = 0;
		if( currentMainTabId != mainTab || bForceRefresh == true )
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
        return iRet;
	}
	/**
	 * サブタブは、状況によって変化するので注意
	 * まだこの関数は未完成（その、サブタブの選択部分)
	 * @param subTab
	 * @param bSndChgMsg 変化があったとき、メッセージを送信するか
	 * @return 0:変化なし 1:変化有り -1:エラー
	 */
	public int setMediaTabSelection( int subTab )//, boolean bForceUpd )
	{
		IDisplayState stateSubTmp = DisplayStateFactory.createDisplayState(subTab);        		
        if( stateSubTmp == null )
        {
        	return -1;
        }
		
		int iRet = 0;
        if( stateSubTmp != null )
        {
        	if( currentMainTabId == TabPage.TABPAGE_ID_MEDIA )
        	{
	        	if( currentSubTabId != subTab || bForceRefresh == true )
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
        		// DisplayStateFactory.createDisplayState(TabPage.TABPAGE_ID_NONE).ChangeDisplayBasedOnThisState(tabMedia);  
        	}
        }
        return iRet;
	}
	void sendUpdateMessage(int tabID)//String tabName)
	{
    	Message msg = new Message();
    	msg.what = TabSelectAction.MSG_ID_TAB_SELECT;
    	msg.obj = tabID;
    	handler.sendMessage(msg);
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
		
		// 時間表時の再描画?
		queueNextRefresh(1);
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
			getAlbumAdp().insertAllDataFromCursor(cursor);//changeCursor(cursor);
			break;
		case TabPage.TABPAGE_ID_ARTIST:
			// Listにカーソルを設定
			getArtistAdp().changeCursor(cursor);
			break;
		case TabPage.TABPAGE_ID_SONG:
			// Listにカーソルを設定
			getTrackAdp().insertAllDataFromCursor(cursor);
			break;
		case TabPage.TABPAGE_ID_PLAYLIST:
			Cursor c = null;
			c = Database.getInstance(isExternalRef()).mergedCursor(cursor, false);//createShortCut);
			// Listにカーソルを設定
			getPlaylistAdp().changeCursor(c);
			break;
		}
	}
	
	public void reScanMedia(int tabID)
	{
		switch( tabID )
		{
		case TabPage.TABPAGE_ID_ALBUM:
			// Listにカーソルを設定
            Log.i("test", "rescan album 2");
			Database.getInstance(externalRef).createAlbumCursor(getAlbumAdp().getQueryHandler(), null );
			break;
		case TabPage.TABPAGE_ID_ARTIST:			
			// Listにカーソルを設定
			Database.getInstance(externalRef).createArtistCursor(getArtistAdp().getQueryHandler(), null);			
			break;
		case TabPage.TABPAGE_ID_SONG:
			
			OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.setPlaylistName( null );        	
			Database.getInstance(externalRef).createTrackCursor(getTrackAdp().getQueryHandler(), null );//, null, null, null);			
			break;
		case TabPage.TABPAGE_ID_PLAYLIST:
			
			Database.getInstance(externalRef).createPlaylistCursor(getPlaylistAdp().getQueryHandler(), null, false);						
			break;
		case TabPage.TABPAGE_ID_NONE:
			break;
		}
		
	}
	
	
	/**
	 * メディアの再スキャン？スキャンのロジック自体に、見直し必要
	 * @param tabID
	 * @param bForce
	 */
	public void reScanMedia(int tabID, boolean bForce)
	{
		// 現在選択中のタブによって操作を変更
		if( //tabName == tabNameMedia && 
			( true == bForce
			|| ControlIDs.TAB_ID_MEDIA == tabID ) )
			//&& currentMainTabId == TabPage.TABPAGE_ID_MEDIA )
		{
			// メディアタブならば
			// メディアを再度クエリ発行して更新する
			switch( currentSubTabId )
			{
			case TabPage.TABPAGE_ID_ALBUM:
				if( bForce == false 
				&& 0 < getAlbumAdp().getCount() ) { //null != Database.getInstance(this).getCursor( Database.AlbumCursorName )) {
					// 再スキャンは重いので、とりあえず、既にカーソルがある場合、強制でないなら再スキャンしない
		            Log.i("test", "rescan escape album");
					break;
				}
				// Listにカーソルを設定
	            Log.i("test", "rescan album");
				Database.getInstance(externalRef).createAlbumCursor(getAlbumAdp().getQueryHandler(), null );//, null);
				break;
			case TabPage.TABPAGE_ID_ARTIST:
				if( bForce == false 
				&& null != Database.getInstance(this).getCursor( Database.ArtistCursorName )) {
					// 再スキャンは重いので、とりあえず、既にカーソルがある場合、強制でないなら再スキャンしない
					break;
				}
				
				// Listにカーソルを設定
				Database.getInstance(externalRef).createArtistCursor(getArtistAdp().getQueryHandler(), null);			
				break;
			case TabPage.TABPAGE_ID_SONG:
				if( bForce == false 
				&& null != Database.getInstance(this).getCursor( Database.SongCursorName )) {
					// 再スキャンは重いので、とりあえず、既にカーソルがある場合、強制でないなら再スキャンしない
					break;
				}
				
				OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.setPlaylistName( null );
				Database.getInstance(externalRef).createTrackCursor(getTrackAdp().getQueryHandler(), null );//, null, null, null);			
				break;
			case TabPage.TABPAGE_ID_PLAYLIST:
//				if( bForce == false 
//				&& null != Database.getInstance(this).getCursor( Database.PlaylistCursorName )) {
//					// 再スキャンは重いので、とりあえず、既にカーソルがある場合、強制でないなら再スキャンしない
//					break;
//				}
				
				Database.getInstance(externalRef).createPlaylistCursor(getPlaylistAdp().getQueryHandler(), null, false);						
				break;
			case TabPage.TABPAGE_ID_NONE:
				// TODO: クリア処理を入れた方がいいかも
				break;
			}
		}
		else if( tabID == ControlIDs.TAB_ID_MAIN 
				&& currentMainTabId == TabPage.TABPAGE_ID_NOW_PLAYLIST )
		{
			// TODO: 現在、トラックと同じカーソルになっているが、考えた方がいいかもしれない
			// NOWPLAYLIST
			OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.setPlaylistName( Database.PlaylistName_NowPlaying );
			
			Database.getInstance(externalRef).createTrackCursor(getTrackAdp().getQueryHandler(), null );//, null, null, null);
		}
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		// MediaPlayer.unbindFromService(mToken);
		super.onStop();
        paused = true;
        handler.removeMessages(REFRESH);
	}

	@Override
	protected void onDestroy() {
		try {
			if( MediaPlayerUtil.sService != null && false == MediaPlayerUtil.sService.isPlaying() )
			{
				// サービスの登録解除
			    MediaPlayerUtil.unbindFromService(mToken);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onDestroy();
	}

	// とりあえず、必要ではない？
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
        paused = false;
	}
	
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
		Log.e("service disconnect","finish because service disconnect.");
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

	    private static int timeImgResIds[] = {
	    	R.drawable.num0_1
	    	,R.drawable.num1_1
	    	,R.drawable.num2_1
	    	,R.drawable.num3_1
	    	,R.drawable.num4_1
	    	,R.drawable.num5_1
	    	,R.drawable.num6_1
	    	,R.drawable.num7_1
	    	,R.drawable.num8_1
	    	,R.drawable.num9_1
	    };
	    
	    private static final Integer[] sTimeArgs = new Integer[6];
	    public void updateTimeDisplayVisible(long duration)
	    {
	    	boolean bShowImgFlg[] = {
	                ( duration >= (3600*60) )
	                ,( duration >= 3600 )
	                ,( duration >= 600 )
	                ,( duration >= 60)
	                ,( duration >= 10)
	                ,( duration > 0)
	    	};
	    	if( TimeControlPanel.getInstance() != null && TimeControlPanel.getInstance().getTimesButton() != null )
	    	{
	    		Button timeBtns[] = TimeControlPanel.getInstance().getTimesButton();
		        for( int i=0; i<timeBtns.length; i++ )
		        {
		        	if( null != timeBtns[i].getView() )
		        	{
		        		((ButtonImpl)timeBtns[i].getView()).setVisibility(bShowImgFlg[i] ? View.VISIBLE : View.INVISIBLE );
		        	}
		        }
	        }
	    }
	    public void updateTimeDisplay(long secs)
	    {
	        /* Provide multiple arguments so the format can be changed easily
	         * by modifying the xml.
	         */
	        //sFormatBuilder.setLength(0);

	    	long tmp = 0;
	        final Integer[] timeArgs = sTimeArgs;
	        tmp = secs;
	        timeArgs[0] = (int) (tmp / (3600*60));
	        tmp -= timeArgs[0]*(3600*60);
	        timeArgs[1] = (int) (tmp / 3600);
	        tmp -= timeArgs[1]*3600;
	        timeArgs[2] = (int) (tmp / 600);
	        tmp -= timeArgs[2]*600;
	        timeArgs[3] = (int) (tmp / 60);
	        tmp -= timeArgs[3]*60;
	        timeArgs[4] = (int) (tmp / 10);
	        tmp -= timeArgs[4]*10;
	        timeArgs[5] = (int) tmp;
	        
	    	if( TimeControlPanel.getInstance() != null && TimeControlPanel.getInstance().getTimesButton() != null )
	    	{
	    		Button timeBtns[] = TimeControlPanel.getInstance().getTimesButton();
		        for( int i=0; i<timeBtns.length; i++ )
		        {
		        	if( null != timeBtns[i].getView() )
		        	{
		        		((ButtonImpl)timeBtns[i].getView()).setImageResource( timeImgResIds[ timeArgs[i] ] );
		        	}
		        }
	        }
	    }
	    
	    Toast mToast;
	    public void showToast(int resid) {
	        if (mToast == null) {
	            mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
	        }
	        mToast.setText(resid);
	        mToast.show();
	    }
}