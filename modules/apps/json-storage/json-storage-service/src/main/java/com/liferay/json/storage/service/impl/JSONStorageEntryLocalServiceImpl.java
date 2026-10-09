/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.json.storage.service.impl;

import com.liferay.json.storage.constants.JSONStorageEntryConstants;
import com.liferay.json.storage.model.JSONStorageEntry;
import com.liferay.json.storage.model.JSONStorageEntryTable;
import com.liferay.json.storage.service.base.JSONStorageEntryLocalServiceBaseImpl;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.sql.dsl.query.OrderByStep;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONDeserializer;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONSerializable;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(
	property = "model.class.name=com.liferay.json.storage.model.JSONStorageEntry",
	service = AopService.class
)
public class JSONStorageEntryLocalServiceImpl
	extends JSONStorageEntryLocalServiceBaseImpl {

	@Override
	public void addJSONStorageEntries(
		long companyId, long classNameId, long classPK, String json) {

		_updateJSONStorageEntries(
			companyId, classNameId, classPK, json, Collections.emptyList());
	}

	@Override
	public void deleteJSONStorageEntries(long classNameId, long classPK) {
		jsonStorageEntryPersistence.removeByCN_CPK(classNameId, classPK);
	}

	@Override
	public List<Long> getClassPKs(
		long companyId, long classNameId, int start, int end) {

		return jsonStorageEntryPersistence.dslQuery(
			DSLQueryFactoryUtil.selectDistinct(
				JSONStorageEntryTable.INSTANCE.classPK
			).from(
				JSONStorageEntryTable.INSTANCE
			).where(
				JSONStorageEntryTable.INSTANCE.companyId.eq(
					companyId
				).and(
					JSONStorageEntryTable.INSTANCE.classNameId.eq(classNameId)
				)
			).orderBy(
				JSONStorageEntryTable.INSTANCE.classPK.ascending()
			).limit(
				start, end
			));
	}

	@Override
	public List<Long> getClassPKs(
		long companyId, long classNameId, Object[] pathParts, Object value,
		int start, int end) {

		OrderByStep orderByStep = _getOrderByStep(
			DSLQueryFactoryUtil.selectDistinct(
				JSONStorageEntryTable.INSTANCE.classPK),
			companyId, classNameId, pathParts, value);

		return jsonStorageEntryPersistence.dslQuery(
			orderByStep.orderBy(
				JSONStorageEntryTable.INSTANCE.classPK.ascending()
			).limit(
				start, end
			));
	}

	@Override
	public int getClassPKsCount(long companyId, long classNameId) {
		return jsonStorageEntryPersistence.dslQueryCount(
			DSLQueryFactoryUtil.countDistinct(
				JSONStorageEntryTable.INSTANCE.classPK
			).from(
				JSONStorageEntryTable.INSTANCE
			).where(
				JSONStorageEntryTable.INSTANCE.companyId.eq(
					companyId
				).and(
					JSONStorageEntryTable.INSTANCE.classNameId.eq(classNameId)
				)
			));
	}

	@Override
	public int getClassPKsCount(
		long companyId, long classNameId, Object[] pathParts, Object value) {

		return jsonStorageEntryPersistence.dslQueryCount(
			_getOrderByStep(
				DSLQueryFactoryUtil.countDistinct(
					JSONStorageEntryTable.INSTANCE.classPK),
				companyId, classNameId, pathParts, value));
	}

	@Override
	public String getJSON(long classNameId, long classPK) {
		JSONSerializable jsonSerializable = _getJSONSerializable(
			jsonStorageEntryPersistence.findByCN_CPK(classNameId, classPK));

		if (jsonSerializable == null) {
			return null;
		}

		return jsonSerializable.toJSONString();
	}

	@Override
	public JSONArray getJSONArray(long classNameId, long classPK) {
		JSONSerializable jsonSerializable = _getJSONSerializable(
			jsonStorageEntryPersistence.findByCN_CPK(classNameId, classPK));

		if (jsonSerializable == null) {
			return null;
		}

		return (JSONArray)jsonSerializable;
	}

	@Override
	public JSONObject getJSONObject(long classNameId, long classPK) {
		JSONSerializable jsonSerializable = _getJSONSerializable(
			jsonStorageEntryPersistence.findByCN_CPK(classNameId, classPK));

		if (jsonSerializable == null) {
			return null;
		}

		return (JSONObject)jsonSerializable;
	}

	@Override
	public JSONSerializable getJSONSerializable(
		long classNameId, long classPK) {

		return _getJSONSerializable(
			jsonStorageEntryPersistence.findByCN_CPK(classNameId, classPK));
	}

	@Override
	public void updateJSONStorageEntries(
		long companyId, long classNameId, long classPK, String json) {

		_updateJSONStorageEntries(
			companyId, classNameId, classPK, json,
			jsonStorageEntryPersistence.findByCN_CPK(classNameId, classPK));
	}

	private JSONSerializable _getJSONSerializable(
		List<JSONStorageEntry> jsonStorageEntries) {

		Map<Long, JSONSerializable> map = new HashMap<>();

		for (JSONStorageEntry jsonStorageEntry : jsonStorageEntries) {
			JSONArray jsonArray = null;
			JSONObject jsonObject = null;

			if (jsonStorageEntry.getIndex() ==
					JSONStorageEntryConstants.INDEX_DEFAULT) {

				jsonObject = (JSONObject)map.computeIfAbsent(
					jsonStorageEntry.getParentJSONStorageEntryId(),
					key -> _jsonFactory.createJSONObject());
			}
			else {
				jsonArray = (JSONArray)map.computeIfAbsent(
					jsonStorageEntry.getParentJSONStorageEntryId(),
					key -> _jsonFactory.createJSONArray());
			}

			int type = jsonStorageEntry.getType();

			if (type == JSONStorageEntryConstants.TYPE_EMPTY) {
				continue;
			}

			Object value = null;

			if (type == JSONStorageEntryConstants.TYPE_ARRAY) {
				value = map.computeIfAbsent(
					jsonStorageEntry.getPrimaryKey(),
					key -> _jsonFactory.createJSONArray());
			}
			else if (type == JSONStorageEntryConstants.TYPE_OBJECT) {
				value = map.computeIfAbsent(
					jsonStorageEntry.getPrimaryKey(),
					key -> _jsonFactory.createJSONObject());
			}
			else if (type == JSONStorageEntryConstants.TYPE_VALUE_LONG) {
				value = jsonStorageEntry.getValueLong();
			}
			else if (type == JSONStorageEntryConstants.TYPE_VALUE_LONG_QUOTED) {
				value = String.valueOf(jsonStorageEntry.getValueLong());
			}
			else if (type == JSONStorageEntryConstants.TYPE_VALUE_STRING) {
				JSONDeserializer<?> jsonDeserializer =
					_jsonFactory.createJSONDeserializer();

				value = jsonDeserializer.deserialize(
					jsonStorageEntry.getValueString());
			}

			if (jsonArray == null) {
				jsonObject.put(jsonStorageEntry.getKey(), value);
			}
			else {
				jsonArray.put(value);
			}
		}

		return map.get(
			JSONStorageEntryConstants.PARENT_JSON_STORAGE_ENTRY_ID_DEFAULT);
	}

	private OrderByStep _getOrderByStep(
		FromStep fromStep, long companyId, long classNameId, Object[] pathParts,
		Object value) {

		JoinStep joinStep = fromStep.from(JSONStorageEntryTable.INSTANCE);

		Predicate predicate = JSONStorageEntryTable.INSTANCE.companyId.eq(
			companyId
		).and(
			JSONStorageEntryTable.INSTANCE.classNameId.eq(classNameId)
		);

		Long valueLong = null;

		if (value instanceof Integer || value instanceof Long) {
			Number number = (Number)value;

			valueLong = number.longValue();
		}
		else {
			valueLong = _parseLong(value);
		}

		if (valueLong != null) {
			predicate = predicate.and(
				JSONStorageEntryTable.INSTANCE.type.in(
					new Integer[] {
						JSONStorageEntryConstants.TYPE_VALUE_LONG,
						JSONStorageEntryConstants.TYPE_VALUE_LONG_QUOTED
					})
			).and(
				JSONStorageEntryTable.INSTANCE.valueLong.eq(valueLong)
			);
		}
		else if (value == null) {
			predicate = predicate.and(
				JSONStorageEntryTable.INSTANCE.type.eq(
					JSONStorageEntryConstants.TYPE_NULL));
		}
		else {
			JSONSerializer jsonSerializer = _jsonFactory.createJSONSerializer();

			predicate = predicate.and(
				JSONStorageEntryTable.INSTANCE.type.eq(
					JSONStorageEntryConstants.TYPE_VALUE_STRING)
			).and(
				DSLFunctionFactoryUtil.castClobText(
					JSONStorageEntryTable.INSTANCE.valueString
				).eq(
					jsonSerializer.serialize(value)
				)
			);
		}

		if (ArrayUtil.isEmpty(pathParts)) {
			return joinStep.where(predicate);
		}

		Object pathPart = pathParts[pathParts.length - 1];

		if (pathPart instanceof String) {
			predicate = predicate.and(
				JSONStorageEntryTable.INSTANCE.key.eq((String)pathPart));
		}
		else if (pathPart instanceof Integer) {
			predicate = predicate.and(
				JSONStorageEntryTable.INSTANCE.index.eq((Integer)pathPart));
		}

		JSONStorageEntryTable previousJSONStorageEntryTable =
			JSONStorageEntryTable.INSTANCE;

		Object previousPathPart = pathPart;

		for (int i = pathParts.length - 2; i >= 0; i--) {
			JSONStorageEntryTable aliasJSONStorageEntryTable =
				JSONStorageEntryTable.INSTANCE.as(
					"aliasJSONStorageEntryTable" + i);

			joinStep = joinStep.innerJoinON(
				aliasJSONStorageEntryTable,
				aliasJSONStorageEntryTable.jsonStorageEntryId.eq(
					previousJSONStorageEntryTable.parentJSONStorageEntryId));

			pathPart = pathParts[i];

			if (pathPart instanceof String) {
				String key = (String)pathPart;

				predicate = predicate.and(
					aliasJSONStorageEntryTable.key.eq(key));
			}
			else if (pathPart instanceof Integer) {
				Integer index = (Integer)pathPart;

				predicate = predicate.and(
					aliasJSONStorageEntryTable.index.eq(index));
			}

			if (previousPathPart instanceof String) {
				predicate = predicate.and(
					aliasJSONStorageEntryTable.type.eq(
						JSONStorageEntryConstants.TYPE_OBJECT));
			}
			else if (previousPathPart instanceof Integer) {
				predicate = predicate.and(
					aliasJSONStorageEntryTable.type.eq(
						JSONStorageEntryConstants.TYPE_ARRAY));
			}

			previousJSONStorageEntryTable = aliasJSONStorageEntryTable;

			previousPathPart = pathPart;
		}

		return joinStep.where(predicate);
	}

	private Long _parseLong(Object object) {
		if (!(object instanceof String)) {
			return null;
		}

		String value = (String)object;

		int length = value.length();

		if (length <= 0) {
			return null;
		}

		int pos = 0;
		long limit = -Long.MAX_VALUE;
		boolean negative = false;

		char c = value.charAt(0);

		if (c < CharPool.NUMBER_0) {
			if (c == CharPool.MINUS) {
				limit = Long.MIN_VALUE;
				negative = true;
			}
			else if (c != CharPool.PLUS) {
				return null;
			}

			if (length == 1) {
				return null;
			}

			pos++;
		}

		long smallLimit = limit / 10;

		long result = 0;

		while (pos < length) {
			if (result < smallLimit) {
				return null;
			}

			c = value.charAt(pos++);

			if ((c < CharPool.NUMBER_0) || (c > CharPool.NUMBER_9)) {
				return null;
			}

			int number = c - CharPool.NUMBER_0;

			result *= 10;

			if (result < (limit + number)) {
				return null;
			}

			result -= number;
		}

		if (negative) {
			return result;
		}

		return -result;
	}

	private void _removeChildJSONStorageEntries(
		Map<Long, List<JSONStorageEntry>> jsonStorageEntriesMap,
		JSONStorageEntry jsonStorageEntry) {

		List<JSONStorageEntry> jsonStorageEntries = jsonStorageEntriesMap.get(
			jsonStorageEntry.getPrimaryKey());

		if (jsonStorageEntries == null) {
			return;
		}

		Queue<JSONStorageEntry> queue = new LinkedList<>(jsonStorageEntries);

		while ((jsonStorageEntry = queue.poll()) != null) {
			jsonStorageEntries = jsonStorageEntriesMap.get(
				jsonStorageEntry.getPrimaryKey());

			if (jsonStorageEntries != null) {
				queue.addAll(jsonStorageEntries);
			}

			jsonStorageEntryPersistence.remove(jsonStorageEntry);
		}
	}

	private void _updateEmptyJSONStorageEntry(
		long companyId, long classNameId, long classPK,
		long parentJSONStorageEntryId, int index,
		List<JSONStorageEntry> jsonStorageEntries,
		Map<Long, List<JSONStorageEntry>> jsonStorageEntriesMap) {

		JSONStorageEntry jsonStorageEntry = null;

		if ((jsonStorageEntries != null) && !jsonStorageEntries.isEmpty()) {
			jsonStorageEntry = jsonStorageEntries.get(0);

			for (int i = 1; i < jsonStorageEntries.size(); i++) {
				jsonStorageEntryPersistence.remove(jsonStorageEntries.get(i));

				_removeChildJSONStorageEntries(
					jsonStorageEntriesMap, jsonStorageEntries.get(i));
			}
		}

		if (jsonStorageEntry == null) {
			jsonStorageEntry = jsonStorageEntryPersistence.create(
				counterLocalService.increment(
					JSONStorageEntry.class.getName()));

			jsonStorageEntry.setCompanyId(companyId);
			jsonStorageEntry.setClassNameId(classNameId);
			jsonStorageEntry.setClassPK(classPK);
			jsonStorageEntry.setParentJSONStorageEntryId(
				parentJSONStorageEntryId);
		}

		if (jsonStorageEntry.isNew() ||
			(index != jsonStorageEntry.getIndex()) ||
			!Objects.equals(StringPool.BLANK, jsonStorageEntry.getKey()) ||
			(jsonStorageEntry.getType() !=
				JSONStorageEntryConstants.TYPE_EMPTY) ||
			!Objects.equals(StringPool.BLANK, jsonStorageEntry.getValue())) {

			jsonStorageEntry.setIndex(index);
			jsonStorageEntry.setKey(StringPool.BLANK);
			jsonStorageEntry.setType(JSONStorageEntryConstants.TYPE_EMPTY);
			jsonStorageEntry.setValue(null);

			jsonStorageEntry = jsonStorageEntryPersistence.update(
				jsonStorageEntry);

			_removeChildJSONStorageEntries(
				jsonStorageEntriesMap, jsonStorageEntry);
		}
	}

	private void _updateJSONArray(
		long companyId, long classNameId, long classPK,
		Map<Long, List<JSONStorageEntry>> jsonStorageEntriesMap,
		List<?> jsonArrays, long parentJSONStorageEntryId) {

		List<JSONStorageEntry> jsonStorageEntries = jsonStorageEntriesMap.get(
			parentJSONStorageEntryId);

		int length = jsonArrays.size();

		for (int i = 0; i < length; i++) {
			Object value = jsonArrays.get(i);

			JSONStorageEntry jsonStorageEntry = null;

			if ((jsonStorageEntries != null) &&
				(i < jsonStorageEntries.size())) {

				jsonStorageEntry = jsonStorageEntries.get(i);
			}

			_updateJSONStorageEntry(
				companyId, classNameId, classPK, parentJSONStorageEntryId, i,
				StringPool.BLANK, value, jsonStorageEntriesMap,
				jsonStorageEntry);
		}

		if (length == 0) {
			_updateEmptyJSONStorageEntry(
				companyId, classNameId, classPK, parentJSONStorageEntryId, 0,
				jsonStorageEntries, jsonStorageEntriesMap);
		}
		else if (jsonStorageEntries != null) {
			for (int i = length; i < jsonStorageEntries.size(); i++) {
				JSONStorageEntry jsonStorageEntry = jsonStorageEntries.get(i);

				jsonStorageEntryPersistence.remove(jsonStorageEntry);

				_removeChildJSONStorageEntries(
					jsonStorageEntriesMap, jsonStorageEntry);
			}
		}
	}

	private void _updateJSONObject(
		long companyId, long classNameId, long classPK,
		Map<Long, List<JSONStorageEntry>> jsonStorageEntriesMap,
		Map<String, Object> objects, long parentJSONStorageEntryId) {

		List<JSONStorageEntry> jsonStorageEntries = jsonStorageEntriesMap.get(
			parentJSONStorageEntryId);

		Set<String> set = objects.keySet();

		for (Map.Entry<String, Object> entry : objects.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();

			JSONStorageEntry jsonStorageEntry = null;

			if (jsonStorageEntries != null) {
				for (JSONStorageEntry currentJSONStorageEntry :
						jsonStorageEntries) {

					if (key.equals(currentJSONStorageEntry.getKey())) {
						jsonStorageEntry = currentJSONStorageEntry;

						break;
					}
				}
			}

			_updateJSONStorageEntry(
				companyId, classNameId, classPK, parentJSONStorageEntryId,
				JSONStorageEntryConstants.INDEX_DEFAULT, key, value,
				jsonStorageEntriesMap, jsonStorageEntry);
		}

		if (set.isEmpty()) {
			_updateEmptyJSONStorageEntry(
				companyId, classNameId, classPK, parentJSONStorageEntryId,
				JSONStorageEntryConstants.INDEX_DEFAULT, jsonStorageEntries,
				jsonStorageEntriesMap);
		}
		else if (jsonStorageEntries != null) {
			for (JSONStorageEntry jsonStorageEntry : jsonStorageEntries) {
				if (set.contains(jsonStorageEntry.getKey())) {
					continue;
				}

				jsonStorageEntryPersistence.remove(jsonStorageEntry);

				_removeChildJSONStorageEntries(
					jsonStorageEntriesMap, jsonStorageEntry);
			}
		}
	}

	private void _updateJSONStorageEntries(
		long companyId, long classNameId, long classPK, String json,
		List<JSONStorageEntry> jsonStorageEntries) {

		Map<Long, List<JSONStorageEntry>> jsonStorageEntriesMap =
			new HashMap<>();

		for (JSONStorageEntry jsonStorageEntry : jsonStorageEntries) {
			List<JSONStorageEntry> newJSONStorageEntries =
				jsonStorageEntriesMap.computeIfAbsent(
					jsonStorageEntry.getParentJSONStorageEntryId(),
					key -> new ArrayList<>());

			newJSONStorageEntries.add(jsonStorageEntry);
		}

		JSONDeserializer<?> jsonDeserializer =
			_jsonFactory.createJSONDeserializer();

		Object object = jsonDeserializer.deserialize(json);

		if (object instanceof List) {
			_updateJSONArray(
				companyId, classNameId, classPK, jsonStorageEntriesMap,
				(List<?>)object,
				JSONStorageEntryConstants.PARENT_JSON_STORAGE_ENTRY_ID_DEFAULT);
		}
		else if (object instanceof Map) {
			_updateJSONObject(
				companyId, classNameId, classPK, jsonStorageEntriesMap,
				(Map<String, Object>)object,
				JSONStorageEntryConstants.PARENT_JSON_STORAGE_ENTRY_ID_DEFAULT);
		}
		else {
			throw new IllegalArgumentException("Invalid JSON: " + json);
		}
	}

	private void _updateJSONStorageEntry(
		long companyId, long classNameId, long classPK,
		long parentJSONStorageEntryId, int index, String key, Object value,
		Map<Long, List<JSONStorageEntry>> jsonStorageEntriesMap,
		JSONStorageEntry jsonStorageEntry) {

		if (jsonStorageEntry == null) {
			jsonStorageEntry = jsonStorageEntryPersistence.create(
				counterLocalService.increment(
					JSONStorageEntry.class.getName()));

			jsonStorageEntry.setCompanyId(companyId);
			jsonStorageEntry.setClassNameId(classNameId);
			jsonStorageEntry.setClassPK(classPK);
			jsonStorageEntry.setParentJSONStorageEntryId(
				parentJSONStorageEntryId);
			jsonStorageEntry.setIndex(index);
			jsonStorageEntry.setKey(key);
		}

		int type = JSONStorageEntryConstants.TYPE_NULL;

		if (value instanceof List) {
			type = JSONStorageEntryConstants.TYPE_ARRAY;

			_updateJSONArray(
				companyId, classNameId, classPK, jsonStorageEntriesMap,
				(List<?>)value, jsonStorageEntry.getPrimaryKey());
		}
		else if (value instanceof Map) {
			type = JSONStorageEntryConstants.TYPE_OBJECT;

			_updateJSONObject(
				companyId, classNameId, classPK, jsonStorageEntriesMap,
				(Map<String, Object>)value, jsonStorageEntry.getPrimaryKey());
		}
		else if (value != null) {
			Long valueLong = null;

			if (value instanceof Integer || value instanceof Long) {
				type = JSONStorageEntryConstants.TYPE_VALUE_LONG;

				Number number = (Number)value;

				valueLong = number.longValue();
			}
			else {
				type = JSONStorageEntryConstants.TYPE_VALUE_LONG_QUOTED;

				valueLong = _parseLong(value);
			}

			if (valueLong != null) {
				value = valueLong;
			}
			else if (value != null) {
				type = JSONStorageEntryConstants.TYPE_VALUE_STRING;

				JSONSerializer jsonSerializer =
					_jsonFactory.createJSONSerializer();

				value = jsonSerializer.serialize(value);
			}
		}

		if (jsonStorageEntry.isNew() ||
			(index != jsonStorageEntry.getIndex()) ||
			!Objects.equals(key, jsonStorageEntry.getKey()) ||
			(type != jsonStorageEntry.getType()) ||
			(((type == JSONStorageEntryConstants.TYPE_VALUE_LONG) ||
			  (type == JSONStorageEntryConstants.TYPE_VALUE_LONG_QUOTED) ||
			  (type == JSONStorageEntryConstants.TYPE_VALUE_STRING)) &&
			 !Objects.equals(value, jsonStorageEntry.getValue()))) {

			jsonStorageEntry.setIndex(index);
			jsonStorageEntry.setKey(key);
			jsonStorageEntry.setType(type);
			jsonStorageEntry.setValue(value);

			jsonStorageEntry = jsonStorageEntryPersistence.update(
				jsonStorageEntry);

			if ((type != JSONStorageEntryConstants.TYPE_ARRAY) &&
				(type != JSONStorageEntryConstants.TYPE_OBJECT)) {

				_removeChildJSONStorageEntries(
					jsonStorageEntriesMap, jsonStorageEntry);
			}
		}
	}

	@Reference
	private JSONFactory _jsonFactory;

}