package okosama.app.panel;

import okosama.app.LogWrapper;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import android.app.Activity;
import android.view.ViewGroup;

/**
 * ���y�t�@�C���̃^�O�̕ҏW
 * @author 25689
 *
 */
public class TagEditorControlPanel extends ControlPanel {
	static TagEditorControlPanel instance;
	public static void createInstance(Activity activity)
	{
		if( instance == null )
		{
			instance = new TagEditorControlPanel(activity);
		}
	}
	public static void deleteInstance()
	{
		removeFromParent();
		instance = null;
	}
	public static TagEditorControlPanel getInstance()
	{
		return instance;
	}
	public static void insertToLayout( ViewGroup tabBaseLayout )
	{
		if( instance != null && instance.getView() != null )
		{
			OkosamaMediaPlayerActivity.removeFromParent(instance.getView());
			tabBaseLayout.addView(instance.getView());
			parent = tabBaseLayout;
		}
		else
		{
			LogWrapper.e("error","tag editor control panel");
		}
	}
	public static void removeFromParent()
	{
		if( instance != null && instance.getView() != null )
		{
			OkosamaMediaPlayerActivity.removeFromParent(instance.getView());
			parent = null;	
		}
	}

	public TagEditorControlPanel(Activity activity) {
		super(activity);
		resetPanelViews(R.layout.panel_vscrollable);

		if( OkosamaMediaPlayerActivity.dispInfo.isPortrait() )
		{
		}
		else
		{
		}
	}


}
