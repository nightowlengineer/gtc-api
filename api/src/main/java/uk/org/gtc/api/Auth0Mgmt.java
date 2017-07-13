package uk.org.gtc.api;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;

public class Auth0Mgmt
{
    private static String accessToken;
    private static Date accessTokenExpiresAt;
    private static GtcConfiguration configuration;
    
    private static Auth0Mgmt instance;
    
    public static ManagementAPI mgmt;
    
    private Auth0Mgmt()
    {
        configuration = GtcConfiguration.getInstance();
        Auth0Mgmt.getAccessToken();
        mgmt = new ManagementAPI(configuration.auth0Domain, getAccessToken());
    }
    
    public static Auth0Mgmt getInstance()
    {
        if (instance == null)
        {
            logger().info("Creating new Auth0Mgmt instance");
            instance = new Auth0Mgmt();
        }
        return instance;
    }
    
    /**
     * Gets the current access token, or requests a new token if the current one
     * has expired.
     * Note that management API tokens cannot be refreshed/renewed: <a href=
     * "https://auth0.com/docs/api/management/v2/tokens#frequently-asked-questions">'Can
     * I refresh my token?'</a>
     *
     * @return a valid management token
     * @throws Auth0Exception
     */
    private static String getAccessToken()
    {
        // No access token or the expiry time has been reached
        if (Auth0Mgmt.accessToken == null || Auth0Mgmt.accessTokenExpiresAt == null
                || Calendar.getInstance().getTime().after(Auth0Mgmt.accessTokenExpiresAt))
        {
            logger().info("Getting new management token");
            final String audience = configuration.auth0Domain + "/api/v2/";
            final AuthAPI auth = new AuthAPI(configuration.auth0Domain, configuration.auth0MgmtApiId, configuration.auth0MgmtApiKey);
            
            TokenHolder tokens = null;
            try
            {
                tokens = auth.requestToken(audience).execute();
            }
            catch (final Auth0Exception e)
            {
                logger().error("Auth0 returned an error", e);
            }
            
            // Store the expiry time (usually 86400 seconds/24 hours)
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, Math.toIntExact(tokens.getExpiresIn()));
            Auth0Mgmt.accessTokenExpiresAt = cal.getTime();
            
            Auth0Mgmt.accessToken = tokens.getAccessToken();
            return Auth0Mgmt.accessToken;
        }
        logger().debug("Using existing access token");
        return Auth0Mgmt.accessToken;
    }
    
    static Logger logger()
    {
        return LoggerFactory.getLogger(Auth0Mgmt.class);
    }
}
