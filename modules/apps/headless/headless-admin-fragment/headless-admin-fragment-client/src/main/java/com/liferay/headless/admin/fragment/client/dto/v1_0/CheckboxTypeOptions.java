/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.CheckboxTypeOptionsSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class CheckboxTypeOptions implements Cloneable, Serializable {

	public static CheckboxTypeOptions toDTO(String json) {
		return CheckboxTypeOptionsSerDes.toDTO(json);
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

	public DisplayType getDisplayType() {
		return displayType;
	}

	public String getDisplayTypeAsString() {
		if (displayType == null) {
			return null;
		}

		return displayType.toString();
	}

	public void setDisplayType(DisplayType displayType) {
		this.displayType = displayType;
	}

	public void setDisplayType(
		UnsafeSupplier<DisplayType, Exception> displayTypeUnsafeSupplier) {

		try {
			displayType = displayTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected DisplayType displayType;

	@Override
	public CheckboxTypeOptions clone() throws CloneNotSupportedException {
		return (CheckboxTypeOptions)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CheckboxTypeOptions)) {
			return false;
		}

		CheckboxTypeOptions checkboxTypeOptions = (CheckboxTypeOptions)object;

		return Objects.equals(toString(), checkboxTypeOptions.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return CheckboxTypeOptionsSerDes.toJSON(this);
	}

	public static enum DisplayType {

		CHECKBOX("checkbox"), TOGGLE("toggle");

		public static DisplayType create(String value) {
			for (DisplayType displayType : values()) {
				if (Objects.equals(displayType.getValue(), value) ||
					Objects.equals(displayType.name(), value)) {

					return displayType;
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

		private DisplayType(String value) {
			_value = value;
		}

		private final String _value;

	}

}
// LIFERAY-REST-BUILDER-HASH:-150566036