package okosama.app.tab.media;

import okosama.app.AppStatus;
import okosama.app.R;
import okosama.app.action.IViewAction;
import okosama.app.action.TabSelectAction;
import okosama.app.behavior.TrackListBehavior;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.tab.Tab;
import okosama.app.tab.TabComponentActionSetter;
import okosama.app.tab.TabComponentPropertySetter;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.tab.TabPage;
import okosama.app.widget.List;
import android.util.SparseArray;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 音楽再生タブ
 * @author 25689
 *
 */
public class TabPageSong extends TabPage {

	public TabPageSong( Tab parent, LinearLayout ll, RelativeLayout rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		// コンストラクタでこのタブのタブIDを設定
		this.tabId = TABPAGE_ID_SONG;
		
		create();
		componentContainer.addView(tabButton.getView());
	}
	/* (non-Javadoc)
	 * @see okosama.app.container.ITabComponent#create()
	 */
	@Override
	public int create() {
		// タブのボタンだけはここで作る？
		tabButton = DroidWidgetKit.getInstance().MakeButton();
		// TAB_BUTTON
		TabComponentPropertySetter tabBtnCreationData = new TabComponentPropertySetter(
			"albumTabBtn", ComponentType.BUTTON, 
			( 120 + 5 ) * 2, 859 - 100, 120, 100,
			R.drawable.music_select_song_image,
			R.drawable.no_image, // R.drawable.tab3_btn_not_select_no_shadow2, 
			"", ScaleType.FIT_XY 
		);
		tabButton.acceptConfigurator(tabBtnCreationData);

		// ---- action
		SparseArray< IViewAction > actMapTemp 
			= new SparseArray< IViewAction >();
		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( parent, tabId ) );
		TabComponentActionSetter actionSetter = new TabComponentActionSetter( actMapTemp );			
		tabButton.acceptConfigurator(actionSetter);
		
		//////////////////// list //////////////////////////
		TabComponentPropertySetter creationData[] = {
			// ------------- TAB
			new TabComponentPropertySetter(
				List.LISTNAME_SONG, ComponentType.LIST_SONG, 
				// 0, 260, 480, 599
				0, 0//150 + 2 // + 90
				, 480, AppStatus.LIST_HEIGHT_1//637 + 70 // - 90//599
				, null, null//R.drawable.tab_3_list_bk
				, "", ScaleType.FIT_XY
			)
		};
		List lsts[] = {
			DroidWidgetKit.getInstance().MakeList( new TrackListBehavior() )
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
		for( List lst : lsts )
		{
			lst.acceptConfigurator(creationData[i]);
			// TODO:ボタンのアクションを設定
			
			
			// lst.getView().setBackgroundColor(Color.GREEN);
			// ボタンをこのタブ子項目として追加
			addChild( lst );
			// ボタンを配置
			// これは、setActivateで行う
			// componentContainer.addView( btn.getView() );
			i++;
		}
		
		//////////////////////// image /////////////////////

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
			// タブボタンを「無」効な時の表示にする
			tabButton.setEnabled( false );
			
			
			// 背景イメージを消す？
//			if( pageContainer.getBackground() != null )
//			{
//				pageContainer.getBackground().setCallback(null);
//			}
//			
//			// 背景イメージを設定する
//			pageContainer.setBackgroundDrawable(
//				OkosamaMediaPlayerActivity.res.getResourceDrawable(
//					R.drawable.tab_3_select_2
//				)
//				// getResources().getDrawable(R.drawable.background_2)
//			);
		}
		else
		{
			// タブがアクティブではなくなった場合
			// タブボタンを「有」効な時の表示にする
			tabButton.setEnabled( true );
			// 背景イメージを消す?
			// 必要なし？
			// pageContainer.setBackgroundDrawable(null);
		}
		// 親タブが無効ならば、表示を消去
		// このタブの場合、Activeとの違いが必要
		// TODO:ここでやるかどうかは微妙
		tabButton.setVisible(parent.isActive());
		
		// TabComponentParentのsetActivateで、全ての子クラスのsetActivateが実行される
        super.setActivate( bActivate );
	}
}
