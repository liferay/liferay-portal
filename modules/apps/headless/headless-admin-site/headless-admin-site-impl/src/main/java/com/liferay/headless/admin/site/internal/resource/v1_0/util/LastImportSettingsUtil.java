/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Collections;
import java.util.Map;

/**
 * @author Lourdes Fernández Besada
 */
public class LastImportSettingsUtil {

	public static Map<String, String> getLastImportSettingsMap(long userId) {
		if (!ExportImportThreadLocal.isStagingInProcess() ||
			!ExportImportThreadLocal.isLayoutImportInProcess()) {

			return Collections.emptyMap();
		}

		User user = UserLocalServiceUtil.fetchUser(userId);

		if (user == null) {
			return HashMapBuilder.put(
				"last-import-date", String.valueOf(System.currentTimeMillis())
			).build();
		}

		return HashMapBuilder.put(
			"last-import-date", String.valueOf(System.currentTimeMillis())
		).put(
			"last-import-user-name", user::getFullName
		).put(
			"last-import-user-uuid", user::getUuid
		).build();
	}

}