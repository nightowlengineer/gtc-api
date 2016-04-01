package uk.org.gtc.api.health;

import com.codahale.metrics.health.HealthCheck;
import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.view.MandrillUserInfo;

public class MandrillHealthCheck extends HealthCheck
{
	private final MandrillApi mandrill;
	
	public MandrillHealthCheck(final MandrillApi mandrill)
	{
		this.mandrill = mandrill;
	}
	
	@Override
	protected Result check() throws Exception
	{
		final MandrillUserInfo userInfo = mandrill.users().info();
		userInfo.getUsername();
		return Result.healthy();
	}
	
}
