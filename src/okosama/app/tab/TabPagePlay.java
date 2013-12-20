package okosama.app.tab;

import java.util.ArrayList;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.panel.MoveTabInfo;
import okosama.app.panel.NowPlayingControlPanel;
import okosama.app.panel.PlayControlPanel;
import okosama.app.panel.SubControlPanel;
import okosama.app.panel.TabMoveLeftInfoPanel;
import okosama.app.panel.TabMoveRightInfoPanel;
import okosama.app.panel.TimeControlPanel;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * ���y�Đ��^�u
 * @author 25689
 *
 */
public class TabPagePlay extends TabPage implements OnTouchListener {
	boolean bPanelShow = true;

	public TabPagePlay( Tab parent, LinearLayout ll, RelativeLayout rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		// �R���X�g���N�^�ł��̃^�u�̃^�uID��ݒ�
		this.tabId = TABPAGE_ID_PLAY;
		
		create(R.layout.tab_layout_content_generic_flickable);
//		componentContainer.addView(tabButton.getView());
	}
	/* (non-Javadoc)
	 * @see okosama.app.container.ITabComponent#create()
	 */
	@Override
	public int create(int panelLayoutID) {
		// �t���b�N���͑Ή�
		ArrayList<MoveTabInfo> arrMti = new ArrayList<MoveTabInfo>();
		// ���t���b�N���̐ݒ�
		MoveTabInfo mti = new MoveTabInfo();
		mti.setTabInfoIndex( MoveTabInfo.LEFT_1 );
		mti.setTabId(ControlIDs.TAB_ID_PLAY);
		mti.setTabPageId(TabPage.TABPAGE_ID_NOW_PLAYLIST);
		mti.setPanelId(R.id.left_move_panel);
		mti.setImageViewId(R.id.left_move_image);
		mti.setTabImageResId(R.drawable.video_normal);
		arrMti.add(mti);
		// �E�t���b�N���̐ݒ�
		MoveTabInfo mtiR = new MoveTabInfo();
		mtiR.setTabInfoIndex( MoveTabInfo.RIGHT_1 );
		mtiR.setTabId(ControlIDs.TAB_ID_PLAY);
		mtiR.setTabPageId(TabPage.TABPAGE_ID_NOW_PLAYLIST);
		mtiR.setPanelId(R.id.right_move_panel);
		mtiR.setImageViewId(R.id.right_move_image);
		mtiR.setTabImageResId(R.drawable.playlist_normal);
		arrMti.add(mtiR);
		
		resetPanelViews( panelLayoutID, arrMti );
		RelativeLayout.LayoutParams lp 
		= OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
        		0, 0
        );
		tabBaseLayout.setLayoutParams(lp);
		
		// ---- action
//		SparseArray< IViewAction > actMapTemp 
//			= new SparseArray< IViewAction >();
//		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( parent, tabId ) );
//		TabComponentActionSetter actionSetter = new TabComponentActionSetter( actMapTemp );			
//		tabButton.acceptConfigurator(actionSetter);
		
		//////////////////// button //////////////////////////
//		TabComponentPropertySetter creationData[] = {
//			// --------------------- STOP
//			new TabComponentPropertySetter(
//				ControlIDs.STOP_BUTTON, this, ComponentType.BUTTON, 
//				150, 500, 100, 100
//				, null, R.drawable.stop_button_image, "", ScaleType.FIT_XY
//			),
//			// --------------------- TWITTER
//			new TabComponentPropertySetter(
//				ControlIDs.TWEET_BUTTON, this, ComponentType.BUTTON, 
//				370, 450, 80, 80
//				, null, R.drawable.internal_btn_image, "", ScaleType.FIT_XY
//			),
//		};
		
		// �w�i�摜�͂Ȃ���setActivate�̒S���Ȃ̂ŁA�����ł͒ǉ����Ȃ�
		
		
		OkosamaMediaPlayerActivity activity = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		// �{�^���̃n���h���N���X���쐬
		// �����炭�A�N���X�Ɏ�������������
//		absWidget widgets[] = {
//			DroidWidgetKit.getInstance().MakeButton()
//			,DroidWidgetKit.getInstance().MakeButton()
//		};
		
