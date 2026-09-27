/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.ItemValueSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class ItemValue implements Cloneable, Serializable {

	public static ItemValue toDTO(String json) {
		return ItemValueSerDes.toDTO(json);
	}

	public ItemExternalReference getItemExternalReference() {
		return itemExternalReference;
	}

	public void setItemExternalReference(
		ItemExternalReference itemExternalReference) {

		this.itemExternalReference = itemExternalReference;
	}

	public void setItemExternalReference(
		UnsafeSupplier<ItemExternalReference, Exception>
			itemExternalReferenceUnsafeSupplier) {

		try {
			itemExternalReference = itemExternalReferenceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ItemExternalReference itemExternalReference;

	public TemplateReference getTemplateReference() {
		return templateReference;
	}

	public void setTemplateReference(TemplateReference templateReference) {
		this.templateReference = templateReference;
	}

	public void setTemplateReference(
		UnsafeSupplier<TemplateReference, Exception>
			templateReferenceUnsafeSupplier) {

		try {
			templateReference = templateReferenceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected TemplateReference templateReference;

	@Override
	public ItemValue clone() throws CloneNotSupportedException {
		return (ItemValue)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ItemValue)) {
			return false;
		}

		ItemValue itemValue = (ItemValue)object;

		return Objects.equals(toString(), itemValue.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ItemValueSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-755935884