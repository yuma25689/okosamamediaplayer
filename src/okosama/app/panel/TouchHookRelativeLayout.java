package okosama.app.panel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class TouchHookRelativeLayout extends RelativeLayout {

	int orgX = 0;
	int orgY = 0;
    int currentX;   //Viewの左辺座標：X軸
    int currentY;   //Viewの上辺座標：Y軸
    int offsetX;    //画面タッチ位置の座標：X軸
    int offsetY;    //画面タッチ位置の座標：Y軸
	
	public TouchHookRelativeLayout(Context context) {
		super(context);
	}
    public TouchHookRelativeLayout(Context context, AttributeSet att) {
        super(context, att);
    }
     	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event)  {
	    // タッチされたらまずonInterceptTouchEventが呼ばれる
	    // ここでtrueを返せば親ViewのonTouchEvent
	    // ここでfalseを返せば子ViewのonClickやらonLongClickやら
		boolean bRet = false;
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
		
        switch(event.getAction()) {
        case MotionEvent.ACTION_MOVE:
        	int diffX = offsetX - x;
            int diffY = offsetY - y;
            if( 10 < Math.abs( diffX ) && Math.abs( diffY ) < Math.abs( diffX ) )
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
	    // ここでtrueを返すとイベントはここで終了
	    // ここでfalseを返すと子ViewのonClickやらonLongClickやら
		boolean bRet = false;
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
            bRet = true;
        	break;
	    case MotionEvent.ACTION_DOWN:
	        break;
	    case MotionEvent.ACTION_UP:
            layout(orgX, orgY, orgX + getWidth(), orgY + getHeight());
            // bRet = true;
	        break;
	    }		
	     
	    return bRet;
	}
}
