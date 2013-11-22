package okosama.app.state;

import android.util.SparseArray;

public class StateStocker {
	
	SparseArray<IDisplayState> stateArray = new SparseArray<IDisplayState>();
	public IDisplayState getState(int key)
	{
		return stateArray.get(key);
	}
	public void putState(int key,IDisplayState state)
	{
		stateArray.put(key,state);
	}
	public void unResisterReceiverAll()
	{
		for( int i=0; i < stateArray.size(); i++ )
		{
			stateArray.valueAt(i).unregisterReceivers(IDisplayState.STATUS_ON_DESTROY);
		}		
	}
	
	public void clear()
	{
		unResisterReceiverAll();
        stateArray.clear();
	}
}
