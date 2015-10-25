var members = db.members.find().toArray();

var i = 0;
for (i; i < members.length; i++)
{
	var member = members[i];
	
	if (!member.phoneNumbers)
	{
		member.phoneNumbers = [];
	}
	
	if (member.mobileNumber)
	{
		member.phoneNumbers.push({"phoneType":"MOBILE","number":member.mobileNumber});
	}
	
	if (member.homeNumber && member.homeNumber.length > 1)
	{
		member.phoneNumbers.push({"phoneType":"HOME","number":member.homeNumber});
	}
	
	if (!member.addresses)
	{
		member.addresses = [{"addressType":"HOME","postcode":member.addressPostcode,"lines":[]}];
	}
	
	if (member.addressLine1 && member.addressLine1.length > 1)
	{
		member.addresses[0].lines.push(member.addressLine1);
	}
	
	if (member.addressLine2 && member.addressLine2.length > 1)
	{
		member.addresses[0].lines.push(member.addressLine2);
	}
	
	if (member.addressLine3 && member.addressLine3.length > 1)
	{
		member.addresses[0].lines.push(member.addressLine3);
	}
	
	if (member.addressLine4 && member.addressLine4.length > 1)
	{
		member.addresses[0].lines.push(member.addressLine4);
	}
	
	delete member.mobileNumber;
	delete member.homeNumber;
	delete members[i].addressLine1;
	delete members[i].addressLine2;
	delete members[i].addressLine3;
	delete members[i].addressLine4;
	delete members[i].addressPostcode;
	
	db.members.update({"_id" : members[i]._id}, members[i]);
}