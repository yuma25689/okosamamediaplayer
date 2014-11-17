package okosama.app.panel;

import okosama.app.LogWrapper;
import okosama.app.R;
import android.app.Activity;
import android.view.ViewGroup;
import android.view.ViewParent;

public class TabMoveLeftInfoPanel extends ControlPanel {
	TabMoveLeftInfoPanel instance;
	public void createInstance(Activity activity)
	{
		if( instance == null )
		{
			instance = new TabMoveLeftInfoPanel(activity);
		}
	}
	public TabMoveLeftInfoPanel getInstance()
	{
		return instance;
	}
	public void insertToLayout( ViewGroup tabBaseLayout )
	{
		if( instance != null && instance.getView() != null )
		{
			if( instance.getView().getParent() != null )
			{
				ViewParent v = instance.getView().getParent();
				if( v instanceof ViewGroup )
				{
					((ViewGroup) v).removeView(instance.getView());
				}
			}

			tabBaseLayout.addView(instance.getView());
			parent = tabBaseLayout;		
		}
		else
		{
			LogWrapper.e("error","insert tab move right info panel");
		}
	}
	public void removeToLayout( ViewGroup tabBaseLayout )
	{
		if( instance != null && instance.getView() != null )
		{
			if( instance.getView().getParent() != null )
			{
				ViewParent v = instance.getView().getParent();
				if( v instanceof ViewGroup )
				{
					((ViewGroup) v).removeView(instance.getView());
				}
			}

			parent = null;				
		}
	}
	
	public TabMoveLeftInfoPanel(Activity activity) {
		super(activity);
		resetPanelViews(R.layout.left_move_panel_common);
		return;		
	}
}
