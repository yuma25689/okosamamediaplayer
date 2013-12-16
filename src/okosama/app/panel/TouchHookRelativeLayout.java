package okosama.app.panel;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R.color;
import okosama.app.tab.Tab;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class TouchHookRelativeLayout extends RelativeLayout {

	static final int INFO_SHOW_PANEL_WIDTH_DIP = 200;
	
	MoveTabInfo nextMoveTabInfo = null;
	
	boolean bLeftShow = false;
	boolean bRightShow = false;
	
	public static final int SHOW_MOVEINFO_RECOGNIZE_PLAY_LEFT = 5;
	public static final int SHOW_MOVEINFO_RECOGNIZE_PLAY_RIGHT = 5;
	// �t���b�N�łǂꂾ������������ŗ�������ׂ̃^�u�ֈړ����邩
	public static final int MOVE_RECOGNIZE_PLAY_LEFT = 180;
	public static final int MOVE_RECOGNIZE_PLAY_RIGHT =180;

	SparseArray<MoveTabInfo> mapMoveTabIdIdx = new SparseArray<MoveTabInfo>();
	public void setMoveTabInfo( int idx, MoveTabInfo tabInfo )
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
            offsetX = x;
            offsetY = y;
            boolean bShow = false;
            // bRet = true;
            if( SHOW_MOVEINFO_RECOGNIZE_PLAY_RIGHT < orgX - currentX )
            {
                // �E�ֈ��ȏ�͂Ȃꂽ��^�u�̈ړ��̕\�����J�n
            	bShow = updateTabInfoPanel( MoveTabInfo.RIGHT_1 );
            }
            else if( SHOW_MOVEINFO_RECOGNIZE_PLAY_LEFT < orgX + currentX )
            {
                // ���ֈ��ȏ�͂Ȃꂽ��^�u�̈ړ��̕\�����J�n
            	bShow = updateTabInfoPanel( MoveTabInfo.LEFT_1 );
            }
            if( bShow == false )
            {
            	// ����ȊO�̏ꍇ�A�N���A�H
                // �S�Ẵ^�u�ړ��p�l�������N���A����
                clearAllMoveTabInfoPanel();            	
            }
        	break;
	    case MotionEvent.ACTION_DOWN:
	        break;
	    case MotionEvent.ACTION_UP:
            // layout(orgX, orgY, orgX + getWidth(), orgY + getHeight());
            // TODO: �K�v������ꍇ�A�^�u�̈ړ����s��
            // �S�Ẵ^�u�ړ��p�l�������N���A����
            clearAllMoveTabInfoPanel();
	        break;
	    }
	    return bRet;
	}
	
	/**
	 * �ړ���^�u���\���p�l���̐ݒ�
	 * @return true:���� false:���s
	 * @param iMoveTabIdx
	 */
	public boolean updateTabInfoPanel(int iMoveTabIdx)
	{
		// �p�l���ɕ\����������擾����
    	if( 0 <= mapMoveTabIdIdx.indexOfKey( iMoveTabIdx ))//MoveTabInfo.RIGHT_1 ))
    	{
    		MoveTabInfo ti = mapMoveTabIdIdx.get( iMoveTabIdx );
    		if( ti != null 
    				// && ti.isShowing() == false
    				&& ti.getTabId() != null
    				&& ti.getTabPageId() != null    				
    				&& ti.getPanelId() != null
    				&& ti.getImageViewId() != null )
    		{
    			// �^�u�����b�N���Ȃ�΁A�ړ��ł��Ȃ����̂Ƃ���
    			Tab tab = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getTabStocker().getTab(ti.getTabId());
    			if( tab.isLocking() )
    			{
    				return false;
    			}
    			
    			// �܂��^�u���p�l�����\������Ă��Ȃ����
    			if( ti.isShowing() == false )
    			{
	    			// TODO:���Ԃ�A���ʉ��ł���
	    			if( ti.tabInfoIndex == MoveTabInfo.RIGHT_1 && bLeftShow == false )
	    			{
	    				TabMoveRightInfoPanel.insertToLayout(this);
	                	bRightShow = true;
	    			}
	    			else if( ti.tabInfoIndex == MoveTabInfo.LEFT_1 && bRightShow == false )
	    			{
	    				TabMoveLeftInfoPanel.insertToLayout(this);
	                	bLeftShow = true;	    				
	    			}
    			}
            	// �^�u�擾
        		RelativeLayout rl = (RelativeLayout) findViewById( ti.getPanelId() );
        		if( rl != null )
        		{
        			if( ti.isShowing() == false )
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
	        			}
        				// rl.layout( currentX, currentY, currentX + getWidth(), currentY + getHeight() );
            			// �p�l����\��
            			rl.setVisibility(View.VISIBLE);
        			}
    				int parent_width = getWidth();
    				int width = OkosamaMediaPlayerActivity.dispInfo.getCorrectionXConsiderDensity( 
    						INFO_SHOW_PANEL_WIDTH_DIP );
//					currentX < 0 ? currentX * -1 : currentX );
					int x = OkosamaMediaPlayerActivity.dispInfo.getCorrectionXConsiderDensity(
						currentX < 0 ? parent_width + currentX : -1 * width + currentX );
		            x = limitMaxCurrentX( x );
		            
    				int y = OkosamaMediaPlayerActivity.dispInfo.getCorrectionYConsiderDensity(
    						currentY);
    				int height = OkosamaMediaPlayerActivity.dispInfo.getCorrectionYConsiderDensity(
    						currentY + getHeight());
    				
    				//rl.layout(x, y, x + width, y + height);
    				
    				RelativeLayout.LayoutParams lp = (LayoutParams) rl.getLayoutParams();
    				lp.leftMargin = x;
    				lp.topMargin = y;
    				lp.width = width;
    				lp.height = height;
    				rl.setLayoutParams(lp);
    				if( ti.isShowing() == false )
    				{
                      	ti.setShowing(true);	    				
    				}
                	if( ( bRightShow &&  MOVE_RECOGNIZE_PLAY_RIGHT < -1 * currentX ) 
                	|| ( bLeftShow && MOVE_RECOGNIZE_PLAY_LEFT < width + x ) )
                	{
                		// ����������ړ�����ꍇ
                		// �w�i�̓����x��������
                		rl.setBackgroundColor(
                				OkosamaMediaPlayerActivity.getResourceAccessor().getColor(color.move_info_move));
                		nextMoveTabInfo = ti;
                	}
                	else
                	{
                		// �����łȂ�
                		rl.setBackgroundColor(
                				OkosamaMediaPlayerActivity.getResourceAccessor().getColor(color.move_info_moving));
                		nextMoveTabInfo = null;
                	}
        		}
    		}
    	}
    	return true;
	}
	/**
	 * �w�肳�ꂽX�������𒴂��Ă�����A���̐��������ς��ɂ��Ė߂�
	 */
	public int limitMaxCurrentX(int currentX)
	{
		int iRet = currentX;
		
		if( bRightShow )
		{
			if( currentX < getWidth() - INFO_SHOW_PANEL_WIDTH_DIP )
			{
				iRet = getWidth() - INFO_SHOW_PANEL_WIDTH_DIP;
			}
		}
		else if( bLeftShow )
		{
			if( 0 < currentX )
			{
				iRet = 0;
			}
		}
		return iRet;
	}
	
	/**
	 * ���̃��C�A�E�g�ŕ\���\�ȑS�Ă̈ړ��^�u�����N���A����
	 */
	public void clearAllMoveTabInfoPanel()
	{
		for( int i=0; i < mapMoveTabIdIdx.size(); i++ )
		{
			MoveTabInfo ti = mapMoveTabIdIdx.valueAt(i);
			if( ti != null 
    				&& ti.isShowing() == true 
    				&& ti.getTabId() != null
    				&& ti.getTabPageId() != null
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
        				TabMoveLeftInfoPanel.removeToLayout(this);
        				TabMoveRightInfoPanel.removeToLayout(this);            			
            			ti.setShowing(false);
    				}
        		}
    		}
			bRightShow = false;
			bLeftShow = false;
			nextMoveTabInfo = null;
			Log.d("clear","movetabinfo");
		}
	}
	
}
