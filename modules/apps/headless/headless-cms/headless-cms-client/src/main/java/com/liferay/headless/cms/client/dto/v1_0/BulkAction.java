/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.client.dto.v1_0;

import com.liferay.headless.cms.client.function.UnsafeSupplier;
import com.liferay.headless.cms.client.serdes.v1_0.BulkActionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Crescenzo Rega
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

	public Boolean getSelectAll() {
		return selectAll;
	}

	public void setSelectAll(Boolean selectAll) {
		this.selectAll = selectAll;
	}

	public void setSelectAll(
		UnsafeSupplier<Boolean, Exception> selectAllUnsafeSupplier) {

		try {
			selectAll = selectAllUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean selectAll;

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

		DEFAULT_PERMISSION_BULK_ACTION("DefaultPermissionBulkAction"),
		DELETE_BULK_ACTION("DeleteBulkAction"),
		KEYWORD_BULK_ACTION("KeywordBulkAction"),
		MOVE_BULK_ACTION("MoveBulkAction"),
		PERMISSION_BULK_ACTION("PermissionBulkAction"),
		STATUS_BULK_ACTION("StatusBulkAction"),
		TAXONOMY_CATEGORY_BULK_ACTION("TaxonomyCategoryBulkAction");

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