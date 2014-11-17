package okosama.app.panel;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R.color;
import okosama.app.action.IViewAction;
import okosama.app.action.TabSelectAction;
import okosama.app.tab.ITabComponent;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPagePlay;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
// import android.view.View;

public class TouchHookRelativeLayout extends RelativeLayout {

	static final int INFO_SHOW_PANEL_WIDTH_DIP = 150;
	static final double FLICK_MOVE_SPEED = 1;
	static final int TOUCH_RECOGNIZE_SPOT_SIZE = 100; 
	
	MoveTabInfo nextMoveTabInfo = null;
	
	boolean bLeftShow = false;
	boolean bRightShow = false;
	
	public static final int SHOW_MOVEINFO_RECOGNIZE_PLAY_LEFT = 5;
	public static final int SHOW_MOVEINFO_RECOGNIZE_PLAY_RIGHT = 5;
	// �t���b�N�łǂꂾ������������ŗ�������ׂ̃^�u�ֈړ����邩
	public static final int MOVE_RECOGNIZE_PLAY_LEFT = 140;
	public static final int MOVE_RECOGNIZE_PLAY_RIGHT =140;

	SparseArray<MoveTabInfo> mapMoveTabIdIdx = new SparseArray<MoveTabInfo>();
	public void setMoveTabInfo( int idx, MoveTabInfo tabInfo )
	{
		mapMoveTabIdIdx.put( idx, tabInfo );
	}
	public void clearMoveTabInfo()
	{
		mapMoveTabIdIdx.clear();
	}
	static final int PLAY = 20;
	int orgX = 0;
	int orgY = 0;
	int firstX = 0;
	int firstY = 0;
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
		
        switch(event.getAction() & MotionEvent.ACTION_MASK ) {
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
		
        switch(event.getAction() & MotionEvent.ACTION_MASK ) {
        case MotionEvent.ACTION_MOVE:
        	int diffX = offsetX - x;
            // int diffY = offsetY - y;
            currentX -= diffX;
            offsetX = x;
            offsetY = y;
            //boolean bShow = false;
            // bRet = true;
            if( bLeftShow == false && SHOW_MOVEINFO_RECOGNIZE_PLAY_RIGHT < orgX - currentX )
            {
                // �E�ֈ��ȏ�͂Ȃꂽ��^�u�̈ړ��̕\�����J�n
            	//bShow = 
            	updateTabInfoPanel( MoveTabInfo.RIGHT_1 );
            }
            else if( bRightShow == false && SHOW_MOVEINFO_RECOGNIZE_PLAY_LEFT < orgX + currentX )
            {
                // ���ֈ��ȏ�͂Ȃꂽ��^�u�̈ړ��̕\�����J�n
            	//bShow = 
            	updateTabInfoPanel( MoveTabInfo.LEFT_1 );
            }
			layout( currentX, currentY, currentX + getWidth(), currentY + getHeight() );
//            if( bShow == false )
//            {
//            	// ����ȊO�̏ꍇ�A�N���A�H
//                // �S�Ẵ^�u�ړ��p�l�������N���A����
//                clearAllMoveTabInfoPanel();            	
//            }
        	break;
	    case MotionEvent.ACTION_DOWN:
	    	firstX = x;
	    	firstY = y;
	        break;
	    case MotionEvent.ACTION_UP:
            layout(orgX, orgY, orgX + getWidth(), orgY + getHeight());
            // �K�v������ꍇ�A�^�u�̈ړ����s��
	    	if( nextMoveTabInfo != null )
	    	{
	    		IViewAction action = new TabSelectAction( 
	    				nextMoveTabInfo.getTabId(),
	    				nextMoveTabInfo.getTabPageId() );
	    		action.doAction(null);
	    		nextMoveTabInfo = null;
	    	}
            // �S�Ẵ^�u�ړ��p�l�������N���A����
            clearAllMoveTabInfoPanel();
            
            // �����ɓ����͎̂c�O�����APlay�^�u�̏ꍇ�A
            // �K�v������΃r���[�^�b�v���ɃR���p�l�\������������
            ITabComponent page = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getCurrentTabPage();
            if( page != null && page instanceof TabPagePlay )
            {
    			Rect outRect = new Rect(
    					firstX - TOUCH_RECOGNIZE_SPOT_SIZE/2,
    					firstY - TOUCH_RECOGNIZE_SPOT_SIZE/2,
    					firstX - TOUCH_RECOGNIZE_SPOT_SIZE/2 + TOUCH_RECOGNIZE_SPOT_SIZE,
    					firstY - TOUCH_RECOGNIZE_SPOT_SIZE/2 + TOUCH_RECOGNIZE_SPOT_SIZE);
    			// outRect.inset(-1*CLICKABLE_OFFSET, -1*CLICKABLE_OFFSET);
    			if( outRect.contains(x,y) )
    			{	            	
	            	TabPagePlay pagePlay = (TabPagePlay) page;
	            	pagePlay.updateControlPanelPlay(!pagePlay.getPanelShowPlay());
    			}
            }
            
	        break;
	    }
	    return bRet;
	}
	
