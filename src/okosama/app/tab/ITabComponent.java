package okosama.app.tab;

import okosama.app.action.IViewAction;
import okosama.app.behavior.IBehavior;
import android.util.SparseArray;
import android.view.View;

/**
 * 
 * @author 25689
 *
 */
public interface ITabComponent {

	/**
	 * ����̍쐬
	 * @return 0:���� 0�ȊO:�ُ�
	 */
	public int create(int panelLayoutId);

	/**
	 * ���O�̐ݒ�
	 * @param name
	 */
	public void setName(String name);
	/**
	 * ���O�̎擾
	 * @return
	 */
	public String getName();
	/**
	 * View�̎擾
	 * @return view
	 */
	public View getView();
	
	/**
	 * Active���ǂ�����ݒ�
	 * @param b
	 */
	public void setActivate( boolean b );

	/**
	 * Visible���ǂ�����ݒ�
	 * @param b
	 */
	public void setVisible( boolean b );

	/**
	 * �\���̍X�V
	 */
	public void updateDisplay();
 
	/**
	 * �q���ڂ̒ǉ�
	 * @param child
	 */
	public void addChild( int ID, ITabComponent child );

	/**
	 * �O���I�u�W�F�N�g�ɐݒ���Ϗ�����
	 * @param conf
	 */
	public void acceptConfigurator( ITabComponentConfigurator conf );
	
	/**
	 * �ݒ肳�ꂽActionMap�̓��e���A�R���|�[�l���g�ɐݒ肷��
	 */
	public void configureAction();
	
	/**
	 * ActionMap������ɐݒ肷��
	 * @param actionMap
	 */
	public void setActionMap(SparseArray< IViewAction > actionMap);
	
	public IBehavior getBehavior();
	/**
	 * �q���ڂ̃N���A
	 */
	// public void clearChild();
}
