package okosama.app.action;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * タブ項目を表示したいときに実行するアクション
 * @author 25689
 *
 */
public final class ShowTabComponentAction implements IViewAction {

	// singleton
	private static ShowTabComponentAction instance = new ShowTabComponentAction();
	private ShowTabComponentAction() {}
	public static ShowTabComponentAction getInstance() {
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
			for( int i=0; i < tabLayout.getChildCount(); i++ )
			{
				if( v == tabLayout.getChildAt(i) )
				{
					// 既に追加されている
					return 1;
				}
			}			
			tabLayout.addView(v);
		}
		return 0;
	}

}
