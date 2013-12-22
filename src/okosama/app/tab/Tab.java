package okosama.app.tab;

import okosama.app.ControlIDs;
import okosama.app.MusicSettingsActivity;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.action.ControllerShowHideAction;
import okosama.app.action.IViewAction;
import okosama.app.action.TabSelectAction;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.widget.Button;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
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
public class Tab extends TabComponentParent {

	public static int HDR_SIZE = 100;
	protected SparseArray<Button> mapBtn;
	int iCurrentTabPageId;
	// next force refresh
	boolean nextForceRefresh = false;
	boolean locking = false;
	
	
	
	/**
	 * @return the locking
	 */
	public boolean isLocking() {
		return locking;
	}

	/**
	 * @param locking the locking to set
	 */
	protected void setLocking(boolean locking) {
		this.locking = locking;
	}

	/**
	 * @return the nextForceRefresh
	 */
	public boolean isNextForceRefresh() {
		return nextForceRefresh;
	}

	/**
	 * @param nextForceRefresh the nextForceRefresh to set
	 */
	public void setNextForceRefresh(boolean nextForceRefresh) {
		this.nextForceRefresh = nextForceRefresh;
	}

	public Tab( int ID, LinearLayout ll, ViewGroup rl )
	{
		this.internalID = ID;
		pageContainer = ll;
		componentContainer = rl;
		iCurrentTabPageId = TabPage.TABPAGE_ID_NONE;
		mapBtn = new SparseArray<Button>();
	}
	
