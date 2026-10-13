/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.rest.dto.v1_0;

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
 * @author José Abelenda
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "A signature request for one or more documents to be signed.",
	value = "SignatureRequest"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A signature request for one or more documents to be signed."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "SignatureRequest")
public class SignatureRequest implements Serializable {

	public static SignatureRequest toDTO(String json) {
		return ObjectMapperUtil.readValue(SignatureRequest.class, json);
	}

	public static SignatureRequest unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(SignatureRequest.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Actions the current user can take on the signature request, such as signing it when it is their turn. Read-only."
	)
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

	@GraphQLField(
		description = "Actions the current user can take on the signature request, such as signing it when it is their turn. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user who created the signature request. Read-only."
	)
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

	@GraphQLField(
		description = "The user who created the signature request. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Creator creator;

	@JsonIgnore
	private Supplier<Creator> _creatorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When the signature request was created. Read-only."
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
		description = "When the signature request was created. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Message of the notification sent to each recipient."
	)
	public String getEmailMessage() {
		if (_emailMessageSupplier != null) {
			emailMessage = _emailMessageSupplier.get();

			_emailMessageSupplier = null;
		}

		return emailMessage;
	}

	public void setEmailMessage(String emailMessage) {
		this.emailMessage = emailMessage;

		_emailMessageSupplier = null;
	}

