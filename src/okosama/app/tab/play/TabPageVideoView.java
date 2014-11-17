package okosama.app.tab.play;

import java.util.ArrayList;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.panel.MoveTabInfo;
import okosama.app.panel.PlayControlPanel;
import okosama.app.panel.SubControlPanel;
import okosama.app.panel.TabMoveLeftInfoPanel;
import okosama.app.panel.TabMoveRightInfoPanel;
import okosama.app.panel.TimeControlPanel;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class TabPageVideoView extends TabPage implements OnTouchListener {

	boolean bPanelShow = true;
	Tab tabContent;
	public TabPageVideoView( Tab parent, LinearLayout ll, RelativeLayout rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		// �R���X�g���N�^�ł��̃^�u�̃^�uID��ݒ�
		this.tabId = TABPAGE_ID_VIDEO_VIEW;
		create(R.layout.tab_layout_content_generic_flickable );
	}
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
		mti.setTabImageResId(R.drawable.playlist_normal);
		arrMti.add(mti);
		// �E�t���b�N���̐ݒ�
		MoveTabInfo mtiR = new MoveTabInfo();
		mtiR.setImageVertialAlign( MoveTabInfo.VERTIAL_TOP );
		mtiR.setTabInfoIndex( MoveTabInfo.RIGHT_1 );
		mtiR.setTabId(ControlIDs.TAB_ID_MAIN);
		mtiR.setTabPageId(TabPage.TABPAGE_ID_MEDIA);
		mtiR.setPanelId(R.id.right_move_panel);
		mtiR.setImageViewId(R.id.right_move_image);
		mtiR.setTabImageResId(R.drawable.music_choice_normal);
		arrMti.add(mtiR);
						
		resetPanelViews( panelLayoutID, arrMti );
		RelativeLayout.LayoutParams lp 
		= OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
        		0, 0
        );
		tabBaseLayout.setLayoutParams(lp);
		tabBaseLayout.setBackgroundResource(R.color.gradiant_red);
		
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

		rightPanel = new TabMoveRightInfoPanel(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
		leftPanel = new TabMoveLeftInfoPanel(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
		
		rightPanel.createInstance(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
		leftPanel.createInstance(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
		rightPanel.insertToLayout(tabBaseLayout);
		leftPanel.insertToLayout(tabBaseLayout);
		
		return 0;
	}
	
	public void updateControlPanel()
	{
		if( bPanelShow )
		{
			SubControlPanel.insertToLayout(tabBaseLayout);
			TimeControlPanel.insertToLayout(tabBaseLayout);
			PlayControlPanel.insertToLayout(tabBaseLayout);
			rightPanel.insertToLayout(tabBaseLayout);
			leftPanel.insertToLayout(tabBaseLayout);			
			// NowPlayingControlPanel.insertToLayout(tabBaseLayout);			
		}
		else
		{
			TimeControlPanel.removeFromParent();//ToLayout(tabBaseLayout);		
			PlayControlPanel.removeFromParent();//ToLayout(tabBaseLayout);
			SubControlPanel.removeFromParent();//ToLayout(tabBaseLayout);
		}
		
	}
	
	@Override
	public void setActivate( boolean bActivate )
	{
		if( bActivate )
		{
			updateControlPanel();
		}
		super.setActivate(bActivate);
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
        	//LogWrapper.e("action down","come");
        	bPanelShow = !bPanelShow;
        	updateControlPanel();
            break;
        }
		
		return false;
	}
	
}
