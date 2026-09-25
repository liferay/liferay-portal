/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.invitation.invite.members.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link MemberRequestService}.
 *
 * @author Brian Wing Shun Chan
 * @see MemberRequestService
 * @generated
 */
public class MemberRequestServiceWrapper
	implements MemberRequestService, ServiceWrapper<MemberRequestService> {

	public MemberRequestServiceWrapper() {
		this(null);
	}

	public MemberRequestServiceWrapper(
		MemberRequestService memberRequestService) {

		_memberRequestService = memberRequestService;
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _memberRequestService.getOSGiServiceIdentifier();
	}

	@Override
	public MemberRequestService getWrappedService() {
		return _memberRequestService;
	}

	@Override
	public void setWrappedService(MemberRequestService memberRequestService) {
		_memberRequestService = memberRequestService;
	}

	private MemberRequestService _memberRequestService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1498663214