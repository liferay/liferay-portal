/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.util.PortalInstances;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Igor Spasic
 */
public class UserResolver {

	public UserResolver(HttpServletRequest httpServletRequest)
		throws PortalException {

		long companyId = ParamUtil.getLong(httpServletRequest, "companyId");
		User user = null;

		String remoteUser = httpServletRequest.getRemoteUser();

		long userId = GetterUtil.getLong(remoteUser);

		if (userId == 0) {
			remoteUser = null;
		}

		if (remoteUser != null) {
			PrincipalThreadLocal.setName(remoteUser);

			user = UserLocalServiceUtil.getUserById(userId);

			if (companyId == 0) {
				companyId = user.getCompanyId();
			}
		}
		else {
			if (companyId == 0) {
				companyId = PortalInstances.getCompanyId(httpServletRequest);
			}

			if (companyId != 0) {
				user = UserLocalServiceUtil.getGuestUser(companyId);
			}
		}

		_companyId = companyId;
		_user = user;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public User getUser() {
		return _user;
	}

	private final long _companyId;
	private final User _user;

}