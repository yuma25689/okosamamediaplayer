package okosama.app.tab;

import java.util.ArrayList;

import okosama.app.ControlDefs;
import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.adapter.IAdapterUpdate;
import okosama.app.behavior.TrackListBehavior;
import okosama.app.behavior.VideoListBehavior;
import okosama.app.panel.MoveTabInfo;
import okosama.app.panel.TabMoveLeftInfoPanel;
import okosama.app.panel.TabMoveRightInfoPanel;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.widget.List;
import okosama.app.widget.absWidget;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class TabPageNowPlaylist extends TabPage {

	//Tab tabContent;
	public TabPageNowPlaylist( Tab parent, LinearLayout ll, RelativeLayout rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		// �R���X�g���N�^�ł��̃^�u�̃^�uID��ݒ�
		this.tabId = TABPAGE_ID_NOW_PLAYLIST;
		create(R.layout.tab_layout_content_empty_show );
//		componentContainer.addView(tabButton.getView());
	}
	ViewGroup EmptyPanel = null;
	@Override
	public int create(int panelLayoutID) {
		// �t���b�N���͑Ή�
		ArrayList<MoveTabInfo> arrMti = new ArrayList<MoveTabInfo>();
		// ���t���b�N���̐ݒ�
		MoveTabInfo mti = new MoveTabInfo();
		mti.setTabInfoIndex( MoveTabInfo.LEFT_1 );
		mti.setTabId(ControlIDs.TAB_ID_MAIN);
		mti.setTabPageId(TabPage.TABPAGE_ID_PLAY);
		mti.setPanelId(R.id.left_move_panel);
		mti.setImageViewId(R.id.left_move_image);
		mti.setTabImageResId(R.drawable.brat_main_normal);
		arrMti.add(mti);
		// �E�t���b�N���̐ݒ�
		MoveTabInfo mtiR = new MoveTabInfo();
		mtiR.setTabInfoIndex( MoveTabInfo.RIGHT_1 );
		mtiR.setTabId(ControlIDs.TAB_ID_MAIN);
		mtiR.setTabPageId(TabPage.TABPAGE_ID_MEDIA);
		mtiR.setPanelId(R.id.right_move_panel);
		mtiR.setImageViewId(R.id.right_move_image);
		mtiR.setTabImageResId(R.drawable.video_normal);
		arrMti.add(mtiR);
				
		resetPanelViews( panelLayoutID, arrMti );
		RelativeLayout.LayoutParams lp 
		= OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
        		0, 0
        );
		tabBaseLayout.setLayoutParams(lp);
		tabBaseLayout.setBackgroundResource(R.color.gradiant_red);
		updateProgressPanel = (ViewGroup)tabBaseLayout.findViewById(R.id.TabCommonProgressPanel );
		EmptyPanel = (ViewGroup)tabBaseLayout.findViewById(R.id.EmptyShowPanel );
		if( EmptyPanel != null )
		{
			EmptyPanel.setVisibility(View.GONE);
		}

		// NowPlaylistTab�{�^���̃A�N�V����
//		SparseArray< IViewAction > actMapTemp
//			= new SparseArray< IViewAction >();
//		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( 
//				parent, tabId ) );
//		TabComponentActionSetter actionSetter = new TabComponentActionSetter( actMapTemp );	
//		tabButton.acceptConfigurator(actionSetter);

		//////////////////// list //////////////////////////
		TabComponentPropertySetter creationData[] = {
			// ------------- TAB
			new TabComponentPropertySetter(
				List.LISTID_NOW_PLAYLIST, this, ComponentType.LIST_NOWPLAYLIST, 
				0, 0,//150 + 2
				480, ControlDefs.LIST_HEIGHT_2//637 //599
				, null, null//R.drawable.tab_3_list_bk
				, "", ScaleType.FIT_XY
			)
		};
		List lst = OkosamaMediaPlayerActivity.getResourceAccessor().getNowPlayingListView();//new TrackListBehavior());
		//DroidWidgetKit.getInstance().MakeList( new TrackListBehavior() );
		if( lst.getView() != null && lst.getView().getParent() != null )
		{
			OkosamaMediaPlayerActivity.removeFromParent(lst.getView());
		}
		if( -1 == widgets.indexOf(lst) )
		{
			widgets.add(lst);
		}			
		// �{�^�����쐬�A�ʒu�����킹�A�A�N�V������ݒ肵�A���C�A�E�g�ɔz�u
		int i=0;
		for( absWidget widget : widgets )
		{
			widget.acceptConfigurator(creationData[i]);
			// TODO:�{�^���̃A�N�V������ݒ�
			//lst.getView().setBackgroundColor(Color.DKGRAY);
			// �{�^�������̃^�u�q���ڂƂ��Ēǉ�
			// addChild( creationData[i].getInternalID(), lst );
			tabBaseLayout.addView( widget.getView() );
			// �{�^����z�u
			// ����́AsetActivate�ōs��
			// componentContainer.addView( btn.getView() );
			i++;
		}
		rightPanel = new TabMoveRightInfoPanel(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
		leftPanel = new TabMoveLeftInfoPanel(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
		
		rightPanel.createInstance(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
		leftPanel.createInstance(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
		rightPanel.insertToLayout(tabBaseLayout);
		leftPanel.insertToLayout(tabBaseLayout);
		
		return 0;
	}
	@Override
	public void startUpdate()
	{
		if( null != EmptyPanel )
		{
			EmptyPanel.setVisibility(View.GONE);
		}
		super.startUpdate();
	}
	public void endUpdate()
	{
		if( null != EmptyPanel )
		{
			// �A�_�v�^����ł���ꍇ�̂݁AEmpty��\��
			
			OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
			IAdapterUpdate adp = act.getAdpStocker().get(TabPage.TABPAGE_ID_SONG);
			if( adp != null )
			{
				if( adp.getMainItemCount() == 0 )
				{
					EmptyPanel.setVisibility(View.VISIBLE);
				}
				else
				{
					EmptyPanel.setVisibility(View.GONE);
				}
			}
		}
		super.endUpdate();
	}
	@Override
	public void setActivate( boolean bActivate )
	{
		if( bActivate )
		{
			// NowPlaying�̃��X�g�́A�����ŕK���\���O��Adapter��behavior��ݒ肵����
			OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
			List lst = OkosamaMediaPlayerActivity.getResourceAccessor().getNowPlayingListView();			
       		if( MediaPlayerUtil.isNowPlayingVideos() )
       		{
       			// ���݁A�r�f�I���Đ��Ώۂ̏ꍇ
       			// Adapter��Behavior��Video�ɐݒ肷��
       			lst.setAdapter( act.getVideoAdp() );
       			lst.setBehavior(new VideoListBehavior());
       		}
       		else
       		{
       			// ���݁A�I�[�f�B�I���Đ��Ώۂ̏ꍇ
       			// Adapter��Behavior��Track�ɐݒ肷��
       			lst.setAdapter( act.getTrackAdp() );
       			lst.setBehavior(new TrackListBehavior());       			
       			
       		}
		}
		super.setActivate(bActivate);		
	}
	
}
