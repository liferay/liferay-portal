/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.serdes.v1_0.ContentPageTemplateSettingsSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class ContentPageTemplateSettings
	extends PageTemplateSettings implements Cloneable, Serializable {

	public static ContentPageTemplateSettings toDTO(String json) {
		return ContentPageTemplateSettingsSerDes.toDTO(json);
	}

	@Override
	public ContentPageTemplateSettings clone()
		throws CloneNotSupportedException {

		return (ContentPageTemplateSettings)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ContentPageTemplateSettings)) {
			return false;
		}

		ContentPageTemplateSettings contentPageTemplateSettings =
			(ContentPageTemplateSettings)object;

		return Objects.equals(
			toString(), contentPageTemplateSettings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ContentPageTemplateSettingsSerDes.toJSON(this);
	}

}