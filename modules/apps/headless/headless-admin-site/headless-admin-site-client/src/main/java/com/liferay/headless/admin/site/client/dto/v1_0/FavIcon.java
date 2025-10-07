/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.FavIconSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public abstract class FavIcon implements Cloneable, Serializable {

	public static FavIcon toDTO(String json) {
		return FavIconSerDes.toDTO(json);
	}

	public FavIconType getFavIconType() {
		return favIconType;
	}

	public String getFavIconTypeAsString() {
		if (favIconType == null) {
			return null;
		}

		return favIconType.toString();
	}

	public void setFavIconType(FavIconType favIconType) {
		this.favIconType = favIconType;
	}

	public void setFavIconType(
		UnsafeSupplier<FavIconType, Exception> favIconTypeUnsafeSupplier) {

		try {
			favIconType = favIconTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FavIconType favIconType;

	@Override
	public FavIcon clone() throws CloneNotSupportedException {
		return (FavIcon)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FavIcon)) {
			return false;
		}

		FavIcon favIcon = (FavIcon)object;

		return Objects.equals(toString(), favIcon.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FavIconSerDes.toJSON(this);
	}

	public static enum FavIconType {

		CLIENT_EXTENSION("ClientExtension"),
		ITEM_EXTERNAL_REFERENCE("ItemExternalReference");

		public static FavIconType create(String value) {
			for (FavIconType favIconType : values()) {
				if (Objects.equals(favIconType.getValue(), value) ||
					Objects.equals(favIconType.name(), value)) {

					return favIconType;
				}
			}

			return null;
		}

		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private FavIconType(String value) {
			_value = value;
		}

		private final String _value;

	}

}