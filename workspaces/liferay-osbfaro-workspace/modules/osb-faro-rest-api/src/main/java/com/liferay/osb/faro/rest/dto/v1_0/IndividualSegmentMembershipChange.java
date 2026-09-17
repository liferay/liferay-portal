/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.dto.v1_0;

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
	description = "A single change to an individual segment's membership: one individual entering or leaving the segment on a given date, with the individual's name and email resolved. Use `getWorkspaceGroupIndividualSegmentMembershipChangesPage` to list changes for a segment over a date range.",
	value = "IndividualSegmentMembershipChange"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A single change to an individual segment's membership: one individual entering or leaving the segment on a given date, with the individual's name and email resolved. Use `getWorkspaceGroupIndividualSegmentMembershipChangesPage` to list changes for a segment over a date range."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "IndividualSegmentMembershipChange")
public class IndividualSegmentMembershipChange implements Serializable {

	public static IndividualSegmentMembershipChange toDTO(String json) {
		return ObjectMapperUtil.readValue(
			IndividualSegmentMembershipChange.class, json);
	}

	public static IndividualSegmentMembershipChange unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			IndividualSegmentMembershipChange.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When the change happened."
	)
	public Date getDateChanged() {
		if (_dateChangedSupplier != null) {
			dateChanged = _dateChangedSupplier.get();

			_dateChangedSupplier = null;
		}

		return dateChanged;
	}

	public void setDateChanged(Date dateChanged) {
		this.dateChanged = dateChanged;

		_dateChangedSupplier = null;
	}

	@JsonIgnore
	public void setDateChanged(
		UnsafeSupplier<Date, Exception> dateChangedUnsafeSupplier) {

		_dateChangedSupplier = () -> {
			try {
				return dateChangedUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "When the change happened.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateChanged;

	@JsonIgnore
	private Supplier<Date> _dateChangedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When the individual first entered the segment."
	)
	public Date getDateFirst() {
		if (_dateFirstSupplier != null) {
			dateFirst = _dateFirstSupplier.get();

			_dateFirstSupplier = null;
		}

		return dateFirst;
	}

	public void setDateFirst(Date dateFirst) {
		this.dateFirst = dateFirst;

		_dateFirstSupplier = null;
	}

	@JsonIgnore
	public void setDateFirst(
		UnsafeSupplier<Date, Exception> dateFirstUnsafeSupplier) {

		_dateFirstSupplier = () -> {
			try {
				return dateFirstUnsafeSupplier.get();
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
		description = "When the individual first entered the segment."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateFirst;

	@JsonIgnore
	private Supplier<Date> _dateFirstSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "ID of the change record."
	)
	public String getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(String id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<String, Exception> idUnsafeSupplier) {
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

	@GraphQLField(description = "ID of the change record.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String id;

	@JsonIgnore
	private Supplier<String> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Email address of the individual whose membership changed."
	)
	public String getIndividualEmail() {
		if (_individualEmailSupplier != null) {
			individualEmail = _individualEmailSupplier.get();

			_individualEmailSupplier = null;
		}

		return individualEmail;
	}

	public void setIndividualEmail(String individualEmail) {
		this.individualEmail = individualEmail;

		_individualEmailSupplier = null;
	}

	@JsonIgnore
	public void setIndividualEmail(
		UnsafeSupplier<String, Exception> individualEmailUnsafeSupplier) {

		_individualEmailSupplier = () -> {
			try {
				return individualEmailUnsafeSupplier.get();
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
		description = "Email address of the individual whose membership changed."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String individualEmail;

	@JsonIgnore
	private Supplier<String> _individualEmailSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "ID of the individual whose membership changed. Use with `getWorkspaceGroupIndividual` to fetch the individual."
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
		description = "ID of the individual whose membership changed. Use with `getWorkspaceGroupIndividual` to fetch the individual."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String individualId;

	@JsonIgnore
	private Supplier<String> _individualIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Display name of the individual whose membership changed."
	)
	public String getIndividualName() {
		if (_individualNameSupplier != null) {
			individualName = _individualNameSupplier.get();

			_individualNameSupplier = null;
		}

		return individualName;
	}

	public void setIndividualName(String individualName) {
		this.individualName = individualName;

		_individualNameSupplier = null;
	}

	@JsonIgnore
	public void setIndividualName(
		UnsafeSupplier<String, Exception> individualNameUnsafeSupplier) {

		_individualNameSupplier = () -> {
			try {
				return individualNameUnsafeSupplier.get();
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
		description = "Display name of the individual whose membership changed."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String individualName;

	@JsonIgnore
	private Supplier<String> _individualNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "ID of the segment this change belongs to."
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

	@GraphQLField(description = "ID of the segment this change belongs to.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String individualSegmentId;

	@JsonIgnore
	private Supplier<String> _individualSegmentIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Kind of change. ADDED when the individual entered the segment; the analytics engine defines the value used when an individual leaves."
	)
	public String getOperation() {
		if (_operationSupplier != null) {
			operation = _operationSupplier.get();

			_operationSupplier = null;
		}

		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;

		_operationSupplier = null;
	}

	@JsonIgnore
	public void setOperation(
		UnsafeSupplier<String, Exception> operationUnsafeSupplier) {

		_operationSupplier = () -> {
			try {
				return operationUnsafeSupplier.get();
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
		description = "Kind of change. ADDED when the individual entered the segment; the analytics engine defines the value used when an individual leaves."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String operation;

	@JsonIgnore
	private Supplier<String> _operationSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof IndividualSegmentMembershipChange)) {
			return false;
		}

		IndividualSegmentMembershipChange individualSegmentMembershipChange =
			(IndividualSegmentMembershipChange)object;

		return Objects.equals(
			toString(), individualSegmentMembershipChange.toString());
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

		Date dateChanged = getDateChanged();

		if (dateChanged != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateChanged\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateChanged));

			sb.append("\"");
		}

		Date dateFirst = getDateFirst();

		if (dateFirst != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateFirst\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateFirst));

			sb.append("\"");
		}

		String id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(id));

			sb.append("\"");
		}

		String individualEmail = getIndividualEmail();

		if (individualEmail != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"individualEmail\": ");

			sb.append("\"");

			sb.append(_escape(individualEmail));

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

		String individualName = getIndividualName();

		if (individualName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"individualName\": ");

			sb.append("\"");

			sb.append(_escape(individualName));

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

		String operation = getOperation();

		if (operation != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"operation\": ");

			sb.append("\"");

			sb.append(_escape(operation));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.osb.faro.rest.dto.v1_0.IndividualSegmentMembershipChange",
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
// LIFERAY-REST-BUILDER-HASH:2052792626