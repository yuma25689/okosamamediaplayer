package okosama.app.tab;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.action.IViewAction;
import okosama.app.action.TabSelectAction;
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
	TabPageMedia( Tab parent, LinearLayout ll, RelativeLayout rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		this.name = OkosamaMediaPlayerActivity.tabNameMedia;
		// �R���X�g���N�^�ł��̃^�u�̃^�uID��ݒ�
		this.tabId = TABPAGE_ID_MEDIA;
		create();
		componentContainer.addView(tabButton.getView());
		componentContainer.addView(toggleEx.getView());
		// componentContainer.addView(toggleIn.getView());
	}
	@Override
	public int create() {
		// �^�u�̃{�^�������͂����ō��H
		tabButton = DroidWidgetKit.getInstance().MakeButton();
		OkosamaMediaPlayerActivity.getResourceAccessor().commonBtns.add(tabButton);
		// TAB_BUTTON
		TabComponentPropertySetter tabBtnCreationData
		= new TabComponentPropertySetter(
			"mediaTabBtn", ComponentType.BUTTON,
			120, 40, 100, 100,
			null, R.drawable.music_choice_button_image,
			"", ScaleType.FIT_XY
		);
		tabButton.acceptConfigurator(tabBtnCreationData);
		// ���f�B�A�̏ꏊ�g�O���{�^��
		// external
		toggleEx = DroidWidgetKit.getInstance().MakeToggleButton();
		TabComponentPropertySetter externalBtnCreationData
		= new TabComponentPropertySetter(
			"externalToggleBtn", ComponentType.TOGGLEBUTTON,
			50, 155 + 2, 80, 80,
			null, R.drawable.external_btn_image,
			"", ScaleType.FIT_XY
		);
		toggleEx.acceptConfigurator(externalBtnCreationData);
		/*
		// internal
		toggleIn = DroidWidgetKit.getInstance().MakeToggleButton();
		TabComponentPropertySetter internalBtnCreationData
		= new TabComponentPropertySetter(
			"externalToggleBtn", ComponentType.TOGGLEBUTTON,
			200, 155 + 2, 80, 80,
			null, R.drawable.internal_btn_image,
			"", ScaleType.FIT_XY
		);
		toggleIn.acceptConfigurator(internalBtnCreationData);
		*/
		
		// MediaTab�{�^���̃A�N�V����
		SparseArray< IViewAction > actMapTemp
			= new SparseArray< IViewAction >();
		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( parent, tabId ) );
		TabComponentActionSetter actionSetter = new TabComponentActionSetter( actMapTemp );	
		tabButton.acceptConfigurator(actionSetter);

		// toggle�̃A�N�V����
		SparseArray< IViewAction > actMapTemp2
		= new SparseArray< IViewAction >();
		actMapTemp2.put( IViewAction.ACTION_ID_ONTOGGLEON, new ToggleChangeAction( ToggleChangeAction.TOGGLE_ID_EXTERNAL, true ) );
		actMapTemp2.put( IViewAction.ACTION_ID_ONTOGGLEOFF, new ToggleChangeAction( ToggleChangeAction.TOGGLE_ID_EXTERNAL, false ) );
		actionSetter = new TabComponentActionSetter( actMapTemp2 );	
		toggleEx.acceptConfigurator(actionSetter);

		/*
		// toggle�̃A�N�V����
		SparseArray< IViewAction > actMapTemp3
		= new SparseArray< IViewAction >();
		actMapTemp3.put( IViewAction.ACTION_ID_ONTOGGLEON, new ToggleChangeAction( ToggleChangeAction.TOGGLE_ID_INTERNAL, true ) );
		actMapTemp3.put( IViewAction.ACTION_ID_ONTOGGLEOFF, new ToggleChangeAction( ToggleChangeAction.TOGGLE_ID_INTERNAL, true ) );
		actionSetter = new TabComponentActionSetter( actMapTemp3 );	
		toggleIn.acceptConfigurator(actionSetter);
		 */
		
		tabContent = OkosamaMediaPlayerActivity.createMediaTab(pageContainer, componentContainer);//new TabMediaSelect(pageContainer, componentContainer);
		// tabContent.create();
        // �^�u�y�[�W��None��
		tabContent.setCurrentTab(TabPage.TABPAGE_ID_NONE, false);
		
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
			// �^�u�{�^�����u���v���Ȏ��̕\���ɂ���
			tabButton.setEnabled( false );
			toggleEx.setEnabled(true);
			//toggleIn.setEnabled(true);
			toggleEx.setVisible(true);
			//toggleIn.setVisible(true);
			
			// TODO:�w�i�C���[�W��ݒ肷��
			// pageContainer.setBackgroundDrawable(null);
			// �^�u�y�[�W��������
			tabContent.setActiveFlg( true );	// setActivate��setActiveFlg���ł��Ă��܂����͕̂s�{�ӂ����d���Ȃ�
			// ����:���f�B�A�^�u�Ƃ����͉̂ˋ�̃^�u�ł����Ȃ��̂ŁA��������ID�ɂ͂ł��Ȃ�
			// ���f�B�A�^�u���I�����ꂽ��A�����ł��̎q�ƂȂ�^�u�̂����ꂩ�����݂̉��ID�ɂ���
			int iTabId = OkosamaMediaPlayerActivity.getCurrentDisplayId(OkosamaMediaPlayerActivity.tabNameMedia);
			if( TabPage.TABPAGE_ID_NONE == iTabId
			|| TabPage.TABPAGE_ID_UNKNOWN == iTabId)
			{
				//TabSelectAction action = new TabSelectAction(tabContent, TabPage.TABPAGE_ID_ARTIST);
				tabContent.setCurrentTab(TabPage.TABPAGE_ID_ARTIST, true);
				//action.doAction(null);
			}
			else
			{
				tabContent.setCurrentTab(iTabId, true);
			}
		}
		else
		{
			// �^�u���A�N�e�B�u�ł͂Ȃ��Ȃ����ꍇ
			// �^�u�{�^�����u�L�v���Ȏ��̕\���ɂ���
			tabButton.setEnabled( true );
			toggleEx.setVisible(false);
			toggleEx.setEnabled(false);
			//toggleIn.setVisible(false);
			//toggleIn.setEnabled(false);
			// �w�i�C���[�W������
			// �K�v�Ȃ��H
			// pageContainer.setBackgroundDrawable(null);
			// �^�u�y�[�W��������
			tabContent.setActiveFlg( false );	// setActivate��setActiveFlg���ł��Ă��܂����͕̂s�{�ӂ����d���Ȃ�			
	        // TODO:�{���́A�O��l��A���M�l�����Č��߂�
			tabContent.setCurrentTab(TabPage.TABPAGE_ID_NONE, false);
		}
		// TabComponentParent��setActivate�ŁA�S�Ă̎q�N���X��setActivate�����s�����
        super.setActivate( bActivate );
        if( toggleEx != null && toggleEx.getView() != null )
        {
        	toggleEx.getView().bringToFront();
        }
	}
}
