package okosama.app.widget;

import android.app.Activity;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

/**
 * このアプリケーションで利用するボタンのハンドル
 * Bridgeパターンを適用
 * @author 25689
 *
 */
public class AutoCompleteEdit extends absWidget {
	
	public AutoCompleteEdit( Activity activity )
	{
		super( activity );
		create();
	}
	
	/**
	 * 実装クラス
	 */
	private AutoCompleteEditImpl impl;

	/**
	 * 実装クラスの設定
	 * @param impl
	 */
	public void setImpl(AutoCompleteEditImpl impl) {
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
		impl = new AutoCompleteEditImpl(activity);
		return 0;
	}

	@Override
	public View getView() {
		return impl;
	}
	public void setAdapter(ArrayAdapter<?> a)
	{
		impl.setAdapter(a);
	}
	public void clearValue()
	{
		impl.setText(null);
	}
	public String getText()
	{
		String ret = null;
		if( null != impl.getText() )
		{
			ret = ((SpannableStringBuilder)impl.getText()).toString();
		}
		return ret;
	}
	
}
