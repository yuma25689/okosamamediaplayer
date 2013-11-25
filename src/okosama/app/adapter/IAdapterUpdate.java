package okosama.app.adapter;

import okosama.app.tab.TabPage;

public interface IAdapterUpdate {
	public void initialize();
	public int stockMediaDataFromDevice(TabPage page);
	public int getMainItemCount();
	public int updateStatus();
	public boolean isLastErrored();
}
