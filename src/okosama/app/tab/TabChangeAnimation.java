package okosama.app.tab;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.anim.TabAnimationFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/**
 * tabの変更が連打された時のために、タブの変更は、このクラスにキューとして持つようにする
 * @author 25689
 *
 */
public class TabChangeAnimation {

	Animation animIn = null;
	Animation animOut = null;
	
	int tabPageCnt[] = new int[12];
	
	// 処理内容
	public static final int TAB_IN = 1;
	public static final int TAB_OUT = 2;
	long outAnimDelay = 0;
	
	// 1回のタブセレクト処理ごとに、idを持つものとする
	final int MAX_TABSELECTION_PROCESS_ID = 10000;
	int tabSelectionProcessId = 0;
	int mostNewSelectionProcessId = 0;
	int lastProcessId = 0;
	final int MAX_ANIM_WAIT = 1000;
	// このフラグがたっているときに来たリクエストは、全て同じ選択処理の一部と見なす
	boolean bGroupingTabSelectionProc = false;
	public void SetTabSelectionLock(boolean b, int tabId)
	{
		if( bGroupingTabSelectionProc == false 
		&& b == true )
		{
			getNextTabSelectionId();
		}		
		// タブのロック
		Tab tab = null;
		// TODO: タブIDからタブを取得する関数を作成
		if( tabId == ControlIDs.TAB_ID_MAIN )
		{
			tab = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getTabStocker().getTab(ControlIDs.TAB_ID_MAIN);
		}
		else if( tabId == ControlIDs.TAB_ID_MEDIA )
		{
			tab = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getTabStocker().getTab(ControlIDs.TAB_ID_MEDIA);
		}
		else if( tabId == ControlIDs.TAB_ID_PLAY )
		{
			tab = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getTabStocker().getTab(ControlIDs.TAB_ID_PLAY);
		}
		if( tab != null )
		{
			tab.setEnableAllTab(!b);
		}
		bGroupingTabSelectionProc = b;
	}
	int getNextTabSelectionId()
	{
		if( bGroupingTabSelectionProc )
		{
			return tabSelectionProcessId;
		}
		if( false == handler.hasMessages(TAB_IN) 
		&& false == handler.hasMessages(TAB_OUT) )
		{
			tabSelectionProcessId = 0;
		}
		else
		{
			if( MAX_TABSELECTION_PROCESS_ID <= tabSelectionProcessId )
			{
				tabSelectionProcessId = 0;
			}
			else
			{
				tabSelectionProcessId++;
			}
		}
		return tabSelectionProcessId;
	}
	
	public class TabChangeAnimationTarget {
		TabChangeAnimationTarget(ViewGroup t,ViewGroup p)
		{
			target = t;
			parent = p;
		}
		ViewGroup target;
		ViewGroup parent;
	}
	public void requestTabMove( int iMoveType, ViewGroup target, ViewGroup parent, int tabID )
	{
		mostNewSelectionProcessId = getNextTabSelectionId();
		Message msg = handler.obtainMessage(
				iMoveType,
				mostNewSelectionProcessId,
				tabID,
				new TabChangeAnimationTarget( target, parent )
		);
		long delay = 1;
		if( outAnimDelay != 0 )
		{
			delay = outAnimDelay;
		}
		handler.sendMessageDelayed(msg, delay);
	}

