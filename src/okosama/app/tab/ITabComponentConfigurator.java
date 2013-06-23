package okosama.app.tab;

/**
 * TabComponent�̐ݒ���s���N���X�̃C���^�t�F�[�X
 * @author 25689
 *
 */
public interface ITabComponentConfigurator {

	/**
	 * �ݒ���s��
	 * @param component
	 * @return �G���[�R�[�h 0:OK 1:NG
	 */
	public int configure( ITabComponent component );
}
