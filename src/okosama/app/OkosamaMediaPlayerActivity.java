package okosama.app;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import okosama.app.action.MediaStopAction;
import okosama.app.adapter.AlbumListRawAdapter;
import okosama.app.adapter.AdapterStocker;
import okosama.app.adapter.ArtistAlbumListRawAdapter;
import okosama.app.adapter.IAdapterUpdate;
import okosama.app.adapter.PlaylistListRawAdapter;
import okosama.app.adapter.TrackListRawAdapter;
import okosama.app.adapter.VideoListRawAdapter;
import okosama.app.factory.DroidWidgetKit;
import android.app.Activity;
import android.content.ComponentName;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.ServiceConnection;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
//import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
//import android.media.MediaScannerConnection;
//import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import okosama.app.panel.NowPlayingControlPanel;
import okosama.app.panel.PlayControlPanel;
import okosama.app.panel.SearchPanel;
import okosama.app.panel.SubControlPanel;
import okosama.app.panel.TimeControlPanel;
import okosama.app.service.MediaInfo;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.service.MediaPlayerUtil.ServiceToken;
import okosama.app.state.DisplayStateFactory;
import okosama.app.state.IDisplayState;
import okosama.app.state.StateStocker;
//import okosama.app.storage.ArtistGroupData;
import okosama.app.storage.Database;
import okosama.app.storage.GenreStocker;
import okosama.app.tab.*;
import okosama.app.widget.Button;
import okosama.app.widget.ExpList;
import okosama.app.widget.Image;
import okosama.app.widget.ImageImpl;
import okosama.app.widget.List;
import okosama.app.widget.absWidget;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
//import okosama.app.widget.ButtonImpl;

public class OkosamaMediaPlayerActivity extends Activity
implements ServiceConnection, Database.Defs {
	public static final String MEDIA_SERVICE_NOTIFY = "MediaServiceNotify";	

	public void selectTab(int tabId, int tabPageId, boolean bForce)
	{
		// タブIDを更新
		updateTabId( tabId, tabPageId, bForce );
	
		// リスナを更新
		updateListeners(IDisplayState.STATUS_ON_CREATE);
		updateListeners(IDisplayState.STATUS_ON_RESUME);
		// メディアを更新
		reScanMediaAndUpdateTabPage(tabId,false);
		// 共通部分再描画
		queueNextRefresh(100);
		updatePlayStateButtonImage();
	}
	public Button controllerShowHideBtn = null;
	public Button getControllerShowHideBtn()
	{
		if( controllerShowHideBtn == null )
		{
			controllerShowHideBtn = DroidWidgetKit.getInstance().MakeButton();
		}
		return controllerShowHideBtn;
	}
	public Button searchPanelShowHideBtn = null;
	public Button getSearchPanelShowHideBtn()
	{
		if( searchPanelShowHideBtn == null )
		{
			searchPanelShowHideBtn = DroidWidgetKit.getInstance().MakeButton();
		}
		return searchPanelShowHideBtn;
	}
	
	public SurfaceView surfaceView = null;
	public SurfaceView getVideoView()
	{
		if( surfaceView == null )
		{
			surfaceView = new SurfaceView(this);
		}
		return surfaceView;
	}
	public SurfaceHolder getVideoViewHolder()
	{
		return getVideoView().getHolder();
	}	
	// タブ格納用
	TabStocker tabStocker = new TabStocker();
	public TabStocker getTabStocker()
	{
		return tabStocker;
	}
	// 状態格納用
	StateStocker stateStocker = new StateStocker();
	public StateStocker getStateStocker()
	{
		return stateStocker;
	}
	// ジャンル格納用
	GenreStocker genreStocker = new GenreStocker();
	public GenreStocker getGenreStocker()
	{
		return genreStocker;
	}
	
	public int getCurrentTabPageId()
	{
		int tabPageId = TabPage.TABPAGE_ID_NONE;
		int mainTab = tabStocker.getCurrentTabPageId(ControlIDs.TAB_ID_MAIN);
        if( mainTab == TabPage.TABPAGE_ID_MEDIA )
        {
        	// 選択されたタブページがメディアタブだった場合
        	// 子となるメディアタブも更新させる
        	tabPageId = tabStocker.getCurrentTabPageId(ControlIDs.TAB_ID_MEDIA);
        }
        else if( mainTab == TabPage.TABPAGE_ID_PLAY )
        {
        	// 選択されたタブページがプレイタブだった場合
        	// 子となるプレイタブも更新させる
        	tabPageId = tabStocker.getCurrentTabPageId(ControlIDs.TAB_ID_PLAY);
        }    
        else
        {
        	// それ以外の場合、MainタブのID
        	tabPageId = mainTab;
        }    
        return tabPageId;
	}
	
    // ポーズ中？
    private boolean paused = false;
    public boolean isPaused()
    {
    	return paused;
    }
	public void updatePlayStateButtonImage()
	{
		//Log.e("update playstatebutton","come");
		if( PlayControlPanel.getInstance() != null ) 
		{
			boolean bRealEnabled = OkosamaMediaPlayerActivity.getResourceAccessor().isSdCanRead();
			boolean bEnabled = getResourceAccessor().isReadSDCardSuccess();
			if( bRealEnabled != bEnabled )
			{
				getResourceAccessor().setReadSDCardSuccess(bRealEnabled);
				bEnabled = bRealEnabled;
				// アプリケーションを再起動
                Message msg = handler.obtainMessage(AppStatus.RESTART);
                handler.removeMessages(AppStatus.RESTART);
                handler.sendMessageDelayed(msg, 1);
				return;
			}
			if( PlayControlPanel.getInstance().isEnabled() != bEnabled )
			{
				PlayControlPanel.getInstance().setEnabled(bEnabled);
			
				if( SubControlPanel.getInstance() != null )
				{
					SubControlPanel.getInstance().setEnabled(bEnabled);
				}			
				if( TimeControlPanel.getInstance() != null )
				{
					TimeControlPanel.getInstance().setEnabled(bEnabled);
				}			
				if( NowPlayingControlPanel.getInstance() != null )
				{
					NowPlayingControlPanel.getInstance().setEnabled(bEnabled);
				}
			}
		}
		if( PlayControlPanel.getInstance() != null ) 
			PlayControlPanel.getInstance().setPlayPauseButtonImage();
		
		if( SubControlPanel.getInstance() != null )
		{
			SubControlPanel.getInstance().setShuffleButtonImage();
			SubControlPanel.getInstance().setRepeatButtonImage();
		}
	}
	public void updateVideoView()
	{
		if( getVideoView() != null ) 
		{
			try {
				if( MediaPlayerUtil.sService != null 
				&& 0 <= MediaPlayerUtil.sService.getQueuePosition() 
				&& MediaPlayerUtil.sService.getCurrentType() == MediaInfo.MEDIA_TYPE_VIDEO )
				{
					getVideoView().setVisibility(View.VISIBLE);
					return;
				}
			} catch (RemoteException e) {
			}
			getVideoView().setVisibility(View.GONE);

		}
	}
	// サービスのトークン
	// TODO:Trying to unbind with null tokenというエラーが発生
    private static ServiceToken mToken = null;
    
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
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity()
			.reScanMediaAndUpdateTabPage(ControlIDs.ID_NOT_SPECIFIED, true);
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
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMediaAndUpdateTabPage(ControlIDs.ID_NOT_SPECIFIED, true);
		}
	}
	private static boolean internalRef = true;
    
    
    // Adapter格納用マップ
	private AdapterStocker adpStocker = new AdapterStocker();
	public AdapterStocker getAdpStocker()
	{
		return adpStocker;
	}
	public IAdapterUpdate getAdapter(int id)
	{
		return adpStocker.get(id);
	}
	public void putAdapter(int key,IAdapterUpdate adp)
	{
		adpStocker.put(key, adp);
	}
	public TrackListRawAdapter getTrackAdp()
	{
		return (TrackListRawAdapter) getAdapter(TabPage.TABPAGE_ID_SONG);
	}
	public AlbumListRawAdapter getAlbumAdp()
	{
		return (AlbumListRawAdapter) getAdapter(TabPage.TABPAGE_ID_ALBUM);
	}
	public VideoListRawAdapter getVideoAdp()
	{
		return (VideoListRawAdapter) getAdapter(TabPage.TABPAGE_ID_VIDEO);
	}
	public ArtistAlbumListRawAdapter getArtistAdp()
	{
		return (ArtistAlbumListRawAdapter) getAdapter(TabPage.TABPAGE_ID_ARTIST);
	}
	public PlaylistListRawAdapter getPlaylistAdp()
	{
		return (PlaylistListRawAdapter) getAdapter(TabPage.TABPAGE_ID_PLAYLIST);
	}
	
	public absWidget getListFromTabID(int tabID)
	{
		switch( tabID )
		{
		case TabPage.TABPAGE_ID_ALBUM:
			return getList( List.LISTID_ALBUM );
		case TabPage.TABPAGE_ID_ARTIST:
			return getExpList( ExpList.LISTID_ARTIST );
		case TabPage.TABPAGE_ID_SONG:
			return getList( List.LISTID_SONG );
		case TabPage.TABPAGE_ID_PLAYLIST:
			return getList( List.LISTID_PLAYLIST );
			
		}
		return null;
	}
	
	SparseArray<List> lists = new SparseArray<List>();
	public void setList(int id, List lst)
	{
		lists.put(id, lst);
	}
	public List getList(int id)
	{
		if( lists.indexOfKey(id) != -1 )
		{
			return lists.get(id);
		}
		return null;
	}
	SparseArray<ExpList> explists = new SparseArray<ExpList>();
	public void setExpList(int id, ExpList lst)
	{
		explists.put(id, lst);
	}
	public ExpList getExpList(int id)
	{
		if( 0 <= explists.indexOfKey(id) )
		{
			return explists.get(id);
		}
		return null;
	}

	// タブの初期化が終わったかどうか
	// onCreateとonResumeでの処理のダブりを回避する目的
	// boolean bTabInitEnd = false;
	
