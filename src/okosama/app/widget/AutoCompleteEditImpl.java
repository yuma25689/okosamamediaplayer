package okosama.app.widget;

import android.content.Context;
import android.widget.AutoCompleteTextView;

public class AutoCompleteEditImpl extends AutoCompleteTextView {

	public AutoCompleteEditImpl(Context context) {
		super(context);
		// ���̃A�v���P�[�V�������L�̐ݒ�
		this.setSingleLine();
		// setPadding(0,0,0,0);
	}

}
