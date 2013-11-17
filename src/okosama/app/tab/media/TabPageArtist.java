package okosama.app.tab.media;

import okosama.app.AppStatus;
import okosama.app.ControlDefs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.behavior.AlbumListBehavior;
import okosama.app.behavior.ArtistListBehavior;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.tab.Tab;
import okosama.app.tab.TabComponentPropertySetter;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.tab.TabPage;
import okosama.app.widget.ExpList;
import okosama.app.widget.List;
import okosama.app.widget.absWidget;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
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
		
		create(R.layout.tab_layout_content_generic_progress);
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
		updateProgressPanel = (ViewGroup)tabBaseLayout.findViewById(R.id.TabCommonProgressPanel ); 
		
		//////////////////// list //////////////////////////
		TabComponentPropertySetter creationData[] = {
			// ------------- LIST
			new TabComponentPropertySetter(
				ExpList.LISTID_ARTIST, ComponentType.LIST_ARTIST, 
				//0, 260, 480, 599
				0, 0//150 + 2 // + 90
				, 480, ControlDefs.LIST_HEIGHT_1//637 + 70 // - 90//599
				, null, null,//R.drawable.tab_2_list_bk
				"", ScaleType.FIT_XY
			)
		};
		ExpList lst = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getExpList(ExpList.LISTID_ARTIST);
		if( lst == null )
		{
			lst = DroidWidgetKit.getInstance().MakeExpList( new ArtistListBehavior() );
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().setExpList(ExpList.LISTID_ARTIST,lst);
			widgets.add(lst);
		}
		else
		{
			ExpandableListView view = ((ExpandableListView) lst.getView());
			view.setAdapter(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getArtistAdp());
			view.invalidateViews();
		}
		// ボタンを作成、位置を合わせ、アクションを設定し、レイアウトに配置
		int i=0;
		for( absWidget widget : widgets )
		{
			widget.acceptConfigurator(creationData[i]);
			// TODO:ボタンのアクションを設定
			
			widget.getView().setBackgroundColor(Color.CYAN);
			//lst.getView().sendToBack();
			// ボタンをこのタブ子項目として追加
			// addChild( creationData[i].getInternalID(), lst );
			tabBaseLayout.addView( widget.getView() );
			// ボタンを配置
			// これは、setActivateで行う
			// componentContainer.addView( btn.getView() );
			i++;
		}
		
		//////////////////////// image /////////////////////

		return 0;
	}
}
