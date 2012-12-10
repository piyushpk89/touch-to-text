package edu.ucsb.cs290.touch.to.text.crypto;

import java.lang.reflect.Field;
import java.security.InvalidKeyException;
import java.security.KeyStore.PasswordProtection;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.DestroyFailedException;

/** Since we never want to store the user's password in plaintext,
 * 	even in memory, we use a password keyed HMAC of the
 *	user's password as the key to the database.
 *
 *	Most methods are package level visible for the other crypto classes.	
 **/
public final class MasterPassword {

	private static MasterPassword instance;
	private static PasswordProtection passphrase; 

	/*
	 * Package level access to decrypt the database. {
	 */

	public static MasterPassword getInstance(String password) {
		if(instance == null || passphrase == null || passphrase.isDestroyed()) {
			instance = new MasterPassword(password);
		}
		return instance;

	}

	char[] getPassword() {
		return passphrase.getPassword();
	}
	
	String getPasswordString() {
		return String.valueOf(passphrase.getPassword());
	}
	
	PasswordProtection getPasswordProtection() {
		return passphrase;
	}

	public MasterPassword(String userPass)  {
		byte[] encodedPass = null;
		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			SecretKeySpec secret = new SecretKeySpec(userPass.getBytes(), mac.getAlgorithm());
			mac.init(secret);
			encodedPass = mac.doFinal(userPass.getBytes());
		} catch (NoSuchAlgorithmException e){

		} catch(InvalidKeyException e) {

		}
		char[] buffer = new char[encodedPass.length >> 1];
		// Fastest byte[] to char[] conversion I know
		for(int i = 0; i < buffer.length; i++) {
			int bpos = i << 1;
			char c = (char)(((encodedPass[bpos]&0x00FF)<<8) + (encodedPass[bpos+1]&0x00FF));
			buffer[i] = c;
		}
		passphrase = new PasswordProtection(buffer);
		for(int i=0;i<buffer.length;i++) {
			buffer[i]=0;
		}
		for(int i=0;i<encodedPass.length;i++) {
			encodedPass[i]=0;
		}

		// Should wipe out all remaining copies of userPass 
		// and zero out any Editables or UI elements that produced it.
		scrub(userPass);
		userPass = null;
		System.gc();
	}

	public void forgetPassword() {
		try {
			passphrase.destroy();
		} catch (DestroyFailedException e) {
			Logger.getLogger("touch-to-text").log(Level.SEVERE,
					"Unable to destroy password in memory!", e);

		}
	}
	
	public static void scrub(String password) {
		Field internal;
		try {
			internal = String.class.getField("value");
			char[] internalA = (char[]) internal.get(password);
			Arrays.fill(internalA, 'X');
		} catch (NoSuchFieldException e) {
			//Never happens
		} catch (IllegalArgumentException e) {
			//Never happens
		} catch (IllegalAccessException e) {
			//never happens
		}
	}

}
