package okosama.app.tab.media;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.tab.Tab;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * タブを模倣したクラス。このクラスは表示を持たない。
 * タブというよりは、Mediatorに近い。
 * タブの作成および、タブ上のコンポーネントの有効/無効、表示/非表示のみを制御する
 * @author 25689
 *
 */
public class TabMediaSelect extends Tab {
	
	public TabMediaSelect(LinearLayout ll, RelativeLayout rl) {
		super(OkosamaMediaPlayerActivity.tabNameMedia, ll, rl);
	}

	/**
	 * タブ全体の作成
	 * @return 0:正常 0以外:異常
	 */
	public int create() {
		int errCode = 0;
		
		// タブの追加
		addChild( new TabPageAlbum( this, pageContainer, componentContainer ) );
		addChild( new TabPageArtist( this, pageContainer, componentContainer ) );
		addChild( new TabPageSong( this, pageContainer, componentContainer ) );
		addChild( new TabPagePlayList( this, pageContainer, componentContainer ) );
		// addChild( new TabPageAlbum( this, pageContainer, componentContainer ) );
		
		// タブページは、setCurrentTabを読んだ時、アクティブなものだけが作られる。
		// なぜかタブページのcreateは呼んではいけないことになってしまった。
		// また、create時のタブIDは不明なので、setCurrentTabはここでは呼ばず、上位に呼ばせる。
		
		return errCode;
	}

}
