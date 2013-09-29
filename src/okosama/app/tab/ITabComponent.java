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
	// public int create();

	/**
	 * ID�̐ݒ�
	 * @param ID
	 */
	//public void setName(String name);
	public void setInternalID(Integer ID);
	/**
	 * ID�̎擾
	 * @return
	 */
	public Integer getInternalID();
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
	 * @param ID ���ڂ�ID
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
