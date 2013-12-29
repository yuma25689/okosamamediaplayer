package okosama.app;

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
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import okosama.app.panel.NowPlayingControlPanel;
import okosama.app.panel.PlayControlPanel;
import okosama.app.panel.SubControlPanel;
import okosama.app.panel.TimeControlPanel;
import okosama.app.service.MediaInfo;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.service.MediaPlayerUtil.ServiceToken;
import okosama.app.state.DisplayStateFactory;
import okosama.app.state.IDisplayState;
import okosama.app.state.StateStocker;
import okosama.app.storage.Database;
import okosama.app.storage.GenreStocker;
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
import android.view.SurfaceHolder;
import android.view.SurfaceView;
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

	public void selectTab(int tabId, int tabPageId, boolean bForce)
	{
		// �^�uID���X�V
		updateTabId( tabId, tabPageId, bForce );
	
		// ���X�i���X�V
		updateListeners(IDisplayState.STATUS_ON_CREATE);
		updateListeners(IDisplayState.STATUS_ON_RESUME);
		// ���f�B�A���X�V
		reScanMediaAndUpdateTabPage(tabId,false);
		// ���ʕ����ĕ`��
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
	// �W�������i�[�p
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
        	// �I�����ꂽ�^�u�y�[�W�����f�B�A�^�u�������ꍇ
        	// �q�ƂȂ郁�f�B�A�^�u���X�V������
        	tabPageId = tabStocker.getCurrentTabPageId(ControlIDs.TAB_ID_MEDIA);
        }
        else if( mainTab == TabPage.TABPAGE_ID_PLAY )
        {
        	// �I�����ꂽ�^�u�y�[�W���v���C�^�u�������ꍇ
        	// �q�ƂȂ�v���C�^�u���X�V������
        	tabPageId = tabStocker.getCurrentTabPageId(ControlIDs.TAB_ID_PLAY);
        }    
        else
        {
        	// ����ȊO�̏ꍇ�AMain�^�u��ID
        	tabPageId = mainTab;
        }    
        return tabPageId;
	}
	
    // �|�[�Y���H
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
				// �A�v���P�[�V�������ċN��
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
				&& MediaPlayerUtil.sService.getQueue() != null
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
	// �T�[�r�X�̃g�[�N��
	// TODO:Trying to unbind with null token�Ƃ����G���[������
    private static ServiceToken mToken;
    
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

		OkosamaMediaPlayerActivity.getResourceAccessor().setReadSDCardSuccess(OkosamaMediaPlayerActivity.getResourceAccessor().isSdCanRead());
        
        // ���ԕ\���̏�����
		updateTimeDisplayVisible(0);
		updateTimeDisplay(0);
        
        // �T�[�r�X�ւ̐ڑ����J�n
        if( 0 == MediaPlayerUtil.getServiceConnectionCount() )
        {
        	// ��΂ɂP�����ڑ�����Ȃ��悤�ɂ���
        	mToken = MediaPlayerUtil.bindToService(this, this);
        	// Toast.makeText(this, "service registered : token=" + mToken, Toast.LENGTH_LONG).show();
        }
//        else
//        {
//        	// ���ɃR�l�N�V����������ꍇ
//        	// �R�l�N�V������id�𒲂ׁA���݂�activity�ƈႤ�ꍇ�́A�ڑ��������H
//        	if( MediaPlayerUtil.hasServiceConnection(this) == false )
//        	{
//        		// �R�l�N�V�����͂��邪�A���̃A�N�e�B�r�e�B�̃R�l�N�V�����͂Ȃ�
//        		
//        	}
//        }

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
		// �w�肳�ꂽ���ʒu�ɑ΂��āA�f�B�X�v���C�T�C�Y���l�������������s��
		int xCorrect = dispInfo.getCorrectionXConsiderDensity(left);
		int yCorrect = dispInfo.getCorrectionYConsiderDensity(top);
		
		// ���ƍ����̎w�肪�Ȃ��̂ŁA�e�𖄂߂�悤�ɐݒ肷��
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
	
	// ��ʊJ�X���̒l�̃o�b�N�A�b�v�ƕ���
	// ���̂Ƃ���A�s�v
