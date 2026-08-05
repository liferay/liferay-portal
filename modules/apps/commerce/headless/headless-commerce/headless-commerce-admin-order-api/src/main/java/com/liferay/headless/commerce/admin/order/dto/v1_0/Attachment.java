/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.dto.v1_0;

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
import jakarta.validation.constraints.DecimalMin;

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
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "A file attached to an order (image, document, virtual asset). Carries the file content on upload and file metadata, download URL, and a restricted flag for visibility control on retrieval.",
	value = "Attachment"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A file attached to an order (image, document, virtual asset). Carries the file content on upload and file metadata, download URL, and a restricted flag for visibility control on retrieval."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Attachment")
public class Attachment implements Serializable {

	public static Attachment toDTO(String json) {
		return ObjectMapperUtil.readValue(Attachment.class, json);
	}

	public static Attachment unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Attachment.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Map of HATEOAS actions available to the current user, keyed by action name. Each value carries the href template and HTTP method, computed dynamically from user permissions. Read-only."
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
		description = "Map of HATEOAS actions available to the current user, keyed by action name. Each value carries the href template and HTTP method, computed dynamically from user permissions. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Base64-encoded file content to upload. Write-only; omitted from GET responses.",
		example = "iVBORw0KGgoAAAANSUhEUgAA"
	)
	public String getAttachment() {
		if (_attachmentSupplier != null) {
			attachment = _attachmentSupplier.get();

			_attachmentSupplier = null;
		}

		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;

		_attachmentSupplier = null;
	}

	@JsonIgnore
	public void setAttachment(
		UnsafeSupplier<String, Exception> attachmentUnsafeSupplier) {

		_attachmentSupplier = () -> {
			try {
				return attachmentUnsafeSupplier.get();
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
		description = "Base64-encoded file content to upload. Write-only; omitted from GET responses."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String attachment;

	@JsonIgnore
	private Supplier<String> _attachmentSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Last modification date and time in ISO 8601 (UTC). Read-only; set by the service on each update.",
		example = "2017-07-21"
	)
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

	@GraphQLField(
		description = "Last modification date and time in ISO 8601 (UTC). Read-only; set by the service on each update."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "File extension extracted from the uploaded file name (for example, pdf, jpg). Read-only.",
		example = "pdf"
	)
	public String getExtension() {
		if (_extensionSupplier != null) {
			extension = _extensionSupplier.get();

			_extensionSupplier = null;
		}

		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;

		_extensionSupplier = null;
	}

	@JsonIgnore
	public void setExtension(
		UnsafeSupplier<String, Exception> extensionUnsafeSupplier) {

		_extensionSupplier = () -> {
			try {
				return extensionUnsafeSupplier.get();
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
		description = "File extension extracted from the uploaded file name (for example, pdf, jpg). Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String extension;

	@JsonIgnore
	private Supplier<String> _extensionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Idempotency key for create and update; must be unique per attachment within the company.",
		example = "AB-34098-789-N"
	)
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

	@GraphQLField(
		description = "Idempotency key for create and update; must be unique per attachment within the company."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Original file name supplied at upload time.",
		example = "invoice.pdf"
	)
	public String getFileName() {
		if (_fileNameSupplier != null) {
			fileName = _fileNameSupplier.get();

			_fileNameSupplier = null;
		}

		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;

		_fileNameSupplier = null;
	}

	@JsonIgnore
	public void setFileName(
		UnsafeSupplier<String, Exception> fileNameUnsafeSupplier) {

		_fileNameSupplier = () -> {
			try {
				return fileNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Original file name supplied at upload time.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String fileName;

	@JsonIgnore
	private Supplier<String> _fileNameSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the order attachment (FK identifier). Read-only.",
		example = "30130"
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
		description = "Reference to the order attachment (FK identifier). Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Numeric sort order for displaying multiple attachments. Lower values come first; defaults to 0.",
		example = "1.0"
	)
	public Double getPriority() {
		if (_prioritySupplier != null) {
			priority = _prioritySupplier.get();

			_prioritySupplier = null;
		}

		return priority;
	}

	public void setPriority(Double priority) {
		this.priority = priority;

		_prioritySupplier = null;
	}

	@JsonIgnore
	public void setPriority(
		UnsafeSupplier<Double, Exception> priorityUnsafeSupplier) {

		_prioritySupplier = () -> {
			try {
				return priorityUnsafeSupplier.get();
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
		description = "Numeric sort order for displaying multiple attachments. Lower values come first; defaults to 0."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Double priority;

	@JsonIgnore
	private Supplier<Double> _prioritySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When true, the attachment is internal-only and not visible to the storefront. When false, it is visible to the buyer.",
		example = "false"
	)
	public Boolean getRestricted() {
		if (_restrictedSupplier != null) {
			restricted = _restrictedSupplier.get();

			_restrictedSupplier = null;
		}

		return restricted;
	}

	public void setRestricted(Boolean restricted) {
		this.restricted = restricted;

		_restrictedSupplier = null;
	}

	@JsonIgnore
	public void setRestricted(
		UnsafeSupplier<Boolean, Exception> restrictedUnsafeSupplier) {

		_restrictedSupplier = () -> {
			try {
				return restrictedUnsafeSupplier.get();
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
		description = "When true, the attachment is internal-only and not visible to the storefront. When false, it is visible to the buyer."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean restricted;

	@JsonIgnore
	private Supplier<Boolean> _restrictedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Display title for the attachment; may differ from the original file name.",
		example = "Invoice"
	)
	public String getTitle() {
		if (_titleSupplier != null) {
			title = _titleSupplier.get();

			_titleSupplier = null;
		}

		return title;
	}

	public void setTitle(String title) {
		this.title = title;

		_titleSupplier = null;
	}

	@JsonIgnore
	public void setTitle(
		UnsafeSupplier<String, Exception> titleUnsafeSupplier) {

		_titleSupplier = () -> {
			try {
				return titleUnsafeSupplier.get();
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
		description = "Display title for the attachment; may differ from the original file name."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String title;

	@JsonIgnore
	private Supplier<String> _titleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Type key for the attachment, drawn from the attachment-type list configured for the company. Free-form string.",
		example = "general"
	)
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

	@GraphQLField(
		description = "Type key for the attachment, drawn from the attachment-type list configured for the company. Free-form string."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String type;

	@JsonIgnore
	private Supplier<String> _typeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Localized human-readable label for the type key. Read-only; resolved against the request locale.",
		example = "General"
	)
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

	@GraphQLField(
		description = "Localized human-readable label for the type key. Read-only; resolved against the request locale."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String typeLabel;

	@JsonIgnore
	private Supplier<String> _typeLabelSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Fully qualified download URL for the file. Read-only; generated by the service.",
		example = "https://example.com/attachments/invoice.pdf"
	)
	public String getUrl() {
		if (_urlSupplier != null) {
			url = _urlSupplier.get();

			_urlSupplier = null;
		}

		return url;
	}

	public void setUrl(String url) {
		this.url = url;

		_urlSupplier = null;
	}

	@JsonIgnore
	public void setUrl(UnsafeSupplier<String, Exception> urlUnsafeSupplier) {
		_urlSupplier = () -> {
			try {
				return urlUnsafeSupplier.get();
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
		description = "Fully qualified download URL for the file. Read-only; generated by the service."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String url;

	@JsonIgnore
	private Supplier<String> _urlSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Attachment)) {
			return false;
		}

		Attachment attachment = (Attachment)object;

		return Objects.equals(toString(), attachment.toString());
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

		String attachment = getAttachment();

		if (attachment != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attachment\": ");

			sb.append("\"");

			sb.append(_escape(attachment));

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

		String extension = getExtension();

		if (extension != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"extension\": ");

			sb.append("\"");

			sb.append(_escape(extension));

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

		String fileName = getFileName();

		if (fileName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileName\": ");

			sb.append("\"");

			sb.append(_escape(fileName));

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

		Double priority = getPriority();

		if (priority != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(priority);
		}

		Boolean restricted = getRestricted();

		if (restricted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"restricted\": ");

			sb.append(restricted);
		}

		String title = getTitle();

		if (title != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(title));

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

		String url = getUrl();

		if (url != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"url\": ");

			sb.append("\"");

			sb.append(_escape(url));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.order.dto.v1_0.Attachment",
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
// LIFERAY-REST-BUILDER-HASH:837412401