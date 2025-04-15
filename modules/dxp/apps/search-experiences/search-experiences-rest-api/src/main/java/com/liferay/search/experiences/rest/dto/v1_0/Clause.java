/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
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
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
@GraphQLName("Clause")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Clause")
public class Clause implements Serializable {

	public static Clause toDTO(String json) {
		return ObjectMapperUtil.readValue(Clause.class, json);
	}

	public static Clause unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Clause.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getAdditive() {
		if (_additiveSupplier != null) {
			additive = _additiveSupplier.get();

			_additiveSupplier = null;
		}

		return additive;
	}

	public void setAdditive(Boolean additive) {
		this.additive = additive;

		_additiveSupplier = null;
	}

	@JsonIgnore
	public void setAdditive(
		UnsafeSupplier<Boolean, Exception> additiveUnsafeSupplier) {

		_additiveSupplier = () -> {
			try {
				return additiveUnsafeSupplier.get();
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
	protected Boolean additive;

	@JsonIgnore
	private Supplier<Boolean> _additiveSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Float getBoost() {
		if (_boostSupplier != null) {
			boost = _boostSupplier.get();

			_boostSupplier = null;
		}

		return boost;
	}

	public void setBoost(Float boost) {
		this.boost = boost;

		_boostSupplier = null;
	}

	@JsonIgnore
	public void setBoost(UnsafeSupplier<Float, Exception> boostUnsafeSupplier) {
		_boostSupplier = () -> {
			try {
				return boostUnsafeSupplier.get();
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
	protected Float boost;

	@JsonIgnore
	private Supplier<Float> _boostSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getContext() {
		if (_contextSupplier != null) {
			context = _contextSupplier.get();

			_contextSupplier = null;
		}

		return context;
	}

	public void setContext(String context) {
		this.context = context;

		_contextSupplier = null;
	}

	@JsonIgnore
	public void setContext(
		UnsafeSupplier<String, Exception> contextUnsafeSupplier) {

		_contextSupplier = () -> {
			try {
				return contextUnsafeSupplier.get();
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
	protected String context;

	@JsonIgnore
	private Supplier<String> _contextSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getDisabled() {
		if (_disabledSupplier != null) {
			disabled = _disabledSupplier.get();

			_disabledSupplier = null;
		}

		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;

		_disabledSupplier = null;
	}

	@JsonIgnore
	public void setDisabled(
		UnsafeSupplier<Boolean, Exception> disabledUnsafeSupplier) {

		_disabledSupplier = () -> {
			try {
				return disabledUnsafeSupplier.get();
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
	protected Boolean disabled;

	@JsonIgnore
	private Supplier<Boolean> _disabledSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getField() {
		if (_fieldSupplier != null) {
			field = _fieldSupplier.get();

			_fieldSupplier = null;
		}

		return field;
	}

	public void setField(String field) {
		this.field = field;

		_fieldSupplier = null;
	}

	@JsonIgnore
	public void setField(
		UnsafeSupplier<String, Exception> fieldUnsafeSupplier) {

		_fieldSupplier = () -> {
			try {
				return fieldUnsafeSupplier.get();
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
	protected String field;

	@JsonIgnore
	private Supplier<String> _fieldSupplier;

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
	public String getOccur() {
		if (_occurSupplier != null) {
			occur = _occurSupplier.get();

			_occurSupplier = null;
		}

		return occur;
	}

	public void setOccur(String occur) {
		this.occur = occur;

		_occurSupplier = null;
	}

	@JsonIgnore
	public void setOccur(
		UnsafeSupplier<String, Exception> occurUnsafeSupplier) {

		_occurSupplier = () -> {
			try {
				return occurUnsafeSupplier.get();
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
	protected String occur;

	@JsonIgnore
	private Supplier<String> _occurSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getParent() {
		if (_parentSupplier != null) {
			parent = _parentSupplier.get();

			_parentSupplier = null;
		}

		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;

		_parentSupplier = null;
	}

	@JsonIgnore
	public void setParent(
		UnsafeSupplier<String, Exception> parentUnsafeSupplier) {

		_parentSupplier = () -> {
			try {
				return parentUnsafeSupplier.get();
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
	protected String parent;

	@JsonIgnore
	private Supplier<String> _parentSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getQuery() {
		if (_querySupplier != null) {
			query = _querySupplier.get();

			_querySupplier = null;
		}

		return query;
	}

	public void setQuery(Object query) {
		this.query = query;

		_querySupplier = null;
	}

	@JsonIgnore
	public void setQuery(
		UnsafeSupplier<Object, Exception> queryUnsafeSupplier) {

		_querySupplier = () -> {
			try {
				return queryUnsafeSupplier.get();
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
	protected Object query;

	@JsonIgnore
	private Supplier<Object> _querySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getType() {
		if (_typeSupplier != null) {
			type = _typeSupplier.get();

			_typeSupplier = null;
		}

		return type;
	}

	public void setType(String type) {
		this.type = type;

		_typeSupplier = null;
	}

	@JsonIgnore
	public void setType(UnsafeSupplier<String, Exception> typeUnsafeSupplier) {
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
	protected String type;

	@JsonIgnore
	private Supplier<String> _typeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getValue() {
		if (_valueSupplier != null) {
			value = _valueSupplier.get();

			_valueSupplier = null;
		}

		return value;
	}

	public void setValue(String value) {
		this.value = value;

		_valueSupplier = null;
	}

	@JsonIgnore
	public void setValue(
		UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

		_valueSupplier = () -> {
			try {
				return valueUnsafeSupplier.get();
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
	protected String value;

	@JsonIgnore
	private Supplier<String> _valueSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Clause)) {
			return false;
		}

		Clause clause = (Clause)object;

		return Objects.equals(toString(), clause.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean additive = getAdditive();

		if (additive != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"additive\": ");

			sb.append(additive);
		}

		Float boost = getBoost();

		if (boost != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"boost\": ");

			sb.append(boost);
		}

		String context = getContext();

		if (context != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"context\": ");

			sb.append("\"");

			sb.append(_escape(context));

			sb.append("\"");
		}

		Boolean disabled = getDisabled();

		if (disabled != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"disabled\": ");

			sb.append(disabled);
		}

		String field = getField();

		if (field != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"field\": ");

			sb.append("\"");

			sb.append(_escape(field));

			sb.append("\"");
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

		String occur = getOccur();

		if (occur != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"occur\": ");

			sb.append("\"");

			sb.append(_escape(occur));

			sb.append("\"");
		}

		String parent = getParent();

		if (parent != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parent\": ");

			sb.append("\"");

			sb.append(_escape(parent));

			sb.append("\"");
		}

		Object query = getQuery();

		if (query != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"query\": ");

			if (query instanceof Map) {
				sb.append(JSONFactoryUtil.createJSONObject((Map<?, ?>)query));
			}
			else if (query instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)query));
				sb.append("\"");
			}
			else {
				sb.append(query);
			}
		}

		String type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(type));

			sb.append("\"");
		}

		String value = getValue();

		if (value != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			sb.append("\"");

			sb.append(_escape(value));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.search.experiences.rest.dto.v1_0.Clause",
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