/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.client.dto.v1_0;

import com.liferay.headless.asset.library.client.function.UnsafeSupplier;
import com.liferay.headless.asset.library.client.serdes.v1_0.SettingsSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Roberto Díaz
 * @generated
 */
@Generated("")
public class Settings implements Cloneable, Serializable {

	public static Settings toDTO(String json) {
		return SettingsSerDes.toDTO(json);
	}

	public Boolean getAutoTaggingEnabled() {
		return autoTaggingEnabled;
	}

	public void setAutoTaggingEnabled(Boolean autoTaggingEnabled) {
		this.autoTaggingEnabled = autoTaggingEnabled;
	}

	public void setAutoTaggingEnabled(
		UnsafeSupplier<Boolean, Exception> autoTaggingEnabledUnsafeSupplier) {

		try {
			autoTaggingEnabled = autoTaggingEnabledUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean autoTaggingEnabled;

	public String[] getAvailableLanguageIds() {
		return availableLanguageIds;
	}

	public void setAvailableLanguageIds(String[] availableLanguageIds) {
		this.availableLanguageIds = availableLanguageIds;
	}

	public void setAvailableLanguageIds(
		UnsafeSupplier<String[], Exception>
			availableLanguageIdsUnsafeSupplier) {

		try {
			availableLanguageIds = availableLanguageIdsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] availableLanguageIds;

	public String getDefaultLanguageId() {
		return defaultLanguageId;
	}

	public void setDefaultLanguageId(String defaultLanguageId) {
		this.defaultLanguageId = defaultLanguageId;
	}

	public void setDefaultLanguageId(
		UnsafeSupplier<String, Exception> defaultLanguageIdUnsafeSupplier) {

		try {
			defaultLanguageId = defaultLanguageIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String defaultLanguageId;

	public String getLogoColor() {
		return logoColor;
	}

	public void setLogoColor(String logoColor) {
		this.logoColor = logoColor;
	}

	public void setLogoColor(
		UnsafeSupplier<String, Exception> logoColorUnsafeSupplier) {

		try {
			logoColor = logoColorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String logoColor;

	public MimeTypeLimit[] getMimeTypeLimits() {
		return mimeTypeLimits;
	}

	public void setMimeTypeLimits(MimeTypeLimit[] mimeTypeLimits) {
		this.mimeTypeLimits = mimeTypeLimits;
	}

	public void setMimeTypeLimits(
		UnsafeSupplier<MimeTypeLimit[], Exception>
			mimeTypeLimitsUnsafeSupplier) {

		try {
			mimeTypeLimits = mimeTypeLimitsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected MimeTypeLimit[] mimeTypeLimits;

	public Boolean getSharingEnabled() {
		return sharingEnabled;
	}

	public void setSharingEnabled(Boolean sharingEnabled) {
		this.sharingEnabled = sharingEnabled;
	}

	public void setSharingEnabled(
		UnsafeSupplier<Boolean, Exception> sharingEnabledUnsafeSupplier) {

		try {
			sharingEnabled = sharingEnabledUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean sharingEnabled;

	public Boolean getUseCustomLanguages() {
		return useCustomLanguages;
	}

	public void setUseCustomLanguages(Boolean useCustomLanguages) {
		this.useCustomLanguages = useCustomLanguages;
	}

	public void setUseCustomLanguages(
		UnsafeSupplier<Boolean, Exception> useCustomLanguagesUnsafeSupplier) {

		try {
			useCustomLanguages = useCustomLanguagesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean useCustomLanguages;

	@Override
	public Settings clone() throws CloneNotSupportedException {
		return (Settings)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Settings)) {
			return false;
		}

		Settings settings = (Settings)object;

		return Objects.equals(toString(), settings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SettingsSerDes.toJSON(this);
	}

}