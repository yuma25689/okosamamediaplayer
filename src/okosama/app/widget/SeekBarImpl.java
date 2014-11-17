package okosama.app.widget;

import android.content.Context;
import android.widget.SeekBar;

/**
 * ���̃A�v���P�[�V�����ŗ��p����V�[�N�o�[�̎���
 * Bridge�p�^�[����K�p
 * @author 25689
 *
 */
public class SeekBarImpl extends SeekBar {

	public SeekBarImpl(Context context) {
		super(context,null,android.R.attr.progressBarStyleHorizontal);	
		// ���̃A�v���P�[�V�������L�̐ݒ�
		setPadding(0,0,0,0);
	}
}