	/**
	 * タブ全体の作成
	 * @return 0:正常 0以外:異常
	 */
	@Override
	public int create(int panelLayoutId) {
		int errCode = 0;

		OkosamaMediaPlayerActivity act 
		= OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();

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
		RelativeLayout rlHdr = (RelativeLayout) tabBaseLayout.findViewById(R.id.tab_header);
		// タブのボタンだけはここで作る？
		// プレイタブボタン
		mapBtn.put( TabPage.TABPAGE_ID_PLAY, DroidWidgetKit.getInstance().MakeButton() );
		TabComponentPropertySetter tabBtnCreationData = new TabComponentPropertySetter(
			ControlIDs.PLAY_TAB_BUTTON, null, ComponentType.BUTTON, 
			10, 40, 100, 100, 
			null, R.drawable.music_tab_button_image,
			"", ScaleType.FIT_XY
		);
		mapBtn.get(TabPage.TABPAGE_ID_PLAY).acceptConfigurator(tabBtnCreationData);		
		SparseArray< IViewAction > actMapTemp 
			= new SparseArray< IViewAction >();
		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( 
				this.getInternalID(),
				TabPage.TABPAGE_ID_PLAY ) );
		mapBtn.get(TabPage.TABPAGE_ID_PLAY).acceptConfigurator(new TabComponentActionSetter( actMapTemp ));
		rlHdr.addView(mapBtn.get(TabPage.TABPAGE_ID_PLAY).getView());
		// キュータブボタン
		mapBtn.put( TabPage.TABPAGE_ID_NOW_PLAYLIST, DroidWidgetKit.getInstance().MakeButton() );
		tabBtnCreationData
		= new TabComponentPropertySetter(
			ControlIDs.NOW_PLAYLIST_TAB_BUTTON, null, ComponentType.BUTTON,
			120, 40, 100, 100,
			null, R.drawable.now_playlist_button_image,
			"", ScaleType.FIT_XY
		);
		mapBtn.get(TabPage.TABPAGE_ID_NOW_PLAYLIST).acceptConfigurator(tabBtnCreationData);		
		actMapTemp
		= new SparseArray< IViewAction >();
		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( 
				this.getInternalID(), TabPage.TABPAGE_ID_NOW_PLAYLIST ) );
		mapBtn.get(TabPage.TABPAGE_ID_NOW_PLAYLIST).acceptConfigurator(new TabComponentActionSetter( actMapTemp ));
		rlHdr.addView(mapBtn.get(TabPage.TABPAGE_ID_NOW_PLAYLIST).getView());
		
		// メディアタブボタン
		mapBtn.put( TabPage.TABPAGE_ID_MEDIA, DroidWidgetKit.getInstance().MakeButton() );
		tabBtnCreationData
		= new TabComponentPropertySetter(
			ControlIDs.MEDIA_TAB_BUTTON, null, ComponentType.BUTTON,
			230, 40, 100, 100,
			null, R.drawable.sdcard_choice_button_image,
			"", ScaleType.FIT_XY
		);
		mapBtn.get(TabPage.TABPAGE_ID_MEDIA).acceptConfigurator(tabBtnCreationData);		
		actMapTemp
		= new SparseArray< IViewAction >();
		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( 
				this.getInternalID(), TabPage.TABPAGE_ID_MEDIA ) );
		mapBtn.get(TabPage.TABPAGE_ID_MEDIA).acceptConfigurator(new TabComponentActionSetter( actMapTemp ));
		rlHdr.addView(mapBtn.get(TabPage.TABPAGE_ID_MEDIA).getView());
		// コントローラボタン
		mapBtn.put( TabPage.TABPAGE_ID_CONTROLLER, act.getControllerShowHideBtn() );
		tabBtnCreationData
		= new TabComponentPropertySetter(
			ControlIDs.CONTROLLER_TAB_BUTTON, null, ComponentType.BUTTON,
			380, 40, 100, 100,
			null, R.drawable.controller_button_image,
			"", ScaleType.FIT_XY
		);
		mapBtn.get(TabPage.TABPAGE_ID_CONTROLLER).acceptConfigurator(tabBtnCreationData);		
		actMapTemp
		= new SparseArray< IViewAction >();
		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new ControllerShowHideAction() );
		mapBtn.get(TabPage.TABPAGE_ID_CONTROLLER).acceptConfigurator(new TabComponentActionSetter( actMapTemp ));
		rlHdr.addView(mapBtn.get(TabPage.TABPAGE_ID_CONTROLLER).getView());				
		
		RelativeLayout rlCont = (RelativeLayout) tabBaseLayout.findViewById(R.id.tab_contents);
		
		// タブの追加
		addChild( TabPage.TABPAGE_ID_PLAY, 
				new TabPagePlay2( this, pageContainer, rlCont ) );
		addChild( TabPage.TABPAGE_ID_NOW_PLAYLIST, 
				new TabPageNowPlaylist( this, pageContainer, rlCont ));
		addChild( TabPage.TABPAGE_ID_MEDIA, 
				new TabPageMedia( this, pageContainer, rlCont ) );//new TabPageMedia( this, pageContainer, rlCont ) );
		// タブページは、setCurrentTabを読んだ時、アクティブなものだけが作られる。
		// なぜかタブページのcreateは呼んではいけないことになってしまった。
		// また、create時のタブIDは不明なので、setCurrentTabはここでは呼ばず、上位に呼ばせる。
		
		// rlCont.setBackgroundResource(R.color.gradiant_base);
		
		// タブのパネルを親から与えられたレイアウトに追加
		componentContainer.addView(tabBaseLayout);
		
		return errCode;
	}
	
	int lastSelectedTabIndexForEnableAllTab = 0;
	/**
	 * タブ切り替え中のロックだが、本来は元のEnableを考慮した制御が必要
	 * @param bEnable
	 */
	public void setEnableAllTab(boolean bEnable,int iTabPageId)
	{
		for( int i=0; i < mapBtn.size(); ++i )
		{
			// あまりよくないが、選択中のタブボタンは、ここでEnable=trueにはさせない
			if( bEnable == true )
			{
				if( //iCurrentTabPageId == mapBtn.keyAt(i) )
						iTabPageId == mapBtn.keyAt(i) )
				{
					mapBtn.valueAt(i).setEnabled(false);
					continue;
				}
			}
			mapBtn.valueAt(i).setEnabled(bEnable);
		}
		setLocking( !bEnable );
	}
	
	/**
	 * 現在のタブを設定する
	 * 現状、TabSelectActionでは結局これが呼ばれる
	 * @param tabPageId
	 */
	public void setCurrentTab(int tabPageId,boolean save)
	{
		Log.d("tab.setCurrentTab", "tab:" + tabPageId);
		//synchronized( OkosamaMediaPlayerActivity.getResourceAccessor().tabAnim )
		//{
			OkosamaMediaPlayerActivity.getResourceAccessor().tabAnim.SetTabSelectionLock(
					true, internalID,tabPageId);
			boolean bOutExec = false;
			boolean bAnimExec = false;
	        SharedPreferences prefs 
	        = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getSharedPreferences(
	                MusicSettingsActivity.PREFERENCES_FILE, Context.MODE_PRIVATE);
	        bAnimExec = prefs.getBoolean(MusicSettingsActivity.KEY_ENABLE_ANIMATION, false);
						
			// 一度全てのタブの選択を解除
			if( null != children.get(iCurrentTabPageId,null) )
			{
	    		// 現在選択中のタブのタブページをクリアする
				children.get(iCurrentTabPageId).setActivate(false);
				bOutExec = true;
	   		}
			// 現在選択中のタブを新しいものに設定する
			if( null != children.get(tabPageId,null) )
			{
				children.get(tabPageId).setActivate(true);
	   		}
			iCurrentTabPageId = tabPageId;
			// アプリケーションに選択されたタブの画面IDを設定する
			// この場所だけでいいかどうかは不明
	        if( save == true )
	        {
	    		OkosamaMediaPlayerActivity act 
	    		= OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
	        	act.setCurrentDisplayId(this.internalID,tabPageId);
	        }
	        if( bAnimExec == false || bOutExec == false )
	        {
	        	OkosamaMediaPlayerActivity.getResourceAccessor().tabAnim.SetTabSelectionLock(
	        			false, internalID,tabPageId);
	        }
		//}

	}

}
