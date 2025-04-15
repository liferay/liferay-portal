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

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
@GraphQLName("ProductVirtualSettings")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ProductVirtualSettings")
public class ProductVirtualSettings implements Serializable {

	public static ProductVirtualSettings toDTO(String json) {
		return ObjectMapperUtil.readValue(ProductVirtualSettings.class, json);
	}

	public static ProductVirtualSettings unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			ProductVirtualSettings.class, json);
	}

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer activationStatus;

	@JsonIgnore
	private Supplier<Integer> _activationStatusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Status activationStatusInfo;

	@JsonIgnore
	private Supplier<Status> _activationStatusInfoSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Base64 encoded file"
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

	@GraphQLField(description = "Base64 encoded file")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String attachment;

	@JsonIgnore
	private Supplier<String> _attachmentSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Number of days to download the attachment"
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

	@GraphQLField(description = "Number of days to download the attachment")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long duration;

	@JsonIgnore
	private Supplier<Long> _durationSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Number of downloads available for attachment"
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

	@GraphQLField(description = "Number of downloads available for attachment")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer maxUsages;

	@JsonIgnore
	private Supplier<Integer> _maxUsagesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ProductVirtualSettingsFileEntry[]
		getProductVirtualSettingsFileEntries() {

		if (_productVirtualSettingsFileEntriesSupplier != null) {
			productVirtualSettingsFileEntries =
				_productVirtualSettingsFileEntriesSupplier.get();

			_productVirtualSettingsFileEntriesSupplier = null;
		}

		return productVirtualSettingsFileEntries;
	}

	public void setProductVirtualSettingsFileEntries(
		ProductVirtualSettingsFileEntry[] productVirtualSettingsFileEntries) {

		this.productVirtualSettingsFileEntries =
			productVirtualSettingsFileEntries;

		_productVirtualSettingsFileEntriesSupplier = null;
	}

	@JsonIgnore
	public void setProductVirtualSettingsFileEntries(
		UnsafeSupplier<ProductVirtualSettingsFileEntry[], Exception>
			productVirtualSettingsFileEntriesUnsafeSupplier) {

		_productVirtualSettingsFileEntriesSupplier = () -> {
			try {
				return productVirtualSettingsFileEntriesUnsafeSupplier.get();
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
	protected ProductVirtualSettingsFileEntry[]
		productVirtualSettingsFileEntries;

	@JsonIgnore
	private Supplier<ProductVirtualSettingsFileEntry[]>
		_productVirtualSettingsFileEntriesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Base64 encoded sample file"
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

	@GraphQLField(description = "Base64 encoded sample file")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String sampleAttachment;

	@JsonIgnore
	private Supplier<String> _sampleAttachmentSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "URL to download the sample file"
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

	@GraphQLField(description = "URL to download the sample file")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String sampleSrc;

	@JsonIgnore
	private Supplier<String> _sampleSrcSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "URL of the sample file"
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

	@GraphQLField(description = "URL of the sample file")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String sampleURL;

	@JsonIgnore
	private Supplier<String> _sampleURLSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "URL to download the file"
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

	@GraphQLField(description = "URL to download the file")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String src;

	@JsonIgnore
	private Supplier<String> _srcSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Terms of Use content",
		example = "{en_US=Croatia, hr_HR=Hrvatska, hu_HU=Horvatorszag}"
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

	@GraphQLField(description = "Terms of Use content")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> termsOfUseContent;

	@JsonIgnore
	private Supplier<Map<String, String>> _termsOfUseContentSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Terms of Use related Article Id"
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

	@GraphQLField(description = "Terms of Use related Article Id")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long termsOfUseJournalArticleId;

	@JsonIgnore
	private Supplier<Long> _termsOfUseJournalArticleIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Terms of Use required"
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

	@GraphQLField(description = "Terms of Use required")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean termsOfUseRequired;

	@JsonIgnore
	private Supplier<Boolean> _termsOfUseRequiredSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(description = "URL of the file")
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

	@GraphQLField(description = "URL of the file")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String url;

	@JsonIgnore
	private Supplier<String> _urlSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Enable sample file"
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

	@GraphQLField(description = "Enable sample file")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean useSample;

	@JsonIgnore
	private Supplier<Boolean> _useSampleSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProductVirtualSettings)) {
			return false;
		}

		ProductVirtualSettings productVirtualSettings =
			(ProductVirtualSettings)object;

		return Objects.equals(toString(), productVirtualSettings.toString());
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

		ProductVirtualSettingsFileEntry[] productVirtualSettingsFileEntries =
			getProductVirtualSettingsFileEntries();

		if (productVirtualSettingsFileEntries != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productVirtualSettingsFileEntries\": ");

			sb.append("[");

			for (int i = 0; i < productVirtualSettingsFileEntries.length; i++) {
				sb.append(String.valueOf(productVirtualSettingsFileEntries[i]));

				if ((i + 1) < productVirtualSettingsFileEntries.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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
		defaultValue = "com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductVirtualSettings",
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