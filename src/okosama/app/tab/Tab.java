package okosama.app.tab;

import java.util.HashMap;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.action.IViewAction;
import okosama.app.action.MediaPlayAction;
import okosama.app.action.NextAction;
import okosama.app.action.PrevAction;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.widget.Button;
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

	TabPageMedia tabPageMedia = null;
	public TabPageMedia getTabPageMedia()
	{
		return tabPageMedia;
	}
	
	public Tab( String name, LinearLayout ll, RelativeLayout rl )
	{
		this.name = name;
		pageContainer = ll;
		componentContainer = rl;
	}
	
	/**
	 * タブ全体の作成
	 * @return 0:正常 0以外:異常
	 */
	public int create() {
		int errCode = 0;
		TabComponentPropertySetter creationData[] = {
			// --------------------- PLAY
			new TabComponentPropertySetter(
				"playbutton", ComponentType.BUTTON, 
				140, 155 + 2
				, 90, 90
				, null, R.drawable.play_button_image, "", ScaleType.FIT_XY
			),
			// --------------------- NEXT
			new TabComponentPropertySetter(
				"nextbutton", ComponentType.BUTTON, 
				270, 155 + 2, 90, 90
				, null, R.drawable.next_button_image, "", ScaleType.FIT_XY
			),
			// --------------------- PREV
			new TabComponentPropertySetter(
				"prevbutton", ComponentType.BUTTON, 
				10, 155 + 2, 90, 90
				, null, R.drawable.back_button_image, "", ScaleType.FIT_XY
			),
			// --------------------- SHUFFLE
			new TabComponentPropertySetter(
				"shufflebutton", ComponentType.BUTTON, 
				20, 700, 100, 100
				, null, R.drawable.btn_no_repeat_image, "", ScaleType.FIT_XY
			),
			// --------------------- REPEAT
			new TabComponentPropertySetter(
				"repeatbutton", ComponentType.BUTTON, 
				200, 690, 100, 100
				, null, R.drawable.btn_no_shuffle_image, "", ScaleType.FIT_XY
			)
		};
				
		// TODO:おそらく、クラスに持った方がいい
		// 画像を書き換え必要になるかもしれない
		Button btns[] = {
			DroidWidgetKit.getInstance().MakeButton()
			,DroidWidgetKit.getInstance().MakeButton()
			,DroidWidgetKit.getInstance().MakeButton()
		};
		
		// Playボタン
		HashMap< Integer, IViewAction > actMapPlay 
		= new HashMap< Integer, IViewAction >();
		actMapPlay.put( IViewAction.ACTION_ID_ONCLICK, new MediaPlayAction() );
		// nextボタン
		HashMap< Integer, IViewAction > actMapNext
			= new HashMap< Integer, IViewAction >();
		actMapNext.put( IViewAction.ACTION_ID_ONCLICK, new NextAction() );
		// backボタン
		HashMap< Integer, IViewAction > actMapBack
			= new HashMap< Integer, IViewAction >();
		actMapBack.put( IViewAction.ACTION_ID_ONCLICK, new PrevAction() );

		TabComponentActionSetter actionSetterCont[] = {
			new TabComponentActionSetter( actMapPlay )
			,new TabComponentActionSetter( actMapNext )
			,new TabComponentActionSetter( actMapBack )
		};
		
		int i=0;
		for( Button btn : btns )
		{
			OkosamaMediaPlayerActivity.getResourceAccessor().commonBtns.add(btn);
			btn.acceptConfigurator(creationData[i]);
			// ボタンのアクションを設定
			if( actionSetterCont[i] != null )
			{
				btn.acceptConfigurator(actionSetterCont[i]);
			}
			// ボタンをこのタブ子項目として追加
			addChild( btn );
			// ボタンを配置
			componentContainer.addView( btn.getView() );
			i++;
		}
		
		// タブの追加
		addChild( new TabPagePlay( this, pageContainer, componentContainer ) );
		tabPageMedia = new TabPageMedia( this, pageContainer, componentContainer );
		addChild( tabPageMedia );
		addChild( new TabPageNowPlaylist( this, pageContainer, componentContainer ));
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
        	if( c instanceof TabPage ) { // できたら使いたくなかった・・・。
        		c.setActivate( ((TabPage) c).IsEqualTabId(tabId) );
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
