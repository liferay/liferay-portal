/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.PageExperienceSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class PageExperience implements Cloneable, Serializable {

	public static PageExperience toDTO(String json) {
		return PageExperienceSerDes.toDTO(json);
	}

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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setKey(UnsafeSupplier<String, Exception> keyUnsafeSupplier) {
		try {
			key = keyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String key;

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

	public PageElement[] getPageElements() {
		return pageElements;
	}

	public void setPageElements(PageElement[] pageElements) {
		this.pageElements = pageElements;
	}

	public void setPageElements(
		UnsafeSupplier<PageElement[], Exception> pageElementsUnsafeSupplier) {

		try {
			pageElements = pageElementsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PageElement[] pageElements;

	public PageRule[] getPageRules() {
		return pageRules;
	}

	public void setPageRules(PageRule[] pageRules) {
		this.pageRules = pageRules;
	}

	public void setPageRules(
		UnsafeSupplier<PageRule[], Exception> pageRulesUnsafeSupplier) {

		try {
			pageRules = pageRulesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PageRule[] pageRules;

	public String getPageSpecificationExternalReferenceCode() {
		return pageSpecificationExternalReferenceCode;
	}

	public void setPageSpecificationExternalReferenceCode(
		String pageSpecificationExternalReferenceCode) {

		this.pageSpecificationExternalReferenceCode =
			pageSpecificationExternalReferenceCode;
	}

	public void setPageSpecificationExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			pageSpecificationExternalReferenceCodeUnsafeSupplier) {

		try {
			pageSpecificationExternalReferenceCode =
				pageSpecificationExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String pageSpecificationExternalReferenceCode;

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

	public String getSegmentExternalReferenceCode() {
		return segmentExternalReferenceCode;
	}

	public void setSegmentExternalReferenceCode(
		String segmentExternalReferenceCode) {

		this.segmentExternalReferenceCode = segmentExternalReferenceCode;
	}

	public void setSegmentExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			segmentExternalReferenceCodeUnsafeSupplier) {

		try {
			segmentExternalReferenceCode =
				segmentExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String segmentExternalReferenceCode;

	@Override
	public PageExperience clone() throws CloneNotSupportedException {
		return (PageExperience)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PageExperience)) {
			return false;
		}

		PageExperience pageExperience = (PageExperience)object;

		return Objects.equals(toString(), pageExperience.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PageExperienceSerDes.toJSON(this);
	}

}