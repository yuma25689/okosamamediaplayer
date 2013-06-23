package okosama.app.widget;

import android.app.Activity;
import android.view.View;
import okosama.app.tab.*;

/**
 * このアプリケーションで利用するボタンのハンドル
 * Bridgeパターンを適用
 * @author 25689
 *
 */
public class Image extends TabLeaf {
		
	public Image( Activity activity )
	{
		super( activity );
		create();
	}
	
	/**
	 * 実装クラス
	 */
	private ImageImpl impl;

	/**
	 * 実装クラスの設定
	 * @param impl
	 */
	public void setImpl(ImageImpl impl) {
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

	@Override
	public int create() {
		// TODO もっと汎用性のあるやり方にできるはず
		impl = new ImageImpl(activity);
		return 0;
	}

	@Override
	public View getView() {
		return impl;
	}
}
