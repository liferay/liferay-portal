/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.UtilityPageSettingsSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class UtilityPageSettings implements Cloneable, Serializable {

	public static UtilityPageSettings toDTO(String json) {
		return UtilityPageSettingsSerDes.toDTO(json);
	}

	public UtilityPageSEOSettings getSeoSettings() {
		return seoSettings;
	}

	public void setSeoSettings(UtilityPageSEOSettings seoSettings) {
		this.seoSettings = seoSettings;
	}

	public void setSeoSettings(
		UnsafeSupplier<UtilityPageSEOSettings, Exception>
			seoSettingsUnsafeSupplier) {

		try {
			seoSettings = seoSettingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected UtilityPageSEOSettings seoSettings;

	@Override
	public UtilityPageSettings clone() throws CloneNotSupportedException {
		return (UtilityPageSettings)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof UtilityPageSettings)) {
			return false;
		}

		UtilityPageSettings utilityPageSettings = (UtilityPageSettings)object;

		return Objects.equals(toString(), utilityPageSettings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return UtilityPageSettingsSerDes.toJSON(this);
	}

}