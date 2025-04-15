/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.FragmentFieldTextSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class FragmentFieldText implements Cloneable, Serializable {

	public static FragmentFieldText toDTO(String json) {
		return FragmentFieldTextSerDes.toDTO(json);
	}

	public FragmentLink getFragmentLink() {
		return fragmentLink;
	}

	public void setFragmentLink(FragmentLink fragmentLink) {
		this.fragmentLink = fragmentLink;
	}

	public void setFragmentLink(
		UnsafeSupplier<FragmentLink, Exception> fragmentLinkUnsafeSupplier) {

		try {
			fragmentLink = fragmentLinkUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FragmentLink fragmentLink;

	public Object getText() {
		return text;
	}

	public void setText(Object text) {
		this.text = text;
	}

	public void setText(UnsafeSupplier<Object, Exception> textUnsafeSupplier) {
		try {
			text = textUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object text;

	@Override
	public FragmentFieldText clone() throws CloneNotSupportedException {
		return (FragmentFieldText)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FragmentFieldText)) {
			return false;
		}

		FragmentFieldText fragmentFieldText = (FragmentFieldText)object;

		return Objects.equals(toString(), fragmentFieldText.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FragmentFieldTextSerDes.toJSON(this);
	}

}