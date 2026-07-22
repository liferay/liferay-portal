/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.serdes.v1_0;

import com.liferay.bulk.rest.client.dto.v1_0.AddObjectToProjectBulkSelectionAction;
import com.liferay.bulk.rest.client.dto.v1_0.AssignStructureDefaultWorkflowBulkSelectionAction;
import com.liferay.bulk.rest.client.dto.v1_0.AssignToObjectBulkSelectionAction;
import com.liferay.bulk.rest.client.dto.v1_0.BulkAction;
import com.liferay.bulk.rest.client.dto.v1_0.BulkActionItem;
import com.liferay.bulk.rest.client.dto.v1_0.CopyObjectBulkSelectionAction;
import com.liferay.bulk.rest.client.dto.v1_0.DefaultPermissionObjectBulkSelectionAction;
import com.liferay.bulk.rest.client.dto.v1_0.DeleteObjectAssetVersionBulkSelectionAction;
import com.liferay.bulk.rest.client.dto.v1_0.DeleteObjectBulkSelectionAction;
import com.liferay.bulk.rest.client.dto.v1_0.DeleteObjectEntryBulkSelectionAction;
import com.liferay.bulk.rest.client.dto.v1_0.DueDateObjectBulkSelectionAction;
import com.liferay.bulk.rest.client.dto.v1_0.DuplicateObjectBulkSelectionAction;
import com.liferay.bulk.rest.client.dto.v1_0.EditObjectCategoriesBulkSelectionAction;
import com.liferay.bulk.rest.client.dto.v1_0.EditObjectTagsBulkSelectionAction;
import com.liferay.bulk.rest.client.dto.v1_0.ExpireObjectBulkSelectionAction;
import com.liferay.bulk.rest.client.dto.v1_0.MoveObjectBulkSelectionAction;
import com.liferay.bulk.rest.client.dto.v1_0.PermissionObjectBulkSelectionAction;
import com.liferay.bulk.rest.client.dto.v1_0.ResetPermissionObjectBulkSelectionAction;
import com.liferay.bulk.rest.client.dto.v1_0.RestoreObjectBulkSelectionAction;
import com.liferay.bulk.rest.client.dto.v1_0.StatusObjectBulkSelectionAction;
import com.liferay.bulk.rest.client.dto.v1_0.UpdateObjectValuesBulkSelectionAction;
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

			if (typeString.equals("AddObjectToProjectBulkSelectionAction")) {
				return AddObjectToProjectBulkSelectionActionSerDes.toJSON(
					(AddObjectToProjectBulkSelectionAction)bulkAction);
			}

			if (typeString.equals(
					"AssignStructureDefaultWorkflowBulkSelectionAction")) {

				return AssignStructureDefaultWorkflowBulkSelectionActionSerDes.
					toJSON(
						(AssignStructureDefaultWorkflowBulkSelectionAction)
							bulkAction);
			}

			if (typeString.equals("AssignToObjectBulkSelectionAction")) {
				return AssignToObjectBulkSelectionActionSerDes.toJSON(
					(AssignToObjectBulkSelectionAction)bulkAction);
			}

			if (typeString.equals("CopyObjectBulkSelectionAction")) {
				return CopyObjectBulkSelectionActionSerDes.toJSON(
					(CopyObjectBulkSelectionAction)bulkAction);
			}

			if (typeString.equals(
					"DefaultPermissionObjectBulkSelectionAction")) {

				return DefaultPermissionObjectBulkSelectionActionSerDes.toJSON(
					(DefaultPermissionObjectBulkSelectionAction)bulkAction);
			}

			if (typeString.equals(
					"DeleteObjectAssetVersionBulkSelectionAction")) {

				return DeleteObjectAssetVersionBulkSelectionActionSerDes.toJSON(
					(DeleteObjectAssetVersionBulkSelectionAction)bulkAction);
			}

			if (typeString.equals("DeleteObjectBulkSelectionAction")) {
				return DeleteObjectBulkSelectionActionSerDes.toJSON(
					(DeleteObjectBulkSelectionAction)bulkAction);
			}

			if (typeString.equals("DeleteObjectEntryBulkSelectionAction")) {
				return DeleteObjectEntryBulkSelectionActionSerDes.toJSON(
					(DeleteObjectEntryBulkSelectionAction)bulkAction);
			}

			if (typeString.equals("DueDateObjectBulkSelectionAction")) {
				return DueDateObjectBulkSelectionActionSerDes.toJSON(
					(DueDateObjectBulkSelectionAction)bulkAction);
			}

			if (typeString.equals("DuplicateObjectBulkSelectionAction")) {
				return DuplicateObjectBulkSelectionActionSerDes.toJSON(
					(DuplicateObjectBulkSelectionAction)bulkAction);
			}

			if (typeString.equals("EditObjectCategoriesBulkSelectionAction")) {
				return EditObjectCategoriesBulkSelectionActionSerDes.toJSON(
					(EditObjectCategoriesBulkSelectionAction)bulkAction);
			}

			if (typeString.equals("EditObjectTagsBulkSelectionAction")) {
				return EditObjectTagsBulkSelectionActionSerDes.toJSON(
					(EditObjectTagsBulkSelectionAction)bulkAction);
			}

			if (typeString.equals("ExpireObjectBulkSelectionAction")) {
				return ExpireObjectBulkSelectionActionSerDes.toJSON(
					(ExpireObjectBulkSelectionAction)bulkAction);
			}

			if (typeString.equals("MoveObjectBulkSelectionAction")) {
				return MoveObjectBulkSelectionActionSerDes.toJSON(
					(MoveObjectBulkSelectionAction)bulkAction);
			}

			if (typeString.equals("PermissionObjectBulkSelectionAction")) {
				return PermissionObjectBulkSelectionActionSerDes.toJSON(
					(PermissionObjectBulkSelectionAction)bulkAction);
			}

			if (typeString.equals("ResetPermissionObjectBulkSelectionAction")) {
				return ResetPermissionObjectBulkSelectionActionSerDes.toJSON(
					(ResetPermissionObjectBulkSelectionAction)bulkAction);
			}

			if (typeString.equals("RestoreObjectBulkSelectionAction")) {
				return RestoreObjectBulkSelectionActionSerDes.toJSON(
					(RestoreObjectBulkSelectionAction)bulkAction);
			}

			if (typeString.equals("StatusObjectBulkSelectionAction")) {
				return StatusObjectBulkSelectionActionSerDes.toJSON(
					(StatusObjectBulkSelectionAction)bulkAction);
			}

			if (typeString.equals("UpdateObjectValuesBulkSelectionAction")) {
				return UpdateObjectValuesBulkSelectionActionSerDes.toJSON(
					(UpdateObjectValuesBulkSelectionAction)bulkAction);
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
						"AddObjectToProjectBulkSelectionAction")) {

					return AddObjectToProjectBulkSelectionAction.toDTO(json);
				}

				if (typeString.equals(
						"AssignStructureDefaultWorkflowBulkSelectionAction")) {

					return AssignStructureDefaultWorkflowBulkSelectionAction.
						toDTO(json);
				}

				if (typeString.equals("AssignToObjectBulkSelectionAction")) {
					return AssignToObjectBulkSelectionAction.toDTO(json);
				}

				if (typeString.equals("CopyObjectBulkSelectionAction")) {
					return CopyObjectBulkSelectionAction.toDTO(json);
				}

				if (typeString.equals(
						"DefaultPermissionObjectBulkSelectionAction")) {

					return DefaultPermissionObjectBulkSelectionAction.toDTO(
						json);
				}

				if (typeString.equals(
						"DeleteObjectAssetVersionBulkSelectionAction")) {

					return DeleteObjectAssetVersionBulkSelectionAction.toDTO(
						json);
				}

				if (typeString.equals("DeleteObjectBulkSelectionAction")) {
					return DeleteObjectBulkSelectionAction.toDTO(json);
				}

				if (typeString.equals("DeleteObjectEntryBulkSelectionAction")) {
					return DeleteObjectEntryBulkSelectionAction.toDTO(json);
				}

				if (typeString.equals("DueDateObjectBulkSelectionAction")) {
					return DueDateObjectBulkSelectionAction.toDTO(json);
				}

				if (typeString.equals("DuplicateObjectBulkSelectionAction")) {
					return DuplicateObjectBulkSelectionAction.toDTO(json);
				}

				if (typeString.equals(
						"EditObjectCategoriesBulkSelectionAction")) {

					return EditObjectCategoriesBulkSelectionAction.toDTO(json);
				}

				if (typeString.equals("EditObjectTagsBulkSelectionAction")) {
					return EditObjectTagsBulkSelectionAction.toDTO(json);
				}

				if (typeString.equals("ExpireObjectBulkSelectionAction")) {
					return ExpireObjectBulkSelectionAction.toDTO(json);
				}

				if (typeString.equals("MoveObjectBulkSelectionAction")) {
					return MoveObjectBulkSelectionAction.toDTO(json);
				}

				if (typeString.equals("PermissionObjectBulkSelectionAction")) {
					return PermissionObjectBulkSelectionAction.toDTO(json);
				}

				if (typeString.equals(
						"ResetPermissionObjectBulkSelectionAction")) {

					return ResetPermissionObjectBulkSelectionAction.toDTO(json);
				}

				if (typeString.equals("RestoreObjectBulkSelectionAction")) {
					return RestoreObjectBulkSelectionAction.toDTO(json);
				}

				if (typeString.equals("StatusObjectBulkSelectionAction")) {
					return StatusObjectBulkSelectionAction.toDTO(json);
				}

				if (typeString.equals(
						"UpdateObjectValuesBulkSelectionAction")) {

					return UpdateObjectValuesBulkSelectionAction.toDTO(json);
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
// LIFERAY-REST-BUILDER-HASH:1370304813