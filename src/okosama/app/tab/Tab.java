package okosama.app.tab;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.action.IViewAction;
import okosama.app.action.MediaPlayPauseAction;
import okosama.app.action.NextAction;
import okosama.app.action.PrevAction;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.widget.Button;
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

	public Tab( String name, LinearLayout ll, ViewGroup rl )
	{
		this.name = name;
		pageContainer = ll;
		componentContainer = rl;
	}
	
	/**
	 * タブ全体の作成
	 * @return 0:正常 0以外:異常
	 */
	public int create(int panelLayoutId) {
		int errCode = 0;
				
		OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();

		// タブのパネルを作成
		LayoutInflater inflator = act.getLayoutInflater();
		tabBaseLayout = (ViewGroup)inflator.inflate(panelLayoutId, null, false);
		
		// タブの追加
		addChild( TabPage.TABPAGE_ID_PLAY, new TabPagePlay( this, pageContainer, tabBaseLayout ) );
		addChild( TabPage.TABPAGE_ID_MEDIA, new TabPageMedia( this, pageContainer, tabBaseLayout ); );
		addChild( TabPage.TABPAGE_ID_PLAY, new TabPageNowPlaylist( this, pageContainer, tabBaseLayout ));
		// タブページは、setCurrentTabを読んだ時、アクティブなものだけが作られる。
		// なぜかタブページのcreateは呼んではいけないことになってしまった。
		// また、create時のタブIDは不明なので、setCurrentTabはここでは呼ばず、上位に呼ばせる。
		
		return errCode;
	}
	
	/**
	 * 現在のタブを設定する
	 * @param tabId
	 */
	public void setCurrentTab(int tabId,boolean save)
	{
        for( ITabComponent c : children ) {
        	if( c instanceof TabPage ) {
        		// 一度全てのタブの選択を解除
        		c.setActivate( false );
        	}
        	// 指定のタブIDのタブだけ、ピンポイントで選択する
        	if( c instanceof TabPage ) { // できたら使いたくなかった・・・。
        		if( ((TabPage) c).IsEqualTabId(tabId) == true )
        		{
        			c.setActivate( true );
        		}
        	}
        }
        for( ITabComponent c : children ) {
        	if( c instanceof TabPage ) { // できたら使いたくなかった・・・。
        		((TabPage) c).setTabButtonToFront();
        	}
        }
		// アプリケーションに選択されたタブの画面IDを設定する
		// この場所だけでいいかどうかは不明
        if( save == true )
        {
        	OkosamaMediaPlayerActivity.setCurrentDisplayId(this.name,tabId);
        }
	}

}
