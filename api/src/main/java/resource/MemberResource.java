package resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;

import domain.MemberDO;
import service.MemberService;

@Path("/member")
@Produces(MediaType.APPLICATION_JSON)
public class MemberResource {
	
	private MemberService memberService;
	
	public MemberResource(MemberService memberService) {
		this.memberService = memberService;
	}
	
	@GET
	@Timed
	public List<MemberDO> getAll() {
		return memberService.getAll();
	}
	
	@GET
    @Timed
    @Path("/id/{id}")
    public MemberDO getMemberById(@PathParam("id") String id) {
    	return memberService.getById(id);
    }
	
    @GET
    @Timed
    @Path("/{memberNumber}")
    public MemberDO getMemberByNumber(@PathParam("memberNumber") Long memberNumber) throws Exception {
    	return memberService.getByMemberNumber(memberNumber);
    }
    
    @POST
    @Timed
    public MemberDO createMember(MemberDO member) throws Exception {
    	if (memberService.findByMemberNumber(member.getMembershipNumber()).isEmpty()) {
    		return memberService.create(member);
    	}
    	else
    		throw new Exception("A member already exists with this membership number");
    }
}