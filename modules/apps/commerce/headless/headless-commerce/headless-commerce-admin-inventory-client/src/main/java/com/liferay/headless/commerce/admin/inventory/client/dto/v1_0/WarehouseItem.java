/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.client.dto.v1_0;

import com.liferay.headless.commerce.admin.inventory.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.inventory.client.serdes.v1_0.WarehouseItemSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Date;
import java.util.Objects;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public class WarehouseItem implements Cloneable, Serializable {

	public static WarehouseItem toDTO(String json) {
		return WarehouseItemSerDes.toDTO(json);
	}

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public void setModifiedDate(
		UnsafeSupplier<Date, Exception> modifiedDateUnsafeSupplier) {

		try {
			modifiedDate = modifiedDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date modifiedDate;

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public void setQuantity(
		UnsafeSupplier<BigDecimal, Exception> quantityUnsafeSupplier) {

		try {
			quantity = quantityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal quantity;

	public BigDecimal getReservedQuantity() {
		return reservedQuantity;
	}

	public void setReservedQuantity(BigDecimal reservedQuantity) {
		this.reservedQuantity = reservedQuantity;
	}

	public void setReservedQuantity(
		UnsafeSupplier<BigDecimal, Exception> reservedQuantityUnsafeSupplier) {

		try {
			reservedQuantity = reservedQuantityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal reservedQuantity;

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public void setSku(UnsafeSupplier<String, Exception> skuUnsafeSupplier) {
		try {
			sku = skuUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String sku;

	public String getUnitOfMeasureKey() {
		return unitOfMeasureKey;
	}

	public void setUnitOfMeasureKey(String unitOfMeasureKey) {
		this.unitOfMeasureKey = unitOfMeasureKey;
	}

	public void setUnitOfMeasureKey(
		UnsafeSupplier<String, Exception> unitOfMeasureKeyUnsafeSupplier) {

		try {
			unitOfMeasureKey = unitOfMeasureKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String unitOfMeasureKey;

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
	public WarehouseItem clone() throws CloneNotSupportedException {
		return (WarehouseItem)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WarehouseItem)) {
			return false;
		}

		WarehouseItem warehouseItem = (WarehouseItem)object;

		return Objects.equals(toString(), warehouseItem.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return WarehouseItemSerDes.toJSON(this);
	}

}