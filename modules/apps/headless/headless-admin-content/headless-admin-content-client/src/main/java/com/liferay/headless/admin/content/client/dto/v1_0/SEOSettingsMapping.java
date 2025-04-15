/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.content.client.dto.v1_0;

import com.liferay.headless.admin.content.client.function.UnsafeSupplier;
import com.liferay.headless.admin.content.client.serdes.v1_0.SEOSettingsMappingSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class SEOSettingsMapping implements Cloneable, Serializable {

	public static SEOSettingsMapping toDTO(String json) {
		return SEOSettingsMappingSerDes.toDTO(json);
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

	public String getHtmlTitleMappingFieldKey() {
		return htmlTitleMappingFieldKey;
	}

	public void setHtmlTitleMappingFieldKey(String htmlTitleMappingFieldKey) {
		this.htmlTitleMappingFieldKey = htmlTitleMappingFieldKey;
	}

	public void setHtmlTitleMappingFieldKey(
		UnsafeSupplier<String, Exception>
			htmlTitleMappingFieldKeyUnsafeSupplier) {

		try {
			htmlTitleMappingFieldKey =
				htmlTitleMappingFieldKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String htmlTitleMappingFieldKey;

	public String getRobots() {
		return robots;
	}

	public void setRobots(String robots) {
		this.robots = robots;
	}

	public void setRobots(
		UnsafeSupplier<String, Exception> robotsUnsafeSupplier) {

		try {
			robots = robotsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String robots;

	public Map<String, String> getRobots_i18n() {
		return robots_i18n;
	}

	public void setRobots_i18n(Map<String, String> robots_i18n) {
		this.robots_i18n = robots_i18n;
	}

	public void setRobots_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			robots_i18nUnsafeSupplier) {

		try {
			robots_i18n = robots_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> robots_i18n;

	@Override
	public SEOSettingsMapping clone() throws CloneNotSupportedException {
		return (SEOSettingsMapping)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SEOSettingsMapping)) {
			return false;
		}

		SEOSettingsMapping seoSettingsMapping = (SEOSettingsMapping)object;

		return Objects.equals(toString(), seoSettingsMapping.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SEOSettingsMappingSerDes.toJSON(this);
	}

}