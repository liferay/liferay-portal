/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.client.dto.v1_0;

import com.liferay.analytics.cms.rest.client.function.UnsafeSupplier;
import com.liferay.analytics.cms.rest.client.serdes.v1_0.ExpiredAssetSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rachael Koestartyo
 * @generated
 */
@Generated("")
public class ExpiredAsset implements Cloneable, Serializable {

	public static ExpiredAsset toDTO(String json) {
		return ExpiredAssetSerDes.toDTO(json);
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public void setHref(UnsafeSupplier<String, Exception> hrefUnsafeSupplier) {
		try {
			href = hrefUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String href;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTitle(
		UnsafeSupplier<String, Exception> titleUnsafeSupplier) {

		try {
			title = titleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String title;

	public Number getUsages() {
		return usages;
	}

	public void setUsages(Number usages) {
		this.usages = usages;
	}

	public void setUsages(
		UnsafeSupplier<Number, Exception> usagesUnsafeSupplier) {

		try {
			usages = usagesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Number usages;

	@Override
	public ExpiredAsset clone() throws CloneNotSupportedException {
		return (ExpiredAsset)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ExpiredAsset)) {
			return false;
		}

		ExpiredAsset expiredAsset = (ExpiredAsset)object;

		return Objects.equals(toString(), expiredAsset.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ExpiredAssetSerDes.toJSON(this);
	}

}