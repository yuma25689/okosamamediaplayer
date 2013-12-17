package okosama.app.tab.media;

import java.util.ArrayList;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.behavior.TrackListBehavior;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.panel.MoveTabInfo;
import okosama.app.tab.Tab;
import okosama.app.tab.TabComponentPropertySetter;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.tab.TabPage;
import okosama.app.widget.List;
import okosama.app.widget.absWidget;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 曲ライブラリタブ
 * @author 25689
 *
 */
public class TabPageSong extends TabPage {

	/**
	 * コンストラクタ
	 * @param parent 親ウィンドウ
	 * @param ll
	 * @param rl
	 */
	public TabPageSong( Tab parent, LinearLayout ll, ViewGroup rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		this.tabId = TABPAGE_ID_SONG;
		
		create(R.layout.tab_layout_content_generic_progress);
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
		mti.setTabPageId(TabPage.TABPAGE_ID_ARTIST);
		mti.setPanelId(R.id.left_move_panel);
		mti.setImageViewId(R.id.left_move_image);
		mti.setTabImageResId(R.drawable.artisttabbtn_normal);
		arrMti.add(mti);
		// 右フリック時の設定
		MoveTabInfo mtiR = new MoveTabInfo();
		mtiR.setTabInfoIndex( MoveTabInfo.RIGHT_1 );
		mtiR.setTabId(ControlIDs.TAB_ID_MEDIA);
		mtiR.setTabPageId(TabPage.TABPAGE_ID_PLAYLIST);
		mtiR.setPanelId(R.id.right_move_panel);
		mtiR.setImageViewId(R.id.right_move_image);
		mtiR.setTabImageResId(R.drawable.playlisttabbtn_normal);
		arrMti.add(mtiR);
				
		// パネルの作成
		resetPanelViews( panelLayoutID, arrMti );
		RelativeLayout.LayoutParams lp 
		= OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
        		0, 0
        );
		// ステータスバーの取得、アイコンの設定
		View v = tabBaseLayout.findViewById(R.id.top_info_bar);
	    ImageView icon = (ImageView) v.findViewById(R.id.icon);
	    BitmapDrawable albumIcon 
	    =  (BitmapDrawable)OkosamaMediaPlayerActivity.getResourceAccessor().getResourceDrawable(
	    		R.drawable.filter_normal );
	    albumIcon.setFilterBitmap(false);
	    albumIcon.setDither(false);
	    icon.setBackgroundDrawable(albumIcon);
		tabBaseLayout.setLayoutParams(lp);
		
		updateProgressPanel = (ViewGroup)tabBaseLayout.findViewById(R.id.TabCommonProgressPanel ); 
		//////////////////// list //////////////////////////
		// パネルにのせるリストの位置の設定
		RelativeLayout.LayoutParams lpList 
		= new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT
        );
		lpList.addRule(RelativeLayout.BELOW,R.id.top_info_bar);
		
		TabComponentPropertySetter creationData[] = {
			// ------------- TAB
			new TabComponentPropertySetter(
				List.LISTID_SONG, this, ComponentType.LIST_SONG, 
				lpList
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
		// 多分、ここで戻してはいけない
		// OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getTrackAdp().setFilterType(TrackListRawAdapter.FILTER_NORMAL);
		super.setActivate(bActivate);
	}
}
