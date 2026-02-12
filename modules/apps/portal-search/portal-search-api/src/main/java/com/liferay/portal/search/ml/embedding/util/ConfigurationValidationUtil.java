/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.ml.embedding.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Petteri Karttunen
 */
public class ConfigurationValidationUtil {

	public static boolean validateAttributes(
		Map<String, Object> attributes, String[] keys) {

		if (MapUtil.isEmpty(attributes)) {
			_log.error(
				"Unable to create embedding because there are no " +
					"configuration attributes ");

			return false;
		}

		List<String> missingKeys = new ArrayList<>();

		for (String key : keys) {
			if (!attributes.containsKey(key)) {
				missingKeys.add(key);
			}
		}

		if (!missingKeys.isEmpty()) {
			_log.error(
				"Unable to create embedding because of missing configuration " +
					"attributes: " + String.join(", ", missingKeys));

			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationValidationUtil.class);

}