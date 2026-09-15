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
	description = "A single browsing session by one individual, with the events recorded during it. Use `getWorkspaceGroupChannelAccountUserSessionsPage` or `getWorkspaceGroupChannelIndividualUserSessionsPage` to list the sessions of an account's individuals or of one individual.",
	value = "UserSession"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A single browsing session by one individual, with the events recorded during it. Use `getWorkspaceGroupChannelAccountUserSessionsPage` or `getWorkspaceGroupChannelIndividualUserSessionsPage` to list the sessions of an account's individuals or of one individual."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "UserSession")
public class UserSession implements Serializable {

	public static UserSession toDTO(String json) {
		return ObjectMapperUtil.readValue(UserSession.class, json);
	}

	public static UserSession unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(UserSession.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "True if the individual became known during this session."
	)
	public Boolean getBecameKnown() {
		if (_becameKnownSupplier != null) {
			becameKnown = _becameKnownSupplier.get();

			_becameKnownSupplier = null;
		}

		return becameKnown;
	}

	public void setBecameKnown(Boolean becameKnown) {
		this.becameKnown = becameKnown;

		_becameKnownSupplier = null;
	}

	@JsonIgnore
	public void setBecameKnown(
		UnsafeSupplier<Boolean, Exception> becameKnownUnsafeSupplier) {

		_becameKnownSupplier = () -> {
			try {
				return becameKnownUnsafeSupplier.get();
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
		description = "True if the individual became known during this session."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Boolean becameKnown;

	@JsonIgnore
	private Supplier<Boolean> _becameKnownSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Browser used during the session."
	)
	public String getBrowserName() {
		if (_browserNameSupplier != null) {
			browserName = _browserNameSupplier.get();

			_browserNameSupplier = null;
		}

		return browserName;
	}

	public void setBrowserName(String browserName) {
		this.browserName = browserName;

		_browserNameSupplier = null;
	}

	@JsonIgnore
	public void setBrowserName(
		UnsafeSupplier<String, Exception> browserNameUnsafeSupplier) {

		_browserNameSupplier = () -> {
			try {
				return browserNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Browser used during the session.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String browserName;

	@JsonIgnore
	private Supplier<String> _browserNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When the session ended."
	)
	public Date getCompleteDate() {
		if (_completeDateSupplier != null) {
			completeDate = _completeDateSupplier.get();

			_completeDateSupplier = null;
		}

		return completeDate;
	}

	public void setCompleteDate(Date completeDate) {
		this.completeDate = completeDate;

		_completeDateSupplier = null;
	}

	@JsonIgnore
	public void setCompleteDate(
		UnsafeSupplier<Date, Exception> completeDateUnsafeSupplier) {

		_completeDateSupplier = () -> {
			try {
				return completeDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "When the session ended.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date completeDate;

	@JsonIgnore
	private Supplier<Date> _completeDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When the session started."
	)
	public Date getCreateDate() {
		if (_createDateSupplier != null) {
			createDate = _createDateSupplier.get();

			_createDateSupplier = null;
		}

		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;

		_createDateSupplier = null;
	}

	@JsonIgnore
	public void setCreateDate(
		UnsafeSupplier<Date, Exception> createDateUnsafeSupplier) {

		_createDateSupplier = () -> {
			try {
				return createDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "When the session started.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date createDate;

	@JsonIgnore
	private Supplier<Date> _createDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Device type used during the session (e.g. 'Desktop', 'Smartphone')."
	)
	public String getDeviceType() {
		if (_deviceTypeSupplier != null) {
			deviceType = _deviceTypeSupplier.get();

			_deviceTypeSupplier = null;
		}

		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;

		_deviceTypeSupplier = null;
	}

	@JsonIgnore
	public void setDeviceType(
		UnsafeSupplier<String, Exception> deviceTypeUnsafeSupplier) {

		_deviceTypeSupplier = () -> {
			try {
				return deviceTypeUnsafeSupplier.get();
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
		description = "Device type used during the session (e.g. 'Desktop', 'Smartphone')."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String deviceType;

	@JsonIgnore
	private Supplier<String> _deviceTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Events recorded during the session, oldest first."
	)
	@Valid
	public Event[] getEvents() {
		if (_eventsSupplier != null) {
			events = _eventsSupplier.get();

			_eventsSupplier = null;
		}

		return events;
	}

	public void setEvents(Event[] events) {
		this.events = events;

		_eventsSupplier = null;
	}

	@JsonIgnore
	public void setEvents(
		UnsafeSupplier<Event[], Exception> eventsUnsafeSupplier) {

		_eventsSupplier = () -> {
			try {
				return eventsUnsafeSupplier.get();
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
		description = "Events recorded during the session, oldest first."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Event[] events;

	@JsonIgnore
	private Supplier<Event[]> _eventsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof UserSession)) {
			return false;
		}

		UserSession userSession = (UserSession)object;

		return Objects.equals(toString(), userSession.toString());
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

		Boolean becameKnown = getBecameKnown();

		if (becameKnown != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"becameKnown\": ");

			sb.append(becameKnown);
		}

		String browserName = getBrowserName();

		if (browserName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"browserName\": ");

			sb.append("\"");

			sb.append(_escape(browserName));

			sb.append("\"");
		}

		Date completeDate = getCompleteDate();

		if (completeDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"completeDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(completeDate));

			sb.append("\"");
		}

		Date createDate = getCreateDate();

		if (createDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"createDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(createDate));

			sb.append("\"");
		}

		String deviceType = getDeviceType();

		if (deviceType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deviceType\": ");

			sb.append("\"");

			sb.append(_escape(deviceType));

			sb.append("\"");
		}

		Event[] events = getEvents();

		if (events != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"events\": ");

			sb.append("[");

			for (int i = 0; i < events.length; i++) {
				sb.append(String.valueOf(events[i]));

				if ((i + 1) < events.length) {
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
		defaultValue = "com.liferay.osb.faro.rest.dto.v1_0.UserSession",
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
// LIFERAY-REST-BUILDER-HASH:384962784