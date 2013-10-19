package okosama.app.tab;

import okosama.app.AppStatus;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.behavior.TrackListBehavior;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.widget.List;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

public class TabPageNowPlaylist extends TabPage {

	Tab tabContent;
	TabPageNowPlaylist( Tab parent, LinearLayout ll, RelativeLayout rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		// �R���X�g���N�^�ł��̃^�u�̃^�uID��ݒ�
		this.tabId = TABPAGE_ID_NOW_PLAYLIST;
		create(R.layout.tab_layout_content_generic);
//		componentContainer.addView(tabButton.getView());
	}
	@Override
	public int create(int panelLayoutID) {
		
		resetPanelViews( panelLayoutID );
		RelativeLayout.LayoutParams lp 
		= OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
        		0, 0
        );
		tabBaseLayout.setLayoutParams(lp);
		tabBaseLayout.setBackgroundResource(R.color.gradiant_test3);

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
				List.LISTID_NOW_PLAYLIST, ComponentType.LIST_NOWPLAYLIST, 
				// 0, 260, 480, 599
				0, 0,//150 + 2
				480, AppStatus.LIST_HEIGHT_1//637 //599
				, null, null//R.drawable.tab_3_list_bk
				, "", ScaleType.FIT_XY
			)
		};
		List lsts[] = {
			DroidWidgetKit.getInstance().MakeList( new TrackListBehavior() )
		};
		// �{�^�����쐬�A�ʒu�����킹�A�A�N�V������ݒ肵�A���C�A�E�g�ɔz�u
		int i=0;
		for( List lst : lsts )
		{
			lst.acceptConfigurator(creationData[i]);
			// TODO:�{�^���̃A�N�V������ݒ�
			
			
			lst.getView().setBackgroundColor(Color.DKGRAY);
			// �{�^�������̃^�u�q���ڂƂ��Ēǉ�
			// addChild( creationData[i].getInternalID(), lst );
			tabBaseLayout.addView( lst.getView() );
			// tabContent = OkosamaMediaPlayerActivity.createMediaTab(pageContainer, componentContainer);//new TabMediaSelect(pageContainer, componentContainer);
			// �{�^����z�u
			// ����́AsetActivate�ōs��
			// componentContainer.addView( btn.getView() );
			i++;
		}
		
		return 0;
	}
}
