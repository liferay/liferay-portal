/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
@GraphQLName("BulkAction")
@JsonFilter("Liferay.Vulcan")
@JsonSubTypes(
	{
		@JsonSubTypes.Type(
			name = "AddObjectToProjectBulkSelectionAction",
			value = AddObjectToProjectBulkSelectionAction.class
		),
		@JsonSubTypes.Type(
			name = "AssignStructureDefaultWorkflowBulkSelectionAction",
			value = AssignStructureDefaultWorkflowBulkSelectionAction.class
		),
		@JsonSubTypes.Type(
			name = "AssignToObjectBulkSelectionAction",
			value = AssignToObjectBulkSelectionAction.class
		),
		@JsonSubTypes.Type(
			name = "CopyObjectBulkSelectionAction",
			value = CopyObjectBulkSelectionAction.class
		),
		@JsonSubTypes.Type(
			name = "DefaultPermissionObjectBulkSelectionAction",
			value = DefaultPermissionObjectBulkSelectionAction.class
		),
		@JsonSubTypes.Type(
			name = "DeleteObjectAssetVersionBulkSelectionAction",
			value = DeleteObjectAssetVersionBulkSelectionAction.class
		),
		@JsonSubTypes.Type(
			name = "DeleteObjectBulkSelectionAction",
			value = DeleteObjectBulkSelectionAction.class
		),
		@JsonSubTypes.Type(
			name = "DeleteObjectEntryBulkSelectionAction",
			value = DeleteObjectEntryBulkSelectionAction.class
		),
		@JsonSubTypes.Type(
			name = "DueDateObjectBulkSelectionAction",
			value = DueDateObjectBulkSelectionAction.class
		),
		@JsonSubTypes.Type(
			name = "DuplicateObjectBulkSelectionAction",
			value = DuplicateObjectBulkSelectionAction.class
		),
		@JsonSubTypes.Type(
			name = "EditObjectCategoriesBulkSelectionAction",
			value = EditObjectCategoriesBulkSelectionAction.class
		),
		@JsonSubTypes.Type(
			name = "EditObjectTagsBulkSelectionAction",
			value = EditObjectTagsBulkSelectionAction.class
		),
		@JsonSubTypes.Type(
			name = "ExpireObjectBulkSelectionAction",
			value = ExpireObjectBulkSelectionAction.class
		),
		@JsonSubTypes.Type(
			name = "MoveObjectBulkSelectionAction",
			value = MoveObjectBulkSelectionAction.class
		),
		@JsonSubTypes.Type(
			name = "PermissionObjectBulkSelectionAction",
			value = PermissionObjectBulkSelectionAction.class
		),
		@JsonSubTypes.Type(
			name = "ResetPermissionObjectBulkSelectionAction",
			value = ResetPermissionObjectBulkSelectionAction.class
		),
		@JsonSubTypes.Type(
			name = "RestoreObjectBulkSelectionAction",
			value = RestoreObjectBulkSelectionAction.class
		),
		@JsonSubTypes.Type(
			name = "StatusObjectBulkSelectionAction",
			value = StatusObjectBulkSelectionAction.class
		),
		@JsonSubTypes.Type(
			name = "UpdateObjectValuesBulkSelectionAction",
			value = UpdateObjectValuesBulkSelectionAction.class
		)
	}
)
@JsonTypeInfo(
	include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type",
	use = JsonTypeInfo.Id.NAME, visible = true
)
@XmlRootElement(name = "BulkAction")
public abstract class BulkAction implements Serializable {

	public static BulkAction toDTO(String json) {
		return ObjectMapperUtil.readValue(BulkAction.class, json);
	}

