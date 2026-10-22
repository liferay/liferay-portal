/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.util;

import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Map;

/**
 * @author Daniel Sanz
 */
public class AMImageAdaptationUtil {

	public static boolean isAdaptationDelegated(
		AMImageConfigurationEntry amImageConfigurationEntry, long companyId) {

		if ((amImageConfigurationEntry == null) ||
			!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-94784")) {

			return false;
		}

		Map<String, String> properties =
			amImageConfigurationEntry.getProperties();

		return GetterUtil.getBoolean(properties.get("delegate-adaptation"));
	}

}