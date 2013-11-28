package okosama.app.tab.play;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.action.IViewAction;
import okosama.app.action.TabSelectAction;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.tab.Tab;
import okosama.app.tab.TabComponentActionSetter;
import okosama.app.tab.TabComponentPropertySetter;
import okosama.app.tab.TabPage;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.tab.TabPageNowPlaylist;
import okosama.app.tab.TabPagePlay;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

/**
 * タブを模倣したクラス。このクラスは表示を持たない。
 * タブというよりは、Mediatorに近い。
 * タブの作成および、タブ上のコンポーネントの有効/無効、表示/非表示のみを制御する
 * @author 25689
 *
 */
public class TabPlayContent extends Tab {
	static final int BUTTON_WIDTH = 90;
	static final int BUTTON_HEIGHT = 90;
	static final int HOOTER_SIZE = BUTTON_HEIGHT + Tab.HDR_SIZE;
	
	public TabPlayContent(int ID, LinearLayout ll, ViewGroup rl) {
		super(ControlIDs.TAB_ID_PLAY, ll, rl);
	}

	/**
	 * タブ全体の作成
	 * @return 0:正常 0以外:異常
	 */
	@Override
	public int create(int panelLayoutId) {
		int errCode = 0;
		OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();

		// タブのパネルを作成
		LayoutInflater inflator = act.getLayoutInflater();
		tabBaseLayout = (ViewGroup)inflator.inflate(panelLayoutId, null, false);
		// パネル位置の設定(FILL_PARENT)
		RelativeLayout.LayoutParams lp 
		= OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
        		0, 0 
        );
		tabBaseLayout.setLayoutParams(lp);
		// タブボタンを置くヘッダとなるレイアウト
		RelativeLayout rlHooter = (RelativeLayout) tabBaseLayout.findViewById(R.id.tab_hooter);
		
		mapBtn.put( TabPage.TABPAGE_ID_PLAY_SUB, DroidWidgetKit.getInstance().MakeButton() );
		TabComponentPropertySetter tabBtnCreationData = new TabComponentPropertySetter(
			ControlIDs.PLAY_TAB_BUTTON, null, ComponentType.BUTTON, 
			0, 0,
			BUTTON_WIDTH, BUTTON_HEIGHT,
			R.drawable.music_tab_button_image,
			R.drawable.no_image,
			"", ScaleType.FIT_XY 
		);
		mapBtn.get(TabPage.TABPAGE_ID_PLAY_SUB).acceptConfigurator(tabBtnCreationData);
		SparseArray< IViewAction > actMapTemp 
		= new SparseArray< IViewAction >();
		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( 
				this.getInternalID(), TabPage.TABPAGE_ID_PLAY_SUB ) );
		TabComponentActionSetter actionSetter = new TabComponentActionSetter( actMapTemp );			
		mapBtn.get(TabPage.TABPAGE_ID_PLAY_SUB).acceptConfigurator(actionSetter);
		rlHooter.addView(mapBtn.get(TabPage.TABPAGE_ID_PLAY_SUB).getView());

		// プレイリストタブ
		mapBtn.put( TabPage.TABPAGE_ID_NOW_PLAYLIST, DroidWidgetKit.getInstance().MakeButton() );
		tabBtnCreationData = new TabComponentPropertySetter(
			ControlIDs.NOW_PLAYLIST_TAB_BUTTON, null, ComponentType.BUTTON, 
			( BUTTON_WIDTH + 5 ) * 1, 0,//DroidDisplayInfo.CURRENT_BASE_DEVICE_HEIGHT - HOOTER_SIZE, 
			BUTTON_WIDTH, BUTTON_HEIGHT,
			R.drawable.now_playlist_button_image,
			R.drawable.no_image,//R.drawable.tab4_btn_not_select_no_shadow2, 
			"", ScaleType.FIT_XY 
		);
		mapBtn.get(TabPage.TABPAGE_ID_NOW_PLAYLIST).acceptConfigurator(tabBtnCreationData);
		actMapTemp = new SparseArray< IViewAction >();
		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( this.getInternalID(), TabPage.TABPAGE_ID_NOW_PLAYLIST ) );
		actionSetter = new TabComponentActionSetter( actMapTemp );			
		mapBtn.get(TabPage.TABPAGE_ID_NOW_PLAYLIST).acceptConfigurator(actionSetter);
		rlHooter.addView(mapBtn.get(TabPage.TABPAGE_ID_NOW_PLAYLIST).getView());
		
		RelativeLayout rlCont = (RelativeLayout) tabBaseLayout.findViewById(R.id.tab_contents);
		// タブの追加
		addChild( TabPage.TABPAGE_ID_PLAY_SUB, new TabPagePlay( this, pageContainer, rlCont ) );
		addChild( TabPage.TABPAGE_ID_NOW_PLAYLIST, new TabPageNowPlaylist( this, pageContainer, rlCont ) );
	
		//rlHooter.setBackgroundResource(R.color.gradiant_test4);
		rlCont.setBackgroundResource(R.color.gradiant_test4);
		
		// タブのパネルを親から与えられたレイアウトに追加
		componentContainer.addView(tabBaseLayout);

		return errCode;
	}

}
