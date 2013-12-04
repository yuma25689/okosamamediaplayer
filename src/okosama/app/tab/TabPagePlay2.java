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
public class TabPagePlay2 extends TabPage {

	Tab tabContent;
	public Tab getTabContent()
	{
		return tabContent;
	}
	// ToggleButton toggleEx;
	//ToggleButton toggleIn;
	public TabPagePlay2( Tab parent, LinearLayout ll, RelativeLayout rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		this.internalID = ControlIDs.TAB_ID_PLAY;
		// コンストラクタでこのタブのタブIDを設定
		this.tabId = TABPAGE_ID_PLAY_SUB;
		create(R.layout.tab_layout_content_generic);
	}
	@Override
	public int create(int panelLayoutID) {

		resetPanelViews( panelLayoutID );
		RelativeLayout.LayoutParams lp 
		= OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
        		0, 0
        );
		tabBaseLayout.setLayoutParams(lp);
		tabBaseLayout.setBackgroundResource(R.color.gradiant_test3);

		tabContent = OkosamaMediaPlayerActivity.getResourceAccessor()
				.getActivity().getTabStocker().createPlayTab(
				pageContainer, tabBaseLayout
		);
		
		return 0;
	}
}
