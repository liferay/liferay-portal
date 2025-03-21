/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.dto.v1_0;

import com.liferay.object.admin.rest.client.function.UnsafeSupplier;
import com.liferay.object.admin.rest.client.serdes.v1_0.ObjectRelationshipSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class ObjectRelationship implements Cloneable, Serializable {

	public static ObjectRelationship toDTO(String json) {
		return ObjectRelationshipSerDes.toDTO(json);
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

	public DeletionType getDeletionType() {
		return deletionType;
	}

	public String getDeletionTypeAsString() {
		if (deletionType == null) {
			return null;
		}

		return deletionType.toString();
	}

	public void setDeletionType(DeletionType deletionType) {
		this.deletionType = deletionType;
	}

	public void setDeletionType(
		UnsafeSupplier<DeletionType, Exception> deletionTypeUnsafeSupplier) {

		try {
			deletionType = deletionTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected DeletionType deletionType;

	public Boolean getEdge() {
		return edge;
	}

	public void setEdge(Boolean edge) {
		this.edge = edge;
	}

	public void setEdge(UnsafeSupplier<Boolean, Exception> edgeUnsafeSupplier) {
		try {
			edge = edgeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean edge;

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

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

	public Map<String, String> getLabel() {
		return label;
	}

	public void setLabel(Map<String, String> label) {
		this.label = label;
	}

	public void setLabel(
		UnsafeSupplier<Map<String, String>, Exception> labelUnsafeSupplier) {

		try {
			label = labelUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> label;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String name;

	public String getObjectDefinitionExternalReferenceCode1() {
		return objectDefinitionExternalReferenceCode1;
	}

	public void setObjectDefinitionExternalReferenceCode1(
		String objectDefinitionExternalReferenceCode1) {

		this.objectDefinitionExternalReferenceCode1 =
			objectDefinitionExternalReferenceCode1;
	}

	public void setObjectDefinitionExternalReferenceCode1(
		UnsafeSupplier<String, Exception>
			objectDefinitionExternalReferenceCode1UnsafeSupplier) {

		try {
			objectDefinitionExternalReferenceCode1 =
				objectDefinitionExternalReferenceCode1UnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String objectDefinitionExternalReferenceCode1;

	public String getObjectDefinitionExternalReferenceCode2() {
		return objectDefinitionExternalReferenceCode2;
	}

	public void setObjectDefinitionExternalReferenceCode2(
		String objectDefinitionExternalReferenceCode2) {

		this.objectDefinitionExternalReferenceCode2 =
			objectDefinitionExternalReferenceCode2;
	}

	public void setObjectDefinitionExternalReferenceCode2(
		UnsafeSupplier<String, Exception>
			objectDefinitionExternalReferenceCode2UnsafeSupplier) {

		try {
			objectDefinitionExternalReferenceCode2 =
				objectDefinitionExternalReferenceCode2UnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String objectDefinitionExternalReferenceCode2;

	public Long getObjectDefinitionId1() {
		return objectDefinitionId1;
	}

	public void setObjectDefinitionId1(Long objectDefinitionId1) {
		this.objectDefinitionId1 = objectDefinitionId1;
	}

	public void setObjectDefinitionId1(
		UnsafeSupplier<Long, Exception> objectDefinitionId1UnsafeSupplier) {

		try {
			objectDefinitionId1 = objectDefinitionId1UnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long objectDefinitionId1;

	public Long getObjectDefinitionId2() {
		return objectDefinitionId2;
	}

	public void setObjectDefinitionId2(Long objectDefinitionId2) {
		this.objectDefinitionId2 = objectDefinitionId2;
	}

	public void setObjectDefinitionId2(
		UnsafeSupplier<Long, Exception> objectDefinitionId2UnsafeSupplier) {

		try {
			objectDefinitionId2 = objectDefinitionId2UnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long objectDefinitionId2;

	public Boolean getObjectDefinitionModifiable2() {
		return objectDefinitionModifiable2;
	}

	public void setObjectDefinitionModifiable2(
		Boolean objectDefinitionModifiable2) {

		this.objectDefinitionModifiable2 = objectDefinitionModifiable2;
	}

	public void setObjectDefinitionModifiable2(
		UnsafeSupplier<Boolean, Exception>
			objectDefinitionModifiable2UnsafeSupplier) {

		try {
			objectDefinitionModifiable2 =
				objectDefinitionModifiable2UnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean objectDefinitionModifiable2;

	public String getObjectDefinitionName2() {
		return objectDefinitionName2;
	}

	public void setObjectDefinitionName2(String objectDefinitionName2) {
		this.objectDefinitionName2 = objectDefinitionName2;
	}

	public void setObjectDefinitionName2(
		UnsafeSupplier<String, Exception> objectDefinitionName2UnsafeSupplier) {

		try {
			objectDefinitionName2 = objectDefinitionName2UnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String objectDefinitionName2;

	public String getObjectDefinitionScope2() {
		return objectDefinitionScope2;
	}

	public void setObjectDefinitionScope2(String objectDefinitionScope2) {
		this.objectDefinitionScope2 = objectDefinitionScope2;
	}

	public void setObjectDefinitionScope2(
		UnsafeSupplier<String, Exception>
			objectDefinitionScope2UnsafeSupplier) {

		try {
			objectDefinitionScope2 = objectDefinitionScope2UnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String objectDefinitionScope2;

	public Boolean getObjectDefinitionSystem2() {
		return objectDefinitionSystem2;
	}

	public void setObjectDefinitionSystem2(Boolean objectDefinitionSystem2) {
		this.objectDefinitionSystem2 = objectDefinitionSystem2;
	}

	public void setObjectDefinitionSystem2(
		UnsafeSupplier<Boolean, Exception>
			objectDefinitionSystem2UnsafeSupplier) {

		try {
			objectDefinitionSystem2 =
				objectDefinitionSystem2UnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean objectDefinitionSystem2;

	public ObjectField getObjectField() {
		return objectField;
	}

	public void setObjectField(ObjectField objectField) {
		this.objectField = objectField;
	}

	public void setObjectField(
		UnsafeSupplier<ObjectField, Exception> objectFieldUnsafeSupplier) {

		try {
			objectField = objectFieldUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ObjectField objectField;

	public Long getParameterObjectFieldId() {
		return parameterObjectFieldId;
	}

	public void setParameterObjectFieldId(Long parameterObjectFieldId) {
		this.parameterObjectFieldId = parameterObjectFieldId;
	}

	public void setParameterObjectFieldId(
		UnsafeSupplier<Long, Exception> parameterObjectFieldIdUnsafeSupplier) {

		try {
			parameterObjectFieldId = parameterObjectFieldIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long parameterObjectFieldId;

	public String getParameterObjectFieldName() {
		return parameterObjectFieldName;
	}

	public void setParameterObjectFieldName(String parameterObjectFieldName) {
		this.parameterObjectFieldName = parameterObjectFieldName;
	}

	public void setParameterObjectFieldName(
		UnsafeSupplier<String, Exception>
			parameterObjectFieldNameUnsafeSupplier) {

		try {
			parameterObjectFieldName =
				parameterObjectFieldNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String parameterObjectFieldName;

	public Boolean getReverse() {
		return reverse;
	}

	public void setReverse(Boolean reverse) {
		this.reverse = reverse;
	}

	public void setReverse(
		UnsafeSupplier<Boolean, Exception> reverseUnsafeSupplier) {

		try {
			reverse = reverseUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean reverse;

	public Boolean getSystem() {
		return system;
	}

	public void setSystem(Boolean system) {
		this.system = system;
	}

	public void setSystem(
		UnsafeSupplier<Boolean, Exception> systemUnsafeSupplier) {

		try {
			system = systemUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean system;

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
	public ObjectRelationship clone() throws CloneNotSupportedException {
		return (ObjectRelationship)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectRelationship)) {
			return false;
		}

		ObjectRelationship objectRelationship = (ObjectRelationship)object;

		return Objects.equals(toString(), objectRelationship.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ObjectRelationshipSerDes.toJSON(this);
	}

	public static enum DeletionType {

		CASCADE("cascade"), DISASSOCIATE("disassociate"), PREVENT("prevent");

		public static DeletionType create(String value) {
			for (DeletionType deletionType : values()) {
				if (Objects.equals(deletionType.getValue(), value) ||
					Objects.equals(deletionType.name(), value)) {

					return deletionType;
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

		private DeletionType(String value) {
			_value = value;
		}

		private final String _value;

	}

	public static enum Type {

		ONE_TO_MANY("oneToMany"), ONE_TO_ONE("oneToOne"),
		MANY_TO_MANY("manyToMany");

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