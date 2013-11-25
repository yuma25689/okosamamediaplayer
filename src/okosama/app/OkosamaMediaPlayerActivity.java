package okosama.app;

//import okosama.app.action.HideTabComponentAction;
//import okosama.app.action.ShowTabComponentAction;
import okosama.app.action.TabSelectAction;
import okosama.app.adapter.AlbumListRawAdapter;
import okosama.app.adapter.AdapterStocker;
//import okosama.app.adapter.ArtistAlbumListAdapter;
import okosama.app.adapter.ArtistAlbumListRawAdapter;
import okosama.app.adapter.IAdapterUpdate;
import okosama.app.adapter.PlaylistListRawAdapter;
import okosama.app.adapter.TrackListRawAdapter;
import okosama.app.factory.DroidWidgetKit;
import android.app.Activity;
import android.content.ComponentName;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
//import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences.Editor;
// import android.database.Cursor;
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
import okosama.app.state.StateStocker;
// import okosama.app.state.absDisplayState;
import okosama.app.storage.Database;
import okosama.app.tab.*;
import okosama.app.widget.Button;
import okosama.app.widget.ExpList;
import okosama.app.widget.List;
import okosama.app.widget.absWidget;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import okosama.app.widget.ButtonImpl;

public class OkosamaMediaPlayerActivity extends Activity
implements ServiceConnection, Database.Defs {
	public static final String MEDIA_SERVICE_NOTIFY = "MediaServiceNotify";
	
	// �^�u�i�[�p
	TabStocker tabStocker = new TabStocker();
	public TabStocker getTabStocker()
	{
		return tabStocker;
	}
	// ��Ԋi�[�p
	StateStocker stateStocker = new StateStocker();
	public StateStocker getStateStocker()
	{
		return stateStocker;
	}
	
	// �������t���b�V���t���O�H
//	public boolean bForceRefresh = false;
//	public void setForceRefreshFlag(boolean bForceRefresh)
//	{
//		this.bForceRefresh = bForceRefresh;
//	}
    // �|�[�Y���H
    private boolean paused = false;
    public boolean isPaused()
    {
    	return paused;
    }
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
	// �T�[�r�X�̃g�[�N��
    private ServiceToken mToken;
    
    // �y�Ȃ̌����ɁAExternal�𗘗p
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
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMediaAndUpdateTabPage(ControlIDs.ID_NOT_SPECIFIED, true);
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
    
    
    // Adapter�i�[�p�}�b�v
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

	// private TempBackupData backupData = new TempBackupData();
	/**
     * �ʂ�����map���ƕۑ����Ă��܂������̂��E�E�E�BTODO �v�m�F
     */
//    @Override
//    public Object onRetainNonConfigurationInstance() {
//    	Log.e("onRetainNonConfigrationInstance","come");
//    	backupData.setAlbumAdp(getAlbumAdp());
//    	backupData.setArtistAdp(getArtistAdp());
//    	backupData.setTracklistAdp(getTrackAdp());
//    	backupData.setPlaylistAdp(getPlaylistAdp());
//        return backupData;
//    }
    // TrackAdapter�p�H
    // editmode���ǂ����B
    // preference�ۑ��Ώ�
    private boolean editMode = false;
	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}
	// �^�u�̏��������I��������ǂ���
	// onCreate��onResume�ł̏����̃_�u����������ړI
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
	
	// ���������ɁA�X�N���[���T�C�Y�擾�ɃX���b�h���K�v�ɂȂ邽�߁A�X���b�h�Ƃ̓������K�v�ɁE�E�E
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
		tabStocker.clearCurrentTabId();
	}
	public void reloadDisplayIdMap()
	{
		SharedPreferences pref = this.getPreferences(MODE_PRIVATE);
		setCurrentDisplayId( ControlIDs.TAB_ID_MAIN, 
				pref.getInt( String.valueOf( ControlIDs.TAB_ID_MAIN ), TabPage.TABPAGE_ID_MEDIA) );
		setCurrentDisplayId( ControlIDs.TAB_ID_MEDIA, 
				pref.getInt( String.valueOf( ControlIDs.TAB_ID_MEDIA ), TabPage.TABPAGE_ID_ARTIST ) );
		setCurrentDisplayId( ControlIDs.TAB_ID_PLAY, 
				pref.getInt( String.valueOf( ControlIDs.TAB_ID_PLAY ), TabPage.TABPAGE_ID_PLAY_SUB ) );
		
	}
	/**
	 * ���݂̉��ID��ݒ肷��
	 * ���̂Ƃ���A���ID�Ƃ����̂́A�^�uID�ɓ�����
	 * �܂��A���̒l�́A�A�N�e�B�r�e�B�ɂ��邪�A�A�v���P�[�V�����S�̂ŗ��p���銴���̂��̂ł���B
	 * �Ƃ肠����static�ɂ��Ă������A�N���X���ړ����Ă�������������Ȃ�
	 * @param internalID
	 * @param iDispId
	 */
	public void setCurrentDisplayId( int internalID, int iDispId )
	{
		tabStocker.setCurrentTabId(internalID, iDispId);
		// tabCurrentDisplayIdMap.put( internalID, iDispId );
	}
	public int getCurrentDisplayId( int internalID )
	{
		// tabStocker.setCurrentTabId(internalID, iDispId);
		tabStocker.getTabIdMap().get(internalID);
		
		if( 0 <= tabStocker.getTabIdMap().indexOfKey( internalID ))
		{
			return tabStocker.getCurrentTabId( internalID );
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
        // �^�C�g���o�[���\���ɁH
        requestWindowFeature(Window.FEATURE_NO_TITLE);
 
        // �r���[�̐ݒ�
        setContentView(R.layout.main);
 
        // Database�N���X�ɃA�N�e�B�r�e�B�i�[
        Database.setActivity( this );
        // handler�N���X�쐬
        handler = new MainHandler( this );
        // �{�����[�������y�p�ɐݒ肷��
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
        // Debug�o��
//        String log;
//        log = String.valueOf(MediaPlayer.getServiceConnectionCount());
//        Toast.makeText(this, log, Toast.LENGTH_LONG).show();
//       
        
        // ���\�[�X�̏���ݒ肷��(�����Őݒ��A��x�Ɛݒ肵�����Ȃ��̂̓��o���C������
        ResourceAccessor.CreateInstance(this);
        res = ResourceAccessor.getInstance();
        // DroidWidgetKit�̐ݒ�
        DroidWidgetKit.getInstance().setActivity(this);
        // ���C�A�E�g�̎擾
        pageContainer = (LinearLayout)findViewById(R.id.main_linearlayout);
        componentContainer = (RelativeLayout)findViewById(R.id.main_relativelayout);
        // �^�u�̕\���؂�ւ��p�̐ݒ�
        //HideTabComponentAction.getInstance().setTabLayout(componentContainer);
        //ShowTabComponentAction.getInstance().setTabLayout(componentContainer);

        // ���ԕ\���̏�����
		updateTimeDisplayVisible(0);
		updateTimeDisplay(0);
        
        // �T�[�r�X�ւ̐ڑ����J�n
        if( 0 == MediaPlayerUtil.getServiceConnectionCount() )
        {
        	mToken = MediaPlayerUtil.bindToService(this, this);
        }      
//        if (savedInstanceState != null) {
//        	// �擾�ł��Ȃ����-1��ԋp����(=TABPAGE_ID_UNKNOWN)
//        	currentMainTabId = savedInstanceState.getInt(tabNameMain + dispIdKey);
//        	currentSubTabId = savedInstanceState.getInt(tabNameMedia + dispIdKey);
//        }
        // �N���A���Ȃ��ƁA���f�B�A�^�u�̓��e�ƃv���C�^�u�̓��e���d�Ȃ��Ă��܂��E�E�E�Ȃ����낤�H
        // setTabSelection( TabPage.TABPAGE_ID_MEDIA, TabPage.TABPAGE_ID_NONE );
        // setTabSelection( TabPage.TABPAGE_ID_NONE, TabPage.TABPAGE_ID_NONE );
        // �����œ��ꂽ��ʖ�
//		SharedPreferences pref = getPreferences(MODE_PRIVATE);
//    	currentMainTabId = pref.getInt(tabNameMain + dispIdKey, TabPage.TABPAGE_ID_UNKNOWN);
//    	currentSubTabId = pref.getInt(tabNameMedia + dispIdKey, TabPage.TABPAGE_ID_UNKNOWN);

        // �T�C�Y���擾�ł�����A���L�̏������s�����悤�ɂ���
    }
    /**
     * �x�[�X�摜��ł̐�΍��W���w�肵���ʒu��\��LayoutParam���쐬����
     * ���A������FILL_PARENT
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
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
        lp.topMargin = yCorrect;
        lp.leftMargin = xCorrect;
        // ���̃A�v���P�[�V�����ł́Abottom��right��margin�̓[�������E�E�E�B
        lp.bottomMargin = 0;
        lp.rightMargin = 0;
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        
        return lp;
	}
    
    /**
     * �x�[�X�摜��ł̐�΍��W���w�肵���ʒu��\��LayoutParam���쐬����
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
		int xCorrect = dispInfo.getCorrectionXConsiderDensity(left);
		int yCorrect = 0;
		int topRule = RelativeLayout.ALIGN_PARENT_TOP;
		if( yCorrect < 0 )
		{
			yCorrect = -1 * dispInfo.getCorrectionYConsiderDensity(top);
			topRule = RelativeLayout.ALIGN_PARENT_BOTTOM;
		}
		else
		{
			yCorrect = dispInfo.getCorrectionYConsiderDensity(top);
		}
		
		RelativeLayout.LayoutParams lp = 
				new RelativeLayout.LayoutParams(
						widthCorrect, heightCorrect);
		
        lp.topMargin = yCorrect;
        lp.leftMargin = xCorrect;
        // ���̃A�v���P�[�V�����ł́Abottom��right��margin�̓[�������E�E�E�B
        lp.bottomMargin = 0;
        lp.rightMargin = 0;
        lp.addRule(topRule);//RelativeLayout.ALIGN_PARENT_TOP);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        
        return lp;
	}
	RelativeLayout.LayoutParams createLayoutParamForAbsolutePos(
			int width, int height, int left, int top )
	{
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
        		(width), (height));
        lp.topMargin = ( top );
        lp.leftMargin = ( left );
        lp.bottomMargin = 0;
        lp.rightMargin = 0;
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        
        return lp;
	}
	public static final String ALBUM_KEY = "Album";
	public static final String ARTIST_GROUP_KEY = "ArtistGroup";
	public static final String ARTIST_CHILD_KEY = "ArtistChild";
	public static final String TRACK_KEY = "Track";
	public static final String PLAYLIST_KEY = "Playlist";
	
	@Override
    public void onSaveInstanceState(Bundle outcicle) {
		// TODO:�}�b�v�����[�v���āA�S���̐ݒ��ۑ�
        super.onSaveInstanceState(outcicle);
    }
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
	IntentFilter intentFilter;
	BroadcastReceiver receiver;
	class MediaServiceNotifyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) 
		{
			// �\���̍X�V
			updatePlayStateButtonImage();
		}
		
	}
	@Override
	protected void onResume() {
		Log.e("onResume","resume!");
    	// ��ʂ̃T�C�Y���̏����X�V����
        dispInfo.init(this, componentContainer, handler);
        
        //bForceRefresh = true;
        paused = false;
        receiver = new MediaServiceNotifyReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(MEDIA_SERVICE_NOTIFY);
        registerReceiver(receiver,intentFilter);
        
		super.onResume();
	}

	@Override
	protected void onPause() {
		// �}�b�v�����[�v���āA�S���̐ݒ��ۑ�
		//tabCurrentDisplayIdMap.
        //outcicle.putInt("displayid", iCurrentDisplayId);
		Editor editor = getPreferences(MODE_PRIVATE).edit();
		//for(Entry<Integer, Integer> e : tabCurrentDisplayIdMap.entrySet()) {
		for(int i=0; i < tabStocker.getTabIdMap().size(); ++i ) {
			editor.putInt( String.valueOf( tabStocker.getTabIdMap().keyAt(i) ),
					tabStocker.getTabIdMap().valueAt(i) );
		}
		editor.commit();
		
		paused = true;
		
		// componentContainer.removeAllViews();
		// bInitEnd = false;
		// bForceRefresh = true;
        getResourceAccessor().releaseSound();
        stateStocker.unResisterReceiverAll();
        
        if( null != receiver )
        {
        	this.unregisterReceiver(receiver);
        	receiver = null;
        }
        
        getResourceAccessor().rereaseMotionSenser();

        // bTabInitEnd = false;
		// System.gc();
        
		super.onPause();
	}
	
	/**
	 * �T�u�^�u�́A�󋵂ɂ���ĕω�����̂Œ���
	 * �܂����̊֐��͖������i���́A�T�u�^�u�̑I�𕔕�)
	 * @param mainTab	 
	 * @param bSndChgMsg �ω����������Ƃ��A���b�Z�[�W�𑗐M���邩
	 * @return 0:�ω��Ȃ� 1:�ω��L�� -1:�G���[
	 */
	public int setMainTabSelection( int mainTab, boolean bForceRefresh )
	{	
		IDisplayState stateMainTmp = DisplayStateFactory.createDisplayState(mainTab);
		if( stateMainTmp == null )
		{
			return -1;
		}	
		int iRet = 0;
		//int iRet2 = 0;
		if( tabStocker.getCurrentTabId(ControlIDs.TAB_ID_MAIN) != mainTab
				|| bForceRefresh == true )
        {
			Log.w("setMainTabSelection", "come");
			if( stateStocker.getState(ControlIDs.TAB_ID_MAIN) != null )
			{
				stateStocker.getState(ControlIDs.TAB_ID_MAIN).unregisterReceivers(IDisplayState.STATUS_ON_PAUSE);
			}
			tabStocker.setCurrentTabId(ControlIDs.TAB_ID_MAIN, mainTab );
			stateStocker.putState(ControlIDs.TAB_ID_MAIN, stateMainTmp);
			iRet = 1;
        }
        if( stateStocker.getState(ControlIDs.TAB_ID_MAIN) != null )
        {
        	// ���ID�����Ԃ��擾�ł���
        	if( iRet == 1 )
        	{
        		//iRet2 = 
        		stateStocker.getState(ControlIDs.TAB_ID_MAIN).ChangeDisplayBasedOnThisState(
        				tabStocker.getTab(ControlIDs.TAB_ID_MAIN));
        	}
        }
        // 2013/11/05 add
        if( mainTab == TabPage.TABPAGE_ID_MEDIA )
        {
           	sendUpdateMessage(ControlIDs.TAB_ID_MEDIA, 
           			tabStocker.getCurrentTabId(ControlIDs.TAB_ID_MEDIA)
           			,bForceRefresh);
        }
        if( mainTab == TabPage.TABPAGE_ID_PLAY )
        {
           	sendUpdateMessage(ControlIDs.TAB_ID_PLAY, 
           			tabStocker.getCurrentTabId(ControlIDs.TAB_ID_PLAY)
           			,bForceRefresh);
        }    
        return iRet;
	}
	/**
	 * �T�u�^�u�́A�󋵂ɂ���ĕω�����̂Œ���
	 * �܂����̊֐��͖������i���́A�T�u�^�u�̑I�𕔕�)
	 * @param subTab
	 * @param bSndChgMsg �ω����������Ƃ��A���b�Z�[�W�𑗐M���邩
	 * @return 0:�ω��Ȃ� 1:�ω��L�� -1:�G���[
	 */
	public int setMediaTabSelection( int subTab, boolean bForceRefresh )
	{
		int iRet = 0;
    	
		if( tabStocker.getTab(ControlIDs.TAB_ID_MEDIA) == null )
		{
			return -1;
		}
    	if( tabStocker.getCurrentTabId(ControlIDs.TAB_ID_MEDIA) != subTab
    			|| true == tabStocker.getTab(ControlIDs.TAB_ID_MEDIA).isNextForceRefresh()
    			|| bForceRefresh == true )
    	{
    		tabStocker.getTab(ControlIDs.TAB_ID_MEDIA).setNextForceRefresh(false);
    		IDisplayState stateSubTmp = DisplayStateFactory.createDisplayState(subTab);        		
            if( stateSubTmp == null )
            {
            	return -1;
            }
    		Log.w("setMediaTabSelection", 
    				"currentmediatab=" + tabStocker.getCurrentTabId(ControlIDs.TAB_ID_MEDIA)
    				+ "next=" + subTab );		
    		IDisplayState stateMedia = stateStocker.getState(
    				ControlIDs.TAB_ID_MEDIA);
			if( stateMedia != null )
			{
				stateMedia.unregisterReceivers(IDisplayState.STATUS_ON_DESTROY);
			}	        		
			tabStocker.setCurrentTabId(ControlIDs.TAB_ID_MEDIA, subTab );
    		stateMedia = stateSubTmp;
			stateStocker.putState(ControlIDs.TAB_ID_MEDIA, stateMedia);
    		iRet = 1;
    		// ���f�B�A�^�u�ł����
    		if( tabStocker.getCurrentTabId(ControlIDs.TAB_ID_MAIN) 
        			== TabPage.TABPAGE_ID_MEDIA )
        	{
    			// �T�u��ʂ����[�h����
        		if( stateMedia != null && tabStocker.getTab(ControlIDs.TAB_ID_MEDIA) != null)
                {
                	stateMedia.ChangeDisplayBasedOnThisState(
                			tabStocker.getTab(ControlIDs.TAB_ID_MEDIA));
                }
        	}
    	}
        return iRet;
	}
	/**
	 * �T�u�^�u�́A�󋵂ɂ���ĕω�����̂Œ���
	 * �܂����̊֐��͖������i���́A�T�u�^�u�̑I�𕔕�)
	 * @param subTab
	 * @param bSndChgMsg �ω����������Ƃ��A���b�Z�[�W�𑗐M���邩
	 * @return 0:�ω��Ȃ� 1:�ω��L�� -1:�G���[
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
    		
    	if( tabStocker.getCurrentTabId(ControlIDs.TAB_ID_PLAY) != subTab 
    			|| true == tabStocker.getTab(ControlIDs.TAB_ID_PLAY).isNextForceRefresh()
    			|| bForceRefresh == true )
    	{
    		tabStocker.getTab(ControlIDs.TAB_ID_PLAY).setNextForceRefresh(false);
			Log.w("setMediaTabSelection", "come");
    		IDisplayState statePlayTab = stateStocker.getState(ControlIDs.TAB_ID_PLAY);
    		
    		if( statePlayTab != null )
    		{
    			statePlayTab.unregisterReceivers(IDisplayState.STATUS_ON_DESTROY);
    		}
    		tabStocker.setCurrentTabId(ControlIDs.TAB_ID_PLAY, subTab);
    		statePlayTab = stateSubTmp;
			stateStocker.putState(ControlIDs.TAB_ID_PLAY, statePlayTab);
    		
    		iRet = 1;
    		// �v���C�^�u�ł����
        	if( tabStocker.getCurrentTabId(ControlIDs.TAB_ID_MAIN) == TabPage.TABPAGE_ID_PLAY )
        	{
        		// �T�u��ʂ����[�h����
        		// ��i�K�ɕ�����Ɠ�x��ʍX�V������̂Ŗ��ʂ������Ǝv���邪�A�Ƃ肠�������ꂵ���v�����Ȃ�
        		// statePlayTab = DisplayStateFactory.createDisplayState(subTab);
        		
                if( statePlayTab != null 
                && tabStocker.getTab(ControlIDs.TAB_ID_PLAY) != null)
                {
            		Log.w("statePlayTab.ChangeDisplayBasedOnThisState", "come");
            		statePlayTab.ChangeDisplayBasedOnThisState(
            				tabStocker.getTab(ControlIDs.TAB_ID_PLAY));
                }
        	}
    	}
        return iRet;
	}
	void updateTabId( int tabId, int tabPageId, boolean bForce )
	{
		if( ControlIDs.TAB_ID_MAIN == tabId )
		{
    		// Activity�̃^�uid���X�V
			int id = tabPageId;
			if( TabPage.TABPAGE_ID_NONE == id 
			|| TabPage.TABPAGE_ID_UNKNOWN == id )
			{
				id = TabPage.TABPAGE_ID_PLAY;
			}	
			setMainTabSelection(
				id,
				bForce
    			// mActivity.getCurrentDisplayId( ControlIDs.TAB_ID_MAIN )
    		);
    		Log.e("maintab select","MSG_ID_TAB_SELECT");			        		
		}
		else if( ControlIDs.TAB_ID_MEDIA == tabId )
		{
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
			int id = tabPageId;//mActivity.getCurrentDisplayId( ControlIDs.TAB_ID_PLAY );
			if( TabPage.TABPAGE_ID_NONE == id 
			|| TabPage.TABPAGE_ID_UNKNOWN == id )
			{
				id = TabPage.TABPAGE_ID_PLAY_SUB;
			}
			setPlayTabSelection( id, bForce );
		}
		
	}
	
	void sendUpdateMessage(int tabID,int tabPageID, Boolean bForce)
	{
    	Message msg = Message.obtain();
    	msg.what = TabSelectAction.MSG_ID_TAB_SELECT;
    	msg.obj = bForce;
    	msg.arg1 = tabID;
    	msg.arg2 = tabPageID;
    	handler.sendMessage(msg);
	}
	void updateCommonCtrls()
	{
		// ���ʂŗ��p����{�^�����őO�ʂɎ����Ă���
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
		
		// ���ԕ\���̍ĕ`��?
		queueNextRefresh(1);
	}
	
	/**
	 * ���݂̏󋵂ɍ��킹�āA���X�i��o�^������
	 */
	void updateListeners(int status)
	{
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
	 * adapter�̏������I������Ƃ��ɁAadapter����R�[���o�b�N���銴���̊֐�
	 */
//	public void initAdapter(int id,Cursor cursor)
//	{
//		initAdapter(id,cursor,false);
//	}
	// 2013/11/03 del ->
//	public void initAdapter(int id,Cursor cursor, boolean isLimited)
//	{
//		if( cursor == null )
//		{
//			Log.w("Warning","cursor is null");
//			// null�̂܂܂�cursor�ݒ肳��Ă����ɖ��͂Ȃ�
//			// return;
//		}
//		
//		switch( id )
//		{
//		// TODO:null�̏ꍇ�A�\������r���[��ύX��������������������Ȃ�
//		case TabPage.TABPAGE_ID_ALBUM:
//			// List�ɃJ�[�\����ݒ�
//			getAlbumAdp().insertAllDataFromCursor(cursor);//changeCursor(cursor);
//			break;
//		case TabPage.TABPAGE_ID_ARTIST:
//			// List�ɃJ�[�\����ݒ�
//			getArtistAdp().insertAllDataFromCursor(cursor);
//			break;
//		case TabPage.TABPAGE_ID_SONG:
//			// List�ɃJ�[�\����ݒ�
//			getTrackAdp().insertAllDataFromCursor(cursor);
//			break;
//		case TabPage.TABPAGE_ID_PLAYLIST:
//			Cursor c = null;
//			c = Database.getInstance(isExternalRef()).mergedCursor(cursor, false);//createShortCut);
//			// List�ɃJ�[�\����ݒ�
//			getPlaylistAdp().changeCursor(c);
//			break;
//		}
//	}
	// 2013/11/03 del <-

	
	/**
	 * �������X�L����
	 * @param tabID
	 */
	public void reScanMediaOfMediaTab(int tabID)
	{
		Tab tabMedia = tabStocker.getTab(ControlIDs.TAB_ID_MEDIA);
		if( tabMedia == null )
		{
			return;
		}
    	TabPage page = (TabPage) tabMedia.getChild(tabID);
    	adpStocker.stockMediaDataFromDevice(tabID, page);
	}
	/**
	 * ���f�B�A�̍ăX�L�����HTODO:�X�L�����̃��W�b�N���̂ɁA�������K�v
	 * @param tabID �^�u��ID(�^�u�y�[�W�ł͂Ȃ��̂Œ���
	 * @param bForce
	 */
	public void reScanMediaAndUpdateTabPage(int tabID, boolean bForce)
	{
		boolean bNotUpdateIfNotEmpty = !bForce;
		// ���ݑI�𒆂̃^�u�ɂ���đ����ύX
		boolean bUpdateOccur = false;
		Tab tabUpd = tabStocker.getTab(tabID);
    	TabPage page = (TabPage)tabUpd.getChild(
    			tabStocker.getCurrentTabId(tabID));
		
		if( ControlIDs.TAB_ID_MEDIA == tabID )
		{
			// ���f�B�A�^�u�Ȃ��
			// ���f�B�A���ēx�N�G�����s���čX�V����
	    	
			((TrackListRawAdapter)adpStocker.get(TabPage.TABPAGE_ID_SONG)).clearFilterType();
	    	bUpdateOccur = 
	    	adpStocker.stockMediaDataFromDevice( 
					tabStocker.getCurrentTabId(tabID), page, bNotUpdateIfNotEmpty );
		}
		else	
		if( tabID == ControlIDs.TAB_ID_PLAY )
		{
			if( tabStocker.getCurrentTabId(ControlIDs.TAB_ID_PLAY) == TabPage.TABPAGE_ID_NOW_PLAYLIST )
			{
				// TODO: ���݁A�g���b�N�Ɠ����J�[�\���ɂȂ��Ă��邪�A�l��������������������Ȃ�
				// NOWPLAYLIST
				// OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.setPlaylistName( Database.PlaylistName_NowPlaying );
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
	protected void onDestroy() {
		try {
			if( MediaPlayerUtil.sService != null && false == MediaPlayerUtil.sService.isPlaying() )
			{
				// �T�[�r�X�̓o�^����
			    MediaPlayerUtil.unbindFromService(mToken);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onDestroy();
	}

	// �Ƃ肠�����A�K�v�ł͂Ȃ��H
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	///////////////////////// �T�[�r�X�p�̃��\�b�h //////////////////////////////
	/**
	 * �T�[�r�X�ڑ���
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
	 * �T�[�r�X�ؒf��
	 */
	@Override
	public void onServiceDisconnected(ComponentName name) {
       // Toast.makeText(this, "onServiceDisconnected:" + name, Toast.LENGTH_LONG).show();
		
		// �悭�킩��Ȃ����ǁA�T�[�r�X�ؒf���ꂽ��I������H
		Log.e("service disconnect","finish because service disconnect.");
		finish();
	}

	
	////////////////// event ////////////////////////////
	View lastEventView = null;
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfoIn) {
		// �ꉞ�A���̂Ƃ���^�O�Ƀ��X�g�������Ă���͂��Ȃ̂ŁA����ŕ��򂷂�H
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
			// �ꉞ�A���̂Ƃ���^�O�Ƀ��X�g�������Ă���͂��Ȃ̂ŁA����ŕ��򂷂�H
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
				
				iRet = stateMedia.onCreateOptionsMenu(menu);
			}
			else if( iRet == IDisplayState.MENU_PLAY_STATE )
			{
				IDisplayState statePlay = stateStocker.getState(ControlIDs.TAB_ID_PLAY);
				if( statePlay == null )
					return false;
				
				iRet = statePlay.onCreateOptionsMenu(menu);
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
//	        return super.onOptionsItemSelected(item);
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
//	    @Override
//	    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//	    	Log.w("��on activity result","come");
//	        switch (requestCode) {
////	            case SCAN_DONE:
////	                if (resultCode == RESULT_CANCELED) {
////	                    finish();
////	                } else {
////	                    getAlbumCursor(mAdapter.getQueryHandler(), null);
////	                }
////	                break;
//
//	            case NEW_PLAYLIST:
//                	Log.w("��new playlist","come");
//	                if (resultCode == RESULT_OK) {
//                    	Log.w("��new playlist","result ok");
//	                    Uri uri = intent.getData();
//	                    if (uri != null) {
//	                    	Log.d("��new playlist","tabid=" + getCurrentSubTabId());
//	                    	IBehavior behavior = getList(getCurrentSubTabId()).getBehavior();
//	                        long [] list = behavior.getCurrentSongList(); //Database.getSongListForAlbum(this, Long.parseLong(mCurrentAlbumId));
//	                        Database.addToPlaylist(this, list, Long.parseLong(uri.getLastPathSegment()));
//	                    }
//	                }
//	                break;
//	        }
//	    }
	    
}