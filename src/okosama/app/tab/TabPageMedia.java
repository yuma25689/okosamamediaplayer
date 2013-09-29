package okosama.app.tab;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.action.IViewAction;
import okosama.app.action.TabSelectAction;
import okosama.app.action.ToggleChangeAction;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.widget.ToggleButton;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

/**
 * メディア選択タブ
 * このタブの下に、さらにタブをのせる
 * @author 25689
 *
 */
public class TabPageMedia extends TabPage {

	Tab tabContent;
	public Tab getTabContent()
	{
		return tabContent;
	}
	ToggleButton toggleEx;
	//ToggleButton toggleIn;
	TabPageMedia( Tab parent, LinearLayout ll, RelativeLayout rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		this.internalID = ControlIDs.TAB_ID_MEDIA;
		// コンストラクタでこのタブのタブIDを設定
		this.tabId = TABPAGE_ID_MEDIA;
		create(R.layout.tab_layout_content_generic);
		// componentContainer.addView(tabButton.getView());
		// componentContainer.addView(toggleEx.getView());
		// componentContainer.addView(toggleIn.getView());
	}
	@Override
	public int create(int panelLayoutID) {

		OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		LayoutInflater inflator = act.getLayoutInflater();
		tabBaseLayout = (ViewGroup)inflator.inflate(panelLayoutID, null, false);

		// メディアの場所トグルボタン
		// external
		toggleEx = DroidWidgetKit.getInstance().MakeToggleButton();
		TabComponentPropertySetter externalBtnCreationData
		= new TabComponentPropertySetter(
			ControlIDs.EXTERNAL_TAB_BUTTON, ComponentType.TOGGLEBUTTON,
			50, 155 + 2, 80, 80,
			null, R.drawable.external_btn_image,
			"", ScaleType.FIT_XY
		);
		toggleEx.acceptConfigurator(externalBtnCreationData);
		
		// toggleのアクション
		SparseArray< IViewAction > actMapTemp2
		= new SparseArray< IViewAction >();
		actMapTemp2.put( IViewAction.ACTION_ID_ONTOGGLEON, new ToggleChangeAction( ToggleChangeAction.TOGGLE_ID_EXTERNAL, true ) );
		actMapTemp2.put( IViewAction.ACTION_ID_ONTOGGLEOFF, new ToggleChangeAction( ToggleChangeAction.TOGGLE_ID_EXTERNAL, false ) );
		TabComponentActionSetter actionSetter = new TabComponentActionSetter( actMapTemp2 );	
		toggleEx.acceptConfigurator(actionSetter);

		tabBaseLayout.addView( toggleEx.getView() );
		tabContent = OkosamaMediaPlayerActivity.createMediaTab(pageContainer, componentContainer);//new TabMediaSelect(pageContainer, componentContainer);
		componentContainer.addView( tabBaseLayout );
		// tabContent.create();
        // タブページをNoneに
		// tabContent.setCurrentTab(TabPage.TABPAGE_ID_NONE, false);
		
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
//			tabButton.setEnabled( false );
			toggleEx.setEnabled(true);
			//toggleIn.setEnabled(true);
			toggleEx.setVisible(true);
			//toggleIn.setVisible(true);
			
			// TODO:背景イメージを設定する
			// pageContainer.setBackgroundDrawable(null);
			// タブページを初期化
			tabContent.setActiveFlg( true );	// setActivateとsetActiveFlgができてしまったのは不本意だが仕方ない
			// 注意:メディアタブというのは架空のタブでしかないので、それを画面IDにはできない
			// メディアタブが選択されたら、ここでその子となるタブのいずれかを現在の画面IDにする
			int iTabId = OkosamaMediaPlayerActivity.getCurrentDisplayId(OkosamaMediaPlayerActivity.tabNameMedia);
			if( TabPage.TABPAGE_ID_NONE == iTabId
			|| TabPage.TABPAGE_ID_UNKNOWN == iTabId)
			{
				//TabSelectAction action = new TabSelectAction(tabContent, TabPage.TABPAGE_ID_ARTIST);
				// tabContent.setCurrentTab(TabPage.TABPAGE_ID_ARTIST, true);
				// コードが複雑になっているので、多分あまりよろしくないけど、もう一度メイン画面内部に格納されているタブの状態のみを更新
				// (画面は更新しない)
				//action.doAction(null);
			}
			else
			{
				tabContent.setCurrentTab(iTabId, true);
			}
		}
		else
		{
			// タブがアクティブではなくなった場合
			// タブボタンを「有」効な時の表示にする
//			tabButton.setEnabled( true );
			toggleEx.setVisible(false);
			toggleEx.setEnabled(false);
			//toggleIn.setVisible(false);
			//toggleIn.setEnabled(false);
			// 背景イメージを消す
			// 必要なし？
			// pageContainer.setBackgroundDrawable(null);
			// タブページを初期化
			tabContent.setActiveFlg( false );	// setActivateとsetActiveFlgができてしまったのは不本意だが仕方ない			
	        // TODO:本来は、前回値や、送信値を見て決める
			tabContent.setCurrentTab(TabPage.TABPAGE_ID_NONE, false);
		}
		// TabComponentParentのsetActivateで、全ての子クラスのsetActivateが実行される
        super.setActivate( bActivate );
        if( toggleEx != null && toggleEx.getView() != null )
        {
        	toggleEx.getView().bringToFront();
        }
	}
}
