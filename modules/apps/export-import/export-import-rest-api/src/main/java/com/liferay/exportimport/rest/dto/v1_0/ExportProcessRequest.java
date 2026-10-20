/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.dto.v1_0;

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

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
@GraphQLName("ExportProcessRequest")
@io.swagger.v3.oas.annotations.media.Schema(requiredProperties = {"name"})
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ExportProcessRequest")
public class ExportProcessRequest implements Serializable {

	public static ExportProcessRequest toDTO(String json) {
		return ObjectMapperUtil.readValue(ExportProcessRequest.class, json);
	}

	public static ExportProcessRequest unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			ExportProcessRequest.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getComments() {
		if (_commentsSupplier != null) {
			comments = _commentsSupplier.get();

			_commentsSupplier = null;
		}

		return comments;
	}

	public void setComments(Boolean comments) {
		this.comments = comments;

		_commentsSupplier = null;
	}

	@JsonIgnore
	public void setComments(
		UnsafeSupplier<Boolean, Exception> commentsUnsafeSupplier) {

		_commentsSupplier = () -> {
			try {
				return commentsUnsafeSupplier.get();
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
	protected Boolean comments;

	@JsonIgnore
	private Supplier<Boolean> _commentsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The dates that select the content, resolved again on every run. Defaults to all the content."
	)
	@Valid
	public DateFilter getDateFilter() {
		if (_dateFilterSupplier != null) {
			dateFilter = _dateFilterSupplier.get();

			_dateFilterSupplier = null;
		}

		return dateFilter;
	}

	public void setDateFilter(DateFilter dateFilter) {
		this.dateFilter = dateFilter;

		_dateFilterSupplier = null;
	}

	@JsonIgnore
	public void setDateFilter(
		UnsafeSupplier<DateFilter, Exception> dateFilterUnsafeSupplier) {

		_dateFilterSupplier = () -> {
			try {
				return dateFilterUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The dates that select the content, resolved again on every run. Defaults to all the content."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected DateFilter dateFilter;

	@JsonIgnore
	private Supplier<DateFilter> _dateFilterSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getDeletions() {
		if (_deletionsSupplier != null) {
			deletions = _deletionsSupplier.get();

			_deletionsSupplier = null;
		}

		return deletions;
	}

	public void setDeletions(Boolean deletions) {
		this.deletions = deletions;

		_deletionsSupplier = null;
	}

	@JsonIgnore
	public void setDeletions(
		UnsafeSupplier<Boolean, Exception> deletionsUnsafeSupplier) {

		_deletionsSupplier = () -> {
			try {
				return deletionsUnsafeSupplier.get();
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
	protected Boolean deletions;

	@JsonIgnore
	private Supplier<Boolean> _deletionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The export process's name, also used as the file name."
	)
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

	@GraphQLField(
		description = "The export process's name, also used as the file name."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getPermissions() {
		if (_permissionsSupplier != null) {
			permissions = _permissionsSupplier.get();

			_permissionsSupplier = null;
		}

		return permissions;
	}

	public void setPermissions(Boolean permissions) {
		this.permissions = permissions;

		_permissionsSupplier = null;
	}

	@JsonIgnore
	public void setPermissions(
		UnsafeSupplier<Boolean, Exception> permissionsUnsafeSupplier) {

		_permissionsSupplier = () -> {
			try {
				return permissionsUnsafeSupplier.get();
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
	protected Boolean permissions;

	@JsonIgnore
	private Supplier<Boolean> _permissionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The portlet data handlers to export, taken from the export preview."
	)
	@Valid
	public PortletDataHandler[] getPortletDataHandlers() {
		if (_portletDataHandlersSupplier != null) {
			portletDataHandlers = _portletDataHandlersSupplier.get();

			_portletDataHandlersSupplier = null;
		}

		return portletDataHandlers;
	}

	public void setPortletDataHandlers(
		PortletDataHandler[] portletDataHandlers) {

		this.portletDataHandlers = portletDataHandlers;

		_portletDataHandlersSupplier = null;
	}

	@JsonIgnore
	public void setPortletDataHandlers(
		UnsafeSupplier<PortletDataHandler[], Exception>
			portletDataHandlersUnsafeSupplier) {

		_portletDataHandlersSupplier = () -> {
			try {
				return portletDataHandlersUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The portlet data handlers to export, taken from the export preview."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PortletDataHandler[] portletDataHandlers;

	@JsonIgnore
	private Supplier<PortletDataHandler[]> _portletDataHandlersSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getRatings() {
		if (_ratingsSupplier != null) {
			ratings = _ratingsSupplier.get();

			_ratingsSupplier = null;
		}

		return ratings;
	}

	public void setRatings(Boolean ratings) {
		this.ratings = ratings;

		_ratingsSupplier = null;
	}

	@JsonIgnore
	public void setRatings(
		UnsafeSupplier<Boolean, Exception> ratingsUnsafeSupplier) {

		_ratingsSupplier = () -> {
			try {
				return ratingsUnsafeSupplier.get();
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
	protected Boolean ratings;

	@JsonIgnore
	private Supplier<Boolean> _ratingsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ExportProcessRequest)) {
			return false;
		}

		ExportProcessRequest exportProcessRequest =
			(ExportProcessRequest)object;

		return Objects.equals(toString(), exportProcessRequest.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean comments = getComments();

		if (comments != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"comments\": ");

			sb.append(comments);
		}

		DateFilter dateFilter = getDateFilter();

		if (dateFilter != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateFilter\": ");

			sb.append(String.valueOf(dateFilter));
		}

		Boolean deletions = getDeletions();

		if (deletions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deletions\": ");

			sb.append(deletions);
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

		Boolean permissions = getPermissions();

		if (permissions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append(permissions);
		}

		PortletDataHandler[] portletDataHandlers = getPortletDataHandlers();

		if (portletDataHandlers != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"portletDataHandlers\": ");

			sb.append("[");

			for (int i = 0; i < portletDataHandlers.length; i++) {
				sb.append(String.valueOf(portletDataHandlers[i]));

				if ((i + 1) < portletDataHandlers.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean ratings = getRatings();

		if (ratings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ratings\": ");

			sb.append(ratings);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.exportimport.rest.dto.v1_0.ExportProcessRequest",
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

	private static String _toJSON(Object value) {
		if (value instanceof Collection) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray((Collection<?>)value));
		}
		else if (value instanceof Map) {
			return String.valueOf(
				JSONFactoryUtil.createJSONObject((Map<?, ?>)value));
		}
		else if (value instanceof Object[]) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray(
					Arrays.asList((Object[])value)));
		}
		else if (value instanceof String) {
			return StringBundler.concat("\"", _escape(value), "\"");
		}

		return String.valueOf(value);
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:-2122297828