package com.yfwl.yfgp.dao;

import com.yfwl.yfgp.model.Invitation;

public interface InvitationMapper {

	Invitation getInvitation(Invitation invitation);

	Integer updateInvitation(Invitation invitation);

	Integer initInvitation(Invitation invitation);

}
