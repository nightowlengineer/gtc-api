package uk.org.gtc.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import uk.org.gtc.api.domain.AuthDO;
import uk.org.gtc.api.resource.AuthResource;
import uk.org.gtc.api.service.AuthService;

public class GtcAuthenticator implements Authenticator<BasicCredentials, AuthDO>
{
	Logger logger;
	
	private final static int ITERATION_NUMBER = 2000;
	
	final AuthService authService;
	
	public GtcAuthenticator(AuthService authService, Logger logger)
	{
		this.authService = authService;
		this.logger = logger;
	}
	
	// https://www.owasp.org/index.php/Hashing_Java
	
	@Override
	public Optional<AuthDO> authenticate(BasicCredentials credentials) throws AuthenticationException
	{
		String username = credentials.getUsername();
		String password = credentials.getPassword();
		if (UtilityHelper.isEmptyOrNull(username) || UtilityHelper.isEmptyOrNull(password))
		{
			username = "";
			password = "";
		}
		else
		{
			username = username.trim().toLowerCase();
		}
		
		MessageDigest md = null;
		
		try
		{
			md = MessageDigest.getInstance("SHA-512");
		}
		catch (NoSuchAlgorithmException e)
		{
			logger.error("Code 276");
			throw new AuthenticationException("An error occurred during authentication. API error 276");
		}
		
		md.reset();
		
		final AuthDO fetchedUser = authService.getByUsername(username);
		
		String digest, salt;
		if (fetchedUser != null)
		{
			digest = fetchedUser.getPassword();
			salt = fetchedUser.getSalt();
			if (UtilityHelper.isEmptyOrNull(salt) || UtilityHelper.isEmptyOrNull(digest))
			{
				throw new AuthenticationException("An error occurred during authentication. API error 277");
			}
		}
		else
		{
			digest = "000000000000000000000000000=";
			salt = "00000000000=";
		}
		
		byte[] bSalt, bDigest;
		
		try
		{
			bDigest = base64ToByte(digest);
			bSalt = base64ToByte(salt);
		}
		catch (IOException e)
		{
			throw new AuthenticationException("An error occurred during authentication. API error 278");
		}
		
		// Compute the new DIGEST
		byte[] proposedDigest;
		try
		{
			proposedDigest = getHash(ITERATION_NUMBER, password, bSalt);
		}
		catch (NoSuchAlgorithmException | UnsupportedEncodingException e)
		{
			throw new AuthenticationException("An error occurred during authentication. API error 279");
		}
		
		if (Arrays.equals(proposedDigest, bDigest))
		{
			return Optional.of(fetchedUser);
		}
		else
		{
			return Optional.absent();
		}
	}
	
	/**
	 * Inserts a new user in the database
	 * 
	 * @param user
	 *            The user object to create
	 * @return boolean Returns true if the login and password are ok (not null
	 *         and length(login)<=100
	 * @throws NoSuchAlgorithmException
	 *             If the algorithm SHA-1 or the SecureRandom is not supported
	 *             by the JVM
	 */
	public Boolean createUser(AuthDO user) throws NoSuchAlgorithmException, AuthenticationException
	{
		final String username = user.getUsername();
		final String password = user.getPassword();
		
		if (UtilityHelper.isEmptyOrNull(username) || username.length() > 20)
		{
			return false;
		}
		
		if (password != null)
		{
			// Uses a secure Random not a simple Random
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			// Salt generation 64 bits long
			byte[] bSalt = new byte[8];
			random.nextBytes(bSalt);
			// Digest computation
			byte[] bDigest;
			try
			{
				bDigest = getHash(ITERATION_NUMBER, password, bSalt);
			}
			catch (UnsupportedEncodingException e)
			{
				throw new AuthenticationException("An error occurred during authentication. API error 280");
			}
			String sDigest = byteToBase64(bDigest);
			String sSalt = byteToBase64(bSalt);
			
			user.setPassword(sDigest);
			user.setSalt(sSalt);
			
			authService.create(user);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * From a password, a number of iterations and a salt, returns the
	 * corresponding digest
	 * 
	 * @param iterationNb
	 *            int The number of iterations of the algorithm
	 * @param password
	 *            String The password to encrypt
	 * @param salt
	 *            byte[] The salt
	 * @return byte[] The digested password
	 * @throws NoSuchAlgorithmException
	 *             If the algorithm doesn't exist
	 * @throws UnsupportedEncodingException
	 */
	public byte[] getHash(int iterationNb, String password, byte[] salt) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		digest.reset();
		digest.update(salt);
		byte[] input = digest.digest(password.getBytes("UTF-8"));
		for (int i = 0; i < iterationNb; i++)
		{
			digest.reset();
			input = digest.digest(input);
		}
		return input;
	}
	
	/**
	 * From a base 64 representation, returns the corresponding byte[]
	 * 
	 * @param data
	 *            String The base64 representation
	 * @return byte[]
	 * @throws IOException
	 */
	public static byte[] base64ToByte(String data) throws IOException
	{
		BASE64Decoder decoder = new BASE64Decoder();
		return decoder.decodeBuffer(data);
	}
	
	/**
	 * From a byte[] returns a base 64 representation
	 * 
	 * @param data
	 *            byte[]
	 * @return String
	 * @throws IOException
	 */
	public static String byteToBase64(byte[] data)
	{
		BASE64Encoder endecoder = new BASE64Encoder();
		return endecoder.encode(data);
	}
	
	Logger logger()
	{
		return LoggerFactory.getLogger(AuthResource.class);
	}
	
}
