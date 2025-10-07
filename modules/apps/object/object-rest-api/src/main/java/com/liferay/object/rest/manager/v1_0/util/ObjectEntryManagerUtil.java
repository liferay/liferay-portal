/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.manager.v1_0.util;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;

import java.util.Map;

/**
 * @author Paulo Albuquerque
 */
public class ObjectEntryManagerUtil {

	public static ObjectEntry partialUpdateObjectEntry(
		ObjectEntry existingObjectEntry, long objectDefinitionId,
		ObjectEntry objectEntry) {

		if (objectEntry.getDateCreated() != null) {
			existingObjectEntry.setDateCreated(objectEntry::getDateCreated);
		}

		if (objectEntry.getDateModified() != null) {
			existingObjectEntry.setDateModified(objectEntry::getDateModified);
		}

		if (objectEntry.getDisplayDate() != null) {
			existingObjectEntry.setDisplayDate(objectEntry::getDisplayDate);
		}

		if (objectEntry.getExpirationDate() != null) {
			existingObjectEntry.setExpirationDate(
				objectEntry::getExpirationDate);
		}

		if (objectEntry.getExternalReferenceCode() != null) {
			existingObjectEntry.setExternalReferenceCode(
				objectEntry::getExternalReferenceCode);
		}

		if (objectEntry.getFriendlyUrlPath() != null) {
			existingObjectEntry.setFriendlyUrlPath(
				objectEntry::getFriendlyUrlPath);
		}

		if (objectEntry.getFriendlyUrlPath_i18n() != null) {
			existingObjectEntry.setFriendlyUrlPath_i18n(
				objectEntry::getFriendlyUrlPath_i18n);
		}

		if (objectEntry.getKeywords() != null) {
			existingObjectEntry.setKeywords(objectEntry::getKeywords);
		}

		if (objectEntry.getObjectEntryFolderExternalReferenceCode() != null) {
			existingObjectEntry.setObjectEntryFolderExternalReferenceCode(
				objectEntry::getObjectEntryFolderExternalReferenceCode);
		}

		if (objectEntry.getObjectEntryFolderId() != null) {
			existingObjectEntry.setObjectEntryFolderId(
				objectEntry::getObjectEntryFolderId);
		}

		existingObjectEntry.setPermissions(objectEntry::getPermissions);

		if (objectEntry.getProperties() != null) {
			Map<String, Object> existingProperties =
				existingObjectEntry.getProperties();

			Map<String, Object> properties = objectEntry.getProperties();

			for (ObjectField objectField :
					ObjectFieldLocalServiceUtil.getObjectFieldsByBusinessType(
						objectDefinitionId,
						ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP)) {

				String objectRelationshipERCObjectFieldName =
					ObjectFieldSettingUtil.getValue(
						ObjectFieldSettingConstants.
							NAME_OBJECT_RELATIONSHIP_ERC_OBJECT_FIELD_NAME,
						objectField);

				String relationshipObjectFieldName = objectField.getName();

				if (properties.containsKey(relationshipObjectFieldName) &&
					!properties.containsKey(
						objectRelationshipERCObjectFieldName)) {

					existingProperties.remove(
						objectRelationshipERCObjectFieldName);
				}
				else if (properties.containsKey(
							objectRelationshipERCObjectFieldName) &&
						 !properties.containsKey(relationshipObjectFieldName)) {

					existingProperties.remove(relationshipObjectFieldName);
				}
			}

			existingProperties.putAll(properties);

			existingObjectEntry.setProperties(() -> existingProperties);
		}

		if (objectEntry.getReviewDate() != null) {
			existingObjectEntry.setReviewDate(objectEntry::getReviewDate);
		}

		if (objectEntry.getStatus() != null) {
			existingObjectEntry.setStatus(objectEntry::getStatus);
		}

		if (objectEntry.getTaxonomyCategoryIds() != null) {
			existingObjectEntry.setTaxonomyCategoryIds(
				objectEntry::getTaxonomyCategoryIds);
		}

		return existingObjectEntry;
	}

}