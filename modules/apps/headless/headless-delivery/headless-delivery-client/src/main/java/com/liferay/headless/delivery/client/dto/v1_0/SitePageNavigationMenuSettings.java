/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.SitePageNavigationMenuSettingsSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class SitePageNavigationMenuSettings implements Cloneable, Serializable {

	public static SitePageNavigationMenuSettings toDTO(String json) {
		return SitePageNavigationMenuSettingsSerDes.toDTO(json);
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public void setQueryString(
		UnsafeSupplier<String, Exception> queryStringUnsafeSupplier) {

		try {
			queryString = queryStringUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String queryString;

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
	public SitePageNavigationMenuSettings clone()
		throws CloneNotSupportedException {

		return (SitePageNavigationMenuSettings)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SitePageNavigationMenuSettings)) {
			return false;
		}

		SitePageNavigationMenuSettings sitePageNavigationMenuSettings =
			(SitePageNavigationMenuSettings)object;

		return Objects.equals(
			toString(), sitePageNavigationMenuSettings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SitePageNavigationMenuSettingsSerDes.toJSON(this);
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