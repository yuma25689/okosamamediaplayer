package okosama.app.widget;

import okosama.app.action.IViewAction;
import android.app.Activity;
import android.view.View;

/**
 * ���̃A�v���P�[�V�����ŗ��p����{�^���̃n���h��
 * Bridge�p�^�[����K�p
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
	 * �����N���X
	 */
	private ButtonImpl impl;

	/**
	 * �����N���X�̐ݒ�
	 * @param impl
	 */
	public void setImpl(ButtonImpl impl) {
		this.impl = impl;
	}
	
	/**
	 * �L����������
	 * @param b
	 */
	@Override
	public void setEnabled( boolean b )
	{
		impl.setEnabled(b);
	}
	/**
	 * �\������
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
		// TODO �����Ɣėp���̂�������ɂł���͂�
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
	                // �N���b�N���̏���
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
							// X���t���b�N��
							// �������AY�ɂ��t���b�N���Ȃ�΁A������Ȃ��̂ŁA���͉���
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
									// ��
									iActionType = IViewAction.ACTION_ID_ONFLICKRIGHT;
								}
								else
								{
									// �E
									iActionType = IViewAction.ACTION_ID_ONFLICKRIGHT;
								}
							}
						}
						else if( ( FLICK_RECOGNIZED_RANGE_LOW <= Math.abs(diffY) 
								&& Math.abs(diffY) <= FLICK_RECOGNIZED_RANGE_HIGH )
								)
						{
							// Y���t���b�N��
							// �������AX�ɂ��t���b�N���Ȃ�΁A������Ȃ��̂ŁA���͉��������A�����ɗ��Ă��鎞�_�ł��̃p�^�[���ł͂Ȃ�
							if( diffY < 0 )
							{
								// ��
								iActionType = IViewAction.ACTION_ID_ONFLICKDOWN;
							}
							else
							{
								// ��
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
					// �K��android�ɏ������Ȃ�
					return false;
//					}
//					return false;
				}
			});
		}
		*/
	}
}
