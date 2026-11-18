/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.upgrade.util;

import com.liferay.mcp.server.rest.internal.constants.MCPServerConstants;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Alejandro Tardín
 */
public class MCPServerProfileUpgradeProcessUtil {

	public static Set<String> getProfilePaths(
			ObjectEntryLocalService objectEntryLocalService,
			ObjectField objectField)
		throws PortalException {

		Set<String> profilePaths = new HashSet<>();

		if (objectField == null) {
			return profilePaths;
		}

		for (ObjectEntry objectEntry :
				objectEntryLocalService.getObjectEntries(
					0, objectField.getObjectDefinitionId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS)) {

			Map<String, Serializable> values =
				objectEntryLocalService.getValues(objectEntry);

			String profilePath = GetterUtil.getString(
				values.get("profilePath"));

			if (Validator.isNull(profilePath)) {
				continue;
			}

			profilePaths.add(profilePath);
		}

		return profilePaths;
	}

	public static void putProfilePath(
		String externalReferenceCode, ObjectField objectField,
		Set<String> profilePaths, Map<String, Serializable> values) {

		if ((objectField == null) ||
			Objects.equals(
				externalReferenceCode,
				MCPServerConstants.
					EXTERNAL_REFERENCE_CODE_MCP_SERVER_DEFAULT_PROFILE) ||
			Validator.isNotNull(values.get("profilePath"))) {

			return;
		}

		String name = GetterUtil.getString(values.get("name"));

		String profilePath = StringUtil.toLowerCase(name);

		profilePath = profilePath.replaceAll("[^a-z0-9]+", "-");
		profilePath = profilePath.replaceAll("^-+|-+$", "");

		if (Validator.isNull(profilePath)) {
			profilePath = "profile";
		}

		String uniqueProfilePath = profilePath;

		for (int i = 1; profilePaths.contains(uniqueProfilePath); i++) {
			uniqueProfilePath = profilePath + "-" + i;
		}

		profilePaths.add(uniqueProfilePath);

		if (_log.isWarnEnabled() && !name.equals(uniqueProfilePath)) {
			_log.warn(
				StringBundler.concat(
					"Generated profile path \"", uniqueProfilePath,
					"\" for MCP server profile \"", name, "\""));
		}

		values.put("profilePath", uniqueProfilePath);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MCPServerProfileUpgradeProcessUtil.class);

}