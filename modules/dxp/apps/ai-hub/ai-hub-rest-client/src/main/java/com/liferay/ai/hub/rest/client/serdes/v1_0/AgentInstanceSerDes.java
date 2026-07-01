/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.client.serdes.v1_0;

import com.liferay.ai.hub.rest.client.dto.v1_0.AgentInstance;
import com.liferay.ai.hub.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Feliphe Marinho
 * @generated
 */
@Generated("")
public class AgentInstanceSerDes {

	public static AgentInstance toDTO(String json) {
		AgentInstanceJSONParser agentInstanceJSONParser =
			new AgentInstanceJSONParser();

		return agentInstanceJSONParser.parseToDTO(json);
	}

	public static AgentInstance[] toDTOs(String json) {
		AgentInstanceJSONParser agentInstanceJSONParser =
			new AgentInstanceJSONParser();

		return agentInstanceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AgentInstance agentInstance) {
		if (agentInstance == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (agentInstance.getAgentDefinitionExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"agentDefinitionExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					agentInstance.getAgentDefinitionExternalReferenceCode()));

			sb.append("\"");
		}

		if (agentInstance.getAsynchronous() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"asynchronous\": ");

			sb.append(agentInstance.getAsynchronous());
		}

		if (agentInstance.getContext() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"context\": ");

			sb.append(_toJSON(agentInstance.getContext()));
		}

		if (agentInstance.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(agentInstance.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (agentInstance.getInstructionDefinitionScope() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"instructionDefinitionScope\": ");

			sb.append("\"");
			sb.append(agentInstance.getInstructionDefinitionScope());
			sb.append("\"");
		}

		if (agentInstance.getOutput() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"output\": ");

			sb.append("\"");

			sb.append(_escape(agentInstance.getOutput()));

			sb.append("\"");
		}

		if (agentInstance.getSseEventSinkKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sseEventSinkKey\": ");

			sb.append("\"");

			sb.append(_escape(agentInstance.getSseEventSinkKey()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AgentInstanceJSONParser agentInstanceJSONParser =
			new AgentInstanceJSONParser();

		return agentInstanceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(AgentInstance agentInstance) {
		if (agentInstance == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (agentInstance.getAgentDefinitionExternalReferenceCode() == null) {
			map.put("agentDefinitionExternalReferenceCode", null);
		}
		else {
			map.put(
				"agentDefinitionExternalReferenceCode",
				String.valueOf(
					agentInstance.getAgentDefinitionExternalReferenceCode()));
		}

		if (agentInstance.getAsynchronous() == null) {
			map.put("asynchronous", null);
		}
		else {
			map.put(
				"asynchronous",
				String.valueOf(agentInstance.getAsynchronous()));
		}

		if (agentInstance.getContext() == null) {
			map.put("context", null);
		}
		else {
			map.put("context", String.valueOf(agentInstance.getContext()));
		}

		if (agentInstance.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(agentInstance.getExternalReferenceCode()));
		}

		if (agentInstance.getInstructionDefinitionScope() == null) {
			map.put("instructionDefinitionScope", null);
		}
		else {
			map.put(
				"instructionDefinitionScope",
				String.valueOf(agentInstance.getInstructionDefinitionScope()));
		}

		if (agentInstance.getOutput() == null) {
			map.put("output", null);
		}
		else {
			map.put("output", String.valueOf(agentInstance.getOutput()));
		}

		if (agentInstance.getSseEventSinkKey() == null) {
			map.put("sseEventSinkKey", null);
		}
		else {
			map.put(
				"sseEventSinkKey",
				String.valueOf(agentInstance.getSseEventSinkKey()));
		}

		return map;
	}

	public static class AgentInstanceJSONParser
		extends BaseJSONParser<AgentInstance> {

		@Override
		protected AgentInstance createDTO() {
			return new AgentInstance();
		}

		@Override
		protected AgentInstance[] createDTOArray(int size) {
			return new AgentInstance[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName,
					"agentDefinitionExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "asynchronous")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "context")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "instructionDefinitionScope")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "output")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sseEventSinkKey")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AgentInstance agentInstance, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName,
					"agentDefinitionExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					agentInstance.setAgentDefinitionExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "asynchronous")) {
				if (jsonParserFieldValue != null) {
					agentInstance.setAsynchronous(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "context")) {
				if (jsonParserFieldValue != null) {
					agentInstance.setContext(
						(Map<String, ?>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					agentInstance.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "instructionDefinitionScope")) {

				if (jsonParserFieldValue != null) {
					agentInstance.setInstructionDefinitionScope(
						AgentInstance.InstructionDefinitionScope.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "output")) {
				if (jsonParserFieldValue != null) {
					agentInstance.setOutput((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sseEventSinkKey")) {
				if (jsonParserFieldValue != null) {
					agentInstance.setSseEventSinkKey(
						(String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:1937200539