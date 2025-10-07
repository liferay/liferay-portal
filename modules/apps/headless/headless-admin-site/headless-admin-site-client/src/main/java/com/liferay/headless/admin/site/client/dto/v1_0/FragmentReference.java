/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.FragmentReferenceSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public abstract class FragmentReference implements Cloneable, Serializable {

	public static FragmentReference toDTO(String json) {
		return FragmentReferenceSerDes.toDTO(json);
	}

	public FragmentReferenceType getFragmentReferenceType() {
		return fragmentReferenceType;
	}

	public String getFragmentReferenceTypeAsString() {
		if (fragmentReferenceType == null) {
			return null;
		}

		return fragmentReferenceType.toString();
	}

	public void setFragmentReferenceType(
		FragmentReferenceType fragmentReferenceType) {

		this.fragmentReferenceType = fragmentReferenceType;
	}

	public void setFragmentReferenceType(
		UnsafeSupplier<FragmentReferenceType, Exception>
			fragmentReferenceTypeUnsafeSupplier) {

		try {
			fragmentReferenceType = fragmentReferenceTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FragmentReferenceType fragmentReferenceType;

	@Override
	public FragmentReference clone() throws CloneNotSupportedException {
		return (FragmentReference)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FragmentReference)) {
			return false;
		}

		FragmentReference fragmentReference = (FragmentReference)object;

		return Objects.equals(toString(), fragmentReference.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FragmentReferenceSerDes.toJSON(this);
	}

	public static enum FragmentReferenceType {

		DEFAULT_FRAGMENT_REFERENCE("DefaultFragmentReference"),
		FRAGMENT_ITEM_EXTERNAL_REFERENCE("FragmentItemExternalReference");

		public static FragmentReferenceType create(String value) {
			for (FragmentReferenceType fragmentReferenceType : values()) {
				if (Objects.equals(fragmentReferenceType.getValue(), value) ||
					Objects.equals(fragmentReferenceType.name(), value)) {

					return fragmentReferenceType;
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

		private FragmentReferenceType(String value) {
			_value = value;
		}

		private final String _value;

	}

}