package okosama.app.state;

import okosama.app.tab.Tab;

public interface IDisplayState {

	// Bundle mBundle;
	
	/**
	 * ���̉�ʏ�ԂɊ�Â��ĉ�ʂ�؂�ւ���
	 * @return 0:���� �}�C�i�X:�ُ� ���ID:�T�u��ʂ���i�Ԃ�̂͐e��ʂ�ID�j
	 */
	public int ChangeDisplayBasedOnThisState(Tab tab);
	
	public static String LSNER_NAME_TRACK = "TRACK_LSN";
	public static String LSNER_NAME_SCAN = "SCAN_LSN";
	public static String HDLER_NAME_RESCAN = "RESCAN_HDL";
	
	
	public static int STATUS_ON_CREATE = 1;
	public static int STATUS_ON_RESUME = 2;
	public static int STATUS_ON_DESTROY = 3;
	public static int STATUS_ON_PAUSE = 4;
	/**
	 * ���̉�ʏ�ԂɊ�Â��āABloadcastReceiver��o�^����
	 * @return �o�^���� 0:�o�^OK 1:�o�^�Ώۂł͂Ȃ� -1:�o�^���s
	 */
	public int registerReceivers(int status);
	
	/**
	 * ���̉�ʏ�Ԃ�(?)BloadcastReceiver����������
	 */
	public void unregisterReceivers(int status);
	
	/**
	 * ���݂̏�ԂŁA�\�����X�V���ׂ��Ƃ��ɃR�[������
	 * @return ���̍X�V�ւ̃J�E���g�_�E��(ms) NO_REFRESH�Ȃ�΁A�p���I�ɂ͍X�V���Ȃ�
	 */
	public long updateDisplay();
	
}
