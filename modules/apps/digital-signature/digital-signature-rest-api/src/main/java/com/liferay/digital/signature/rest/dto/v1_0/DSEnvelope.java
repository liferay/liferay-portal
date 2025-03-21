/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.rest.dto.v1_0;

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
 * @author José Abelenda
 * @generated
 */
@Generated("")
@GraphQLName("DSEnvelope")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "DSEnvelope")
public class DSEnvelope implements Serializable {

	public static DSEnvelope toDTO(String json) {
		return ObjectMapperUtil.readValue(DSEnvelope.class, json);
	}

	public static DSEnvelope unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(DSEnvelope.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getDateModified() {
		if (_dateModifiedSupplier != null) {
			dateModified = _dateModifiedSupplier.get();

			_dateModifiedSupplier = null;
		}

		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;

		_dateModifiedSupplier = null;
	}

	@JsonIgnore
	public void setDateModified(
		UnsafeSupplier<Date, Exception> dateModifiedUnsafeSupplier) {

		_dateModifiedSupplier = () -> {
			try {
				return dateModifiedUnsafeSupplier.get();
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
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public DSDocument[] getDsDocument() {
		if (_dsDocumentSupplier != null) {
			dsDocument = _dsDocumentSupplier.get();

			_dsDocumentSupplier = null;
		}

		return dsDocument;
	}

	public void setDsDocument(DSDocument[] dsDocument) {
		this.dsDocument = dsDocument;

		_dsDocumentSupplier = null;
	}

	@JsonIgnore
	public void setDsDocument(
		UnsafeSupplier<DSDocument[], Exception> dsDocumentUnsafeSupplier) {

		_dsDocumentSupplier = () -> {
			try {
				return dsDocumentUnsafeSupplier.get();
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
	protected DSDocument[] dsDocument;

	@JsonIgnore
	private Supplier<DSDocument[]> _dsDocumentSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public DSRecipient[] getDsRecipient() {
		if (_dsRecipientSupplier != null) {
			dsRecipient = _dsRecipientSupplier.get();

			_dsRecipientSupplier = null;
		}

		return dsRecipient;
	}

	public void setDsRecipient(DSRecipient[] dsRecipient) {
		this.dsRecipient = dsRecipient;

		_dsRecipientSupplier = null;
	}

	@JsonIgnore
	public void setDsRecipient(
		UnsafeSupplier<DSRecipient[], Exception> dsRecipientUnsafeSupplier) {

		_dsRecipientSupplier = () -> {
			try {
				return dsRecipientUnsafeSupplier.get();
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
	protected DSRecipient[] dsRecipient;

	@JsonIgnore
	private Supplier<DSRecipient[]> _dsRecipientSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getEmailBlurb() {
		if (_emailBlurbSupplier != null) {
			emailBlurb = _emailBlurbSupplier.get();

			_emailBlurbSupplier = null;
		}

		return emailBlurb;
	}

	public void setEmailBlurb(String emailBlurb) {
		this.emailBlurb = emailBlurb;

		_emailBlurbSupplier = null;
	}

	@JsonIgnore
	public void setEmailBlurb(
		UnsafeSupplier<String, Exception> emailBlurbUnsafeSupplier) {

		_emailBlurbSupplier = () -> {
			try {
				return emailBlurbUnsafeSupplier.get();
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
	protected String emailBlurb;

	@JsonIgnore
	private Supplier<String> _emailBlurbSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getEmailSubject() {
		if (_emailSubjectSupplier != null) {
			emailSubject = _emailSubjectSupplier.get();

			_emailSubjectSupplier = null;
		}

		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;

		_emailSubjectSupplier = null;
	}

	@JsonIgnore
	public void setEmailSubject(
		UnsafeSupplier<String, Exception> emailSubjectUnsafeSupplier) {

		_emailSubjectSupplier = () -> {
			try {
				return emailSubjectUnsafeSupplier.get();
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
	protected String emailSubject;

	@JsonIgnore
	private Supplier<String> _emailSubjectSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String id;

	@JsonIgnore
	private Supplier<String> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getSenderEmailAddress() {
		if (_senderEmailAddressSupplier != null) {
			senderEmailAddress = _senderEmailAddressSupplier.get();

			_senderEmailAddressSupplier = null;
		}

		return senderEmailAddress;
	}

	public void setSenderEmailAddress(String senderEmailAddress) {
		this.senderEmailAddress = senderEmailAddress;

		_senderEmailAddressSupplier = null;
	}

	@JsonIgnore
	public void setSenderEmailAddress(
		UnsafeSupplier<String, Exception> senderEmailAddressUnsafeSupplier) {

		_senderEmailAddressSupplier = () -> {
			try {
				return senderEmailAddressUnsafeSupplier.get();
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
	protected String senderEmailAddress;

	@JsonIgnore
	private Supplier<String> _senderEmailAddressSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getSiteId() {
		if (_siteIdSupplier != null) {
			siteId = _siteIdSupplier.get();

			_siteIdSupplier = null;
		}

		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;

		_siteIdSupplier = null;
	}

	@JsonIgnore
	public void setSiteId(
		UnsafeSupplier<Long, Exception> siteIdUnsafeSupplier) {

		_siteIdSupplier = () -> {
			try {
				return siteIdUnsafeSupplier.get();
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
	protected Long siteId;

	@JsonIgnore
	private Supplier<Long> _siteIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getStatus() {
		if (_statusSupplier != null) {
			status = _statusSupplier.get();

			_statusSupplier = null;
		}

		return status;
	}

	public void setStatus(String status) {
		this.status = status;

		_statusSupplier = null;
	}

	@JsonIgnore
	public void setStatus(
		UnsafeSupplier<String, Exception> statusUnsafeSupplier) {

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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String status;

	@JsonIgnore
	private Supplier<String> _statusSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DSEnvelope)) {
			return false;
		}

		DSEnvelope dsEnvelope = (DSEnvelope)object;

		return Objects.equals(toString(), dsEnvelope.toString());
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

		Date dateModified = getDateModified();

		if (dateModified != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateModified));

			sb.append("\"");
		}

		DSDocument[] dsDocument = getDsDocument();

		if (dsDocument != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dsDocument\": ");

			sb.append("[");

			for (int i = 0; i < dsDocument.length; i++) {
				sb.append(String.valueOf(dsDocument[i]));

				if ((i + 1) < dsDocument.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		DSRecipient[] dsRecipient = getDsRecipient();

		if (dsRecipient != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dsRecipient\": ");

			sb.append("[");

			for (int i = 0; i < dsRecipient.length; i++) {
				sb.append(String.valueOf(dsRecipient[i]));

				if ((i + 1) < dsRecipient.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String emailBlurb = getEmailBlurb();

		if (emailBlurb != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emailBlurb\": ");

			sb.append("\"");

			sb.append(_escape(emailBlurb));

			sb.append("\"");
		}

		String emailSubject = getEmailSubject();

		if (emailSubject != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emailSubject\": ");

			sb.append("\"");

			sb.append(_escape(emailSubject));

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

		String senderEmailAddress = getSenderEmailAddress();

		if (senderEmailAddress != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"senderEmailAddress\": ");

			sb.append("\"");

			sb.append(_escape(senderEmailAddress));

			sb.append("\"");
		}

		Long siteId = getSiteId();

		if (siteId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(siteId);
		}

		String status = getStatus();

		if (status != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");

			sb.append(_escape(status));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.digital.signature.rest.dto.v1_0.DSEnvelope",
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