/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.rest.client.dto.v1_0;

import com.liferay.seo.studio.rest.client.function.UnsafeSupplier;
import com.liferay.seo.studio.rest.client.serdes.v1_0.PageContentSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Brooke Dalton
 * @generated
 */
@Generated("")
public class PageContent implements Cloneable, Serializable {

	public static PageContent toDTO(String json) {
		return PageContentSerDes.toDTO(json);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setContent(
		UnsafeSupplier<String, Exception> contentUnsafeSupplier) {

		try {
			content = contentUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String content;

	@Override
	public PageContent clone() throws CloneNotSupportedException {
		return (PageContent)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PageContent)) {
			return false;
		}

		PageContent pageContent = (PageContent)object;

		return Objects.equals(toString(), pageContent.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PageContentSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:1706081423