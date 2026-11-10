/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.upgrade.util;

import com.liferay.mcp.server.rest.internal.constants.MCPServerConstants;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Alejandro Tardín
 */
public class MCPServerProfileUpgradeProcessUtil {

	public static void putProfilePath(
		String externalReferenceCode, long objectDefinitionId,
		ObjectFieldLocalService objectFieldLocalService,
		Map<String, Serializable> values) {

		if (Objects.equals(
				externalReferenceCode,
				MCPServerConstants.
					EXTERNAL_REFERENCE_CODE_MCP_SERVER_DEFAULT_PROFILE) ||
			Validator.isNotNull(values.get("profilePath"))) {

			return;
		}

		ObjectField objectField = objectFieldLocalService.fetchObjectField(
			objectDefinitionId, "profilePath");

		if (objectField == null) {
			return;
		}

		String profilePath = StringUtil.toLowerCase(
			GetterUtil.getString(values.get("name")));

		profilePath = profilePath.replaceAll("[^a-z0-9]+", "-");

		values.put("profilePath", profilePath.replaceAll("^-+|-+$", ""));
	}

}