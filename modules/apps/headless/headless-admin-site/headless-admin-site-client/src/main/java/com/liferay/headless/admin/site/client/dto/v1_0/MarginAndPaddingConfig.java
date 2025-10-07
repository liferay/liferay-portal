/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.MarginAndPaddingConfigSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class MarginAndPaddingConfig implements Cloneable, Serializable {

	public static MarginAndPaddingConfig toDTO(String json) {
		return MarginAndPaddingConfigSerDes.toDTO(json);
	}

	public Object getMargin() {
		return margin;
	}

	public void setMargin(Object margin) {
		this.margin = margin;
	}

	public void setMargin(
		UnsafeSupplier<Object, Exception> marginUnsafeSupplier) {

		try {
			margin = marginUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object margin;

	public Object getPadding() {
		return padding;
	}

	public void setPadding(Object padding) {
		this.padding = padding;
	}

	public void setPadding(
		UnsafeSupplier<Object, Exception> paddingUnsafeSupplier) {

		try {
			padding = paddingUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object padding;

	@Override
	public MarginAndPaddingConfig clone() throws CloneNotSupportedException {
		return (MarginAndPaddingConfig)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof MarginAndPaddingConfig)) {
			return false;
		}

		MarginAndPaddingConfig marginAndPaddingConfig =
			(MarginAndPaddingConfig)object;

		return Objects.equals(toString(), marginAndPaddingConfig.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return MarginAndPaddingConfigSerDes.toJSON(this);
	}

}