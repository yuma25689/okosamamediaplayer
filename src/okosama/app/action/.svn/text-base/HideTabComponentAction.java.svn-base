package okosama.app.action;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * タブ項目を隠したいときに実行するアクション
 * @author 25689
 *
 */
public final class HideTabComponentAction implements IViewAction {

	// singleton
	private static HideTabComponentAction instance = new HideTabComponentAction();
	private HideTabComponentAction() {}
	public static HideTabComponentAction getInstance() {
		return instance;
	}
	
	RelativeLayout tabLayout;
	/**
	 * @param tabLayout the tabLayout to set
	 */
	public void setTabLayout(RelativeLayout tabLayout) {
		this.tabLayout = tabLayout;
	}
	
	/**
	 * 
	 */
	@Override
	public int doAction( View v ) {
		if( tabLayout != null && v != null )
		{
			tabLayout.removeView(v);
		}
		return 0;
	}

}
