/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.CollectionReferenceSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public abstract class CollectionReference implements Cloneable, Serializable {

	public static CollectionReference toDTO(String json) {
		return CollectionReferenceSerDes.toDTO(json);
	}

	public CollectionType getCollectionType() {
		return collectionType;
	}

	public String getCollectionTypeAsString() {
		if (collectionType == null) {
			return null;
		}

		return collectionType.toString();
	}

	public void setCollectionType(CollectionType collectionType) {
		this.collectionType = collectionType;
	}

	public void setCollectionType(
		UnsafeSupplier<CollectionType, Exception>
			collectionTypeUnsafeSupplier) {

		try {
			collectionType = collectionTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected CollectionType collectionType;

	@Override
	public CollectionReference clone() throws CloneNotSupportedException {
		return (CollectionReference)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CollectionReference)) {
			return false;
		}

		CollectionReference collectionReference = (CollectionReference)object;

		return Objects.equals(toString(), collectionReference.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return CollectionReferenceSerDes.toJSON(this);
	}

	public static enum CollectionType {

		COLLECTION("Collection"), COLLECTION_PROVIDER("CollectionProvider");

		public static CollectionType create(String value) {
			for (CollectionType collectionType : values()) {
				if (Objects.equals(collectionType.getValue(), value) ||
					Objects.equals(collectionType.name(), value)) {

					return collectionType;
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

		private CollectionType(String value) {
			_value = value;
		}

		private final String _value;

	}

}