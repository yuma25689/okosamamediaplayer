package okosama.app.tab;

import java.util.ArrayList;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.action.IViewAction;
import okosama.app.adapter.AlbumListRawAdapter;
import okosama.app.panel.MoveTabInfo;
import okosama.app.panel.NowPlayingControlPanel;
import okosama.app.panel.PlayControlPanel;
import okosama.app.panel.SubControlPanel;
import okosama.app.panel.TabMoveLeftInfoPanel;
import okosama.app.panel.TabMoveRightInfoPanel;
import okosama.app.panel.TimeControlPanel;
import okosama.app.service.MediaPlayerUtil;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 音楽再生タブ
 * @author 25689
 *
 */
public class TabPagePlay extends TabPage { //implements OnTouchListener {

	boolean bPanelShowPlay = true;
	public boolean getPanelShowPlay() {
		return bPanelShowPlay;
	}

	public TabPagePlay( Tab parent, LinearLayout ll, RelativeLayout rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		// コンストラクタでこのタブのタブIDを設定
		this.tabId = TABPAGE_ID_PLAY;
		
		create(R.layout.tab_layout_content_generic_flickable);
//		componentContainer.addView(tabButton.getView());
	}
	/* (non-Javadoc)
	 * @see okosama.app.container.ITabComponent#create()
	 */
	@Override
	public int create(int panelLayoutID) {
		// フリック入力対応
		ArrayList<MoveTabInfo> arrMti = new ArrayList<MoveTabInfo>();
		// 左フリック時の設定
		MoveTabInfo mti = new MoveTabInfo();
		mti.setTabInfoIndex( MoveTabInfo.LEFT_1 );
		mti.setTabId(ControlIDs.TAB_ID_MAIN);
		mti.setTabPageId(TabPage.TABPAGE_ID_MEDIA);
		mti.setPanelId(R.id.left_move_panel);
		mti.setImageViewId(R.id.left_move_image);
		mti.setTabImageResId(R.drawable.video_normal);
		arrMti.add(mti);
		// 右フリック時の設定
		MoveTabInfo mtiR = new MoveTabInfo();
		mtiR.setTabInfoIndex( MoveTabInfo.RIGHT_1 );
		mtiR.setTabId(ControlIDs.TAB_ID_MAIN);
		mtiR.setTabPageId(TabPage.TABPAGE_ID_NOW_PLAYLIST);
		mtiR.setPanelId(R.id.right_move_panel);
		mtiR.setImageViewId(R.id.right_move_image);
		mtiR.setTabImageResId(R.drawable.playlist_normal);
		arrMti.add(mtiR);
		
		resetPanelViews( panelLayoutID, arrMti );
		RelativeLayout.LayoutParams lp 
		= OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
        		0, 0
        );
		tabBaseLayout.setLayoutParams(lp);
		
		
		// 背景画像はなぜかsetActivateの担当なので、ここでは追加しない
		
		
		OkosamaMediaPlayerActivity activity = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		// ボタンのハンドルクラスを作成
		// おそらく、クラスに持った方がいい
//		absWidget widgets[] = {
//			DroidWidgetKit.getInstance().MakeButton()
//			,DroidWidgetKit.getInstance().MakeButton()
//		};
		
		// ---- action
		// Stopボタン
//		SparseArray< IViewAction > actMapStop 
//			= new SparseArray< IViewAction >();
//		actMapStop.put( IViewAction.ACTION_ID_ONCLICK, new MediaStopAction() );
//		// twitterボタン
//		SparseArray< IViewAction > actMapTwitter
//			= new SparseArray< IViewAction >();
//		actMapTwitter.put( IViewAction.ACTION_ID_ONCLICK, new TweetAction() );
//		
//		TabComponentActionSetter actionSetterCont[] = {
//			new TabComponentActionSetter( actMapStop )
//			,new TabComponentActionSetter( actMapTwitter )
//		};
		
		// ボタンを作成、位置を合わせ、アクションを設定し、レイアウトに配置
//		int i=0;
//		for( absWidget widget : widgets )
//		{
//			widget.acceptConfigurator(creationData[i]);
//			// TODO:ボタンのアクションを設定
//			if( actionSetterCont[i] != null )
//			{
//				widget.acceptConfigurator(actionSetterCont[i]);
//			}
//			
//			// ボタンをこのタブ子項目として追加
//			//addChild( creationData[i].getInternalID(), widget );
//			tabBaseLayout.addView( widget.getView() );
//			
//			// ボタンを配置
//			// これは、setActivateで行う
//			// componentContainer.addView( btn.getView() );
//			i++;
//		}
		// 一説によると、最後に加えられたもののZorderが一番上らしい。
		// どっちにしても、BringToFrontで変えられるらしいが
		SubControlPanel.insertToLayout(tabBaseLayout);
		TimeControlPanel.insertToLayout(tabBaseLayout);
		PlayControlPanel.insertToLayout(tabBaseLayout);
//		tabBaseLayout.addView( activity.getSubCP().getView() );
//		tabBaseLayout.addView( activity.getTimeCP().getView() );
//		tabBaseLayout.addView( activity.getPlayCP().getView() );
		// TODO: 裏画面と背景を合わせることでなんとかそれっぽく見えているが、実は裏画面いっぱいにフィットしていない
		tabBaseLayout.setBackgroundResource(R.color.gradiant_tab_base);
		
