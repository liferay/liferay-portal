/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.dto.v1_0;

import com.liferay.object.admin.rest.client.function.UnsafeSupplier;
import com.liferay.object.admin.rest.client.serdes.v1_0.ObjectViewSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class ObjectView implements Cloneable, Serializable {

	public static ObjectView toDTO(String json) {
		return ObjectViewSerDes.toDTO(json);
	}

	public Map<String, Map<String, String>> getActions() {
		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;
	}

	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		try {
			actions = actionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Map<String, String>> actions;

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public void setDateCreated(
		UnsafeSupplier<Date, Exception> dateCreatedUnsafeSupplier) {

		try {
			dateCreated = dateCreatedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateCreated;

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public void setDateModified(
		UnsafeSupplier<Date, Exception> dateModifiedUnsafeSupplier) {

		try {
			dateModified = dateModifiedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateModified;

	public Boolean getDefaultObjectView() {
		return defaultObjectView;
	}

	public void setDefaultObjectView(Boolean defaultObjectView) {
		this.defaultObjectView = defaultObjectView;
	}

	public void setDefaultObjectView(
		UnsafeSupplier<Boolean, Exception> defaultObjectViewUnsafeSupplier) {

		try {
			defaultObjectView = defaultObjectViewUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean defaultObjectView;

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

	public Map<String, String> getName() {
		return name;
	}

	public void setName(Map<String, String> name) {
		this.name = name;
	}

	public void setName(
		UnsafeSupplier<Map<String, String>, Exception> nameUnsafeSupplier) {

		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> name;

	public String getObjectDefinitionExternalReferenceCode() {
		return objectDefinitionExternalReferenceCode;
	}

	public void setObjectDefinitionExternalReferenceCode(
		String objectDefinitionExternalReferenceCode) {

		this.objectDefinitionExternalReferenceCode =
			objectDefinitionExternalReferenceCode;
	}

	public void setObjectDefinitionExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			objectDefinitionExternalReferenceCodeUnsafeSupplier) {

		try {
			objectDefinitionExternalReferenceCode =
				objectDefinitionExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String objectDefinitionExternalReferenceCode;

	public Long getObjectDefinitionId() {
		return objectDefinitionId;
	}

	public void setObjectDefinitionId(Long objectDefinitionId) {
		this.objectDefinitionId = objectDefinitionId;
	}

	public void setObjectDefinitionId(
		UnsafeSupplier<Long, Exception> objectDefinitionIdUnsafeSupplier) {

		try {
			objectDefinitionId = objectDefinitionIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long objectDefinitionId;

	public ObjectViewColumn[] getObjectViewColumns() {
		return objectViewColumns;
	}

	public void setObjectViewColumns(ObjectViewColumn[] objectViewColumns) {
		this.objectViewColumns = objectViewColumns;
	}

	public void setObjectViewColumns(
		UnsafeSupplier<ObjectViewColumn[], Exception>
			objectViewColumnsUnsafeSupplier) {

		try {
			objectViewColumns = objectViewColumnsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ObjectViewColumn[] objectViewColumns;

	public ObjectViewFilterColumn[] getObjectViewFilterColumns() {
		return objectViewFilterColumns;
	}

	public void setObjectViewFilterColumns(
		ObjectViewFilterColumn[] objectViewFilterColumns) {

		this.objectViewFilterColumns = objectViewFilterColumns;
	}

	public void setObjectViewFilterColumns(
		UnsafeSupplier<ObjectViewFilterColumn[], Exception>
			objectViewFilterColumnsUnsafeSupplier) {

		try {
			objectViewFilterColumns =
				objectViewFilterColumnsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ObjectViewFilterColumn[] objectViewFilterColumns;

	public ObjectViewSortColumn[] getObjectViewSortColumns() {
		return objectViewSortColumns;
	}

	public void setObjectViewSortColumns(
		ObjectViewSortColumn[] objectViewSortColumns) {

		this.objectViewSortColumns = objectViewSortColumns;
	}

	public void setObjectViewSortColumns(
		UnsafeSupplier<ObjectViewSortColumn[], Exception>
			objectViewSortColumnsUnsafeSupplier) {

		try {
			objectViewSortColumns = objectViewSortColumnsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ObjectViewSortColumn[] objectViewSortColumns;

	@Override
	public ObjectView clone() throws CloneNotSupportedException {
		return (ObjectView)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectView)) {
			return false;
		}

		ObjectView objectView = (ObjectView)object;

		return Objects.equals(toString(), objectView.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ObjectViewSerDes.toJSON(this);
	}

}