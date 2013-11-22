package okosama.app.adapter;

public interface IAdapterUpdate {
	public void initialize();
	public int stockMediaDataFromDevice();
	public int getMainItemCount();
	public int updateStatus();
	public boolean isLastErrored();
}
