/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName("ObjectLayoutTab")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ObjectLayoutTab")
public class ObjectLayoutTab implements Serializable {

	public static ObjectLayoutTab toDTO(String json) {
		return ObjectMapperUtil.readValue(ObjectLayoutTab.class, json);
	}

	public static ObjectLayoutTab unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ObjectLayoutTab.class, json);
	}

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
	public Map<String, String> getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(Map<String, String> name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(
		UnsafeSupplier<Map<String, String>, Exception> nameUnsafeSupplier) {

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
	protected Map<String, String> name;

	@JsonIgnore
	private Supplier<Map<String, String>> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ObjectLayoutBox[] getObjectLayoutBoxes() {
		if (_objectLayoutBoxesSupplier != null) {
			objectLayoutBoxes = _objectLayoutBoxesSupplier.get();

			_objectLayoutBoxesSupplier = null;
		}

		return objectLayoutBoxes;
	}

	public void setObjectLayoutBoxes(ObjectLayoutBox[] objectLayoutBoxes) {
		this.objectLayoutBoxes = objectLayoutBoxes;

		_objectLayoutBoxesSupplier = null;
	}

	@JsonIgnore
	public void setObjectLayoutBoxes(
		UnsafeSupplier<ObjectLayoutBox[], Exception>
			objectLayoutBoxesUnsafeSupplier) {

		_objectLayoutBoxesSupplier = () -> {
			try {
				return objectLayoutBoxesUnsafeSupplier.get();
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
	protected ObjectLayoutBox[] objectLayoutBoxes;

	@JsonIgnore
	private Supplier<ObjectLayoutBox[]> _objectLayoutBoxesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getObjectRelationshipExternalReferenceCode() {
		if (_objectRelationshipExternalReferenceCodeSupplier != null) {
			objectRelationshipExternalReferenceCode =
				_objectRelationshipExternalReferenceCodeSupplier.get();

			_objectRelationshipExternalReferenceCodeSupplier = null;
		}

		return objectRelationshipExternalReferenceCode;
	}

	public void setObjectRelationshipExternalReferenceCode(
		String objectRelationshipExternalReferenceCode) {

		this.objectRelationshipExternalReferenceCode =
			objectRelationshipExternalReferenceCode;

		_objectRelationshipExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setObjectRelationshipExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			objectRelationshipExternalReferenceCodeUnsafeSupplier) {

		_objectRelationshipExternalReferenceCodeSupplier = () -> {
			try {
				return objectRelationshipExternalReferenceCodeUnsafeSupplier.
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
	protected String objectRelationshipExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _objectRelationshipExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getObjectRelationshipId() {
		if (_objectRelationshipIdSupplier != null) {
			objectRelationshipId = _objectRelationshipIdSupplier.get();

			_objectRelationshipIdSupplier = null;
		}

		return objectRelationshipId;
	}

	public void setObjectRelationshipId(Long objectRelationshipId) {
		this.objectRelationshipId = objectRelationshipId;

		_objectRelationshipIdSupplier = null;
	}

	@JsonIgnore
	public void setObjectRelationshipId(
		UnsafeSupplier<Long, Exception> objectRelationshipIdUnsafeSupplier) {

		_objectRelationshipIdSupplier = () -> {
			try {
				return objectRelationshipIdUnsafeSupplier.get();
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
	protected Long objectRelationshipId;

	@JsonIgnore
	private Supplier<Long> _objectRelationshipIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getPriority() {
		if (_prioritySupplier != null) {
			priority = _prioritySupplier.get();

			_prioritySupplier = null;
		}

		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;

		_prioritySupplier = null;
	}

	@JsonIgnore
	public void setPriority(
		UnsafeSupplier<Integer, Exception> priorityUnsafeSupplier) {

		_prioritySupplier = () -> {
			try {
				return priorityUnsafeSupplier.get();
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
	protected Integer priority;

	@JsonIgnore
	private Supplier<Integer> _prioritySupplier;

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
		StringBundler sb = new StringBundler();

		sb.append("{");

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		Map<String, String> name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(name));
		}

		ObjectLayoutBox[] objectLayoutBoxes = getObjectLayoutBoxes();

		if (objectLayoutBoxes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectLayoutBoxes\": ");

			sb.append("[");

			for (int i = 0; i < objectLayoutBoxes.length; i++) {
				sb.append(String.valueOf(objectLayoutBoxes[i]));

				if ((i + 1) < objectLayoutBoxes.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String objectRelationshipExternalReferenceCode =
			getObjectRelationshipExternalReferenceCode();

		if (objectRelationshipExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectRelationshipExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(objectRelationshipExternalReferenceCode));

			sb.append("\"");
		}

		Long objectRelationshipId = getObjectRelationshipId();

		if (objectRelationshipId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectRelationshipId\": ");

			sb.append(objectRelationshipId);
		}

		Integer priority = getPriority();

		if (priority != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(priority);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.object.admin.rest.dto.v1_0.ObjectLayoutTab",
		name = "x-class-name"
	)
	public String xClassName;

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