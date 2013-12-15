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

	// フリックでどれだけ動かした後で離したら隣のタブへ移動するか
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
		// タブ情報のインデックス
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
            layout(currentX, currentY, currentX + getWidth(), currentY + getHeight());
            offsetX = x;
            offsetY = y;
            // bRet = true;
            if( MOVE_RECOGNIZE_PLAY_RIGHT < orgX - currentX )
            {
                // 右へ一定以上はなれたら
            	// ここで離すと右のタブへ移動するというのをユーザに表示
            	showTabInfoPanel( MoveTabInfo.RIGHT_1 );
            }
            else if( MOVE_RECOGNIZE_PLAY_LEFT < orgX + currentX )
            {
                // 左へ一定以上はなれたら
            	// ここで離すと左のタブへ移動するというのをユーザに表示
            	showTabInfoPanel( MoveTabInfo.LEFT_1 );
            }
            else
            {
            	// それ以外の場合、クリア？
                // 全てのタブ移動パネル情報をクリアする
                clearAllMoveTabInfoPanel();            	
            }
        	break;
	    case MotionEvent.ACTION_DOWN:
	        break;
	    case MotionEvent.ACTION_UP:
            layout(orgX, orgY, orgX + getWidth(), orgY + getHeight());
            // TODO: 必要がある場合、タブの移動を行う
            // 全てのタブ移動パネル情報をクリアする
            clearAllMoveTabInfoPanel();
	        break;
	    }
	    return bRet;
	}
	
	/**
	 * 移動先タブ情報表示パネルの設定
	 * @param iMoveTabIdx
	 */
	public void showTabInfoPanel(int iMoveTabIdx)
	{
		// 右のパネルに表示する情報を取得する
    	if( 0 < mapMoveTabIdIdx.indexOfKey( iMoveTabIdx ))//MoveTabInfo.RIGHT_1 ))
    	{
    		MoveTabInfo ti = mapMoveTabIdIdx.get( iMoveTabIdx );
    		if( ti != null 
    				&& ti.isShowing() == false 
    				&& ti.getPanelId() != null
    				&& ti.getImageViewId() != null )
    		{
    			// まだ右のタブ情報パネルが表示されていなければ
            	// 右のタブへ
        		RelativeLayout rl = (RelativeLayout) findViewById( ti.getPanelId() );
        		if( rl != null )
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
            			// 右のパネルを表示
            			rl.setVisibility(View.VISIBLE);
                    	ti.setShowing(true);        				
        			}
        		}
    		}
    	}
		
	}
	/**
	 * このレイアウトで表示可能な全ての移動タブ情報をクリアする
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
            			ti.setShowing(false);
    				}
        		}
    		}		
		}
	}
	
}
