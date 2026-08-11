/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.headless.delivery.dto.v1_0.Creator;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
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
@GraphQLName("ScheduledPublishProcess")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ScheduledPublishProcess")
public class ScheduledPublishProcess implements Serializable {

	public static ScheduledPublishProcess toDTO(String json) {
		return ObjectMapperUtil.readValue(ScheduledPublishProcess.class, json);
	}

	public static ScheduledPublishProcess unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			ScheduledPublishProcess.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Creator getCreator() {
		if (_creatorSupplier != null) {
			creator = _creatorSupplier.get();

			_creatorSupplier = null;
		}

		return creator;
	}

	public void setCreator(Creator creator) {
		this.creator = creator;

		_creatorSupplier = null;
	}

	@JsonIgnore
	public void setCreator(
		UnsafeSupplier<Creator, Exception> creatorUnsafeSupplier) {

		_creatorSupplier = () -> {
			try {
				return creatorUnsafeSupplier.get();
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
	protected Creator creator;

	@JsonIgnore
	private Supplier<Creator> _creatorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The cron expression that fires the scheduled publication."
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

	@GraphQLField(
		description = "The cron expression that fires the scheduled publication."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String cronExpression;

	@JsonIgnore
	private Supplier<String> _cronExpressionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The scheduled publish process's creation date."
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

	@GraphQLField(
		description = "The scheduled publish process's creation date."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The scheduled publish process's ID."
	)
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

	@GraphQLField(description = "The scheduled publish process's ID.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The scheduled publish process's name."
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

	@GraphQLField(description = "The scheduled publish process's name.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The next date the scheduled publication fires."
	)
	public Date getNextFireDate() {
		if (_nextFireDateSupplier != null) {
			nextFireDate = _nextFireDateSupplier.get();

			_nextFireDateSupplier = null;
		}

		return nextFireDate;
	}

	public void setNextFireDate(Date nextFireDate) {
		this.nextFireDate = nextFireDate;

		_nextFireDateSupplier = null;
	}

	@JsonIgnore
	public void setNextFireDate(
		UnsafeSupplier<Date, Exception> nextFireDateUnsafeSupplier) {

		_nextFireDateSupplier = () -> {
			try {
				return nextFireDateUnsafeSupplier.get();
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
		description = "The next date the scheduled publication fires."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date nextFireDate;

	@JsonIgnore
	private Supplier<Date> _nextFireDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The raw export import parameters the scheduled publication was created with."
	)
	@Valid
	public Object getPublishParameters() {
		if (_publishParametersSupplier != null) {
			publishParameters = _publishParametersSupplier.get();

			_publishParametersSupplier = null;
		}

		return publishParameters;
	}

	public void setPublishParameters(Object publishParameters) {
		this.publishParameters = publishParameters;

		_publishParametersSupplier = null;
	}

	@JsonIgnore
	public void setPublishParameters(
		UnsafeSupplier<Object, Exception> publishParametersUnsafeSupplier) {

		_publishParametersSupplier = () -> {
			try {
				return publishParametersUnsafeSupplier.get();
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
		description = "The raw export import parameters the scheduled publication was created with."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Object publishParameters;

	@JsonIgnore
	private Supplier<Object> _publishParametersSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The date the scheduled publication stops firing."
	)
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

	@GraphQLField(
		description = "The date the scheduled publication stops firing."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date scheduleEndDate;

	@JsonIgnore
	private Supplier<Date> _scheduleEndDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The date the scheduled publication starts firing."
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
		description = "The date the scheduled publication starts firing."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date scheduleStartDate;

	@JsonIgnore
	private Supplier<Date> _scheduleStartDateSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ScheduledPublishProcess)) {
			return false;
		}

		ScheduledPublishProcess scheduledPublishProcess =
			(ScheduledPublishProcess)object;

		return Objects.equals(toString(), scheduledPublishProcess.toString());
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

		Creator creator = getCreator();

		if (creator != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(creator);
		}

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

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
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

		Date nextFireDate = getNextFireDate();

		if (nextFireDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nextFireDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(nextFireDate));

			sb.append("\"");
		}

		Object publishParameters = getPublishParameters();

		if (publishParameters != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"publishParameters\": ");

			if (publishParameters instanceof Collection) {
				sb.append(
					JSONFactoryUtil.createJSONArray(
						(Collection<?>)publishParameters));
			}
			else if (publishParameters instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject(
						(Map<?, ?>)publishParameters));
			}
			else if (publishParameters instanceof Object[]) {
				sb.append(
					JSONFactoryUtil.createJSONArray(
						Arrays.asList((Object[])publishParameters)));
			}
			else if (publishParameters instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)publishParameters));
				sb.append("\"");
			}
			else {
				sb.append(publishParameters);
			}
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

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.exportimport.rest.dto.v1_0.ScheduledPublishProcess",
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
// LIFERAY-REST-BUILDER-HASH:1075655950