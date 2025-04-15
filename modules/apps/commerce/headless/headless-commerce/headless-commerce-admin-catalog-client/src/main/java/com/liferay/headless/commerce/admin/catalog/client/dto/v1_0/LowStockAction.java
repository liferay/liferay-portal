/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.dto.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.LowStockActionSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class LowStockAction implements Cloneable, Serializable {

	public static LowStockAction toDTO(String json) {
		return LowStockActionSerDes.toDTO(json);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setKey(UnsafeSupplier<String, Exception> keyUnsafeSupplier) {
		try {
			key = keyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String key;

	public Map<String, String> getLabel() {
		return label;
	}

	public void setLabel(Map<String, String> label) {
		this.label = label;
	}

	public void setLabel(
		UnsafeSupplier<Map<String, String>, Exception> labelUnsafeSupplier) {

		try {
			label = labelUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> label;

	@Override
	public LowStockAction clone() throws CloneNotSupportedException {
		return (LowStockAction)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof LowStockAction)) {
			return false;
		}

		LowStockAction lowStockAction = (LowStockAction)object;

		return Objects.equals(toString(), lowStockAction.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return LowStockActionSerDes.toJSON(this);
	}

}