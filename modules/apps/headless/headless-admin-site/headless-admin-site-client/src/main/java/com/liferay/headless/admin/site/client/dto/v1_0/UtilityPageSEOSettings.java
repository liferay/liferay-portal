/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.UtilityPageSEOSettingsSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class UtilityPageSEOSettings implements Cloneable, Serializable {

	public static UtilityPageSEOSettings toDTO(String json) {
		return UtilityPageSEOSettingsSerDes.toDTO(json);
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

	public Map<String, String> getHtmlTitle_i18n() {
		return htmlTitle_i18n;
	}

	public void setHtmlTitle_i18n(Map<String, String> htmlTitle_i18n) {
		this.htmlTitle_i18n = htmlTitle_i18n;
	}

	public void setHtmlTitle_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			htmlTitle_i18nUnsafeSupplier) {

		try {
			htmlTitle_i18n = htmlTitle_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> htmlTitle_i18n;

	@Override
	public UtilityPageSEOSettings clone() throws CloneNotSupportedException {
		return (UtilityPageSEOSettings)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof UtilityPageSEOSettings)) {
			return false;
		}

		UtilityPageSEOSettings utilityPageSEOSettings =
			(UtilityPageSEOSettings)object;

		return Objects.equals(toString(), utilityPageSEOSettings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return UtilityPageSEOSettingsSerDes.toJSON(this);
	}

}