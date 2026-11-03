/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.util.v1_0;

import com.liferay.object.constants.ObjectConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectPortletKeys;
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
		long companyId,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		String resourceName) {

		if (StringUtil.startsWith(resourceName, _PREFIX_CLASS_NAME)) {
			return _getResourceName(
				objectDefinitionLocalService.fetchObjectDefinitionByClassName(
					companyId, resourceName),
				_PREFIX_CLASS_NAME, resourceName);
		}

		if (StringUtil.startsWith(resourceName, _PREFIX_PORTLET_ID)) {
			return _getResourceName(
				objectDefinitionLocalService.fetchObjectDefinitionByClassName(
					companyId,
					_PREFIX_CLASS_NAME +
						resourceName.substring(_PREFIX_PORTLET_ID.length())),
				_PREFIX_PORTLET_ID, resourceName);
		}

		if (StringUtil.startsWith(resourceName, _PREFIX_RESOURCE_NAME)) {
			return _getResourceName(
				objectDefinitionLocalService.fetchObjectDefinition(
					GetterUtil.getLong(
						resourceName.substring(
							_PREFIX_RESOURCE_NAME.length()))),
				_PREFIX_RESOURCE_NAME, resourceName);
		}

		return resourceName;
	}

	public static String toResourceName(
		long companyId,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		String resourceName) {

		if (StringUtil.startsWith(resourceName, _PREFIX_CLASS_NAME)) {
			ObjectDefinition objectDefinition =
				objectDefinitionLocalService.
					fetchObjectDefinitionByExternalReferenceCode(
						resourceName.substring(_PREFIX_CLASS_NAME.length()),
						companyId);

			if (objectDefinition != null) {
				return objectDefinition.getClassName();
			}
		}
		else if (StringUtil.startsWith(resourceName, _PREFIX_PORTLET_ID)) {
			ObjectDefinition objectDefinition =
				objectDefinitionLocalService.
					fetchObjectDefinitionByExternalReferenceCode(
						resourceName.substring(_PREFIX_PORTLET_ID.length()),
						companyId);

			if (objectDefinition != null) {
				return objectDefinition.getPortletId();
			}
		}
		else if (StringUtil.startsWith(resourceName, _PREFIX_RESOURCE_NAME)) {
			ObjectDefinition objectDefinition =
				objectDefinitionLocalService.
					fetchObjectDefinitionByExternalReferenceCode(
						resourceName.substring(_PREFIX_RESOURCE_NAME.length()),
						companyId);

			if (objectDefinition != null) {
				return _PREFIX_RESOURCE_NAME +
					objectDefinition.getObjectDefinitionId();
			}
		}

		return resourceName;
	}

	private static String _getResourceName(
		ObjectDefinition objectDefinition, String prefix, String resourceName) {

		if (objectDefinition == null) {
			return resourceName;
		}

		return prefix + objectDefinition.getExternalReferenceCode();
	}

	private static final String _PREFIX_CLASS_NAME =
		ObjectDefinitionConstants.CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION;

	private static final String _PREFIX_PORTLET_ID =
		ObjectPortletKeys.OBJECT_DEFINITIONS + StringPool.UNDERLINE;

	private static final String _PREFIX_RESOURCE_NAME =
		ObjectConstants.RESOURCE_NAME + StringPool.POUND;

}