/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.FragmentLinkInlineValueSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class FragmentLinkInlineValue
	extends FragmentLinkValue implements Cloneable, Serializable {

	public static FragmentLinkInlineValue toDTO(String json) {
		return FragmentLinkInlineValueSerDes.toDTO(json);
	}

	public Map<String, String> getValue_i18n() {
		return value_i18n;
	}

	public void setValue_i18n(Map<String, String> value_i18n) {
		this.value_i18n = value_i18n;
	}

	public void setValue_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			value_i18nUnsafeSupplier) {

		try {
			value_i18n = value_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> value_i18n;

	@Override
	public FragmentLinkInlineValue clone() throws CloneNotSupportedException {
		return (FragmentLinkInlineValue)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FragmentLinkInlineValue)) {
			return false;
		}

		FragmentLinkInlineValue fragmentLinkInlineValue =
			(FragmentLinkInlineValue)object;

		return Objects.equals(toString(), fragmentLinkInlineValue.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FragmentLinkInlineValueSerDes.toJSON(this);
	}

}