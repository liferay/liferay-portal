/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.batch.engine.client.dto.v1_0;

import com.liferay.headless.batch.engine.client.function.UnsafeSupplier;
import com.liferay.headless.batch.engine.client.serdes.v1_0.FailedItemSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Ivica Cardic
 * @generated
 */
@Generated("")
public class FailedItem implements Cloneable, Serializable {

	public static FailedItem toDTO(String json) {
		return FailedItemSerDes.toDTO(json);
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public void setItem(UnsafeSupplier<String, Exception> itemUnsafeSupplier) {
		try {
			item = itemUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String item;

	public Integer getItemIndex() {
		return itemIndex;
	}

	public void setItemIndex(Integer itemIndex) {
		this.itemIndex = itemIndex;
	}

	public void setItemIndex(
		UnsafeSupplier<Integer, Exception> itemIndexUnsafeSupplier) {

		try {
			itemIndex = itemIndexUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer itemIndex;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setMessage(
		UnsafeSupplier<String, Exception> messageUnsafeSupplier) {

		try {
			message = messageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String message;

	@Override
	public FailedItem clone() throws CloneNotSupportedException {
		return (FailedItem)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FailedItem)) {
			return false;
		}

		FailedItem failedItem = (FailedItem)object;

		return Objects.equals(toString(), failedItem.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FailedItemSerDes.toJSON(this);
	}

}