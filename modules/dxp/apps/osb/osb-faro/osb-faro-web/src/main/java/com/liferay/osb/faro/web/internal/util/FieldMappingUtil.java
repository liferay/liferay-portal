/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.util;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.constants.FieldMappingConstants;
import com.liferay.osb.faro.engine.client.model.FieldMapping;
import com.liferay.osb.faro.engine.client.model.FieldMappingMap;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Matthew Kong
 */
public class FieldMappingUtil {

	public static String getDisplayName(FieldMapping fieldMapping) {
		String languageKey =
			FieldMappingConstants.getDemographicsFieldMappingLanguageKey(
				fieldMapping.getFieldName());

		if (languageKey != null) {
			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			User user = permissionChecker.getUser();

			return LanguageUtil.get(user.getLocale(), languageKey);
		}

		return fieldMapping.getDisplayName();
	}

	public static List<FieldMappingMap> getNewFieldMappingMaps(
		ContactsEngineClient contactsEngineClient, FaroProject faroProject,
		String context, List<FieldMappingMap> fieldMappingMaps) {

		List<FieldMappingMap> newFieldMappingMaps = new ArrayList<>();

		Results<FieldMapping> results = contactsEngineClient.getFieldMappings(
			faroProject, context,
			TransformUtil.transform(fieldMappingMaps, FieldMappingMap::getName),
			1, 10000, null);

		Set<String> currentFieldNames = new HashSet<>();

		for (FieldMapping fieldMapping : results.getItems()) {
			currentFieldNames.add(fieldMapping.getFieldName());
		}

		Set<String> newFieldMappingNames = new HashSet<>();

		for (FieldMappingMap fieldMappingMap : fieldMappingMaps) {
			String name = fieldMappingMap.getName();

			if (currentFieldNames.contains(name) ||
				newFieldMappingNames.contains(name)) {

				continue;
			}

			newFieldMappingNames.add(name);

			newFieldMappingMaps.add(fieldMappingMap);
		}

		return newFieldMappingMaps;
	}

}