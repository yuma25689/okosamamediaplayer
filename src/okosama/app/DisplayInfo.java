package okosama.app;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * 画面情報取得用クラスのハンドル。
 * @author 25689
 *
 */
public final class DisplayInfo {
	
	public static final int MSG_INIT_END = 100;
	
	// Singleton
	private static DisplayInfo instance = new DisplayInfo();
	private DisplayInfo() {}
	public static DisplayInfo getInstance() {
		return instance;
	}
	
	private DroidDisplayInfo _impl = new DroidDisplayInfo();
	
	/**
	 * 初期化 この関数は微妙にandroid固有になってしまったが、仕方ない。
	 * @param activity
	 * @param viewForMeasureBarHeight
	 */
	public void init(Activity activity,
			View viewForMeasureBarHeight,
			Handler handler)
	{
		_impl.init(activity,viewForMeasureBarHeight,handler);
	}
	
	/**
	 * サイズの補正値を取得
	 * @return
	 */
	public int getCorrectionXConsiderDensity( int orgX )
	{
		return _impl.getCorrectionXConsiderDensity( orgX );
	}
	public int getCorrectionYConsiderDensity( int orgY )
	{
		return _impl.getCorrectionYConsiderDensity( orgY );
	}
	public double getBkImageWidth()
	{
		return _impl.orgWidthOfBk;
	}
	public double getBkImageHeight()
	{
		return _impl.orgHeightOfBk;
	}
	public boolean isPortrait()
	{
		return _impl.isPortrait();
	}
	
	public RelativeLayout.LayoutParams createLayoutParamsForTabContent()
	{
		RelativeLayout.LayoutParams lpList = null;
		if( OkosamaMediaPlayerActivity.dispInfo.isPortrait() )
		{
			lpList = new RelativeLayout.LayoutParams(
	        		RelativeLayout.LayoutParams.FILL_PARENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT
	        );
			lpList.addRule(RelativeLayout.ABOVE,R.id.bottom_info_bar);
			lpList.addRule(RelativeLayout.RIGHT_OF,R.id.left_move_panel);
			lpList.addRule(RelativeLayout.LEFT_OF,R.id.right_move_panel);
		}
		else
		{
			lpList = new RelativeLayout.LayoutParams(
	        		RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.FILL_PARENT
	        );
			lpList.addRule(RelativeLayout.LEFT_OF,R.id.bottom_info_bar);
		}
		return lpList;
	}
}
