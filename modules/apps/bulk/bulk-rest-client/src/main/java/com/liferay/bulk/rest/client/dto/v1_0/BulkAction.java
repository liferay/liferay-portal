/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.dto.v1_0;

import com.liferay.bulk.rest.client.function.UnsafeSupplier;
import com.liferay.bulk.rest.client.serdes.v1_0.BulkActionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public abstract class BulkAction implements Cloneable, Serializable {

	public static BulkAction toDTO(String json) {
		return BulkActionSerDes.toDTO(json);
	}

	public BulkActionItem[] getBulkActionItems() {
		return bulkActionItems;
	}

	public void setBulkActionItems(BulkActionItem[] bulkActionItems) {
		this.bulkActionItems = bulkActionItems;
	}

	public void setBulkActionItems(
		UnsafeSupplier<BulkActionItem[], Exception>
			bulkActionItemsUnsafeSupplier) {

		try {
			bulkActionItems = bulkActionItemsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BulkActionItem[] bulkActionItems;

	public SelectionScope getSelectionScope() {
		return selectionScope;
	}

	public void setSelectionScope(SelectionScope selectionScope) {
		this.selectionScope = selectionScope;
	}

	public void setSelectionScope(
		UnsafeSupplier<SelectionScope, Exception>
			selectionScopeUnsafeSupplier) {

		try {
			selectionScope = selectionScopeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected SelectionScope selectionScope;

	public Type getType() {
		return type;
	}

	public String getTypeAsString() {
		if (type == null) {
			return null;
		}

		return type.toString();
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setType(UnsafeSupplier<Type, Exception> typeUnsafeSupplier) {
		try {
			type = typeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Type type;

	@Override
	public BulkAction clone() throws CloneNotSupportedException {
		return (BulkAction)super.clone();
	}

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
		return BulkActionSerDes.toJSON(this);
	}

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

		public static Type create(String value) {
			for (Type type : values()) {
				if (Objects.equals(type.getValue(), value) ||
					Objects.equals(type.name(), value)) {

					return type;
				}
			}

			return null;
		}

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

}
// LIFERAY-REST-BUILDER-HASH:-1056758656