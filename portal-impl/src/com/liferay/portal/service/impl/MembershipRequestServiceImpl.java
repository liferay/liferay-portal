/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.MembershipRequest;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.service.base.MembershipRequestServiceBaseImpl;

/**
 * @author Jorge Ferrer
 */
public class MembershipRequestServiceImpl
	extends MembershipRequestServiceBaseImpl {

	@Override
	public MembershipRequest addMembershipRequest(
			long groupId, String comments, ServiceContext serviceContext)
		throws PortalException {

		return membershipRequestLocalService.addMembershipRequest(
			getUserId(), groupId, comments, serviceContext);
	}

	@Override
	public void deleteMembershipRequests(long groupId, long statusId)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.ASSIGN_MEMBERS);

		membershipRequestLocalService.deleteMembershipRequests(
			groupId, statusId);
	}

	@Override
	public MembershipRequest getMembershipRequest(long membershipRequestId)
		throws PortalException {

		MembershipRequest membershipRequest =
			membershipRequestPersistence.findByPrimaryKey(membershipRequestId);

		GroupPermissionUtil.check(
			getPermissionChecker(), membershipRequest.getGroupId(),
			ActionKeys.ASSIGN_MEMBERS);

		return membershipRequest;
	}

	@Override
	public void updateStatus(
			long membershipRequestId, String reviewComments, long statusId,
			ServiceContext serviceContext)
		throws PortalException {

		MembershipRequest membershipRequest =
			membershipRequestPersistence.findByPrimaryKey(membershipRequestId);

		GroupPermissionUtil.check(
			getPermissionChecker(), membershipRequest.getGroupId(),
			ActionKeys.ASSIGN_MEMBERS);

		membershipRequestLocalService.updateStatus(
			getUserId(), membershipRequestId, reviewComments, statusId, true,
			serviceContext);
	}

}