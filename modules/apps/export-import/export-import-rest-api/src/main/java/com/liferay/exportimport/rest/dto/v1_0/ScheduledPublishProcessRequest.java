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
import jakarta.validation.constraints.NotNull;

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
@GraphQLName("ScheduledPublishProcessRequest")
@io.swagger.v3.oas.annotations.media.Schema(
	requiredProperties = {"cronExpression", "publishProcessRequest"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ScheduledPublishProcessRequest")
public class ScheduledPublishProcessRequest implements Serializable {

	public static ScheduledPublishProcessRequest toDTO(String json) {
		return ObjectMapperUtil.readValue(
			ScheduledPublishProcessRequest.class, json);
	}

	public static ScheduledPublishProcessRequest unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			ScheduledPublishProcessRequest.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The cron expression the publication fires on."
	)
	public String getCronExpression() {
		if (_cronExpressionSupplier != null) {
			cronExpression = _cronExpressionSupplier.get();

			_cronExpressionSupplier = null;
		}

		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;

		_cronExpressionSupplier = null;
	}

	@JsonIgnore
	public void setCronExpression(
		UnsafeSupplier<String, Exception> cronExpressionUnsafeSupplier) {

		_cronExpressionSupplier = () -> {
			try {
				return cronExpressionUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The cron expression the publication fires on.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String cronExpression;

	@JsonIgnore
	private Supplier<String> _cronExpressionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The publish process request to run every time the schedule fires."
	)
	@Valid
	public PublishProcessRequest getPublishProcessRequest() {
		if (_publishProcessRequestSupplier != null) {
			publishProcessRequest = _publishProcessRequestSupplier.get();

			_publishProcessRequestSupplier = null;
		}

		return publishProcessRequest;
	}

	public void setPublishProcessRequest(
		PublishProcessRequest publishProcessRequest) {

		this.publishProcessRequest = publishProcessRequest;

		_publishProcessRequestSupplier = null;
	}

	@JsonIgnore
	public void setPublishProcessRequest(
		UnsafeSupplier<PublishProcessRequest, Exception>
			publishProcessRequestUnsafeSupplier) {

		_publishProcessRequestSupplier = () -> {
			try {
				return publishProcessRequestUnsafeSupplier.get();
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
		description = "The publish process request to run every time the schedule fires."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotNull
	protected PublishProcessRequest publishProcessRequest;

	@JsonIgnore
	private Supplier<PublishProcessRequest> _publishProcessRequestSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getScheduleEndDate() {
		if (_scheduleEndDateSupplier != null) {
			scheduleEndDate = _scheduleEndDateSupplier.get();

			_scheduleEndDateSupplier = null;
		}

		return scheduleEndDate;
	}

	public void setScheduleEndDate(Date scheduleEndDate) {
		this.scheduleEndDate = scheduleEndDate;

		_scheduleEndDateSupplier = null;
	}

	@JsonIgnore
	public void setScheduleEndDate(
		UnsafeSupplier<Date, Exception> scheduleEndDateUnsafeSupplier) {

		_scheduleEndDateSupplier = () -> {
			try {
				return scheduleEndDateUnsafeSupplier.get();
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
	protected Date scheduleEndDate;

	@JsonIgnore
	private Supplier<Date> _scheduleEndDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The date the publication starts firing. Defaults to now."
	)
	public Date getScheduleStartDate() {
		if (_scheduleStartDateSupplier != null) {
			scheduleStartDate = _scheduleStartDateSupplier.get();

			_scheduleStartDateSupplier = null;
		}

		return scheduleStartDate;
	}

	public void setScheduleStartDate(Date scheduleStartDate) {
		this.scheduleStartDate = scheduleStartDate;

		_scheduleStartDateSupplier = null;
	}

	@JsonIgnore
	public void setScheduleStartDate(
		UnsafeSupplier<Date, Exception> scheduleStartDateUnsafeSupplier) {

		_scheduleStartDateSupplier = () -> {
			try {
				return scheduleStartDateUnsafeSupplier.get();
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
		description = "The date the publication starts firing. Defaults to now."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date scheduleStartDate;

	@JsonIgnore
	private Supplier<Date> _scheduleStartDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The time zone the cron expression is evaluated in. Defaults to the server time zone."
	)
	public String getTimeZoneId() {
		if (_timeZoneIdSupplier != null) {
			timeZoneId = _timeZoneIdSupplier.get();

			_timeZoneIdSupplier = null;
		}

		return timeZoneId;
	}

	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;

		_timeZoneIdSupplier = null;
	}

	@JsonIgnore
	public void setTimeZoneId(
		UnsafeSupplier<String, Exception> timeZoneIdUnsafeSupplier) {

		_timeZoneIdSupplier = () -> {
			try {
				return timeZoneIdUnsafeSupplier.get();
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
		description = "The time zone the cron expression is evaluated in. Defaults to the server time zone."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String timeZoneId;

	@JsonIgnore
	private Supplier<String> _timeZoneIdSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ScheduledPublishProcessRequest)) {
			return false;
		}

		ScheduledPublishProcessRequest scheduledPublishProcessRequest =
			(ScheduledPublishProcessRequest)object;

		return Objects.equals(
			toString(), scheduledPublishProcessRequest.toString());
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

		String cronExpression = getCronExpression();

		if (cronExpression != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cronExpression\": ");

			sb.append("\"");

			sb.append(_escape(cronExpression));

			sb.append("\"");
		}

		PublishProcessRequest publishProcessRequest =
			getPublishProcessRequest();

		if (publishProcessRequest != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"publishProcessRequest\": ");

			sb.append(String.valueOf(publishProcessRequest));
		}

		Date scheduleEndDate = getScheduleEndDate();

		if (scheduleEndDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scheduleEndDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(scheduleEndDate));

			sb.append("\"");
		}

		Date scheduleStartDate = getScheduleStartDate();

		if (scheduleStartDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scheduleStartDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(scheduleStartDate));

			sb.append("\"");
		}

		String timeZoneId = getTimeZoneId();

		if (timeZoneId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"timeZoneId\": ");

			sb.append("\"");

			sb.append(_escape(timeZoneId));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.exportimport.rest.dto.v1_0.ScheduledPublishProcessRequest",
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
// LIFERAY-REST-BUILDER-HASH:650384456