//	@Override
//    public void onSaveInstanceState(Bundle outcicle) {
//		// TODO:�}�b�v�����[�v���āA�S���̐ݒ��ۑ�
//        super.onSaveInstanceState(outcicle);
//    }	
//	@Override
//	protected void onRestoreInstanceState(Bundle savedInstanceState) {
//		super.onRestoreInstanceState(savedInstanceState);
//	}
	IntentFilter intentFilter;
	BroadcastReceiver receiver;
	
	/**
	 * ���f�B�A�T�[�r�X�����intent�̃��V�[�o
	 * @author 25689
	 *
	 */
	class MediaServiceNotifyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) 
		{
			// ���f�B�A�T�[�r�X����intent���󂯎������
			// �Đ��{�^���̕\���X�V
			updatePlayStateButtonImage();
		}
	}
	@Override
	protected void onResume() {
		Log.w("onResume","resume!");
    	// ��ʂ̃T�C�Y���̏����X�V����
		// �I�������handler�b�Z�[�W��������
		// ���݁A�����ŏ��߂ĉ�ʈʒu�̏��������s���Ă���
        dispInfo.init(this, componentContainer, handler);
        
        //bForceRefresh = true;
        paused = false;
        // ���V�[�o�̍쐬�A�o�^
        receiver = new MediaServiceNotifyReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(MEDIA_SERVICE_NOTIFY);
        registerReceiver(receiver,intentFilter);
        
		super.onResume();
	}

	@Override
	protected void onPause() {
		// �}�b�v�����[�v���āA�S���̐ݒ��ۑ�
		Editor editor = getPreferences(MODE_PRIVATE).edit();
		// ���ݑI������Ă���^�uID
		for(int i=0; i < tabStocker.getTabPageIdMap().size(); ++i ) {
			editor.putInt( String.valueOf( tabStocker.getTabPageIdMap().keyAt(i) ),
					tabStocker.getTabPageIdMap().valueAt(i) );
		}
		editor.commit();
		
		paused = true;

		// ���ʉ��N���X�̉��
        getResourceAccessor().releaseSound();
        // �S�Ẵ��V�[�o�̓o�^����
        stateStocker.unResisterReceiverAll();
        if( null != receiver )
        {
        	this.unregisterReceiver(receiver);
        	receiver = null;
        }
        
        // ���[�V�����Z���T�̓o�^����
        getResourceAccessor().rereaseMotionSenser();
        
		super.onPause();
	}
	
	/**
	 * ���C���^�u�̑I���̕ύX
	 * @param �V�����I�������AmainTab�̃^�u�y�[�WID 
	 * @param bForceRefresh �����I�Ƀ��t���b�V�����邩
	 * @return 0:�ω��Ȃ� 1:�ω��L�� -1:�G���[
	 */
	public int setMainTabSelection( int mainTab, boolean bForceRefresh )
	{	
		// �V�����I�������^�u�̃X�e�[�^�X�N���X���쐬
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
			// �^�u���ς���Ă��邩�A�������t���b�V���̏ꍇ
			// Log.w("setMainTabSelection", "come");
			if( stateStocker.getState(ControlIDs.TAB_ID_MAIN) != null )
			{
				// �O�̃^�u�̃��V�[�o��o�^����
				stateStocker.getState(ControlIDs.TAB_ID_MAIN).unregisterReceivers(IDisplayState.STATUS_ON_DESTROY);
			}
			// �I������Ă���^�u�̕ύX
			tabStocker.setCurrentTabPageId(ControlIDs.TAB_ID_MAIN, mainTab );
			// �X�e�[�^�X�N���X�̕ύX
			stateStocker.putState(ControlIDs.TAB_ID_MAIN, stateMainTmp);
			iRet = 1;
        }
        if( stateStocker.getState(ControlIDs.TAB_ID_MAIN) != null )
        {
        	// ���ID�����Ԃ��擾�ł���
        	if( iRet == 1 )
        	{
        		// ���݂̃^�u�ɉ����āA�f�B�X�v���C��؂�ւ���
        		stateStocker.getState(ControlIDs.TAB_ID_MAIN).ChangeDisplayBasedOnThisState(
        				tabStocker.getTab(ControlIDs.TAB_ID_MAIN));
        	}
        }
        if( mainTab == TabPage.TABPAGE_ID_MEDIA )
        {
        	// �I�����ꂽ�^�u�y�[�W�����f�B�A�^�u�������ꍇ
        	// �q�ƂȂ郁�f�B�A�^�u���X�V������
           	sendUpdateMessage(ControlIDs.TAB_ID_MEDIA, 
           			tabStocker.getCurrentTabPageId(ControlIDs.TAB_ID_MEDIA)
           			,bForceRefresh);
        }
        else if( mainTab == TabPage.TABPAGE_ID_PLAY )
        {
        	// �I�����ꂽ�^�u�y�[�W���v���C�^�u�������ꍇ
        	// �q�ƂȂ�v���C�^�u���X�V������
           	sendUpdateMessage(ControlIDs.TAB_ID_PLAY, 
           			tabStocker.getCurrentTabPageId(ControlIDs.TAB_ID_PLAY)
           			,bForceRefresh);
        }    
        return iRet;
	}
	/**
	 * ���f�B�A�^�u���̃^�u�y�[�W��I������
	 * @param subTab �V�����I���������^�u�y�[�W��ID
	 * @param bForceRefresh �������t���b�V���t���O
	 * @return 0:�ω��Ȃ� 1:�ω��L�� -1:�G���[
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
    		// �^�u�y�[�W���ύX����Ă��邩�A���̃^�u�����̃��t���b�V�������������t���b�V���ɂȂ�t���O�������Ă��邩�A
    		// �������t���b�V���t���O�������Ă���ꍇ
    		// ���̃��t���b�V�������������t���b�V���ɂȂ�t���O�𗎂Ƃ�
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
    		// ���C���^�u�̑I�������f�B�A�^�u�ł����
    		if( tabStocker.getCurrentTabPageId(ControlIDs.TAB_ID_MAIN) 
        			== TabPage.TABPAGE_ID_MEDIA )
        	{
    			// �T�u��ʂ����[�h����
        		if( stateMedia != null && tabStocker.getTab(ControlIDs.TAB_ID_MEDIA) != null)
                {
                	stateMedia.ChangeDisplayBasedOnThisState(
                			tabStocker.getTab(ControlIDs.TAB_ID_MEDIA));
                	// �ʂ̃��C���^�u�̎q�^�u�̑I�����N���A
                	// tabStocker.getTab(ControlIDs.TAB_ID_PLAY).setCurrentTab( TabPage.TABPAGE_ID_NONE, true );
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
    		// �v���C�^�u�ł����
        	if( tabStocker.getCurrentTabPageId(ControlIDs.TAB_ID_MAIN) == TabPage.TABPAGE_ID_PLAY )
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
                	// �ʂ̃v���C�^�u��I��
                	// tabStocker.getTab(ControlIDs.TAB_ID_PLAY).setCurrentTab( TabPage.TABPAGE_ID_PLAY_SUB, true );
            		
                }
        	}
    	}
        return iRet;
	}
	/**
	 * �^�uID���X�V����
	 * @param tabId �^�u��ID
	 * @param tabPageId �^�u�y�[�W��ID
	 * @param bForce �������t���b�V���t���O
	 */
	public void updateTabId( int tabId, int tabPageId, boolean bForce )
	{
		// ���݂̃^�u��ݒ肷��
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
			if( TabPage.TABPAGE_ID_NONE == id 
			|| TabPage.TABPAGE_ID_UNKNOWN == id )
			{
				id = TabPage.TABPAGE_ID_PLAY_SUB;
			}
			setPlayTabSelection( id, bForce );
		}
		
	}

	/**
	 * �^�u�̍X�V�������郁�b�Z�[�W�𓊂���.
	 * �����A�^�u�I���A�N�V���������s���邾��
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
	 * ���݂̏󋵂ɍ��킹�āA���X�i��o�^������
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
	 * ���f�B�A���C�u�����̃��X�L����
	 * @param tabPageID �^�u�y�[�W��ID
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
    			tabStocker.getCurrentTabPageId(tabID));
		
		if( ControlIDs.TAB_ID_MEDIA == tabID )
		{
			// ���f�B�A�^�u�Ȃ��
			// ���f�B�A���ēx�N�G�����s���čX�V����
	    	
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
        // �S�Ẵ��V�[�o�̓o�^����
        stateStocker.unResisterReceiverAll();
		
		//try {
			if( MediaPlayerUtil.sService != null ) //&& false == MediaPlayerUtil.sService.isPlaying() )
			{
				// �T�[�r�X�̓o�^����
			    MediaPlayerUtil.unbindFromService(mToken);
	        	// Toast.makeText(this, "service unregistered : token=" + mToken, Toast.LENGTH_LONG).show();			    
			}
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		// �j�����́A�Ȃ��N���A 
		MediaStopAction stopAction = new MediaStopAction();
		stopAction.doAction(null);
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
		        		((ButtonImpl)timeBtns[i].getView()).setImageBitmap( getResourceAccessor().createBitmapFromDrawableId(timeImgResIds[ timeArgs[i] ]) );
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