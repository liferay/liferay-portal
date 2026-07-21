/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.util;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;

import java.util.Objects;

/**
 * @author Matthew Kong
 */
public class EmailUtil {

	public static String getCheckIconURL() {
		return FaroPropsValues.FARO_URL.concat(
			"/o/osb-faro-web/images/email/icon_check.png");
	}

	public static String getDocumentationURL(FaroProject faroProject) {
		if (faroProject.isDataPlatform()) {
			return "https://learn.liferay.com/w/liferay-data-platform/index";
		}

		return "https://learn.liferay.com/en/w/analytics-cloud/index";
	}

	public static String getEmailHeaderURL() {
		return FaroPropsValues.FARO_URL.concat(
			"/o/osb-faro-web/images/email/email_header.png");
	}

	public static String getLanguageKey(
		FaroProject faroProject, String analyticsCloudLanguageKey,
		String dataPlatformLanguageKey) {

		if (faroProject.isDataPlatform()) {
			return dataPlatformLanguageKey;
		}

		return analyticsCloudLanguageKey;
	}

	public static String getLiferayIconURL() {
		return FaroPropsValues.FARO_URL.concat(
			"/o/osb-faro-web/images/email/liferay_logo_1.png");
	}

	public static String getLiferayLogoIconURL() {
		return FaroPropsValues.FARO_URL.concat(
			"/o/osb-faro-web/images/email/liferay_logo.png");
	}

	public static String getProductName(FaroProject faroProject) {
		if (faroProject.isDataPlatform()) {
			return "Liferay Data Platform";
		}

		return "Liferay Analytics Cloud";
	}

	public static String getSenderEmailAddress(FaroProject faroProject) {
		if (faroProject.isDataPlatform()) {
			return "ldp@liferay.com";
		}

		return "ac@liferay.com";
	}

	public static String getSenderName(FaroProject faroProject) {
		if (faroProject.isDataPlatform()) {
			return "Liferay Data Platform";
		}

		return "Analytics Cloud";
	}

	public static String getShareIconURL() {
		return FaroPropsValues.FARO_URL.concat(
			"/o/osb-faro-web/images/email/icon_share.png");
	}

	public static String getTrendIconURL(String trend) {
		if (Objects.equals(trend, "NEGATIVE")) {
			return FaroPropsValues.FARO_URL.concat(
				"/o/osb-faro-web/images/email/icon_order_arrow_down.png");
		}
		else if (Objects.equals(trend, "POSITIVE")) {
			return FaroPropsValues.FARO_URL.concat(
				"/o/osb-faro-web/images/email/icon_order_arrow_up.png");
		}

		return FaroPropsValues.FARO_URL.concat(
			"/o/osb-faro-web/images/email/icon_empty.png");
	}

	public static String getWorkspaceURL(Group group) {
		StringBuilder sb = new StringBuilder(4);

		sb.append(FaroPropsValues.FARO_URL);
		sb.append("/workspace");

		String friendlyURL = group.getFriendlyURL();

		if ((friendlyURL != null) && !friendlyURL.isEmpty()) {
			sb.append(friendlyURL);
		}
		else {
			sb.append(StringPool.SLASH);
			sb.append(group.getGroupId());
		}

		return sb.toString();
	}

}