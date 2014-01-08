package okosama.app.tab;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
		tabBaseLayout.setBackgroundResource(R.color.gradiant_blue);
		// メディアの場所トグルボタン
		// external
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
