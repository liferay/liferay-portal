/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.serdes.v1_0.ContentPageSettingsSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class ContentPageSettings
	extends PageSettings implements Cloneable, Serializable {

	public static ContentPageSettings toDTO(String json) {
		return ContentPageSettingsSerDes.toDTO(json);
	}

	@Override
	public ContentPageSettings clone() throws CloneNotSupportedException {
		return (ContentPageSettings)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ContentPageSettings)) {
			return false;
		}

		ContentPageSettings contentPageSettings = (ContentPageSettings)object;

		return Objects.equals(toString(), contentPageSettings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ContentPageSettingsSerDes.toJSON(this);
	}

}