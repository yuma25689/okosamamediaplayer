package okosama.app.storage;

import java.io.Serializable;
// import java.util.ArrayList;

public class GenreData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8832540249308517413L;
	long genreId;
	String genreName;
    // ジャンルに対するオーディオの一覧を取得する場合、コメント解除
	// ArrayList<Long> lstAudioIds = new ArrayList<Long>();
	
//	/**
//	 * @return the lstAudioIds
//	 */
//	public ArrayList<Long> getLstAudioIds() {
//		return lstAudioIds;
//	}
//	/**
//	 * @param lstAudioIds the lstAudioIds to set
//	 */
//	public void setLstAudioIds(ArrayList<Long> lstAudioIds) {
//		this.lstAudioIds = lstAudioIds;
//	}
//	public void addAudioId( Long lngId )
//	{
//		lstAudioIds.add(lngId);
//	}
	
	/**
	 * @return the genreId
	 */
	public long getGenreId() {
		return genreId;
	}
	/**
	 * @param genreId the genreId to set
	 */
	public void setGenreId(long genreId) {
		this.genreId = genreId;
	}
	/**
	 * @return the genreName
	 */
	public String getGenreName() {
		return genreName;
	}
	/**
	 * @param genreName the genreName to set
	 */
	public void setGenreName(String genreName) {
		this.genreName = genreName;
	}
	@Override
	public String toString()
	{
		return this.genreName;
	}
	
}
