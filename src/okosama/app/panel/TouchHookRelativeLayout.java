package okosama.app.panel;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.widget.Button;
import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class TouchHookRelativeLayout extends RelativeLayout {

	// �t���b�N�łǂꂾ������������ŗ�������ׂ̃^�u�ֈړ����邩
	public static final int MOVE_RECOGNIZE_PLAY_LEFT = 100;
	public static final int MOVE_RECOGNIZE_PLAY_RIGHT = 100;
		
	public class MoveTabInfo
	{
		/**
		 * @return the tabId
		 */
		public int getTabId() {
			return tabId;
		}
		/**
		 * @param tabId the tabId to set
		 */
		public void setTabId(int tabId) {
			this.tabId = tabId;
		}
		// �^�u���̃C���f�b�N�X
		public static final int LEFT_1 = 1;
		public static final int RIGHT_1 = 2;
				
		//Button btnTabBtn;
		Integer tabId = null;
		Integer panelId = null;
		/**
		 * @return the panelId
		 */
		public Integer getPanelId() {
			return panelId;
		}
		/**
		 * @param panelId the panelId to set
		 */
		public void setPanelId(Integer panelId) {
			this.panelId = panelId;
		}
		/**
		 * @return the imageViewId
		 */
		public Integer getImageViewId() {
			return imageViewId;
		}
		/**
		 * @param imageViewId the imageViewId to set
		 */
		public void setImageViewId(Integer imageViewId) {
			this.imageViewId = imageViewId;
		}
		Integer imageViewId = null;
		Integer tabImageResId = null;
		boolean showing = false;
		/**
		 * @return the showing
		 */
		public boolean isShowing() {
			return showing;
		}
		/**
		 * @param showing the showing to set
		 */
		public void setShowing(boolean showing) {
			this.showing = showing;
		}
		/**
		 * @return the tabImageResId
		 */
		public Integer getTabImageResId() {
			return tabImageResId;
		}
		/**
		 * @param tabImageResId the tabImageResId to set
		 */
		public void setTabImageResId(int tabImageResId) {
			this.tabImageResId = tabImageResId;
		}
	}

	SparseArray<MoveTabInfo> mapMoveTabIdIdx = new SparseArray<MoveTabInfo>();
	public void setMoveTabIdIdx( int idx, MoveTabInfo tabInfo )
	{
		mapMoveTabIdIdx.put( idx, tabInfo );
	}
	
	
	static final int PLAY = 20;
	int orgX = 0;
	int orgY = 0;
    int currentX;   //View�̍��Ӎ��W�FX��
    int currentY;   //View�̏�Ӎ��W�FY��
    int offsetX;    //��ʃ^�b�`�ʒu�̍��W�FX��
    int offsetY;    //��ʃ^�b�`�ʒu�̍��W�FY��
	
	public TouchHookRelativeLayout(Context context) {
		super(context);
	}
    public TouchHookRelativeLayout(Context context, AttributeSet att) {
        super(context, att);
    }
     	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event)  {
	    // �^�b�`���ꂽ��܂�onInterceptTouchEvent���Ă΂��
	    // ������true��Ԃ��ΐeView��onTouchEvent
	    // ������false��Ԃ��ΎqView��onClick���onLongClick���
		boolean bRet = false;
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
		
        switch(event.getAction()) {
        case MotionEvent.ACTION_MOVE:
        	int diffX = offsetX - x;
            int diffY = offsetY - y;
            if( PLAY < Math.abs( diffX ) && Math.abs( diffY ) < Math.abs( diffX ) )
            {
            	bRet = true;
            }
            else
            {
            	bRet = false;
            }
        	break;
	    case MotionEvent.ACTION_DOWN:
	        currentX = getLeft();
	        currentY = getTop();
	        offsetX = x;
	        offsetY = y;
	    	
	        break;
	    case MotionEvent.ACTION_UP:
            // layout(orgX, orgY, orgX + getWidth(), orgY + getHeight());
            // bRet = true;
	        break;
	    }		
	     
	    return bRet;
	}
	 
	@Override
	public boolean onTouchEvent(MotionEvent event)  {
	    // ������true��Ԃ��ƃC�x���g�͂����ŏI��
	    // ������false��Ԃ��ƎqView��onClick���onLongClick���
		boolean bRet = true;
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
		
        switch(event.getAction()) {
        case MotionEvent.ACTION_MOVE:
        	int diffX = offsetX - x;
            // int diffY = offsetY - y;
            currentX -= diffX;
            layout(currentX, currentY, currentX + getWidth(), currentY + getHeight());
            offsetX = x;
            offsetY = y;
            // bRet = true;
            if( MOVE_RECOGNIZE_PLAY_RIGHT < orgX - currentX )
            {
                // �E�ֈ��ȏ�͂Ȃꂽ��
            	// �����ŗ����ƉE�̃^�u�ֈړ�����Ƃ����̂����[�U�ɕ\��
            	showTabInfoPanel( MoveTabInfo.RIGHT_1 );
            }
            else if( MOVE_RECOGNIZE_PLAY_LEFT < orgX + currentX )
            {
                // ���ֈ��ȏ�͂Ȃꂽ��
            	// �����ŗ����ƍ��̃^�u�ֈړ�����Ƃ����̂����[�U�ɕ\��
            	showTabInfoPanel( MoveTabInfo.LEFT_1 );
            }
            else
            {
            	// ����ȊO�̏ꍇ�A�N���A�H
                // �S�Ẵ^�u�ړ��p�l�������N���A����
                clearAllMoveTabInfoPanel();            	
            }
        	break;
	    case MotionEvent.ACTION_DOWN:
	        break;
	    case MotionEvent.ACTION_UP:
            layout(orgX, orgY, orgX + getWidth(), orgY + getHeight());
            // TODO: �K�v������ꍇ�A�^�u�̈ړ����s��
            // �S�Ẵ^�u�ړ��p�l�������N���A����
            clearAllMoveTabInfoPanel();
	        break;
	    }
	    return bRet;
	}
	
	/**
	 * �ړ���^�u���\���p�l���̐ݒ�
	 * @param iMoveTabIdx
	 */
	public void showTabInfoPanel(int iMoveTabIdx)
	{
		// �E�̃p�l���ɕ\����������擾����
    	if( 0 < mapMoveTabIdIdx.indexOfKey( iMoveTabIdx ))//MoveTabInfo.RIGHT_1 ))
    	{
    		MoveTabInfo ti = mapMoveTabIdIdx.get( iMoveTabIdx );
    		if( ti != null 
    				&& ti.isShowing() == false 
    				&& ti.getPanelId() != null
    				&& ti.getImageViewId() != null )
    		{
    			// �܂��E�̃^�u���p�l�����\������Ă��Ȃ����
            	// �E�̃^�u��
        		RelativeLayout rl = (RelativeLayout) findViewById( ti.getPanelId() );
        		if( rl != null )
        		{
        			// �p�l���̃C���[�W��ݒ�
        			ImageView iv = (ImageView) rl.findViewById( ti.getImageViewId() );
        			if( iv != null )
        			{
        				if( ti.getTabImageResId() != null )
        				{
        					// �ړ���^�u�̃C���[�W���擾�ł����ꍇ
        					// ������A�ݒ肷��
        					iv.setImageDrawable(
        							OkosamaMediaPlayerActivity.
        							getResourceAccessor().
        							getResourceDrawable(
        									ti.getTabImageResId()));
        				}
            			// �E�̃p�l����\��
            			rl.setVisibility(View.VISIBLE);
                    	ti.setShowing(true);        				
        			}
        		}
    		}
    	}
		
	}
	/**
	 * ���̃��C�A�E�g�ŕ\���\�ȑS�Ă̈ړ��^�u�����N���A����
	 */
	public void clearAllMoveTabInfoPanel()
	{
		for( int i=0; i < mapMoveTabIdIdx.size(); i++ )
		{
			MoveTabInfo ti = mapMoveTabIdIdx.get(i);
			if( ti != null 
    				&& ti.isShowing() == true 
    				&& ti.getPanelId() != null
    				&& ti.getImageViewId() != null )
    		{
    			// �^�u���p�l�����\������Ă�����
        		RelativeLayout rl = (RelativeLayout) findViewById( ti.getPanelId() );
        		if( rl != null )
        		{
        			// �p�l���̃C���[�W���N���A
        			ImageView iv = (ImageView) rl.findViewById( ti.getImageViewId() );
        			if( iv != null )
        			{
    					iv.setImageDrawable(null);
            			// �p�l�����\��
            			rl.setVisibility(View.GONE);
            			ti.setShowing(false);
    				}
        		}
    		}		
		}
	}
	
}
