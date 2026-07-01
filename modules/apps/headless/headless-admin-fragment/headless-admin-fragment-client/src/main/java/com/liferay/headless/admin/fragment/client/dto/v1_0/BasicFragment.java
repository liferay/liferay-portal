/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.serdes.v1_0.BasicFragmentSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class BasicFragment extends Fragment implements Cloneable, Serializable {

	public static BasicFragment toDTO(String json) {
		return BasicFragmentSerDes.toDTO(json);
	}

	@Override
	public BasicFragment clone() throws CloneNotSupportedException {
		return (BasicFragment)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof BasicFragment)) {
			return false;
		}

		BasicFragment basicFragment = (BasicFragment)object;

		return Objects.equals(toString(), basicFragment.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return BasicFragmentSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:1230403479