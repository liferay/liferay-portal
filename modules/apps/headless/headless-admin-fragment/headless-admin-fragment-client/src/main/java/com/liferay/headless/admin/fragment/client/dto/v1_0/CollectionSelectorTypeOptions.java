/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.CollectionSelectorTypeOptionsSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class CollectionSelectorTypeOptions implements Cloneable, Serializable {

	public static CollectionSelectorTypeOptions toDTO(String json) {
		return CollectionSelectorTypeOptionsSerDes.toDTO(json);
	}

	public Map<String, Dependency> getDependency() {
		return dependency;
	}

	public void setDependency(Map<String, Dependency> dependency) {
		this.dependency = dependency;
	}

	public void setDependency(
		UnsafeSupplier<Map<String, Dependency>, Exception>
			dependencyUnsafeSupplier) {

		try {
			dependency = dependencyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Dependency> dependency;

	public String getItemSubtype() {
		return itemSubtype;
	}

	public void setItemSubtype(String itemSubtype) {
		this.itemSubtype = itemSubtype;
	}

	public void setItemSubtype(
		UnsafeSupplier<String, Exception> itemSubtypeUnsafeSupplier) {

		try {
			itemSubtype = itemSubtypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String itemSubtype;

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public void setItemType(
		UnsafeSupplier<String, Exception> itemTypeUnsafeSupplier) {

		try {
			itemType = itemTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String itemType;

	public Integer getNumberOfItems() {
		return numberOfItems;
	}

	public void setNumberOfItems(Integer numberOfItems) {
		this.numberOfItems = numberOfItems;
	}

	public void setNumberOfItems(
		UnsafeSupplier<Integer, Exception> numberOfItemsUnsafeSupplier) {

		try {
			numberOfItems = numberOfItemsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer numberOfItems;

	@Override
	public CollectionSelectorTypeOptions clone()
		throws CloneNotSupportedException {

		return (CollectionSelectorTypeOptions)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CollectionSelectorTypeOptions)) {
			return false;
		}

		CollectionSelectorTypeOptions collectionSelectorTypeOptions =
			(CollectionSelectorTypeOptions)object;

		return Objects.equals(
			toString(), collectionSelectorTypeOptions.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return CollectionSelectorTypeOptionsSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:115211309