package okosama.app.media;

public class ID3Error {
	
	String message;
	String frameID;
	
    ID3Error(String Message, String FrameID)
    {
        frameID = FrameID;
        message = Message;
    }

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}
