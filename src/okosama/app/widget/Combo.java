package okosama.app.widget;

import okosama.app.storage.ISimpleData;
import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;

/**
 * このアプリケーションで利用するボタンのハンドル
 * Bridgeパターンを適用
 * @author 25689
 *
 */
public class Combo extends absWidget {
	
	public Combo( Activity activity )
	{
		super( activity );
		create();
	}
	
	/**
	 * 実装クラス
	 */
	private ComboImpl impl;

	/**
	 * 実装クラスの設定
	 * @param impl
	 */
	public void setImpl(ComboImpl impl) {
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
		impl = new ComboImpl(activity);
		return 0;
	}

	@Override
	public View getView() {
		return impl;
	}
	
	public void setAdapter(SpinnerAdapter a)
	{
		impl.setAdapter(a);
	}
	public void clearValue()
	{
		impl.setAdapter(null);
		// TODO:これでうまくいくとは思えないが・・・
		// impl.setSelection(-1);
	}

	public Object getSelectedItem()
	{
		return impl.getSelectedItem();
	}
	public <T extends ISimpleData> void setSelection(long id, T item) {
        ArrayAdapter<T> adapter = (ArrayAdapter<T>) impl.getAdapter();
        int index = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).getDataId() == id) {
                index = i;
                break;
            }
        }
        impl.setSelection(index);
    }	
}
