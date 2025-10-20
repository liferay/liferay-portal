/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.PageElementDefinitionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public abstract class PageElementDefinition implements Cloneable, Serializable {

	public static PageElementDefinition toDTO(String json) {
		return PageElementDefinitionSerDes.toDTO(json);
	}

	public Type getType() {
		return type;
	}

	public String getTypeAsString() {
		if (type == null) {
			return null;
		}

		return type.toString();
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setType(UnsafeSupplier<Type, Exception> typeUnsafeSupplier) {
		try {
			type = typeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Type type;

	@Override
	public PageElementDefinition clone() throws CloneNotSupportedException {
		return (PageElementDefinition)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PageElementDefinition)) {
			return false;
		}

		PageElementDefinition pageElementDefinition =
			(PageElementDefinition)object;

		return Objects.equals(toString(), pageElementDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PageElementDefinitionSerDes.toJSON(this);
	}

	public static enum Type {

		COLLECTION("Collection"), COLLECTION_ITEM("CollectionItem"),
		CONTAINER("Container"), DROP_ZONE("DropZone"), FORM("Form"),
		FORM_STEP("FormStep"), FORM_STEP_CONTAINER("FormStepContainer"),
		FRAGMENT("Fragment"), FRAGMENT_COMPOSITION("FragmentComposition"),
		FRAGMENT_DROP_ZONE("FragmentDropZone"), GRID("Grid"), MODULE("Module"),
		WIDGET("Widget");

		public static Type create(String value) {
			for (Type type : values()) {
				if (Objects.equals(type.getValue(), value) ||
					Objects.equals(type.name(), value)) {

					return type;
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

		private Type(String value) {
			_value = value;
		}

		private final String _value;

	}

}