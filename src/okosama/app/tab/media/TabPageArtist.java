package okosama.app.tab.media;

import java.util.HashMap;

import okosama.app.R;
import okosama.app.action.IViewAction;
import okosama.app.action.TabSelectAction;
import okosama.app.behavior.ArtistListBehavior;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.tab.Tab;
import okosama.app.tab.TabComponentActionSetter;
import okosama.app.tab.TabComponentPropertySetter;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.tab.TabPage;
import okosama.app.widget.ExpList;
import android.graphics.Color;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * ���y�Đ��^�u
 * @author 25689
 *
 */
public class TabPageArtist extends TabPage {

	public TabPageArtist( Tab parent, LinearLayout ll, RelativeLayout rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		// �R���X�g���N�^�ł��̃^�u�̃^�uID��ݒ�
		this.tabId = TABPAGE_ID_ARTIST;
		
		create();
		componentContainer.addView(tabButton.getView());
	}
	/* (non-Javadoc)
	 * @see okosama.app.container.ITabComponent#create()
	 */
	@Override
	public int create() {
		// �^�u�̃{�^�������͂����ō��H
		tabButton = DroidWidgetKit.getInstance().MakeButton();
		// TAB_BUTTON
		TabComponentPropertySetter tabBtnCreationData = new TabComponentPropertySetter(
			"artisttabbtn", ComponentType.BUTTON, 
			//0, 859 - 150 + 2, 90, 70,
			90 + 5, 859 - 70, 90, 70,
			R.drawable.music_select_artist_image,
			R.drawable.tab2_btn_select_2, 
			"", ScaleType.FIT_XY 
		);
		tabButton.acceptConfigurator(tabBtnCreationData);

		// ---- action
		HashMap< Integer, IViewAction > actMapTemp 
			= new HashMap< Integer, IViewAction >();
		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( parent, tabId ) );
		TabComponentActionSetter actionSetter = new TabComponentActionSetter( actMapTemp );			
		tabButton.acceptConfigurator(actionSetter);
		
		//////////////////// list //////////////////////////
		TabComponentPropertySetter creationData[] = {
			// ------------- LIST
			new TabComponentPropertySetter(
				ExpList.LISTNAME_ARTIST, ComponentType.LIST_ARTIST, 
				//0, 260, 480, 599
				0, 150 + 2 + 90, 480, 637 - 90//599
				, null, null,//R.drawable.tab_2_list_bk
				"", ScaleType.FIT_XY
			)
		};
		ExpList lsts[] = {
			DroidWidgetKit.getInstance().MakeExpList( new ArtistListBehavior() )
//			,DroidWidgetKit.getInstance().MakeButton()
//			,DroidWidgetKit.getInstance().MakeButton()
//			,DroidWidgetKit.getInstance().MakeButton()
//			,DroidWidgetKit.getInstance().MakeButton()
//			,DroidWidgetKit.getInstance().MakeButton()
//			,DroidWidgetKit.getInstance().MakeButton()
//			,DroidWidgetKit.getInstance().MakeButton()
		};
		// �{�^�����쐬�A�ʒu�����킹�A�A�N�V������ݒ肵�A���C�A�E�g�ɔz�u
		int i=0;
		for( ExpList lst : lsts )
		{
			lst.acceptConfigurator(creationData[i]);
			// TODO:�{�^���̃A�N�V������ݒ�
			
			lst.getView().setBackgroundColor(Color.CYAN);
			
			// �{�^�������̃^�u�q���ڂƂ��Ēǉ�
			addChild( lst );
			// �{�^����z�u
			// ����́AsetActivate�ōs��
			// componentContainer.addView( btn.getView() );
			i++;
		}
		
		//////////////////////// image /////////////////////

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
			// �^�u�{�^�����u���v���Ȏ��̕\���ɂ���
			tabButton.setEnabled( false );
			// �w�i�C���[�W�������H
//			if( pageContainer.getBackground() != null )
//			{
//				pageContainer.getBackground().setCallback(null);
//			}			
			// �w�i�C���[�W��ݒ肷��
//			pageContainer.setBackgroundDrawable(
//				OkosamaMediaPlayerActivity.res.getResourceDrawable(
//					R.drawable.tab_1_select_2
//				)
//				// getResources().getDrawable(R.drawable.background_2)
//			);
		}
		else
		{
			// �^�u���A�N�e�B�u�ł͂Ȃ��Ȃ����ꍇ
			// �^�u�{�^�����u�L�v���Ȏ��̕\���ɂ���
			tabButton.setEnabled( true );
			// �w�i�C���[�W������
			// �K�v�Ȃ��H
			// pageContainer.setBackgroundDrawable(null);
		}
		// �e�^�u�������Ȃ�΁A�\��������
		// ���̃^�u�̏ꍇ�AActive�Ƃ̈Ⴂ���K�v
		// TODO:�����ł�邩�ǂ����͔���
		tabButton.setVisible(parent.isActive());
		
		// TabComponentParent��setActivate�ŁA�S�Ă̎q�N���X��setActivate�����s�����
        super.setActivate( bActivate );
	}
}
