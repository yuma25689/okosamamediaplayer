package okosama.app.tab;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;


public class TabViewTouchListener implements OnTouchListener {
	
	int orgX;
	int orgY;
	
	public TabViewTouchListener( int orgX, int orgY )
	{
		this.orgX = orgX;
		this.orgY = orgY;
	}
	
    int currentX;   //Viewの左辺座標：X軸
    int currentY;   //Viewの上辺座標：Y軸
    int offsetX;    //画面タッチ位置の座標：X軸
    int offsetY;    //画面タッチ位置の座標：Y軸

	@Override
    public boolean onTouch(View view, MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
         
        switch(event.getAction()) {
        case MotionEvent.ACTION_MOVE:
        	int diffX = offsetX - x;
            int diffY = offsetY - y;
             
            currentX -= diffX;
            //currentY -= diffY;
            view.layout(currentX, currentY, currentX + view.getWidth(), currentY + view.getHeight());
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
		return true;
	}

}
