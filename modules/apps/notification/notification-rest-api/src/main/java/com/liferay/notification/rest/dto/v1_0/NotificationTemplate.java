/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.rest.dto.v1_0;

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
 * @author Gabriel Albuquerque
 * @generated
 */
@Generated("")
@GraphQLName("NotificationTemplate")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "NotificationTemplate")
public class NotificationTemplate implements Serializable {

	public static NotificationTemplate toDTO(String json) {
		return ObjectMapperUtil.readValue(NotificationTemplate.class, json);
	}

	public static NotificationTemplate unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			NotificationTemplate.class, json);
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
	public String[] getAttachmentObjectFieldExternalReferenceCodes() {
		if (_attachmentObjectFieldExternalReferenceCodesSupplier != null) {
			attachmentObjectFieldExternalReferenceCodes =
				_attachmentObjectFieldExternalReferenceCodesSupplier.get();

			_attachmentObjectFieldExternalReferenceCodesSupplier = null;
		}

		return attachmentObjectFieldExternalReferenceCodes;
	}

	public void setAttachmentObjectFieldExternalReferenceCodes(
		String[] attachmentObjectFieldExternalReferenceCodes) {

		this.attachmentObjectFieldExternalReferenceCodes =
			attachmentObjectFieldExternalReferenceCodes;

		_attachmentObjectFieldExternalReferenceCodesSupplier = null;
	}

	@JsonIgnore
	public void setAttachmentObjectFieldExternalReferenceCodes(
		UnsafeSupplier<String[], Exception>
			attachmentObjectFieldExternalReferenceCodesUnsafeSupplier) {

		_attachmentObjectFieldExternalReferenceCodesSupplier = () -> {
			try {
				return attachmentObjectFieldExternalReferenceCodesUnsafeSupplier.
					get();
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
	protected String[] attachmentObjectFieldExternalReferenceCodes;

	@JsonIgnore
	private Supplier<String[]>
		_attachmentObjectFieldExternalReferenceCodesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long[] getAttachmentObjectFieldIds() {
		if (_attachmentObjectFieldIdsSupplier != null) {
			attachmentObjectFieldIds = _attachmentObjectFieldIdsSupplier.get();

			_attachmentObjectFieldIdsSupplier = null;
		}

		return attachmentObjectFieldIds;
	}

	public void setAttachmentObjectFieldIds(Long[] attachmentObjectFieldIds) {
		this.attachmentObjectFieldIds = attachmentObjectFieldIds;

		_attachmentObjectFieldIdsSupplier = null;
	}

	@JsonIgnore
	public void setAttachmentObjectFieldIds(
		UnsafeSupplier<Long[], Exception>
			attachmentObjectFieldIdsUnsafeSupplier) {

		_attachmentObjectFieldIdsSupplier = () -> {
			try {
				return attachmentObjectFieldIdsUnsafeSupplier.get();
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
	protected Long[] attachmentObjectFieldIds;

	@JsonIgnore
	private Supplier<Long[]> _attachmentObjectFieldIdsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, String> getBody() {
		if (_bodySupplier != null) {
			body = _bodySupplier.get();

			_bodySupplier = null;
		}

		return body;
	}

	public void setBody(Map<String, String> body) {
		this.body = body;

		_bodySupplier = null;
	}

	@JsonIgnore
	public void setBody(
		UnsafeSupplier<Map<String, String>, Exception> bodyUnsafeSupplier) {

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
	protected Map<String, String> body;

	@JsonIgnore
	private Supplier<Map<String, String>> _bodySupplier;

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
	public String getDescription() {
		if (_descriptionSupplier != null) {
			description = _descriptionSupplier.get();

			_descriptionSupplier = null;
		}

		return description;
	}

	public void setDescription(String description) {
		this.description = description;

		_descriptionSupplier = null;
	}

	@JsonIgnore
	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		_descriptionSupplier = () -> {
			try {
				return descriptionUnsafeSupplier.get();
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
	protected String description;

	@JsonIgnore
	private Supplier<String> _descriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("editorType")
	@Valid
	public EditorType getEditorType() {
		if (_editorTypeSupplier != null) {
			editorType = _editorTypeSupplier.get();

			_editorTypeSupplier = null;
		}

		return editorType;
	}

	@JsonIgnore
	public String getEditorTypeAsString() {
		EditorType editorType = getEditorType();

		if (editorType == null) {
			return null;
		}

		return editorType.toString();
	}

	public void setEditorType(EditorType editorType) {
		this.editorType = editorType;

		_editorTypeSupplier = null;
	}

	@JsonIgnore
	public void setEditorType(
		UnsafeSupplier<EditorType, Exception> editorTypeUnsafeSupplier) {

		_editorTypeSupplier = () -> {
			try {
				return editorTypeUnsafeSupplier.get();
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
	protected EditorType editorType;

	@JsonIgnore
	private Supplier<EditorType> _editorTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getExternalReferenceCode() {
		if (_externalReferenceCodeSupplier != null) {
			externalReferenceCode = _externalReferenceCodeSupplier.get();

			_externalReferenceCodeSupplier = null;
		}

		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;

		_externalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		_externalReferenceCodeSupplier = () -> {
			try {
				return externalReferenceCodeUnsafeSupplier.get();
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
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

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
	@Valid
	public Map<String, String> getName_i18n() {
		if (_name_i18nSupplier != null) {
			name_i18n = _name_i18nSupplier.get();

			_name_i18nSupplier = null;
		}

		return name_i18n;
	}

	public void setName_i18n(Map<String, String> name_i18n) {
		this.name_i18n = name_i18n;

		_name_i18nSupplier = null;
	}

	@JsonIgnore
	public void setName_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			name_i18nUnsafeSupplier) {

		_name_i18nSupplier = () -> {
			try {
				return name_i18nUnsafeSupplier.get();
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
	protected Map<String, String> name_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _name_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getObjectDefinitionExternalReferenceCode() {
		if (_objectDefinitionExternalReferenceCodeSupplier != null) {
			objectDefinitionExternalReferenceCode =
				_objectDefinitionExternalReferenceCodeSupplier.get();

			_objectDefinitionExternalReferenceCodeSupplier = null;
		}

		return objectDefinitionExternalReferenceCode;
	}

	public void setObjectDefinitionExternalReferenceCode(
		String objectDefinitionExternalReferenceCode) {

		this.objectDefinitionExternalReferenceCode =
			objectDefinitionExternalReferenceCode;

		_objectDefinitionExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setObjectDefinitionExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			objectDefinitionExternalReferenceCodeUnsafeSupplier) {

		_objectDefinitionExternalReferenceCodeSupplier = () -> {
			try {
				return objectDefinitionExternalReferenceCodeUnsafeSupplier.
					get();
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
	protected String objectDefinitionExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _objectDefinitionExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getObjectDefinitionId() {
		if (_objectDefinitionIdSupplier != null) {
			objectDefinitionId = _objectDefinitionIdSupplier.get();

			_objectDefinitionIdSupplier = null;
		}

		return objectDefinitionId;
	}

	public void setObjectDefinitionId(Long objectDefinitionId) {
		this.objectDefinitionId = objectDefinitionId;

		_objectDefinitionIdSupplier = null;
	}

	@JsonIgnore
	public void setObjectDefinitionId(
		UnsafeSupplier<Long, Exception> objectDefinitionIdUnsafeSupplier) {

		_objectDefinitionIdSupplier = () -> {
			try {
				return objectDefinitionIdUnsafeSupplier.get();
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
	protected Long objectDefinitionId;

	@JsonIgnore
	private Supplier<Long> _objectDefinitionIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getRecipientType() {
		if (_recipientTypeSupplier != null) {
			recipientType = _recipientTypeSupplier.get();

			_recipientTypeSupplier = null;
		}

		return recipientType;
	}

	public void setRecipientType(String recipientType) {
		this.recipientType = recipientType;

		_recipientTypeSupplier = null;
	}

	@JsonIgnore
	public void setRecipientType(
		UnsafeSupplier<String, Exception> recipientTypeUnsafeSupplier) {

		_recipientTypeSupplier = () -> {
			try {
				return recipientTypeUnsafeSupplier.get();
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
	protected String recipientType;

	@JsonIgnore
	private Supplier<String> _recipientTypeSupplier;

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
	@Valid
	public Map<String, String> getSubject() {
		if (_subjectSupplier != null) {
			subject = _subjectSupplier.get();

			_subjectSupplier = null;
		}

		return subject;
	}

	public void setSubject(Map<String, String> subject) {
		this.subject = subject;

		_subjectSupplier = null;
	}

	@JsonIgnore
	public void setSubject(
		UnsafeSupplier<Map<String, String>, Exception> subjectUnsafeSupplier) {

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
	protected Map<String, String> subject;

	@JsonIgnore
	private Supplier<Map<String, String>> _subjectSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getSystem() {
		if (_systemSupplier != null) {
			system = _systemSupplier.get();

			_systemSupplier = null;
		}

		return system;
	}

	public void setSystem(Boolean system) {
		this.system = system;

		_systemSupplier = null;
	}

	@JsonIgnore
	public void setSystem(
		UnsafeSupplier<Boolean, Exception> systemUnsafeSupplier) {

		_systemSupplier = () -> {
			try {
				return systemUnsafeSupplier.get();
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
	protected Boolean system;

	@JsonIgnore
	private Supplier<Boolean> _systemSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String typeLabel;

	@JsonIgnore
	private Supplier<String> _typeLabelSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof NotificationTemplate)) {
			return false;
		}

		NotificationTemplate notificationTemplate =
			(NotificationTemplate)object;

		return Objects.equals(toString(), notificationTemplate.toString());
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

		String[] attachmentObjectFieldExternalReferenceCodes =
			getAttachmentObjectFieldExternalReferenceCodes();

		if (attachmentObjectFieldExternalReferenceCodes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attachmentObjectFieldExternalReferenceCodes\": ");

			sb.append("[");

			for (int i = 0;
				 i < attachmentObjectFieldExternalReferenceCodes.length; i++) {

				sb.append("\"");

				sb.append(
					_escape(attachmentObjectFieldExternalReferenceCodes[i]));

				sb.append("\"");

				if ((i + 1) <
						attachmentObjectFieldExternalReferenceCodes.length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Long[] attachmentObjectFieldIds = getAttachmentObjectFieldIds();

		if (attachmentObjectFieldIds != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attachmentObjectFieldIds\": ");

			sb.append("[");

			for (int i = 0; i < attachmentObjectFieldIds.length; i++) {
				sb.append(attachmentObjectFieldIds[i]);

				if ((i + 1) < attachmentObjectFieldIds.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Map<String, String> body = getBody();

		if (body != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"body\": ");

			sb.append(_toJSON(body));
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

		String description = getDescription();

		if (description != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(description));

			sb.append("\"");
		}

		EditorType editorType = getEditorType();

		if (editorType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"editorType\": ");

			sb.append("\"");

			sb.append(editorType);

			sb.append("\"");
		}

		String externalReferenceCode = getExternalReferenceCode();

		if (externalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(externalReferenceCode));

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

		Map<String, String> name_i18n = getName_i18n();

		if (name_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(name_i18n));
		}

		String objectDefinitionExternalReferenceCode =
			getObjectDefinitionExternalReferenceCode();

		if (objectDefinitionExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(objectDefinitionExternalReferenceCode));

			sb.append("\"");
		}

		Long objectDefinitionId = getObjectDefinitionId();

		if (objectDefinitionId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionId\": ");

			sb.append(objectDefinitionId);
		}

		String recipientType = getRecipientType();

		if (recipientType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"recipientType\": ");

			sb.append("\"");

			sb.append(_escape(recipientType));

			sb.append("\"");
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

		Map<String, String> subject = getSubject();

		if (subject != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subject\": ");

			sb.append(_toJSON(subject));
		}

		Boolean system = getSystem();

		if (system != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"system\": ");

			sb.append(system);
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
		defaultValue = "com.liferay.notification.rest.dto.v1_0.NotificationTemplate",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("EditorType")
	public static enum EditorType {

		FREE_MARKER("freeMarker"), RICH_TEXT("richText");

		@JsonCreator
		public static EditorType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (EditorType editorType : values()) {
				if (Objects.equals(editorType.getValue(), value)) {
					return editorType;
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

		private EditorType(String value) {
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