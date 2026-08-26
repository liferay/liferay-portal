/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model.bag;

import com.liferay.object.model.ObjectField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Shuyang Zhou
 */
public class ObjectFieldBag {

	public ObjectFieldBag(
		boolean modifiableAndSystem, List<ObjectField> objectFields) {

		_objectFields = objectFields;

		for (ObjectField objectField : _objectFields) {
			_idObjectFieldsMap.put(objectField.getObjectFieldId(), objectField);
			_nameObjectFieldsMap.put(objectField.getName(), objectField);

			if (!objectField.isIndexed()) {
				continue;
			}

			_indexedObjectFields.add(objectField);

			if (modifiableAndSystem) {
				if (!objectField.isMetadata()) {
					_nestedIndexedObjectFields.add(objectField);
				}
			}
			else if (!objectField.isSystem()) {
				_nestedIndexedObjectFields.add(objectField);
			}
		}
	}

	public List<ObjectField> getIndexedObjectFields() {
		return _indexedObjectFields;
	}

	public List<ObjectField> getNestedIndexedObjectFields() {
		return _nestedIndexedObjectFields;
	}

	public ObjectField getObjectField(long objectFieldId) {
		return _idObjectFieldsMap.get(objectFieldId);
	}

	public ObjectField getObjectField(String name) {
		return _nameObjectFieldsMap.get(name);
	}

	private final Map<Long, ObjectField> _idObjectFieldsMap = new HashMap<>();
	private final List<ObjectField> _indexedObjectFields = new ArrayList<>();
	private final Map<String, ObjectField> _nameObjectFieldsMap =
		new HashMap<>();
	private final List<ObjectField> _nestedIndexedObjectFields =
		new ArrayList<>();
	private final List<ObjectField> _objectFields;

}