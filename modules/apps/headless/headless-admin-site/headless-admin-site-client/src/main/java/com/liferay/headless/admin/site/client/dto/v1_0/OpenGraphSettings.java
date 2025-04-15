/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.OpenGraphSettingsSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class OpenGraphSettings implements Cloneable, Serializable {

	public static OpenGraphSettings toDTO(String json) {
		return OpenGraphSettingsSerDes.toDTO(json);
	}

	public Map<String, String> getDescription_i18n() {
		return description_i18n;
	}

	public void setDescription_i18n(Map<String, String> description_i18n) {
		this.description_i18n = description_i18n;
	}

	public void setDescription_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			description_i18nUnsafeSupplier) {

		try {
			description_i18n = description_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> description_i18n;

	public ItemExternalReference getImage() {
		return image;
	}

	public void setImage(ItemExternalReference image) {
		this.image = image;
	}

	public void setImage(
		UnsafeSupplier<ItemExternalReference, Exception> imageUnsafeSupplier) {

		try {
			image = imageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ItemExternalReference image;

	public Map<String, String> getImageAlt_i18n() {
		return imageAlt_i18n;
	}

	public void setImageAlt_i18n(Map<String, String> imageAlt_i18n) {
		this.imageAlt_i18n = imageAlt_i18n;
	}

	public void setImageAlt_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			imageAlt_i18nUnsafeSupplier) {

		try {
			imageAlt_i18n = imageAlt_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> imageAlt_i18n;

	public Map<String, String> getTitle_i18n() {
		return title_i18n;
	}

	public void setTitle_i18n(Map<String, String> title_i18n) {
		this.title_i18n = title_i18n;
	}

	public void setTitle_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			title_i18nUnsafeSupplier) {

		try {
			title_i18n = title_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> title_i18n;

	@Override
	public OpenGraphSettings clone() throws CloneNotSupportedException {
		return (OpenGraphSettings)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof OpenGraphSettings)) {
			return false;
		}

		OpenGraphSettings openGraphSettings = (OpenGraphSettings)object;

		return Objects.equals(toString(), openGraphSettings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return OpenGraphSettingsSerDes.toJSON(this);
	}

}