package okosama.app.widget;

import android.content.Context;
import android.widget.ProgressBar;

/**
 * ���̃A�v���P�[�V�����ŗ��p����v���O���X�o�[�̎���
 * Bridge�p�^�[����K�p
 * @author 25689
 *
 */
public class ProgressBarImpl extends ProgressBar {

	public ProgressBarImpl(Context context) {
		super(context,null,android.R.attr.progressBarStyleHorizontal);
		// ���̃A�v���P�[�V�������L�̐ݒ�
		setPadding(0,0,0,0);
	}
}
