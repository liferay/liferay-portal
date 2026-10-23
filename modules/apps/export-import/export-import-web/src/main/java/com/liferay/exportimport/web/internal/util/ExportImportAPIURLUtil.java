/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.util;

import com.liferay.exportimport.util.ScopeUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.util.Validator;

import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

/**
 * @author Daniel Raposo
 */
public class ExportImportAPIURLUtil {

	public static String getAPIURL(
		String endpoint, Group group, long plid, String portletId) {

		if (Validator.isBlank(portletId)) {
			return ScopeUtil.getAPIURL(group, endpoint);
		}

		Layout layout = getPortletLayout(plid, portletId);

		if (layout == null) {
			return StringBundler.concat(
				ScopeUtil.getAPIURL(group, endpoint), "?portletId=",
				URLEncoder.encode(portletId, StandardCharsets.UTF_8));
		}

		return StringBundler.concat(
			ScopeUtil.getAPIURL(
				GroupLocalServiceUtil.fetchGroup(layout.getGroupId()),
				endpoint),
			"?portletId=", URLEncoder.encode(portletId, StandardCharsets.UTF_8),
			"&sitePageExternalReferenceCode=",
			URLEncoder.encode(
				layout.getExternalReferenceCode(), StandardCharsets.UTF_8));
	}

	public static Layout getPortletLayout(long plid, String portletId) {
		if (Validator.isBlank(portletId)) {
			return null;
		}

		Layout layout = LayoutLocalServiceUtil.fetchLayout(plid);

		if ((layout == null) || layout.isTypeControlPanel()) {
			return null;
		}

		return layout;
	}

}