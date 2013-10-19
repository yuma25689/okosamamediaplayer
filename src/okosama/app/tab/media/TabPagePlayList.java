package okosama.app.tab.media;

import okosama.app.AppStatus;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.behavior.PlaylistListBehavior;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.tab.Tab;
import okosama.app.tab.TabComponentPropertySetter;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.tab.TabPage;
import okosama.app.widget.List;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * ���y�Đ��^�u
 * @author 25689
 *
 */
public class TabPagePlayList extends TabPage {

	public TabPagePlayList( Tab parent, LinearLayout ll, ViewGroup rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		// �R���X�g���N�^�ł��̃^�u�̃^�uID��ݒ�
		this.tabId = TABPAGE_ID_PLAYLIST;
		
		create(R.layout.tab_layout_content_generic);
		// componentContainer.addView(tabButton.getView());
	}
	/* (non-Javadoc)
	 * @see okosama.app.container.ITabComponent#create()
	 */
	@Override
	public int create(int panelLayoutID) {
		
		resetPanelViews( panelLayoutID );
		RelativeLayout.LayoutParams lp 
		= OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
        		0, 0
        );
		tabBaseLayout.setLayoutParams(lp);
		
		//////////////////// list //////////////////////////
		TabComponentPropertySetter creationData[] = {
			// ------------- LIST
			new TabComponentPropertySetter(
				List.LISTID_PLAYLIST, ComponentType.LIST_PLAYLIST, 
				//0, 260, 480, 599
				0, 0//150 + 2 // + 90
				, 480, AppStatus.LIST_HEIGHT_1//637 + 70 //- 90//599
				, null, null,//R.drawable.tab_4_list_bk,
				"", ScaleType.FIT_XY
			)
		};
		List lsts[] = {
			DroidWidgetKit.getInstance().MakeList( new PlaylistListBehavior() )
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
		for( List lst : lsts )
		{
			lst.acceptConfigurator(creationData[i]);
			// TODO:�{�^���̃A�N�V������ݒ�
			
			// lst.getView().setBackgroundColor(Color.MAGENTA);
			
			// �{�^�������̃^�u�q���ڂƂ��Ēǉ�
			// addChild( creationData[i].getInternalID(), lst );
			tabBaseLayout.addView( lst.getView() );
			
			// �{�^����z�u
			// ����́AsetActivate�ōs��
			// componentContainer.addView( btn.getView() );
			i++;
		}
		
		//////////////////////// image /////////////////////

		return 0;
	}
}
