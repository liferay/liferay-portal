/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.dto.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.ProductConfigurationSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Map;
import java.util.Objects;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class ProductConfiguration implements Cloneable, Serializable {

	public static ProductConfiguration toDTO(String json) {
		return ProductConfigurationSerDes.toDTO(json);
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

	public Boolean getAllowBackOrder() {
		return allowBackOrder;
	}

	public void setAllowBackOrder(Boolean allowBackOrder) {
		this.allowBackOrder = allowBackOrder;
	}

	public void setAllowBackOrder(
		UnsafeSupplier<Boolean, Exception> allowBackOrderUnsafeSupplier) {

		try {
			allowBackOrder = allowBackOrderUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean allowBackOrder;

	public BigDecimal[] getAllowedOrderQuantities() {
		return allowedOrderQuantities;
	}

	public void setAllowedOrderQuantities(BigDecimal[] allowedOrderQuantities) {
		this.allowedOrderQuantities = allowedOrderQuantities;
	}

	public void setAllowedOrderQuantities(
		UnsafeSupplier<BigDecimal[], Exception>
			allowedOrderQuantitiesUnsafeSupplier) {

		try {
			allowedOrderQuantities = allowedOrderQuantitiesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal[] allowedOrderQuantities;

	public Long getAvailabilityEstimateId() {
		return availabilityEstimateId;
	}

	public void setAvailabilityEstimateId(Long availabilityEstimateId) {
		this.availabilityEstimateId = availabilityEstimateId;
	}

	public void setAvailabilityEstimateId(
		UnsafeSupplier<Long, Exception> availabilityEstimateIdUnsafeSupplier) {

		try {
			availabilityEstimateId = availabilityEstimateIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long availabilityEstimateId;

	public Map<String, String> getAvailabilityEstimateName() {
		return availabilityEstimateName;
	}

	public void setAvailabilityEstimateName(
		Map<String, String> availabilityEstimateName) {

		this.availabilityEstimateName = availabilityEstimateName;
	}

	public void setAvailabilityEstimateName(
		UnsafeSupplier<Map<String, String>, Exception>
			availabilityEstimateNameUnsafeSupplier) {

		try {
			availabilityEstimateName =
				availabilityEstimateNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> availabilityEstimateName;

	public String[] getDifferences() {
		return differences;
	}

	public void setDifferences(String[] differences) {
		this.differences = differences;
	}

	public void setDifferences(
		UnsafeSupplier<String[], Exception> differencesUnsafeSupplier) {

		try {
			differences = differencesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] differences;

	public Boolean getDisplayAvailability() {
		return displayAvailability;
	}

	public void setDisplayAvailability(Boolean displayAvailability) {
		this.displayAvailability = displayAvailability;
	}

	public void setDisplayAvailability(
		UnsafeSupplier<Boolean, Exception> displayAvailabilityUnsafeSupplier) {

		try {
			displayAvailability = displayAvailabilityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean displayAvailability;

	public Boolean getDisplayStockQuantity() {
		return displayStockQuantity;
	}

	public void setDisplayStockQuantity(Boolean displayStockQuantity) {
		this.displayStockQuantity = displayStockQuantity;
	}

	public void setDisplayStockQuantity(
		UnsafeSupplier<Boolean, Exception> displayStockQuantityUnsafeSupplier) {

		try {
			displayStockQuantity = displayStockQuantityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean displayStockQuantity;

	public String getEntityExternalReferenceCode() {
		return entityExternalReferenceCode;
	}

	public void setEntityExternalReferenceCode(
		String entityExternalReferenceCode) {

		this.entityExternalReferenceCode = entityExternalReferenceCode;
	}

	public void setEntityExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			entityExternalReferenceCodeUnsafeSupplier) {

		try {
			entityExternalReferenceCode =
				entityExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String entityExternalReferenceCode;

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public void setEntityId(
		UnsafeSupplier<Long, Exception> entityIdUnsafeSupplier) {

		try {
			entityId = entityIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long entityId;

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public void setEntityName(
		UnsafeSupplier<String, Exception> entityNameUnsafeSupplier) {

		try {
			entityName = entityNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String entityName;

	public EntityType getEntityType() {
		return entityType;
	}

	public String getEntityTypeAsString() {
		if (entityType == null) {
			return null;
		}

		return entityType.toString();
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

	public void setEntityType(
		UnsafeSupplier<EntityType, Exception> entityTypeUnsafeSupplier) {

		try {
			entityType = entityTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected EntityType entityType;

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

	public String getInventoryEngine() {
		return inventoryEngine;
	}

	public void setInventoryEngine(String inventoryEngine) {
		this.inventoryEngine = inventoryEngine;
	}

	public void setInventoryEngine(
		UnsafeSupplier<String, Exception> inventoryEngineUnsafeSupplier) {

		try {
			inventoryEngine = inventoryEngineUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String inventoryEngine;

	public String getLowStockAction() {
		return lowStockAction;
	}

	public void setLowStockAction(String lowStockAction) {
		this.lowStockAction = lowStockAction;
	}

	public void setLowStockAction(
		UnsafeSupplier<String, Exception> lowStockActionUnsafeSupplier) {

		try {
			lowStockAction = lowStockActionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String lowStockAction;

	public BigDecimal getMaxOrderQuantity() {
		return maxOrderQuantity;
	}

	public void setMaxOrderQuantity(BigDecimal maxOrderQuantity) {
		this.maxOrderQuantity = maxOrderQuantity;
	}

	public void setMaxOrderQuantity(
		UnsafeSupplier<BigDecimal, Exception> maxOrderQuantityUnsafeSupplier) {

		try {
			maxOrderQuantity = maxOrderQuantityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal maxOrderQuantity;

	public BigDecimal getMinOrderQuantity() {
		return minOrderQuantity;
	}

	public void setMinOrderQuantity(BigDecimal minOrderQuantity) {
		this.minOrderQuantity = minOrderQuantity;
	}

	public void setMinOrderQuantity(
		UnsafeSupplier<BigDecimal, Exception> minOrderQuantityUnsafeSupplier) {

		try {
			minOrderQuantity = minOrderQuantityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal minOrderQuantity;

	public BigDecimal getMinStockQuantity() {
		return minStockQuantity;
	}

	public void setMinStockQuantity(BigDecimal minStockQuantity) {
		this.minStockQuantity = minStockQuantity;
	}

	public void setMinStockQuantity(
		UnsafeSupplier<BigDecimal, Exception> minStockQuantityUnsafeSupplier) {

		try {
			minStockQuantity = minStockQuantityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal minStockQuantity;

	public BigDecimal getMultipleOrderQuantity() {
		return multipleOrderQuantity;
	}

	public void setMultipleOrderQuantity(BigDecimal multipleOrderQuantity) {
		this.multipleOrderQuantity = multipleOrderQuantity;
	}

	public void setMultipleOrderQuantity(
		UnsafeSupplier<BigDecimal, Exception>
			multipleOrderQuantityUnsafeSupplier) {

		try {
			multipleOrderQuantity = multipleOrderQuantityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal multipleOrderQuantity;

	public ProductShippingConfiguration getProductShippingConfiguration() {
		return productShippingConfiguration;
	}

	public void setProductShippingConfiguration(
		ProductShippingConfiguration productShippingConfiguration) {

		this.productShippingConfiguration = productShippingConfiguration;
	}

	public void setProductShippingConfiguration(
		UnsafeSupplier<ProductShippingConfiguration, Exception>
			productShippingConfigurationUnsafeSupplier) {

		try {
			productShippingConfiguration =
				productShippingConfigurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ProductShippingConfiguration productShippingConfiguration;

	public ProductTaxConfiguration getProductTaxConfiguration() {
		return productTaxConfiguration;
	}

	public void setProductTaxConfiguration(
		ProductTaxConfiguration productTaxConfiguration) {

		this.productTaxConfiguration = productTaxConfiguration;
	}

	public void setProductTaxConfiguration(
		UnsafeSupplier<ProductTaxConfiguration, Exception>
			productTaxConfigurationUnsafeSupplier) {

		try {
			productTaxConfiguration =
				productTaxConfigurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ProductTaxConfiguration productTaxConfiguration;

	public Boolean getPurchasable() {
		return purchasable;
	}

	public void setPurchasable(Boolean purchasable) {
		this.purchasable = purchasable;
	}

	public void setPurchasable(
		UnsafeSupplier<Boolean, Exception> purchasableUnsafeSupplier) {

		try {
			purchasable = purchasableUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean purchasable;

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public void setVisible(
		UnsafeSupplier<Boolean, Exception> visibleUnsafeSupplier) {

		try {
			visible = visibleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean visible;

	@Override
	public ProductConfiguration clone() throws CloneNotSupportedException {
		return (ProductConfiguration)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProductConfiguration)) {
			return false;
		}

		ProductConfiguration productConfiguration =
			(ProductConfiguration)object;

		return Objects.equals(toString(), productConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ProductConfigurationSerDes.toJSON(this);
	}

	public static enum EntityType {

		PRODUCT("product"), TEMPLATE("template");

		public static EntityType create(String value) {
			for (EntityType entityType : values()) {
				if (Objects.equals(entityType.getValue(), value) ||
					Objects.equals(entityType.name(), value)) {

					return entityType;
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

		private EntityType(String value) {
			_value = value;
		}

		private final String _value;

	}

}