package uk.org.gtc.api;

import static j2html.TagCreator.body;
import static j2html.TagCreator.div;
import static j2html.TagCreator.each;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.strong;
import static j2html.TagCreator.tbody;
import static j2html.TagCreator.td;
import static j2html.TagCreator.tr;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class EmailService
{
    final SendGrid sendgrid;
    final MemberService memberService;
    
    final Email defaultNoReplyAddress = new Email("no-reply@gtc.org.uk", "The GTC");
    
    public EmailService(final SendGrid sendgrid, final MemberService memberService)
    {
        this.sendgrid = sendgrid;
        this.memberService = memberService;
    }
    
    private String buildDiffContent(final ImportDiff diff)
    {
        return body(
                div(h2("New members"),
                        tbody(each(diff.getCreatedSet(),
                                createdMemberNumber -> generateHtmlForMember(createdMemberNumber)))),
                div(h2("Updated members"),
                        tbody(each(diff.getUpdatedSet(),
                                updatedMemberNumber -> generateHtmlForMember(updatedMemberNumber)))),
                div(h2("Deleted members"),
                        tbody(each(diff.getDeletedSet(),
                                deletedMemberNumber -> generateHtmlForMember(deletedMemberNumber)))))
                                        .render();
    }
    
    public ContainerTag generateHtmlForMember(final Long memberNumber)
    {
        final MemberDO member = memberService.getByMemberNumber(memberNumber);
        if (member == null)
        {
            return tr(td(strong(memberNumber.toString())));
        }
        else
        {
            return tr(td(strong(memberNumber.toString())), td(member.getEmail()), td(member.getType().toString()),
                    td(member.getFirstName()), td(member.getLastName()));
        }
    }
    
    public boolean sendImportNotificationEmail(final ImportDiff diff)
    {
        // Setup template and default content
        final EmailTemplate template = EmailTemplate.OFFICE_MEMBER_UPDATE;
        final Mail mail = new Mail();
        mail.setFrom(defaultNoReplyAddress);
        mail.setTemplateId(template.getSendGridTemplateId());
        mail.setSubject(template.getDefaultSubject());
        
        final Personalization p = new Personalization();
        p.addTo(new Email(template.getDefaultRecipient()));
        mail.addPersonalization(p);
        
        final Content emailContent = new Content("text/html", buildDiffContent(diff));
        mail.addContent(emailContent);
        
        // Send request to SendGrid
        final Request request = new Request();
        request.method = Method.POST;
        request.endpoint = "mail/send";
        try
        {
            request.body = mail.build();
            final Response response = sendgrid.api(request);
            if (response.statusCode == 202)
            {
                return true;
            }
            else
            {
                logger().error("API error sending notification email: {}", response.body);
                return false;
            }
        }
        catch (final IOException e)
        {
            logger().error("Comms error sending notification email", e);
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean sendAccountLinkedNotification(final MemberDO member)
    {
        // Setup template and default content
        final EmailTemplate template = EmailTemplate.MEMBER_ACCOUNT_LINKED;
        final Mail mail = new Mail();
        mail.setFrom(defaultNoReplyAddress);
        mail.setTemplateId(template.getSendGridTemplateId());
        
        final Personalization p = new Personalization();
        p.addTo(new Email(member.getEmail()));
        p.addSubstitution("[%firstName%]", member.getFirstName());
        mail.addPersonalization(p);
        
        // Send request to SendGrid
        final Request request = new Request();
        request.method = Method.POST;
        request.endpoint = "mail/send";
        try
        {
            request.body = mail.build();
            final Response response = sendgrid.api(request);
            if (response.statusCode == 202)
            {
                return true;
            }
            else
            {
                logger().error("API error sending account linking email: {}", response.body);
                return false;
            }
        }
        catch (final IOException e)
        {
            logger().error("Comms error sending account linking email", e);
            e.printStackTrace();
            return false;
        }
    }
    
    Logger logger()
    {
        return LoggerFactory.getLogger(EmailService.class);
    }
    
}
