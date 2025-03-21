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

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
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
@GraphQLName("ObjectView")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ObjectView")
public class ObjectView implements Serializable {

	public static ObjectView toDTO(String json) {
		return ObjectMapperUtil.readValue(ObjectView.class, json);
	}

	public static ObjectView unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ObjectView.class, json);
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
	public Date getDateCreated() {
		if (_dateCreatedSupplier != null) {
			dateCreated = _dateCreatedSupplier.get();

			_dateCreatedSupplier = null;
		}

		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;

		_dateCreatedSupplier = null;
	}

	@JsonIgnore
	public void setDateCreated(
		UnsafeSupplier<Date, Exception> dateCreatedUnsafeSupplier) {

		_dateCreatedSupplier = () -> {
			try {
				return dateCreatedUnsafeSupplier.get();
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
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getDateModified() {
		if (_dateModifiedSupplier != null) {
			dateModified = _dateModifiedSupplier.get();

			_dateModifiedSupplier = null;
		}

		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;

		_dateModifiedSupplier = null;
	}

	@JsonIgnore
	public void setDateModified(
		UnsafeSupplier<Date, Exception> dateModifiedUnsafeSupplier) {

		_dateModifiedSupplier = () -> {
			try {
				return dateModifiedUnsafeSupplier.get();
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
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getDefaultObjectView() {
		if (_defaultObjectViewSupplier != null) {
			defaultObjectView = _defaultObjectViewSupplier.get();

			_defaultObjectViewSupplier = null;
		}

		return defaultObjectView;
	}

	public void setDefaultObjectView(Boolean defaultObjectView) {
		this.defaultObjectView = defaultObjectView;

		_defaultObjectViewSupplier = null;
	}

	@JsonIgnore
	public void setDefaultObjectView(
		UnsafeSupplier<Boolean, Exception> defaultObjectViewUnsafeSupplier) {

		_defaultObjectViewSupplier = () -> {
			try {
				return defaultObjectViewUnsafeSupplier.get();
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
	protected Boolean defaultObjectView;

	@JsonIgnore
	private Supplier<Boolean> _defaultObjectViewSupplier;

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
	public String getObjectDefinitionExternalReferenceCode() {
		if (_objectDefinitionExternalReferenceCodeSupplier != null) {
			objectDefinitionExternalReferenceCode =
				_objectDefinitionExternalReferenceCodeSupplier.get();

			_objectDefinitionExternalReferenceCodeSupplier = null;
		}

		return objectDefinitionExternalReferenceCode;
	}

	public void setObjectDefinitionExternalReferenceCode(
		String objectDefinitionExternalReferenceCode) {

		this.objectDefinitionExternalReferenceCode =
			objectDefinitionExternalReferenceCode;

		_objectDefinitionExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setObjectDefinitionExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			objectDefinitionExternalReferenceCodeUnsafeSupplier) {

		_objectDefinitionExternalReferenceCodeSupplier = () -> {
			try {
				return objectDefinitionExternalReferenceCodeUnsafeSupplier.
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
	protected String objectDefinitionExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _objectDefinitionExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getObjectDefinitionId() {
		if (_objectDefinitionIdSupplier != null) {
			objectDefinitionId = _objectDefinitionIdSupplier.get();

			_objectDefinitionIdSupplier = null;
		}

		return objectDefinitionId;
	}

	public void setObjectDefinitionId(Long objectDefinitionId) {
		this.objectDefinitionId = objectDefinitionId;

		_objectDefinitionIdSupplier = null;
	}

	@JsonIgnore
	public void setObjectDefinitionId(
		UnsafeSupplier<Long, Exception> objectDefinitionIdUnsafeSupplier) {

		_objectDefinitionIdSupplier = () -> {
			try {
				return objectDefinitionIdUnsafeSupplier.get();
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
	protected Long objectDefinitionId;

	@JsonIgnore
	private Supplier<Long> _objectDefinitionIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ObjectViewColumn[] getObjectViewColumns() {
		if (_objectViewColumnsSupplier != null) {
			objectViewColumns = _objectViewColumnsSupplier.get();

			_objectViewColumnsSupplier = null;
		}

		return objectViewColumns;
	}

	public void setObjectViewColumns(ObjectViewColumn[] objectViewColumns) {
		this.objectViewColumns = objectViewColumns;

		_objectViewColumnsSupplier = null;
	}

	@JsonIgnore
	public void setObjectViewColumns(
		UnsafeSupplier<ObjectViewColumn[], Exception>
			objectViewColumnsUnsafeSupplier) {

		_objectViewColumnsSupplier = () -> {
			try {
				return objectViewColumnsUnsafeSupplier.get();
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
	protected ObjectViewColumn[] objectViewColumns;

	@JsonIgnore
	private Supplier<ObjectViewColumn[]> _objectViewColumnsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ObjectViewFilterColumn[] getObjectViewFilterColumns() {
		if (_objectViewFilterColumnsSupplier != null) {
			objectViewFilterColumns = _objectViewFilterColumnsSupplier.get();

			_objectViewFilterColumnsSupplier = null;
		}

		return objectViewFilterColumns;
	}

	public void setObjectViewFilterColumns(
		ObjectViewFilterColumn[] objectViewFilterColumns) {

		this.objectViewFilterColumns = objectViewFilterColumns;

		_objectViewFilterColumnsSupplier = null;
	}

	@JsonIgnore
	public void setObjectViewFilterColumns(
		UnsafeSupplier<ObjectViewFilterColumn[], Exception>
			objectViewFilterColumnsUnsafeSupplier) {

		_objectViewFilterColumnsSupplier = () -> {
			try {
				return objectViewFilterColumnsUnsafeSupplier.get();
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
	protected ObjectViewFilterColumn[] objectViewFilterColumns;

	@JsonIgnore
	private Supplier<ObjectViewFilterColumn[]> _objectViewFilterColumnsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ObjectViewSortColumn[] getObjectViewSortColumns() {
		if (_objectViewSortColumnsSupplier != null) {
			objectViewSortColumns = _objectViewSortColumnsSupplier.get();

			_objectViewSortColumnsSupplier = null;
		}

		return objectViewSortColumns;
	}

	public void setObjectViewSortColumns(
		ObjectViewSortColumn[] objectViewSortColumns) {

		this.objectViewSortColumns = objectViewSortColumns;

		_objectViewSortColumnsSupplier = null;
	}

	@JsonIgnore
	public void setObjectViewSortColumns(
		UnsafeSupplier<ObjectViewSortColumn[], Exception>
			objectViewSortColumnsUnsafeSupplier) {

		_objectViewSortColumnsSupplier = () -> {
			try {
				return objectViewSortColumnsUnsafeSupplier.get();
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
	protected ObjectViewSortColumn[] objectViewSortColumns;

	@JsonIgnore
	private Supplier<ObjectViewSortColumn[]> _objectViewSortColumnsSupplier;

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
		StringBundler sb = new StringBundler();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		Map<String, Map<String, String>> actions = getActions();

		if (actions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(actions));
		}

		Date dateCreated = getDateCreated();

		if (dateCreated != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateCreated));

			sb.append("\"");
		}

		Date dateModified = getDateModified();

		if (dateModified != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateModified));

			sb.append("\"");
		}

		Boolean defaultObjectView = getDefaultObjectView();

		if (defaultObjectView != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultObjectView\": ");

			sb.append(defaultObjectView);
		}

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

		String objectDefinitionExternalReferenceCode =
			getObjectDefinitionExternalReferenceCode();

		if (objectDefinitionExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(objectDefinitionExternalReferenceCode));

			sb.append("\"");
		}

		Long objectDefinitionId = getObjectDefinitionId();

		if (objectDefinitionId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionId\": ");

			sb.append(objectDefinitionId);
		}

		ObjectViewColumn[] objectViewColumns = getObjectViewColumns();

		if (objectViewColumns != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectViewColumns\": ");

			sb.append("[");

			for (int i = 0; i < objectViewColumns.length; i++) {
				sb.append(String.valueOf(objectViewColumns[i]));

				if ((i + 1) < objectViewColumns.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		ObjectViewFilterColumn[] objectViewFilterColumns =
			getObjectViewFilterColumns();

		if (objectViewFilterColumns != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectViewFilterColumns\": ");

			sb.append("[");

			for (int i = 0; i < objectViewFilterColumns.length; i++) {
				sb.append(String.valueOf(objectViewFilterColumns[i]));

				if ((i + 1) < objectViewFilterColumns.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		ObjectViewSortColumn[] objectViewSortColumns =
			getObjectViewSortColumns();

		if (objectViewSortColumns != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectViewSortColumns\": ");

			sb.append("[");

			for (int i = 0; i < objectViewSortColumns.length; i++) {
				sb.append(String.valueOf(objectViewSortColumns[i]));

				if ((i + 1) < objectViewSortColumns.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.object.admin.rest.dto.v1_0.ObjectView",
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