/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.client.dto.v1_0;

import com.liferay.headless.commerce.machine.learning.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.machine.learning.client.serdes.v1_0.ProductOptionSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Generated("")
public class ProductOption implements Cloneable, Serializable {

	public static ProductOption toDTO(String json) {
		return ProductOptionSerDes.toDTO(json);
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

	public String getOptionKey() {
		return optionKey;
	}

	public void setOptionKey(String optionKey) {
		this.optionKey = optionKey;
	}

	public void setOptionKey(
		UnsafeSupplier<String, Exception> optionKeyUnsafeSupplier) {

		try {
			optionKey = optionKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String optionKey;

	public Map[] getValues() {
		return values;
	}

	public void setValues(Map[] values) {
		this.values = values;
	}

	public void setValues(
		UnsafeSupplier<Map[], Exception> valuesUnsafeSupplier) {

		try {
			values = valuesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map[] values;

	@Override
	public ProductOption clone() throws CloneNotSupportedException {
		return (ProductOption)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProductOption)) {
			return false;
		}

		ProductOption productOption = (ProductOption)object;

		return Objects.equals(toString(), productOption.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ProductOptionSerDes.toJSON(this);
	}

}