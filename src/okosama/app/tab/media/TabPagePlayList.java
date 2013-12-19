package okosama.app.tab.media;

import java.util.ArrayList;

import okosama.app.ControlDefs;
import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.behavior.PlaylistListBehavior;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.panel.MoveTabInfo;
import okosama.app.panel.TabMoveLeftInfoPanel;
import okosama.app.panel.TabMoveRightInfoPanel;
import okosama.app.tab.Tab;
import okosama.app.tab.TabComponentPropertySetter;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.tab.TabPage;
import okosama.app.widget.List;
import okosama.app.widget.absWidget;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 音楽再生タブ
 * @author 25689
 *
 */
public class TabPagePlayList extends TabPage {

	public TabPagePlayList( Tab parent, LinearLayout ll, ViewGroup rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		// コンストラクタでこのタブのタブIDを設定
		this.tabId = TABPAGE_ID_PLAYLIST;
		
		create(R.layout.tab_layout_content_generic_progress);
		// componentContainer.addView(tabButton.getView());
	}
	/* (non-Javadoc)
	 * @see okosama.app.container.ITabComponent#create()
	 */
	@Override
	public int create(int panelLayoutID) {
		// フリック入力対応
		ArrayList<MoveTabInfo> arrMti = new ArrayList<MoveTabInfo>();
		// 左フリック時の設定
		MoveTabInfo mti = new MoveTabInfo();
		mti.setTabInfoIndex( MoveTabInfo.LEFT_1 );
		mti.setTabId(ControlIDs.TAB_ID_MEDIA);
		mti.setTabPageId(TabPage.TABPAGE_ID_SONG);
		mti.setPanelId(R.id.left_move_panel);
		mti.setImageViewId(R.id.left_move_image);
		mti.setTabImageResId(R.drawable.songtabbtn_normal);
		arrMti.add(mti);
		// 右フリック時の設定
		MoveTabInfo mtiR = new MoveTabInfo();
		mtiR.setTabInfoIndex( MoveTabInfo.RIGHT_1 );
		mtiR.setTabId(ControlIDs.TAB_ID_MEDIA);
		mtiR.setTabPageId(TabPage.TABPAGE_ID_VIDEO);
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
		updateProgressPanel = (ViewGroup)tabBaseLayout.findViewById(R.id.TabCommonProgressPanel ); 
		
		//////////////////// list //////////////////////////
		TabComponentPropertySetter creationData[] = {
			// ------------- LIST
			new TabComponentPropertySetter(
				List.LISTID_PLAYLIST, this, ComponentType.LIST_PLAYLIST, 
				//0, 260, 480, 599
				0, 0//150 + 2 // + 90
				, 480, ControlDefs.LIST_HEIGHT_1//637 + 70 //- 90//599
				, null, null,//R.drawable.tab_4_list_bk,
				"", ScaleType.FIT_XY
			)
		};
		List lst = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getList(List.LISTID_PLAYLIST);
		if( lst == null )
		{
			lst = DroidWidgetKit.getInstance().MakeList( new PlaylistListBehavior() );
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().setList(List.LISTID_PLAYLIST,lst);
		
			// ボタンを作成、位置を合わせ、アクションを設定し、レイアウトに配置
			widgets.add(lst);
		}
		int i=0;
		
		for( absWidget widget : widgets )
		{
			widget.acceptConfigurator(creationData[i]);
			// TODO:ボタンのアクションを設定
			
			widget.getView().setBackgroundColor(Color.MAGENTA);
			
			// ボタンをこのタブ子項目として追加
			// addChild( creationData[i].getInternalID(), lst );
			tabBaseLayout.addView( widget.getView() );

			rightPanel = new TabMoveRightInfoPanel(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
			leftPanel = new TabMoveLeftInfoPanel(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
			rightPanel.createInstance(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
			leftPanel.createInstance(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
			rightPanel.insertToLayout(tabBaseLayout);
			leftPanel.insertToLayout(tabBaseLayout);
			
			// ボタンを配置
			// これは、setActivateで行う
			// componentContainer.addView( btn.getView() );
			i++;
		}
		
		//////////////////////// image /////////////////////

		return 0;
	}
}
