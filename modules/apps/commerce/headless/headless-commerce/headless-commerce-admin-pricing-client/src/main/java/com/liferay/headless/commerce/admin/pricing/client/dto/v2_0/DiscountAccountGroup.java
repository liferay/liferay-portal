/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.client.dto.v2_0;

import com.liferay.headless.commerce.admin.pricing.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0.DiscountAccountGroupSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class DiscountAccountGroup implements Cloneable, Serializable {

	public static DiscountAccountGroup toDTO(String json) {
		return DiscountAccountGroupSerDes.toDTO(json);
	}

	public PricingAccountGroup getAccountGroup() {
		return accountGroup;
	}

	public void setAccountGroup(PricingAccountGroup accountGroup) {
		this.accountGroup = accountGroup;
	}

	public void setAccountGroup(
		UnsafeSupplier<PricingAccountGroup, Exception>
			accountGroupUnsafeSupplier) {

		try {
			accountGroup = accountGroupUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PricingAccountGroup accountGroup;

	public String getAccountGroupExternalReferenceCode() {
		return accountGroupExternalReferenceCode;
	}

	public void setAccountGroupExternalReferenceCode(
		String accountGroupExternalReferenceCode) {

		this.accountGroupExternalReferenceCode =
			accountGroupExternalReferenceCode;
	}

	public void setAccountGroupExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			accountGroupExternalReferenceCodeUnsafeSupplier) {

		try {
			accountGroupExternalReferenceCode =
				accountGroupExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String accountGroupExternalReferenceCode;

	public Long getAccountGroupId() {
		return accountGroupId;
	}

	public void setAccountGroupId(Long accountGroupId) {
		this.accountGroupId = accountGroupId;
	}

	public void setAccountGroupId(
		UnsafeSupplier<Long, Exception> accountGroupIdUnsafeSupplier) {

		try {
			accountGroupId = accountGroupIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long accountGroupId;

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

	public Long getDiscountAccountGroupId() {
		return discountAccountGroupId;
	}

	public void setDiscountAccountGroupId(Long discountAccountGroupId) {
		this.discountAccountGroupId = discountAccountGroupId;
	}

	public void setDiscountAccountGroupId(
		UnsafeSupplier<Long, Exception> discountAccountGroupIdUnsafeSupplier) {

		try {
			discountAccountGroupId = discountAccountGroupIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long discountAccountGroupId;

	public String getDiscountExternalReferenceCode() {
		return discountExternalReferenceCode;
	}

	public void setDiscountExternalReferenceCode(
		String discountExternalReferenceCode) {

		this.discountExternalReferenceCode = discountExternalReferenceCode;
	}

	public void setDiscountExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			discountExternalReferenceCodeUnsafeSupplier) {

		try {
			discountExternalReferenceCode =
				discountExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String discountExternalReferenceCode;

	public Long getDiscountId() {
		return discountId;
	}

	public void setDiscountId(Long discountId) {
		this.discountId = discountId;
	}

	public void setDiscountId(
		UnsafeSupplier<Long, Exception> discountIdUnsafeSupplier) {

		try {
			discountId = discountIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long discountId;

	@Override
	public DiscountAccountGroup clone() throws CloneNotSupportedException {
		return (DiscountAccountGroup)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DiscountAccountGroup)) {
			return false;
		}

		DiscountAccountGroup discountAccountGroup =
			(DiscountAccountGroup)object;

		return Objects.equals(toString(), discountAccountGroup.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return DiscountAccountGroupSerDes.toJSON(this);
	}

}