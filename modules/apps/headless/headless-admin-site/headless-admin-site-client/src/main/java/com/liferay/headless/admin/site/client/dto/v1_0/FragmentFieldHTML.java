/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.FragmentFieldHTMLSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class FragmentFieldHTML implements Cloneable, Serializable {

	public static FragmentFieldHTML toDTO(String json) {
		return FragmentFieldHTMLSerDes.toDTO(json);
	}

	public Object getHtml() {
		return html;
	}

	public void setHtml(Object html) {
		this.html = html;
	}

	public void setHtml(UnsafeSupplier<Object, Exception> htmlUnsafeSupplier) {
		try {
			html = htmlUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object html;

	@Override
	public FragmentFieldHTML clone() throws CloneNotSupportedException {
		return (FragmentFieldHTML)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FragmentFieldHTML)) {
			return false;
		}

		FragmentFieldHTML fragmentFieldHTML = (FragmentFieldHTML)object;

		return Objects.equals(toString(), fragmentFieldHTML.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FragmentFieldHTMLSerDes.toJSON(this);
	}

}