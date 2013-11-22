package okosama.app.tab;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.action.IViewAction;
import okosama.app.action.ToggleChangeAction;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.widget.ToggleButton;
import android.util.SparseArray;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

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
	ToggleButton toggleEx;
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
		tabBaseLayout.setBackgroundResource(R.color.gradiant_test2);
		
		// ���f�B�A�̏ꏊ�g�O���{�^��
		// external
		toggleEx = DroidWidgetKit.getInstance().MakeToggleButton();
		TabComponentPropertySetter externalBtnCreationData
		= new TabComponentPropertySetter(
			ControlIDs.EXTERNAL_TAB_BUTTON, ComponentType.TOGGLEBUTTON,
			50, 155 + 2, 80, 80,
			null, R.drawable.external_btn_image,
			"", ScaleType.FIT_XY
		);
		toggleEx.acceptConfigurator(externalBtnCreationData);
		
		// toggle�̃A�N�V����
		SparseArray< IViewAction > actMapTemp2
		= new SparseArray< IViewAction >();
		actMapTemp2.put( IViewAction.ACTION_ID_ONTOGGLEON, new ToggleChangeAction( ToggleChangeAction.TOGGLE_ID_EXTERNAL, true ) );
		actMapTemp2.put( IViewAction.ACTION_ID_ONTOGGLEOFF, new ToggleChangeAction( ToggleChangeAction.TOGGLE_ID_EXTERNAL, false ) );
		TabComponentActionSetter actionSetter = new TabComponentActionSetter( actMapTemp2 );	
		toggleEx.acceptConfigurator(actionSetter);

		// tabBaseLayout.addView( toggleEx.getView() );
		tabContent = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getTabStocker().createMediaTab(
				pageContainer, tabBaseLayout);
		
		return 0;
	}
	/**
	 * Active���ǂ�����ݒ�B
	 * @param bActivate
	 */
	@Override
	public void setActivate( boolean bActivate )
	{
		if( bActivate )
		{
			// �^�u���A�N�e�B�u�����ꂽ�ꍇ
			// =���f�B�A�^�u���I�����ꂽ�ꍇ�H
			toggleEx.setEnabled(true);
			toggleEx.setVisible(true);
			
			// TODO:�w�i�C���[�W��ݒ肷��
			// pageContainer.setBackgroundDrawable(null);
			// �^�u�y�[�W��������
			// tabContent.setActiveFlg( true );	// setActivate��setActiveFlg���ł��Ă��܂����͕̂s�{�ӂ����d���Ȃ�
			// ����:���f�B�A�^�u�Ƃ����͉̂ˋ�̃^�u�ł����Ȃ��̂ŁA��������ID�ɂ͂ł��Ȃ�
			// ���f�B�A�^�u���I�����ꂽ��A�����ł��̎q�ƂȂ�^�u�̂����ꂩ�����݂̉��ID�ɂ���
			//int iTabId = OkosamaMediaPlayerActivity.getCurrentDisplayId(ControlIDs.TAB_ID_MEDIA);
			//if( TabPage.TABPAGE_ID_NONE == iTabId
			//|| TabPage.TABPAGE_ID_UNKNOWN == iTabId)
			//{
				//TabSelectAction action = new TabSelectAction(tabContent, TabPage.TABPAGE_ID_ARTIST);
				// tabContent.setCurrentTab(TabPage.TABPAGE_ID_ARTIST, true);
				// �R�[�h�����G�ɂȂ��Ă���̂ŁA�������܂��낵���Ȃ����ǁA������x���C����ʓ����Ɋi�[����Ă���^�u�̏�Ԃ݂̂��X�V
				// (��ʂ͍X�V���Ȃ�)
				//action.doAction(null);
//			}
//			else
//			{
//				tabContent.setCurrentTab(iTabId, true);
//			}
			// componentContainer.addView( tabBaseLayout );
			
		}
		else
		{
			// �^�u���A�N�e�B�u�ł͂Ȃ��Ȃ����ꍇ
			// �^�u�{�^�����u�L�v���Ȏ��̕\���ɂ���
//			tabButton.setEnabled( true );
			toggleEx.setVisible(false);
			toggleEx.setEnabled(false);
			//toggleIn.setVisible(false);
			//toggleIn.setEnabled(false);
			// �w�i�C���[�W������
			// �K�v�Ȃ��H
			// pageContainer.setBackgroundDrawable(null);
			// �^�u�y�[�W��������
			// tabContent.setActiveFlg( false );	// setActivate��setActiveFlg���ł��Ă��܂����͕̂s�{�ӂ����d���Ȃ�			
	        // TODO:�{���́A�O��l��A���M�l�����Č��߂�
			// tabContent.setCurrentTab(TabPage.TABPAGE_ID_NONE, false);
			// omponentContainer.removeView( tabBaseLayout );			
		}
		// TabComponentParent��setActivate�ŁA�S�Ă̎q�N���X��setActivate�����s�����
        super.setActivate( bActivate );
        if( toggleEx != null && toggleEx.getView() != null )
        {
        	toggleEx.getView().bringToFront();
        }
	}
}
