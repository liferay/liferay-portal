/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.frontend.data.set.filter.factory;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectViewFilterColumnConstants;
import com.liferay.object.field.filter.parser.ObjectFieldFilterContributorRegistry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectViewFilterColumn;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Feliphe Marinho
 */
public class ObjectFieldFDSFilterFactoryRegistry {

	public ObjectFieldFDSFilterFactoryRegistry(
		Language language,
		ObjectFieldFilterContributorRegistry
			objectFieldFilterContributorRegistry,
		ObjectFieldLocalService objectFieldLocalService) {

		_objectFieldLocalService = objectFieldLocalService;

		ObjectFieldDateRangeFDSFilterFactory
			objectFieldDateRangeFDSFilterFactory =
				new ObjectFieldDateRangeFDSFilterFactory(
					language, _objectFieldLocalService);

		_objectFieldBusinessTypeKeyMap.put(
			ObjectFieldConstants.BUSINESS_TYPE_DATE,
			objectFieldDateRangeFDSFilterFactory);
		_objectFieldFilterTypeKeyMap.put(
			ObjectViewFilterColumnConstants.FILTER_TYPE_DATE_RANGE,
			objectFieldDateRangeFDSFilterFactory);

		ListTypeEntryObjectFieldFDSFilterFactory
			listTypeEntryObjectFieldFDSFilterFactory =
				new ListTypeEntryObjectFieldFDSFilterFactory(
					objectFieldFilterContributorRegistry);

		_objectFieldBusinessTypeKeyMap.put(
			ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST,
			listTypeEntryObjectFieldFDSFilterFactory);
		_objectFieldBusinessTypeKeyMap.put(
			ObjectFieldConstants.BUSINESS_TYPE_PICKLIST,
			listTypeEntryObjectFieldFDSFilterFactory);
		_objectFieldBusinessTypeKeyMap.put(
			ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP,
			listTypeEntryObjectFieldFDSFilterFactory);
		_objectFieldFilterTypeKeyMap.put(
			ObjectViewFilterColumnConstants.FILTER_TYPE_EXCLUDES,
			listTypeEntryObjectFieldFDSFilterFactory);
		_objectFieldFilterTypeKeyMap.put(
			ObjectViewFilterColumnConstants.FILTER_TYPE_INCLUDES,
			listTypeEntryObjectFieldFDSFilterFactory);
	}

	public ObjectFieldFDSFilterFactory getObjectFieldFDSFilterFactory(
			long objectDefinitionId,
			ObjectViewFilterColumn objectViewFilterColumn)
		throws PortalException {

		if (Validator.isNotNull(objectViewFilterColumn.getFilterType())) {
			return _objectFieldFilterTypeKeyMap.get(
				objectViewFilterColumn.getFilterType());
		}

		if (Objects.equals(
				objectViewFilterColumn.getObjectFieldName(), "dateCreated") ||
			Objects.equals(
				objectViewFilterColumn.getObjectFieldName(), "dateModified")) {

			return _objectFieldBusinessTypeKeyMap.get(
				ObjectFieldConstants.BUSINESS_TYPE_DATE);
		}

		if (Objects.equals(
				objectViewFilterColumn.getObjectFieldName(), "status")) {

			return _objectFieldBusinessTypeKeyMap.get(
				ObjectFieldConstants.BUSINESS_TYPE_PICKLIST);
		}

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectDefinitionId, objectViewFilterColumn.getObjectFieldName());

		return _objectFieldBusinessTypeKeyMap.get(
			objectField.getBusinessType());
	}

	private final Map<String, ObjectFieldFDSFilterFactory>
		_objectFieldBusinessTypeKeyMap = new HashMap<>();
	private final Map<String, ObjectFieldFDSFilterFactory>
		_objectFieldFilterTypeKeyMap = new HashMap<>();
	private final ObjectFieldLocalService _objectFieldLocalService;

}