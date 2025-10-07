/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.headless.admin.site.dto.v1_0.NavigationSettings;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.util.Objects;

/**
 * @author Jürgen Kappler
 * @author Javier de Arcos
 */
public class NavigationSettingsUtil {

	public static NavigationSettings toNavigationSettings(
		UnicodeProperties unicodeProperties) {

		return new NavigationSettings() {
			{
				setTarget(() -> unicodeProperties.getProperty("target"));
				setTargetType(
					() -> {
						if (Objects.equals(
								unicodeProperties.getProperty("targetType"),
								"useNewTab")) {

							return TargetType.NEW_TAB;
						}

						return TargetType.SPECIFIC_FRAME;
					});
			}
		};
	}

}