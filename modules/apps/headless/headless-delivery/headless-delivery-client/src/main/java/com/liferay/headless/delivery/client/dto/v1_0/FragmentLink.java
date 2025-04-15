/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.FragmentLinkSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class FragmentLink implements Cloneable, Serializable {

	public static FragmentLink toDTO(String json) {
		return FragmentLinkSerDes.toDTO(json);
	}

	public Object getHref() {
		return href;
	}

	public void setHref(Object href) {
		this.href = href;
	}

	public void setHref(UnsafeSupplier<Object, Exception> hrefUnsafeSupplier) {
		try {
			href = hrefUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object href;

	public Target getTarget() {
		return target;
	}

	public String getTargetAsString() {
		if (target == null) {
			return null;
		}

		return target.toString();
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	public void setTarget(
		UnsafeSupplier<Target, Exception> targetUnsafeSupplier) {

		try {
			target = targetUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Target target;

	public FragmentLinkValue getValue() {
		return value;
	}

	public void setValue(FragmentLinkValue value) {
		this.value = value;
	}

	public void setValue(
		UnsafeSupplier<FragmentLinkValue, Exception> valueUnsafeSupplier) {

		try {
			value = valueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FragmentLinkValue value;

	public Map<String, FragmentLinkValue> getValue_i18n() {
		return value_i18n;
	}

	public void setValue_i18n(Map<String, FragmentLinkValue> value_i18n) {
		this.value_i18n = value_i18n;
	}

	public void setValue_i18n(
		UnsafeSupplier<Map<String, FragmentLinkValue>, Exception>
			value_i18nUnsafeSupplier) {

		try {
			value_i18n = value_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, FragmentLinkValue> value_i18n;

	@Override
	public FragmentLink clone() throws CloneNotSupportedException {
		return (FragmentLink)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FragmentLink)) {
			return false;
		}

		FragmentLink fragmentLink = (FragmentLink)object;

		return Objects.equals(toString(), fragmentLink.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FragmentLinkSerDes.toJSON(this);
	}

	public static enum Target {

		BLANK("Blank"), PARENT("Parent"), SELF("Self"), TOP("Top");

		public static Target create(String value) {
			for (Target target : values()) {
				if (Objects.equals(target.getValue(), value) ||
					Objects.equals(target.name(), value)) {

					return target;
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

		private Target(String value) {
			_value = value;
		}

		private final String _value;

	}

}