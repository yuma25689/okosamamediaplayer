package okosama.app.tab;

import okosama.app.ControlIDs;
import okosama.app.R;
import okosama.app.tab.media.TabMediaSelect;
import okosama.app.tab.play.TabPlayContent;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class TabStocker {
	
	int currentTabID = ControlIDs.TAB_ID_MAIN;
	public void setCurrentTabId(int tabID)
	{
		currentTabID = tabID;
	}
	public int getCurrentTabId()
	{
		return currentTabID;
	}
	
	
	SparseArray<Tab> tabArray = new SparseArray<Tab>();
	SparseArray<Integer> currentTabPageId = new SparseArray<Integer>();
	public SparseArray<Integer> getTabPageIdMap()
	{
		return currentTabPageId;
	}
	public int getCurrentTabPageId(int key)
	{
		return currentTabPageId.get(key);
	}
	public void setCurrentTabPageId(int key,Integer id)
	{
		currentTabPageId.put(key,id);
	}
	public void clearCurrentTabPageId()
	{
		currentTabPageId.clear();
	}
	
	/**
	 * Mainのタブを作成する
	 * @param pageContainer
	 * @param componentContainer
	 * @return
	 */
	public Tab createTabMain(
			LinearLayout pageContainer,RelativeLayout componentContainer)
	{
		Tab tabMain = getTab(ControlIDs.TAB_ID_MAIN);
		if( tabMain == null )
		{
			tabMain = new Tab(
            	ControlIDs.TAB_ID_MAIN
            	,pageContainer
            	,componentContainer 
            );
	        tabMain.create(R.layout.tab_layout_header);
	        putTab(ControlIDs.TAB_ID_MAIN,tabMain);
		}
        return tabMain;
	}
	
	/**
	 * メディアタブを作成する
	 * @param pageContainer
	 * @param componentContainer
	 * @return
	 */
	public TabMediaSelect createMediaTab(
			LinearLayout pageContainer, ViewGroup componentContainer )
	{
		TabMediaSelect tabMedia = (TabMediaSelect) getTab(ControlIDs.TAB_ID_MEDIA);
		if( tabMedia == null )
		{
			tabMedia = new TabMediaSelect( ControlIDs.TAB_ID_MEDIA, pageContainer, componentContainer );
			tabMedia.create(R.layout.tab_layout_hooter);
			putTab(ControlIDs.TAB_ID_MEDIA,tabMedia);
		}
		return tabMedia;
	}
	
	/**
	 * プレイのタブを作成する
	 * @param pageContainer
	 * @param componentContainer
	 * @return
	 */
	public TabPlayContent createPlayTab(
			LinearLayout pageContainer, ViewGroup componentContainer )
	{
		TabPlayContent tabPlayCont = (TabPlayContent) getTab(ControlIDs.TAB_ID_PLAY);
		if( tabPlayCont == null )
		{
			tabPlayCont = new TabPlayContent( 
					ControlIDs.TAB_ID_PLAY, pageContainer, componentContainer );
			tabPlayCont.create(R.layout.tab_layout_hooter);
			putTab(ControlIDs.TAB_ID_PLAY,tabPlayCont);
		}
		return tabPlayCont;
	}
	
	public void putTab(int key, Tab tab)
	{
		tabArray.put(key, tab);
	}
	
	/**
	 * 格納されているタブを取得する
	 * @param key ControlIDのタブIDを使うものとする
	 * @return
	 */
	public Tab getTab(int key)
	{
		if( 0 <= tabArray.indexOfKey(key))
		{
			return tabArray.get(key);
		}
		return null;
	}

	public void clear()
	{
		tabArray.clear();
	}
	
}
