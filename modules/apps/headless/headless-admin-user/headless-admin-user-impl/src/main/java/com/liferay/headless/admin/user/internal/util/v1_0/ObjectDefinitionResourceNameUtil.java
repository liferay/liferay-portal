/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.util.v1_0;

import com.liferay.object.constants.ObjectConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Balazs Breier
 */
public class ObjectDefinitionResourceNameUtil {

	public static String toExternalReferenceCodeResourceName(
		String resourceName,
		ObjectDefinitionLocalService objectDefinitionLocalService) {

		if (!StringUtil.startsWith(resourceName, _RESOURCE_NAME_PREFIX)) {
			return resourceName;
		}

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.fetchObjectDefinition(
				GetterUtil.getLong(
					resourceName.substring(_RESOURCE_NAME_PREFIX.length())));

		if (objectDefinition == null) {
			return resourceName;
		}

		return _RESOURCE_NAME_PREFIX +
			objectDefinition.getExternalReferenceCode();
	}

	public static String toObjectDefinitionIdResourceName(
		String resourceName, long companyId,
		ObjectDefinitionLocalService objectDefinitionLocalService) {

		if (!StringUtil.startsWith(resourceName, _RESOURCE_NAME_PREFIX)) {
			return resourceName;
		}

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					resourceName.substring(_RESOURCE_NAME_PREFIX.length()),
					companyId);

		if (objectDefinition == null) {
			return resourceName;
		}

		return _RESOURCE_NAME_PREFIX + objectDefinition.getObjectDefinitionId();
	}

	private static final String _RESOURCE_NAME_PREFIX =
		ObjectConstants.RESOURCE_NAME + StringPool.POUND;

}