		// ---- action
		// Stop�{�^��
//		SparseArray< IViewAction > actMapStop 
//			= new SparseArray< IViewAction >();
//		actMapStop.put( IViewAction.ACTION_ID_ONCLICK, new MediaStopAction() );
//		// twitter�{�^��
//		SparseArray< IViewAction > actMapTwitter
//			= new SparseArray< IViewAction >();
//		actMapTwitter.put( IViewAction.ACTION_ID_ONCLICK, new TweetAction() );
//		
//		TabComponentActionSetter actionSetterCont[] = {
//			new TabComponentActionSetter( actMapStop )
//			,new TabComponentActionSetter( actMapTwitter )
//		};
		
		// �{�^�����쐬�A�ʒu�����킹�A�A�N�V������ݒ肵�A���C�A�E�g�ɔz�u
//		int i=0;
//		for( absWidget widget : widgets )
//		{
//			widget.acceptConfigurator(creationData[i]);
//			// TODO:�{�^���̃A�N�V������ݒ�
//			if( actionSetterCont[i] != null )
//			{
//				widget.acceptConfigurator(actionSetterCont[i]);
//			}
//			
//			// �{�^�������̃^�u�q���ڂƂ��Ēǉ�
//			//addChild( creationData[i].getInternalID(), widget );
//			tabBaseLayout.addView( widget.getView() );
//			
//			// �{�^����z�u
//			// ����́AsetActivate�ōs��
//			// componentContainer.addView( btn.getView() );
//			i++;
//		}
		// ����ɂ��ƁA�Ō�ɉ�����ꂽ���̂�Zorder����ԏ�炵���B
		// �ǂ����ɂ��Ă��ABringToFront�ŕς�����炵����
		SubControlPanel.insertToLayout(tabBaseLayout);
		TimeControlPanel.insertToLayout(tabBaseLayout);
		PlayControlPanel.insertToLayout(tabBaseLayout);
//		tabBaseLayout.addView( activity.getSubCP().getView() );
//		tabBaseLayout.addView( activity.getTimeCP().getView() );
//		tabBaseLayout.addView( activity.getPlayCP().getView() );
		tabBaseLayout.setBackgroundResource(R.color.gradiant_test);
		
		// TODO: �T�[�t�B�X�r���[�������ς��ɓ����
		SurfaceView videoView 
		= OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getVideoView();
		RelativeLayout.LayoutParams lpVideoView
		= OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
        		0, 0 
        );
		videoView.setLayoutParams(lpVideoView);
		videoView.setOnTouchListener(this);
		tabBaseLayout.addView( videoView );
		// tabBaseLayout.setOnTouchListener(new TabViewTouchListener(0,0));
		rightPanel = new TabMoveRightInfoPanel(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
		leftPanel = new TabMoveLeftInfoPanel(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
		
		rightPanel.createInstance(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
		leftPanel.createInstance(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
		rightPanel.insertToLayout(tabBaseLayout);
		leftPanel.insertToLayout(tabBaseLayout);
		
		activity.updatePlayStateButtonImage();
		
		return 0;
	}
	@Override
	public void setActivate( boolean bActivate )
	{
		if( bActivate )
		{
			SubControlPanel.insertToLayout(tabBaseLayout);
			NowPlayingControlPanel.insertToLayout(tabBaseLayout);
			TimeControlPanel.insertToLayout(tabBaseLayout);
			PlayControlPanel.insertToLayout(tabBaseLayout);
			rightPanel.insertToLayout(tabBaseLayout);
			leftPanel.insertToLayout(tabBaseLayout);			
		}
		super.setActivate(bActivate);
	}
	public void updateControlPanel()
	{
		if( bPanelShow )
		{
			updateControlPanel();
			// NowPlayingControlPanel.insertToLayout(tabBaseLayout);			
		}
		else
		{
			TimeControlPanel.removeToLayout(tabBaseLayout);		
			PlayControlPanel.removeToLayout(tabBaseLayout);
			SubControlPanel.removeToLayout(tabBaseLayout);
		}
		
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
        	//Log.e("action down","come");
        	bPanelShow = !bPanelShow;
        	updateControlPanel();
            break;
        }
		
		return false;
	}
	
}
