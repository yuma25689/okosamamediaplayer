package okosama.app.widget;

import android.content.Context;
import android.widget.TextView;

/**
 * ���̃A�v���P�[�V�����ŗ��p����{�^���̎���
 * Bridge�p�^�[����K�p
 * @author 25689
 *
 */
public class LabelImpl extends TextView {

	public LabelImpl(Context context) {
		super(context);
		// ���̃A�v���P�[�V�������L�̐ݒ�
		setPadding(0,0,0,0);
	}

}
