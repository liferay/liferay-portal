/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.dto.v1_0;

import com.liferay.object.admin.rest.client.function.UnsafeSupplier;
import com.liferay.object.admin.rest.client.serdes.v1_0.ObjectViewSortColumnSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class ObjectViewSortColumn implements Cloneable, Serializable {

	public static ObjectViewSortColumn toDTO(String json) {
		return ObjectViewSortColumnSerDes.toDTO(json);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public String getObjectFieldName() {
		return objectFieldName;
	}

	public void setObjectFieldName(String objectFieldName) {
		this.objectFieldName = objectFieldName;
	}

	public void setObjectFieldName(
		UnsafeSupplier<String, Exception> objectFieldNameUnsafeSupplier) {

		try {
			objectFieldName = objectFieldNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String objectFieldName;

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public void setPriority(
		UnsafeSupplier<Integer, Exception> priorityUnsafeSupplier) {

		try {
			priority = priorityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer priority;

	public SortOrder getSortOrder() {
		return sortOrder;
	}

	public String getSortOrderAsString() {
		if (sortOrder == null) {
			return null;
		}

		return sortOrder.toString();
	}

	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

	public void setSortOrder(
		UnsafeSupplier<SortOrder, Exception> sortOrderUnsafeSupplier) {

		try {
			sortOrder = sortOrderUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected SortOrder sortOrder;

	@Override
	public ObjectViewSortColumn clone() throws CloneNotSupportedException {
		return (ObjectViewSortColumn)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectViewSortColumn)) {
			return false;
		}

		ObjectViewSortColumn objectViewSortColumn =
			(ObjectViewSortColumn)object;

		return Objects.equals(toString(), objectViewSortColumn.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ObjectViewSortColumnSerDes.toJSON(this);
	}

	public static enum SortOrder {

		ASC("asc"), DESC("desc");

		public static SortOrder create(String value) {
			for (SortOrder sortOrder : values()) {
				if (Objects.equals(sortOrder.getValue(), value) ||
					Objects.equals(sortOrder.name(), value)) {

					return sortOrder;
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

		private SortOrder(String value) {
			_value = value;
		}

		private final String _value;

	}

}