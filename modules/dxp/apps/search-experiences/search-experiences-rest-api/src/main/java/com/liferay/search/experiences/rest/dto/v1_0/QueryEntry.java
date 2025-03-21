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
@GraphQLName("QueryEntry")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "QueryEntry")
public class QueryEntry implements Serializable {

	public static QueryEntry toDTO(String json) {
		return ObjectMapperUtil.readValue(QueryEntry.class, json);
	}

	public static QueryEntry unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(QueryEntry.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Clause[] getClauses() {
		if (_clausesSupplier != null) {
			clauses = _clausesSupplier.get();

			_clausesSupplier = null;
		}

		return clauses;
	}

	public void setClauses(Clause[] clauses) {
		this.clauses = clauses;

		_clausesSupplier = null;
	}

	@JsonIgnore
	public void setClauses(
		UnsafeSupplier<Clause[], Exception> clausesUnsafeSupplier) {

		_clausesSupplier = () -> {
			try {
				return clausesUnsafeSupplier.get();
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
	protected Clause[] clauses;

	@JsonIgnore
	private Supplier<Clause[]> _clausesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Condition getCondition() {
		if (_conditionSupplier != null) {
			condition = _conditionSupplier.get();

			_conditionSupplier = null;
		}

		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;

		_conditionSupplier = null;
	}

	@JsonIgnore
	public void setCondition(
		UnsafeSupplier<Condition, Exception> conditionUnsafeSupplier) {

		_conditionSupplier = () -> {
			try {
				return conditionUnsafeSupplier.get();
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
	protected Condition condition;

	@JsonIgnore
	private Supplier<Condition> _conditionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getEnabled() {
		if (_enabledSupplier != null) {
			enabled = _enabledSupplier.get();

			_enabledSupplier = null;
		}

		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;

		_enabledSupplier = null;
	}

	@JsonIgnore
	public void setEnabled(
		UnsafeSupplier<Boolean, Exception> enabledUnsafeSupplier) {

		_enabledSupplier = () -> {
			try {
				return enabledUnsafeSupplier.get();
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
	protected Boolean enabled;

	@JsonIgnore
	private Supplier<Boolean> _enabledSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Clause[] getPostFilterClauses() {
		if (_postFilterClausesSupplier != null) {
			postFilterClauses = _postFilterClausesSupplier.get();

			_postFilterClausesSupplier = null;
		}

		return postFilterClauses;
	}

	public void setPostFilterClauses(Clause[] postFilterClauses) {
		this.postFilterClauses = postFilterClauses;

		_postFilterClausesSupplier = null;
	}

	@JsonIgnore
	public void setPostFilterClauses(
		UnsafeSupplier<Clause[], Exception> postFilterClausesUnsafeSupplier) {

		_postFilterClausesSupplier = () -> {
			try {
				return postFilterClausesUnsafeSupplier.get();
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
	protected Clause[] postFilterClauses;

	@JsonIgnore
	private Supplier<Clause[]> _postFilterClausesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Rescore[] getRescores() {
		if (_rescoresSupplier != null) {
			rescores = _rescoresSupplier.get();

			_rescoresSupplier = null;
		}

		return rescores;
	}

	public void setRescores(Rescore[] rescores) {
		this.rescores = rescores;

		_rescoresSupplier = null;
	}

	@JsonIgnore
	public void setRescores(
		UnsafeSupplier<Rescore[], Exception> rescoresUnsafeSupplier) {

		_rescoresSupplier = () -> {
			try {
				return rescoresUnsafeSupplier.get();
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
	protected Rescore[] rescores;

	@JsonIgnore
	private Supplier<Rescore[]> _rescoresSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof QueryEntry)) {
			return false;
		}

		QueryEntry queryEntry = (QueryEntry)object;

		return Objects.equals(toString(), queryEntry.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Clause[] clauses = getClauses();

		if (clauses != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"clauses\": ");

			sb.append("[");

			for (int i = 0; i < clauses.length; i++) {
				sb.append(String.valueOf(clauses[i]));

				if ((i + 1) < clauses.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Condition condition = getCondition();

		if (condition != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"condition\": ");

			sb.append(String.valueOf(condition));
		}

		Boolean enabled = getEnabled();

		if (enabled != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enabled\": ");

			sb.append(enabled);
		}

		Clause[] postFilterClauses = getPostFilterClauses();

		if (postFilterClauses != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"postFilterClauses\": ");

			sb.append("[");

			for (int i = 0; i < postFilterClauses.length; i++) {
				sb.append(String.valueOf(postFilterClauses[i]));

				if ((i + 1) < postFilterClauses.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Rescore[] rescores = getRescores();

		if (rescores != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"rescores\": ");

			sb.append("[");

			for (int i = 0; i < rescores.length; i++) {
				sb.append(String.valueOf(rescores[i]));

				if ((i + 1) < rescores.length) {
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
		defaultValue = "com.liferay.search.experiences.rest.dto.v1_0.QueryEntry",
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