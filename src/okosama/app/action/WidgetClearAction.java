package okosama.app.action;

import okosama.app.widget.absWidget;

public class WidgetClearAction implements IViewAction {

	absWidget widget = null;
	public WidgetClearAction(absWidget widget_)
	{
		widget = widget_;
	}
	@Override
	public int doAction(Object param) 
	{
		widget.clearValue();
		return 0;
	}

}
