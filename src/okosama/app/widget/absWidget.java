package okosama.app.widget;

import okosama.app.tab.ITabComponentConfigurator;
import okosama.app.tab.TabLeaf;
import android.app.Activity;
import android.view.View;

public class absWidget extends TabLeaf {
	public absWidget(Activity activity) {
		super(activity);
	}
	public void setEnabled( boolean b ) {
	}
	public void setVisible( boolean b ) {
	}
	public int create() {
		return 0;
	}
	public View getView() {
		return null;
	}
	public void configureAction() {
		super.configureAction();
	}
	@Override
	public void acceptConfigurator(ITabComponentConfigurator conf) {
		conf.configure(this);
	}
}
