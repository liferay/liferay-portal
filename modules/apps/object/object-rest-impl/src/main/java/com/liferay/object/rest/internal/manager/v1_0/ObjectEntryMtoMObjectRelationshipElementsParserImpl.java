/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.manager.v1_0;

import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;

import java.util.List;
import java.util.Map;

/**
 * @author Carlos Correa
 * @author Sergio Jimenez del Coso
 */
public class ObjectEntryMtoMObjectRelationshipElementsParserImpl
	extends BaseObjectRelationshipElementsParserImpl<ObjectEntry> {

	public ObjectEntryMtoMObjectRelationshipElementsParserImpl(
		ObjectDefinition objectDefinition) {

		super(objectDefinition);
	}

	@Override
	public String getObjectRelationshipType() {
		return ObjectRelationshipConstants.TYPE_MANY_TO_MANY;
	}

	@Override
	public List<ObjectEntry> parse(
			ObjectRelationship objectRelationship, Object value)
		throws Exception {

		return parseMany(value);
	}

	@Override
	protected ObjectEntry parseOne(Object object) {
		if (object == null) {
			return null;
		}

		validateOne(object);

		return toObjectEntry((Map<String, Object>)object);
	}

}