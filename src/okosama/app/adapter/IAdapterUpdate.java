package okosama.app.adapter;

import okosama.app.storage.FilterData;
import okosama.app.storage.ISimpleData;
import okosama.app.tab.TabPage;

public interface IAdapterUpdate<T extends ISimpleData> {
	public void initialize();
	public int stockMediaDataFromDevice(TabPage page);
	public void clearAdapterData();
	public int getMainItemCount();
	public int updateStatus();
	public boolean isLastErrored();
	public void setFilterData(FilterData data);
	public FilterData getFilterData();
	public boolean isFilterData(T data);
	public void clearFilterData();
	
}
