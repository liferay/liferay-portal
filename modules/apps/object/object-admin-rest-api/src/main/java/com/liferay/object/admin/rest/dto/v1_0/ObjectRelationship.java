/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName("ObjectRelationship")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ObjectRelationship")
public class ObjectRelationship implements Serializable {

	public static ObjectRelationship toDTO(String json) {
		return ObjectMapperUtil.readValue(ObjectRelationship.class, json);
	}

	public static ObjectRelationship unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ObjectRelationship.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, Map<String, String>> getActions() {
		if (_actionsSupplier != null) {
			actions = _actionsSupplier.get();

			_actionsSupplier = null;
		}

		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;

		_actionsSupplier = null;
	}

	@JsonIgnore
	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		_actionsSupplier = () -> {
			try {
				return actionsUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("deletionType")
	@Valid
	public DeletionType getDeletionType() {
		if (_deletionTypeSupplier != null) {
			deletionType = _deletionTypeSupplier.get();

			_deletionTypeSupplier = null;
		}

		return deletionType;
	}

	@JsonIgnore
	public String getDeletionTypeAsString() {
		DeletionType deletionType = getDeletionType();

		if (deletionType == null) {
			return null;
		}

		return deletionType.toString();
	}

	public void setDeletionType(DeletionType deletionType) {
		this.deletionType = deletionType;

		_deletionTypeSupplier = null;
	}

	@JsonIgnore
	public void setDeletionType(
		UnsafeSupplier<DeletionType, Exception> deletionTypeUnsafeSupplier) {

		_deletionTypeSupplier = () -> {
			try {
				return deletionTypeUnsafeSupplier.get();
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
	protected DeletionType deletionType;

	@JsonIgnore
	private Supplier<DeletionType> _deletionTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getEdge() {
		if (_edgeSupplier != null) {
			edge = _edgeSupplier.get();

			_edgeSupplier = null;
		}

		return edge;
	}

	public void setEdge(Boolean edge) {
		this.edge = edge;

		_edgeSupplier = null;
	}

	@JsonIgnore
	public void setEdge(UnsafeSupplier<Boolean, Exception> edgeUnsafeSupplier) {
		_edgeSupplier = () -> {
			try {
				return edgeUnsafeSupplier.get();
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
	protected Boolean edge;

	@JsonIgnore
	private Supplier<Boolean> _edgeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getExternalReferenceCode() {
		if (_externalReferenceCodeSupplier != null) {
			externalReferenceCode = _externalReferenceCodeSupplier.get();

			_externalReferenceCodeSupplier = null;
		}

		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;

		_externalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		_externalReferenceCodeSupplier = () -> {
			try {
				return externalReferenceCodeUnsafeSupplier.get();
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
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(Long id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		_idSupplier = () -> {
			try {
				return idUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, String> getLabel() {
		if (_labelSupplier != null) {
			label = _labelSupplier.get();

			_labelSupplier = null;
		}

		return label;
	}

	public void setLabel(Map<String, String> label) {
		this.label = label;

		_labelSupplier = null;
	}

	@JsonIgnore
	public void setLabel(
		UnsafeSupplier<Map<String, String>, Exception> labelUnsafeSupplier) {

		_labelSupplier = () -> {
			try {
				return labelUnsafeSupplier.get();
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
	protected Map<String, String> label;

	@JsonIgnore
	private Supplier<Map<String, String>> _labelSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(String name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		_nameSupplier = () -> {
			try {
				return nameUnsafeSupplier.get();
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
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getObjectDefinitionExternalReferenceCode1() {
		if (_objectDefinitionExternalReferenceCode1Supplier != null) {
			objectDefinitionExternalReferenceCode1 =
				_objectDefinitionExternalReferenceCode1Supplier.get();

			_objectDefinitionExternalReferenceCode1Supplier = null;
		}

		return objectDefinitionExternalReferenceCode1;
	}

	public void setObjectDefinitionExternalReferenceCode1(
		String objectDefinitionExternalReferenceCode1) {

		this.objectDefinitionExternalReferenceCode1 =
			objectDefinitionExternalReferenceCode1;

		_objectDefinitionExternalReferenceCode1Supplier = null;
	}

	@JsonIgnore
	public void setObjectDefinitionExternalReferenceCode1(
		UnsafeSupplier<String, Exception>
			objectDefinitionExternalReferenceCode1UnsafeSupplier) {

		_objectDefinitionExternalReferenceCode1Supplier = () -> {
			try {
				return objectDefinitionExternalReferenceCode1UnsafeSupplier.
					get();
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
	protected String objectDefinitionExternalReferenceCode1;

	@JsonIgnore
	private Supplier<String> _objectDefinitionExternalReferenceCode1Supplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getObjectDefinitionExternalReferenceCode2() {
		if (_objectDefinitionExternalReferenceCode2Supplier != null) {
			objectDefinitionExternalReferenceCode2 =
				_objectDefinitionExternalReferenceCode2Supplier.get();

			_objectDefinitionExternalReferenceCode2Supplier = null;
		}

		return objectDefinitionExternalReferenceCode2;
	}

	public void setObjectDefinitionExternalReferenceCode2(
		String objectDefinitionExternalReferenceCode2) {

		this.objectDefinitionExternalReferenceCode2 =
			objectDefinitionExternalReferenceCode2;

		_objectDefinitionExternalReferenceCode2Supplier = null;
	}

	@JsonIgnore
	public void setObjectDefinitionExternalReferenceCode2(
		UnsafeSupplier<String, Exception>
			objectDefinitionExternalReferenceCode2UnsafeSupplier) {

		_objectDefinitionExternalReferenceCode2Supplier = () -> {
			try {
				return objectDefinitionExternalReferenceCode2UnsafeSupplier.
					get();
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
	protected String objectDefinitionExternalReferenceCode2;

	@JsonIgnore
	private Supplier<String> _objectDefinitionExternalReferenceCode2Supplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getObjectDefinitionId1() {
		if (_objectDefinitionId1Supplier != null) {
			objectDefinitionId1 = _objectDefinitionId1Supplier.get();

			_objectDefinitionId1Supplier = null;
		}

		return objectDefinitionId1;
	}

	public void setObjectDefinitionId1(Long objectDefinitionId1) {
		this.objectDefinitionId1 = objectDefinitionId1;

		_objectDefinitionId1Supplier = null;
	}

	@JsonIgnore
	public void setObjectDefinitionId1(
		UnsafeSupplier<Long, Exception> objectDefinitionId1UnsafeSupplier) {

		_objectDefinitionId1Supplier = () -> {
			try {
				return objectDefinitionId1UnsafeSupplier.get();
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
	protected Long objectDefinitionId1;

	@JsonIgnore
	private Supplier<Long> _objectDefinitionId1Supplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getObjectDefinitionId2() {
		if (_objectDefinitionId2Supplier != null) {
			objectDefinitionId2 = _objectDefinitionId2Supplier.get();

			_objectDefinitionId2Supplier = null;
		}

		return objectDefinitionId2;
	}

	public void setObjectDefinitionId2(Long objectDefinitionId2) {
		this.objectDefinitionId2 = objectDefinitionId2;

		_objectDefinitionId2Supplier = null;
	}

	@JsonIgnore
	public void setObjectDefinitionId2(
		UnsafeSupplier<Long, Exception> objectDefinitionId2UnsafeSupplier) {

		_objectDefinitionId2Supplier = () -> {
			try {
				return objectDefinitionId2UnsafeSupplier.get();
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
	protected Long objectDefinitionId2;

	@JsonIgnore
	private Supplier<Long> _objectDefinitionId2Supplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getObjectDefinitionModifiable2() {
		if (_objectDefinitionModifiable2Supplier != null) {
			objectDefinitionModifiable2 =
				_objectDefinitionModifiable2Supplier.get();

			_objectDefinitionModifiable2Supplier = null;
		}

		return objectDefinitionModifiable2;
	}

	public void setObjectDefinitionModifiable2(
		Boolean objectDefinitionModifiable2) {

		this.objectDefinitionModifiable2 = objectDefinitionModifiable2;

		_objectDefinitionModifiable2Supplier = null;
	}

	@JsonIgnore
	public void setObjectDefinitionModifiable2(
		UnsafeSupplier<Boolean, Exception>
			objectDefinitionModifiable2UnsafeSupplier) {

		_objectDefinitionModifiable2Supplier = () -> {
			try {
				return objectDefinitionModifiable2UnsafeSupplier.get();
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
	protected Boolean objectDefinitionModifiable2;

	@JsonIgnore
	private Supplier<Boolean> _objectDefinitionModifiable2Supplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getObjectDefinitionName2() {
		if (_objectDefinitionName2Supplier != null) {
			objectDefinitionName2 = _objectDefinitionName2Supplier.get();

			_objectDefinitionName2Supplier = null;
		}

		return objectDefinitionName2;
	}

	public void setObjectDefinitionName2(String objectDefinitionName2) {
		this.objectDefinitionName2 = objectDefinitionName2;

		_objectDefinitionName2Supplier = null;
	}

	@JsonIgnore
	public void setObjectDefinitionName2(
		UnsafeSupplier<String, Exception> objectDefinitionName2UnsafeSupplier) {

		_objectDefinitionName2Supplier = () -> {
			try {
				return objectDefinitionName2UnsafeSupplier.get();
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
	protected String objectDefinitionName2;

	@JsonIgnore
	private Supplier<String> _objectDefinitionName2Supplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getObjectDefinitionScope2() {
		if (_objectDefinitionScope2Supplier != null) {
			objectDefinitionScope2 = _objectDefinitionScope2Supplier.get();

			_objectDefinitionScope2Supplier = null;
		}

		return objectDefinitionScope2;
	}

	public void setObjectDefinitionScope2(String objectDefinitionScope2) {
		this.objectDefinitionScope2 = objectDefinitionScope2;

		_objectDefinitionScope2Supplier = null;
	}

	@JsonIgnore
	public void setObjectDefinitionScope2(
		UnsafeSupplier<String, Exception>
			objectDefinitionScope2UnsafeSupplier) {

		_objectDefinitionScope2Supplier = () -> {
			try {
				return objectDefinitionScope2UnsafeSupplier.get();
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
	protected String objectDefinitionScope2;

	@JsonIgnore
	private Supplier<String> _objectDefinitionScope2Supplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getObjectDefinitionSystem2() {
		if (_objectDefinitionSystem2Supplier != null) {
			objectDefinitionSystem2 = _objectDefinitionSystem2Supplier.get();

			_objectDefinitionSystem2Supplier = null;
		}

		return objectDefinitionSystem2;
	}

	public void setObjectDefinitionSystem2(Boolean objectDefinitionSystem2) {
		this.objectDefinitionSystem2 = objectDefinitionSystem2;

		_objectDefinitionSystem2Supplier = null;
	}

	@JsonIgnore
	public void setObjectDefinitionSystem2(
		UnsafeSupplier<Boolean, Exception>
			objectDefinitionSystem2UnsafeSupplier) {

		_objectDefinitionSystem2Supplier = () -> {
			try {
				return objectDefinitionSystem2UnsafeSupplier.get();
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
	protected Boolean objectDefinitionSystem2;

	@JsonIgnore
	private Supplier<Boolean> _objectDefinitionSystem2Supplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ObjectField getObjectField() {
		if (_objectFieldSupplier != null) {
			objectField = _objectFieldSupplier.get();

			_objectFieldSupplier = null;
		}

		return objectField;
	}

	public void setObjectField(ObjectField objectField) {
		this.objectField = objectField;

		_objectFieldSupplier = null;
	}

	@JsonIgnore
	public void setObjectField(
		UnsafeSupplier<ObjectField, Exception> objectFieldUnsafeSupplier) {

		_objectFieldSupplier = () -> {
			try {
				return objectFieldUnsafeSupplier.get();
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
	protected ObjectField objectField;

	@JsonIgnore
	private Supplier<ObjectField> _objectFieldSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getParameterObjectFieldId() {
		if (_parameterObjectFieldIdSupplier != null) {
			parameterObjectFieldId = _parameterObjectFieldIdSupplier.get();

			_parameterObjectFieldIdSupplier = null;
		}

		return parameterObjectFieldId;
	}

	public void setParameterObjectFieldId(Long parameterObjectFieldId) {
		this.parameterObjectFieldId = parameterObjectFieldId;

		_parameterObjectFieldIdSupplier = null;
	}

	@JsonIgnore
	public void setParameterObjectFieldId(
		UnsafeSupplier<Long, Exception> parameterObjectFieldIdUnsafeSupplier) {

		_parameterObjectFieldIdSupplier = () -> {
			try {
				return parameterObjectFieldIdUnsafeSupplier.get();
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
	protected Long parameterObjectFieldId;

	@JsonIgnore
	private Supplier<Long> _parameterObjectFieldIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getParameterObjectFieldName() {
		if (_parameterObjectFieldNameSupplier != null) {
			parameterObjectFieldName = _parameterObjectFieldNameSupplier.get();

			_parameterObjectFieldNameSupplier = null;
		}

		return parameterObjectFieldName;
	}

	public void setParameterObjectFieldName(String parameterObjectFieldName) {
		this.parameterObjectFieldName = parameterObjectFieldName;

		_parameterObjectFieldNameSupplier = null;
	}

	@JsonIgnore
	public void setParameterObjectFieldName(
		UnsafeSupplier<String, Exception>
			parameterObjectFieldNameUnsafeSupplier) {

		_parameterObjectFieldNameSupplier = () -> {
			try {
				return parameterObjectFieldNameUnsafeSupplier.get();
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
	protected String parameterObjectFieldName;

	@JsonIgnore
	private Supplier<String> _parameterObjectFieldNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getReverse() {
		if (_reverseSupplier != null) {
			reverse = _reverseSupplier.get();

			_reverseSupplier = null;
		}

		return reverse;
	}

	public void setReverse(Boolean reverse) {
		this.reverse = reverse;

		_reverseSupplier = null;
	}

	@JsonIgnore
	public void setReverse(
		UnsafeSupplier<Boolean, Exception> reverseUnsafeSupplier) {

		_reverseSupplier = () -> {
			try {
				return reverseUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Boolean reverse;

	@JsonIgnore
	private Supplier<Boolean> _reverseSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getSystem() {
		if (_systemSupplier != null) {
			system = _systemSupplier.get();

			_systemSupplier = null;
		}

		return system;
	}

	public void setSystem(Boolean system) {
		this.system = system;

		_systemSupplier = null;
	}

	@JsonIgnore
	public void setSystem(
		UnsafeSupplier<Boolean, Exception> systemUnsafeSupplier) {

		_systemSupplier = () -> {
			try {
				return systemUnsafeSupplier.get();
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
	protected Boolean system;

	@JsonIgnore
	private Supplier<Boolean> _systemSupplier;

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
		StringBundler sb = new StringBundler();

		sb.append("{");

		Map<String, Map<String, String>> actions = getActions();

		if (actions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(actions));
		}

		DeletionType deletionType = getDeletionType();

		if (deletionType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deletionType\": ");

			sb.append("\"");

			sb.append(deletionType);

			sb.append("\"");
		}

		Boolean edge = getEdge();

		if (edge != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"edge\": ");

			sb.append(edge);
		}

		String externalReferenceCode = getExternalReferenceCode();

		if (externalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(externalReferenceCode));

			sb.append("\"");
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		Map<String, String> label = getLabel();

		if (label != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append(_toJSON(label));
		}

		String name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(name));

			sb.append("\"");
		}

		String objectDefinitionExternalReferenceCode1 =
			getObjectDefinitionExternalReferenceCode1();

		if (objectDefinitionExternalReferenceCode1 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionExternalReferenceCode1\": ");

			sb.append("\"");

			sb.append(_escape(objectDefinitionExternalReferenceCode1));

			sb.append("\"");
		}

		String objectDefinitionExternalReferenceCode2 =
			getObjectDefinitionExternalReferenceCode2();

		if (objectDefinitionExternalReferenceCode2 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionExternalReferenceCode2\": ");

			sb.append("\"");

			sb.append(_escape(objectDefinitionExternalReferenceCode2));

			sb.append("\"");
		}

		Long objectDefinitionId1 = getObjectDefinitionId1();

		if (objectDefinitionId1 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionId1\": ");

			sb.append(objectDefinitionId1);
		}

		Long objectDefinitionId2 = getObjectDefinitionId2();

		if (objectDefinitionId2 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionId2\": ");

			sb.append(objectDefinitionId2);
		}

		Boolean objectDefinitionModifiable2 = getObjectDefinitionModifiable2();

		if (objectDefinitionModifiable2 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionModifiable2\": ");

			sb.append(objectDefinitionModifiable2);
		}

		String objectDefinitionName2 = getObjectDefinitionName2();

		if (objectDefinitionName2 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionName2\": ");

			sb.append("\"");

			sb.append(_escape(objectDefinitionName2));

			sb.append("\"");
		}

		String objectDefinitionScope2 = getObjectDefinitionScope2();

		if (objectDefinitionScope2 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionScope2\": ");

			sb.append("\"");

			sb.append(_escape(objectDefinitionScope2));

			sb.append("\"");
		}

		Boolean objectDefinitionSystem2 = getObjectDefinitionSystem2();

		if (objectDefinitionSystem2 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionSystem2\": ");

			sb.append(objectDefinitionSystem2);
		}

		ObjectField objectField = getObjectField();

		if (objectField != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectField\": ");

			sb.append(String.valueOf(objectField));
		}

		Long parameterObjectFieldId = getParameterObjectFieldId();

		if (parameterObjectFieldId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parameterObjectFieldId\": ");

			sb.append(parameterObjectFieldId);
		}

		String parameterObjectFieldName = getParameterObjectFieldName();

		if (parameterObjectFieldName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parameterObjectFieldName\": ");

			sb.append("\"");

			sb.append(_escape(parameterObjectFieldName));

			sb.append("\"");
		}

		Boolean reverse = getReverse();

		if (reverse != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"reverse\": ");

			sb.append(reverse);
		}

		Boolean system = getSystem();

		if (system != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"system\": ");

			sb.append(system);
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
		defaultValue = "com.liferay.object.admin.rest.dto.v1_0.ObjectRelationship",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("DeletionType")
	public static enum DeletionType {

		CASCADE("cascade"), DISASSOCIATE("disassociate"), PREVENT("prevent");

		@JsonCreator
		public static DeletionType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (DeletionType deletionType : values()) {
				if (Objects.equals(deletionType.getValue(), value)) {
					return deletionType;
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

		private DeletionType(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("Type")
	public static enum Type {

		ONE_TO_MANY("oneToMany"), ONE_TO_ONE("oneToOne"),
		MANY_TO_MANY("manyToMany");

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