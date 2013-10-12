package okosama.app.tab.media;

import okosama.app.AppStatus;
import okosama.app.OkosamaMediaPlayerActivity;
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
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 音楽再生タブ
 * @author 25689
 *
 */
public class TabPageArtist extends TabPage {

	public TabPageArtist( Tab parent, LinearLayout ll, ViewGroup rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		// コンストラクタでこのタブのタブIDを設定
		this.tabId = TABPAGE_ID_ARTIST;
		
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

		// タブのボタンだけはここで作る？
		//tabButton = DroidWidgetKit.getInstance().MakeButton();
		// TAB_BUTTON
//		TabComponentPropertySetter tabBtnCreationData = new TabComponentPropertySetter(
//			"artisttabbtn", ComponentType.BUTTON, 
//			//0, 859 - 150 + 2, 90, 70,
//			120 + 5, 859 - 100, 120, 100,
//			R.drawable.music_select_artist_image,
//			R.drawable.no_image,//R.drawable.tab2_btn_select_2, 
//			"", ScaleType.FIT_XY 
//		);
//		tabButton.acceptConfigurator(tabBtnCreationData);

		
		//////////////////// list //////////////////////////
		TabComponentPropertySetter creationData[] = {
			// ------------- LIST
			new TabComponentPropertySetter(
				ExpList.LISTID_ARTIST, ComponentType.LIST_ARTIST, 
				//0, 260, 480, 599
				0, 0//150 + 2 // + 90
				, 480, AppStatus.LIST_HEIGHT_1//637 + 70 // - 90//599
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
		
		// ボタンを作成、位置を合わせ、アクションを設定し、レイアウトに配置
		int i=0;
		for( ExpList lst : lsts )
		{
			lst.acceptConfigurator(creationData[i]);
			// TODO:ボタンのアクションを設定
			
			lst.getView().setBackgroundColor(Color.CYAN);
			//lst.getView().sendToBack();
			// ボタンをこのタブ子項目として追加
			// addChild( creationData[i].getInternalID(), lst );
			tabBaseLayout.addView( lst.getView() );
			// ボタンを配置
			// これは、setActivateで行う
			// componentContainer.addView( btn.getView() );
			i++;
		}
		
		//////////////////////// image /////////////////////

		return 0;
	}
}
