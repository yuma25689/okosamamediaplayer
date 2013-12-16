package okosama.app.panel;

import okosama.app.R;
import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewParent;

public class TabMoveRightInfoPanel extends ControlPanel {
	static TabMoveRightInfoPanel instance;
	public static void createInstance(Activity activity)
	{
		if( instance == null )
		{
			instance = new TabMoveRightInfoPanel(activity);
		}
	}
	public static TabMoveRightInfoPanel getInstance()
	{
		return instance;
	}
	public static void insertToLayout( ViewGroup tabBaseLayout )
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
			Log.e("error","insert tab move right info panel");
		}
	}
	public static void removeToLayout( ViewGroup tabBaseLayout )
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
	
	public TabMoveRightInfoPanel(Activity activity) {
		super(activity);
		resetPanelViews(R.layout.right_move_panel_common);
		return;		
	}
}
