package okosama.app.widget;

import android.app.Activity;
import android.view.View;
import okosama.app.action.IViewAction;
import okosama.app.tab.*;

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
	            public void onClick(View v) {
	                // クリック時の処理
	            	actionMap.get( IViewAction.ACTION_ID_ONCLICK )
	            		.doAction(v);
	            }
	        });
		}
	}
}
