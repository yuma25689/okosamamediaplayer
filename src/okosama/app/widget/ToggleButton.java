package okosama.app.widget;

import android.app.Activity;
import android.view.View;
import android.widget.CompoundButton;
import okosama.app.action.IViewAction;
import okosama.app.tab.*;

/**
 * このアプリケーションで利用するボタンのハンドル
 * Bridgeパターンを適用
 * @author 25689
 *
 */
public class ToggleButton extends TabLeaf {
	
	
	public ToggleButton( Activity activity )
	{
		super( activity );
		create();
	}
	
	/**
	 * 実装クラス
	 */
	private ToggleButtonImpl impl;

	/**
	 * 実装クラスの設定
	 * @param impl
	 */
	public void setImpl(ToggleButtonImpl impl) {
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
		impl = new ToggleButtonImpl(activity);
		return 0;
	}

	@Override
	public View getView() {
		return impl;
	}
	
	@Override
	public void configureAction()
	{
		if( actionMap.containsKey( IViewAction.ACTION_ID_ONTOGGLEON ) 
		&& actionMap.containsKey( IViewAction.ACTION_ID_ONTOGGLEOFF ))
		{
			impl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton v, boolean isChecked) {
	                // クリック時の処理
					if( isChecked )
					{
		            	actionMap.get( IViewAction.ACTION_ID_ONTOGGLEON )
		            		.doAction(v);
					}
					else
					{
		            	actionMap.get( IViewAction.ACTION_ID_ONTOGGLEOFF )
	            		.doAction(v);						
					}
	            }
	        });
		}
	}
}
