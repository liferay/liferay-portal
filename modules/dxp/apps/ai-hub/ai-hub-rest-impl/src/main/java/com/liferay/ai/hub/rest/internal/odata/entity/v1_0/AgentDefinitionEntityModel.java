/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.internal.odata.entity.v1_0;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.odata.entity.BooleanEntityField;
import com.liferay.portal.odata.entity.DateTimeEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Davyson Melo
 */
@Component(
	property = "entity.model.name=" + AgentDefinitionEntityModel.NAME,
	service = EntityModel.class
)
public class AgentDefinitionEntityModel implements EntityModel {

	public static final String NAME = "AgentDefinition";

	@Override
	public Map<String, EntityField> getEntityFieldsMap() {
		return _entityFieldMap;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Activate
	protected void activate() {
		_entityFieldMap = EntityModel.toEntityFieldsMap(
			new BooleanEntityField("active", locale -> "active"),
			new DateTimeEntityField(
				"dateCreated", locale -> Field.CREATE_DATE,
				locale -> Field.CREATE_DATE),
			new DateTimeEntityField(
				"dateModified", locale -> "modifiedDate",
				locale -> "modifiedDate"));
	}

	private Map<String, EntityField> _entityFieldMap;

}