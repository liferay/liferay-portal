/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.rest.client.dto.v1_0;

import com.liferay.seo.studio.rest.client.function.UnsafeSupplier;
import com.liferay.seo.studio.rest.client.serdes.v1_0.AutofixSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Brooke Dalton
 * @generated
 */
@Generated("")
public class Autofix implements Cloneable, Serializable {

	public static Autofix toDTO(String json) {
		return AutofixSerDes.toDTO(json);
	}

	public String getInsightType() {
		return insightType;
	}

	public void setInsightType(String insightType) {
		this.insightType = insightType;
	}

	public void setInsightType(
		UnsafeSupplier<String, Exception> insightTypeUnsafeSupplier) {

		try {
			insightType = insightTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String insightType;

	public String getPageURL() {
		return pageURL;
	}

	public void setPageURL(String pageURL) {
		this.pageURL = pageURL;
	}

	public void setPageURL(
		UnsafeSupplier<String, Exception> pageURLUnsafeSupplier) {

		try {
			pageURL = pageURLUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String pageURL;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setValue(
		UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

		try {
			value = valueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String value;

	@Override
	public Autofix clone() throws CloneNotSupportedException {
		return (Autofix)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Autofix)) {
			return false;
		}

		Autofix autofix = (Autofix)object;

		return Objects.equals(toString(), autofix.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return AutofixSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:561046989