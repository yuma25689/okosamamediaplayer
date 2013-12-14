package okosama.app.tab;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;


public class TabListViewTouchListener implements OnTouchListener {
	
	int orgX;
	int orgY;
	
	public TabListViewTouchListener( int orgX, int orgY )
	{
		this.orgX = orgX;
		this.orgY = orgY;
	}
	
    int currentX;   //View�̍��Ӎ��W�FX��
    int currentY;   //View�̏�Ӎ��W�FY��
    int offsetX;    //��ʃ^�b�`�ʒu�̍��W�FX��
    int offsetY;    //��ʃ^�b�`�ʒu�̍��W�FY��

	@Override
    public boolean onTouch(View view, MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        
        boolean bRet = false;
        switch(event.getAction()) {
        case MotionEvent.ACTION_MOVE:
        	int diffX = offsetX - x;
            // int diffY = offsetY - y;
             
            currentX -= diffX;
            // currentY -= diffY;
//            if( currentX > diffY )
//            {
//            	bRet = true;
//            	break;
//            }
            view.layout(currentX, currentY, 
            		currentX + view.getWidth(),
            		currentY + view.getHeight());
            offsetX = x;
            offsetY = y;        	
        	break;
	    case MotionEvent.ACTION_DOWN:
	        currentX = view.getLeft();
	        currentY = view.getTop();
	        offsetX = x;
	        offsetY = y;
	        break;
	    case MotionEvent.ACTION_UP:
            view.layout(orgX, orgY, orgX + view.getWidth(), orgY + view.getHeight());
	        break;
	    }		
		return bRet;
	}

}
