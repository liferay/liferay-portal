/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.util;

import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.field.business.type.ObjectFieldBusinessTypeRegistry;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

/**
 * @author Carolina Barbosa
 */
public class ObjectEntryValuesUtil {

	public static Object getValue(
			Long groupId,
			ObjectDefinitionLocalService objectDefinitionLocalService,
			ObjectEntryLocalService objectEntryLocalService,
			ObjectField objectField,
			ObjectFieldBusinessTypeRegistry objectFieldBusinessTypeRegistry,
			long userId, Map<String, Object> values)
		throws PortalException {

		try {
			ObjectFieldBusinessType objectFieldBusinessType =
				objectFieldBusinessTypeRegistry.getObjectFieldBusinessType(
					objectField.getBusinessType());

			return objectFieldBusinessType.getValue(
				groupId, objectField, userId, values);
		}
		catch (NoSuchObjectEntryException noSuchObjectEntryException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchObjectEntryException);
			}

			String externalReferenceCode =
				noSuchObjectEntryException.getExternalReferenceCode();

			if (Validator.isNull(externalReferenceCode)) {
				throw noSuchObjectEntryException;
			}

			ObjectEntry objectEntry = objectEntryLocalService.addObjectEntry(
				externalReferenceCode, groupId, userId,
				objectDefinitionLocalService.getObjectDefinition(
					noSuchObjectEntryException.getObjectDefinitionId()),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);

			return objectEntry.getObjectEntryId();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryValuesUtil.class);

}