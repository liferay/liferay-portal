/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.util;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.site.pim.site.initializer.constants.PIMObjectFolderConstants;

import java.util.Collections;
import java.util.List;

/**
 * @author Andrea Sbarra
 */
public class PIMProductTypeUtil {

	public static List<ObjectDefinition> getObjectDefinitions(
		long companyId,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectFolderLocalService objectFolderLocalService) {

		ObjectFolder objectFolder =
			objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				PIMObjectFolderConstants.EXTERNAL_REFERENCE_CODE_PRODUCT_TYPES,
				companyId);

		if (objectFolder == null) {
			return Collections.emptyList();
		}

		return objectDefinitionLocalService.getObjectFolderObjectDefinitions(
			objectFolder.getObjectFolderId());
	}

	public static List<ObjectField> getObjectFields(
		ObjectDefinition objectDefinition,
		ObjectFieldLocalService objectFieldLocalService) {

		return ListUtil.filter(
			objectFieldLocalService.getObjectFields(
				objectDefinition.getObjectDefinitionId()),
			objectField -> {
				if (GetterUtil.getBoolean(objectField.getReadOnly())) {
					return false;
				}

				String businessType = objectField.getBusinessType();

				if (businessType.equals(
						ObjectFieldConstants.BUSINESS_TYPE_DATE) ||
					businessType.equals(
						ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME)) {

					return false;
				}

				return true;
			});
	}

}