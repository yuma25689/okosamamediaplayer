package okosama.app.tab;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
//import android.view.View;

/**
 * ���f�B�A�I���^�u
 * ���̃^�u�̉��ɁA����Ƀ^�u���̂���
 * @author 25689
 *
 */
public class TabPagePlay2 extends TabPage {

	Tab tabContent;
	public Tab getTabContent()
	{
		return tabContent;
	}
	// ToggleButton toggleEx;
	//ToggleButton toggleIn;
	public TabPagePlay2( Tab parent, LinearLayout ll, RelativeLayout rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		this.internalID = ControlIDs.TAB_ID_PLAY;
		// �R���X�g���N�^�ł��̃^�u�̃^�uID��ݒ�
		this.tabId = TABPAGE_ID_PLAY_SUB;
		create(R.layout.tab_layout_content_generic);
	}
	@Override
	public int create(int panelLayoutID) {

		resetPanelViews( panelLayoutID );
		RelativeLayout.LayoutParams lp 
		= OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
        		0, 0
        );
		tabBaseLayout.setLayoutParams(lp);
		tabBaseLayout.setBackgroundResource(R.color.gradiant_tab_base);

		tabContent = OkosamaMediaPlayerActivity.getResourceAccessor()
				.getActivity().getTabStocker().createPlayTab(
				pageContainer, tabBaseLayout
		);
		
		return 0;
	}
	@Override
	public void setActivate( boolean bActivate )
	{
		super.setActivate(bActivate);
		if( bActivate )
		{
			TabPagePlay tabPlay = (TabPagePlay) tabContent.getChild(TABPAGE_ID_PLAY_SUB);
			tabPlay.updateAlbumArtOnThePlayTab();
			tabPlay.updateControlPanelPlay(true);
//			OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
//			act.getControllerShowHideBtn().getView().setVisibility(View.GONE);
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().selectTab(TabPage.TABPAGE_ID_PLAY,TABPAGE_ID_PLAY_SUB,false);			
		}
	}	
}
