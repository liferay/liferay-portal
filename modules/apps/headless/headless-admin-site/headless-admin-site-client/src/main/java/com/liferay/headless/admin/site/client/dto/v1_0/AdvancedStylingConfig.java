/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.AdvancedStylingConfigSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class AdvancedStylingConfig implements Cloneable, Serializable {

	public static AdvancedStylingConfig toDTO(String json) {
		return AdvancedStylingConfigSerDes.toDTO(json);
	}

	public String getCustomCSS() {
		return customCSS;
	}

	public void setCustomCSS(String customCSS) {
		this.customCSS = customCSS;
	}

	public void setCustomCSS(
		UnsafeSupplier<String, Exception> customCSSUnsafeSupplier) {

		try {
			customCSS = customCSSUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String customCSS;

	public String getCustomCSSClassNames() {
		return customCSSClassNames;
	}

	public void setCustomCSSClassNames(String customCSSClassNames) {
		this.customCSSClassNames = customCSSClassNames;
	}

	public void setCustomCSSClassNames(
		UnsafeSupplier<String, Exception> customCSSClassNamesUnsafeSupplier) {

		try {
			customCSSClassNames = customCSSClassNamesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String customCSSClassNames;

	@Override
	public AdvancedStylingConfig clone() throws CloneNotSupportedException {
		return (AdvancedStylingConfig)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AdvancedStylingConfig)) {
			return false;
		}

		AdvancedStylingConfig advancedStylingConfig =
			(AdvancedStylingConfig)object;

		return Objects.equals(toString(), advancedStylingConfig.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return AdvancedStylingConfigSerDes.toJSON(this);
	}

}