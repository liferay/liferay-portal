/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.shipment.client.dto.v1_0;

import com.liferay.headless.commerce.admin.shipment.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.shipment.client.serdes.v1_0.ShipmentItemSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class ShipmentItem implements Cloneable, Serializable {

	public static ShipmentItem toDTO(String json) {
		return ShipmentItemSerDes.toDTO(json);
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

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public void setCreateDate(
		UnsafeSupplier<Date, Exception> createDateUnsafeSupplier) {

		try {
			createDate = createDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date createDate;

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

	public String getOrderItemExternalReferenceCode() {
		return orderItemExternalReferenceCode;
	}

	public void setOrderItemExternalReferenceCode(
		String orderItemExternalReferenceCode) {

		this.orderItemExternalReferenceCode = orderItemExternalReferenceCode;
	}

	public void setOrderItemExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			orderItemExternalReferenceCodeUnsafeSupplier) {

		try {
			orderItemExternalReferenceCode =
				orderItemExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String orderItemExternalReferenceCode;

	public Long getOrderItemId() {
		return orderItemId;
	}

	public void setOrderItemId(Long orderItemId) {
		this.orderItemId = orderItemId;
	}

	public void setOrderItemId(
		UnsafeSupplier<Long, Exception> orderItemIdUnsafeSupplier) {

		try {
			orderItemId = orderItemIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long orderItemId;

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

	public String getShipmentExternalReferenceCode() {
		return shipmentExternalReferenceCode;
	}

	public void setShipmentExternalReferenceCode(
		String shipmentExternalReferenceCode) {

		this.shipmentExternalReferenceCode = shipmentExternalReferenceCode;
	}

	public void setShipmentExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			shipmentExternalReferenceCodeUnsafeSupplier) {

		try {
			shipmentExternalReferenceCode =
				shipmentExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String shipmentExternalReferenceCode;

	public Long getShipmentId() {
		return shipmentId;
	}

	public void setShipmentId(Long shipmentId) {
		this.shipmentId = shipmentId;
	}

	public void setShipmentId(
		UnsafeSupplier<Long, Exception> shipmentIdUnsafeSupplier) {

		try {
			shipmentId = shipmentIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long shipmentId;

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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setUserName(
		UnsafeSupplier<String, Exception> userNameUnsafeSupplier) {

		try {
			userName = userNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String userName;

	public Boolean getValidateInventory() {
		return validateInventory;
	}

	public void setValidateInventory(Boolean validateInventory) {
		this.validateInventory = validateInventory;
	}

	public void setValidateInventory(
		UnsafeSupplier<Boolean, Exception> validateInventoryUnsafeSupplier) {

		try {
			validateInventory = validateInventoryUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean validateInventory;

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
	public ShipmentItem clone() throws CloneNotSupportedException {
		return (ShipmentItem)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ShipmentItem)) {
			return false;
		}

		ShipmentItem shipmentItem = (ShipmentItem)object;

		return Objects.equals(toString(), shipmentItem.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ShipmentItemSerDes.toJSON(this);
	}

}