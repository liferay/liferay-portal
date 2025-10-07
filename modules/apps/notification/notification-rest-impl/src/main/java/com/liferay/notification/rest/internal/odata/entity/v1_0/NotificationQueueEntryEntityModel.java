/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.rest.internal.odata.entity.v1_0;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.odata.entity.DateTimeEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;

import java.util.Map;

/**
 * @author Pedro Leite
 */
public class NotificationQueueEntryEntityModel implements EntityModel {

	public NotificationQueueEntryEntityModel() {
		_entityFieldsMap = EntityModel.toEntityFieldsMap(
			new DateTimeEntityField(
				Field.SENT_DATE,
				locale -> Field.getSortableFieldName(Field.SENT_DATE),
				locale -> Field.SENT_DATE));
	}

	@Override
	public Map<String, EntityField> getEntityFieldsMap() {
		return _entityFieldsMap;
	}

	private final Map<String, EntityField> _entityFieldsMap;

}