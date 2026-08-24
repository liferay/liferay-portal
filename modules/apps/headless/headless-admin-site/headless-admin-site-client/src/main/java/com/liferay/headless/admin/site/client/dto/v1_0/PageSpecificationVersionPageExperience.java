/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.PageSpecificationVersionPageExperienceSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class PageSpecificationVersionPageExperience
	implements Cloneable, Serializable {

	public static PageSpecificationVersionPageExperience toDTO(String json) {
		return PageSpecificationVersionPageExperienceSerDes.toDTO(json);
	}

	public String[] getAvailablePreviewLanguageIds() {
		return availablePreviewLanguageIds;
	}

	public void setAvailablePreviewLanguageIds(
		String[] availablePreviewLanguageIds) {

		this.availablePreviewLanguageIds = availablePreviewLanguageIds;
	}

	public void setAvailablePreviewLanguageIds(
		UnsafeSupplier<String[], Exception>
			availablePreviewLanguageIdsUnsafeSupplier) {

		try {
			availablePreviewLanguageIds =
				availablePreviewLanguageIdsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] availablePreviewLanguageIds;

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

	public Map<String, String> getName_i18n() {
		return name_i18n;
	}

	public void setName_i18n(Map<String, String> name_i18n) {
		this.name_i18n = name_i18n;
	}

	public void setName_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			name_i18nUnsafeSupplier) {

		try {
			name_i18n = name_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> name_i18n;

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public void setPriority(
		UnsafeSupplier<Integer, Exception> priorityUnsafeSupplier) {

		try {
			priority = priorityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer priority;

	@Override
	public PageSpecificationVersionPageExperience clone()
		throws CloneNotSupportedException {

		return (PageSpecificationVersionPageExperience)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PageSpecificationVersionPageExperience)) {
			return false;
		}

		PageSpecificationVersionPageExperience
			pageSpecificationVersionPageExperience =
				(PageSpecificationVersionPageExperience)object;

		return Objects.equals(
			toString(), pageSpecificationVersionPageExperience.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PageSpecificationVersionPageExperienceSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:1336990203