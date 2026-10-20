/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.dto.v1_0;

import com.liferay.exportimport.rest.client.serdes.v1_0.FromLastPublishDateFilterSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class FromLastPublishDateFilter
	extends DateFilter implements Cloneable, Serializable {

	public static FromLastPublishDateFilter toDTO(String json) {
		return FromLastPublishDateFilterSerDes.toDTO(json);
	}

	@Override
	public FromLastPublishDateFilter clone() throws CloneNotSupportedException {
		return (FromLastPublishDateFilter)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FromLastPublishDateFilter)) {
			return false;
		}

		FromLastPublishDateFilter fromLastPublishDateFilter =
			(FromLastPublishDateFilter)object;

		return Objects.equals(toString(), fromLastPublishDateFilter.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FromLastPublishDateFilterSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:184592653