	public static BulkAction unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(BulkAction.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public BulkActionItem[] getBulkActionItems() {
		if (_bulkActionItemsSupplier != null) {
			bulkActionItems = _bulkActionItemsSupplier.get();

			_bulkActionItemsSupplier = null;
		}

		return bulkActionItems;
	}

	public void setBulkActionItems(BulkActionItem[] bulkActionItems) {
		this.bulkActionItems = bulkActionItems;

		_bulkActionItemsSupplier = null;
	}

	@JsonIgnore
	public void setBulkActionItems(
		UnsafeSupplier<BulkActionItem[], Exception>
			bulkActionItemsUnsafeSupplier) {

		_bulkActionItemsSupplier = () -> {
			try {
				return bulkActionItemsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected BulkActionItem[] bulkActionItems;

	@JsonIgnore
	private Supplier<BulkActionItem[]> _bulkActionItemsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public SelectionScope getSelectionScope() {
		if (_selectionScopeSupplier != null) {
			selectionScope = _selectionScopeSupplier.get();

			_selectionScopeSupplier = null;
		}

		return selectionScope;
	}

	public void setSelectionScope(SelectionScope selectionScope) {
		this.selectionScope = selectionScope;

		_selectionScopeSupplier = null;
	}

	@JsonIgnore
	public void setSelectionScope(
		UnsafeSupplier<SelectionScope, Exception>
			selectionScopeUnsafeSupplier) {

		_selectionScopeSupplier = () -> {
			try {
				return selectionScopeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected SelectionScope selectionScope;

	@JsonIgnore
	private Supplier<SelectionScope> _selectionScopeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("type")
	@Valid
	public Type getType() {
		if (_typeSupplier != null) {
			type = _typeSupplier.get();

			_typeSupplier = null;
		}

		return type;
	}

	@JsonIgnore
	public String getTypeAsString() {
		Type type = getType();

		if (type == null) {
			return null;
		}

		return type.toString();
	}

	public void setType(Type type) {
		this.type = type;

		_typeSupplier = null;
	}

	@JsonIgnore
	public void setType(UnsafeSupplier<Type, Exception> typeUnsafeSupplier) {
		_typeSupplier = () -> {
			try {
				return typeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Type type;

	@JsonIgnore
	private Supplier<Type> _typeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof BulkAction)) {
			return false;
		}

		BulkAction bulkAction = (BulkAction)object;

		return Objects.equals(toString(), bulkAction.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		BulkActionItem[] bulkActionItems = getBulkActionItems();

		if (bulkActionItems != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"bulkActionItems\": ");

			sb.append("[");

			for (int i = 0; i < bulkActionItems.length; i++) {
				sb.append(String.valueOf(bulkActionItems[i]));

				if ((i + 1) < bulkActionItems.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		SelectionScope selectionScope = getSelectionScope();

		if (selectionScope != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selectionScope\": ");

			sb.append(String.valueOf(selectionScope));
		}

		Type type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(type);
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.bulk.rest.dto.v1_0.BulkAction",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Type")
	public static enum Type {

		ADD_OBJECT_TO_PROJECT_BULK_SELECTION_ACTION(
			"AddObjectToProjectBulkSelectionAction"),
		ASSIGN_STRUCTURE_DEFAULT_WORKFLOW_BULK_SELECTION_ACTION(
			"AssignStructureDefaultWorkflowBulkSelectionAction"),
		ASSIGN_TO_OBJECT_BULK_SELECTION_ACTION(
			"AssignToObjectBulkSelectionAction"),
		COPY_OBJECT_BULK_SELECTION_ACTION("CopyObjectBulkSelectionAction"),
		DEFAULT_PERMISSION_OBJECT_BULK_SELECTION_ACTION(
			"DefaultPermissionObjectBulkSelectionAction"),
		DELETE_OBJECT_ASSET_VERSION_BULK_SELECTION_ACTION(
			"DeleteObjectAssetVersionBulkSelectionAction"),
		DELETE_OBJECT_BULK_SELECTION_ACTION("DeleteObjectBulkSelectionAction"),
		DELETE_OBJECT_ENTRY_BULK_SELECTION_ACTION(
			"DeleteObjectEntryBulkSelectionAction"),
		DUE_DATE_OBJECT_BULK_SELECTION_ACTION(
			"DueDateObjectBulkSelectionAction"),
		DUPLICATE_OBJECT_BULK_SELECTION_ACTION(
			"DuplicateObjectBulkSelectionAction"),
		EXPIRE_OBJECT_BULK_SELECTION_ACTION("ExpireObjectBulkSelectionAction"),
		EDIT_OBJECT_TAGS_BULK_SELECTION_ACTION(
			"EditObjectTagsBulkSelectionAction"),
		MOVE_OBJECT_BULK_SELECTION_ACTION("MoveObjectBulkSelectionAction"),
		PERMISSION_OBJECT_BULK_SELECTION_ACTION(
			"PermissionObjectBulkSelectionAction"),
		RESET_PERMISSION_OBJECT_BULK_SELECTION_ACTION(
			"ResetPermissionObjectBulkSelectionAction"),
		RESTORE_OBJECT_BULK_SELECTION_ACTION(
			"RestoreObjectBulkSelectionAction"),
		STATUS_OBJECT_BULK_SELECTION_ACTION("StatusObjectBulkSelectionAction"),
		EDIT_OBJECT_CATEGORIES_BULK_SELECTION_ACTION(
			"EditObjectCategoriesBulkSelectionAction"),
		UPDATE_OBJECT_VALUES_BULK_SELECTION_ACTION(
			"UpdateObjectValuesBulkSelectionAction");

		@JsonCreator
		public static Type create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Type type : values()) {
				if (Objects.equals(type.getValue(), value)) {
					return type;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private Type(String value) {
			_value = value;
		}

		private final String _value;

	}

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
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
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:1038831538