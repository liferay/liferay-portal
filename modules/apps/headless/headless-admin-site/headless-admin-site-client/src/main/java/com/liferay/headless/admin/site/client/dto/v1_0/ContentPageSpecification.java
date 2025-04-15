/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.ContentPageSpecificationSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class ContentPageSpecification
	extends PageSpecification implements Cloneable, Serializable {

	public static ContentPageSpecification toDTO(String json) {
		return ContentPageSpecificationSerDes.toDTO(json);
	}

	public String getDraftContentPageSpecificationExternalReferenceCode() {
		return draftContentPageSpecificationExternalReferenceCode;
	}

	public void setDraftContentPageSpecificationExternalReferenceCode(
		String draftContentPageSpecificationExternalReferenceCode) {

		this.draftContentPageSpecificationExternalReferenceCode =
			draftContentPageSpecificationExternalReferenceCode;
	}

	public void setDraftContentPageSpecificationExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			draftContentPageSpecificationExternalReferenceCodeUnsafeSupplier) {

		try {
			draftContentPageSpecificationExternalReferenceCode =
				draftContentPageSpecificationExternalReferenceCodeUnsafeSupplier.
					get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String draftContentPageSpecificationExternalReferenceCode;

	public PageExperience[] getPageExperiences() {
		return pageExperiences;
	}

	public void setPageExperiences(PageExperience[] pageExperiences) {
		this.pageExperiences = pageExperiences;
	}

	public void setPageExperiences(
		UnsafeSupplier<PageExperience[], Exception>
			pageExperiencesUnsafeSupplier) {

		try {
			pageExperiences = pageExperiencesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PageExperience[] pageExperiences;

	@Override
	public ContentPageSpecification clone() throws CloneNotSupportedException {
		return (ContentPageSpecification)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ContentPageSpecification)) {
			return false;
		}

		ContentPageSpecification contentPageSpecification =
			(ContentPageSpecification)object;

		return Objects.equals(toString(), contentPageSpecification.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ContentPageSpecificationSerDes.toJSON(this);
	}

}