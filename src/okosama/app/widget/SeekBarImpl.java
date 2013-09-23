package okosama.app.widget;

import android.content.Context;
import android.widget.SeekBar;

/**
 * このアプリケーションで利用するシークバーの実装
 * Bridgeパターンを適用
 * @author 25689
 *
 */
public class SeekBarImpl extends SeekBar {

	public SeekBarImpl(Context context) {
		super(context,null,android.R.attr.progressBarStyleHorizontal);
		// このアプリケーション特有の設定
		setPadding(0,0,0,0);
	}
}
