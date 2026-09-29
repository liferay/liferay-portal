/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.ContextualMenuNavigationMenuValueSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class ContextualMenuNavigationMenuValue
	extends NavigationMenuValue implements Cloneable, Serializable {

	public static ContextualMenuNavigationMenuValue toDTO(String json) {
		return ContextualMenuNavigationMenuValueSerDes.toDTO(json);
	}

	public ContextualMenuType getContextualMenuType() {
		return contextualMenuType;
	}

	public String getContextualMenuTypeAsString() {
		if (contextualMenuType == null) {
			return null;
		}

		return contextualMenuType.toString();
	}

	public void setContextualMenuType(ContextualMenuType contextualMenuType) {
		this.contextualMenuType = contextualMenuType;
	}

	public void setContextualMenuType(
		UnsafeSupplier<ContextualMenuType, Exception>
			contextualMenuTypeUnsafeSupplier) {

		try {
			contextualMenuType = contextualMenuTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ContextualMenuType contextualMenuType;

	@Override
	public ContextualMenuNavigationMenuValue clone()
		throws CloneNotSupportedException {

		return (ContextualMenuNavigationMenuValue)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ContextualMenuNavigationMenuValue)) {
			return false;
		}

		ContextualMenuNavigationMenuValue contextualMenuNavigationMenuValue =
			(ContextualMenuNavigationMenuValue)object;

		return Objects.equals(
			toString(), contextualMenuNavigationMenuValue.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ContextualMenuNavigationMenuValueSerDes.toJSON(this);
	}

	public static enum ContextualMenuType {

		CHILDREN("Children"), PARENT_AND_ITS_SIBLINGS("ParentAndItsSiblings"),
		SELF_AND_SIBLINGS("SelfAndSiblings");

		public static ContextualMenuType create(String value) {
			for (ContextualMenuType contextualMenuType : values()) {
				if (Objects.equals(contextualMenuType.getValue(), value) ||
					Objects.equals(contextualMenuType.name(), value)) {

					return contextualMenuType;
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

		private ContextualMenuType(String value) {
			_value = value;
		}

		private final String _value;

	}

}
// LIFERAY-REST-BUILDER-HASH:872602608