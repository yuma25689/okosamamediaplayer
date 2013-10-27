package okosama.app.tab;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.action.IViewAction;
import okosama.app.action.MediaStopAction;
import okosama.app.action.TweetAction;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.panel.PlayControlPanel;
import okosama.app.panel.SubControlPanel;
import okosama.app.panel.TimeControlPanel;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.widget.absWidget;
import android.util.SparseArray;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 音楽再生タブ
 * @author 25689
 *
 */
public class TabPagePlay extends TabPage {

	public TabPagePlay( Tab parent, LinearLayout ll, RelativeLayout rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		// コンストラクタでこのタブのタブIDを設定
		this.tabId = TABPAGE_ID_PLAY;
		
		create(R.layout.tab_layout_content_generic);
//		componentContainer.addView(tabButton.getView());
	}
	/* (non-Javadoc)
	 * @see okosama.app.container.ITabComponent#create()
	 */
	@Override
	public int create(int panelLayoutID) {
		
		resetPanelViews( panelLayoutID );
		RelativeLayout.LayoutParams lp 
		= OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
        		0, 0
        );
		tabBaseLayout.setLayoutParams(lp);
		// ---- action
//		SparseArray< IViewAction > actMapTemp 
//			= new SparseArray< IViewAction >();
//		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( parent, tabId ) );
//		TabComponentActionSetter actionSetter = new TabComponentActionSetter( actMapTemp );			
//		tabButton.acceptConfigurator(actionSetter);
		
		//////////////////// button //////////////////////////
		TabComponentPropertySetter creationData[] = {
			// --------------------- STOP
			new TabComponentPropertySetter(
				ControlIDs.STOP_BUTTON, ComponentType.BUTTON, 
				150, 590, 100, 100
				, null, R.drawable.stop_button_image, "", ScaleType.FIT_XY
			),
			// --------------------- TWITTER
			new TabComponentPropertySetter(
				ControlIDs.TWEET_BUTTON, ComponentType.BUTTON, 
				370, 550, 80, 80
				, null, R.drawable.internal_btn_image, "", ScaleType.FIT_XY
			),
		};
		
		// 背景画像はなぜかsetActivateの担当なので、ここでは追加しない
		
		
		OkosamaMediaPlayerActivity activity = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		// ボタンのハンドルクラスを作成
		// おそらく、クラスに持った方がいい
		absWidget widgets[] = {
			DroidWidgetKit.getInstance().MakeButton()
			,DroidWidgetKit.getInstance().MakeButton()
		};
		
		// ---- action
		// Stopボタン
		SparseArray< IViewAction > actMapStop 
			= new SparseArray< IViewAction >();
		actMapStop.put( IViewAction.ACTION_ID_ONCLICK, new MediaStopAction() );
		// twitterボタン
		SparseArray< IViewAction > actMapTwitter
			= new SparseArray< IViewAction >();
		actMapTwitter.put( IViewAction.ACTION_ID_ONCLICK, new TweetAction() );
		
		TabComponentActionSetter actionSetterCont[] = {
			new TabComponentActionSetter( actMapStop )
			,new TabComponentActionSetter( actMapTwitter )
		};
		
		// ボタンを作成、位置を合わせ、アクションを設定し、レイアウトに配置
		int i=0;
		for( absWidget widget : widgets )
		{
			widget.acceptConfigurator(creationData[i]);
			// TODO:ボタンのアクションを設定
			if( actionSetterCont[i] != null )
			{
				widget.acceptConfigurator(actionSetterCont[i]);
			}
			
			// ボタンをこのタブ子項目として追加
			//addChild( creationData[i].getInternalID(), widget );
			tabBaseLayout.addView( widget.getView() );
			
			// ボタンを配置
			// これは、setActivateで行う
			// componentContainer.addView( btn.getView() );
			i++;
		}
		// 一説によると、最後に加えられたもののZorderが一番上らしい。
		// どっちにしても、BringToFrontで変えられるらしいが
		SubControlPanel.insertToLayout(tabBaseLayout);
		TimeControlPanel.insertToLayout(tabBaseLayout);
		PlayControlPanel.insertToLayout(tabBaseLayout);
//		tabBaseLayout.addView( activity.getSubCP().getView() );
//		tabBaseLayout.addView( activity.getTimeCP().getView() );
//		tabBaseLayout.addView( activity.getPlayCP().getView() );
		tabBaseLayout.setBackgroundResource(R.color.gradiant_test);
		
		activity.updatePlayStateButtonImage();
		
		return 0;
	}
}
