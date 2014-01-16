
package okosama.app.factory;

import okosama.app.behavior.IExpListBehavior;
import okosama.app.behavior.IListBehavior;
import okosama.app.widget.*;

/**
 * @author 
 *
 */
public interface IWidgetKit {
	Button MakeButton();
	List MakeList(IListBehavior behavior);
	ExpList MakeExpList(IExpListBehavior behavior);
	ToggleButton MakeToggleButton();
	Label MakeLabel();
	ProgressBar MakeProgressBar();
	SeekBar MakeSeekBar();
	Image MakeImage();
	Edit MakeEdit();
	Combo MakeCombo();
}
