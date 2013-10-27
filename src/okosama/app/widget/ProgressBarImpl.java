package okosama.app.widget;

import android.content.Context;
import android.widget.ProgressBar;

/**
 * このアプリケーションで利用するプログレスバーの実装
 * Bridgeパターンを適用
 * @author 25689
 *
 */
public class ProgressBarImpl extends ProgressBar {

	public ProgressBarImpl(Context context) {
		super(context,null,android.R.attr.progressBarStyleSmallInverse);
		// このアプリケーション特有の設定
		setPadding(5,5,5,5);
	}
}
