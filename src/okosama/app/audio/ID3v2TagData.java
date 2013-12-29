//package okosama.app.audio;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//
//import okosama.app.service.ByteEncoder;
//
///**
// * @author 25689
// *
// */
//public class ID3v2TagData implements IAudioTagData {
////	private String filePath;
////	private String title;
////	private String artist;
////	private String album;
////	private String year;
////	private String comment;
////	private byte trackNumber;
////	private byte genre;
//	
//    private String _FilePath; // ID3 file path
////    private FilterCollection _Filter; // Contain Filter Frames
////    private FilterTypes _FilterType; //Indicate wich filter type use
////    private ID3v2Flags _Flags;
//    private bool _LoadLinkedFrames; // Indicate load Link frames when loading ID3 or not
//    private bool _DropUnknown; // if true. unknown frames will not save
//    private Version _ver; // Contain ID3 version information
//    private bool _HaveTag; // Indicate if current file have ID3v2 Info
//    private ErrorCollection _Errors; // Contain Errors that occured
//    private static TextEncodings _DefaultUnicodeEncoding = TextEncodings.UTF_16; // when use AutoTextEncoding which unicode type must use
//    private static bool _AutoTextEncoding = true; // when want to save frame use automatic text encoding
//
//    // Frames that can be more than one
//    private FramesCollection<TextFrame> _TextFrames;
//    private FramesCollection<UserTextFrame> _UserTextFrames;
//    private FramesCollection<PrivateFrame> _PrivateFrames;
//    private FramesCollection<TextWithLanguageFrame> _TextWithLangFrames;
//    private FramesCollection<SynchronisedText> _SynchronisedTextFrames;
//    private FramesCollection<AttachedPictureFrame> _AttachedPictureFrames;
//    private FramesCollection<GeneralFileFrame> _EncapsulatedObjectFrames;
//    private FramesCollection<PopularimeterFrame> _PopularimeterFrames;
//    private FramesCollection<AudioEncryptionFrame> _AudioEncryptionFrames;
//    private FramesCollection<LinkFrame> _LinkFrames;
//    private FramesCollection<TermOfUseFrame> _TermOfUseFrames;
//    private FramesCollection<DataWithSymbolFrame> _DataWithSymbolFrames;
//    private FramesCollection<BinaryFrame> _UnknownFrames;
//
//    // Frames that can't repeat
//    private BinaryFrame _MCDIFrame;
//    private SynchronisedTempoFrame _SYTCFrame; // Synchronised tempo codes        
//    private PlayCounterFrame _PCNTFrame; // Play Counter        
//    private RecomendedBufferSizeFrame _RBUFFrame;
//    private OwnershipFrame _OWNEFrame; // Owner ship
//    private CommercialFrame _COMRFrame;
//    private ReverbFrame _RVRBFrame;
//    private Equalisation _EQUAFrame;
//    private RelativeVolumeFrame _RVADFrame;
//    private EventTimingCodeFrame _ETCOFrame;
//    private PositionSynchronisedFrame _POSSFrame;
//	
//	/**
//	 * @return the filePath
//	 */
//	public String getFilePath() {
//		return filePath;
//	}
//	/**
//	 * @param filePath the filePath to set
//	 */
//	public void setFilePath(String filePath) {
//		this.filePath = filePath;
//	}
//	/**
//	 * @return the title
//	 */
//	public String getTitle() {
//		return title;
//	}
//	/**
//	 * @param title the title to set
//	 */
//	public void setTitle(String title) {
//		this.title = title;
//	}
//	/**
//	 * @return the artist
//	 */
//	public String getArtist() {
//		return artist;
//	}
//	/**
//	 * @param artist the artist to set
//	 */
//	public void setArtist(String artist) {
//		this.artist = artist;
//	}
//	/**
//	 * @return the album
//	 */
//	public String getAlbum() {
//		return album;
//	}
//	/**
//	 * @param album the album to set
//	 */
//	public void setAlbum(String album) {
//		this.album = album;
//	}
//	/**
//	 * @return the year
//	 */
//	public String getYear() {
//		return year;
//	}
//	/**
//	 * @param year the year to set
//	 */
//	public void setYear(String year) {
//		this.year = year;
//	}
//	/**
//	 * @return the comment
//	 */
//	public String getComment() {
//		return comment;
//	}
//	/**
//	 * @param comment the comment to set
//	 */
//	public void setComment(String comment) {
//		this.comment = comment;
//	}
//	/**
//	 * @return the trackNumber
//	 */
//	public byte getTrackNumber() {
//		return trackNumber;
//	}
//	/**
//	 * @param trackNumber the trackNumber to set
//	 */
//	public void setTrackNumber(byte trackNumber) {
//		this.trackNumber = trackNumber;
//	}
//	/**
//	 * @return the genre
//	 */
//	public byte getGenre() {
//		return genre;
//	}
//	/**
//	 * @param genre the genre to set
//	 */
//	public void setGenre(byte genre) {
//		this.genre = genre;
//	}
//	
//	public static final int ID3_V1_TITLE_BYTE_COUNT = 30;
//	public static final int ID3_V1_ARTIST_BYTE_COUNT = 30;
//	public static final int ID3_V1_ALBUM_BYTE_COUNT = 30;
//	public static final int ID3_V1_YEAR_BYTE_COUNT = 4;
//	public static final int ID3_V1_COMMENT_BYTE_COUNT = 28;
//	public static final int ID3_V1_TRACKNUM_BYTE_COUNT = 1;
//	public static final int ID3_V1_GENRE_BYTE_COUNT = 1;
//	
//	byte bufferID3v1[] = null;
//	
//	public String readStreamAsString(FileInputStream fis, int len) throws IOException
//	{
//		String strRet = null;
//		if( len == fis.read(bufferID3v1, 0, len ) )
//		{
//			// 読み込まれた長さがlenと等しい場合のみ、読み込み成功
//			
//			// byte配列を、Stringに変換する
//			// エンコードの取得
//			ByteEncoder encoder = new ByteEncoder();
//			
//			//String strEncName = 
//			encoder.encode(bufferID3v1, len, strRet);
//			
//		}
//		return strRet;
//	}
//	public Byte readStreamAsByte(FileInputStream fis) throws IOException
//	{
//		bufferID3v1[0] = 0x00;
//		if( 1 == fis.read(bufferID3v1, 0, 1 ) )
//		{
//			// 読み込まれた長さがlenと等しい場合のみ、読み込み成功
//		}
//		return bufferID3v1[0];
//	}
//	
//	/**
//	 * このメソッドの場所はあまり正しくないかもしれない
//	 * @param fis
//	 */
//	@Override
//	public boolean Load( FileInputStream fis )
//	{
//		bufferID3v1 = new byte[AudioTagParser.ID3v1_TAG_LEN];
//		
//		try 
//		{
//			artist = readStreamAsString( fis, ID3_V1_ARTIST_BYTE_COUNT );
//			album = readStreamAsString( fis, ID3_V1_ALBUM_BYTE_COUNT );
//			year = readStreamAsString( fis, ID3_V1_YEAR_BYTE_COUNT );
//			comment = readStreamAsString( fis, ID3_V1_COMMENT_BYTE_COUNT );
//			trackNumber = readStreamAsByte( fis );
//			genre = readStreamAsByte( fis );
//		} catch( IOException ex )
//		{
//			ex.printStackTrace();
//			return false;
//		} finally {
//			bufferID3v1 = null;
//		}
//		return true;
//	}
//}
