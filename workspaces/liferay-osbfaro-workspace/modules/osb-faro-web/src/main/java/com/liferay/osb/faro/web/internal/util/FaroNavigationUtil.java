/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.util;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.provisioning.client.model.display.main.FaroSubscriptionDisplay;
import com.liferay.osb.faro.service.FaroProjectLocalServiceUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Eudaldo Alonso
 */
public class FaroNavigationUtil {

	public static String fetchLayoutURL(
			String friendlyURL, ThemeDisplay themeDisplay)
		throws Exception {

		Layout layout = LayoutLocalServiceUtil.fetchLayoutByFriendlyURL(
			themeDisplay.getScopeGroupId(), false, friendlyURL);

		if (layout == null) {
			return null;
		}

		return PortalUtil.getLayoutRelativeURL(layout, themeDisplay);
	}

	public static boolean isLDPEnabled(long groupId) throws Exception {
		FaroProject faroProject =
			FaroProjectLocalServiceUtil.fetchFaroProjectByGroupId(groupId);

		if ((faroProject == null) ||
			Validator.isNull(faroProject.getSubscription())) {

			return false;
		}

		FaroSubscriptionDisplay faroSubscriptionDisplay = JSONUtil.readValue(
			faroProject.getSubscription(), FaroSubscriptionDisplay.class);

		String name = faroSubscriptionDisplay.getName();

		if ((name != null) && name.contains("Data Platform")) {
			return true;
		}

		return false;
	}

}