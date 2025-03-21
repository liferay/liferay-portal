/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.dto.v1_0;

import com.liferay.object.admin.rest.client.function.UnsafeSupplier;
import com.liferay.object.admin.rest.client.serdes.v1_0.ObjectLayoutTabSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class ObjectLayoutTab implements Cloneable, Serializable {

	public static ObjectLayoutTab toDTO(String json) {
		return ObjectLayoutTabSerDes.toDTO(json);
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

	public ObjectLayoutBox[] getObjectLayoutBoxes() {
		return objectLayoutBoxes;
	}

	public void setObjectLayoutBoxes(ObjectLayoutBox[] objectLayoutBoxes) {
		this.objectLayoutBoxes = objectLayoutBoxes;
	}

	public void setObjectLayoutBoxes(
		UnsafeSupplier<ObjectLayoutBox[], Exception>
			objectLayoutBoxesUnsafeSupplier) {

		try {
			objectLayoutBoxes = objectLayoutBoxesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ObjectLayoutBox[] objectLayoutBoxes;

	public String getObjectRelationshipExternalReferenceCode() {
		return objectRelationshipExternalReferenceCode;
	}

	public void setObjectRelationshipExternalReferenceCode(
		String objectRelationshipExternalReferenceCode) {

		this.objectRelationshipExternalReferenceCode =
			objectRelationshipExternalReferenceCode;
	}

	public void setObjectRelationshipExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			objectRelationshipExternalReferenceCodeUnsafeSupplier) {

		try {
			objectRelationshipExternalReferenceCode =
				objectRelationshipExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String objectRelationshipExternalReferenceCode;

	public Long getObjectRelationshipId() {
		return objectRelationshipId;
	}

	public void setObjectRelationshipId(Long objectRelationshipId) {
		this.objectRelationshipId = objectRelationshipId;
	}

	public void setObjectRelationshipId(
		UnsafeSupplier<Long, Exception> objectRelationshipIdUnsafeSupplier) {

		try {
			objectRelationshipId = objectRelationshipIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long objectRelationshipId;

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

	@Override
	public ObjectLayoutTab clone() throws CloneNotSupportedException {
		return (ObjectLayoutTab)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectLayoutTab)) {
			return false;
		}

		ObjectLayoutTab objectLayoutTab = (ObjectLayoutTab)object;

		return Objects.equals(toString(), objectLayoutTab.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ObjectLayoutTabSerDes.toJSON(this);
	}

}