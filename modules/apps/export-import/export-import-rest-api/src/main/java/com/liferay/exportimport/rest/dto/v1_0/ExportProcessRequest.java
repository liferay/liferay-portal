/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
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
		description = "The recipe used to resolve the export window at every run. ALL exports everything, DATE_RANGE exports the window between the start and end dates, resolving a missing end date as the run time, and LAST exports a window of whole hours ending at the run time, derived from the start date. When absent, the type is inferred from the given dates."
	)
	@JsonGetter("dateRangeType")
	@Valid
	public DateRangeType getDateRangeType() {
		if (_dateRangeTypeSupplier != null) {
			dateRangeType = _dateRangeTypeSupplier.get();

			_dateRangeTypeSupplier = null;
		}

		return dateRangeType;
	}

	@JsonIgnore
	public String getDateRangeTypeAsString() {
		DateRangeType dateRangeType = getDateRangeType();

		if (dateRangeType == null) {
			return null;
		}

		return dateRangeType.toString();
	}

	public void setDateRangeType(DateRangeType dateRangeType) {
		this.dateRangeType = dateRangeType;

		_dateRangeTypeSupplier = null;
	}

	@JsonIgnore
	public void setDateRangeType(
		UnsafeSupplier<DateRangeType, Exception> dateRangeTypeUnsafeSupplier) {

		_dateRangeTypeSupplier = () -> {
			try {
				return dateRangeTypeUnsafeSupplier.get();
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
		description = "The recipe used to resolve the export window at every run. ALL exports everything, DATE_RANGE exports the window between the start and end dates, resolving a missing end date as the run time, and LAST exports a window of whole hours ending at the run time, derived from the start date. When absent, the type is inferred from the given dates."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected DateRangeType dateRangeType;

	@JsonIgnore
	private Supplier<DateRangeType> _dateRangeTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether the deletions recorded in the date range are exported, so that an import removes the same items."
	)
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

	@GraphQLField(
		description = "A flag that indicates whether the deletions recorded in the date range are exported, so that an import removes the same items."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean deletions;

	@JsonIgnore
	private Supplier<Boolean> _deletionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The export window's end date."
	)
	public Date getEndDate() {
		if (_endDateSupplier != null) {
			endDate = _endDateSupplier.get();

			_endDateSupplier = null;
		}

		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;

		_endDateSupplier = null;
	}

	@JsonIgnore
	public void setEndDate(
		UnsafeSupplier<Date, Exception> endDateUnsafeSupplier) {

		_endDateSupplier = () -> {
			try {
				return endDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The export window's end date.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date endDate;

	@JsonIgnore
	private Supplier<Date> _endDateSupplier;

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

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The external reference codes of the sites to export in full, as listed by the export preview sites. Applies only to the instance."
	)
	public String[] getSiteExternalReferenceCodes() {
		if (_siteExternalReferenceCodesSupplier != null) {
			siteExternalReferenceCodes =
				_siteExternalReferenceCodesSupplier.get();

			_siteExternalReferenceCodesSupplier = null;
		}

		return siteExternalReferenceCodes;
	}

	public void setSiteExternalReferenceCodes(
		String[] siteExternalReferenceCodes) {

		this.siteExternalReferenceCodes = siteExternalReferenceCodes;

		_siteExternalReferenceCodesSupplier = null;
	}

	@JsonIgnore
	public void setSiteExternalReferenceCodes(
		UnsafeSupplier<String[], Exception>
			siteExternalReferenceCodesUnsafeSupplier) {

		_siteExternalReferenceCodesSupplier = () -> {
			try {
				return siteExternalReferenceCodesUnsafeSupplier.get();
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
		description = "The external reference codes of the sites to export in full, as listed by the export preview sites. Applies only to the instance."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] siteExternalReferenceCodes;

	@JsonIgnore
	private Supplier<String[]> _siteExternalReferenceCodesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The export window's start date."
	)
	public Date getStartDate() {
		if (_startDateSupplier != null) {
			startDate = _startDateSupplier.get();

			_startDateSupplier = null;
		}

		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;

		_startDateSupplier = null;
	}

	@JsonIgnore
	public void setStartDate(
		UnsafeSupplier<Date, Exception> startDateUnsafeSupplier) {

		_startDateSupplier = () -> {
			try {
				return startDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The export window's start date.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date startDate;

	@JsonIgnore
	private Supplier<Date> _startDateSupplier;

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

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		Boolean comments = getComments();

		if (comments != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"comments\": ");

			sb.append(comments);
		}

		DateRangeType dateRangeType = getDateRangeType();

		if (dateRangeType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateRangeType\": ");

			sb.append("\"");
			sb.append(dateRangeType);
			sb.append("\"");
		}

		Boolean deletions = getDeletions();

		if (deletions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deletions\": ");

			sb.append(deletions);
		}

		Date endDate = getEndDate();

		if (endDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"endDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(endDate));

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

		String[] siteExternalReferenceCodes = getSiteExternalReferenceCodes();

		if (siteExternalReferenceCodes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteExternalReferenceCodes\": ");

			sb.append("[");

			for (int i = 0; i < siteExternalReferenceCodes.length; i++) {
				sb.append("\"");

				sb.append(_escape(siteExternalReferenceCodes[i]));

				sb.append("\"");

				if ((i + 1) < siteExternalReferenceCodes.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Date startDate = getStartDate();

		if (startDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"startDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(startDate));

			sb.append("\"");
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

	@GraphQLName("DateRangeType")
	public static enum DateRangeType {

		ALL("ALL"), DATE_RANGE("DATE_RANGE"), LAST("LAST");

		@JsonCreator
		public static DateRangeType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (DateRangeType dateRangeType : values()) {
				if (Objects.equals(dateRangeType.getValue(), value)) {
					return dateRangeType;
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

		private DateRangeType(String value) {
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
// LIFERAY-REST-BUILDER-HASH:1645804438