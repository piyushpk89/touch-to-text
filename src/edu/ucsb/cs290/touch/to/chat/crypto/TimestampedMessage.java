 
package edu.ucsb.cs290.touch.to.chat.crypto;

import java.io.Serializable;
import java.util.Date;

import edu.ucsb.cs290.touch.to.chat.remote.messages.SignedMessage;

public class TimestampedMessage implements Serializable {
	final SignedMessage message;
	final Date time;
	public TimestampedMessage(SignedMessage message, long dateTime) {
		this.time = new Date(dateTime);
		this.message = message;
	}
}