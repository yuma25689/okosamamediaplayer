package okosama.app.widget;

import android.content.Context;
import android.widget.ExpandableListView;

/**
 * このアプリケーションで利用する開けるリストの実装
 * Bridgeパターンを適用
 * @author 25689
 *
 */
public class ExpListImpl extends ExpandableListView {

	public ExpListImpl(Context context) {		
		super(context);
	}
}
