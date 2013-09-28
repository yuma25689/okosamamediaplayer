package okosama.app.tab;

import java.util.ArrayList;
import java.util.HashMap;
// import java.util.HashMap;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.action.IViewAction;
import okosama.app.behavior.IBehavior;

import android.app.Activity;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * �^�u��̍��ڂ̐e�ƂȂ邱�Ƃ��ł���N���X�̒��ۃN���X
 * @author 25689
 *
 */
public abstract class TabComponentParent implements ITabComponent {

	Activity activity;	
	public TabComponentParent()
	{
	}
	public TabComponentParent( Activity activity )
	{
		this.activity = activity;		
	}
	protected String name;
	public void setName(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return this.name;
	}
	protected boolean active = false;
	public boolean isActive() {
		return active;
	}
	public void setActiveFlg( boolean b ) {
		active = b;
	}

	ViewGroup tabBaseLayout;
	
	// �q���ڂ̃��X�g
	// �{���͍ŏ���add���鎞�ɍ�����������ʂ��Ȃ��͂������A������̕������S�ł͂���
	protected HashMap<Integer,ITabComponent> children 
		= new HashMap<Integer,ITabComponent>();
	
	// �����ɃR���e�i�����̂́A��肪�G��������Ȃ��B
	// ���ʓI�ɂ��̃N���X�̓^�u�ƃ^�u�y�[�W�ɂ����p������Ă��Ȃ����A
	// �q�ł�����g��Ȃ��N���X�������ł���̂���
	// �݌v�Ƃ��Ă͂悭�Ȃ�
	protected LinearLayout pageContainer;
	protected ViewGroup componentContainer;

	protected Tab parent;
	protected SparseArray< IViewAction > actionMap;
	public void setActionMap(SparseArray< IViewAction > actionMap) {
		this.actionMap = actionMap;
	}
	
	/**
	 * View�̎擾
	 * @return view
	 */
	public View getView() {
		return null;
	}
	/**
	 * Active���ǂ�����ݒ�B�q�̓��֐����R�[������
	 * @param b
	 */	
	public void setActivate( boolean b )
	{
		active = b;
        for( ITabComponent c : children.values() ) {
        	c.setActivate( b );
        }
	}
	/**
	 * Visible���ǂ�����ݒ�B�q�̓��֐����R�[������
	 * @param b
	 */	
	public void setVisible( boolean b )
	{
        for( ITabComponent c : children.values() ) {
        	c.setVisible( b );
        }	
	}
	/**
	 * �\���̍X�V�B�q�̓��֐����R�[������
	 */
	public void updateDisplay() {
        for( ITabComponent c : children.values() ) {
        	c.updateDisplay();
        }
	}

	/**
	 * �q���ڂ̒ǉ�
	 * @param child
	 */
	public void addChild( int tabId, ITabComponent child ) {
		children.put(tabId,child);
	}

	/**
	 * �O���I�u�W�F�N�g�ɐݒ���Ϗ�����
	 * �q�̃I�u�W�F�N�g�ɂ͓K�p���Ȃ�
	 * @param conf
	 */
	public void acceptConfigurator( ITabComponentConfigurator conf )
	{
		conf.configure(this);
	}
	/**
	 * @param pageContainer the pageContainer to set
	 */
	public void setPageContainer(LinearLayout pageContainer) {
		this.pageContainer = pageContainer;
	}	
	public void setComponentContainer(RelativeLayout componentContainer) {
		this.componentContainer = componentContainer;
	}
	/**
	 * �ݒ肳�ꂽ�A�N�V�����̓��e���A�R���|�[�l���g�ɐݒ肷��
	 */
	public void configureAction()
	{
	}
	
	public IBehavior getBehavior()
	{
		return null;
	}
	/**
	 * �q���ڂ̃N���A
	 */
//	public void clearChild() {
//		children.clear();
//	}	
}
