/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.content.client.dto.v1_0;

import com.liferay.headless.admin.content.client.function.UnsafeSupplier;
import com.liferay.headless.admin.content.client.serdes.v1_0.PageRuleActionSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class PageRuleAction implements Cloneable, Serializable {

	public static PageRuleAction toDTO(String json) {
		return PageRuleActionSerDes.toDTO(json);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<String, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String id;

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public void setItemId(
		UnsafeSupplier<String, Exception> itemIdUnsafeSupplier) {

		try {
			itemId = itemIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String itemId;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setType(UnsafeSupplier<String, Exception> typeUnsafeSupplier) {
		try {
			type = typeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String type;

	@Override
	public PageRuleAction clone() throws CloneNotSupportedException {
		return (PageRuleAction)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PageRuleAction)) {
			return false;
		}

		PageRuleAction pageRuleAction = (PageRuleAction)object;

		return Objects.equals(toString(), pageRuleAction.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PageRuleActionSerDes.toJSON(this);
	}

}