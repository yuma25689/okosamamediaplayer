package okosama.app.factory;

import android.app.Activity;
import okosama.app.behavior.IExpListBehavior;
import okosama.app.behavior.IListBehavior;
import okosama.app.widget.Button;
import okosama.app.widget.ExpList;
import okosama.app.widget.Label;
import okosama.app.widget.List;
import okosama.app.widget.ProgressBar;
import okosama.app.widget.SeekBar;
import okosama.app.widget.ToggleButton;

public class DroidWidgetKit implements IWidgetKit {
	// singleton
	private static DroidWidgetKit instance = new DroidWidgetKit();
	private DroidWidgetKit() {}
	public static DroidWidgetKit getInstance() {
		return instance;
	}
	Activity activity;
	public Activity getActivity() {
		return activity;
	}
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	@Override
	public Button MakeButton() {
		return new Button(activity);
	}
	@Override
	public List MakeList( IListBehavior behavior ) {
		return new List(activity, behavior);
	}	
	@Override
	public ExpList MakeExpList(IExpListBehavior behavior) {
		return new ExpList(activity,behavior);
	}
	@Override
	public ToggleButton MakeToggleButton() {
		return new ToggleButton(activity);
	}
	@Override
	public Label MakeLabel()
	{
		Label ret = new Label(activity);
		return ret;
	}
	@Override
	public ProgressBar MakeProgressBar()
	{
		ProgressBar ret = new ProgressBar(activity);
		return ret;
	}
	@Override
	public SeekBar MakeSeekBar()
	{
		SeekBar ret = new SeekBar(activity);
		return ret;
	}	
}
