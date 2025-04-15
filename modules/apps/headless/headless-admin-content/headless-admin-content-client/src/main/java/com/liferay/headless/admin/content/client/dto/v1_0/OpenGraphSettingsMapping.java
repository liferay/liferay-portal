/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.content.client.dto.v1_0;

import com.liferay.headless.admin.content.client.function.UnsafeSupplier;
import com.liferay.headless.admin.content.client.serdes.v1_0.OpenGraphSettingsMappingSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class OpenGraphSettingsMapping implements Cloneable, Serializable {

	public static OpenGraphSettingsMapping toDTO(String json) {
		return OpenGraphSettingsMappingSerDes.toDTO(json);
	}

	public String getDescriptionMappingFieldKey() {
		return descriptionMappingFieldKey;
	}

	public void setDescriptionMappingFieldKey(
		String descriptionMappingFieldKey) {

		this.descriptionMappingFieldKey = descriptionMappingFieldKey;
	}

	public void setDescriptionMappingFieldKey(
		UnsafeSupplier<String, Exception>
			descriptionMappingFieldKeyUnsafeSupplier) {

		try {
			descriptionMappingFieldKey =
				descriptionMappingFieldKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String descriptionMappingFieldKey;

	public String getImageAltMappingFieldKey() {
		return imageAltMappingFieldKey;
	}

	public void setImageAltMappingFieldKey(String imageAltMappingFieldKey) {
		this.imageAltMappingFieldKey = imageAltMappingFieldKey;
	}

	public void setImageAltMappingFieldKey(
		UnsafeSupplier<String, Exception>
			imageAltMappingFieldKeyUnsafeSupplier) {

		try {
			imageAltMappingFieldKey =
				imageAltMappingFieldKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String imageAltMappingFieldKey;

	public String getImageMappingFieldKey() {
		return imageMappingFieldKey;
	}

	public void setImageMappingFieldKey(String imageMappingFieldKey) {
		this.imageMappingFieldKey = imageMappingFieldKey;
	}

	public void setImageMappingFieldKey(
		UnsafeSupplier<String, Exception> imageMappingFieldKeyUnsafeSupplier) {

		try {
			imageMappingFieldKey = imageMappingFieldKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String imageMappingFieldKey;

	public String getTitleMappingFieldKey() {
		return titleMappingFieldKey;
	}

	public void setTitleMappingFieldKey(String titleMappingFieldKey) {
		this.titleMappingFieldKey = titleMappingFieldKey;
	}

	public void setTitleMappingFieldKey(
		UnsafeSupplier<String, Exception> titleMappingFieldKeyUnsafeSupplier) {

		try {
			titleMappingFieldKey = titleMappingFieldKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String titleMappingFieldKey;

	@Override
	public OpenGraphSettingsMapping clone() throws CloneNotSupportedException {
		return (OpenGraphSettingsMapping)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof OpenGraphSettingsMapping)) {
			return false;
		}

		OpenGraphSettingsMapping openGraphSettingsMapping =
			(OpenGraphSettingsMapping)object;

		return Objects.equals(toString(), openGraphSettingsMapping.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return OpenGraphSettingsMappingSerDes.toJSON(this);
	}

}