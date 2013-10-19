package okosama.app.widget;

import android.app.Activity;
import android.view.View;
import okosama.app.R;
import okosama.app.action.IViewAction;

/**
 * このアプリケーションで利用するプログレスバーのハンドル
 * ->SeekBarと勘違いしていたようなので、永久に利用されないかもしれない
 * Bridgeパターンを適用
 * @author 25689
 *
 */
public class ProgressBar extends absWidget {
	public ProgressBar( Activity activity )
	{
		super( activity );
		create();
	}
	
	/**
	 * 実装クラス
	 */
	private ProgressBarImpl impl;

	/**
	 * 実装クラスの設定
	 * @param impl
	 */
	public void setImpl(ProgressBarImpl impl) {
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
		impl = new ProgressBarImpl(activity);
		impl.setProgressDrawable(activity.getResources().getDrawable(R.drawable.progress_image));
		impl.setIndeterminate(false);
		impl.setClickable(true);
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
			// impl.setOnSeekBarChangeListener();
		}
	}
	
	public void setMax( int max )
	{
		impl.setMax( max );
	}
	public void setProgress( int val )
	{
		impl.setProgress( val );
	}
	public void setVisibility( int i )
	{
		impl.setVisibility( i );
	}
}
