/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.util;

import com.liferay.portal.configuration.persistence.ConfigurationOverridePropertiesUtil;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;

import java.util.Map;

/**
 * @author Christian Moura
 */
public class AuditConfigurationOverrideUtil {

	public static boolean isOverridden(String name) {
		Map<String, Object> overrideProperties =
			ConfigurationOverridePropertiesUtil.getOverrideProperties(
				AuditConfiguration.class.getName());

		if (overrideProperties == null) {
			return false;
		}

		return overrideProperties.containsKey(name);
	}

}