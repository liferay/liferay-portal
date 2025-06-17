/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.manager.v1_0;

import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Sergio Jimenez del Coso
 */
public class SystemObjectEntry1toMObjectRelationshipElementsParserImpl
	extends BaseObjectRelationshipElementsParserImpl<Map<String, Object>> {

	public SystemObjectEntry1toMObjectRelationshipElementsParserImpl(
		ObjectDefinition objectDefinition) {

		super(objectDefinition);
	}

	@Override
	public String getObjectRelationshipType() {
		return ObjectRelationshipConstants.TYPE_ONE_TO_MANY;
	}

	@Override
	public List<Map<String, Object>> parse(
			ObjectRelationship objectRelationship, Object value)
		throws Exception {

		if (objectRelationship.getObjectDefinitionId1() ==
				objectDefinition.getObjectDefinitionId()) {

			Map<String, Object> systemObjectEntryMap = parseOne(value);

			if (systemObjectEntryMap == null) {
				return Collections.emptyList();
			}

			return Collections.singletonList(systemObjectEntryMap);
		}

		return parseMany(value);
	}

	@Override
	protected Map<String, Object> parseOne(Object object) {
		if (object == null) {
			return null;
		}

		validateOne(object);

		Map<String, Object> nestedSystemObjectEntryProperties =
			(Map<String, Object>)object;

		if (MapUtil.isEmpty(nestedSystemObjectEntryProperties)) {
			return null;
		}

		return nestedSystemObjectEntryProperties;
	}

}