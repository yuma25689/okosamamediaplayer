
package okosama.app.factory;

import okosama.app.behavior.IExpListBehavior;
import okosama.app.behavior.IListBehavior;
import okosama.app.widget.AutoCompleteEdit;
import okosama.app.widget.Button;
import okosama.app.widget.Combo;
import okosama.app.widget.Edit;
import okosama.app.widget.ExpList;
import okosama.app.widget.Image;
import okosama.app.widget.Label;
import okosama.app.widget.List;
import okosama.app.widget.ProgressBar;
import okosama.app.widget.SeekBar;
import okosama.app.widget.ToggleButton;

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
	AutoCompleteEdit MakeAutoCompleteEdit();
	Combo MakeCombo();
}
