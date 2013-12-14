package okosama.app.tab;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.action.IViewAction;
import okosama.app.action.ToggleChangeAction;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.widget.ToggleButton;
import android.util.SparseArray;
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
	public void resetTabContent()
	{
		clearChild();
		tabContent = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getTabStocker().createMediaTab(
				pageContainer, tabBaseLayout);
	}
	// ToggleButton toggleEx;
	//ToggleButton toggleIn;
	public TabPageMedia( Tab parent, LinearLayout ll, RelativeLayout rl ) {
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

		resetPanelViews( panelLayoutID );
		RelativeLayout.LayoutParams lp 
		= OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
        		0, 0
        );
		tabBaseLayout.setLayoutParams(lp);
		tabBaseLayout.setBackgroundResource(R.color.gradiant_test2);
		// メディアの場所トグルボタン
		// external
//		toggleEx = DroidWidgetKit.getInstance().MakeToggleButton();
//		TabComponentPropertySetter externalBtnCreationData
//		= new TabComponentPropertySetter(
//			ControlIDs.EXTERNAL_TAB_BUTTON, this, ComponentType.TOGGLEBUTTON,
//			50, 155 + 2, 80, 80,
//			null, R.drawable.external_btn_image,
//			"", ScaleType.FIT_XY
//		);
//		toggleEx.acceptConfigurator(externalBtnCreationData);
		
		// toggleのアクション
//		SparseArray< IViewAction > actMapTemp2
//		= new SparseArray< IViewAction >();
//		actMapTemp2.put( IViewAction.ACTION_ID_ONTOGGLEON, new ToggleChangeAction( ToggleChangeAction.TOGGLE_ID_EXTERNAL, true ) );
//		actMapTemp2.put( IViewAction.ACTION_ID_ONTOGGLEOFF, new ToggleChangeAction( ToggleChangeAction.TOGGLE_ID_EXTERNAL, false ) );
//		TabComponentActionSetter actionSetter = new TabComponentActionSetter( actMapTemp2 );	
//		toggleEx.acceptConfigurator(actionSetter);

		// tabBaseLayout.addView( toggleEx.getView() );
		// このパネルにTabMediaSelectが追加される
		// このパネルが表示されるとき、TabMediaSelectが表示される
//		tabContent = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getTabStocker().createMediaTab(
//				pageContainer, tabBaseLayout);
		resetTabContent();
		return 0;
	}
	/**
	 * Activeかどうかを設定。
	 * @param bActivate
	 */
	@Override
	public void setActivate( boolean bActivate )
	{
//		if( bActivate )
//		{
//			// タブがアクティブ化された場合
//			// =メディアタブが選択された場合？
//			toggleEx.setEnabled(true);
//			toggleEx.setVisible(true);			
//		}
//		else
//		{
//			// タブがアクティブではなくなった場合
//			// タブボタンを「有」効な時の表示にする
////			tabButton.setEnabled( true );
//			toggleEx.setVisible(false);
//			toggleEx.setEnabled(false);
//		}
		// TabComponentParentのsetActivateで、全ての子クラスのsetActivateが実行される
        super.setActivate( bActivate );
//        if( toggleEx != null && toggleEx.getView() != null )
//        {
//        	toggleEx.getView().bringToFront();
//        }
	}
}