	@JsonIgnore
	public void setEmailMessage(
		UnsafeSupplier<String, Exception> emailMessageUnsafeSupplier) {

		_emailMessageSupplier = () -> {
			try {
				return emailMessageUnsafeSupplier.get();
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
		description = "Message of the notification sent to each recipient."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String emailMessage;

	@JsonIgnore
	private Supplier<String> _emailMessageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Subject of the notification sent to each recipient."
	)
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

	@GraphQLField(
		description = "Subject of the notification sent to each recipient."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String emailSubject;

	@JsonIgnore
	private Supplier<String> _emailSubjectSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When the provider voids the signature request if it is still unsigned. Read-only; derived from the expiration supplied on creation."
	)
	public Date getExpirationDate() {
		if (_expirationDateSupplier != null) {
			expirationDate = _expirationDateSupplier.get();

			_expirationDateSupplier = null;
		}

		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;

		_expirationDateSupplier = null;
	}

	@JsonIgnore
	public void setExpirationDate(
		UnsafeSupplier<Date, Exception> expirationDateUnsafeSupplier) {

		_expirationDateSupplier = () -> {
			try {
				return expirationDateUnsafeSupplier.get();
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
		description = "When the provider voids the signature request if it is still unsigned. Read-only; derived from the expiration supplied on creation."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date expirationDate;

	@JsonIgnore
	private Supplier<Date> _expirationDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Days until the signature request expires. Zero leaves it open. Supplied on creation; not returned on read."
	)
	public Integer getExpireAfter() {
		if (_expireAfterSupplier != null) {
			expireAfter = _expireAfterSupplier.get();

			_expireAfterSupplier = null;
		}

		return expireAfter;
	}

	public void setExpireAfter(Integer expireAfter) {
		this.expireAfter = expireAfter;

		_expireAfterSupplier = null;
	}

	@JsonIgnore
	public void setExpireAfter(
		UnsafeSupplier<Integer, Exception> expireAfterUnsafeSupplier) {

		_expireAfterSupplier = () -> {
			try {
				return expireAfterUnsafeSupplier.get();
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
		description = "Days until the signature request expires. Zero leaves it open. Supplied on creation; not returned on read."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer expireAfter;

	@JsonIgnore
	private Supplier<Integer> _expireAfterSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The document library file entries to be signed."
	)
	@Valid
	public FileEntryBrief[] getFileEntryBriefs() {
		if (_fileEntryBriefsSupplier != null) {
			fileEntryBriefs = _fileEntryBriefsSupplier.get();

			_fileEntryBriefsSupplier = null;
		}

		return fileEntryBriefs;
	}

	public void setFileEntryBriefs(FileEntryBrief[] fileEntryBriefs) {
		this.fileEntryBriefs = fileEntryBriefs;

		_fileEntryBriefsSupplier = null;
	}

	@JsonIgnore
	public void setFileEntryBriefs(
		UnsafeSupplier<FileEntryBrief[], Exception>
			fileEntryBriefsUnsafeSupplier) {

		_fileEntryBriefsSupplier = () -> {
			try {
				return fileEntryBriefsUnsafeSupplier.get();
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
		description = "The document library file entries to be signed."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected FileEntryBrief[] fileEntryBriefs;

	@JsonIgnore
	private Supplier<FileEntryBrief[]> _fileEntryBriefsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Document library file entries to be signed. Supplied on creation; not returned on read."
	)
	public Long[] getFileEntryIds() {
		if (_fileEntryIdsSupplier != null) {
			fileEntryIds = _fileEntryIdsSupplier.get();

			_fileEntryIdsSupplier = null;
		}

		return fileEntryIds;
	}

	public void setFileEntryIds(Long[] fileEntryIds) {
		this.fileEntryIds = fileEntryIds;

		_fileEntryIdsSupplier = null;
	}

	@JsonIgnore
	public void setFileEntryIds(
		UnsafeSupplier<Long[], Exception> fileEntryIdsUnsafeSupplier) {

		_fileEntryIdsSupplier = () -> {
			try {
				return fileEntryIdsUnsafeSupplier.get();
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
		description = "Document library file entries to be signed. Supplied on creation; not returned on read."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long[] fileEntryIds;

	@JsonIgnore
	private Supplier<Long[]> _fileEntryIdsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "ID of the signature request. Read-only; assigned by the server."
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

	@GraphQLField(
		description = "ID of the signature request. Read-only; assigned by the server."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Name of the signature request. Supplied on creation; not returned on read."
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
		description = "Name of the signature request. Supplied on creation; not returned on read."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether Liferay emails each recipient a link to sign. Defaults to true. Supplied on creation; not returned on read."
	)
	public Boolean getSendNotifications() {
		if (_sendNotificationsSupplier != null) {
			sendNotifications = _sendNotificationsSupplier.get();

			_sendNotificationsSupplier = null;
		}

		return sendNotifications;
	}

	public void setSendNotifications(Boolean sendNotifications) {
		this.sendNotifications = sendNotifications;

		_sendNotificationsSupplier = null;
	}

	@JsonIgnore
	public void setSendNotifications(
		UnsafeSupplier<Boolean, Exception> sendNotificationsUnsafeSupplier) {

		_sendNotificationsSupplier = () -> {
			try {
				return sendNotificationsUnsafeSupplier.get();
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
		description = "Whether Liferay emails each recipient a link to sign. Defaults to true. Supplied on creation; not returned on read."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean sendNotifications;

	@JsonIgnore
	private Supplier<Boolean> _sendNotificationsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The recipients on the signature request. Supply a user ID for each one when creating; every other field is read-only."
	)
	@Valid
	public SignatureRequestRecipient[] getSignatureRequestRecipients() {
		if (_signatureRequestRecipientsSupplier != null) {
			signatureRequestRecipients =
				_signatureRequestRecipientsSupplier.get();

			_signatureRequestRecipientsSupplier = null;
		}

		return signatureRequestRecipients;
	}

	public void setSignatureRequestRecipients(
		SignatureRequestRecipient[] signatureRequestRecipients) {

		this.signatureRequestRecipients = signatureRequestRecipients;

		_signatureRequestRecipientsSupplier = null;
	}

	@JsonIgnore
	public void setSignatureRequestRecipients(
		UnsafeSupplier<SignatureRequestRecipient[], Exception>
			signatureRequestRecipientsUnsafeSupplier) {

		_signatureRequestRecipientsSupplier = () -> {
			try {
				return signatureRequestRecipientsUnsafeSupplier.get();
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
		description = "The recipients on the signature request. Supply a user ID for each one when creating; every other field is read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected SignatureRequestRecipient[] signatureRequestRecipients;

	@JsonIgnore
	private Supplier<SignatureRequestRecipient[]>
		_signatureRequestRecipientsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Status of the signature request as Liferay last recorded it. Read-only."
	)
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

	@GraphQLField(
		description = "Status of the signature request as Liferay last recorded it. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String status;

	@JsonIgnore
	private Supplier<String> _statusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When the status last changed. Read-only."
	)
	public Date getStatusDate() {
		if (_statusDateSupplier != null) {
			statusDate = _statusDateSupplier.get();

			_statusDateSupplier = null;
		}

		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;

		_statusDateSupplier = null;
	}

	@JsonIgnore
	public void setStatusDate(
		UnsafeSupplier<Date, Exception> statusDateUnsafeSupplier) {

		_statusDateSupplier = () -> {
			try {
				return statusDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "When the status last changed. Read-only.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date statusDate;

	@JsonIgnore
	private Supplier<Date> _statusDateSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SignatureRequest)) {
			return false;
		}

		SignatureRequest signatureRequest = (SignatureRequest)object;

		return Objects.equals(toString(), signatureRequest.toString());
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

		Creator creator = getCreator();

		if (creator != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(creator);
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

		String emailMessage = getEmailMessage();

		if (emailMessage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emailMessage\": ");

			sb.append("\"");

			sb.append(_escape(emailMessage));

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

		Date expirationDate = getExpirationDate();

		if (expirationDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expirationDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(expirationDate));

			sb.append("\"");
		}

		Integer expireAfter = getExpireAfter();

		if (expireAfter != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expireAfter\": ");

			sb.append(expireAfter);
		}

		FileEntryBrief[] fileEntryBriefs = getFileEntryBriefs();

		if (fileEntryBriefs != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileEntryBriefs\": ");

			sb.append("[");

			for (int i = 0; i < fileEntryBriefs.length; i++) {
				sb.append(String.valueOf(fileEntryBriefs[i]));

				if ((i + 1) < fileEntryBriefs.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Long[] fileEntryIds = getFileEntryIds();

		if (fileEntryIds != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileEntryIds\": ");

			sb.append("[");

			for (int i = 0; i < fileEntryIds.length; i++) {
				sb.append(fileEntryIds[i]);

				if ((i + 1) < fileEntryIds.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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

		Boolean sendNotifications = getSendNotifications();

		if (sendNotifications != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sendNotifications\": ");

			sb.append(sendNotifications);
		}

		SignatureRequestRecipient[] signatureRequestRecipients =
			getSignatureRequestRecipients();

		if (signatureRequestRecipients != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"signatureRequestRecipients\": ");

			sb.append("[");

			for (int i = 0; i < signatureRequestRecipients.length; i++) {
				sb.append(String.valueOf(signatureRequestRecipients[i]));

				if ((i + 1) < signatureRequestRecipients.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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

		Date statusDate = getStatusDate();

		if (statusDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"statusDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(statusDate));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.digital.signature.rest.dto.v1_0.SignatureRequest",
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
// LIFERAY-REST-BUILDER-HASH:1266117173