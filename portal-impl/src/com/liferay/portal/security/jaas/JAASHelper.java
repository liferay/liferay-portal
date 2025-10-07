/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.jaas;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsValues;

/**
 * @author Raymond Augé
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
public class JAASHelper {

	public static JAASHelper getInstance() {
		return _jaasHelper;
	}

	public static long getJAASUserId(long companyId, String name)
		throws PortalException {

		return _jaasHelper.doGetJAASUserId(companyId, name);
	}

	public static void setInstance(JAASHelper instance) {
		_jaasHelper = instance;
	}

	protected long doGetJAASUserId(long companyId, String name)
		throws PortalException {

		String jaasAuthType = PropsValues.PORTAL_JAAS_AUTH_TYPE;

		if (jaasAuthType.equals("login")) {
			Company company = CompanyLocalServiceUtil.getCompany(companyId);

			String authType = company.getAuthType();

			if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
				jaasAuthType = "emailAddress";
			}
			else if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
				jaasAuthType = "screenName";
			}
			else {
				jaasAuthType = "userId";
			}
		}

		long userId = 0;

		if (jaasAuthType.equals("emailAddress")) {
			User user = UserLocalServiceUtil.fetchUserByEmailAddress(
				companyId, name);

			if (user != null) {
				userId = user.getUserId();
			}
		}
		else if (jaasAuthType.equals("screenName")) {
			User user = UserLocalServiceUtil.fetchUserByScreenName(
				companyId, name);

			if (user != null) {
				userId = user.getUserId();
			}
		}
		else {
			userId = GetterUtil.getLong(name);
		}

		return userId;
	}

	private static JAASHelper _jaasHelper = new JAASHelper();

}