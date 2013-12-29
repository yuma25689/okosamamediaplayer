package okosama.app.audio;

import java.io.FileInputStream;
import java.io.IOException;

import okosama.app.service.ByteEncoder;

/**
 * @author 25689
 *
 */
public class ID3v1TagData implements IAudioTagData {
	private String filePath;
	private String title;
	private String artist;
	private String album;
	private String year;
	private String comment;
	private byte trackNumber;
	private byte genre;
	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}
	/**
	 * @param filePath the filePath to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the artist
	 */
	public String getArtist() {
		return artist;
	}
	/**
	 * @param artist the artist to set
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}
	/**
	 * @return the album
	 */
	public String getAlbum() {
		return album;
	}
	/**
	 * @param album the album to set
	 */
	public void setAlbum(String album) {
		this.album = album;
	}
	/**
	 * @return the year
	 */
	public String getYear() {
		return year;
	}
	/**
	 * @param year the year to set
	 */
	public void setYear(String year) {
		this.year = year;
	}
	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}
	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	/**
	 * @return the trackNumber
	 */
	public byte getTrackNumber() {
		return trackNumber;
	}
	/**
	 * @param trackNumber the trackNumber to set
	 */
	public void setTrackNumber(byte trackNumber) {
		this.trackNumber = trackNumber;
	}
	/**
	 * @return the genre
	 */
	public byte getGenre() {
		return genre;
	}
	/**
	 * @param genre the genre to set
	 */
	public void setGenre(byte genre) {
		this.genre = genre;
	}
	
	public static final int ID3_V1_TITLE_BYTE_COUNT = 30;
	public static final int ID3_V1_ARTIST_BYTE_COUNT = 30;
	public static final int ID3_V1_ALBUM_BYTE_COUNT = 30;
	public static final int ID3_V1_YEAR_BYTE_COUNT = 4;
	public static final int ID3_V1_COMMENT_BYTE_COUNT = 28;
	public static final int ID3_V1_TRACKNUM_BYTE_COUNT = 1;
	public static final int ID3_V1_GENRE_BYTE_COUNT = 1;
	
	byte bufferID3v1[] = null;
	
	public String readStreamAsString(FileInputStream fis, int len) throws IOException
	{
		String strRet = null;
		if( len == fis.read(bufferID3v1, 0, len ) )
		{
			// 読み込まれた長さがlenと等しい場合のみ、読み込み成功
			
			// byte配列を、Stringに変換する
			// エンコードの取得
			ByteEncoder encoder = new ByteEncoder();
			
			//String strEncName = 
			encoder.encode(bufferID3v1, len, strRet);
			
		}
		return strRet;
	}
	public Byte readStreamAsByte(FileInputStream fis) throws IOException
	{
		bufferID3v1[0] = 0x00;
		if( 1 == fis.read(bufferID3v1, 0, 1 ) )
		{
			// 読み込まれた長さがlenと等しい場合のみ、読み込み成功
		}
		return bufferID3v1[0];
	}
	
	/**
	 * このメソッドの場所はあまり正しくないかもしれない
	 * @param fis
	 */
	@Override
	public boolean Load( FileInputStream fis )
	{
		bufferID3v1 = new byte[AudioTagParser.ID3v1_TAG_LEN];
		
		try 
		{
			artist = readStreamAsString( fis, ID3_V1_ARTIST_BYTE_COUNT );
			album = readStreamAsString( fis, ID3_V1_ALBUM_BYTE_COUNT );
			year = readStreamAsString( fis, ID3_V1_YEAR_BYTE_COUNT );
			comment = readStreamAsString( fis, ID3_V1_COMMENT_BYTE_COUNT );
			trackNumber = readStreamAsByte( fis );
			genre = readStreamAsByte( fis );
		} catch( IOException ex )
		{
			ex.printStackTrace();
			return false;
		} finally {
			bufferID3v1 = null;
		}
		return true;
	}
}
