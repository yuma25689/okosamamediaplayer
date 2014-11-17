package okosama.app.widget;

import okosama.app.storage.ISimpleData;
import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

/**
 * ���̃A�v���P�[�V�����ŗ��p����{�^���̃n���h��
 * Bridge�p�^�[����K�p
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
	 * �����N���X
	 */
	private ComboImpl impl;

	/**
	 * �����N���X�̐ݒ�
	 * @param impl
	 */
	public void setImpl(ComboImpl impl) {
		this.impl = impl;
	}
	
	/**
	 * �L����������
	 * @param b
	 */
	@Override
	public void setEnabled( boolean b )
	{
		impl.setEnabled(b);
	}
	/**
	 * �\������
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
		// TODO �����Ɣėp���̂�������ɂł���͂�
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
		//impl.setAdapter(null);
		if( impl.getAdapter() != null && 0 < impl.getAdapter().getCount())
		{
			impl.setSelection(0);
		}
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