		// TODO: サーフィスビューをいっぱいに入れる
		SurfaceView videoView 
		= OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getVideoView();
//		RelativeLayout.LayoutParams lpVideoView
//		= OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
//        		0, 0 
//        );
		RelativeLayout.LayoutParams lpVideoView 
		= OkosamaMediaPlayerActivity.dispInfo.createLayoutParamsForTabContent();
//		= new RelativeLayout.LayoutParams(
//        		RelativeLayout.LayoutParams.FILL_PARENT,
//				RelativeLayout.LayoutParams.WRAP_CONTENT
//        );
		
		videoView.setVisibility(View.GONE);
		videoView.setLayoutParams(lpVideoView);
		// videoView.setOnTouchListener(this);
		// TODO:videoを使う時は、コメントを外す
		// tabBaseLayout.addView( videoView );
		// tabBaseLayout.setOnTouchListener(new TabViewTouchListener(0,0));
		rightPanel = new TabMoveRightInfoPanel(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
		leftPanel = new TabMoveLeftInfoPanel(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
		
		rightPanel.createInstance(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
		leftPanel.createInstance(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
		rightPanel.insertToLayout(tabBaseLayout);
		leftPanel.insertToLayout(tabBaseLayout);
		
		this.setCtrlPanelShowFlg(true);
		activity.updatePlayStateButtonImage();
		
		return 0;
	}
	@Override
	public void setActivate( boolean bActivate )
	{
		super.setActivate(bActivate);		
		if( bActivate )
		{
			SurfaceView videoView 
			= OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getVideoView();
			if( videoView.getParent() != null )
			{
				ViewParent v = videoView.getParent();
				if( v instanceof ViewGroup )
				{
					((ViewGroup) v).removeView(videoView);
				}
			}			
			tabBaseLayout.addView( videoView );
			
			SubControlPanel.insertToLayout(tabBaseLayout);
			NowPlayingControlPanel.insertToLayout(tabBaseLayout);
			TimeControlPanel.insertToLayout(tabBaseLayout);
			PlayControlPanel.insertToLayout(tabBaseLayout);
			if( rightPanel != null )
			{
				rightPanel.insertToLayout(tabBaseLayout);
			}
			if( leftPanel != null )
			{
				leftPanel.insertToLayout(tabBaseLayout);
			}
			// OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
			// act.getControllerShowHideBtn().getView().setVisibility(View.GONE);
		}
		else
		{
			// OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().
			// getControllerShowHideBtn().getView().setVisibility(View.VISIBLE);	
		}
	}
	public void updateAlbumArtOnThePlayTab()
	{
		OkosamaMediaPlayerActivity act 
		= OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();		
		// できたら、アルバムアートを表示する
        long currentalbumid = MediaPlayerUtil.getCurrentAlbumId();
        AlbumListRawAdapter adp = (AlbumListRawAdapter)act.getAdapter(TABPAGE_ID_ALBUM);
        String art = adp.getAlbumArtFromId((int)currentalbumid);
    	Log.d("albumart - playpanel","albumID=" + currentalbumid + "art=" + art);
    	// TODO: ちゃんとやる
        if( art != null && art != "")
        {
	        Drawable d = MediaPlayerUtil.getCachedArtwork(
            		act, currentalbumid, null);
	        tabBaseLayout.setBackgroundDrawable(d);
        }
        else
        {
        	tabBaseLayout.setBackgroundResource(R.color.gradiant_tab_base);
        }
	}

//	int firstX = 0;
//	int firstY = 0;
//	@Override
//	public boolean onTouch(View v, MotionEvent event) {
//		int x = (int) event.getRawX();
//		int y = (int) event.getRawY();
//		switch (event.getAction() & MotionEvent.ACTION_MASK) {
//        case MotionEvent.ACTION_DOWN:
//        	firstX = x;
//        	firstY = y;
//			break;
//        case MotionEvent.ACTION_UP:
//			Rect outRect = new Rect(
//					firstX - TOUCH_RECOGNIZE_SPOT_SIZE/2,
//					firstY - TOUCH_RECOGNIZE_SPOT_SIZE/2,
//					firstX - TOUCH_RECOGNIZE_SPOT_SIZE/2 + TOUCH_RECOGNIZE_SPOT_SIZE,
//					firstY - TOUCH_RECOGNIZE_SPOT_SIZE/2 + TOUCH_RECOGNIZE_SPOT_SIZE);
//			// outRect.inset(-1*CLICKABLE_OFFSET, -1*CLICKABLE_OFFSET);
//			if( outRect.contains(x,y) )
//			{
//	        	updateControlPanelPlay(!bPanelShowPlay);
//			}        	
//            break;
//        }
//		return false;
//	}
	public void updateControlPanelPlay(boolean bPanelShowPlay_)
	{
		bPanelShowPlay = bPanelShowPlay_;
		updateControlPanelPlay();
	}
	public void updateControlPanelPlay()
	{
		if( bPanelShowPlay )
		{
			SubControlPanel.insertToLayout(tabBaseLayout);
			NowPlayingControlPanel.insertToLayout(tabBaseLayout);
			TimeControlPanel.insertToLayout(tabBaseLayout);
			PlayControlPanel.insertToLayout(tabBaseLayout);
			bPanelShow = true;
			
//			if( rightPanel != null )
//			{
//				rightPanel.insertToLayout(tabBaseLayout);
//			}
//			if( leftPanel != null )
//			{
//				leftPanel.insertToLayout(tabBaseLayout);
//			}
			// NowPlayingControlPanel.insertToLayout(tabBaseLayout);			
		}
		else
		{
			TimeControlPanel.removeFromParent();		
			NowPlayingControlPanel.removeFromParent();
			SubControlPanel.removeFromParent();
			PlayControlPanel.removeFromParent();	//ToLayout(tabBaseLayout);			
			bPanelShow = false;			
		}
	}
}
