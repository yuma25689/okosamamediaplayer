package okosama.app.tab.media;

import okosama.app.AppStatus;
import okosama.app.ControlDefs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.adapter.TrackListRawAdapter;
import okosama.app.behavior.PlaylistListBehavior;
import okosama.app.behavior.TrackListBehavior;
import okosama.app.factory.DroidWidgetKit;
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
public class TabPageSong extends TabPage {

	public TabPageSong( Tab parent, LinearLayout ll, ViewGroup rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		// コンストラクタでこのタブのタブIDを設定
		this.tabId = TABPAGE_ID_SONG;
		
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
		
		// タブのボタンだけはここで作る？
//		tabButton = DroidWidgetKit.getInstance().MakeButton();
//		// TAB_BUTTON
//		TabComponentPropertySetter tabBtnCreationData = new TabComponentPropertySetter(
//			"albumTabBtn", ComponentType.BUTTON, 
//			( 120 + 5 ) * 2, 859 - 100, 120, 100,
//			R.drawable.music_select_song_image,
//			R.drawable.no_image, // R.drawable.tab3_btn_not_select_no_shadow2, 
//			"", ScaleType.FIT_XY 
//		);
//		tabButton.acceptConfigurator(tabBtnCreationData);


		
		//////////////////// list //////////////////////////
		TabComponentPropertySetter creationData[] = {
			// ------------- TAB
			new TabComponentPropertySetter(
				List.LISTID_SONG, ComponentType.LIST_SONG, 
				// 0, 260, 480, 599
				0, 0//150 + 2 // + 90
				, 480, ControlDefs.LIST_HEIGHT_1//637 + 70 // - 90//599
				, null, null//R.drawable.tab_3_list_bk
				, "", ScaleType.FIT_XY
			)
		};
		List lst = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getList(List.LISTID_SONG);
		if( lst == null )
		{
			lst = DroidWidgetKit.getInstance().MakeList( new TrackListBehavior() );
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().setList(List.LISTID_SONG,lst);
		
			widgets.add(lst);
		}
		
		// ボタンを作成、位置を合わせ、アクションを設定し、レイアウトに配置
		int i=0;
		for( absWidget widget : widgets )
		{
			widget.acceptConfigurator(creationData[i]);
			// TODO:ボタンのアクションを設定
			
			
			widget.getView().setBackgroundColor(Color.BLUE);
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
	
	@Override
	public void setActivate( boolean bActivate )
	{
		// OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getTrackAdp().setFilterType(TrackListRawAdapter.FILTER_NORMAL);
		super.setActivate(bActivate);
	}
}
