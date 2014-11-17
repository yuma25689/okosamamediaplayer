package okosama.app.tab;

// import java.util.HashMap;

import java.util.ArrayList;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.action.IViewAction;
import okosama.app.behavior.IBehavior;
import okosama.app.panel.MoveTabInfo;
import okosama.app.panel.TouchHookRelativeLayout;
import okosama.app.widget.absWidget;
import android.app.Activity;
import android.util.SparseArray;
import android.view.LayoutInflater;
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
	protected abstract int create( int panelLayoutId );
	public TabComponentParent()
	{
	}
	public TabComponentParent( Activity activity )
	{
		this.activity = activity;		
	}
	protected Integer internalID;
	@Override
	public void setInternalID(Integer internalID)
	{
		this.internalID = internalID;
	}
	@Override
	public Integer getInternalID()
	{
		return this.internalID;
	}
	protected boolean active = false;
	protected boolean enable = true;
	public boolean isEnabled() {
		return enable;
	}
	public boolean isActive() {
		return active;
	}
	public void setActiveFlg( boolean b ) {
		active = b;
	}

	protected ViewGroup tabBaseLayout = null;
	public ViewGroup getTabBaseLayout()
	{
		return tabBaseLayout;
	}
	protected ViewGroup updateProgressPanel = null;
	protected ArrayList<absWidget> widgets = new ArrayList<absWidget>();

	public void startUpdate()
	{
		if( null != updateProgressPanel )
		{
			updateProgressPanel.setVisibility(View.VISIBLE);
			for( absWidget widget : widgets )
			{
				if( widget.getView() != null )
				{
					if( widget.getVisibleFlag() == View.VISIBLE )
					{
						widget.getView().setVisibility(View.GONE);
					}
				}
			}
		}
	}
	public void endUpdate()
	{
		if( null != updateProgressPanel )
		{
			updateProgressPanel.setVisibility(View.GONE);
			for( absWidget widget : widgets )
			{
				if( widget.getView() != null )
				{
					if( widget.getVisibleFlag() == View.VISIBLE )
					{
						widget.setVisible(View.VISIBLE);
					}
				}
			}
		}
	}
	
	/**
	 * �w�肳�ꂽID�̃��C�A�E�g���쐬���A������widget���N���A����
	 * @param iPanelLayoutId
	 */
	protected void resetPanelViews(int iPanelLayoutId)
	{
		OkosamaMediaPlayerActivity act 
		= OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		LayoutInflater inflator = act.getLayoutInflater();
		tabBaseLayout = (ViewGroup)inflator.inflate(iPanelLayoutId, null, false);	
		widgets.clear();
	}
	protected void resetPanelViews(int iPanelLayoutId,ArrayList<MoveTabInfo> arrMti)
	{
		OkosamaMediaPlayerActivity act 
		= OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		LayoutInflater inflator = act.getLayoutInflater();
		TouchHookRelativeLayout thrl = (TouchHookRelativeLayout) inflator.inflate(iPanelLayoutId, null, false);
		thrl.clearAllMoveTabInfoPanel();
		thrl.clearMoveTabInfo();
		for( MoveTabInfo mti : arrMti )
		{
			thrl.setMoveTabInfo(mti.getTabInfoIndex(), mti);
		}
		tabBaseLayout = thrl;
		widgets.clear();
	}

	// �q���ڂ̃��X�g
	// �{���͍ŏ���add���鎞�ɍ�����������ʂ��Ȃ��͂������A������̕������S�ł͂���
	protected SparseArray<ITabComponent> children 
		= new SparseArray<ITabComponent>();
	
	// �����ɃR���e�i�����̂́A��肪�G��������Ȃ��B
	// ���ʓI�ɂ��̃N���X�̓^�u�ƃ^�u�y�[�W�ɂ����p������Ă��Ȃ����A
	// �q�ł�����g��Ȃ��N���X�������ł���̂���
	// �݌v�Ƃ��Ă͂悭�Ȃ�
	protected LinearLayout pageContainer;
	protected ViewGroup componentContainer;

	protected Tab parent;
	protected SparseArray< IViewAction > actionMap;
	@Override
	public void setActionMap(SparseArray< IViewAction > actionMap) {
		this.actionMap = actionMap;
	}
	
	/**
	 * View�̎擾
	 * @return view
	 */
	@Override
	public View getView() {
		return null;
	}
	/**
	 * Active���ǂ�����ݒ�B�q�̓��֐����R�[������
	 * @param b
	 */	
	@Override
	public void setActivate( boolean b )
	{
		active = b;
        for( int i=0; i < children.size(); i++ ) {
        	children.valueAt(i).setActivate( b );
        }
	}
	/**
	 * �g�p�\���ǂ�����ݒ�B�q�̓��֐����R�[������
	 * @param b
	 */	
	@Override
	public void setEnabled( boolean b )
	{
		enable = b;
        for( int i=0; i < children.size(); i++ ) {
        	children.valueAt(i).setEnabled( b );
        }
	}	
	/**
	 * Visible���ǂ�����ݒ�B�q�̓��֐����R�[������
	 * @param b
	 */	
	@Override
	public void setVisible( boolean b )
	{
		for( int i=0; i < children.size(); i++ ) {
			children.valueAt(i).setVisible( b );
        }	
	}
	/**
	 * �\���̍X�V�B�q�̓��֐����R�[������
	 */
	@Override
	public void updateDisplay() {
		for( int i=0; i < children.size(); i++ ) {
			children.valueAt(i).updateDisplay();
        }
	}

	/**
	 * �q���ڂ̒ǉ�
	 * @param child
	 */
	@Override
	public void addChild( int ID, ITabComponent child ) {
		children.put(ID,child);
	}

	public ITabComponent getChild( int ID )
	{
		if( 0 <= children.indexOfKey(ID) )
		{
			return children.get(ID);
		}
		return null;
	}
	
	
	/**
	 * �O���I�u�W�F�N�g�ɐݒ���Ϗ�����
	 * �q�̃I�u�W�F�N�g�ɂ͓K�p���Ȃ�
	 * @param conf
	 */
	@Override
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
	@Override
	public void configureAction()
	{
	}
	
	@Override
	public IBehavior getBehavior()
	{
		return null;
	}
	/**
	 * �q���ڂ̃N���A
	 */
	public void clearChild() {
		for( int i=0; i < children.size(); i++ ) {
			if( children.valueAt(i) instanceof TabComponentParent )
			{
				((TabComponentParent)children.valueAt(i)).clearWidgets();
			}
        }
		children.clear();
	}
	void clearWidgets()
	{
		widgets.clear();
	}
	
}
