/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.rest.client.serdes.v1_0;

import com.liferay.batch.planner.rest.client.dto.v1_0.Mapping;
import com.liferay.batch.planner.rest.client.dto.v1_0.Plan;
import com.liferay.batch.planner.rest.client.dto.v1_0.Policy;
import com.liferay.batch.planner.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Matija Petanjek
 * @generated
 */
@Generated("")
public class PlanSerDes {

	public static Plan toDTO(String json) {
		PlanJSONParser planJSONParser = new PlanJSONParser();

		return planJSONParser.parseToDTO(json);
	}

	public static Plan[] toDTOs(String json) {
		PlanJSONParser planJSONParser = new PlanJSONParser();

		return planJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Plan plan) {
		if (plan == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (plan.getActive() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(plan.getActive());
		}

		if (plan.getExport() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"export\": ");

			sb.append(plan.getExport());
		}

		if (plan.getExternalType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalType\": ");

			sb.append("\"");

			sb.append(_escape(plan.getExternalType()));

			sb.append("\"");
		}

		if (plan.getExternalURL() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalURL\": ");

			sb.append("\"");

			sb.append(_escape(plan.getExternalURL()));

			sb.append("\"");
		}

		if (plan.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(plan.getId());
		}

		if (plan.getInternalClassName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"internalClassName\": ");

			sb.append("\"");

			sb.append(_escape(plan.getInternalClassName()));

			sb.append("\"");
		}

		if (plan.getInternalClassNameKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"internalClassNameKey\": ");

			sb.append("\"");

			sb.append(_escape(plan.getInternalClassNameKey()));

			sb.append("\"");
		}

		if (plan.getMappings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"mappings\": ");

			sb.append("[");

			for (int i = 0; i < plan.getMappings().length; i++) {
				sb.append(String.valueOf(plan.getMappings()[i]));

				if ((i + 1) < plan.getMappings().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (plan.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(plan.getName()));

			sb.append("\"");
		}

		if (plan.getPolicies() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"policies\": ");

			sb.append("[");

			for (int i = 0; i < plan.getPolicies().length; i++) {
				sb.append(String.valueOf(plan.getPolicies()[i]));

				if ((i + 1) < plan.getPolicies().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (plan.getSize() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"size\": ");

			sb.append(plan.getSize());
		}

		if (plan.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(plan.getStatus());
		}

		if (plan.getTaskItemDelegateName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taskItemDelegateName\": ");

			sb.append("\"");

			sb.append(_escape(plan.getTaskItemDelegateName()));

			sb.append("\"");
		}

		if (plan.getTemplate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"template\": ");

			sb.append(plan.getTemplate());
		}

		if (plan.getTotal() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"total\": ");

			sb.append(plan.getTotal());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PlanJSONParser planJSONParser = new PlanJSONParser();

		return planJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Plan plan) {
		if (plan == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (plan.getActive() == null) {
			map.put("active", null);
		}
		else {
			map.put("active", String.valueOf(plan.getActive()));
		}

		if (plan.getExport() == null) {
			map.put("export", null);
		}
		else {
			map.put("export", String.valueOf(plan.getExport()));
		}

		if (plan.getExternalType() == null) {
			map.put("externalType", null);
		}
		else {
			map.put("externalType", String.valueOf(plan.getExternalType()));
		}

		if (plan.getExternalURL() == null) {
			map.put("externalURL", null);
		}
		else {
			map.put("externalURL", String.valueOf(plan.getExternalURL()));
		}

		if (plan.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(plan.getId()));
		}

		if (plan.getInternalClassName() == null) {
			map.put("internalClassName", null);
		}
		else {
			map.put(
				"internalClassName",
				String.valueOf(plan.getInternalClassName()));
		}

		if (plan.getInternalClassNameKey() == null) {
			map.put("internalClassNameKey", null);
		}
		else {
			map.put(
				"internalClassNameKey",
				String.valueOf(plan.getInternalClassNameKey()));
		}

		if (plan.getMappings() == null) {
			map.put("mappings", null);
		}
		else {
			map.put("mappings", String.valueOf(plan.getMappings()));
		}

		if (plan.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(plan.getName()));
		}

		if (plan.getPolicies() == null) {
			map.put("policies", null);
		}
		else {
			map.put("policies", String.valueOf(plan.getPolicies()));
		}

		if (plan.getSize() == null) {
			map.put("size", null);
		}
		else {
			map.put("size", String.valueOf(plan.getSize()));
		}

		if (plan.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(plan.getStatus()));
		}

		if (plan.getTaskItemDelegateName() == null) {
			map.put("taskItemDelegateName", null);
		}
		else {
			map.put(
				"taskItemDelegateName",
				String.valueOf(plan.getTaskItemDelegateName()));
		}

		if (plan.getTemplate() == null) {
			map.put("template", null);
		}
		else {
			map.put("template", String.valueOf(plan.getTemplate()));
		}

		if (plan.getTotal() == null) {
			map.put("total", null);
		}
		else {
			map.put("total", String.valueOf(plan.getTotal()));
		}

		return map;
	}

	public static class PlanJSONParser extends BaseJSONParser<Plan> {

		@Override
		protected Plan createDTO() {
			return new Plan();
		}

		@Override
		protected Plan[] createDTOArray(int size) {
			return new Plan[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "active")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "export")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "externalType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "externalURL")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "internalClassName")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "internalClassNameKey")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "mappings")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "policies")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "size")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "taskItemDelegateName")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "template")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "total")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Plan plan, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "active")) {
				if (jsonParserFieldValue != null) {
					plan.setActive((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "export")) {
				if (jsonParserFieldValue != null) {
					plan.setExport((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "externalType")) {
				if (jsonParserFieldValue != null) {
					plan.setExternalType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "externalURL")) {
				if (jsonParserFieldValue != null) {
					plan.setExternalURL((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					plan.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "internalClassName")) {
				if (jsonParserFieldValue != null) {
					plan.setInternalClassName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "internalClassNameKey")) {

				if (jsonParserFieldValue != null) {
					plan.setInternalClassNameKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "mappings")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Mapping[] mappingsArray =
						new Mapping[jsonParserFieldValues.length];

					for (int i = 0; i < mappingsArray.length; i++) {
						mappingsArray[i] = MappingSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					plan.setMappings(mappingsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					plan.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "policies")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Policy[] policiesArray =
						new Policy[jsonParserFieldValues.length];

					for (int i = 0; i < policiesArray.length; i++) {
						policiesArray[i] = PolicySerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					plan.setPolicies(policiesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "size")) {
				if (jsonParserFieldValue != null) {
					plan.setSize(Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					plan.setStatus(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "taskItemDelegateName")) {

				if (jsonParserFieldValue != null) {
					plan.setTaskItemDelegateName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "template")) {
				if (jsonParserFieldValue != null) {
					plan.setTemplate((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "total")) {
				if (jsonParserFieldValue != null) {
					plan.setTotal(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
		}

	}

	private static String _escape(Object object) {
		String string = String.valueOf(object);

		for (String[] strings : BaseJSONParser.JSON_ESCAPE_STRINGS) {
			string = string.replace(strings[0], strings[1]);
		}

		return string;
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(entry.getKey());
			sb.append("\": ");

			Object value = entry.getValue();

			sb.append(_toJSON(value));

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static String _toJSON(Object value) {
		if (value == null) {
			return "null";
		}

		if (value instanceof Map) {
			return _toJSON((Map)value);
		}

		Class<?> clazz = value.getClass();

		if (clazz.isArray()) {
			StringBuilder sb = new StringBuilder("[");

			Object[] values = (Object[])value;

			for (int i = 0; i < values.length; i++) {
				sb.append(_toJSON(values[i]));

				if ((i + 1) < values.length) {
					sb.append(", ");
				}
			}

			sb.append("]");

			return sb.toString();
		}

		if (value instanceof String) {
			return "\"" + _escape(value) + "\"";
		}

		return String.valueOf(value);
	}

}