package okosama.app.tab;

import okosama.app.ControlIDs;
import okosama.app.MusicSettingsActivity;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.action.ControllerShowHideAction;
import okosama.app.action.IViewAction;
import okosama.app.action.SearchPanelShowHideAction;
import okosama.app.action.TabSelectAction;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.widget.Button;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * �^�u��͕킵���N���X�B���̃N���X�͕\���������Ȃ��B
 * �^�u�Ƃ������́AMediator�ɋ߂��B
 * �^�u�̍쐬����сA�^�u��̃R���|�[�l���g�̗L��/�����A�\��/��\���݂̂𐧌䂷��
 * @author 25689
 *
 */
public class Tab extends TabComponentParent {

	//public static int HDR_SIZE = 100;
	protected SparseArray<Button> mapBtn;
	int iCurrentTabPageId;
	// next force refresh
	boolean nextForceRefresh = false;
	boolean locking = false;
	/**
	 * @return the locking
	 */
	public boolean isLocking() {
		return locking;
	}

	/**
	 * @param locking the locking to set
	 */
	protected void setLocking(boolean locking) {
		this.locking = locking;
	}

	/**
	 * @return the nextForceRefresh
	 */
	public boolean isNextForceRefresh() {
		return nextForceRefresh;
	}

	/**
	 * @param nextForceRefresh the nextForceRefresh to set
	 */
	public void setNextForceRefresh(boolean nextForceRefresh) {
		this.nextForceRefresh = nextForceRefresh;
	}

	public Tab( int ID, LinearLayout ll, ViewGroup rl )
	{
		this.internalID = ID;
		pageContainer = ll;
		componentContainer = rl;
		iCurrentTabPageId = TabPage.TABPAGE_ID_NONE;
		mapBtn = new SparseArray<Button>();
	}
	
	/**
	 * �^�u�S�̂̍쐬
	 * @return 0:���� 0�ȊO:�ُ�
	 */
	@Override
	public int create(int panelLayoutId) {
		int errCode = 0;

		OkosamaMediaPlayerActivity act 
		= OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();

		// �^�u�̃p�l�����쐬
		LayoutInflater inflator = act.getLayoutInflater();
		tabBaseLayout = (ViewGroup)inflator.inflate(panelLayoutId, null, false);
		// �p�l���ʒu�̐ݒ�(FILL_PARENT)
		RelativeLayout.LayoutParams lp 
		= OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
        		0, 0
        );
		tabBaseLayout.setLayoutParams(lp);
		// �^�u�{�^����u���w�b�_�ƂȂ郌�C�A�E�g
		RelativeLayout rlHdr = (RelativeLayout) tabBaseLayout.findViewById(R.id.tab_header);
		// �^�u�̃{�^�������͂����ō��H
		// �v���C�^�u�{�^��
		mapBtn.put( TabPage.TABPAGE_ID_PLAY, DroidWidgetKit.getInstance().MakeButton() );
		TabComponentPropertySetter tabBtnCreationData = new TabComponentPropertySetter(
			ControlIDs.PLAY_TAB_BUTTON, null, ComponentType.BUTTON, 
			10, 5, 100, 100, 
			null, R.drawable.selector_music_tab_button_image,
			"", ScaleType.FIT_XY
		);
		mapBtn.get(TabPage.TABPAGE_ID_PLAY).acceptConfigurator(tabBtnCreationData);		
		SparseArray< IViewAction > actMapTemp 
			= new SparseArray< IViewAction >();
		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( 
				this.getInternalID(),
				TabPage.TABPAGE_ID_PLAY ) );
		mapBtn.get(TabPage.TABPAGE_ID_PLAY).acceptConfigurator(new TabComponentActionSetter( actMapTemp ));
		rlHdr.addView(mapBtn.get(TabPage.TABPAGE_ID_PLAY).getView());
		// �L���[�^�u�{�^��
		mapBtn.put( TabPage.TABPAGE_ID_NOW_PLAYLIST, DroidWidgetKit.getInstance().MakeButton() );
		tabBtnCreationData
		= new TabComponentPropertySetter(
			ControlIDs.NOW_PLAYLIST_TAB_BUTTON, null, ComponentType.BUTTON,
			120, 5, 100, 100,
			null, R.drawable.selector_now_playlist_button_image,
			"", ScaleType.FIT_XY
		);
		mapBtn.get(TabPage.TABPAGE_ID_NOW_PLAYLIST).acceptConfigurator(tabBtnCreationData);		
		actMapTemp
		= new SparseArray< IViewAction >();
		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( 
				this.getInternalID(), TabPage.TABPAGE_ID_NOW_PLAYLIST ) );
		mapBtn.get(TabPage.TABPAGE_ID_NOW_PLAYLIST).acceptConfigurator(new TabComponentActionSetter( actMapTemp ));
		rlHdr.addView(mapBtn.get(TabPage.TABPAGE_ID_NOW_PLAYLIST).getView());
		
		// ���f�B�A�^�u�{�^��
		mapBtn.put( TabPage.TABPAGE_ID_MEDIA, DroidWidgetKit.getInstance().MakeButton() );
		tabBtnCreationData
		= new TabComponentPropertySetter(
			ControlIDs.MEDIA_TAB_BUTTON, null, ComponentType.BUTTON,
			230, 5, 100, 100,
			null, R.drawable.selector_sdcard_choice_button_image,
			"", ScaleType.FIT_XY
		);
		mapBtn.get(TabPage.TABPAGE_ID_MEDIA).acceptConfigurator(tabBtnCreationData);		
		actMapTemp
		= new SparseArray< IViewAction >();
		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( 
				this.getInternalID(), TabPage.TABPAGE_ID_MEDIA ) );
		mapBtn.get(TabPage.TABPAGE_ID_MEDIA).acceptConfigurator(new TabComponentActionSetter( actMapTemp ));
		rlHdr.addView(mapBtn.get(TabPage.TABPAGE_ID_MEDIA).getView());
		// �R���g���[���{�^��
		mapBtn.put( TabPage.TABPAGE_ID_CONTROLLER, act.getControllerShowHideBtn() );
		tabBtnCreationData
		= new TabComponentPropertySetter(
			ControlIDs.CONTROLLER_TAB_BUTTON, null, ComponentType.BUTTON,
			360, 5, 100, 100,
			null, R.drawable.selector_controller_button_image,
			"", ScaleType.FIT_XY
		);
		mapBtn.get(TabPage.TABPAGE_ID_CONTROLLER).acceptConfigurator(tabBtnCreationData);		
		actMapTemp
		= new SparseArray< IViewAction >();
		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new ControllerShowHideAction() );
		mapBtn.get(TabPage.TABPAGE_ID_CONTROLLER).acceptConfigurator(new TabComponentActionSetter( actMapTemp ));
		OkosamaMediaPlayerActivity.removeFromParent(mapBtn.get(TabPage.TABPAGE_ID_CONTROLLER).getView());
		rlHdr.addView(mapBtn.get(TabPage.TABPAGE_ID_CONTROLLER).getView());				
		// �����p�l���\���{�^��
		mapBtn.put( TabPage.TABPAGE_ID_SEARCH, act.getSearchPanelShowHideBtn() );
		tabBtnCreationData
		= new TabComponentPropertySetter(
			ControlIDs.SRCH_TAB_BUTTON, null, ComponentType.BUTTON,
			470, 5, 100, 100,
			null, R.drawable.selector_filter_btn_image,
			"", ScaleType.FIT_XY
		);
		mapBtn.get(TabPage.TABPAGE_ID_SEARCH).acceptConfigurator(tabBtnCreationData);		
		actMapTemp
		= new SparseArray< IViewAction >();
		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new SearchPanelShowHideAction() );
		mapBtn.get(TabPage.TABPAGE_ID_SEARCH).acceptConfigurator(new TabComponentActionSetter( actMapTemp ));
		OkosamaMediaPlayerActivity.removeFromParent(mapBtn.get(TabPage.TABPAGE_ID_SEARCH).getView());
		rlHdr.addView(mapBtn.get(TabPage.TABPAGE_ID_SEARCH).getView());				
		
		RelativeLayout rlCont = (RelativeLayout) tabBaseLayout.findViewById(R.id.tab_contents);
		
		// �^�u�̒ǉ�
		addChild( TabPage.TABPAGE_ID_PLAY, 
				new TabPagePlay2( this, pageContainer, rlCont ) );
		addChild( TabPage.TABPAGE_ID_NOW_PLAYLIST, 
				new TabPageNowPlaylist( this, pageContainer, rlCont ));
		addChild( TabPage.TABPAGE_ID_MEDIA, 
				new TabPageMedia( this, pageContainer, rlCont ) );//new TabPageMedia( this, pageContainer, rlCont ) );
		// �^�u�y�[�W�́AsetCurrentTab��ǂ񂾎��A�A�N�e�B�u�Ȃ��̂����������B
		// �Ȃ����^�u�y�[�W��create�͌Ă�ł͂����Ȃ����ƂɂȂ��Ă��܂����B
		// �܂��Acreate���̃^�uID�͕s���Ȃ̂ŁAsetCurrentTab�͂����ł͌Ă΂��A��ʂɌĂ΂���B
		
		// rlCont.setBackgroundResource(R.color.gradiant_base);
		// VideoView�́A�g��Ȃ����A�ŏ��ɓ˂����ނƂ��ɂȂ�����ʂ��u���b�N�A�E�g����̂ŁA
		// �����ōŏ��ɓ˂�����ł����i�����͋N�����ɂ���Ƃ���Ȃ̂ŁA�u���b�N�A�E�g���Ă��s���R�����Ȃ��Ǝv����j
		SurfaceView videoView 
		= OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getVideoView();
		videoView.setVisibility(View.GONE);
		OkosamaMediaPlayerActivity.removeFromParent( videoView );

		// �^�u�̃p�l����e����^����ꂽ���C�A�E�g�ɒǉ�
		componentContainer.addView(tabBaseLayout);		
		return errCode;
	}
	
	int lastSelectedTabIndexForEnableAllTab = 0;
	/**
	 * �^�u�؂�ւ����̃��b�N�����A�{���͌���Enable���l���������䂪�K�v
	 * @param bEnable
	 */
	public void setEnableAllTab(boolean bEnable,int iTabPageId)
	{
		for( int i=0; i < mapBtn.size(); ++i )
		{
			// ���܂�悭�Ȃ����A�I�𒆂̃^�u�{�^���́A������Enable=true�ɂ͂����Ȃ�
			if( bEnable == true )
			{
				if( //iCurrentTabPageId == mapBtn.keyAt(i) )
						iTabPageId == mapBtn.keyAt(i) )
				{
					mapBtn.valueAt(i).setEnabled(false);
					continue;
				}
			}
			mapBtn.valueAt(i).setEnabled(bEnable);
		}
		setLocking( !bEnable );
	}
	
	/**
	 * ���݂̃^�u��ݒ肷��
	 * ����ATabSelectAction�ł͌��ǂ��ꂪ�Ă΂��
	 * @param tabPageId
	 */
	public void setCurrentTab(int tabPageId,boolean save)
	{
		Log.d("tab.setCurrentTab", "tab:" + tabPageId);
		//synchronized( OkosamaMediaPlayerActivity.getResourceAccessor().tabAnim )
		//{
			OkosamaMediaPlayerActivity.getResourceAccessor().tabAnim.SetTabSelectionLock(
					true, internalID,tabPageId);
			boolean bOutExec = false;
			boolean bAnimExec = false;
	        SharedPreferences prefs 
	        = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getSharedPreferences(
	                MusicSettingsActivity.PREFERENCES_FILE, Context.MODE_PRIVATE);
	        bAnimExec = prefs.getBoolean(MusicSettingsActivity.KEY_ENABLE_ANIMATION, false);
						
			// ��x�S�Ẵ^�u�̑I��������
			if( null != children.get(iCurrentTabPageId,null) )
			{
	    		// ���ݑI�𒆂̃^�u�̃^�u�y�[�W���N���A����
				children.get(iCurrentTabPageId).setActivate(false);
				bOutExec = true;
	   		}
			// ���ݑI�𒆂̃^�u��V�������̂ɐݒ肷��
			if( null != children.get(tabPageId,null) )
			{
				children.get(tabPageId).setActivate(true);
	   		}
			iCurrentTabPageId = tabPageId;
			// �A�v���P�[�V�����ɑI�����ꂽ�^�u�̉��ID��ݒ肷��
			// ���̏ꏊ�����ł������ǂ����͕s��
	        if( save == true )
	        {
	    		OkosamaMediaPlayerActivity act 
	    		= OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
	        	act.setCurrentDisplayId(this.internalID,tabPageId);
	        }
	        if( bAnimExec == false || bOutExec == false )
	        {
	        	OkosamaMediaPlayerActivity.getResourceAccessor().tabAnim.SetTabSelectionLock(
	        			false, internalID,tabPageId);
	        }
		//}

	}
	/**
	 * ���̃^�u��j������
	 */
	public void destroy()
	{
		if( tabBaseLayout != null )
		{
			RelativeLayout rlHdr = (RelativeLayout) tabBaseLayout.findViewById(R.id.tab_header);
			if( rlHdr != null )
			{
				rlHdr.removeAllViews();
			}
			RelativeLayout rlHooter = (RelativeLayout) tabBaseLayout.findViewById(R.id.tab_hooter);
			if( rlHooter != null )
			{
				rlHooter.removeAllViews();
			}
			tabBaseLayout.removeAllViews();
		}
		if( componentContainer != null )
		{
			componentContainer.removeAllViews();
		}
		this.clearChild();
		this.mapBtn.clear();
	}

}
