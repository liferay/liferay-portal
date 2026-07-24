/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.util;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;

import java.util.Objects;

/**
 * @author Jürgen Kappler
 */
public class DepotRoleNameUtil {

	public static String getAdministratorRoleName(
		long companyId, String subtype) {

		if (Objects.equals(
				subtype, DepotRolesConstants.SUBTYPE_DESIGN_LIBRARY) &&
			FeatureFlagManagerUtil.isEnabled(companyId, "LPD-57283")) {

			return DepotRolesConstants.DESIGN_LIBRARY_ADMINISTRATOR;
		}
		else if (Objects.equals(subtype, DepotRolesConstants.SUBTYPE_PROJECT)) {
			return DepotRolesConstants.PROJECT_MANAGER;
		}

		return DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR;
	}

	public static String getContentReviewerRoleName(
		long companyId, String subtype) {

		if (Objects.equals(
				subtype, DepotRolesConstants.SUBTYPE_DESIGN_LIBRARY) &&
			FeatureFlagManagerUtil.isEnabled(companyId, "LPD-57283")) {

			return DepotRolesConstants.DESIGN_LIBRARY_CONTENT_REVIEWER;
		}

		return DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER;
	}

	public static String getMemberRoleName(long companyId, String subtype) {
		if (Objects.equals(
				subtype, DepotRolesConstants.SUBTYPE_DESIGN_LIBRARY) &&
			FeatureFlagManagerUtil.isEnabled(companyId, "LPD-57283")) {

			return DepotRolesConstants.DESIGN_LIBRARY_MEMBER;
		}
		else if (Objects.equals(subtype, DepotRolesConstants.SUBTYPE_PROJECT)) {
			return DepotRolesConstants.PROJECT_MEMBER;
		}

		return DepotRolesConstants.ASSET_LIBRARY_MEMBER;
	}

	public static String getOwnerRoleName(long companyId, String subtype) {
		if (Objects.equals(
				subtype, DepotRolesConstants.SUBTYPE_DESIGN_LIBRARY) &&
			FeatureFlagManagerUtil.isEnabled(companyId, "LPD-57283")) {

			return DepotRolesConstants.DESIGN_LIBRARY_OWNER;
		}

		return DepotRolesConstants.ASSET_LIBRARY_OWNER;
	}

}