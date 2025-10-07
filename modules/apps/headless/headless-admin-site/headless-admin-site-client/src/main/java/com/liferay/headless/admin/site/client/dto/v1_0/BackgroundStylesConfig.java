/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.BackgroundStylesConfigSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class BackgroundStylesConfig implements Cloneable, Serializable {

	public static BackgroundStylesConfig toDTO(String json) {
		return BackgroundStylesConfigSerDes.toDTO(json);
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public void setBackgroundColor(
		UnsafeSupplier<String, Exception> backgroundColorUnsafeSupplier) {

		try {
			backgroundColor = backgroundColorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String backgroundColor;

	@Override
	public BackgroundStylesConfig clone() throws CloneNotSupportedException {
		return (BackgroundStylesConfig)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof BackgroundStylesConfig)) {
			return false;
		}

		BackgroundStylesConfig backgroundStylesConfig =
			(BackgroundStylesConfig)object;

		return Objects.equals(toString(), backgroundStylesConfig.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return BackgroundStylesConfigSerDes.toJSON(this);
	}

}