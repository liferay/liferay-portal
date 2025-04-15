/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.content.client.dto.v1_0;

import com.liferay.headless.admin.content.client.function.UnsafeSupplier;
import com.liferay.headless.admin.content.client.serdes.v1_0.DisplayPageTemplateSettingsSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class DisplayPageTemplateSettings implements Cloneable, Serializable {

	public static DisplayPageTemplateSettings toDTO(String json) {
		return DisplayPageTemplateSettingsSerDes.toDTO(json);
	}

	public ContentAssociation getContentAssociation() {
		return contentAssociation;
	}

	public void setContentAssociation(ContentAssociation contentAssociation) {
		this.contentAssociation = contentAssociation;
	}

	public void setContentAssociation(
		UnsafeSupplier<ContentAssociation, Exception>
			contentAssociationUnsafeSupplier) {

		try {
			contentAssociation = contentAssociationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ContentAssociation contentAssociation;

	public OpenGraphSettingsMapping getOpenGraphSettingsMapping() {
		return openGraphSettingsMapping;
	}

	public void setOpenGraphSettingsMapping(
		OpenGraphSettingsMapping openGraphSettingsMapping) {

		this.openGraphSettingsMapping = openGraphSettingsMapping;
	}

	public void setOpenGraphSettingsMapping(
		UnsafeSupplier<OpenGraphSettingsMapping, Exception>
			openGraphSettingsMappingUnsafeSupplier) {

		try {
			openGraphSettingsMapping =
				openGraphSettingsMappingUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected OpenGraphSettingsMapping openGraphSettingsMapping;

	public SEOSettingsMapping getSeoSettingsMapping() {
		return seoSettingsMapping;
	}

	public void setSeoSettingsMapping(SEOSettingsMapping seoSettingsMapping) {
		this.seoSettingsMapping = seoSettingsMapping;
	}

	public void setSeoSettingsMapping(
		UnsafeSupplier<SEOSettingsMapping, Exception>
			seoSettingsMappingUnsafeSupplier) {

		try {
			seoSettingsMapping = seoSettingsMappingUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected SEOSettingsMapping seoSettingsMapping;

	@Override
	public DisplayPageTemplateSettings clone()
		throws CloneNotSupportedException {

		return (DisplayPageTemplateSettings)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DisplayPageTemplateSettings)) {
			return false;
		}

		DisplayPageTemplateSettings displayPageTemplateSettings =
			(DisplayPageTemplateSettings)object;

		return Objects.equals(
			toString(), displayPageTemplateSettings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return DisplayPageTemplateSettingsSerDes.toJSON(this);
	}

}