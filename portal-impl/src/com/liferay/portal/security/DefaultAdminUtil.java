/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.PropsValues;

/**
 * @author Alvaro Saugar
 * @author Stian Sigvartsen
 */
public class DefaultAdminUtil {

	public static User fetchDefaultAdmin(long companyId) {
		Company company = CompanyLocalServiceUtil.fetchCompany(companyId);

		String emailAddressAdmin =
			PropsValues.DEFAULT_ADMIN_EMAIL_ADDRESS_PREFIX + StringPool.AT +
				company.getMx();

		return UserLocalServiceUtil.fetchUserByEmailAddress(
			companyId, emailAddressAdmin);
	}

}