/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.dto.v1_0;

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

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "A single individual's membership in an individual segment. Captures when the Individual entered the segment and (if applicable) when they exited.",
	value = "IndividualSegmentMembership"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A single individual's membership in an individual segment. Captures when the Individual entered the segment and (if applicable) when they exited."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "IndividualSegmentMembership")
public class IndividualSegmentMembership implements Serializable {

	public static IndividualSegmentMembership toDTO(String json) {
		return ObjectMapperUtil.readValue(
			IndividualSegmentMembership.class, json);
	}

	public static IndividualSegmentMembership unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			IndividualSegmentMembership.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When the individual entered the segment."
	)
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

	@GraphQLField(description = "When the individual entered the segment.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When the individual exited the segment. Null if membership is still active."
	)
	public Date getDateRemoved() {
		if (_dateRemovedSupplier != null) {
			dateRemoved = _dateRemovedSupplier.get();

			_dateRemovedSupplier = null;
		}

		return dateRemoved;
	}

	public void setDateRemoved(Date dateRemoved) {
		this.dateRemoved = dateRemoved;

		_dateRemovedSupplier = null;
	}

	@JsonIgnore
	public void setDateRemoved(
		UnsafeSupplier<Date, Exception> dateRemovedUnsafeSupplier) {

		_dateRemovedSupplier = () -> {
			try {
				return dateRemovedUnsafeSupplier.get();
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
		description = "When the individual exited the segment. Null if membership is still active."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateRemoved;

	@JsonIgnore
	private Supplier<Date> _dateRemovedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "ID of the individual whose membership this record describes. Use with `getWorkspaceGroupIndividual` to fetch the individual."
	)
	public String getIndividualId() {
		if (_individualIdSupplier != null) {
			individualId = _individualIdSupplier.get();

			_individualIdSupplier = null;
		}

		return individualId;
	}

	public void setIndividualId(String individualId) {
		this.individualId = individualId;

		_individualIdSupplier = null;
	}

	@JsonIgnore
	public void setIndividualId(
		UnsafeSupplier<String, Exception> individualIdUnsafeSupplier) {

		_individualIdSupplier = () -> {
			try {
				return individualIdUnsafeSupplier.get();
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
		description = "ID of the individual whose membership this record describes. Use with `getWorkspaceGroupIndividual` to fetch the individual."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String individualId;

	@JsonIgnore
	private Supplier<String> _individualIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "ID of the segment this membership belongs to."
	)
	public String getIndividualSegmentId() {
		if (_individualSegmentIdSupplier != null) {
			individualSegmentId = _individualSegmentIdSupplier.get();

			_individualSegmentIdSupplier = null;
		}

		return individualSegmentId;
	}

	public void setIndividualSegmentId(String individualSegmentId) {
		this.individualSegmentId = individualSegmentId;

		_individualSegmentIdSupplier = null;
	}

	@JsonIgnore
	public void setIndividualSegmentId(
		UnsafeSupplier<String, Exception> individualSegmentIdUnsafeSupplier) {

		_individualSegmentIdSupplier = () -> {
			try {
				return individualSegmentIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "ID of the segment this membership belongs to.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String individualSegmentId;

	@JsonIgnore
	private Supplier<String> _individualSegmentIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Membership status. ACTIVE means currently a member; INACTIVE means formerly a member."
	)
	@JsonGetter("status")
	@Valid
	public Status getStatus() {
		if (_statusSupplier != null) {
			status = _statusSupplier.get();

			_statusSupplier = null;
		}

		return status;
	}

	@JsonIgnore
	public String getStatusAsString() {
		Status status = getStatus();

		if (status == null) {
			return null;
		}

		return status.toString();
	}

	public void setStatus(Status status) {
		this.status = status;

		_statusSupplier = null;
	}

	@JsonIgnore
	public void setStatus(
		UnsafeSupplier<Status, Exception> statusUnsafeSupplier) {

		_statusSupplier = () -> {
			try {
				return statusUnsafeSupplier.get();
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
		description = "Membership status. ACTIVE means currently a member; INACTIVE means formerly a member."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Status status;

	@JsonIgnore
	private Supplier<Status> _statusSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof IndividualSegmentMembership)) {
			return false;
		}

		IndividualSegmentMembership individualSegmentMembership =
			(IndividualSegmentMembership)object;

		return Objects.equals(
			toString(), individualSegmentMembership.toString());
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

		Date dateRemoved = getDateRemoved();

		if (dateRemoved != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateRemoved\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateRemoved));

			sb.append("\"");
		}

		String individualId = getIndividualId();

		if (individualId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"individualId\": ");

			sb.append("\"");

			sb.append(_escape(individualId));

			sb.append("\"");
		}

		String individualSegmentId = getIndividualSegmentId();

		if (individualSegmentId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"individualSegmentId\": ");

			sb.append("\"");

			sb.append(_escape(individualSegmentId));

			sb.append("\"");
		}

		Status status = getStatus();

		if (status != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");
			sb.append(status);
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.osb.faro.rest.dto.v1_0.IndividualSegmentMembership",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Status")
	public static enum Status {

		ACTIVE("ACTIVE"), INACTIVE("INACTIVE");

		@JsonCreator
		public static Status create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Status status : values()) {
				if (Objects.equals(status.getValue(), value)) {
					return status;
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

		private Status(String value) {
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
// LIFERAY-REST-BUILDER-HASH:1050858682