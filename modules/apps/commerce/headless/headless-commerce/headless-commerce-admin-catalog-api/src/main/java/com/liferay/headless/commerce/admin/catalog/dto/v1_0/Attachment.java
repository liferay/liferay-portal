/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.dto.v1_0;

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
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "An attachment associated with a product (or one of its options or option values) in the admin catalog write surface. Used to create, read, replace, and patch either gallery images (type 0) or generic attachments (type 1).",
	value = "Attachment"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "An attachment associated with a product (or one of its options or option values) in the admin catalog write surface. Used to create, read, replace, and patch either gallery images (type 0) or generic attachments (type 1)."
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
		description = "Base64-encoded payload of the file. On write it supplies the file body inline; it is decoded server-side and stored as a new document-library file entry, is mutually exclusive with `src`, and is ignored when `src` is supplied. On read it is always populated, so an exported payload carries its content instead of a `src` URL pointing back at the source environment.",
		example = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg=="
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
		description = "Base64-encoded payload of the file. On write it supplies the file body inline; it is decoded server-side and stored as a new document-library file entry, is mutually exclusive with `src`, and is ignored when `src` is supplied. On read it is always populated, so an exported payload carries its content instead of a `src` URL pointing back at the source environment."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String attachment;

	@JsonIgnore
	private Supplier<String> _attachmentSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When true the attachment is treated as an externally hosted asset -- no uploaded file is required and `cdnURL` becomes mandatory and must be a valid URL. When false the attachment must reference an existing document-library file.",
		example = "true"
	)
	public Boolean getCdnEnabled() {
		if (_cdnEnabledSupplier != null) {
			cdnEnabled = _cdnEnabledSupplier.get();

			_cdnEnabledSupplier = null;
		}

		return cdnEnabled;
	}

	public void setCdnEnabled(Boolean cdnEnabled) {
		this.cdnEnabled = cdnEnabled;

		_cdnEnabledSupplier = null;
	}

	@JsonIgnore
	public void setCdnEnabled(
		UnsafeSupplier<Boolean, Exception> cdnEnabledUnsafeSupplier) {

		_cdnEnabledSupplier = () -> {
			try {
				return cdnEnabledUnsafeSupplier.get();
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
		description = "When true the attachment is treated as an externally hosted asset -- no uploaded file is required and `cdnURL` becomes mandatory and must be a valid URL. When false the attachment must reference an existing document-library file."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean cdnEnabled;

	@JsonIgnore
	private Supplier<Boolean> _cdnEnabledSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Public CDN URL used when `cdnEnabled` is true; must be a valid URL. Ignored when `cdnEnabled` is false.",
		example = "https://cdn.example.com/attachment.png"
	)
	public String getCdnURL() {
		if (_cdnURLSupplier != null) {
			cdnURL = _cdnURLSupplier.get();

			_cdnURLSupplier = null;
		}

		return cdnURL;
	}

	public void setCdnURL(String cdnURL) {
		this.cdnURL = cdnURL;

		_cdnURLSupplier = null;
	}

	@JsonIgnore
	public void setCdnURL(
		UnsafeSupplier<String, Exception> cdnURLUnsafeSupplier) {

		_cdnURLSupplier = () -> {
			try {
				return cdnURLUnsafeSupplier.get();
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
		description = "Public CDN URL used when `cdnEnabled` is true; must be a valid URL. Ignored when `cdnEnabled` is false."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String cdnURL;

	@JsonIgnore
	private Supplier<String> _cdnURLSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "MIME type of the uploaded file. When omitted, the server derives it from the decoded file content; the resolved type then drives the file extension applied to the generated unique file name.",
		example = "image/png"
	)
	public String getContentType() {
		if (_contentTypeSupplier != null) {
			contentType = _contentTypeSupplier.get();

			_contentTypeSupplier = null;
		}

		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;

		_contentTypeSupplier = null;
	}

	@JsonIgnore
	public void setContentType(
		UnsafeSupplier<String, Exception> contentTypeUnsafeSupplier) {

		_contentTypeSupplier = () -> {
			try {
				return contentTypeUnsafeSupplier.get();
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
		description = "MIME type of the uploaded file. When omitted, the server derives it from the decoded file content; the resolved type then drives the file extension applied to the generated unique file name."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String contentType;

	@JsonIgnore
	private Supplier<String> _contentTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Custom expando attributes attached to the attachment; on read they are filtered by the accept-language locale, and on write they are applied to the attachment's expando bridge."
	)
	@Valid
	public com.liferay.portal.vulcan.custom.field.CustomField[]
		getCustomFields() {

		if (_customFieldsSupplier != null) {
			customFields = _customFieldsSupplier.get();

			_customFieldsSupplier = null;
		}

		return customFields;
	}

	public void setCustomFields(
		com.liferay.portal.vulcan.custom.field.CustomField[] customFields) {

		this.customFields = customFields;

		_customFieldsSupplier = null;
	}

	@JsonIgnore
	public void setCustomFields(
		UnsafeSupplier
			<com.liferay.portal.vulcan.custom.field.CustomField[], Exception>
				customFieldsUnsafeSupplier) {

		_customFieldsSupplier = () -> {
			try {
				return customFieldsUnsafeSupplier.get();
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
		description = "Custom expando attributes attached to the attachment; on read they are filtered by the accept-language locale, and on write they are applied to the attachment's expando bridge."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected com.liferay.portal.vulcan.custom.field.CustomField[] customFields;

	@JsonIgnore
	private Supplier<com.liferay.portal.vulcan.custom.field.CustomField[]>
		_customFieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Date and time when the attachment becomes visible. ISO 8601 date-time interpreted in the request user's time zone. When the value lies in the future the attachment is persisted as scheduled instead of draft or approved.",
		example = "2017-07-21"
	)
	public Date getDisplayDate() {
		if (_displayDateSupplier != null) {
			displayDate = _displayDateSupplier.get();

			_displayDateSupplier = null;
		}

		return displayDate;
	}

	public void setDisplayDate(Date displayDate) {
		this.displayDate = displayDate;

		_displayDateSupplier = null;
	}

	@JsonIgnore
	public void setDisplayDate(
		UnsafeSupplier<Date, Exception> displayDateUnsafeSupplier) {

		_displayDateSupplier = () -> {
			try {
				return displayDateUnsafeSupplier.get();
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
		description = "Date and time when the attachment becomes visible. ISO 8601 date-time interpreted in the request user's time zone. When the value lies in the future the attachment is persisted as scheduled instead of draft or approved."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date displayDate;

	@JsonIgnore
	private Supplier<Date> _displayDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Date and time when the attachment is hidden. ISO 8601 date-time in the user's time zone; must be in the future and after `displayDate`. When this date has already passed the attachment is persisted as expired. Ignored when `neverExpire` is true.",
		example = "2017-08-21"
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
		description = "Date and time when the attachment is hidden. ISO 8601 date-time in the user's time zone; must be in the future and after `displayDate`. When this date has already passed the attachment is persisted as expired. Ignored when `neverExpire` is true."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date expirationDate;

	@JsonIgnore
	private Supplier<Date> _expirationDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Idempotency key for create and update; must be unique per attachment within the company. Used to resolve the upsert target; reusing a code that points at a different parent is rejected as a duplicate.",
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
		description = "Idempotency key for create and update; must be unique per attachment within the company. Used to resolve the upsert target; reusing a code that points at a different parent is rejected as a duplicate."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Optional external reference code of an existing document-library file to attach. On write the server resolves it scoped to `fileEntryGroupExternalReferenceCode` and links the resulting file. Required together with `fileEntryGroupExternalReferenceCode` when `fileEntryId` is 0 and no inline content or URL is supplied. On read it reflects the linked file.",
		example = "AB-34098-789-N"
	)
	public String getFileEntryExternalReferenceCode() {
		if (_fileEntryExternalReferenceCodeSupplier != null) {
			fileEntryExternalReferenceCode =
				_fileEntryExternalReferenceCodeSupplier.get();

			_fileEntryExternalReferenceCodeSupplier = null;
		}

		return fileEntryExternalReferenceCode;
	}

	public void setFileEntryExternalReferenceCode(
		String fileEntryExternalReferenceCode) {

		this.fileEntryExternalReferenceCode = fileEntryExternalReferenceCode;

		_fileEntryExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setFileEntryExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			fileEntryExternalReferenceCodeUnsafeSupplier) {

		_fileEntryExternalReferenceCodeSupplier = () -> {
			try {
				return fileEntryExternalReferenceCodeUnsafeSupplier.get();
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
		description = "Optional external reference code of an existing document-library file to attach. On write the server resolves it scoped to `fileEntryGroupExternalReferenceCode` and links the resulting file. Required together with `fileEntryGroupExternalReferenceCode` when `fileEntryId` is 0 and no inline content or URL is supplied. On read it reflects the linked file."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String fileEntryExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _fileEntryExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the site that owns the referenced document-library file. An unknown value is rejected. On read it returns the site code of the underlying file.",
		example = "AB-34098-789-N"
	)
	public String getFileEntryGroupExternalReferenceCode() {
		if (_fileEntryGroupExternalReferenceCodeSupplier != null) {
			fileEntryGroupExternalReferenceCode =
				_fileEntryGroupExternalReferenceCodeSupplier.get();

			_fileEntryGroupExternalReferenceCodeSupplier = null;
		}

		return fileEntryGroupExternalReferenceCode;
	}

	public void setFileEntryGroupExternalReferenceCode(
		String fileEntryGroupExternalReferenceCode) {

		this.fileEntryGroupExternalReferenceCode =
			fileEntryGroupExternalReferenceCode;

		_fileEntryGroupExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setFileEntryGroupExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			fileEntryGroupExternalReferenceCodeUnsafeSupplier) {

		_fileEntryGroupExternalReferenceCodeSupplier = () -> {
			try {
				return fileEntryGroupExternalReferenceCodeUnsafeSupplier.get();
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
		description = "External reference code of the site that owns the referenced document-library file. An unknown value is rejected. On read it returns the site code of the underlying file."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String fileEntryGroupExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _fileEntryGroupExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Identifier of the underlying document-library file. When 0 on write, a new file is created from the supplied base64 body, URL, or file external reference code. When `cdnEnabled` is false a non-zero value is required, otherwise the request is rejected. The current user must hold view permission on this file.",
		example = "30130"
	)
	public Long getFileEntryId() {
		if (_fileEntryIdSupplier != null) {
			fileEntryId = _fileEntryIdSupplier.get();

			_fileEntryIdSupplier = null;
		}

		return fileEntryId;
	}

	public void setFileEntryId(Long fileEntryId) {
		this.fileEntryId = fileEntryId;

		_fileEntryIdSupplier = null;
	}

	@JsonIgnore
	public void setFileEntryId(
		UnsafeSupplier<Long, Exception> fileEntryIdUnsafeSupplier) {

		_fileEntryIdSupplier = () -> {
			try {
				return fileEntryIdUnsafeSupplier.get();
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
		description = "Identifier of the underlying document-library file. When 0 on write, a new file is created from the supplied base64 body, URL, or file external reference code. When `cdnEnabled` is false a non-zero value is required, otherwise the request is rejected. The current user must hold view permission on this file."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long fileEntryId;

	@JsonIgnore
	private Supplier<Long> _fileEntryIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When true the attachment is exposed in the storefront product gallery. Defaults to true when omitted on write.",
		example = "true"
	)
	public Boolean getGalleryEnabled() {
		if (_galleryEnabledSupplier != null) {
			galleryEnabled = _galleryEnabledSupplier.get();

			_galleryEnabledSupplier = null;
		}

		return galleryEnabled;
	}

	public void setGalleryEnabled(Boolean galleryEnabled) {
		this.galleryEnabled = galleryEnabled;

		_galleryEnabledSupplier = null;
	}

	@JsonIgnore
	public void setGalleryEnabled(
		UnsafeSupplier<Boolean, Exception> galleryEnabledUnsafeSupplier) {

		_galleryEnabledSupplier = () -> {
			try {
				return galleryEnabledUnsafeSupplier.get();
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
		description = "When true the attachment is exposed in the storefront product gallery. Defaults to true when omitted on write."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean galleryEnabled;

	@JsonIgnore
	private Supplier<Boolean> _galleryEnabledSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Identifier of the attachment. Read-only; assigned by the server.",
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
		description = "Identifier of the attachment. Read-only; assigned by the server."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When true the attachment never expires and `expirationDate` is ignored. Defaults to false on write.",
		example = "true"
	)
	public Boolean getNeverExpire() {
		if (_neverExpireSupplier != null) {
			neverExpire = _neverExpireSupplier.get();

			_neverExpireSupplier = null;
		}

		return neverExpire;
	}

	public void setNeverExpire(Boolean neverExpire) {
		this.neverExpire = neverExpire;

		_neverExpireSupplier = null;
	}

	@JsonIgnore
	public void setNeverExpire(
		UnsafeSupplier<Boolean, Exception> neverExpireUnsafeSupplier) {

		_neverExpireSupplier = () -> {
			try {
				return neverExpireUnsafeSupplier.get();
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
		description = "When true the attachment never expires and `expirationDate` is ignored. Defaults to false on write."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean neverExpire;

	@JsonIgnore
	private Supplier<Boolean> _neverExpireSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Free-form key/value map that binds the attachment to a specific SKU variant of the parent product; each entry is resolved against the product's options and option values, and unknown keys, options, or values are rejected.",
		example = "{color=yellow, optionKey=optionValueKey, size=xs}"
	)
	@Valid
	public Map<String, String> getOptions() {
		if (_optionsSupplier != null) {
			options = _optionsSupplier.get();

			_optionsSupplier = null;
		}

		return options;
	}

	public void setOptions(Map<String, String> options) {
		this.options = options;

		_optionsSupplier = null;
	}

	@JsonIgnore
	public void setOptions(
		UnsafeSupplier<Map<String, String>, Exception> optionsUnsafeSupplier) {

		_optionsSupplier = () -> {
			try {
				return optionsUnsafeSupplier.get();
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
		description = "Free-form key/value map that binds the attachment to a specific SKU variant of the parent product; each entry is resolved against the product's options and option values, and unknown keys, options, or values are rejected."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> options;

	@JsonIgnore
	private Supplier<Map<String, String>> _optionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Sort weight used to order attachments within a product gallery; higher values are returned first. Defaults to 0.0 when omitted.",
		example = "1.2"
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
		description = "Sort weight used to order attachments within a product gallery; higher values are returned first. Defaults to 0.0 when omitted."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Double priority;

	@JsonIgnore
	private Supplier<Double> _prioritySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "URL pointing at the binary to download. On write the server fetches the URL and stores the body as a new document-library file; the `file://` protocol is rejected. On read it returns the portal download URL; for external video shortcuts the URL is intentionally blank.",
		example = "https://example.com/attachment.png"
	)
	public String getSrc() {
		if (_srcSupplier != null) {
			src = _srcSupplier.get();

			_srcSupplier = null;
		}

		return src;
	}

	public void setSrc(String src) {
		this.src = src;

		_srcSupplier = null;
	}

	@JsonIgnore
	public void setSrc(UnsafeSupplier<String, Exception> srcUnsafeSupplier) {
		_srcSupplier = () -> {
			try {
				return srcUnsafeSupplier.get();
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
		description = "URL pointing at the binary to download. On write the server fetches the URL and stores the body as a new document-library file; the `file://` protocol is rejected. On read it returns the portal download URL; for external video shortcuts the URL is intentionally blank."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String src;

	@JsonIgnore
	private Supplier<String> _srcSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Asset tag names attached to the underlying asset entry; used for categorization and search facets.",
		example = "[tag1, tag2, tag3]"
	)
	public String[] getTags() {
		if (_tagsSupplier != null) {
			tags = _tagsSupplier.get();

			_tagsSupplier = null;
		}

		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;

		_tagsSupplier = null;
	}

	@JsonIgnore
	public void setTags(
		UnsafeSupplier<String[], Exception> tagsUnsafeSupplier) {

		_tagsSupplier = () -> {
			try {
				return tagsUnsafeSupplier.get();
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
		description = "Asset tag names attached to the underlying asset entry; used for categorization and search facets."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] tags;

	@JsonIgnore
	private Supplier<String[]> _tagsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Localized text. Map keys are locale codes; values are the translated strings. Falls back to the file name in the site default locale when no value is supplied.",
		example = "{en_US=Hand Saw, hr_HR=Attachment Title HR, hu_HU=Attachment Title HU}"
	)
	@Valid
	public Map<String, String> getTitle() {
		if (_titleSupplier != null) {
			title = _titleSupplier.get();

			_titleSupplier = null;
		}

		return title;
	}

	public void setTitle(Map<String, String> title) {
		this.title = title;

		_titleSupplier = null;
	}

	@JsonIgnore
	public void setTitle(
		UnsafeSupplier<Map<String, String>, Exception> titleUnsafeSupplier) {

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
		description = "Localized text. Map keys are locale codes; values are the translated strings. Falls back to the file name in the site default locale when no value is supplied."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> title;

	@JsonIgnore
	private Supplier<Map<String, String>> _titleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Integer type code. Mapping -- 0=Image, 1=Other. Set by the resource endpoint -- image endpoints write 0, attachment endpoints write 1. The diagram image variant uses code 2 internally but is not exposed through the attachment surface.",
		example = "0"
	)
	public Integer getType() {
		if (_typeSupplier != null) {
			type = _typeSupplier.get();

			_typeSupplier = null;
		}

		return type;
	}

	public void setType(Integer type) {
		this.type = type;

		_typeSupplier = null;
	}

	@JsonIgnore
	public void setType(UnsafeSupplier<Integer, Exception> typeUnsafeSupplier) {
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
		description = "Integer type code. Mapping -- 0=Image, 1=Other. Set by the resource endpoint -- image endpoints write 0, attachment endpoints write 1. The diagram image variant uses code 2 internally but is not exposed through the attachment surface."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Integer type;

	@JsonIgnore
	private Supplier<Integer> _typeSupplier;

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

		Boolean cdnEnabled = getCdnEnabled();

		if (cdnEnabled != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cdnEnabled\": ");

			sb.append(cdnEnabled);
		}

		String cdnURL = getCdnURL();

		if (cdnURL != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cdnURL\": ");

			sb.append("\"");

			sb.append(_escape(cdnURL));

			sb.append("\"");
		}

		String contentType = getContentType();

		if (contentType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentType\": ");

			sb.append("\"");

			sb.append(_escape(contentType));

			sb.append("\"");
		}

		com.liferay.portal.vulcan.custom.field.CustomField[] customFields =
			getCustomFields();

		if (customFields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < customFields.length; i++) {
				sb.append(customFields[i]);

				if ((i + 1) < customFields.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Date displayDate = getDisplayDate();

		if (displayDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(displayDate));

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

		String fileEntryExternalReferenceCode =
			getFileEntryExternalReferenceCode();

		if (fileEntryExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileEntryExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(fileEntryExternalReferenceCode));

			sb.append("\"");
		}

		String fileEntryGroupExternalReferenceCode =
			getFileEntryGroupExternalReferenceCode();

		if (fileEntryGroupExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileEntryGroupExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(fileEntryGroupExternalReferenceCode));

			sb.append("\"");
		}

		Long fileEntryId = getFileEntryId();

		if (fileEntryId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileEntryId\": ");

			sb.append(fileEntryId);
		}

		Boolean galleryEnabled = getGalleryEnabled();

		if (galleryEnabled != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"galleryEnabled\": ");

			sb.append(galleryEnabled);
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		Boolean neverExpire = getNeverExpire();

		if (neverExpire != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"neverExpire\": ");

			sb.append(neverExpire);
		}

		Map<String, String> options = getOptions();

		if (options != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"options\": ");

			sb.append(_toJSON(options));
		}

		Double priority = getPriority();

		if (priority != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(priority);
		}

		String src = getSrc();

		if (src != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"src\": ");

			sb.append("\"");

			sb.append(_escape(src));

			sb.append("\"");
		}

		String[] tags = getTags();

		if (tags != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tags\": ");

			sb.append("[");

			for (int i = 0; i < tags.length; i++) {
				sb.append("\"");

				sb.append(_escape(tags[i]));

				sb.append("\"");

				if ((i + 1) < tags.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Map<String, String> title = getTitle();

		if (title != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append(_toJSON(title));
		}

		Integer type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append(type);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.catalog.dto.v1_0.Attachment",
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
// LIFERAY-REST-BUILDER-HASH:666283951