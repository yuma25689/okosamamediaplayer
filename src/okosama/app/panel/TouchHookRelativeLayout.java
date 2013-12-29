package okosama.app.panel;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R.color;
import okosama.app.action.IViewAction;
import okosama.app.action.TabSelectAction;
import okosama.app.tab.Tab;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
// import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class TouchHookRelativeLayout extends RelativeLayout {

	static final int INFO_SHOW_PANEL_WIDTH_DIP = 150;
	static final double FLICK_MOVE_SPEED = 1;
	
	MoveTabInfo nextMoveTabInfo = null;
	
	boolean bLeftShow = false;
	boolean bRightShow = false;
	
	public static final int SHOW_MOVEINFO_RECOGNIZE_PLAY_LEFT = 5;
	public static final int SHOW_MOVEINFO_RECOGNIZE_PLAY_RIGHT = 5;
	// フリックでどれだけ動かした後で離したら隣のタブへ移動するか
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
            //boolean bShow = false;
            // bRet = true;
            if( bLeftShow == false && SHOW_MOVEINFO_RECOGNIZE_PLAY_RIGHT < orgX - currentX )
            {
                // 右へ一定以上はなれたらタブの移動の表示を開始
            	//bShow = 
            	updateTabInfoPanel( MoveTabInfo.RIGHT_1 );
            }
            else if( bRightShow == false && SHOW_MOVEINFO_RECOGNIZE_PLAY_LEFT < orgX + currentX )
            {
                // 左へ一定以上はなれたらタブの移動の表示を開始
            	//bShow = 
            	updateTabInfoPanel( MoveTabInfo.LEFT_1 );
            }
			layout( currentX, currentY, currentX + getWidth(), currentY + getHeight() );
//            if( bShow == false )
//            {
//            	// それ以外の場合、クリア？
//                // 全てのタブ移動パネル情報をクリアする
//                clearAllMoveTabInfoPanel();            	
//            }
        	break;
	    case MotionEvent.ACTION_DOWN:
	        break;
	    case MotionEvent.ACTION_UP:
            layout(orgX, orgY, orgX + getWidth(), orgY + getHeight());
            // 必要がある場合、タブの移動を行う
	    	if( nextMoveTabInfo != null )
	    	{
	    		IViewAction action = new TabSelectAction( 
	    				nextMoveTabInfo.getTabId(),
	    				nextMoveTabInfo.getTabPageId() );
	    		action.doAction(null);
	    		nextMoveTabInfo = null;
	    	}
	    	
            // 全てのタブ移動パネル情報をクリアする
            clearAllMoveTabInfoPanel();
	        break;
	    }
	    return bRet;
	}
	
	RelativeLayout rlPanel = null;
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
            	// タブ取得
        		if( rlPanel != null )
        		{
        			/*
        			if( ti.isShowing() == false )
        			{
	        			// パネルのイメージを設定
	        			ImageView iv = (ImageView) rlPanel.findViewById( ti.getImageViewId() );
	        			if( false )//iv != null )
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
            			// パネルを表示
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
					// なるべく簡単なフリックで到達するように、移動量をFLICK_MOVE_SPEED倍する
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
                		// 今離したら移動する場合
                		// 背景の透明度を下げる
                		rlPanel.setBackgroundColor(
                			OkosamaMediaPlayerActivity.getResourceAccessor().getColor(color.move_info_move));
                		nextMoveTabInfo = ti;
                	}
                	else
                	{
                		Log.d("x","=" + x);
                		Log.d("width","=" + width);
                		// そうでない
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
    					// なるべく簡単なフリックで到達するように、移動量をFLICK_MOVE_SPEED倍する
        				int y = OkosamaMediaPlayerActivity.dispInfo.getCorrectionYConsiderDensity(
        						currentY);
        				int height = OkosamaMediaPlayerActivity.dispInfo.getCorrectionYConsiderDensity(
        						currentY ) + getHeight();
        				
        				//rl.layout(x, y, x + width, y + height);
        				
        				RelativeLayout.LayoutParams lp = (LayoutParams) rl.getLayoutParams();
        				lp.leftMargin = x;
        				lp.topMargin = y;
        				lp.width = 0;	// 非表示
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
