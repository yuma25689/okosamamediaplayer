package okosama.app.service;

public class MediaInfo {
	public MediaInfo( long id, int type )
	{
		this.id = id;
		this.mediaType = type;
	}
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return the mediaType
	 */
	public int getMediaType() {
		return mediaType;
	}
	/**
	 * @param mediaType the mediaType to set
	 */
	public void setMediaType(int mediaType) {
		this.mediaType = mediaType;
	}
	public static final int MEDIA_TYPE_AUDIO = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	
	long id;
	int mediaType;
	
	
	public void copy( MediaInfo src )
	{
		id = src.getId();
		mediaType = src.getMediaType();
	}
	
}
