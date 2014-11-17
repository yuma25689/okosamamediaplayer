package okosama.app.action;
import android.view.View;
import android.widget.RelativeLayout;
// import android.view.animation.AlphaAnimation;

/**
 * �^�u���ڂ��B�������Ƃ��Ɏ��s����A�N�V����
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
	public int doAction( Object param ) {
		View v = (View)param;
		if( tabLayout != null && v != null )
		{
//			AlphaAnimation animation = new AlphaAnimation( 100, 0 );
//			animation.setDuration(1000);
//			v.startAnimation(animation);
			tabLayout.removeView(v);
		}
		return 0;
	}

}
