/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.FragmentLinkSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class FragmentLink implements Cloneable, Serializable {

	public static FragmentLink toDTO(String json) {
		return FragmentLinkSerDes.toDTO(json);
	}

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

}