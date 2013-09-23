package okosama.app.action;
import android.view.View;

/**
 * �A�N�V�����N���X�̃C���^�t�F�[�X
 * ���̃N���X�K�w�́A���ƃV�X�e���ւ̈ˑ��������Ȃ��Ă��܂�������
 * @author 25689
 *
 */
public interface IViewAction {

	public static final int ACTION_ID_NONE = 0;
	public static final int ACTION_ID_ONCLICK = 1;
	public static final int ACTION_ID_ONTOGGLEON = 2;
	public static final int ACTION_ID_ONTOGGLEOFF = 3;
	public static final int ACTION_ID_ONCLICKSEEK = 4;
	
	/**
	 * �A�N�V���������s����
	 * @return �G���[�R�[�h 0:���� 0�ȊO:�ُ�
	 */
	int doAction(Object param);
}
