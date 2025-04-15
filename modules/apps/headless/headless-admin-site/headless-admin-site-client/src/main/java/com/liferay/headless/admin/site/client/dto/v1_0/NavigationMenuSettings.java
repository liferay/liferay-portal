/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.NavigationMenuSettingsSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class NavigationMenuSettings implements Cloneable, Serializable {

	public static NavigationMenuSettings toDTO(String json) {
		return NavigationMenuSettingsSerDes.toDTO(json);
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void setTarget(
		UnsafeSupplier<String, Exception> targetUnsafeSupplier) {

		try {
			target = targetUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String target;

	public TargetType getTargetType() {
		return targetType;
	}

	public String getTargetTypeAsString() {
		if (targetType == null) {
			return null;
		}

		return targetType.toString();
	}

	public void setTargetType(TargetType targetType) {
		this.targetType = targetType;
	}

	public void setTargetType(
		UnsafeSupplier<TargetType, Exception> targetTypeUnsafeSupplier) {

		try {
			targetType = targetTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected TargetType targetType;

	@Override
	public NavigationMenuSettings clone() throws CloneNotSupportedException {
		return (NavigationMenuSettings)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof NavigationMenuSettings)) {
			return false;
		}

		NavigationMenuSettings navigationMenuSettings =
			(NavigationMenuSettings)object;

		return Objects.equals(toString(), navigationMenuSettings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return NavigationMenuSettingsSerDes.toJSON(this);
	}

	public static enum TargetType {

		SPECIFIC_FRAME("SpecificFrame"), NEW_TAB("NewTab");

		public static TargetType create(String value) {
			for (TargetType targetType : values()) {
				if (Objects.equals(targetType.getValue(), value) ||
					Objects.equals(targetType.name(), value)) {

					return targetType;
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

		private TargetType(String value) {
			_value = value;
		}

		private final String _value;

	}

}