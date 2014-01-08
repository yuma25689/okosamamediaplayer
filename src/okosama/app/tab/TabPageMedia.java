package okosama.app.tab;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * ���f�B�A�I���^�u
 * ���̃^�u�̉��ɁA����Ƀ^�u���̂���
 * @author 25689
 *
 */
public class TabPageMedia extends TabPage {

	Tab tabContent;
	public Tab getTabContent()
	{
		return tabContent;
	}
	public void resetTabContent()
	{
		clearChild();
		tabContent = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getTabStocker().createMediaTab(
				pageContainer, tabBaseLayout);
	}
	// ToggleButton toggleEx;
	//ToggleButton toggleIn;
	public TabPageMedia( Tab parent, LinearLayout ll, RelativeLayout rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		this.internalID = ControlIDs.TAB_ID_MEDIA;
		// �R���X�g���N�^�ł��̃^�u�̃^�uID��ݒ�
		this.tabId = TABPAGE_ID_MEDIA;
		create(R.layout.tab_layout_content_generic);
		// componentContainer.addView(tabButton.getView());
		// componentContainer.addView(toggleEx.getView());
		// componentContainer.addView(toggleIn.getView());
	}
	@Override
	public int create(int panelLayoutID) {

		resetPanelViews( panelLayoutID );
		RelativeLayout.LayoutParams lp 
		= OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
        		0, 0
        );
		tabBaseLayout.setLayoutParams(lp);
		tabBaseLayout.setBackgroundResource(R.color.gradiant_blue);
		// ���f�B�A�̏ꏊ�g�O���{�^��
		// external
		resetTabContent();
		return 0;
	}
	/**
	 * Active���ǂ�����ݒ�B
	 * @param bActivate
	 */
	@Override
	public void setActivate( boolean bActivate )
	{
//		if( bActivate )
//		{
//			// �^�u���A�N�e�B�u�����ꂽ�ꍇ
//			// =���f�B�A�^�u���I�����ꂽ�ꍇ�H
//			toggleEx.setEnabled(true);
//			toggleEx.setVisible(true);			
//		}
//		else
//		{
//			// �^�u���A�N�e�B�u�ł͂Ȃ��Ȃ����ꍇ
//			// �^�u�{�^�����u�L�v���Ȏ��̕\���ɂ���
////			tabButton.setEnabled( true );
//			toggleEx.setVisible(false);
//			toggleEx.setEnabled(false);
//		}
		// TabComponentParent��setActivate�ŁA�S�Ă̎q�N���X��setActivate�����s�����
        super.setActivate( bActivate );
//        if( toggleEx != null && toggleEx.getView() != null )
//        {
//        	toggleEx.getView().bringToFront();
//        }
	}
}
