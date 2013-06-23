package okosama.app.widget;

import android.content.Context;
import android.widget.ImageButton;

/**
 * このアプリケーションで利用するボタンの実装
 * Bridgeパターンを適用
 * @author 25689
 *
 */
public class ButtonImpl extends ImageButton {

	public ButtonImpl(Context context) {
		super(context);
		// このアプリケーション特有の設定
		setPadding(0,0,0,0);
	}

}
