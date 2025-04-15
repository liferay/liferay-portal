/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0;

import com.liferay.headless.commerce.delivery.catalog.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.delivery.catalog.client.serdes.v1_0.WishListSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class WishList implements Cloneable, Serializable {

	public static WishList toDTO(String json) {
		return WishListSerDes.toDTO(json);
	}

	public Boolean getDefaultWishList() {
		return defaultWishList;
	}

	public void setDefaultWishList(Boolean defaultWishList) {
		this.defaultWishList = defaultWishList;
	}

	public void setDefaultWishList(
		UnsafeSupplier<Boolean, Exception> defaultWishListUnsafeSupplier) {

		try {
			defaultWishList = defaultWishListUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean defaultWishList;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String name;

	public WishListItem[] getWishListItems() {
		return wishListItems;
	}

	public void setWishListItems(WishListItem[] wishListItems) {
		this.wishListItems = wishListItems;
	}

	public void setWishListItems(
		UnsafeSupplier<WishListItem[], Exception> wishListItemsUnsafeSupplier) {

		try {
			wishListItems = wishListItemsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected WishListItem[] wishListItems;

	@Override
	public WishList clone() throws CloneNotSupportedException {
		return (WishList)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WishList)) {
			return false;
		}

		WishList wishList = (WishList)object;

		return Objects.equals(toString(), wishList.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return WishListSerDes.toJSON(this);
	}

}