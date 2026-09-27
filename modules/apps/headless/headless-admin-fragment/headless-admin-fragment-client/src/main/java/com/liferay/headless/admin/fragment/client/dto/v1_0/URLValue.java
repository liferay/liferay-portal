/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.URLValueSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public abstract class URLValue implements Cloneable, Serializable {

	public static URLValue toDTO(String json) {
		return URLValueSerDes.toDTO(json);
	}

	public UrlType getUrlType() {
		return urlType;
	}

	public String getUrlTypeAsString() {
		if (urlType == null) {
			return null;
		}

		return urlType.toString();
	}

	public void setUrlType(UrlType urlType) {
		this.urlType = urlType;
	}

	public void setUrlType(
		UnsafeSupplier<UrlType, Exception> urlTypeUnsafeSupplier) {

		try {
			urlType = urlTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected UrlType urlType;

	@Override
	public URLValue clone() throws CloneNotSupportedException {
		return (URLValue)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof URLValue)) {
			return false;
		}

		URLValue urlValue = (URLValue)object;

		return Objects.equals(toString(), urlValue.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return URLValueSerDes.toJSON(this);
	}

	public static enum UrlType {

		HREF("Href"), SITE_PAGE("SitePage");

		public static UrlType create(String value) {
			for (UrlType urlType : values()) {
				if (Objects.equals(urlType.getValue(), value) ||
					Objects.equals(urlType.name(), value)) {

					return urlType;
				}
			}

			return null;
		}

		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private UrlType(String value) {
			_value = value;
		}

		private final String _value;

	}

}
// LIFERAY-REST-BUILDER-HASH:289535479