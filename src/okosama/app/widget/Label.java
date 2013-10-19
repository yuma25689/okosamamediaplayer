package okosama.app.widget;

import android.app.Activity;
import android.view.View;

/**
 * このアプリケーションで利用するボタンのハンドル
 * Bridgeパターンを適用
 * @author 25689
 *
 */
public class Label extends absWidget {
	
	public Label( Activity activity )
	{
		super( activity );
		create();
	}
	
	/**
	 * 実装クラス
	 */
	private LabelImpl impl;

	/**
	 * 実装クラスの設定
	 * @param impl
	 */
	public void setImpl(LabelImpl impl) {
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
		impl = new LabelImpl(activity);
		return 0;
	}

	@Override
	public View getView() {
		return impl;
	}
}
