package uk.org.gtc.api.health;

import com.codahale.metrics.health.HealthCheck;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

public class SendGridHealthCheck extends HealthCheck
{
	private final SendGrid sg;
	
	public SendGridHealthCheck(final SendGrid sg)
	{
		this.sg = sg;
	}
	
	@Override
	protected Result check() throws Exception
	{
		final Request request = new Request();
		request.method = Method.GET;
		request.endpoint = "user/username";
		final Response response = sg.api(request);
		if (response.statusCode == 200)
		{
			return Result.healthy();
		}
		else
		{
			return Result.unhealthy(response.body);
		}
	}
	
}
