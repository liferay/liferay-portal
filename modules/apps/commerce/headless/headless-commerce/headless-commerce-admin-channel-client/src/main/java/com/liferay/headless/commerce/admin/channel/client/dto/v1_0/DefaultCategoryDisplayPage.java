/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.client.dto.v1_0;

import com.liferay.headless.commerce.admin.channel.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.channel.client.serdes.v1_0.DefaultCategoryDisplayPageSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class DefaultCategoryDisplayPage implements Cloneable, Serializable {

	public static DefaultCategoryDisplayPage toDTO(String json) {
		return DefaultCategoryDisplayPageSerDes.toDTO(json);
	}

	public Map<String, Map<String, String>> getActions() {
		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;
	}

	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		try {
			actions = actionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Map<String, String>> actions;

	public String getPageUuid() {
		return pageUuid;
	}

	public void setPageUuid(String pageUuid) {
		this.pageUuid = pageUuid;
	}

	public void setPageUuid(
		UnsafeSupplier<String, Exception> pageUuidUnsafeSupplier) {

		try {
			pageUuid = pageUuidUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String pageUuid;

	@Override
	public DefaultCategoryDisplayPage clone()
		throws CloneNotSupportedException {

		return (DefaultCategoryDisplayPage)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DefaultCategoryDisplayPage)) {
			return false;
		}

		DefaultCategoryDisplayPage defaultCategoryDisplayPage =
			(DefaultCategoryDisplayPage)object;

		return Objects.equals(
			toString(), defaultCategoryDisplayPage.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return DefaultCategoryDisplayPageSerDes.toJSON(this);
	}

}