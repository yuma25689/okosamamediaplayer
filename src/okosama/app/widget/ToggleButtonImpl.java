package okosama.app.widget;

import android.content.Context;
import android.widget.ToggleButton;

/**
 * このアプリケーションで利用するボタンの実装
 * Bridgeパターンを適用
 * @author 25689
 *
 */
public class ToggleButtonImpl extends ToggleButton {

	public ToggleButtonImpl(Context context) {
		super(context);
		// このアプリケーション特有の設定
		setPadding(0,0,0,0);
		setTextOn("");
		setTextOff("");
	}

}
