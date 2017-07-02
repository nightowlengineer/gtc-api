package uk.org.gtc.api;

import java.io.IOException;

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

import j2html.tags.ContainerTag;
import uk.org.gtc.api.domain.EmailTemplate;
import uk.org.gtc.api.domain.ImportDiff;
import uk.org.gtc.api.domain.MemberDO;
import uk.org.gtc.api.service.MemberService;

import static j2html.TagCreator.*;

public class SendGridHelper {
	final SendGrid sendgrid;
	final MemberService memberService;

	final Email defaultNoReplyAddress = new Email("no-reply@gtc.org.uk", "The GTC");

	public SendGridHelper(final SendGrid sendgrid, final MemberService memberService) {
		this.sendgrid = sendgrid;
		this.memberService = memberService;
	}

	public boolean sendImportNotificationEmail(final ImportDiff diff) {
		// Setup template and default content
		final EmailTemplate template = EmailTemplate.OFFICE_MEMBER_UPDATE;
		final Mail mail = new Mail();
		mail.setFrom(defaultNoReplyAddress);
		mail.setTemplateId(template.getSendGridTemplateId());

		final Personalization p = new Personalization();
		p.addTo(new Email(template.getDefaultRecipient()));
		mail.addPersonalization(p);

		final Content emailContent = new Content("text/plain", buildDiffContent(diff));
		mail.addContent(emailContent);

		// Send request to SendGrid
		Request request = new Request();
		request.method = Method.POST;
		request.endpoint = "mail/send";
		try {
			request.body = mail.build();
			Response response = sendgrid.api(request);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private String buildDiffContent(final ImportDiff diff) {
		return body(
				div(h2("New members"),
						each(diff.getCreatedSet(),
								createdMemberNumber -> generateHtmlForMember(createdMemberNumber))),
				div(h2("Updated members"),
						each(diff.getUpdatedSet(),
								updatedMemberNumber -> generateHtmlForMember(updatedMemberNumber))),
				div(h2("Deleted members"), each(diff.getDeletedSet(),
						deletedMemberNumber -> generateHtmlForMember(deletedMemberNumber)))

		).render();
	}

	public ContainerTag generateHtmlForMember(final Long memberNumber) {
		final MemberDO member = memberService.getByMemberNumber(memberNumber);
		return div(strong(memberNumber.toString()), span(member.getEmail()), span(member.getType().toString()),
				span(member.getFirstName()), span(member.getLastName()));
	}

}
