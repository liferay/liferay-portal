/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.group.capability.GroupCapabilityUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

/**
 * @author Daniel Raposo
 */
public class ScopeUtil {

	public static String getAPIURL(Group group, String endpoint) {
		return _BASE_PATH + _getScopePath(group) + endpoint;
	}

	public static String getAPIURL(String endpoint) {
		return _BASE_PATH + endpoint;
	}

	public static boolean isCommentsAndRatingsEnabled(Group group) {
		if (!isInstanceScoped(group) ||
			FeatureFlagManagerUtil.isEnabled(
				group.getCompanyId(), "LPD-43996")) {

			return true;
		}

		return false;
	}

	public static boolean isInstanceScoped(Group group) {
		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		if (group.isControlPanel() ||
			stagingGroupHelper.isCompanyGroup(group)) {

			return true;
		}

		return false;
	}

	public static boolean isLookAndFeelEnabled(Group group) {
		if (GroupCapabilityUtil.isSupportsPages(group) && !group.isCompany() &&
			!group.isLayoutPrototype()) {

			return true;
		}

		return false;
	}

	private static String _getScopePath(Group group) {
		if (isInstanceScoped(group)) {
			return StringPool.BLANK;
		}

		String externalReferenceCode = URLEncoder.encode(
			group.getExternalReferenceCode(), StandardCharsets.UTF_8);

		if (group.isDepot()) {
			return "/asset-libraries/" + externalReferenceCode;
		}

		return "/sites/" + externalReferenceCode;
	}

	private static final String _BASE_PATH = "/o/export-import/v1.0";

}