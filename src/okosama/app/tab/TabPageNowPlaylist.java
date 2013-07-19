package okosama.app.tab;

import java.util.HashMap;

import okosama.app.AppStatus;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.action.IViewAction;
import okosama.app.action.TabSelectAction;
import okosama.app.behavior.TrackListBehavior;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.widget.List;
import android.graphics.Color;
import android.util.SparseArray;
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
		// コンストラクタでこのタブのタブIDを設定
		this.tabId = TABPAGE_ID_NOW_PLAYLIST;
		create();
		componentContainer.addView(tabButton.getView());
	}
	@Override
	public int create() {
		// タブのボタンだけはここで作る？
		tabButton = DroidWidgetKit.getInstance().MakeButton();
		OkosamaMediaPlayerActivity.getResourceAccessor().commonBtns.add(tabButton);
		// TAB_BUTTON
		TabComponentPropertySetter tabBtnCreationData
		= new TabComponentPropertySetter(
			"playlistTabBtn", ComponentType.BUTTON,
			230, 40, 100, 100,
			null, R.drawable.now_playlist_button_image,
			"", ScaleType.FIT_XY
		);
		tabButton.acceptConfigurator(tabBtnCreationData);
		// NowPlaylistTabボタンのアクション
		SparseArray< IViewAction > actMapTemp
			= new SparseArray< IViewAction >();
		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( parent, tabId ) );
		TabComponentActionSetter actionSetter = new TabComponentActionSetter( actMapTemp );	
		tabButton.acceptConfigurator(actionSetter);

		//////////////////// list //////////////////////////
		TabComponentPropertySetter creationData[] = {
			// ------------- TAB
			new TabComponentPropertySetter(
				List.LISTNAME_NOW_PLAYLIST, ComponentType.LIST_NOWPLAYLIST, 
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
		// ボタンを作成、位置を合わせ、アクションを設定し、レイアウトに配置
		int i=0;
		for( List lst : lsts )
		{
			lst.acceptConfigurator(creationData[i]);
			// TODO:ボタンのアクションを設定
			
			
			lst.getView().setBackgroundColor(Color.DKGRAY);
			// ボタンをこのタブ子項目として追加
			addChild( lst );
			// ボタンを配置
			// これは、setActivateで行う
			// componentContainer.addView( btn.getView() );
			i++;
		}		
		return 0;
	}
	/**
	 * Activeかどうかを設定。
	 * @param bActivate
	 */
	@Override
	public void setActivate( boolean bActivate )
	{
		if( bActivate )
		{
			// タブがアクティブ化された場合
			// =メディアタブが選択された場合？
			// タブボタンを「無」効な時の表示にする
			tabButton.setEnabled( false );
			// pageContainer.setBackgroundColor(Color.rgb(100, 120, 140));

			// TODO:背景イメージを設定する
			// pageContainer.setBackgroundDrawable(null);
			// タブページを初期化
			// tabContent.setActiveFlg( true );	// setActivateとsetActiveFlgができてしまったのは不本意だが仕方ない
		}
		else
		{
			// タブがアクティブではなくなった場合
			// タブボタンを「有」効な時の表示にする
			tabButton.setEnabled( true );
			// 背景イメージを消す
			// 必要なし？
			// pageContainer.setBackgroundDrawable(null);
			// タブページを初期化
			// tabContent.setActiveFlg( false );	// setActivateとsetActiveFlgができてしまったのは不本意だが仕方ない
		}
		// TabComponentParentのsetActivateで、全ての子クラスのsetActivateが実行される
        super.setActivate( bActivate );
	}

}
