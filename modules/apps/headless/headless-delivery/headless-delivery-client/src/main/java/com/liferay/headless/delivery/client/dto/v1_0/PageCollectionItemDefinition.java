/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.PageCollectionItemDefinitionSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class PageCollectionItemDefinition implements Cloneable, Serializable {

	public static PageCollectionItemDefinition toDTO(String json) {
		return PageCollectionItemDefinitionSerDes.toDTO(json);
	}

	public Object getCollectionItemConfig() {
		return collectionItemConfig;
	}

	public void setCollectionItemConfig(Object collectionItemConfig) {
		this.collectionItemConfig = collectionItemConfig;
	}

	public void setCollectionItemConfig(
		UnsafeSupplier<Object, Exception> collectionItemConfigUnsafeSupplier) {

		try {
			collectionItemConfig = collectionItemConfigUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object collectionItemConfig;

	@Override
	public PageCollectionItemDefinition clone()
		throws CloneNotSupportedException {

		return (PageCollectionItemDefinition)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PageCollectionItemDefinition)) {
			return false;
		}

		PageCollectionItemDefinition pageCollectionItemDefinition =
			(PageCollectionItemDefinition)object;

		return Objects.equals(
			toString(), pageCollectionItemDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PageCollectionItemDefinitionSerDes.toJSON(this);
	}

}