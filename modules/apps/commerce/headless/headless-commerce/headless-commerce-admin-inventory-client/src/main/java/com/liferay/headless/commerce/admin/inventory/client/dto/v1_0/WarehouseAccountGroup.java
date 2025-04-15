/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.client.dto.v1_0;

import com.liferay.headless.commerce.admin.inventory.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.inventory.client.serdes.v1_0.WarehouseAccountGroupSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public class WarehouseAccountGroup implements Cloneable, Serializable {

	public static WarehouseAccountGroup toDTO(String json) {
		return WarehouseAccountGroupSerDes.toDTO(json);
	}

	public AccountGroup getAccountGroup() {
		return accountGroup;
	}

	public void setAccountGroup(AccountGroup accountGroup) {
		this.accountGroup = accountGroup;
	}

	public void setAccountGroup(
		UnsafeSupplier<AccountGroup, Exception> accountGroupUnsafeSupplier) {

		try {
			accountGroup = accountGroupUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected AccountGroup accountGroup;

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

	public Long getWarehouseAccountGroupId() {
		return warehouseAccountGroupId;
	}

	public void setWarehouseAccountGroupId(Long warehouseAccountGroupId) {
		this.warehouseAccountGroupId = warehouseAccountGroupId;
	}

	public void setWarehouseAccountGroupId(
		UnsafeSupplier<Long, Exception> warehouseAccountGroupIdUnsafeSupplier) {

		try {
			warehouseAccountGroupId =
				warehouseAccountGroupIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long warehouseAccountGroupId;

	public String getWarehouseExternalReferenceCode() {
		return warehouseExternalReferenceCode;
	}

	public void setWarehouseExternalReferenceCode(
		String warehouseExternalReferenceCode) {

		this.warehouseExternalReferenceCode = warehouseExternalReferenceCode;
	}

	public void setWarehouseExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			warehouseExternalReferenceCodeUnsafeSupplier) {

		try {
			warehouseExternalReferenceCode =
				warehouseExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String warehouseExternalReferenceCode;

	public Long getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Long warehouseId) {
		this.warehouseId = warehouseId;
	}

	public void setWarehouseId(
		UnsafeSupplier<Long, Exception> warehouseIdUnsafeSupplier) {

		try {
			warehouseId = warehouseIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long warehouseId;

	@Override
	public WarehouseAccountGroup clone() throws CloneNotSupportedException {
		return (WarehouseAccountGroup)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WarehouseAccountGroup)) {
			return false;
		}

		WarehouseAccountGroup warehouseAccountGroup =
			(WarehouseAccountGroup)object;

		return Objects.equals(toString(), warehouseAccountGroup.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return WarehouseAccountGroupSerDes.toJSON(this);
	}

}