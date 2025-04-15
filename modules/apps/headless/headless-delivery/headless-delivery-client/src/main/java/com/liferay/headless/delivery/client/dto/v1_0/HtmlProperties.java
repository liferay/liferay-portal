/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.HtmlPropertiesSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class HtmlProperties implements Cloneable, Serializable {

	public static HtmlProperties toDTO(String json) {
		return HtmlPropertiesSerDes.toDTO(json);
	}

	public HtmlTag getHtmlTag() {
		return htmlTag;
	}

	public String getHtmlTagAsString() {
		if (htmlTag == null) {
			return null;
		}

		return htmlTag.toString();
	}

	public void setHtmlTag(HtmlTag htmlTag) {
		this.htmlTag = htmlTag;
	}

	public void setHtmlTag(
		UnsafeSupplier<HtmlTag, Exception> htmlTagUnsafeSupplier) {

		try {
			htmlTag = htmlTagUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected HtmlTag htmlTag;

	@Override
	public HtmlProperties clone() throws CloneNotSupportedException {
		return (HtmlProperties)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof HtmlProperties)) {
			return false;
		}

		HtmlProperties htmlProperties = (HtmlProperties)object;

		return Objects.equals(toString(), htmlProperties.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return HtmlPropertiesSerDes.toJSON(this);
	}

	public static enum HtmlTag {

		ARTICLE("Article"), ASIDE("Aside"), DIV("Div"), FOOTER("Footer"),
		HEADER("Header"), MAIN("Main"), NAV("Nav"), SECTION("Section");

		public static HtmlTag create(String value) {
			for (HtmlTag htmlTag : values()) {
				if (Objects.equals(htmlTag.getValue(), value) ||
					Objects.equals(htmlTag.name(), value)) {

					return htmlTag;
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

		private HtmlTag(String value) {
			_value = value;
		}

		private final String _value;

	}

}