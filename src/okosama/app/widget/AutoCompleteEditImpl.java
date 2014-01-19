package okosama.app.widget;

import android.content.Context;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

public class AutoCompleteEditImpl extends AutoCompleteTextView {

	public AutoCompleteEditImpl(Context context) {
		super(context);
		// このアプリケーション特有の設定
		this.setSingleLine();
		// setPadding(0,0,0,0);
	}

}
