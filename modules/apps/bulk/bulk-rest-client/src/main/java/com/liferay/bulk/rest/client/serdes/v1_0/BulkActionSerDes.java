/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.serdes.v1_0;

import com.liferay.bulk.rest.client.dto.v1_0.AssignStructureDefaultWorkflowBulkAction;
import com.liferay.bulk.rest.client.dto.v1_0.AssignToBulkAction;
import com.liferay.bulk.rest.client.dto.v1_0.BulkAction;
import com.liferay.bulk.rest.client.dto.v1_0.BulkActionItem;
import com.liferay.bulk.rest.client.dto.v1_0.CopyBulkAction;
import com.liferay.bulk.rest.client.dto.v1_0.DefaultPermissionBulkAction;
import com.liferay.bulk.rest.client.dto.v1_0.DeleteAssetVersionBulkAction;
import com.liferay.bulk.rest.client.dto.v1_0.DeleteBulkAction;
import com.liferay.bulk.rest.client.dto.v1_0.DueDateBulkAction;
import com.liferay.bulk.rest.client.dto.v1_0.ExpireBulkAction;
import com.liferay.bulk.rest.client.dto.v1_0.KeywordBulkAction;
import com.liferay.bulk.rest.client.dto.v1_0.MoveBulkAction;
import com.liferay.bulk.rest.client.dto.v1_0.PermissionBulkAction;
import com.liferay.bulk.rest.client.dto.v1_0.ResetPermissionBulkAction;
import com.liferay.bulk.rest.client.dto.v1_0.StatusBulkAction;
import com.liferay.bulk.rest.client.dto.v1_0.TaxonomyCategoryBulkAction;
import com.liferay.bulk.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class BulkActionSerDes {

	public static BulkAction toDTO(String json) {
		BulkActionJSONParser bulkActionJSONParser = new BulkActionJSONParser();

		return bulkActionJSONParser.parseToDTO(json);
	}

	public static BulkAction[] toDTOs(String json) {
		BulkActionJSONParser bulkActionJSONParser = new BulkActionJSONParser();

		return bulkActionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(BulkAction bulkAction) {
		if (bulkAction == null) {
			return "null";
		}

		BulkAction.Type type = bulkAction.getType();

		if (type != null) {
			String typeString = type.toString();

			if (typeString.equals("AssignStructureDefaultWorkflowBulkAction")) {
				return AssignStructureDefaultWorkflowBulkActionSerDes.toJSON(
					(AssignStructureDefaultWorkflowBulkAction)bulkAction);
			}

			if (typeString.equals("AssignToBulkAction")) {
				return AssignToBulkActionSerDes.toJSON(
					(AssignToBulkAction)bulkAction);
			}

			if (typeString.equals("CopyBulkAction")) {
				return CopyBulkActionSerDes.toJSON((CopyBulkAction)bulkAction);
			}

			if (typeString.equals("DefaultPermissionBulkAction")) {
				return DefaultPermissionBulkActionSerDes.toJSON(
					(DefaultPermissionBulkAction)bulkAction);
			}

			if (typeString.equals("DeleteAssetVersionBulkAction")) {
				return DeleteAssetVersionBulkActionSerDes.toJSON(
					(DeleteAssetVersionBulkAction)bulkAction);
			}

			if (typeString.equals("DeleteBulkAction")) {
				return DeleteBulkActionSerDes.toJSON(
					(DeleteBulkAction)bulkAction);
			}

			if (typeString.equals("DeleteObjectEntryBulkAction")) {
				return DeleteBulkActionSerDes.toJSON(
					(DeleteBulkAction)bulkAction);
			}

			if (typeString.equals("DueDateBulkAction")) {
				return DueDateBulkActionSerDes.toJSON(
					(DueDateBulkAction)bulkAction);
			}

			if (typeString.equals("ExpireBulkAction")) {
				return ExpireBulkActionSerDes.toJSON(
					(ExpireBulkAction)bulkAction);
			}

			if (typeString.equals("KeywordBulkAction")) {
				return KeywordBulkActionSerDes.toJSON(
					(KeywordBulkAction)bulkAction);
			}

			if (typeString.equals("MoveBulkAction")) {
				return MoveBulkActionSerDes.toJSON((MoveBulkAction)bulkAction);
			}

			if (typeString.equals("PermissionBulkAction")) {
				return PermissionBulkActionSerDes.toJSON(
					(PermissionBulkAction)bulkAction);
			}

			if (typeString.equals("ResetPermissionBulkAction")) {
				return ResetPermissionBulkActionSerDes.toJSON(
					(ResetPermissionBulkAction)bulkAction);
			}

			if (typeString.equals("StatusBulkAction")) {
				return StatusBulkActionSerDes.toJSON(
					(StatusBulkAction)bulkAction);
			}

			if (typeString.equals("TaxonomyCategoryBulkAction")) {
				return TaxonomyCategoryBulkActionSerDes.toJSON(
					(TaxonomyCategoryBulkAction)bulkAction);
			}

			throw new IllegalArgumentException("Unknown type " + typeString);
		}
		else {
			throw new IllegalArgumentException("Missing type parameter");
		}
	}

	public static Map<String, Object> toMap(String json) {
		BulkActionJSONParser bulkActionJSONParser = new BulkActionJSONParser();

		return bulkActionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(BulkAction bulkAction) {
		if (bulkAction == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (bulkAction.getBulkActionItems() == null) {
			map.put("bulkActionItems", null);
		}
		else {
			map.put(
				"bulkActionItems",
				String.valueOf(bulkAction.getBulkActionItems()));
		}

		if (bulkAction.getSelectionScope() == null) {
			map.put("selectionScope", null);
		}
		else {
			map.put(
				"selectionScope",
				String.valueOf(bulkAction.getSelectionScope()));
		}

		if (bulkAction.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(bulkAction.getType()));
		}

		return map;
	}

	public static class BulkActionJSONParser
		extends BaseJSONParser<BulkAction> {

		@Override
		protected BulkAction createDTO() {
			return null;
		}

		@Override
		protected BulkAction[] createDTOArray(int size) {
			return new BulkAction[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "bulkActionItems")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "selectionScope")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		public BulkAction parseToDTO(String json) {
			Map<String, Object> jsonMap = parseToMap(json);

			Object type = jsonMap.get("type");

			if (type != null) {
				String typeString = type.toString();

				if (typeString.equals(
						"AssignStructureDefaultWorkflowBulkAction")) {

					return AssignStructureDefaultWorkflowBulkAction.toDTO(json);
				}

				if (typeString.equals("AssignToBulkAction")) {
					return AssignToBulkAction.toDTO(json);
				}

				if (typeString.equals("CopyBulkAction")) {
					return CopyBulkAction.toDTO(json);
				}

				if (typeString.equals("DefaultPermissionBulkAction")) {
					return DefaultPermissionBulkAction.toDTO(json);
				}

				if (typeString.equals("DeleteAssetVersionBulkAction")) {
					return DeleteAssetVersionBulkAction.toDTO(json);
				}

				if (typeString.equals("DeleteBulkAction")) {
					return DeleteBulkAction.toDTO(json);
				}

				if (typeString.equals("DeleteObjectEntryBulkAction")) {
					return DeleteBulkAction.toDTO(json);
				}

				if (typeString.equals("DueDateBulkAction")) {
					return DueDateBulkAction.toDTO(json);
				}

				if (typeString.equals("ExpireBulkAction")) {
					return ExpireBulkAction.toDTO(json);
				}

				if (typeString.equals("KeywordBulkAction")) {
					return KeywordBulkAction.toDTO(json);
				}

				if (typeString.equals("MoveBulkAction")) {
					return MoveBulkAction.toDTO(json);
				}

				if (typeString.equals("PermissionBulkAction")) {
					return PermissionBulkAction.toDTO(json);
				}

				if (typeString.equals("ResetPermissionBulkAction")) {
					return ResetPermissionBulkAction.toDTO(json);
				}

				if (typeString.equals("StatusBulkAction")) {
					return StatusBulkAction.toDTO(json);
				}

				if (typeString.equals("TaxonomyCategoryBulkAction")) {
					return TaxonomyCategoryBulkAction.toDTO(json);
				}

				throw new IllegalArgumentException(
					"Unknown type " + typeString);
			}
			else {
				throw new IllegalArgumentException("Missing type parameter");
			}
		}

		@Override
		protected void setField(
			BulkAction bulkAction, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "bulkActionItems")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					BulkActionItem[] bulkActionItemsArray =
						new BulkActionItem[jsonParserFieldValues.length];

					for (int i = 0; i < bulkActionItemsArray.length; i++) {
						bulkActionItemsArray[i] = BulkActionItemSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					bulkAction.setBulkActionItems(bulkActionItemsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "selectionScope")) {
				if (jsonParserFieldValue != null) {
					bulkAction.setSelectionScope(
						SelectionScopeSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					bulkAction.setType(
						BulkAction.Type.create((String)jsonParserFieldValue));
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