/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.manager.v1_0;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectRelationshipElementsParser;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.ws.rs.BadRequestException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Carlos Correa
 * @author Sergio Jimenez del Coso
 */
public abstract class BaseObjectRelationshipElementsParserImpl<T>
	implements ObjectRelationshipElementsParser {

	public BaseObjectRelationshipElementsParserImpl(
		ObjectDefinition objectDefinition) {

		this.objectDefinition = objectDefinition;
	}

	@Override
	public String getClassName() {
		return objectDefinition.getClassName();
	}

	@Override
	public long getCompanyId() {
		return objectDefinition.getCompanyId();
	}

	protected List<T> parseMany(Object object) {
		List<T> objects = null;

		if (object == null) {
			objects = new ArrayList<>();
		}
		else if (object instanceof List) {
			objects = (List<T>)object;
		}
		else if (object instanceof Object[]) {
			objects = (List<T>)Arrays.asList((Object[])object);
		}
		else {
			throw new BadRequestException(
				"Unable to create nested object entries");
		}

		return TransformUtil.transform(objects, this::parseOne);
	}

	protected abstract T parseOne(Object object);

	protected ObjectEntry toObjectEntry(
		Map<String, Object> nestedObjectEntryProperties) {

		return ObjectMapperUtil.readValue(
			ObjectEntry.class, nestedObjectEntryProperties);
	}

	protected void validateOne(Object object) {
		if (!(object instanceof Map)) {
			throw new BadRequestException(
				"Unable to create nested object entries");
		}
	}

	protected ObjectDefinition objectDefinition;

}