/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.language.override.internal.odata.entity.v1_0;

import com.liferay.portal.language.override.model.PLOEntryTable;
import com.liferay.portal.odata.entity.DateTimeEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.entity.StringEntityField;

import java.util.Map;

/**
 * @author Thiago Buarque
 */
public class LanguageOverrideEntityModel implements EntityModel {

	public LanguageOverrideEntityModel() {
		_entityFieldsMap = EntityModel.toEntityFieldsMap(
			new DateTimeEntityField(
				"dateCreated",
				locale -> PLOEntryTable.INSTANCE.createDate.getName(),
				locale -> PLOEntryTable.INSTANCE.createDate.getName()),
			new DateTimeEntityField(
				"dateModified",
				locale -> PLOEntryTable.INSTANCE.modifiedDate.getName(),
				locale -> PLOEntryTable.INSTANCE.modifiedDate.getName()),
			new StringEntityField(
				"externalReferenceCode",
				locale ->
					PLOEntryTable.INSTANCE.externalReferenceCode.getName()),
			new StringEntityField(
				"key", locale -> PLOEntryTable.INSTANCE.key.getName()),
			new StringEntityField(
				"languageId",
				locale -> PLOEntryTable.INSTANCE.languageId.getName()));
	}

	@Override
	public Map<String, EntityField> getEntityFieldsMap() {
		return _entityFieldsMap;
	}

	private final Map<String, EntityField> _entityFieldsMap;

}