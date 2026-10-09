/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.jaxrs.extension;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.Serializable;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Javier de Arcos
 */
@JsonFilter("Liferay.Vulcan")
public class ExtendedEntity {

	public static ExtendedEntity extend(
		Object entity, Map<String, Serializable> extendedProperties,
		Set<String> filteredPropertyKeys) {

		if (extendedProperties == null) {
			extendedProperties = Collections.emptyMap();
		}

		if (filteredPropertyKeys == null) {
			filteredPropertyKeys = Collections.emptySet();
		}

		return new ExtendedEntity(
			entity, extendedProperties, filteredPropertyKeys);
	}

	@JsonUnwrapped
	public Object getEntity() {
		return _entity;
	}

	@JsonAnyGetter
	public Map<String, Serializable> getExtendedProperties() {
		return _extendedProperties;
	}

	private ExtendedEntity(
		Object entity, Map<String, Serializable> extendedProperties,
		Set<String> filteredPropertyNames) {

		_entity = entity;

		_extendedProperties = new HashMap<>(extendedProperties);

		Set<String> extendedPropertyKeys = _extendedProperties.keySet();

		extendedPropertyKeys.removeIf(Objects::isNull);

		for (String key : filteredPropertyNames) {
			_extendedProperties.put(key, null);
		}

		for (Field field : _getAllFields()) {
			if (!_extendedProperties.containsKey(field.getName())) {
				continue;
			}

			try {
				field.setAccessible(true);
				field.set(_entity, null);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}
	}

	private Field[] _getAllFields() {
		List<Field> fields = new ArrayList<>();

		Class<?> clazz = _entity.getClass();

		while (clazz != Object.class) {
			Collections.addAll(fields, clazz.getDeclaredFields());

			clazz = clazz.getSuperclass();
		}

		return fields.toArray(new Field[0]);
	}

	private static final Log _log = LogFactoryUtil.getLog(ExtendedEntity.class);

	private final Object _entity;
	private final Map<String, Serializable> _extendedProperties;

}