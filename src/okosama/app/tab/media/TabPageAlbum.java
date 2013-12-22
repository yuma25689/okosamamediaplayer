package okosama.app.tab.media;

import java.util.ArrayList;

import okosama.app.ControlDefs;
import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.behavior.AlbumListBehavior;
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
 * アルバムライブラリタブ
 * @author 25689
 *
 */
public class TabPageAlbum extends TabPage {

	/**
	 * コンストラクタ
	 * @param parent 親
	 * @param ll 親の大元のレイアウト
	 * @param rl 親のレイアウト
	 */
	public TabPageAlbum( Tab parent, LinearLayout ll, ViewGroup rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		// コンストラクタでこのタブのタブIDを設定
		this.tabId = TABPAGE_ID_ALBUM;
		// プログレス付きのレイアウト
		create(R.layout.tab_layout_content_generic_progress);
	}
	@Override
	public int create(int panelLayoutID) {
		// フリック入力対応
		
		ArrayList<MoveTabInfo> arrMti = new ArrayList<MoveTabInfo>();
		// 左フリック時の設定
		MoveTabInfo mti = new MoveTabInfo();
		mti.setImageVertialAlign( MoveTabInfo.VERTIAL_TOP );
		mti.setTabInfoIndex( MoveTabInfo.LEFT_1 );
		mti.setTabId(ControlIDs.TAB_ID_MAIN);
		mti.setTabPageId(TabPage.TABPAGE_ID_NOW_PLAYLIST);
		mti.setPanelId(R.id.left_move_panel);
		mti.setImageViewId(R.id.left_move_image);
		mti.setTabImageResId(R.drawable.brat_main_normal);
		arrMti.add(mti);
		// 右フリック時の設定
		MoveTabInfo mtiR = new MoveTabInfo();
		mtiR.setTabInfoIndex( MoveTabInfo.RIGHT_1 );
		mtiR.setTabId(ControlIDs.TAB_ID_MEDIA);
		mtiR.setTabPageId(TabPage.TABPAGE_ID_ARTIST);
		mtiR.setPanelId(R.id.right_move_panel);
		mtiR.setImageViewId(R.id.right_move_image);
		mtiR.setTabImageResId(R.drawable.artisttabbtn_normal);
		arrMti.add(mtiR);

		// レイアウトをクリア
		resetPanelViews(panelLayoutID,arrMti);
		// パネルの位置を設定
		RelativeLayout.LayoutParams lp 
		= OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
        	0, 0, ControlDefs.APP_BASE_WIDTH, ControlDefs.APP_BASE_HEIGHT
        );
		tabBaseLayout.setLayoutParams(lp);
		
		// TODO:何やってるか調査 ???
//		View v = tabBaseLayout.findViewById(R.id.top_info_bar);
//	    ImageView icon = (ImageView) v.findViewById(R.id.icon);
//	    BitmapDrawable albumIcon 
//	    =  (BitmapDrawable)OkosamaMediaPlayerActivity.getResourceAccessor().getResourceDrawable(
//	    		android.R.drawable.divider_horizontal_dark );
//	    albumIcon.setFilterBitmap(false);
//	    albumIcon.setDither(false);
//	    icon.setBackgroundDrawable(albumIcon);
		
		// プログレスバーの設定
//		updateProgressPanel = (ViewGroup)tabBaseLayout.findViewById(R.id.TabCommonProgressPanel );
//		ProgressBar prog = (ProgressBar) updateProgressPanel.findViewById(R.id.progress_common);
//		prog.setBackgroundResource(R.drawable.empty);
		
		// パネルにのせるリストの位置の設定
		RelativeLayout.LayoutParams lpList 
		= new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT
        );
		lpList.addRule(RelativeLayout.BELOW,R.id.top_info_bar);
		lpList.addRule(RelativeLayout.RIGHT_OF,R.id.left_move_panel);
		lpList.addRule(RelativeLayout.LEFT_OF,R.id.right_move_panel);
		// リストの作成
		TabComponentPropertySetter creationData[] = {
			// リストの性質情報を設定
			new TabComponentPropertySetter(
				List.LISTID_ALBUM, this, ComponentType.LIST_ALBUM, 
				lpList
				, null, null, 
				"", ScaleType.FIT_XY
			),
		};
		// 作成済みの場合、メイン画面に既に格納してあるリストを取得
		List lst = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getList(List.LISTID_ALBUM);
		if( lst == null )
		{
			// まだ作成されていない場合、リストを作成し、メイン画面に格納する
			lst = DroidWidgetKit.getInstance().MakeList( new AlbumListBehavior() );
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().setList(
					List.LISTID_ALBUM,lst);
			widgets.add(lst);
		}
		
		// このパネルにのせた全てのwidgetの性質を設定
		creationData[0].setColorBack(Color.WHITE);
		int i=0;
		for( absWidget widget : widgets )
		{
			widget.acceptConfigurator(creationData[i]);
			
			// このタブの子項目として追加
			tabBaseLayout.addView( widget.getView() );
			i++;
		}
		// lst.getView().setOnTouchListener(new TabListViewTouchListener(0,0));
		rightPanel = new TabMoveRightInfoPanel(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
		leftPanel = new TabMoveLeftInfoPanel(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
		rightPanel.createInstance(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
		leftPanel.createInstance(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
		rightPanel.insertToLayout(tabBaseLayout);
		leftPanel.insertToLayout(tabBaseLayout);
		// Log.e("album flick setting","ok");
		
		return 0;
	}
}