    // サイズが取得できたら、下記の処理実行されるようにする
    Handler handler =  new Handler(){
        //メッセージ受信
        @Override
		public void handleMessage(Message message) {
        	if( message.what != TAB_IN
        	&&  message.what != TAB_OUT )
        	{
        		return;
        	}
//        	if( animEndWait == true )
//        	{
//        		// 処理が終わるまで待ってからもう一度
//        		Log.d("handler", "queue wait occured");
//        		// this.sendMessageAtFrontOfQueue(message);
//        		return;
//        	}
        	
        	// 最新のものだけを処理しようと思っていたが、そうすると、漏れが生じるので、とりあえずここはコメント
//    		if( message.arg1 != mostNewSelectionProcessId )
//    		{
//    			if( message.arg1 != lastProcessId )
//    			{
//    				Log.d("tab anim","through:" + message.arg2);
//    				// 最新じゃなく、既に処理中でもないものは無視
//    				return;
//    			}
//    			else
//    			{
//    				Log.d("tab anim","last process id:" + message.arg2 );
//    			}
//    		}
    		lastProcessId = message.arg1;
    		TabChangeAnimationTarget target = (TabChangeAnimationTarget)message.obj;
    		ViewGroup tabBaseLayout 		= target.target;
    		ViewGroup componentContainer 	= target.parent;
    		switch( message.what )
    		{
        	case TAB_IN:
    			//if( animIn == null )
    			{
    				// アニメーションは動的に生成
    				// TODO: 端末の傾きによって、アニメーションを変更する
    				animIn = TabAnimationFactory.createTabInAnimation();
//    				animIn = AnimationUtils.loadAnimation(
//    					OkosamaMediaPlayerActivity.getResourceAccessor().getActivity()
//    					, R.anim.left_in );
    			}
        		if( tabBaseLayout.getParent() != null )
        		{
        			if( tabBaseLayout.getParent() instanceof ViewGroup )
        				((ViewGroup)tabBaseLayout.getParent()).removeView( tabBaseLayout );
        		}
        		if( 0 > componentContainer.indexOfChild( tabBaseLayout ))
            	{
        			componentContainer.addView( tabBaseLayout );
        			componentContainer.invalidate();
//        			tabPageCnt[message.arg2]++;
//        			Log.d("anim","add:" + message.arg2 + " cnt:" + tabPageCnt[message.arg2]);       		
            	}
    			Log.d("anim","in start:" + message.arg2 );        		
    			tabBaseLayout.startAnimation(animIn);
    			
        		break;
        	case TAB_OUT:
    			//if( animOut == null )
    			{
    				// TODO: アニメーションは動的に生成
    				animOut = TabAnimationFactory.createTabOutAnimation();
//    				animOut = AnimationUtils.loadAnimation(
//    						OkosamaMediaPlayerActivity.getResourceAccessor().getActivity()
//    						, R.anim.right_out );
        			outAnimDelay = animOut.getDuration();
    			}
    			animOut.setAnimationListener(
    				new TabOutAnimationListener(tabBaseLayout,
    						componentContainer,
    						message.arg2)
    			);
				// tabBaseLayout.setAnimation(anim);
				//animOut.setFillAfter(true);
    			
    			animEndWait = true;
				//tabBaseLayout.startAnimation(animOut);
    			// componentContainer.removeView( tabBaseLayout );
    			componentContainer.removeAllViews();
    			componentContainer.invalidate();
//    			tabPageCnt[message.arg2]--;
//    			Log.d("anim","out start:" + message.arg2 + " cnt:" + tabPageCnt[message.arg2] );
        		break;
    		}
    	}
    };
	
    boolean animEndWait = false;
    class TabOutAnimationListener implements AnimationListener {
    	ViewGroup tabBaseLayout 		= null;
    	ViewGroup componentContainer 	= null;
    	int tabId = 0;
    	
    	TabOutAnimationListener(ViewGroup v, ViewGroup p, int i)
    	{
    		tabBaseLayout = v;
    		componentContainer = p;
    		tabId = i;
    	}
    	
	    @Override
	    public void onAnimationEnd(Animation animation) {
			//Log.i("anim_end","ok");
			componentContainer.post(new Runnable() {
	            @Override
				public void run() {
	            	if( 0 <= componentContainer.indexOfChild( tabBaseLayout ))
	            	{
                		componentContainer.removeView( tabBaseLayout );
                		Log.d("anim","remove:" + tabId);
    		    	}
            		Log.d("anim","out end:" + tabId);
            		animEndWait = false;
            		outAnimDelay = 0;
	            }
	        });				
	    }

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			//Log.i("anim_start","ok");
		}  
    	
    }
    
}
