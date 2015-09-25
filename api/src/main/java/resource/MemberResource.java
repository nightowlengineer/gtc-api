package resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;

import domain.MemberDO;
import domain.MemberStatus;
import domain.MemberType;
import domain.Salutation;

@Path("/member")
@Produces(MediaType.APPLICATION_JSON)
public class MemberResource {
    @GET
    @Timed
    @Path("/{memberNumber}")
    public MemberDO getMember(@PathParam("memberNumber") long memberNumber) {
        return new MemberDO(memberNumber, MemberType.FULL, MemberStatus.CURRENT, Salutation.MR, "James", "Test", "james@milligan.tv");
    }
}