package okosama.app.adapter;

import android.util.SparseArray;

public class AdapterStocker {
	SparseArray<IAdapterUpdate> array = new SparseArray<IAdapterUpdate>();
	
	public void initAllAdapter()
	{
		for( int i=0; i < array.size(); i++ )
		{
			IAdapterUpdate adp = array.get(array.keyAt(i));
			adp.initialize();
		}
	}
	
	/**
	 * 
	 * @param key TabPageID�𗘗p����
	 * @return
	 */
	public IAdapterUpdate get(int key)
	{
		if( 0 > array.indexOfKey(key))
		{
			return null;
		}
		return array.get(key);
	}
	/**
	 * 
	 * @param key tabPageID�𗘗p����
	 * @param adp
	 */
	public void put(int key,IAdapterUpdate adp)
	{
		if( 0 <= array.indexOfKey(key) )
		{
			return;
		}
		array.put(key,adp);
	}
	
	public void stockMediaDataFromDevice(int key)
	{
		if( get(key) != null )
		{
			get(key).stockMediaDataFromDevice();
		}
	}
	/**
	 * ���X�g�̃A�_�v�^�̍X�V���s��
	 * @param key
	 * @param bUpdateWhenEmptyOnly
	 * @return ���ڂ̍X�V���s��ꂽ���ǂ���
	 */
	public boolean stockMediaDataFromDevice(int key,boolean bUpdateWhenEmptyOnly)
	{
		if( bUpdateWhenEmptyOnly )
		{
			if( get(key) != null )
			{
				if( 0 < get(key).getMainItemCount() )
				{
					// ���ڂ�����ꍇ�A�X�V���Ȃ�
					get(key).updateStatus();
					return false;
				}				
			}
		}
		stockMediaDataFromDevice(key);
		return true;
	}
	
	public void clear()
	{
        array.clear();
	}
}
