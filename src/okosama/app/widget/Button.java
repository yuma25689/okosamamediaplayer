package okosama.app.widget;

import android.app.Activity;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import okosama.app.action.IViewAction;

/**
 * このアプリケーションで利用するボタンのハンドル
 * Bridgeパターンを適用
 * @author 25689
 *
 */
public class Button extends absWidget {
	public Button( Activity activity )
	{
		super( activity );
		create();
	}
	
	/**
	 * 実装クラス
	 */
	private ButtonImpl impl;

	/**
	 * 実装クラスの設定
	 * @param impl
	 */
	public void setImpl(ButtonImpl impl) {
		this.impl = impl;
	}
	
	/**
	 * 有効無効制御
	 * @param b
	 */
	@Override
	public void setEnabled( boolean b )
	{
		impl.setEnabled(b);
	}
	/**
	 * 表示制御
	 * @param b
	 */
	@Override
	public void setVisible( boolean b )
	{
		if( b )
		{
			impl.setVisibility(View.VISIBLE);
		}
		else
		{
			impl.setVisibility(View.INVISIBLE);
		}
	}
	@Override
	public int create() {
		// TODO もっと汎用性のあるやり方にできるはず
		impl = new ButtonImpl(activity);
		return 0;
	}
	
//	private int currentX;
//	private int currentY;
//	private int offsetX;
//	private int offsetY;
	int iActionType = IViewAction.ACTION_ID_NONE;
	@Override
	public View getView() {
		return impl;
	}
	
	@Override
	public void configureAction()
	{
		if( actionMap.get( IViewAction.ACTION_ID_ONCLICK, null ) != null )
		{
			impl.setOnClickListener(new View.OnClickListener() {
	            @Override
				public void onClick(View v) {
	                // クリック時の処理
	            	actionMap.get( IViewAction.ACTION_ID_ONCLICK )
	            		.doAction(v);
	            }
	        });
		}
		/*
		if( 
			actionMap.get( IViewAction.ACTION_ID_ONCLICK, null ) != null
		|| actionMap.get( IViewAction.ACTION_ID_ONFLICKUP, null ) != null 
		|| actionMap.get( IViewAction.ACTION_ID_ONFLICKDOWN, null ) != null
		|| actionMap.get( IViewAction.ACTION_ID_ONFLICKLEFT, null ) != null
		|| actionMap.get( IViewAction.ACTION_ID_ONFLICKRIGHT, null ) != null )
		{
			impl.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// _gestureDetector.onTouchEvent(event);

					// boolean bExec = false;
					int x = (int) event.getRawX();
					int y = (int) event.getRawY();

					if (event.getAction() == MotionEvent.ACTION_MOVE) {

						int diffX = firstX - x;
						int diffY = firstY - y;

//						currentX -= diffX;
//						currentY -= diffY;
//						v.layout(currentX, currentY, currentX + v.getWidth(),
//								currentY + v.getHeight());

//						offsetX = x;
//						offsetY = y;
						
						if( ( FLICK_RECOGNIZED_RANGE_LOW <= Math.abs(diffX) 
							&& Math.abs(diffX) <= FLICK_RECOGNIZED_RANGE_HIGH )
							)
						{
							// Xがフリック中
							// ただし、Yにもフリック中ならば、分からないので、入力解除
							if(
								( FLICK_RECOGNIZED_RANGE_LOW <= Math.abs(diffY) 
								&& Math.abs(diffY) <= FLICK_RECOGNIZED_RANGE_HIGH )
								)
							{	
							}
							else
							{
								
								if( diffX < 0 )
								{
									// 左
									iActionType = IViewAction.ACTION_ID_ONFLICKRIGHT;
								}
								else
								{
									// 右
									iActionType = IViewAction.ACTION_ID_ONFLICKRIGHT;
								}
							}
						}
						else if( ( FLICK_RECOGNIZED_RANGE_LOW <= Math.abs(diffY) 
								&& Math.abs(diffY) <= FLICK_RECOGNIZED_RANGE_HIGH )
								)
						{
							// Yがフリック中
							// ただし、Xにもフリック中ならば、分からないので、入力解除だが、ここに来ている時点でそのパターンではない
							if( diffY < 0 )
							{
								// 下
								iActionType = IViewAction.ACTION_ID_ONFLICKDOWN;
							}
							else
							{
								// 上
								iActionType = IViewAction.ACTION_ID_ONFLICKUP;
							}
						}
						else
						{
							iActionType = IViewAction.ACTION_ID_NONE;
						}
					} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
						firstX = x; // offsetX
						firstY = y; // offsetY
						iActionType = IViewAction.ACTION_ID_NONE;
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						if( iActionType == IViewAction.ACTION_ID_NONE )
						{
							Rect outRect = new Rect();
							v.getGlobalVisibleRect(outRect);
							outRect.inset(-1*CLICKABLE_OFFSET, -1*CLICKABLE_OFFSET);
							if( outRect.contains(x,y) )
							{
								iActionType = IViewAction.ACTION_ID_ONCLICK;
							}
						}
		                if( iActionType != IViewAction.ACTION_ID_NONE )
		                {
			            	IViewAction action = actionMap.get( iActionType );
			            	if( action != null )
			            	{
			            		action.doAction(v);
			            		// bExec = true;
							}
		                }
					}
					// 必ずandroidに処理をつなぐ
					return false;
//					}
//					return false;
				}
			});
		}
		*/
	}
}
