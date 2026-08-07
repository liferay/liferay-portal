/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.RoleAssignmentException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.fips.constants.FIPSConstants;

/**
 * @author Manuele Castro
 */
public class FIPSUtil {

	public static void checkCryptoOfficerRole(
			long administratorRoleId, long companyId, long[] roleIds)
		throws RoleAssignmentException {

		if (!PropsValues.FIPS_ENABLED) {
			return;
		}

		Role cryptoOfficerRole = RoleLocalServiceUtil.fetchRole(
			companyId, RoleConstants.CRYPTO_OFFICER);

		if ((cryptoOfficerRole != null) &&
			ArrayUtil.contains(roleIds, administratorRoleId) &&
			ArrayUtil.contains(roleIds, cryptoOfficerRole.getRoleId())) {

			throw new RoleAssignmentException(
				StringBundler.concat(
					"A user cannot be assigned to roles \"",
					RoleConstants.ADMINISTRATOR, "\" and \"",
					RoleConstants.CRYPTO_OFFICER,
					"\" at the same time in FIPS mode"));
		}
	}

	public static boolean hasCryptoOfficerRole(User user) {
		if (!PropsValues.FIPS_ENABLED) {
			return false;
		}

		try {
			return RoleLocalServiceUtil.hasUserRole(
				user.getUserId(), user.getCompanyId(),
				RoleConstants.CRYPTO_OFFICER, false);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return false;
	}

	public static boolean isCryptoOfficerPasswordPolicy(
		String passwordPolicyName) {

		if (PropsValues.FIPS_ENABLED &&
			StringUtil.equals(
				passwordPolicyName,
				FIPSConstants.PASSWORD_POLICY_NAME_CRYPTO_OFFICER)) {

			return true;
		}

		return false;
	}

	public static boolean isCryptoOfficerRole(String roleName) {
		if (PropsValues.FIPS_ENABLED &&
			StringUtil.equals(roleName, RoleConstants.CRYPTO_OFFICER)) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(FIPSUtil.class);

}