//	public static int TIMECHAR_WIDTH = 80;
//	public static int TIMECHAR_HEIGHT = 100;

	LinearLayout pageContainer = null;
	public LinearLayout getMainPageContainer()
	{
		return pageContainer;
	}
	RelativeLayout componentContainer = null;
	public RelativeLayout getMainComponentContainer()
	{
		return componentContainer;
	}
	
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
	
	// 初期化時に、スクリーンサイズ取得にスレッドが必要になるため、スレッドとの同期が必要に・・・
	private static Handler handler = null;
	public Handler getHandler()
	{
		return handler;
	}
    public void queueNextRefresh(long delay) {
        if (!paused && delay != AppStatus.NO_REFRESH) {
            Message msg = handler.obtainMessage(AppStatus.REFRESH);
            handler.removeMessages(AppStatus.REFRESH);
            handler.sendMessageDelayed(msg, delay);
        }
    }	
	// boolean bInitEnd = false;
	
	// private static HashMap<Integer,Integer> tabCurrentDisplayIdMap = new HashMap<Integer,Integer>();
	public void clearDisplayIdMap()
	{
		tabStocker.clearCurrentTabPageId();
	}
	public void reloadDisplayIdMap()
	{
		SharedPreferences pref = this.getPreferences(MODE_PRIVATE);
		setCurrentDisplayId( ControlIDs.TAB_ID_MAIN, 
				pref.getInt( String.valueOf( ControlIDs.TAB_ID_MAIN ), TabPage.TABPAGE_ID_MEDIA) );
		setCurrentDisplayId( ControlIDs.TAB_ID_MEDIA, 
				pref.getInt( String.valueOf( ControlIDs.TAB_ID_MEDIA ), TabPage.TABPAGE_ID_ARTIST ) );
		setCurrentDisplayId( ControlIDs.TAB_ID_PLAY,
						TabPage.TABPAGE_ID_PLAY_SUB );//,
				//pref.getInt( String.valueOf( ControlIDs.TAB_ID_PLAY ),
						//TabPage.TABPAGE_ID_PLAY_SUB );
		
	}
	/**
	 * 現在の画面IDを設定する
	 * 今のところ、画面IDというのは、タブIDに等しい
	 * また、この値は、アクティビティにあるが、アプリケーション全体で利用する感じのものである。
	 * とりあえずstaticにしておくが、クラスを移動してもいいかもしれない
	 * @param internalID
	 * @param iDispId
	 */
	public void setCurrentDisplayId( int internalID, int iDispId )
	{
		tabStocker.setCurrentTabPageId(internalID, iDispId);
		// tabCurrentDisplayIdMap.put( internalID, iDispId );
	}
	public int getCurrentDisplayId( int internalID )
	{
		// tabStocker.setCurrentTabId(internalID, iDispId);
		// tabStocker.getTabPageIdMap().get(internalID);
		
		if( 0 <= tabStocker.getTabPageIdMap().indexOfKey( internalID ))
		{
			return tabStocker.getCurrentTabPageId( internalID );
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
        // タイトルバーを非表示に？
        requestWindowFeature(Window.FEATURE_NO_TITLE);
 
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        // ビューの設定
        setContentView(R.layout.main);
 
        // Databaseクラスにアクティビティ格納
        Database.setActivity( this );
        // handlerクラス作成
        handler = new MainHandler( this );
        handler.sendEmptyMessage(AppStatus.INIT_ALL_REFRESH);
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

		OkosamaMediaPlayerActivity.getResourceAccessor().setReadSDCardSuccess(OkosamaMediaPlayerActivity.getResourceAccessor().isSdCanRead());
        
        // 時間表時の初期化
		updateTimeDisplayVisible(0);
		updateTimeDisplay(0);
        
        // サービスへの接続を開始
        if( 0 == MediaPlayerUtil.getServiceConnectionCount() 
        || MediaPlayerUtil.sService == null )	// 横での起動対応
        {
        	// 絶対に１つしか接続されないようにする
        	mToken = MediaPlayerUtil.bindToService(this, this);
        	// Toast.makeText(this, "service registered : token=" + mToken, Toast.LENGTH_LONG).show();
        }
//        else
//        {
//        	// 既にコネクションがある場合
//        	// コネクションのidを調べ、現在のactivityと違う場合は、接続し直す？
//        	if( MediaPlayerUtil.hasServiceConnection(this) == false )
//        	{
//        		// コネクションはあるが、このアクティビティのコネクションはない
//        		
//        	}
//        }
        // TODO:メディアスキャンの実行
        // doMediaScan();
    }
	// 対象の拡張子とmineTypeの配列作成
	class ExtWithInfo
	{
		ExtWithInfo(String ext, String mine)
		{
			extension = ext;
			mineType= mine;
		}
		String extension;
		String mineType;
		/**
		 * @return the extension
		 */
		public String getExtension() {
			return extension;
		}
		/**
		 * @param extension the extension to set
		 */
		public void setExtension(String extension) {
			this.extension = extension;
		}
		/**
		 * @return the mineType
		 */
		public String getMineType() {
			return mineType;
		}
		/**
		 * @param mineType the mineType to set
		 */
		public void setMineType(String mineType) {
			this.mineType = mineType;
		}	    
		
	};
	ExtWithInfo mMediaScanTargetExtInfo[] = {
		new ExtWithInfo(".3gp","video/3gpp")
		,new ExtWithInfo(".mp4","video/mp4")
		,new ExtWithInfo(".m4v","video/mp4")
		,new ExtWithInfo(".aac","audio/aac")
		//".m4a",
		//".flac",
		,new ExtWithInfo(".mp3","audio/mpeg")
		,new ExtWithInfo(".ogg","audio/ogg")	// videoもありえる？
		,new ExtWithInfo(".wav","audio/wav")
				//,
		//".webm"
	};

	public int updateAndroidMediaDatabase()
	{
		// メディアスキャンの実行
		MediaScannerConnection.scanFile(
				getApplicationContext(),
				mediaScanTarget.toArray(new String[mediaScanTarget.size()]),
				mediaScanTargetMine.toArray(new String[mediaScanTarget.size()]),
		        null);
		return 0;
	}
    ArrayList<String> mediaScanTarget = new ArrayList<String>();
    //HashSet<String> mediaScanExcludeTarget = new HashSet<String>();
    ArrayList<String> mediaScanTargetMine = new ArrayList<String>();
    public int doMediaScan()
    {
    	// 果たしてスレッドにしていいのかどうか・・・
        AsyncTask<Activity, Void, Integer> task = new AsyncTask<Activity, Void, Integer>() {
            @Override
            protected Integer doInBackground(Activity... params) {
            	Log.i("mediascan - doInBackground","start");
            	mediaScanTarget.clear();
            	mediaScanTargetMine.clear();
            	String status = Environment.getExternalStorageState();
            	if( false == Environment.MEDIA_MOUNTED.equals(status) )
            	{
            		Log.w("external storage status",status);
            		return 0;
            	}
            	// SDカードのルートから、全てのファイルを検索
            	String sdroot_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
            	//String sdCardAndroid = sdroot_path + "Android";	// 不安が残る・・・
            	File sdroot = new File(sdroot_path);
            	Log.i("sdroot_path",sdroot_path);
            	//Log.i("sdandroid", sdCardAndroid );
            	getMediaScanTarget(sdroot);
            	//File sdmusicdir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            	//File sdmoviedir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
            	//File sdOkosama = OkosamaMediaPlayerActivity.this.getExternalFilesDir(null);
            	//Log.i("sdmusic_path",sdmusicdir.getPath());
            	//getMediaScanTarget(sdmusicdir);
            	//Log.i("sdmovies_path",sdmoviedir.getPath());
            	//getMediaScanTarget(sdmoviedir);
            	handler.sendEmptyMessage(MainHandler.MEDIA_SCAN_TARGET_CREATED);
            	
                return 0;
            }

            @Override
            protected void onPostExecute(Integer ret) 
            {
            	//MediaScannerConnection.
            	Log.i("onPostExecute(mediascan)","ret=" + ret );
            }
        };
        task.execute();
        return 0;
		// メディアスキャンの実行
//    	String[] filePath = {
//    			Environment.getExternalStorageDirectory().toString()
//    	};
//    	String[] mimeType = {"*/*"};
//    	MediaScannerConnection.scanFile(
//    			this,
//    	        filePath,
//    	        mimeType,
//    	        null);
    	
    	// 👇よく載っているが、本来は不正なやり方なので、
    	// 新しいandroidだと落ちる
//		String _url = "file://" + Environment.getExternalStorageDirectory();
//		Uri _uri = Uri.parse(_url);
//		sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, _uri));	
    }

    private void getMediaScanTarget(File dir)//String dirPath)
    {
    	// ディレクトリのパスに.が含まれていたら、データフォルダとみなして除外する
    	// 困ることもあるかもしれないが、とりあえずまあいいでしょう・・・。
    	if( dir.getPath().indexOf('.') != -1 )
    	{
    		return;
    	}
    	
    	// File dir = new File(dirPath);
    	final File[] files = dir.listFiles();

    	if( files == null )
    	{
    		return;
    	}
    	// 対象の拡張子に一致するものだけを抜く
    	for (int i = 0; i < files.length; i++) {
    		if( files[i].isDirectory() )
    		{
    			// ディレクトリ検索
    			getMediaScanTarget( files[i] );
    		}
    		else if( files[i].isFile() && files[i].isHidden() == false )
    		{
    			// ファイルならば、拡張子が対象のものか調べる
    			for( ExtWithInfo inf : mMediaScanTargetExtInfo )
    			{
	        		if( files[i].getPath().endsWith(inf.getExtension()) )
	        		{
	        			mediaScanTarget.add( files[i].getPath() );
	        			mediaScanTargetMine.add( inf.getMineType() );
	        			Log.i("get mediaScanTarget", "path=" + files[i].getPath() 
	        					+ " mineType=" + inf.getMineType());
	        		}
    			}
    		}
    		
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
		// 指定された左位置に対して、ディスプレイサイズを考慮した調整を行う
		int xCorrect = dispInfo.getCorrectionXConsiderDensity(left);
		int yCorrect = dispInfo.getCorrectionYConsiderDensity(top);
		
		// 幅と高さの指定がないので、親を埋めるように設定する
		RelativeLayout.LayoutParams lp = 
				new RelativeLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		// ここで、縦横の変換をかます
		// ソースコードに書いてある座標、大きさは縦用のものがだが、横向きの場合、横用に変換して座標を返す		
		if( true == dispInfo.isPortrait() )
		{
	        lp.topMargin = yCorrect;
	        lp.leftMargin = xCorrect;
		}
		else
		{
			lp.leftMargin = yCorrect;
			lp.topMargin = xCorrect;
		}
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
		return createLayoutParamForAbsolutePosOnBk(left, top, width, height, true );
	}
	
	public static RelativeLayout.LayoutParams 
	createLayoutParamForAbsolutePosOnBk(
			int left, int top, int width, int height, boolean bConvertPortraitAndHorz )
	{
		int widthCorrect = 0;
		if( width == RelativeLayout.LayoutParams.FILL_PARENT
		|| width == RelativeLayout.LayoutParams.WRAP_CONTENT )
		{
			widthCorrect = width;
		}
		else
		{
			widthCorrect = dispInfo.getCorrectionXConsiderDensity(width);
		}
		int heightCorrect = 0;
		if( height == RelativeLayout.LayoutParams.FILL_PARENT
		|| height == RelativeLayout.LayoutParams.WRAP_CONTENT )
		{
			heightCorrect = height;
		}
		else
		{
			heightCorrect = dispInfo.getCorrectionYConsiderDensity(height);
		}
		int xCorrect = 0;
			xCorrect = dispInfo.getCorrectionXConsiderDensity(left);
//		}
//		else
//		{
//			xCorrect = dispInfo.getCorrectionYConsiderDensity(top);			
//		}
		int yCorrect = 0;
		int topRule = RelativeLayout.ALIGN_PARENT_TOP;
//		if(dispInfo.isPortrait())
//		{		
			yCorrect = dispInfo.getCorrectionYConsiderDensity(top);
//		}
//		else
//		{
//			yCorrect = dispInfo.getCorrectionXConsiderDensity(left);
//		}

		if( yCorrect < 0 )
		{
			yCorrect = -1 * dispInfo.getCorrectionYConsiderDensity(top);
			topRule = RelativeLayout.ALIGN_PARENT_BOTTOM;
		}
//		else
//		{
//			yCorrect = dispInfo.getCorrectionYConsiderDensity(top);
//		}
		
		RelativeLayout.LayoutParams lp = null;

		// ここで、縦横の変換をかます
		// ソースコードに書いてある座標、大きさは縦用のものがだが、横向きの場合、横用に変換して座標を返す		
		if( true == dispInfo.isPortrait() || bConvertPortraitAndHorz == false )
		{
			lp = new RelativeLayout.LayoutParams(
					widthCorrect, heightCorrect);
	        lp.topMargin = yCorrect;
	        lp.leftMargin = xCorrect;
	        // このアプリケーションでは、bottomとrightのmarginはゼロだが・・・。
	        lp.bottomMargin = 0;
	        lp.rightMargin = 0;
		}
		else
		{
			lp = new RelativeLayout.LayoutParams(
					heightCorrect, widthCorrect);
	        lp.topMargin = xCorrect;
	        lp.leftMargin = yCorrect;
	        // このアプリケーションでは、bottomとrightのmarginはゼロだが・・・。
	        lp.bottomMargin = 0;
	        lp.rightMargin = 0;
		}
		
        lp.addRule(topRule);//RelativeLayout.ALIGN_PARENT_TOP);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        
        return lp;
	}
//	RelativeLayout.LayoutParams createLayoutParamForAbsolutePos(
//			int width, int height, int left, int top )
//	{
//		RelativeLayout.LayoutParams lp = null;
//		if( true == dispInfo.isPortrait() )
//		{
//			lp = new RelativeLayout.LayoutParams(
//	        		(height), (width));			
//	        lp.topMargin = ( top );
//	        lp.leftMargin = ( left );
//	        lp.bottomMargin = 0;
//	        lp.rightMargin = 0;
//		}
//		else
//		{
//			lp = new RelativeLayout.LayoutParams(
//	        		(width), (height));			
//	        lp.topMargin = left;
//	        lp.leftMargin = top;
//	        lp.bottomMargin = 0;
//	        lp.rightMargin = 0;
//		}
//        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        
//        return lp;
//	}
	
	// 画面開店時の値のバックアップと復元
	// 今のところ、不要
//	@Override
//    public void onSaveInstanceState(Bundle outcicle) {
//		// TODO:マップをループして、全部の設定を保存
//        super.onSaveInstanceState(outcicle);
//    }	
//	@Override
//	protected void onRestoreInstanceState(Bundle savedInstanceState) {
//		super.onRestoreInstanceState(savedInstanceState);
//	}
	IntentFilter intentFilter;
	BroadcastReceiver receiver;
	
	/**
	 * メディアサービスからのintentのレシーバ
	 * @author 25689
	 *
	 */
	class MediaServiceNotifyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) 
		{
			// メディアサービスからintentを受け取ったら
			// 再生ボタンの表示更新
			updatePlayStateButtonImage();
		}
	}
	@Override
	protected void onResume() {
		Log.w("onResume","resume!");
    	// 画面のサイズ等の情報を更新する
		// 終わったらhandlerッセージが送られる
		// 現在、そこで初めて画面位置の初期化を行っている
        dispInfo.init(this, componentContainer, handler,false);
        
        //bForceRefresh = true;
        paused = false;
        // レシーバの作成、登録
        receiver = new MediaServiceNotifyReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(MEDIA_SERVICE_NOTIFY);
        registerReceiver(receiver,intentFilter);
        
		super.onResume();
	}

	@Override
	protected void onPause() {
		handler.removeMessages(AppStatus.INIT_ALL_REFRESH);
		
		// マップをループして、全部の設定を保存
		Editor editor = getPreferences(MODE_PRIVATE).edit();
		// 現在選択されているタブID
		for(int i=0; i < tabStocker.getTabPageIdMap().size(); ++i ) {
			editor.putInt( String.valueOf( tabStocker.getTabPageIdMap().keyAt(i) ),
					tabStocker.getTabPageIdMap().valueAt(i) );
		}
		editor.commit();
		
		paused = true;

		// 効果音クラスの解放
        getResourceAccessor().releaseSound();
        // 全てのレシーバの登録解除
        stateStocker.unResisterReceiverAll();
        if( null != receiver )
        {
        	this.unregisterReceiver(receiver);
        	receiver = null;
        }
        
        // モーションセンサの登録解除
        getResourceAccessor().rereaseMotionSenser();
        
        // サービスとの連携がされていないのに、Activity終了が起こる場合
//        if( MediaPlayerUtil.sService == null && mToken != null )
//        {
//        	
//        }
		super.onPause();
	}
	
	/**
	 * メインタブの選択の変更
	 * @param 新しく選択される、mainTabのタブページID 
	 * @param bForceRefresh 強制的にリフレッシュするか
	 * @return 0:変化なし 1:変化有り -1:エラー
	 */
	public int setMainTabSelection( int mainTab, boolean bForceRefresh )
	{	
		// 新しく選択されるタブのステータスクラスを作成
		IDisplayState stateMainTmp = DisplayStateFactory.createDisplayState(mainTab);
		if( stateMainTmp == null )
		{
			return -1;
		}	
		int iRet = 0;
		//int iRet2 = 0;
		if( tabStocker.getCurrentTabPageId(ControlIDs.TAB_ID_MAIN) != mainTab
				|| bForceRefresh == true )
        {
			// タブが変わっているか、強制リフレッシュの場合
			// Log.w("setMainTabSelection", "come");
			if( stateStocker.getState(ControlIDs.TAB_ID_MAIN) != null )
			{
				// 前のタブのレシーバを登録解除
				stateStocker.getState(ControlIDs.TAB_ID_MAIN).unregisterReceivers(IDisplayState.STATUS_ON_DESTROY);
			}
			// 選択されているタブの変更
			tabStocker.setCurrentTabPageId(ControlIDs.TAB_ID_MAIN, mainTab );
			// ステータスクラスの変更
			stateStocker.putState(ControlIDs.TAB_ID_MAIN, stateMainTmp);
			iRet = 1;
        }
        if( stateStocker.getState(ControlIDs.TAB_ID_MAIN) != null )
        {
        	// 画面IDから状態が取得できた
        	if( iRet == 1 )
        	{
        		// 現在のタブに応じて、ディスプレイを切り替える
        		stateStocker.getState(ControlIDs.TAB_ID_MAIN).ChangeDisplayBasedOnThisState(
        				tabStocker.getTab(ControlIDs.TAB_ID_MAIN));
        	}
        }
        if( mainTab == TabPage.TABPAGE_ID_MEDIA )
        {
        	// 選択されたタブページがメディアタブだった場合
        	// 子となるメディアタブも更新させる
           	sendUpdateMessage(ControlIDs.TAB_ID_MEDIA, 
           			tabStocker.getCurrentTabPageId(ControlIDs.TAB_ID_MEDIA)
           			,bForceRefresh);
        }
        else if( mainTab == TabPage.TABPAGE_ID_PLAY )
        {
        	// 選択されたタブページがプレイタブだった場合
        	// 子となるプレイタブも更新させる
           	sendUpdateMessage(ControlIDs.TAB_ID_PLAY, 
           			tabStocker.getCurrentTabPageId(ControlIDs.TAB_ID_PLAY)
           			,bForceRefresh);
        }    
        return iRet;
	}
	/**
	 * 現在選択されているタブページを取得する
	 * @return
	 */
	public ITabComponent getCurrentTabPage()
	{
		ITabComponent page = null;
		int iTabId = getTabStocker().getCurrentTabId();
		int iCurrentTabPageId = getCurrentTabPageId();
		if( null != getTabStocker().getTab(iTabId) )
		{
			page = getTabStocker().getTab(iTabId).getChild(iCurrentTabPageId);
		}
		return page;
	}
	/**
	 * メディアタブ内のタブページを選択する
	 * @param subTab 新しく選択したいタブページのID
	 * @param bForceRefresh 強制リフレッシュフラグ
	 * @return 0:変化なし 1:変化有り -1:エラー
	 */
	public int setMediaTabSelection( int subTab, boolean bForceRefresh )
	{
		int iRet = 0;
    	
		if( tabStocker.getTab(ControlIDs.TAB_ID_MEDIA) == null )
		{
			return -1;
		}
    	if( tabStocker.getCurrentTabPageId(ControlIDs.TAB_ID_MEDIA) != subTab
    			|| true == tabStocker.getTab(ControlIDs.TAB_ID_MEDIA).isNextForceRefresh()
    			|| bForceRefresh == true )
    	{
    		// タブページが変更されているか、このタブが次のリフレッシュだけ強制リフレッシュになるフラグがたっているか、
    		// 強制リフレッシュフラグがたっている場合
    		// 次のリフレッシュだけ強制リフレッシュになるフラグを落とす
    		tabStocker.getTab(ControlIDs.TAB_ID_MEDIA).setNextForceRefresh(false);
    		IDisplayState stateSubTmp = DisplayStateFactory.createDisplayState(subTab);        		
            if( stateSubTmp == null )
            {
            	return -1;
            }
    		Log.w("setMediaTabSelection", 
    				"currentmediatab=" + tabStocker.getCurrentTabPageId(ControlIDs.TAB_ID_MEDIA)
    				+ "next=" + subTab );		
    		IDisplayState stateMedia = stateStocker.getState(
    				ControlIDs.TAB_ID_MEDIA);
			if( stateMedia != null )
			{
				stateMedia.unregisterReceivers(IDisplayState.STATUS_ON_DESTROY);
			}	        		
			tabStocker.setCurrentTabPageId(ControlIDs.TAB_ID_MEDIA, subTab );
    		stateMedia = stateSubTmp;
			stateStocker.putState(ControlIDs.TAB_ID_MEDIA, stateMedia);
    		iRet = 1;
    		// メインタブの選択がメディアタブであれば
    		if( tabStocker.getCurrentTabPageId(ControlIDs.TAB_ID_MAIN) 
        			== TabPage.TABPAGE_ID_MEDIA )
        	{
    			// サブ画面をロードする
        		if( stateMedia != null && tabStocker.getTab(ControlIDs.TAB_ID_MEDIA) != null)
                {
                	stateMedia.ChangeDisplayBasedOnThisState(
                			tabStocker.getTab(ControlIDs.TAB_ID_MEDIA));
                	// 別のメインタブの子タブの選択をクリア
                	// tabStocker.getTab(ControlIDs.TAB_ID_PLAY).setCurrentTab( TabPage.TABPAGE_ID_NONE, true );
                }
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
	public int setPlayTabSelection( int subTab, boolean bForceRefresh )
	{
		// Log.w("setMediaTabSelection", "come tabid=" + subTab);
		if( tabStocker.getTab(ControlIDs.TAB_ID_PLAY) == null )
		{
			return -1;
		}		
		IDisplayState stateSubTmp = DisplayStateFactory.createDisplayState(subTab);        		
        if( stateSubTmp == null )
        {
        	return -1;
        }
		
		int iRet = 0;
    		
    	if( tabStocker.getCurrentTabPageId(ControlIDs.TAB_ID_PLAY) != subTab 
    			|| true == tabStocker.getTab(ControlIDs.TAB_ID_PLAY).isNextForceRefresh()
    			|| bForceRefresh == true )
    	{
    		tabStocker.getTab(ControlIDs.TAB_ID_PLAY).setNextForceRefresh(false);
			Log.w("setPlayTabSelection", "come");
    		IDisplayState statePlayTab = stateStocker.getState(ControlIDs.TAB_ID_PLAY);
    		
    		if( statePlayTab != null )
    		{
    			statePlayTab.unregisterReceivers(IDisplayState.STATUS_ON_DESTROY);
    		}
    		tabStocker.setCurrentTabPageId(ControlIDs.TAB_ID_PLAY, subTab);
    		statePlayTab = stateSubTmp;
			stateStocker.putState(ControlIDs.TAB_ID_PLAY, statePlayTab);
    		
    		iRet = 1;
    		// プレイタブであれば
        	if( tabStocker.getCurrentTabPageId(ControlIDs.TAB_ID_MAIN) == TabPage.TABPAGE_ID_PLAY )
        	{
        		// サブ画面をロードする
        		// 二段階に分けると二度画面更新が走るので無駄が多いと思われるが、とりあえずそれしか思いつかない
        		// statePlayTab = DisplayStateFactory.createDisplayState(subTab);
        		
                if( statePlayTab != null 
                && tabStocker.getTab(ControlIDs.TAB_ID_PLAY) != null)
                {
            		Log.w("statePlayTab.ChangeDisplayBasedOnThisState", "come");
            		statePlayTab.ChangeDisplayBasedOnThisState(
            				tabStocker.getTab(ControlIDs.TAB_ID_PLAY));
                	// 別のプレイタブを選択
                	// tabStocker.getTab(ControlIDs.TAB_ID_PLAY).setCurrentTab( TabPage.TABPAGE_ID_PLAY_SUB, true );
            		
                }
        	}
    	}
        return iRet;
	}
	/**
	 * タブIDを更新する
	 * @param tabId タブのID
	 * @param tabPageId タブページのID
	 * @param bForce 強制リフレッシュフラグ
	 */
	public void updateTabId( int tabId, int tabPageId, boolean bForce )
	{
		// 現在のタブを設定する
		this.tabStocker.setCurrentTabId( tabId );
		if( ControlIDs.TAB_ID_MAIN == tabId )
		{
			int id = tabPageId;
			if( TabPage.TABPAGE_ID_NONE == id 
			|| TabPage.TABPAGE_ID_UNKNOWN == id )
			{
				id = TabPage.TABPAGE_ID_PLAY;
			}	
			setMainTabSelection(
				id,
				bForce
    		);
    		Log.e("maintab select","MSG_ID_TAB_SELECT");			        		
		}
		else if( ControlIDs.TAB_ID_MEDIA == tabId )
		{
	        if( tabStocker.getCurrentTabPageId(ControlIDs.TAB_ID_MAIN) != TabPage.TABPAGE_ID_MEDIA )
	        {
	        	tabStocker.setCurrentTabPageId(ControlIDs.TAB_ID_MAIN, TabPage.TABPAGE_ID_MEDIA );
	    		IDisplayState stateMainTmp = DisplayStateFactory.createDisplayState(TabPage.TABPAGE_ID_MEDIA);
	    		stateStocker.putState(ControlIDs.TAB_ID_MAIN,stateMainTmp);
	    		stateMainTmp.ChangeDisplayBasedOnThisState(tabStocker.getTab(ControlIDs.TAB_ID_MAIN));
	        	tabStocker.getTab(tabId).setNextForceRefresh(true);
	        	
	        }
			
			int id = tabPageId;//mActivity.getCurrentDisplayId( 
//				ControlIDs.TAB_ID_MEDIA 
//			);
			if( TabPage.TABPAGE_ID_NONE == id 
			|| TabPage.TABPAGE_ID_UNKNOWN == id )
			{
				id = TabPage.TABPAGE_ID_ARTIST;
			}
			Log.e("mediatab select","MSG_ID_TAB_SELECT");
			setMediaTabSelection( id, bForce );
		}
		else if( ControlIDs.TAB_ID_PLAY == tabId )
		{			
	        if( tabStocker.getCurrentTabPageId(ControlIDs.TAB_ID_MAIN) != TabPage.TABPAGE_ID_PLAY )
	        {
	        	tabStocker.setCurrentTabPageId(ControlIDs.TAB_ID_MAIN, TabPage.TABPAGE_ID_PLAY );
	    		IDisplayState stateMainTmp = DisplayStateFactory.createDisplayState(TabPage.TABPAGE_ID_PLAY);
	    		stateStocker.putState(ControlIDs.TAB_ID_MAIN,stateMainTmp);
	    		stateMainTmp.ChangeDisplayBasedOnThisState(tabStocker.getTab(ControlIDs.TAB_ID_MAIN));
	        	tabStocker.getTab(tabId).setNextForceRefresh(true);	        	
	        }
			
			int id = tabPageId;//mActivity.getCurrentDisplayId( ControlIDs.TAB_ID_PLAY );
			// PlaySubが強制的に選択
//			if( TabPage.TABPAGE_ID_NONE == id 
//			|| TabPage.TABPAGE_ID_UNKNOWN == id )
//			{
				id = TabPage.TABPAGE_ID_PLAY_SUB;
//			}
			setPlayTabSelection( id, bForce );
		}
		
	}

	/**
	 * タブの更新をさせるメッセージを投げる.
	 * ただ、タブ選択アクションを実行するだけ
	 * @param tabID
	 * @param tabPageID
	 * @param bForce
	 */
	public void sendUpdateMessage(int tabID,int tabPageID, Boolean bForce)
	{
//    	Message msg = Message.obtain();
//    	msg.what = TabSelectAction.MSG_ID_TAB_SELECT;
//    	msg.obj = bForce;
//    	msg.arg1 = tabID;
//    	msg.arg2 = tabPageID;
//    	handler.sendMessage(msg);
		selectTab(tabID,tabPageID,bForce);
	}
	
	/**
	 * 現在の状況に合わせて、リスナを登録し直す
	 */
	void updateListeners(int status)
	{
		if( false == getResourceAccessor().isReadSDCardSuccess() )
		{
			return;
		}
		
		IDisplayState stateMain = stateStocker.getState(ControlIDs.TAB_ID_MAIN);
	
		if( stateMain == null )
		{
			return;
		}
		stateMain.unregisterReceivers(status);
		int iRet = stateMain.registerReceivers(status);
		if( iRet == 1 )
		{
			IDisplayState stateMedia = stateStocker.getState(ControlIDs.TAB_ID_MEDIA);
			
			if( stateMedia == null )
			{
				return;
			}
			stateMedia.unregisterReceivers(status);
			iRet = stateMedia.registerReceivers(status);//);
		}
		else if( iRet == 2 )
		{
			IDisplayState statePlayTab = stateStocker.getState(ControlIDs.TAB_ID_PLAY);
			
			if( statePlayTab == null )
			{
				return;
			}
			statePlayTab.unregisterReceivers(status);
			iRet = statePlayTab.registerReceivers(status);//);
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
	 * メディアライブラリのリスキャン
	 * @param tabPageID タブページのID
	 */
	public void reScanMediaOfMediaTab(int tabPageID)
	{
		Tab tabMedia = tabStocker.getTab(ControlIDs.TAB_ID_MEDIA);
		if( tabMedia == null )
		{
			return;
		}
    	TabPage page = (TabPage) tabMedia.getChild(tabPageID);
    	adpStocker.stockMediaDataFromDevice(tabPageID, page);
	}
	/**
	 * メディアの再スキャン？TODO:スキャンのロジック自体に、見直し必要
	 * @param tabID タブのID(タブページではないので注意
	 * @param bForce
	 */
	public void reScanMediaAndUpdateTabPage(int tabID, boolean bForce)
	{
		if( null == tabStocker.getTab(tabID) )
		{
			return;
		}
		boolean bNotUpdateIfNotEmpty = !bForce;
		// 現在選択中のタブによって操作を変更
		boolean bUpdateOccur = false;
		Tab tabUpd = tabStocker.getTab(tabID);
    	TabPage page = (TabPage)tabUpd.getChild(
    			tabStocker.getCurrentTabPageId(tabID));
		
		if( ControlIDs.TAB_ID_MEDIA == tabID )
		{
			// メディアタブならば
			// メディアを再度クエリ発行して更新する
	    	
			((TrackListRawAdapter)adpStocker.get(TabPage.TABPAGE_ID_SONG)).clearFilterType();
	    	bUpdateOccur = 
	    	adpStocker.stockMediaDataFromDevice( 
					tabStocker.getCurrentTabPageId(tabID), page, bNotUpdateIfNotEmpty );
		}
		else	
		//if( tabID == ControlIDs.TAB_ID_PLAY )
		if( tabID == ControlIDs.TAB_ID_MAIN )
		{
			if( tabStocker.getCurrentTabPageId(ControlIDs.TAB_ID_MAIN) == TabPage.TABPAGE_ID_NOW_PLAYLIST )
			{
				// TODO: 現在、トラックと同じカーソルになっているが、考えた方がいいかもしれない
				// NOWPLAYLIST
				// OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.setPlaylistName( Database.PlaylistName_NowPlaying );
				if( MediaPlayerUtil.isNowPlayingVideos() )
				{
					VideoListRawAdapter adp = (VideoListRawAdapter)adpStocker.get(TabPage.TABPAGE_ID_VIDEO);
					
					bUpdateOccur = 
			    	adpStocker.stockMediaDataFromDevice( 
							TabPage.TABPAGE_ID_VIDEO, page, bNotUpdateIfNotEmpty );
					if( bUpdateOccur == false )
					{
						adp.updateList();
					}

				}
				else
				{
					TrackListRawAdapter adp = (TrackListRawAdapter)adpStocker.get(TabPage.TABPAGE_ID_SONG);
					adp.setFilterType(TrackListRawAdapter.FILTER_NOW_QUEUE);
					bUpdateOccur = 
			    	adpStocker.stockMediaDataFromDevice( 
							TabPage.TABPAGE_ID_SONG, page, bNotUpdateIfNotEmpty );
					if( bUpdateOccur == false )
					{
						adp.updateList();
					}
				}
			}
		}
		if(bUpdateOccur == false )
		{
			if( page != null )
			{
				page.startUpdate();
				page.endUpdate();
			}
		}	    	
		
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		// MediaPlayer.unbindFromService(mToken);
		super.onStop();
        handler.removeMessages(AppStatus.REFRESH);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
		// マップをループして、全部の設定を保存
		Editor editor = getPreferences(MODE_PRIVATE).edit();
		// 現在選択されているタブID
		for(int i=0; i < tabStocker.getTabPageIdMap().size(); ++i ) {
			editor.putInt( String.valueOf( tabStocker.getTabPageIdMap().keyAt(i) ),
					tabStocker.getTabPageIdMap().valueAt(i) );
		}
		editor.commit();

		//if( newConfig.orientation // == ActivityInfo.CONFIG_ORIENTATION )
		//{
			// 向きの変更によってActivity終了の場合
	        // TODO: コントロールパネルは、縦横変更時にレイアウトの再調整が必要なので、ここで消してしまう？
	        NowPlayingControlPanel.deleteInstance();
	        SubControlPanel.deleteInstance();
	        PlayControlPanel.deleteInstance();
	        TimeControlPanel.deleteInstance();
	        
	        // 危険かもしれないが、ビットマップをクリアしてしまう？
	        // getResourceAccessor().clearAllBitmap();
	        
	        // タブを破棄
	        Tab tab = tabStocker.getTab(ControlIDs.TAB_ID_MAIN);
	        if( tab != null )
	        {
	        	tab.destroy();
	        }
	        
	        getTabStocker().clear();
	        
	        // アダプタを破棄？
	        // getAdpStocker().clear();
	        //System.gc();
	        
	        dispInfo.init(this, componentContainer, handler, true);
	        
		//}
		
	}
	@Override
	protected void onDestroy() {
        // 全てのレシーバの登録解除
        stateStocker.unResisterReceiverAll();
		
		//try {
			if(mToken != null)// MediaPlayerUtil.sService != null ) //&& false == MediaPlayerUtil.sService.isPlaying() )
			{
				// サービスの登録解除
			    MediaPlayerUtil.unbindFromService(mToken);
	        	// Toast.makeText(this, "service unregistered : token=" + mToken, Toast.LENGTH_LONG).show();			    
			}
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		int change = getChangingConfigurations();
		Log.d("onDestroy", "getChangingConfigurations() :" + change + " (" + String.format("0x%08x", change) + ")");  
		
//		if( change == ActivityInfo.CONFIG_ORIENTATION )
//		{
//			// 向きの変更によってActivity終了の場合
//	        // TODO: コントロールパネルは、縦横変更時にレイアウトの再調整が必要なので、ここで消してしまう？
//	        NowPlayingControlPanel.deleteInstance();
//	        SubControlPanel.deleteInstance();
//	        PlayControlPanel.deleteInstance();
//	        TimeControlPanel.deleteInstance();
//	        
//	        // 危険かもしれないが、ビットマップをクリアしてしまう？
//	        // getResourceAccessor().clearAllBitmap();
//	        
//	        getTabStocker().clear();
//	        getAdpStocker().clear();
//	        System.gc();
//		}
			  
		// 破棄時は、曲もクリア 
		MediaStopAction stopAction = new MediaStopAction();
		stopAction.doAction(null);
		super.onDestroy();
	}

	// とりあえず、必要ではない？
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	///////////////////////// サービス用のメソッド //////////////////////////////
	/**
	 * サービス接続時
	 */
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		// TODO Auto-generated method stub
		IDisplayState stateMain = stateStocker.getState(ControlIDs.TAB_ID_MAIN);
		
		if( stateMain == null )
		{
			Log.i("tabstate=null","tab state is null on the service connected.");
			return;
		}
		updateListeners(IDisplayState.STATUS_ON_CREATE);
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

	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
	        switch (requestCode) {
	            case DeleteItems.DELETE_REQUEST_CODE:
	                if (resultCode == DeleteItems.DELETE_DONE) 
	                {
	        			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMediaAndUpdateTabPage(
	        					ControlIDs.TAB_ID_MEDIA,
	        					true
	        				);	                	
	                }
	                break;
	        }
	    }



	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        super.onCreateOptionsMenu(menu);
			IDisplayState stateMain = stateStocker.getState(ControlIDs.TAB_ID_MAIN);
	        
			if( stateMain == null )
			{
				return false;
			}
			int iRet = stateMain.onCreateOptionsMenu(menu);
			if( iRet == IDisplayState.MENU_MEDIA_STATE )
			{
				IDisplayState stateMedia = stateStocker.getState(ControlIDs.TAB_ID_MEDIA);
				if( stateMedia == null )
					return false;
				try {				
					iRet = stateMedia.onCreateOptionsMenu(menu);
				} catch( OutOfMemoryError ex ) {
					Log.e("OutOfMemory","Menu Create");
					System.gc();
					iRet = stateMedia.onCreateOptionsMenu(menu);
				}

			}
			else if( iRet == IDisplayState.MENU_PLAY_STATE )
			{
				IDisplayState statePlay = stateStocker.getState(ControlIDs.TAB_ID_PLAY);
				if( statePlay == null )
					return false;
				
				try {
					iRet = statePlay.onCreateOptionsMenu(menu);
				} catch( OutOfMemoryError ex ) {
					Log.e("OutOfMemory","Menu Create");
					System.gc();
					iRet = statePlay.onCreateOptionsMenu(menu);
				}
				
			}
	        return true;
	    }

	    @Override
	    public boolean onPrepareOptionsMenu(Menu menu) {
//	        MusicUtils.setPartyShuffleMenuIcon(menu);
	        boolean b = super.onPrepareOptionsMenu(menu);
			IDisplayState stateMain = stateStocker.getState(ControlIDs.TAB_ID_MAIN);
	        
			if( stateMain == null )
			{
				return false;
			}
			int iRet = stateMain.onPrepareOptionsMenu(menu);
			if( iRet == IDisplayState.MENU_MEDIA_STATE )
			{
				IDisplayState stateMedia = stateStocker.getState(ControlIDs.TAB_ID_MEDIA);
				
				if( stateMedia == null )
				{
					return false;
				}
				iRet = stateMedia.onPrepareOptionsMenu(menu);
			}
			else if( iRet == IDisplayState.MENU_PLAY_STATE )
			{
				IDisplayState statePlay = stateStocker.getState(ControlIDs.TAB_ID_PLAY);
				if( statePlay == null )
					return false;
				
				iRet = statePlay.onPrepareOptionsMenu(menu);
			}
			
	        return b;
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	boolean b = super.onOptionsItemSelected(item);
			IDisplayState stateMain = stateStocker.getState(ControlIDs.TAB_ID_MAIN);
	    	
			if( stateMain == null )
			{
				return false;
			}
			int iRet = stateMain.onOptionsItemSelected(item);
			if( iRet == IDisplayState.MENU_MEDIA_STATE )
			{
				IDisplayState stateMedia = stateStocker.getState(ControlIDs.TAB_ID_MEDIA);
				
				if( stateMedia == null )
				{
					return false;
				}
				iRet = stateMedia.onOptionsItemSelected(item);
			}
			else if( iRet == IDisplayState.MENU_PLAY_STATE )
			{
				IDisplayState statePlay = stateStocker.getState(ControlIDs.TAB_ID_PLAY);
				if( statePlay == null )
					return false;
				
				iRet = statePlay.onOptionsItemSelected(item);
			}
			
			return b;
			
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
	    		Image timeBtns[] = TimeControlPanel.getInstance().getTimesButton();
		        for( int i=0; i<timeBtns.length; i++ )
		        {
		        	if( null != timeBtns[i].getView() )
		        	{
		        		((ImageImpl)timeBtns[i].getView()).setVisibility(bShowImgFlg[i] ? View.VISIBLE : View.INVISIBLE );
		        	}
		        }
	        }
	    }
	    public void updateTimeDisplay(long secs)
	    {
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
	    		Image timeBtns[] = TimeControlPanel.getInstance().getTimesButton();
		        for( int i=0; i<timeBtns.length; i++ )
		        {
		        	if( null != timeBtns[i].getView() )
		        	{
		        		((ImageImpl)timeBtns[i].getView()).setImageBitmap( getResourceAccessor().createBitmapFromDrawableId(
		        				timeImgResIds[ timeArgs[i] ]) );
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
		static public void removeFromParent( View v )
		{
			if( v.getParent() != null )
			{
				ViewParent p = v.getParent();
				if( p instanceof ViewGroup )
				{
					((ViewGroup) p).removeView(v);
				}
			}			
		}
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if(keyCode != KeyEvent.KEYCODE_BACK){
				return super.onKeyDown(keyCode, event);
			}else{
				// 検索パネルが出ている時は、Backキーで検索パネルのClose
				TabPage page = (TabPage)getCurrentTabPage();
				if( null != page )//act.getTabStocker().getTab(iTabId) )
				{
					if( SearchPanel.getInstance().getView() != null )
					{
						if(SearchPanel.getInstance().getView().getParent() != null 
						&& page.getTabBaseLayout() == SearchPanel.getInstance().getView().getParent()	
						)
						{
							removeFromParent(SearchPanel.getInstance().getView());
							return false;
						}
					}
				}
				return super.onKeyDown(keyCode, event);
			}
		}
}