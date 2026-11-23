/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.frontend.data.set.filter.factory;

import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectViewFilterColumn;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.web.internal.object.entries.frontend.data.set.filter.ObjectFieldDateRangeFDSFilter;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.Field;

import java.util.Locale;
import java.util.Objects;

/**
 * @author Feliphe Marinho
 */
public class ObjectFieldDateRangeFDSFilterFactory
	implements ObjectFieldFDSFilterFactory {

	public ObjectFieldDateRangeFDSFilterFactory(
		Language language, ObjectFieldLocalService objectFieldLocalService) {

		_language = language;
		_objectFieldLocalService = objectFieldLocalService;
	}

	@Override
	public FDSFilter create(
			long groupId, Locale locale, long objectDefinitionId,
			ObjectViewFilterColumn objectViewFilterColumn)
		throws PortalException {

		String id = objectViewFilterColumn.getObjectFieldName();

		if (Objects.equals(
				objectViewFilterColumn.getObjectFieldName(),
				Field.CREATE_DATE)) {

			id = "dateCreated";
		}
		else if (Objects.equals(
					objectViewFilterColumn.getObjectFieldName(),
					"modifiedDate")) {

			id = "dateModified";
		}

		return new ObjectFieldDateRangeFDSFilter(
			groupId, id,
			_getLabel(
				locale, objectDefinitionId,
				objectViewFilterColumn.getObjectFieldName()));
	}

	private String _getLabel(
		Locale locale, long objectDefinitionId,
		String objectViewFilterColumnName) {

		if (Objects.equals(objectViewFilterColumnName, "dateCreated")) {
			return _language.get(locale, "creation-date");
		}

		if (Objects.equals(objectViewFilterColumnName, "dateModified")) {
			return _language.get(locale, "modified-date");
		}

		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			objectDefinitionId, objectViewFilterColumnName);

		return objectField.getLabel(locale);
	}

	private final Language _language;
	private final ObjectFieldLocalService _objectFieldLocalService;

}