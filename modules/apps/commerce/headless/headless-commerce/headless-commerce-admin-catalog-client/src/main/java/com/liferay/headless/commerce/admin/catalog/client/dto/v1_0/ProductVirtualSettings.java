/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.dto.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.ProductVirtualSettingsSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class ProductVirtualSettings implements Cloneable, Serializable {

	public static ProductVirtualSettings toDTO(String json) {
		return ProductVirtualSettingsSerDes.toDTO(json);
	}

	public Integer getActivationStatus() {
		return activationStatus;
	}

	public void setActivationStatus(Integer activationStatus) {
		this.activationStatus = activationStatus;
	}

	public void setActivationStatus(
		UnsafeSupplier<Integer, Exception> activationStatusUnsafeSupplier) {

		try {
			activationStatus = activationStatusUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer activationStatus;

	public Status getActivationStatusInfo() {
		return activationStatusInfo;
	}

	public void setActivationStatusInfo(Status activationStatusInfo) {
		this.activationStatusInfo = activationStatusInfo;
	}

	public void setActivationStatusInfo(
		UnsafeSupplier<Status, Exception> activationStatusInfoUnsafeSupplier) {

		try {
			activationStatusInfo = activationStatusInfoUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Status activationStatusInfo;

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public void setAttachment(
		UnsafeSupplier<String, Exception> attachmentUnsafeSupplier) {

		try {
			attachment = attachmentUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String attachment;

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public void setDuration(
		UnsafeSupplier<Long, Exception> durationUnsafeSupplier) {

		try {
			duration = durationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long duration;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public Integer getMaxUsages() {
		return maxUsages;
	}

	public void setMaxUsages(Integer maxUsages) {
		this.maxUsages = maxUsages;
	}

	public void setMaxUsages(
		UnsafeSupplier<Integer, Exception> maxUsagesUnsafeSupplier) {

		try {
			maxUsages = maxUsagesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer maxUsages;

	public ProductVirtualSettingsFileEntry[]
		getProductVirtualSettingsFileEntries() {

		return productVirtualSettingsFileEntries;
	}

	public void setProductVirtualSettingsFileEntries(
		ProductVirtualSettingsFileEntry[] productVirtualSettingsFileEntries) {

		this.productVirtualSettingsFileEntries =
			productVirtualSettingsFileEntries;
	}

	public void setProductVirtualSettingsFileEntries(
		UnsafeSupplier<ProductVirtualSettingsFileEntry[], Exception>
			productVirtualSettingsFileEntriesUnsafeSupplier) {

		try {
			productVirtualSettingsFileEntries =
				productVirtualSettingsFileEntriesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ProductVirtualSettingsFileEntry[]
		productVirtualSettingsFileEntries;

	public String getSampleAttachment() {
		return sampleAttachment;
	}

	public void setSampleAttachment(String sampleAttachment) {
		this.sampleAttachment = sampleAttachment;
	}

	public void setSampleAttachment(
		UnsafeSupplier<String, Exception> sampleAttachmentUnsafeSupplier) {

		try {
			sampleAttachment = sampleAttachmentUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String sampleAttachment;

	public String getSampleSrc() {
		return sampleSrc;
	}

	public void setSampleSrc(String sampleSrc) {
		this.sampleSrc = sampleSrc;
	}

	public void setSampleSrc(
		UnsafeSupplier<String, Exception> sampleSrcUnsafeSupplier) {

		try {
			sampleSrc = sampleSrcUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String sampleSrc;

	public String getSampleURL() {
		return sampleURL;
	}

	public void setSampleURL(String sampleURL) {
		this.sampleURL = sampleURL;
	}

	public void setSampleURL(
		UnsafeSupplier<String, Exception> sampleURLUnsafeSupplier) {

		try {
			sampleURL = sampleURLUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String sampleURL;

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public void setSrc(UnsafeSupplier<String, Exception> srcUnsafeSupplier) {
		try {
			src = srcUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String src;

	public Map<String, String> getTermsOfUseContent() {
		return termsOfUseContent;
	}

	public void setTermsOfUseContent(Map<String, String> termsOfUseContent) {
		this.termsOfUseContent = termsOfUseContent;
	}

	public void setTermsOfUseContent(
		UnsafeSupplier<Map<String, String>, Exception>
			termsOfUseContentUnsafeSupplier) {

		try {
			termsOfUseContent = termsOfUseContentUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> termsOfUseContent;

	public Long getTermsOfUseJournalArticleId() {
		return termsOfUseJournalArticleId;
	}

	public void setTermsOfUseJournalArticleId(Long termsOfUseJournalArticleId) {
		this.termsOfUseJournalArticleId = termsOfUseJournalArticleId;
	}

	public void setTermsOfUseJournalArticleId(
		UnsafeSupplier<Long, Exception>
			termsOfUseJournalArticleIdUnsafeSupplier) {

		try {
			termsOfUseJournalArticleId =
				termsOfUseJournalArticleIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long termsOfUseJournalArticleId;

	public Boolean getTermsOfUseRequired() {
		return termsOfUseRequired;
	}

	public void setTermsOfUseRequired(Boolean termsOfUseRequired) {
		this.termsOfUseRequired = termsOfUseRequired;
	}

	public void setTermsOfUseRequired(
		UnsafeSupplier<Boolean, Exception> termsOfUseRequiredUnsafeSupplier) {

		try {
			termsOfUseRequired = termsOfUseRequiredUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean termsOfUseRequired;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUrl(UnsafeSupplier<String, Exception> urlUnsafeSupplier) {
		try {
			url = urlUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String url;

	public Boolean getUseSample() {
		return useSample;
	}

	public void setUseSample(Boolean useSample) {
		this.useSample = useSample;
	}

	public void setUseSample(
		UnsafeSupplier<Boolean, Exception> useSampleUnsafeSupplier) {

		try {
			useSample = useSampleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean useSample;

	@Override
	public ProductVirtualSettings clone() throws CloneNotSupportedException {
		return (ProductVirtualSettings)super.clone();
	}

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
		return ProductVirtualSettingsSerDes.toJSON(this);
	}

}