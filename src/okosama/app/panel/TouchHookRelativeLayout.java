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
	// フリックでどれだけ動かした後で離したら隣のタブへ移動するか
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
	    // ここでtrueを返すとイベントはここで終了
	    // ここでfalseを返すと子ViewのonClickやらonLongClickやら
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
                // 右へ一定以上はなれたらタブの移動の表示を開始
            	bShow = updateTabInfoPanel( MoveTabInfo.RIGHT_1 );
            }
            else if( SHOW_MOVEINFO_RECOGNIZE_PLAY_LEFT < orgX + currentX )
            {
                // 左へ一定以上はなれたらタブの移動の表示を開始
            	bShow = updateTabInfoPanel( MoveTabInfo.LEFT_1 );
            }
            if( bShow == false )
            {
            	// それ以外の場合、クリア？
                // 全てのタブ移動パネル情報をクリアする
                clearAllMoveTabInfoPanel();            	
            }
        	break;
	    case MotionEvent.ACTION_DOWN:
	        break;
	    case MotionEvent.ACTION_UP:
            // layout(orgX, orgY, orgX + getWidth(), orgY + getHeight());
            // TODO: 必要がある場合、タブの移動を行う
            // 全てのタブ移動パネル情報をクリアする
            clearAllMoveTabInfoPanel();
	        break;
	    }
	    return bRet;
	}
	
	/**
	 * 移動先タブ情報表示パネルの設定
	 * @return true:成功 false:失敗
	 * @param iMoveTabIdx
	 */
	public boolean updateTabInfoPanel(int iMoveTabIdx)
	{
		// パネルに表示する情報を取得する
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
    			// タブがロック中ならば、移動できないものとする
    			Tab tab = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getTabStocker().getTab(ti.getTabId());
    			if( tab.isLocking() )
    			{
    				return false;
    			}
    			
    			// まだタブ情報パネルが表示されていなければ
    			if( ti.isShowing() == false )
    			{
	    			// TODO:たぶん、共通化できる
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
            	// タブ取得
        		RelativeLayout rl = (RelativeLayout) findViewById( ti.getPanelId() );
        		if( rl != null )
        		{
        			if( ti.isShowing() == false )
        			{
	        			// パネルのイメージを設定
	        			ImageView iv = (ImageView) rl.findViewById( ti.getImageViewId() );
	        			if( iv != null )
	        			{
	        				if( ti.getTabImageResId() != null )
	        				{
	        					// 移動先タブのイメージが取得できた場合
	        					// それを、設定する
	        					iv.setImageDrawable(
	        							OkosamaMediaPlayerActivity.
	        							getResourceAccessor().
	        							getResourceDrawable(
	        									ti.getTabImageResId()));
	        				}
	        			}
        				// rl.layout( currentX, currentY, currentX + getWidth(), currentY + getHeight() );
            			// パネルを表示
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
                		// 今離したら移動する場合
                		// 背景の透明度を下げる
                		rl.setBackgroundColor(
                				OkosamaMediaPlayerActivity.getResourceAccessor().getColor(color.move_info_move));
                		nextMoveTabInfo = ti;
                	}
                	else
                	{
                		// そうでない
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
	 * 指定されたXが制限を超えていたら、その制限いっぱいにして戻す
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
	 * このレイアウトで表示可能な全ての移動タブ情報をクリアする
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
    			// タブ情報パネルが表示されていたら
        		RelativeLayout rl = (RelativeLayout) findViewById( ti.getPanelId() );
        		if( rl != null )
        		{
        			// パネルのイメージをクリア
        			ImageView iv = (ImageView) rl.findViewById( ti.getImageViewId() );
        			if( iv != null )
        			{
    					iv.setImageDrawable(null);    					
            			// パネルを非表示
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
