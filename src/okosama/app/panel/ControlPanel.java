package okosama.app.panel;

import okosama.app.tab.TabComponentParent;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

/**
 * コントロール用のウィジェットをまとめたパネルの親クラス
 * @author 25689
 *
 */
public class ControlPanel extends TabComponentParent {	
	static ViewGroup parent;
	/**
	 * resume前に親から抜いておかないと、resume時に落ちるので、仕方なく
	 */
	public void removeViewFromParent()
	{
		if( parent != null && getView() != null )
			parent.removeView(getView());
	}
	
	public ControlPanel(Activity activity) {
		super(activity);
	}

	@Override
	public int create(int panelLayoutId) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	/**
	 * Panelを返却
	 */
	public View getView() {
		return tabBaseLayout;
	}
	
	public void setLayoutParams( ViewGroup.LayoutParams lp )
	{
		if( tabBaseLayout != null )
		{
			tabBaseLayout.setLayoutParams(lp);
		}
	}
}
