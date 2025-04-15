/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.serdes.v1_0.ContentPageTemplateSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class ContentPageTemplate
	extends PageTemplate implements Cloneable, Serializable {

	public static ContentPageTemplate toDTO(String json) {
		return ContentPageTemplateSerDes.toDTO(json);
	}

	@Override
	public ContentPageTemplate clone() throws CloneNotSupportedException {
		return (ContentPageTemplate)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ContentPageTemplate)) {
			return false;
		}

		ContentPageTemplate contentPageTemplate = (ContentPageTemplate)object;

		return Objects.equals(toString(), contentPageTemplate.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ContentPageTemplateSerDes.toJSON(this);
	}

}