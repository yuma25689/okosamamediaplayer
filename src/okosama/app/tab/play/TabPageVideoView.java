package okosama.app.tab.play;

import okosama.app.ControlDefs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.adapter.IAdapterUpdate;
import okosama.app.behavior.TrackListBehavior;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.panel.NowPlayingControlPanel;
import okosama.app.panel.PlayControlPanel;
import okosama.app.panel.SubControlPanel;
import okosama.app.panel.TimeControlPanel;
import okosama.app.tab.Tab;
import okosama.app.tab.TabChangeAnimation;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.tab.TabPage;
import okosama.app.widget.List;
import okosama.app.widget.absWidget;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

public class TabPageVideoView extends TabPage implements OnTouchListener {

	boolean bPanelShow = true;
	Tab tabContent;
	public TabPageVideoView( Tab parent, LinearLayout ll, RelativeLayout rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		// コンストラクタでこのタブのタブIDを設定
		this.tabId = TABPAGE_ID_VIDEO_VIEW;
		create(R.layout.tab_layout_content_generic_flickable );
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
		
		// TODO: サーフィスビューをいっぱいに入れる
		SurfaceView videoView 
		= OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getVideoView();
		RelativeLayout.LayoutParams lpVideoView
		= OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
        		0, 0 
        );
		videoView.setLayoutParams(lpVideoView);
		videoView.setOnTouchListener(this);
		tabBaseLayout.addView( videoView );
		
		return 0;
	}
	
	public void updateControlPanel()
	{
		if( bPanelShow )
		{
			SubControlPanel.insertToLayout(tabBaseLayout);
			TimeControlPanel.insertToLayout(tabBaseLayout);
			PlayControlPanel.insertToLayout(tabBaseLayout);
			// NowPlayingControlPanel.insertToLayout(tabBaseLayout);			
		}
		else
		{
			TimeControlPanel.removeToLayout(tabBaseLayout);		
			PlayControlPanel.removeToLayout(tabBaseLayout);
			SubControlPanel.removeToLayout(tabBaseLayout);			
		}
		
	}
	
	@Override
	public void setActivate( boolean bActivate )
	{
		if( bActivate )
		{
			updateControlPanel();
		}
		super.setActivate(bActivate);
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
        	//Log.e("action down","come");
        	bPanelShow = !bPanelShow;
        	updateControlPanel();
            break;
        }
		
		return false;
	}
	
}
