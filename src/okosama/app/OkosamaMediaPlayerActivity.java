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
import android.os.RemoteException;
import okosama.app.service.MediaPlaybackService;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.service.MediaPlayerUtil.ServiceToken;
import okosama.app.state.DisplayStateFactory;
import okosama.app.state.IDisplayState;
import okosama.app.state.absDisplayState;
import okosama.app.storage.Database;
import okosama.app.tab.*;
import okosama.app.tab.media.TabMediaSelect;
import okosama.app.widget.Button;
import okosama.app.widget.TimeControlPanel;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import okosama.app.widget.ButtonImpl;

public class OkosamaMediaPlayerActivity extends Activity
implements ServiceConnection {

	// �������t���b�V���t���O�H
	public boolean bForceRefresh = false;
	// ���t���b�V���p���b�Z�[�WID
    public static final int REFRESH = 1001;
    // �|�[�Y���H
    private boolean paused;
	// ���܂�悭�Ȃ����A�����ɒu��
    // ���s�[�g�{�^��
	Button btnRepeat = null;
	public Button getRepeatButton()
	{
		if( btnRepeat == null )
		{
			btnRepeat = DroidWidgetKit.getInstance().MakeButton();
		}
		return btnRepeat;
	}
	public void setRepeatButtonImage()
	{
        if (MediaPlayerUtil.sService == null || btnRepeat == null || btnRepeat.getView() == null ) return;
        try {
            switch (MediaPlayerUtil.sService.getRepeatMode()) {
                case MediaPlaybackService.REPEAT_ALL:
                	((ButtonImpl)btnRepeat.getView()).setImageResource(R.drawable.btn_no_repeat_image);
                    break;
                case MediaPlaybackService.REPEAT_CURRENT:
                	((ButtonImpl)btnRepeat.getView()).setImageResource(R.drawable.btn_one_repeat_image);
                    break;
                default:
                	((ButtonImpl)btnRepeat.getView()).setImageResource(R.drawable.btn_repeat_all_image);
                    break;
            }
        } catch (RemoteException ex) {
        }	
	}
	// �V���b�t���{�^��
	Button btnShuffle = null;
	public Button getShuffleButton()
	{
		if( btnShuffle == null )
		{
			btnShuffle = DroidWidgetKit.getInstance().MakeButton();
		}		
		return btnShuffle;
	}
	public void setShuffleButtonImage()
	{
        if (MediaPlayerUtil.sService == null || btnShuffle == null || btnShuffle.getView() == null ) return;
        try {
            switch (MediaPlayerUtil.sService.getShuffleMode()) {
                case MediaPlaybackService.SHUFFLE_AUTO:
                	((ButtonImpl)btnShuffle.getView()).setImageResource(R.drawable.btn_shuffle_auto_image);
                    break;
                case MediaPlaybackService.SHUFFLE_NORMAL:
                	((ButtonImpl)btnShuffle.getView()).setImageResource(R.drawable.btn_shuffle_all_image);
                    break;
                //case MediaPlaybackService.SHUFFLE_NONE:
                default:
                	((ButtonImpl)btnShuffle.getView()).setImageResource(R.drawable.btn_no_shuffle_image);
                    break;
            }
        } catch (RemoteException ex) {
        }	
	}
	// ���ԃp�l��
	TimeControlPanel timeCP = null;
	public TimeControlPanel getTimeCP()
	{
		if( timeCP == null )
		{
			timeCP = new TimeControlPanel(this);
		}
		return timeCP;
	}
	// �|�[�Y�{�^��
	Button btnPlayPause = null;
	public Button getPlayPauseButton()
	{
		if( btnPlayPause == null )
		{
			btnPlayPause = DroidWidgetKit.getInstance().MakeButton();
		}
		return btnPlayPause;
	}
	public void setPlayPauseButtonImage()
	{
        if (MediaPlayerUtil.sService == null 
        		|| btnPlayPause == null 
        		|| btnPlayPause.getView() == null ) return;
        try {
            if(MediaPlayerUtil.sService.isPlaying()== true) 
            {
               	((ButtonImpl)btnPlayPause.getView()).setImageResource(R.drawable.pause_button_image);
            }
            else
            {
            	((ButtonImpl)btnPlayPause.getView()).setImageResource(R.drawable.play_button_image);
            }
        } catch (RemoteException ex) {
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
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMedia(null, true);
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
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMedia(null, true);
		}
	}
	private static boolean internalRef = true;
    
    
    // Adapter�i�[�p�}�b�v
    // Object��Adapter���i�[����
    // ���܂��������ǂ���������Ȃ����A���̂Ƃ���A�����onRetainNonConfigurationInstance�ňꎞ�ۑ�����Ώۂɂ���
    // HashMap< String, Object > mapAdapter;
    // AdapterStocker adapters;
    // �b���
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
     * �ʂ�����map���ƕۑ����Ă��܂������̂��E�E�E�BTODO �v�m�F
     */
    @Override
    public Object onRetainNonConfigurationInstance() {
    	// TODO: �ł�����adapter��ԋp
        return null;// adapters;
    }
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
	
	public static int TIMECHAR_WIDTH = 80;
	public static int TIMECHAR_HEIGHT = 100;

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
	public Tab getTabMain()
	{
		return tab;
	}
//	public static Tab getMediaTab()
//	{
//		return tabMedia;
//	}
	
	// ���������ɁA�X�N���[���T�C�Y�擾�ɃX���b�h���K�v�ɂȂ邽�߁A�X���b�h�Ƃ̓������K�v�ɁE�E�E
	private static Handler handler = null;
	public Handler getHandler()
	{
		return handler;
	}
	boolean bInitEnd = false;
	
	private static HashMap<String,Integer> tabCurrentDisplayIdMap = new HashMap<String,Integer>();
	/**
	 * ���݂̉��ID��ݒ肷��
	 * ���̂Ƃ���A���ID�Ƃ����̂́A�^�uID�ɓ�����
	 * �܂��A���̒l�́A�A�N�e�B�r�e�B�ɂ��邪�A�A�v���P�[�V�����S�̂ŗ��p���銴���̂��̂ł���B
	 * �Ƃ肠����static�ɂ��Ă������A�N���X���ړ����Ă�������������Ȃ�
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
        HideTabComponentAction.getInstance().setTabLayout(componentContainer);
        ShowTabComponentAction.getInstance().setTabLayout(componentContainer);

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
        handler =  new Handler(){
	        //���b�Z�[�W��M
	        public void handleMessage(Message message) {
	        	if( message.what == REFRESH )
        		{
	                long next = NO_REFRESH;
	                if( currentMainTabId != TabPage.TABPAGE_ID_MEDIA )
	                {
	        			if( stateMain == null )
	        			{
	        				return;
	        			}
	        			next = stateMain.updateDisplay();
	                }
	                else
	                {
	        			if( stateSub == null )
	        			{
	        				return;
	        			}
	                	next = stateSub.updateDisplay();
	                }
	                queueNextRefresh(next);
	                return;
        		}
	        	switch( message.arg1 )
	        	{
		        	case DisplayInfo.MSG_INIT_END:
		        	{
			        	if( bInitEnd == true )
			        	{
			        	}
			        	else
			        	{
			        		// �^�u���쐬
				            tab = new Tab(
				            	tabNameMain
				            	,pageContainer
				            	,componentContainer 
				            );
				            tab.create(R.layout.tab_layout_header);
			        	}
			            // ���ݑI�𒆂̃^�u�̏����N���A����
			            // TODO:�ꏊ����
			           	tabCurrentDisplayIdMap.clear();
			            // �K�v�ł���΁A�ݒ�𕜌�����
			            // TODO:���ꂪ����OnCreate�̃^�C�~���O�Ȃ̂͗v����
			            // OnResume�̕���������������Ȃ�
			            // ��ʈړ�&����������
		//	           	if( bTabInitEnd == false )
		//	           	{
			    		SharedPreferences pref = getPreferences(MODE_PRIVATE);
			    		setCurrentDisplayId( tabNameMain, pref.getInt(tabNameMain + dispIdKey, TabPage.TABPAGE_ID_UNKNOWN) );
			    		setCurrentDisplayId( tabNameMedia, pref.getInt(tabNameMedia + dispIdKey, TabPage.TABPAGE_ID_ARTIST ) );//TabPage.TABPAGE_ID_UNKNOWN) );
//			           	setMainTabSelection(
//			           		pref.getInt(tabNameMain + dispIdKey, TabPage.TABPAGE_ID_UNKNOWN)
//			           	);
//			           	setMediaTabSelection(
//			           		pref.getInt(tabNameMedia + dispIdKey, TabPage.TABPAGE_ID_UNKNOWN)
//			           	);
			           	sendUpdateMessage(tabNameMain);
			           	sendUpdateMessage(tabNameMedia);
//			           	TabSelectAction selAct = new TabSelectAction(tab, currentMainTabId);
//			           	selAct.doAction(null);
//			           	TabSelectAction selActMedia = new TabSelectAction( tab.getTabPageMedia().getTabContent(), currentSubTabId );
//			           	selActMedia.doAction(null);
			           	getTimeCP().setDurationLabel(0);
			    				           	
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
		        		// �^�u���I�����ꂽ�ʒm
		        		if( message.arg2 == TabPage.TABPAGE_ID_MEDIA 
		        		&& currentMainTabId == TabPage.TABPAGE_ID_MEDIA )
		        		{
		        			// ���f�B�A�^�u���ł̏���
		        			// �����������Ȃ�
		        			break;
		        		}
		        		// Activity�̃^�uid���X�V
		        		setMainTabSelection(
		        			OkosamaMediaPlayerActivity.getCurrentDisplayId(OkosamaMediaPlayerActivity.tabNameMain)
		        		);
		        		setMediaTabSelection(
		        			OkosamaMediaPlayerActivity.getCurrentDisplayId(OkosamaMediaPlayerActivity.tabNameMedia)
		        		);
		        	
		        		// ���X�i���X�V
		            	updateListeners();
		            	// ���f�B�A���X�V
		            	reScanMedia((String)message.obj,false);
		            	// ���ʕ����ĕ`��
		            	updateCommonCtrls();
		        		break;
	        	}
	        };
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
						RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
		
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
		int widthCorrect = dispInfo.getCorrectionXConsiderDensity(width);
		int heightCorrect = dispInfo.getCorrectionYConsiderDensity(height);
		int xCorrect = dispInfo.getCorrectionXConsiderDensity(left);
		int yCorrect = dispInfo.getCorrectionYConsiderDensity(top);
		
		RelativeLayout.LayoutParams lp = 
				new RelativeLayout.LayoutParams(
						widthCorrect, heightCorrect);
		
        lp.topMargin = yCorrect;
        lp.leftMargin = xCorrect;
        // ���̃A�v���P�[�V�����ł́Abottom��right��margin�̓[�������E�E�E�B
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
		// TODO:�}�b�v�����[�v���āA�S���̐ݒ��ۑ�
		//tabCurrentDisplayIdMap.
        //outcicle.putInt("displayid", iCurrentDisplayId);
//		for(Entry<String, Integer> e : tabCurrentDisplayIdMap.entrySet()) {
//		    outcicle.putInt( e.getKey(), e.getValue() );
//		}
        super.onSaveInstanceState(outcicle);
    }

	@Override
	protected void onPause() {
		// �}�b�v�����[�v���āA�S���̐ݒ��ۑ�
		//tabCurrentDisplayIdMap.
        //outcicle.putInt("displayid", iCurrentDisplayId);
		Editor editor = getPreferences(MODE_PRIVATE).edit();
		for(Entry<String, Integer> e : tabCurrentDisplayIdMap.entrySet()) {
			editor.putInt( e.getKey(), e.getValue() );
		}
		editor.commit();
		
		// TODO Auto-generated method stub
		// setTabSelection( TabPage.TABPAGE_ID_NONE, TabPage.TABPAGE_ID_NONE );
		// �{���́A��L�ł����Ȃ��ė~�����̂����A���󂻂��Ȃ��Ă��Ȃ���������Ȃ�
		// �S�Ẵr���[���N���A���Ă���
		if( pageContainer.getBackground() != null )
		{
			pageContainer.getBackground().setCallback(null);
			pageContainer.setBackgroundDrawable(null);
		}
		componentContainer.removeAllViews();
		bInitEnd = false;
		bForceRefresh = true;
        getResourceAccessor().releaseSound();
		
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
    	// ��ʂ̃T�C�Y���̏����X�V����
        dispInfo.init(this, componentContainer, handler);
		
		//setTabSelection( currentMainTabId, currentSubTabId );
//		}
        getResourceAccessor().initSound();
        bForceRefresh = true;
		super.onResume();
	}

	/**
	 * �T�u�^�u�́A�󋵂ɂ���ĕω�����̂Œ���
	 * �܂����̊֐��͖������i���́A�T�u�^�u�̑I�𕔕�)
	 * @param mainTab	 
	 * @param bSndChgMsg �ω����������Ƃ��A���b�Z�[�W�𑗐M���邩
	 * @return 0:�ω��Ȃ� 1:�ω��L�� -1:�G���[
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
        	// ���ID�����Ԃ��擾�ł���
        	if( iRet == 1 )
        	{
        		//iRet2 = 
        		stateMain.ChangeDisplayBasedOnThisState(tab);
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
	        		// ���f�B�A�^�u�ł����
	        		// �T�u��ʂ����[�h����
	        		// ��i�K�ɕ�����Ɠ�x��ʍX�V������̂Ŗ��ʂ������Ǝv���邪�A�Ƃ肠�������ꂵ���v�����Ȃ�
	                stateSub = DisplayStateFactory.createDisplayState(subTab);        		
	                if( stateSub != null && tabMedia != null)
	                {
	                	stateSub.ChangeDisplayBasedOnThisState(tabMedia);
	                }
	        	}
        	}
        	else
        	{
        		// MediaTab�łȂ��ꍇ�A�T�uTab�͋[���I��None�ōX�V������
        		// DisplayStateFactory.createDisplayState(TabPage.TABPAGE_ID_NONE).ChangeDisplayBasedOnThisState(tabMedia);  
        	}
        }
        return iRet;
	}
	void sendUpdateMessage(String tabName)
	{
    	Message msg = new Message();
    	msg.arg1 = TabSelectAction.MSG_ID_TAB_SELECT;
    	msg.obj = tabName;
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
	 * adapter�̏������I������Ƃ��ɁAadapter����R�[���o�b�N���銴���̊֐�
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
			// null�̂܂܂�cursor�ݒ肳��Ă����ɖ��͂Ȃ�
			// return;
		}
		switch( id )
		{
		// TODO:null�̏ꍇ�A�\������r���[��ύX��������������������Ȃ�
		case TabPage.TABPAGE_ID_ALBUM:
			// List�ɃJ�[�\����ݒ�
			getAlbumAdp().changeCursor(cursor);
			break;
		case TabPage.TABPAGE_ID_ARTIST:
			// List�ɃJ�[�\����ݒ�
			getArtistAdp().changeCursor(cursor);
			break;
		case TabPage.TABPAGE_ID_SONG:
			// List�ɃJ�[�\����ݒ�
			getTrackAdp().changeCursor(cursor);
			break;
		case TabPage.TABPAGE_ID_PLAYLIST:
			Cursor c = null;
			c = Database.getInstance(isExternalRef()).mergedCursor(cursor, false);//createShortCut);
			// List�ɃJ�[�\����ݒ�
			getPlaylistAdp().changeCursor(c);
			break;
		}
	}
	
	public void reScanMedia(String tabName, boolean bForce)
	{
		// ���ݑI�𒆂̃^�u�ɂ���đ����ύX
		if( //tabName == tabNameMedia && 
			( true == bForce
			|| false == tabNameMain.equals(tabName) )
			&& currentMainTabId == TabPage.TABPAGE_ID_MEDIA )
		{
			// ���f�B�A�^�u�Ȃ��
			// ���f�B�A���ēx�N�G�����s���čX�V����
			switch( currentSubTabId )
			{
			case TabPage.TABPAGE_ID_ALBUM:
				// List�ɃJ�[�\����ݒ�
				Database.getInstance(externalRef).createAlbumCursor(getAlbumAdp().getQueryHandler(), null );//, null);
				break;
			case TabPage.TABPAGE_ID_ARTIST:
				// List�ɃJ�[�\����ݒ�
				Database.getInstance(externalRef).createArtistCursor(getArtistAdp().getQueryHandler(), null);			
				break;
			case TabPage.TABPAGE_ID_SONG:
				OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.setPlaylistName( null );        	
				Database.getInstance(externalRef).createTrackCursor(getTrackAdp().getQueryHandler(), null, true );//, null, null, null);			
				break;
			case TabPage.TABPAGE_ID_PLAYLIST:
				Database.getInstance(externalRef).createPlaylistCursor(getPlaylistAdp().getQueryHandler(), null, false);						
				break;
			case TabPage.TABPAGE_ID_NONE:
				// TODO: �N���A��������ꂽ������������
				break;
			}
		}
		else if( tabName != null && tabName.equals( tabNameMain ) && currentMainTabId == TabPage.TABPAGE_ID_NOW_PLAYLIST )
		{
			// TODO: ���݁A�g���b�N�Ɠ����J�[�\���ɂȂ��Ă��邪�A�l��������������������Ȃ�
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
        paused = true;
        handler.removeMessages(REFRESH);
	}

	@Override
	protected void onDestroy() {
		try {
			if( false == MediaPlayerUtil.sService.isPlaying() )
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
        paused = false;
	}
	
	///////////////////////// �T�[�r�X�p�̃��\�b�h //////////////////////////////
	/**
	 * �T�[�r�X�ڑ���
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
	 * �T�[�r�X�ؒf��
	 */
	@Override
	public void onServiceDisconnected(ComponentName name) {
       // Toast.makeText(this, "onServiceDisconnected:" + name, Toast.LENGTH_LONG).show();
		
		// �悭�킩��Ȃ����ǁA�T�[�r�X�ؒf���ꂽ��I������H
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
	        Button timeBtns[] = getResourceAccessor().appStatus.getTimesButton();
	        for( int i=0; i<timeBtns.length; i++ )
	        {
	        	if( null != timeBtns[i].getView() )
	        	{
	        		((ButtonImpl)timeBtns[i].getView()).setVisibility(bShowImgFlg[i] ? View.VISIBLE : View.INVISIBLE );
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
	        
	        Button timeBtns[] = getResourceAccessor().appStatus.getTimesButton();
	        for( int i=0; i<timeBtns.length; i++ )
	        {
	        	if( null != timeBtns[i].getView() )
	        	{
	        		((ButtonImpl)timeBtns[i].getView()).setImageResource( timeImgResIds[ timeArgs[i] ] );
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