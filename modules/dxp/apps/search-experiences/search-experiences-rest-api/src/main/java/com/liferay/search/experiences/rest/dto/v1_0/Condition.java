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
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
@GraphQLName("Condition")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Condition")
public class Condition implements Serializable {

	public static Condition toDTO(String json) {
		return ObjectMapperUtil.readValue(Condition.class, json);
	}

	public static Condition unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Condition.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Condition[] getAllConditions() {
		if (_allConditionsSupplier != null) {
			allConditions = _allConditionsSupplier.get();

			_allConditionsSupplier = null;
		}

		return allConditions;
	}

	public void setAllConditions(Condition[] allConditions) {
		this.allConditions = allConditions;

		_allConditionsSupplier = null;
	}

	@JsonIgnore
	public void setAllConditions(
		UnsafeSupplier<Condition[], Exception> allConditionsUnsafeSupplier) {

		_allConditionsSupplier = () -> {
			try {
				return allConditionsUnsafeSupplier.get();
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
	protected Condition[] allConditions;

	@JsonIgnore
	private Supplier<Condition[]> _allConditionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Condition[] getAnyConditions() {
		if (_anyConditionsSupplier != null) {
			anyConditions = _anyConditionsSupplier.get();

			_anyConditionsSupplier = null;
		}

		return anyConditions;
	}

	public void setAnyConditions(Condition[] anyConditions) {
		this.anyConditions = anyConditions;

		_anyConditionsSupplier = null;
	}

	@JsonIgnore
	public void setAnyConditions(
		UnsafeSupplier<Condition[], Exception> anyConditionsUnsafeSupplier) {

		_anyConditionsSupplier = () -> {
			try {
				return anyConditionsUnsafeSupplier.get();
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
	protected Condition[] anyConditions;

	@JsonIgnore
	private Supplier<Condition[]> _anyConditionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Contains getContains() {
		if (_containsSupplier != null) {
			contains = _containsSupplier.get();

			_containsSupplier = null;
		}

		return contains;
	}

	public void setContains(Contains contains) {
		this.contains = contains;

		_containsSupplier = null;
	}

	@JsonIgnore
	public void setContains(
		UnsafeSupplier<Contains, Exception> containsUnsafeSupplier) {

		_containsSupplier = () -> {
			try {
				return containsUnsafeSupplier.get();
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
	protected Contains contains;

	@JsonIgnore
	private Supplier<Contains> _containsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Equals getEquals() {
		if (_equalsSupplier != null) {
			equals = _equalsSupplier.get();

			_equalsSupplier = null;
		}

		return equals;
	}

	public void setEquals(Equals equals) {
		this.equals = equals;

		_equalsSupplier = null;
	}

	@JsonIgnore
	public void setEquals(
		UnsafeSupplier<Equals, Exception> equalsUnsafeSupplier) {

		_equalsSupplier = () -> {
			try {
				return equalsUnsafeSupplier.get();
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
	protected Equals equals;

	@JsonIgnore
	private Supplier<Equals> _equalsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Exists getExists() {
		if (_existsSupplier != null) {
			exists = _existsSupplier.get();

			_existsSupplier = null;
		}

		return exists;
	}

	public void setExists(Exists exists) {
		this.exists = exists;

		_existsSupplier = null;
	}

	@JsonIgnore
	public void setExists(
		UnsafeSupplier<Exists, Exception> existsUnsafeSupplier) {

		_existsSupplier = () -> {
			try {
				return existsUnsafeSupplier.get();
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
	protected Exists exists;

	@JsonIgnore
	private Supplier<Exists> _existsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public In getIn() {
		if (_inSupplier != null) {
			in = _inSupplier.get();

			_inSupplier = null;
		}

		return in;
	}

	public void setIn(In in) {
		this.in = in;

		_inSupplier = null;
	}

	@JsonIgnore
	public void setIn(UnsafeSupplier<In, Exception> inUnsafeSupplier) {
		_inSupplier = () -> {
			try {
				return inUnsafeSupplier.get();
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
	protected In in;

	@JsonIgnore
	private Supplier<In> _inSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Condition getNot() {
		if (_notSupplier != null) {
			not = _notSupplier.get();

			_notSupplier = null;
		}

		return not;
	}

	public void setNot(Condition not) {
		this.not = not;

		_notSupplier = null;
	}

	@JsonIgnore
	public void setNot(UnsafeSupplier<Condition, Exception> notUnsafeSupplier) {
		_notSupplier = () -> {
			try {
				return notUnsafeSupplier.get();
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
	protected Condition not;

	@JsonIgnore
	private Supplier<Condition> _notSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Range getRange() {
		if (_rangeSupplier != null) {
			range = _rangeSupplier.get();

			_rangeSupplier = null;
		}

		return range;
	}

	public void setRange(Range range) {
		this.range = range;

		_rangeSupplier = null;
	}

	@JsonIgnore
	public void setRange(UnsafeSupplier<Range, Exception> rangeUnsafeSupplier) {
		_rangeSupplier = () -> {
			try {
				return rangeUnsafeSupplier.get();
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
	protected Range range;

	@JsonIgnore
	private Supplier<Range> _rangeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Condition)) {
			return false;
		}

		Condition condition = (Condition)object;

		return Objects.equals(toString(), condition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Condition[] allConditions = getAllConditions();

		if (allConditions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"allConditions\": ");

			sb.append("[");

			for (int i = 0; i < allConditions.length; i++) {
				sb.append(String.valueOf(allConditions[i]));

				if ((i + 1) < allConditions.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Condition[] anyConditions = getAnyConditions();

		if (anyConditions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"anyConditions\": ");

			sb.append("[");

			for (int i = 0; i < anyConditions.length; i++) {
				sb.append(String.valueOf(anyConditions[i]));

				if ((i + 1) < anyConditions.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Contains contains = getContains();

		if (contains != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contains\": ");

			sb.append(String.valueOf(contains));
		}

		Equals equals = getEquals();

		if (equals != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"equals\": ");

			sb.append(String.valueOf(equals));
		}

		Exists exists = getExists();

		if (exists != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"exists\": ");

			sb.append(String.valueOf(exists));
		}

		In in = getIn();

		if (in != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"in\": ");

			sb.append(String.valueOf(in));
		}

		Condition not = getNot();

		if (not != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"not\": ");

			sb.append(String.valueOf(not));
		}

		Range range = getRange();

		if (range != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"range\": ");

			sb.append(String.valueOf(range));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.search.experiences.rest.dto.v1_0.Condition",
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