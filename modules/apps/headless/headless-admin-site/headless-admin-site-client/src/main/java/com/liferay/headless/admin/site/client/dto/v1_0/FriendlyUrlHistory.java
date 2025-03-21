/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.FriendlyUrlHistorySerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class FriendlyUrlHistory implements Cloneable, Serializable {

	public static FriendlyUrlHistory toDTO(String json) {
		return FriendlyUrlHistorySerDes.toDTO(json);
	}

	public Object getFriendlyUrlPath_i18n() {
		return friendlyUrlPath_i18n;
	}

	public void setFriendlyUrlPath_i18n(Object friendlyUrlPath_i18n) {
		this.friendlyUrlPath_i18n = friendlyUrlPath_i18n;
	}

	public void setFriendlyUrlPath_i18n(
		UnsafeSupplier<Object, Exception> friendlyUrlPath_i18nUnsafeSupplier) {

		try {
			friendlyUrlPath_i18n = friendlyUrlPath_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object friendlyUrlPath_i18n;

	@Override
	public FriendlyUrlHistory clone() throws CloneNotSupportedException {
		return (FriendlyUrlHistory)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FriendlyUrlHistory)) {
			return false;
		}

		FriendlyUrlHistory friendlyUrlHistory = (FriendlyUrlHistory)object;

		return Objects.equals(toString(), friendlyUrlHistory.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FriendlyUrlHistorySerDes.toJSON(this);
	}

}