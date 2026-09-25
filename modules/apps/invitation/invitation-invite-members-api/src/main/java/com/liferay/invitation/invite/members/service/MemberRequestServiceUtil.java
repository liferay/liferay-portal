/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.invitation.invite.members.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

/**
 * Provides the remote service utility for MemberRequest. This utility wraps
 * <code>com.liferay.invitation.invite.members.service.impl.MemberRequestServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see MemberRequestService
 * @generated
 */
public class MemberRequestServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.invitation.invite.members.service.impl.MemberRequestServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static void addMemberRequests(
			long groupId, long[] receiverUserIds, long invitedRoleId,
			long invitedTeamId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().addMemberRequests(
			groupId, receiverUserIds, invitedRoleId, invitedTeamId,
			serviceContext);
	}

	public static void addMemberRequests(
			long groupId, String[] emailAddresses, long invitedRoleId,
			long invitedTeamId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().addMemberRequests(
			groupId, emailAddresses, invitedRoleId, invitedTeamId,
			serviceContext);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static MemberRequestService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<MemberRequestService> _serviceSnapshot =
		new Snapshot<>(
			MemberRequestServiceUtil.class, MemberRequestService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:418085533