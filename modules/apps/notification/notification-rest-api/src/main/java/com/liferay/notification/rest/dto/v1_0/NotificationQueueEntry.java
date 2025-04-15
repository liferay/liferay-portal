/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.rest.dto.v1_0;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Gabriel Albuquerque
 * @generated
 */
@Generated("")
@GraphQLName("NotificationQueueEntry")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "NotificationQueueEntry")
public class NotificationQueueEntry implements Serializable {

	public static NotificationQueueEntry toDTO(String json) {
		return ObjectMapperUtil.readValue(NotificationQueueEntry.class, json);
	}

	public static NotificationQueueEntry unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			NotificationQueueEntry.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, Map<String, String>> getActions() {
		if (_actionsSupplier != null) {
			actions = _actionsSupplier.get();

			_actionsSupplier = null;
		}

		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;

		_actionsSupplier = null;
	}

	@JsonIgnore
	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		_actionsSupplier = () -> {
			try {
				return actionsUnsafeSupplier.get();
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
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getBody() {
		if (_bodySupplier != null) {
			body = _bodySupplier.get();

			_bodySupplier = null;
		}

		return body;
	}

	public void setBody(String body) {
		this.body = body;

		_bodySupplier = null;
	}

	@JsonIgnore
	public void setBody(UnsafeSupplier<String, Exception> bodyUnsafeSupplier) {
		_bodySupplier = () -> {
			try {
				return bodyUnsafeSupplier.get();
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
	protected String body;

	@JsonIgnore
	private Supplier<String> _bodySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getFromName() {
		if (_fromNameSupplier != null) {
			fromName = _fromNameSupplier.get();

			_fromNameSupplier = null;
		}

		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;

		_fromNameSupplier = null;
	}

	@JsonIgnore
	public void setFromName(
		UnsafeSupplier<String, Exception> fromNameUnsafeSupplier) {

		_fromNameSupplier = () -> {
			try {
				return fromNameUnsafeSupplier.get();
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
	protected String fromName;

	@JsonIgnore
	private Supplier<String> _fromNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object[] getRecipients() {
		if (_recipientsSupplier != null) {
			recipients = _recipientsSupplier.get();

			_recipientsSupplier = null;
		}

		return recipients;
	}

	public void setRecipients(Object[] recipients) {
		this.recipients = recipients;

		_recipientsSupplier = null;
	}

	@JsonIgnore
	public void setRecipients(
		UnsafeSupplier<Object[], Exception> recipientsUnsafeSupplier) {

		_recipientsSupplier = () -> {
			try {
				return recipientsUnsafeSupplier.get();
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
	protected Object[] recipients;

	@JsonIgnore
	private Supplier<Object[]> _recipientsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getRecipientsSummary() {
		if (_recipientsSummarySupplier != null) {
			recipientsSummary = _recipientsSummarySupplier.get();

			_recipientsSummarySupplier = null;
		}

		return recipientsSummary;
	}

	public void setRecipientsSummary(String recipientsSummary) {
		this.recipientsSummary = recipientsSummary;

		_recipientsSummarySupplier = null;
	}

	@JsonIgnore
	public void setRecipientsSummary(
		UnsafeSupplier<String, Exception> recipientsSummaryUnsafeSupplier) {

		_recipientsSummarySupplier = () -> {
			try {
				return recipientsSummaryUnsafeSupplier.get();
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
	protected String recipientsSummary;

	@JsonIgnore
	private Supplier<String> _recipientsSummarySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getSentDate() {
		if (_sentDateSupplier != null) {
			sentDate = _sentDateSupplier.get();

			_sentDateSupplier = null;
		}

		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;

		_sentDateSupplier = null;
	}

	@JsonIgnore
	public void setSentDate(
		UnsafeSupplier<Date, Exception> sentDateUnsafeSupplier) {

		_sentDateSupplier = () -> {
			try {
				return sentDateUnsafeSupplier.get();
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
	protected Date sentDate;

	@JsonIgnore
	private Supplier<Date> _sentDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getStatus() {
		if (_statusSupplier != null) {
			status = _statusSupplier.get();

			_statusSupplier = null;
		}

		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;

		_statusSupplier = null;
	}

	@JsonIgnore
	public void setStatus(
		UnsafeSupplier<Integer, Exception> statusUnsafeSupplier) {

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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Integer status;

	@JsonIgnore
	private Supplier<Integer> _statusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getSubject() {
		if (_subjectSupplier != null) {
			subject = _subjectSupplier.get();

			_subjectSupplier = null;
		}

		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;

		_subjectSupplier = null;
	}

	@JsonIgnore
	public void setSubject(
		UnsafeSupplier<String, Exception> subjectUnsafeSupplier) {

		_subjectSupplier = () -> {
			try {
				return subjectUnsafeSupplier.get();
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
	protected String subject;

	@JsonIgnore
	private Supplier<String> _subjectSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTriggerBy() {
		if (_triggerBySupplier != null) {
			triggerBy = _triggerBySupplier.get();

			_triggerBySupplier = null;
		}

		return triggerBy;
	}

	public void setTriggerBy(String triggerBy) {
		this.triggerBy = triggerBy;

		_triggerBySupplier = null;
	}

	@JsonIgnore
	public void setTriggerBy(
		UnsafeSupplier<String, Exception> triggerByUnsafeSupplier) {

		_triggerBySupplier = () -> {
			try {
				return triggerByUnsafeSupplier.get();
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
	protected String triggerBy;

	@JsonIgnore
	private Supplier<String> _triggerBySupplier;

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
	public String getTypeLabel() {
		if (_typeLabelSupplier != null) {
			typeLabel = _typeLabelSupplier.get();

			_typeLabelSupplier = null;
		}

		return typeLabel;
	}

	public void setTypeLabel(String typeLabel) {
		this.typeLabel = typeLabel;

		_typeLabelSupplier = null;
	}

	@JsonIgnore
	public void setTypeLabel(
		UnsafeSupplier<String, Exception> typeLabelUnsafeSupplier) {

		_typeLabelSupplier = () -> {
			try {
				return typeLabelUnsafeSupplier.get();
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
	protected String typeLabel;

	@JsonIgnore
	private Supplier<String> _typeLabelSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof NotificationQueueEntry)) {
			return false;
		}

		NotificationQueueEntry notificationQueueEntry =
			(NotificationQueueEntry)object;

		return Objects.equals(toString(), notificationQueueEntry.toString());
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

		Map<String, Map<String, String>> actions = getActions();

		if (actions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(actions));
		}

		String body = getBody();

		if (body != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"body\": ");

			sb.append("\"");

			sb.append(_escape(body));

			sb.append("\"");
		}

		String fromName = getFromName();

		if (fromName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fromName\": ");

			sb.append("\"");

			sb.append(_escape(fromName));

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

		Object[] recipients = getRecipients();

		if (recipients != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"recipients\": ");

			sb.append("[");

			for (int i = 0; i < recipients.length; i++) {
				sb.append("\"");

				sb.append(_escape(recipients[i]));

				sb.append("\"");

				if ((i + 1) < recipients.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String recipientsSummary = getRecipientsSummary();

		if (recipientsSummary != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"recipientsSummary\": ");

			sb.append("\"");

			sb.append(_escape(recipientsSummary));

			sb.append("\"");
		}

		Date sentDate = getSentDate();

		if (sentDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sentDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(sentDate));

			sb.append("\"");
		}

		Integer status = getStatus();

		if (status != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(status);
		}

		String subject = getSubject();

		if (subject != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subject\": ");

			sb.append("\"");

			sb.append(_escape(subject));

			sb.append("\"");
		}

		String triggerBy = getTriggerBy();

		if (triggerBy != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"triggerBy\": ");

			sb.append("\"");

			sb.append(_escape(triggerBy));

			sb.append("\"");
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

		String typeLabel = getTypeLabel();

		if (typeLabel != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"typeLabel\": ");

			sb.append("\"");

			sb.append(_escape(typeLabel));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.notification.rest.dto.v1_0.NotificationQueueEntry",
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