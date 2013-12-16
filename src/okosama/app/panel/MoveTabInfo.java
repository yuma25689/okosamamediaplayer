package okosama.app.panel;

public class MoveTabInfo {
	/**
	 * @return the tabId
	 */
	public Integer getTabId() {
		return tabId;
	}
	/**
	 * @param tabId the tabId to set
	 */
	public void setTabId(int tabId) {
		this.tabId = tabId;
	}
	// タブ情報のインデックス
	public static final int LEFT_1 = 1;
	public static final int RIGHT_1 = 2;
	
	Integer tabInfoIndex = null;
	
	
	/**
	 * @return the tabInfoIndex
	 */
	public Integer getTabInfoIndex() {
		return tabInfoIndex;
	}
	/**
	 * @param tabInfoIndex the tabInfoIndex to set
	 */
	public void setTabInfoIndex(Integer tabInfoIndex) {
		this.tabInfoIndex = tabInfoIndex;
	}
	//Button btnTabBtn;
	Integer tabId = null;
	Integer tabPageId = null;
	/**
	 * @return the tabPageId
	 */
	public Integer getTabPageId() {
		return tabPageId;
	}
	/**
	 * @param tabPageId the tabPageId to set
	 */
	public void setTabPageId(Integer tabPageId) {
		this.tabPageId = tabPageId;
	}
	Integer panelId = null;
	/**
	 * @return the panelId
	 */
	public Integer getPanelId() {
		return panelId;
	}
	/**
	 * @param panelId the panelId to set
	 */
	public void setPanelId(Integer panelId) {
		this.panelId = panelId;
	}
	/**
	 * @return the imageViewId
	 */
	public Integer getImageViewId() {
		return imageViewId;
	}
	/**
	 * @param imageViewId the imageViewId to set
	 */
	public void setImageViewId(Integer imageViewId) {
		this.imageViewId = imageViewId;
	}
	Integer imageViewId = null;
	Integer tabImageResId = null;
	boolean showing = false;
	/**
	 * @return the showing
	 */
	public boolean isShowing() {
		return showing;
	}
	/**
	 * @param showing the showing to set
	 */
	public void setShowing(boolean showing) {
		this.showing = showing;
	}
	/**
	 * @return the tabImageResId
	 */
	public Integer getTabImageResId() {
		return tabImageResId;
	}
	/**
	 * @param tabImageResId the tabImageResId to set
	 */
	public void setTabImageResId(int tabImageResId) {
		this.tabImageResId = tabImageResId;
	}

}
