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
	description = "Per-SKU override for digital-download settings of a virtual product; only meaningful when the parent product type is virtual.",
	value = "SkuVirtualSettings"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Per-SKU override for digital-download settings of a virtual product; only meaningful when the parent product type is virtual."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "SkuVirtualSettings")
public class SkuVirtualSettings implements Serializable {

	public static SkuVirtualSettings toDTO(String json) {
		return ObjectMapperUtil.readValue(SkuVirtualSettings.class, json);
	}

	public static SkuVirtualSettings unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(SkuVirtualSettings.class, json);
	}

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Order status at which the virtual asset becomes downloadable. Common values are 0 (Completed, the default), 17 (Pending), and 10 (Processing).",
		example = "0"
	)
	public Integer getActivationStatus() {
		if (_activationStatusSupplier != null) {
			activationStatus = _activationStatusSupplier.get();

			_activationStatusSupplier = null;
		}

		return activationStatus;
	}

	public void setActivationStatus(Integer activationStatus) {
		this.activationStatus = activationStatus;

		_activationStatusSupplier = null;
	}

	@JsonIgnore
	public void setActivationStatus(
		UnsafeSupplier<Integer, Exception> activationStatusUnsafeSupplier) {

		_activationStatusSupplier = () -> {
			try {
				return activationStatusUnsafeSupplier.get();
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
		description = "Order status at which the virtual asset becomes downloadable. Common values are 0 (Completed, the default), 17 (Pending), and 10 (Processing)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer activationStatus;

	@JsonIgnore
	private Supplier<Integer> _activationStatusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Localized label for `activationStatus`; read-only."
	)
	@Valid
	public Status getActivationStatusInfo() {
		if (_activationStatusInfoSupplier != null) {
			activationStatusInfo = _activationStatusInfoSupplier.get();

			_activationStatusInfoSupplier = null;
		}

		return activationStatusInfo;
	}

	public void setActivationStatusInfo(Status activationStatusInfo) {
		this.activationStatusInfo = activationStatusInfo;

		_activationStatusInfoSupplier = null;
	}

	@JsonIgnore
	public void setActivationStatusInfo(
		UnsafeSupplier<Status, Exception> activationStatusInfoUnsafeSupplier) {

		_activationStatusInfoSupplier = () -> {
			try {
				return activationStatusInfoUnsafeSupplier.get();
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
		description = "Localized label for `activationStatus`; read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Status activationStatusInfo;

	@JsonIgnore
	private Supplier<Status> _activationStatusInfoSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Base64-encoded file uploaded as the main virtual download asset on create or update; write-only and not returned on read. The resolved download URL is exposed through `src`.",
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
		description = "Base64-encoded file uploaded as the main virtual download asset on create or update; write-only and not returned on read. The resolved download URL is exposed through `src`."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String attachment;

	@JsonIgnore
	private Supplier<String> _attachmentSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Number of days during which a customer can download the virtual asset after activation.",
		example = "30"
	)
	public Long getDuration() {
		if (_durationSupplier != null) {
			duration = _durationSupplier.get();

			_durationSupplier = null;
		}

		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;

		_durationSupplier = null;
	}

	@JsonIgnore
	public void setDuration(
		UnsafeSupplier<Long, Exception> durationUnsafeSupplier) {

		_durationSupplier = () -> {
			try {
				return durationUnsafeSupplier.get();
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
		description = "Number of days during which a customer can download the virtual asset after activation."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long duration;

	@JsonIgnore
	private Supplier<Long> _durationSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Internal numeric identifier of the virtual settings entry.",
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
		description = "Internal numeric identifier of the virtual settings entry."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Maximum number of times a customer can download the virtual asset; zero means unlimited.",
		example = "0"
	)
	public Integer getMaxUsages() {
		if (_maxUsagesSupplier != null) {
			maxUsages = _maxUsagesSupplier.get();

			_maxUsagesSupplier = null;
		}

		return maxUsages;
	}

	public void setMaxUsages(Integer maxUsages) {
		this.maxUsages = maxUsages;

		_maxUsagesSupplier = null;
	}

	@JsonIgnore
	public void setMaxUsages(
		UnsafeSupplier<Integer, Exception> maxUsagesUnsafeSupplier) {

		_maxUsagesSupplier = () -> {
			try {
				return maxUsagesUnsafeSupplier.get();
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
		description = "Maximum number of times a customer can download the virtual asset; zero means unlimited."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer maxUsages;

	@JsonIgnore
	private Supplier<Integer> _maxUsagesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When true the service uses these per-SKU virtual settings instead of the parent product's virtual settings.",
		example = "true"
	)
	public Boolean getOverride() {
		if (_overrideSupplier != null) {
			override = _overrideSupplier.get();

			_overrideSupplier = null;
		}

		return override;
	}

	public void setOverride(Boolean override) {
		this.override = override;

		_overrideSupplier = null;
	}

	@JsonIgnore
	public void setOverride(
		UnsafeSupplier<Boolean, Exception> overrideUnsafeSupplier) {

		_overrideSupplier = () -> {
			try {
				return overrideUnsafeSupplier.get();
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
		description = "When true the service uses these per-SKU virtual settings instead of the parent product's virtual settings."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean override;

	@JsonIgnore
	private Supplier<Boolean> _overrideSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Base64-encoded sample file uploaded on create or update so customers can preview the virtual product before purchase; write-only and not returned on read. The resolved sample download URL is exposed through `sampleSrc`.",
		example = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg=="
	)
	public String getSampleAttachment() {
		if (_sampleAttachmentSupplier != null) {
			sampleAttachment = _sampleAttachmentSupplier.get();

			_sampleAttachmentSupplier = null;
		}

		return sampleAttachment;
	}

	public void setSampleAttachment(String sampleAttachment) {
		this.sampleAttachment = sampleAttachment;

		_sampleAttachmentSupplier = null;
	}

	@JsonIgnore
	public void setSampleAttachment(
		UnsafeSupplier<String, Exception> sampleAttachmentUnsafeSupplier) {

		_sampleAttachmentSupplier = () -> {
			try {
				return sampleAttachmentUnsafeSupplier.get();
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
		description = "Base64-encoded sample file uploaded on create or update so customers can preview the virtual product before purchase; write-only and not returned on read. The resolved sample download URL is exposed through `sampleSrc`."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String sampleAttachment;

	@JsonIgnore
	private Supplier<String> _sampleAttachmentSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Resolved internal sample download URL for the uploaded sample file; null when no sample file is attached. Read-only.",
		example = "https://example.com/sample.pdf"
	)
	public String getSampleSrc() {
		if (_sampleSrcSupplier != null) {
			sampleSrc = _sampleSrcSupplier.get();

			_sampleSrcSupplier = null;
		}

		return sampleSrc;
	}

	public void setSampleSrc(String sampleSrc) {
		this.sampleSrc = sampleSrc;

		_sampleSrcSupplier = null;
	}

	@JsonIgnore
	public void setSampleSrc(
		UnsafeSupplier<String, Exception> sampleSrcUnsafeSupplier) {

		_sampleSrcSupplier = () -> {
			try {
				return sampleSrcUnsafeSupplier.get();
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
		description = "Resolved internal sample download URL for the uploaded sample file; null when no sample file is attached. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String sampleSrc;

	@JsonIgnore
	private Supplier<String> _sampleSrcSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External URL pointing at a sample of the virtual product, used when the sample is hosted outside the portal; when `useSample` is true and no sample file is provided the URL must be a syntactically valid URL.",
		example = "https://example.com/sample.pdf"
	)
	public String getSampleURL() {
		if (_sampleURLSupplier != null) {
			sampleURL = _sampleURLSupplier.get();

			_sampleURLSupplier = null;
		}

		return sampleURL;
	}

	public void setSampleURL(String sampleURL) {
		this.sampleURL = sampleURL;

		_sampleURLSupplier = null;
	}

	@JsonIgnore
	public void setSampleURL(
		UnsafeSupplier<String, Exception> sampleURLUnsafeSupplier) {

		_sampleURLSupplier = () -> {
			try {
				return sampleURLUnsafeSupplier.get();
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
		description = "External URL pointing at a sample of the virtual product, used when the sample is hosted outside the portal; when `useSample` is true and no sample file is provided the URL must be a syntactically valid URL."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String sampleURL;

	@JsonIgnore
	private Supplier<String> _sampleURLSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Additional file attachments associated with this SKU's virtual settings."
	)
	@Valid
	public SkuVirtualSettingsFileEntry[] getSkuVirtualSettingsFileEntries() {
		if (_skuVirtualSettingsFileEntriesSupplier != null) {
			skuVirtualSettingsFileEntries =
				_skuVirtualSettingsFileEntriesSupplier.get();

			_skuVirtualSettingsFileEntriesSupplier = null;
		}

		return skuVirtualSettingsFileEntries;
	}

	public void setSkuVirtualSettingsFileEntries(
		SkuVirtualSettingsFileEntry[] skuVirtualSettingsFileEntries) {

		this.skuVirtualSettingsFileEntries = skuVirtualSettingsFileEntries;

		_skuVirtualSettingsFileEntriesSupplier = null;
	}

	@JsonIgnore
	public void setSkuVirtualSettingsFileEntries(
		UnsafeSupplier<SkuVirtualSettingsFileEntry[], Exception>
			skuVirtualSettingsFileEntriesUnsafeSupplier) {

		_skuVirtualSettingsFileEntriesSupplier = () -> {
			try {
				return skuVirtualSettingsFileEntriesUnsafeSupplier.get();
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
		description = "Additional file attachments associated with this SKU's virtual settings."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected SkuVirtualSettingsFileEntry[] skuVirtualSettingsFileEntries;

	@JsonIgnore
	private Supplier<SkuVirtualSettingsFileEntry[]>
		_skuVirtualSettingsFileEntriesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Resolved internal download URL for the first attached file entry; null when no file is attached. Read-only.",
		example = "https://example.com/download.zip"
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
		description = "Resolved internal download URL for the first attached file entry; null when no file is attached. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String src;

	@JsonIgnore
	private Supplier<String> _srcSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Localized text. Map keys are locale codes; values are the translated strings. Used as inline terms of use content when `termsOfUseRequired` is true and no `termsOfUseJournalArticleId` is supplied; at least one non-null entry is required.",
		example = "{en_US=By downloading you accept the license terms., hr_HR=Preuzimanjem prihvacate uvjete licence., hu_HU=A letoltessel elfogadja a licencfelteteleket.}"
	)
	@Valid
	public Map<String, String> getTermsOfUseContent() {
		if (_termsOfUseContentSupplier != null) {
			termsOfUseContent = _termsOfUseContentSupplier.get();

			_termsOfUseContentSupplier = null;
		}

		return termsOfUseContent;
	}

	public void setTermsOfUseContent(Map<String, String> termsOfUseContent) {
		this.termsOfUseContent = termsOfUseContent;

		_termsOfUseContentSupplier = null;
	}

	@JsonIgnore
	public void setTermsOfUseContent(
		UnsafeSupplier<Map<String, String>, Exception>
			termsOfUseContentUnsafeSupplier) {

		_termsOfUseContentSupplier = () -> {
			try {
				return termsOfUseContentUnsafeSupplier.get();
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
		description = "Localized text. Map keys are locale codes; values are the translated strings. Used as inline terms of use content when `termsOfUseRequired` is true and no `termsOfUseJournalArticleId` is supplied; at least one non-null entry is required."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> termsOfUseContent;

	@JsonIgnore
	private Supplier<Map<String, String>> _termsOfUseContentSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the web content article that supplies the terms of use; it takes precedence over `termsOfUseJournalArticleId`. The code is looked up in the site or asset library given by `termsOfUseJournalArticleGroupExternalReferenceCode`, or in the catalog that owns the SKU when that code is omitted, and resolves to the most recent version of the article. An unresolved code leaves the SKU without a terms of use article, which fails the request when `termsOfUseRequired` is true and no `termsOfUseContent` is supplied.",
		example = "AB-34098-789-N"
	)
	public String getTermsOfUseJournalArticleExternalReferenceCode() {
		if (_termsOfUseJournalArticleExternalReferenceCodeSupplier != null) {
			termsOfUseJournalArticleExternalReferenceCode =
				_termsOfUseJournalArticleExternalReferenceCodeSupplier.get();

			_termsOfUseJournalArticleExternalReferenceCodeSupplier = null;
		}

		return termsOfUseJournalArticleExternalReferenceCode;
	}

	public void setTermsOfUseJournalArticleExternalReferenceCode(
		String termsOfUseJournalArticleExternalReferenceCode) {

		this.termsOfUseJournalArticleExternalReferenceCode =
			termsOfUseJournalArticleExternalReferenceCode;

		_termsOfUseJournalArticleExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setTermsOfUseJournalArticleExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			termsOfUseJournalArticleExternalReferenceCodeUnsafeSupplier) {

		_termsOfUseJournalArticleExternalReferenceCodeSupplier = () -> {
			try {
				return termsOfUseJournalArticleExternalReferenceCodeUnsafeSupplier.
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

	@GraphQLField(
		description = "External reference code of the web content article that supplies the terms of use; it takes precedence over `termsOfUseJournalArticleId`. The code is looked up in the site or asset library given by `termsOfUseJournalArticleGroupExternalReferenceCode`, or in the catalog that owns the SKU when that code is omitted, and resolves to the most recent version of the article. An unresolved code leaves the SKU without a terms of use article, which fails the request when `termsOfUseRequired` is true and no `termsOfUseContent` is supplied."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String termsOfUseJournalArticleExternalReferenceCode;

	@JsonIgnore
	private Supplier<String>
		_termsOfUseJournalArticleExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the site or asset library that owns the web content article named by `termsOfUseJournalArticleExternalReferenceCode`; supply it when the article lives outside the catalog that owns the SKU, since a web content external reference code is unique only within the site or asset library that holds it. It defaults to the catalog that owns the SKU. An unresolved code fails the request.",
		example = "AB-34098-789-N"
	)
	public String getTermsOfUseJournalArticleGroupExternalReferenceCode() {
		if (_termsOfUseJournalArticleGroupExternalReferenceCodeSupplier !=
				null) {

			termsOfUseJournalArticleGroupExternalReferenceCode =
				_termsOfUseJournalArticleGroupExternalReferenceCodeSupplier.
					get();

			_termsOfUseJournalArticleGroupExternalReferenceCodeSupplier = null;
		}

		return termsOfUseJournalArticleGroupExternalReferenceCode;
	}

	public void setTermsOfUseJournalArticleGroupExternalReferenceCode(
		String termsOfUseJournalArticleGroupExternalReferenceCode) {

		this.termsOfUseJournalArticleGroupExternalReferenceCode =
			termsOfUseJournalArticleGroupExternalReferenceCode;

		_termsOfUseJournalArticleGroupExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setTermsOfUseJournalArticleGroupExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			termsOfUseJournalArticleGroupExternalReferenceCodeUnsafeSupplier) {

		_termsOfUseJournalArticleGroupExternalReferenceCodeSupplier = () -> {
			try {
				return termsOfUseJournalArticleGroupExternalReferenceCodeUnsafeSupplier.
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

	@GraphQLField(
		description = "External reference code of the site or asset library that owns the web content article named by `termsOfUseJournalArticleExternalReferenceCode`; supply it when the article lives outside the catalog that owns the SKU, since a web content external reference code is unique only within the site or asset library that holds it. It defaults to the catalog that owns the SKU. An unresolved code fails the request."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String termsOfUseJournalArticleGroupExternalReferenceCode;

	@JsonIgnore
	private Supplier<String>
		_termsOfUseJournalArticleGroupExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Internal identifier of a web content article that supplies the terms of use a customer must accept before download; when set and `termsOfUseRequired` is true the inline `termsOfUseContent` is cleared. The article must exist.",
		example = "30130"
	)
	public Long getTermsOfUseJournalArticleId() {
		if (_termsOfUseJournalArticleIdSupplier != null) {
			termsOfUseJournalArticleId =
				_termsOfUseJournalArticleIdSupplier.get();

			_termsOfUseJournalArticleIdSupplier = null;
		}

		return termsOfUseJournalArticleId;
	}

	public void setTermsOfUseJournalArticleId(Long termsOfUseJournalArticleId) {
		this.termsOfUseJournalArticleId = termsOfUseJournalArticleId;

		_termsOfUseJournalArticleIdSupplier = null;
	}

	@JsonIgnore
	public void setTermsOfUseJournalArticleId(
		UnsafeSupplier<Long, Exception>
			termsOfUseJournalArticleIdUnsafeSupplier) {

		_termsOfUseJournalArticleIdSupplier = () -> {
			try {
				return termsOfUseJournalArticleIdUnsafeSupplier.get();
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
		description = "Internal identifier of a web content article that supplies the terms of use a customer must accept before download; when set and `termsOfUseRequired` is true the inline `termsOfUseContent` is cleared. The article must exist."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long termsOfUseJournalArticleId;

	@JsonIgnore
	private Supplier<Long> _termsOfUseJournalArticleIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether the customer must accept terms of use before downloading; when true the service requires either `termsOfUseJournalArticleId` or non-empty `termsOfUseContent`.",
		example = "true"
	)
	public Boolean getTermsOfUseRequired() {
		if (_termsOfUseRequiredSupplier != null) {
			termsOfUseRequired = _termsOfUseRequiredSupplier.get();

			_termsOfUseRequiredSupplier = null;
		}

		return termsOfUseRequired;
	}

	public void setTermsOfUseRequired(Boolean termsOfUseRequired) {
		this.termsOfUseRequired = termsOfUseRequired;

		_termsOfUseRequiredSupplier = null;
	}

	@JsonIgnore
	public void setTermsOfUseRequired(
		UnsafeSupplier<Boolean, Exception> termsOfUseRequiredUnsafeSupplier) {

		_termsOfUseRequiredSupplier = () -> {
			try {
				return termsOfUseRequiredUnsafeSupplier.get();
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
		description = "Whether the customer must accept terms of use before downloading; when true the service requires either `termsOfUseJournalArticleId` or non-empty `termsOfUseContent`."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean termsOfUseRequired;

	@JsonIgnore
	private Supplier<Boolean> _termsOfUseRequiredSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External URL pointing at the virtual asset when the file is hosted outside the portal; on read the value is the URL of the first attached file entry when present.",
		example = "https://example.com/download.zip"
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
		description = "External URL pointing at the virtual asset when the file is hosted outside the portal; on read the value is the URL of the first attached file entry when present."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String url;

	@JsonIgnore
	private Supplier<String> _urlSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether a sample preview is offered to customers before purchase; when true the service requires either a `sampleAttachment` or a syntactically valid `sampleURL`.",
		example = "true"
	)
	public Boolean getUseSample() {
		if (_useSampleSupplier != null) {
			useSample = _useSampleSupplier.get();

			_useSampleSupplier = null;
		}

		return useSample;
	}

	public void setUseSample(Boolean useSample) {
		this.useSample = useSample;

		_useSampleSupplier = null;
	}

	@JsonIgnore
	public void setUseSample(
		UnsafeSupplier<Boolean, Exception> useSampleUnsafeSupplier) {

		_useSampleSupplier = () -> {
			try {
				return useSampleUnsafeSupplier.get();
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
		description = "Whether a sample preview is offered to customers before purchase; when true the service requires either a `sampleAttachment` or a syntactically valid `sampleURL`."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean useSample;

	@JsonIgnore
	private Supplier<Boolean> _useSampleSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SkuVirtualSettings)) {
			return false;
		}

		SkuVirtualSettings skuVirtualSettings = (SkuVirtualSettings)object;

		return Objects.equals(toString(), skuVirtualSettings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Integer activationStatus = getActivationStatus();

		if (activationStatus != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"activationStatus\": ");

			sb.append(activationStatus);
		}

		Status activationStatusInfo = getActivationStatusInfo();

		if (activationStatusInfo != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"activationStatusInfo\": ");

			sb.append(String.valueOf(activationStatusInfo));
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

		Long duration = getDuration();

		if (duration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"duration\": ");

			sb.append(duration);
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		Integer maxUsages = getMaxUsages();

		if (maxUsages != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxUsages\": ");

			sb.append(maxUsages);
		}

		Boolean override = getOverride();

		if (override != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"override\": ");

			sb.append(override);
		}

		String sampleAttachment = getSampleAttachment();

		if (sampleAttachment != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sampleAttachment\": ");

			sb.append("\"");

			sb.append(_escape(sampleAttachment));

			sb.append("\"");
		}

		String sampleSrc = getSampleSrc();

		if (sampleSrc != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sampleSrc\": ");

			sb.append("\"");

			sb.append(_escape(sampleSrc));

			sb.append("\"");
		}

		String sampleURL = getSampleURL();

		if (sampleURL != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sampleURL\": ");

			sb.append("\"");

			sb.append(_escape(sampleURL));

			sb.append("\"");
		}

		SkuVirtualSettingsFileEntry[] skuVirtualSettingsFileEntries =
			getSkuVirtualSettingsFileEntries();

		if (skuVirtualSettingsFileEntries != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuVirtualSettingsFileEntries\": ");

			sb.append("[");

			for (int i = 0; i < skuVirtualSettingsFileEntries.length; i++) {
				sb.append(String.valueOf(skuVirtualSettingsFileEntries[i]));

				if ((i + 1) < skuVirtualSettingsFileEntries.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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

		Map<String, String> termsOfUseContent = getTermsOfUseContent();

		if (termsOfUseContent != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"termsOfUseContent\": ");

			sb.append(_toJSON(termsOfUseContent));
		}

		String termsOfUseJournalArticleExternalReferenceCode =
			getTermsOfUseJournalArticleExternalReferenceCode();

		if (termsOfUseJournalArticleExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"termsOfUseJournalArticleExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(termsOfUseJournalArticleExternalReferenceCode));

			sb.append("\"");
		}

		String termsOfUseJournalArticleGroupExternalReferenceCode =
			getTermsOfUseJournalArticleGroupExternalReferenceCode();

		if (termsOfUseJournalArticleGroupExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append(
				"\"termsOfUseJournalArticleGroupExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(termsOfUseJournalArticleGroupExternalReferenceCode));

			sb.append("\"");
		}

		Long termsOfUseJournalArticleId = getTermsOfUseJournalArticleId();

		if (termsOfUseJournalArticleId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"termsOfUseJournalArticleId\": ");

			sb.append(termsOfUseJournalArticleId);
		}

		Boolean termsOfUseRequired = getTermsOfUseRequired();

		if (termsOfUseRequired != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"termsOfUseRequired\": ");

			sb.append(termsOfUseRequired);
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

		Boolean useSample = getUseSample();

		if (useSample != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"useSample\": ");

			sb.append(useSample);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.catalog.dto.v1_0.SkuVirtualSettings",
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
// LIFERAY-REST-BUILDER-HASH:-2025957831