	RelativeLayout rlPanel = null;
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
	    				//TabMoveRightInfoPanel.insertToLayout(this);
	                	bRightShow = true;
	        			rlPanel = (RelativeLayout) findViewById( ti.getPanelId() );
	    			}
	    			else if( ti.tabInfoIndex == MoveTabInfo.LEFT_1 && bRightShow == false )
	    			{
	    				//TabMoveLeftInfoPanel.insertToLayout(this);
	                	bLeftShow = true;	    				
	        			rlPanel = (RelativeLayout) findViewById( ti.getPanelId() );
	    			}
    			}
            	// �^�u�擾
        		if( rlPanel != null )
        		{
        			/*
        			if( ti.isShowing() == false )
        			{
	        			// �p�l���̃C���[�W��ݒ�
	        			ImageView iv = (ImageView) rlPanel.findViewById( ti.getImageViewId() );
	        			if( false )//iv != null )
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
	        					RelativeLayout.LayoutParams lpIv = (LayoutParams) iv.getLayoutParams();
        						
	        					switch( ti.getImageVertialAlign() )
	        					{
	        					case MoveTabInfo.VERTIAL_CENTER:
	        						lpIv.addRule(RelativeLayout.CENTER_VERTICAL);
	        						break;
	        					case MoveTabInfo.VERTIAL_TOP:
	        						lpIv.addRule(RelativeLayout.ALIGN_PARENT_TOP);
	        						break;
	        					case MoveTabInfo.VERTIAL_BOTTOM:
	        						lpIv.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	        						break;
	        					}
	        					
	        				}
	        			}
            			// �p�l����\��
	        			rlPanel.setVisibility(View.VISIBLE);
        			}*/
    				int parent_width = getWidth();
    				int border_right = OkosamaMediaPlayerActivity.dispInfo.getCorrectionXConsiderDensity(
    						MOVE_RECOGNIZE_PLAY_RIGHT
    				);
    				int border_left = OkosamaMediaPlayerActivity.dispInfo.getCorrectionXConsiderDensity(
    						MOVE_RECOGNIZE_PLAY_LEFT
    				);
    				int width = OkosamaMediaPlayerActivity.dispInfo.getCorrectionXConsiderDensity( 
    						INFO_SHOW_PANEL_WIDTH_DIP );
//					currentX < 0 ? currentX * -1 : currentX );
					int x = 0;
					if( currentX < 0 )
					{
						x = OkosamaMediaPlayerActivity.dispInfo.getCorrectionXConsiderDensity( currentX ) + parent_width; 
					}
					else
					{
						x = -1 * width + currentX;
					}
					// �Ȃ�ׂ��ȒP�ȃt���b�N�œ��B����悤�ɁA�ړ��ʂ�FLICK_MOVE_SPEED�{����
					x = (int)(x * FLICK_MOVE_SPEED);
		            x = limitMaxCurrentX( x );
		            
//    				int y = OkosamaMediaPlayerActivity.dispInfo.getCorrectionYConsiderDensity(
//    						currentY);
//    				int height = OkosamaMediaPlayerActivity.dispInfo.getCorrectionYConsiderDensity(
//    						currentY ) + getHeight();
    				
    				// rlPanel.layout(x, y, x + width, y + height);

    				if( ti.isShowing() == true )
    				{
        				//rlPanel.layout(x, y, x + width, y + height);
    				}
    				else
    				{
    				
//	    				RelativeLayout.LayoutParams lp = (LayoutParams) rlPanel.getLayoutParams();
//	    				lp.leftMargin = x;
//	    				lp.topMargin = y;
//	    				lp.width = width;
//	    				lp.height = height;
//	    				rlPanel.setLayoutParams(lp);
//    				
//                      	ti.setShowing(true);	    				
    				}
                	if( ( bRightShow &&  border_right < -1 * currentX ) 
                	|| ( bLeftShow && border_left < width + x ) )
                	{
                		// ����������ړ�����ꍇ
                		// �w�i�̓����x��������
                		rlPanel.setBackgroundColor(
                			OkosamaMediaPlayerActivity.getResourceAccessor().getColor(color.move_info_move));
                		nextMoveTabInfo = ti;
                	}
                	else
                	{
                		Log.d("x","=" + x);
                		Log.d("width","=" + width);
                		// �����łȂ�
                		rlPanel.setBackgroundColor(
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
            			// rl.setVisibility(View.GONE);
        				int parent_width = getWidth();    					
        				int width = OkosamaMediaPlayerActivity.dispInfo.getCorrectionXConsiderDensity( 
        						INFO_SHOW_PANEL_WIDTH_DIP );    					
    					int x = 0;
    					if( currentX < 0 )
    					{
    						x = parent_width; 
    					}
    					else
    					{
    						x = -1 * width;
    					}
    					// �Ȃ�ׂ��ȒP�ȃt���b�N�œ��B����悤�ɁA�ړ��ʂ�FLICK_MOVE_SPEED�{����
        				int y = OkosamaMediaPlayerActivity.dispInfo.getCorrectionYConsiderDensity(
        						currentY);
        				int height = OkosamaMediaPlayerActivity.dispInfo.getCorrectionYConsiderDensity(
        						currentY ) + getHeight();
        				
        				//rl.layout(x, y, x + width, y + height);
        				
        				RelativeLayout.LayoutParams lp = (LayoutParams) rl.getLayoutParams();
        				lp.leftMargin = x;
        				lp.topMargin = y;
        				lp.width = 0;	// ��\��
        				lp.height = height;
        				rl.setLayoutParams(lp);
    					
        				// TabMoveLeftInfoPanel.removeToLayout(this);
        				// TabMoveRightInfoPanel.removeToLayout(this);            			
            			ti.setShowing(false);
    				}
        		}
    		}
			layout( orgX, orgY, orgX + getWidth(), orgY + getHeight() );
			
			bRightShow = false;
			bLeftShow = false;
			nextMoveTabInfo = null;
			Log.d("clear","movetabinfo");
		}
	}
	
}
