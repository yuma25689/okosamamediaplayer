package okosama.app.widget;

import okosama.app.tab.ITabComponentConfigurator;
import okosama.app.tab.TabLeaf;
import android.app.Activity;
import android.view.View;

public class absWidget extends TabLeaf {
	int visibleFlag = View.VISIBLE;
	/**
	 * @return the visibleFlag
	 */
	public int getVisibleFlag() {
		return visibleFlag;
	}
	/**
	 * @param visibleFlag the visibleFlag to set
	 */
	public void setVisibleFlag(int visibleFlag) {
		this.visibleFlag = visibleFlag;
	}
	public void setVisible( int visibleFlag )
	{
		if( this.getView() != null )
		{
			this.getView().setVisibility(visibleFlag);
		}
	}
	public absWidget(Activity activity) {
		super(activity);
	}
	public boolean isEnabled()
	{
		return this.getView().isEnabled();
	}
	public void setEnabled( boolean b ) {
		this.getView().setEnabled(b);
	}
	
	public int create() {
		return 0;
	}
	@Override
	public View getView() {
		return null;
	}
	@Override
	public void configureAction() {
		super.configureAction();
	}
	@Override
	public void acceptConfigurator(ITabComponentConfigurator conf) {
		conf.configure(this);
	}
	
	public void clearChild()
	{
		return;
	}
}
