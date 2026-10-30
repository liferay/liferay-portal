/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletRequest;

/**
 * @author Víctor Galán
 */
public class AudienceVariationUtil {

	public static String getAudienceEntryERC(PortletRequest portletRequest) {
		if (!FeatureFlagManagerUtil.isEnabled(
				PortalUtil.getCompanyId(portletRequest), "LPD-109242")) {

			return StringPool.BLANK;
		}

		return ParamUtil.getString(portletRequest, "audienceEntryERC");
	}

}