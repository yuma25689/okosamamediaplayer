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
	 * @param key TabPageIDを利用する
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
	 * @param key tabPageIDを利用する
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
	 * リストのアダプタの更新を行う
	 * @param key
	 * @param bUpdateWhenEmptyOnly
	 * @return 項目の更新が行われたかどうか
	 */
	public boolean stockMediaDataFromDevice(int key,boolean bUpdateWhenEmptyOnly)
	{
		if( bUpdateWhenEmptyOnly )
		{
			if( get(key) != null )
			{
				if( 0 < get(key).getMainItemCount() )
				{
					// 項目がある場合、更新しない
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
