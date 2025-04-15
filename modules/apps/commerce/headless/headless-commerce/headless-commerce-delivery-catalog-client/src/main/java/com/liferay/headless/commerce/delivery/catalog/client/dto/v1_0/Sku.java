/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0;

import com.liferay.headless.commerce.delivery.catalog.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.delivery.catalog.client.serdes.v1_0.SkuSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Date;
import java.util.Objects;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class Sku implements Cloneable, Serializable {

	public static Sku toDTO(String json) {
		return SkuSerDes.toDTO(json);
	}

	public DDMOption[] getDDMOptions() {
		return DDMOptions;
	}

	public void setDDMOptions(DDMOption[] DDMOptions) {
		this.DDMOptions = DDMOptions;
	}

	public void setDDMOptions(
		UnsafeSupplier<DDMOption[], Exception> DDMOptionsUnsafeSupplier) {

		try {
			DDMOptions = DDMOptionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected DDMOption[] DDMOptions;

	public String[] getAllowedOrderQuantities() {
		return allowedOrderQuantities;
	}

	public void setAllowedOrderQuantities(String[] allowedOrderQuantities) {
		this.allowedOrderQuantities = allowedOrderQuantities;
	}

	public void setAllowedOrderQuantities(
		UnsafeSupplier<String[], Exception>
			allowedOrderQuantitiesUnsafeSupplier) {

		try {
			allowedOrderQuantities = allowedOrderQuantitiesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] allowedOrderQuantities;

	public Availability getAvailability() {
		return availability;
	}

	public void setAvailability(Availability availability) {
		this.availability = availability;
	}

	public void setAvailability(
		UnsafeSupplier<Availability, Exception> availabilityUnsafeSupplier) {

		try {
			availability = availabilityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Availability availability;

	public Boolean getBackOrderAllowed() {
		return backOrderAllowed;
	}

	public void setBackOrderAllowed(Boolean backOrderAllowed) {
		this.backOrderAllowed = backOrderAllowed;
	}

	public void setBackOrderAllowed(
		UnsafeSupplier<Boolean, Exception> backOrderAllowedUnsafeSupplier) {

		try {
			backOrderAllowed = backOrderAllowedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean backOrderAllowed;

	public com.liferay.headless.commerce.delivery.catalog.client.custom.field.
		CustomField[] getCustomFields() {

		return customFields;
	}

	public void setCustomFields(
		com.liferay.headless.commerce.delivery.catalog.client.custom.field.
			CustomField[] customFields) {

		this.customFields = customFields;
	}

	public void setCustomFields(
		UnsafeSupplier
			<com.liferay.headless.commerce.delivery.catalog.client.custom.field.
				CustomField[],
			 Exception> customFieldsUnsafeSupplier) {

		try {
			customFields = customFieldsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected
		com.liferay.headless.commerce.delivery.catalog.client.custom.field.
			CustomField[] customFields;

	public Double getDepth() {
		return depth;
	}

	public void setDepth(Double depth) {
		this.depth = depth;
	}

	public void setDepth(
		UnsafeSupplier<Double, Exception> depthUnsafeSupplier) {

		try {
			depth = depthUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double depth;

	public Boolean getDiscontinued() {
		return discontinued;
	}

	public void setDiscontinued(Boolean discontinued) {
		this.discontinued = discontinued;
	}

	public void setDiscontinued(
		UnsafeSupplier<Boolean, Exception> discontinuedUnsafeSupplier) {

		try {
			discontinued = discontinuedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean discontinued;

	public Date getDiscontinuedDate() {
		return discontinuedDate;
	}

	public void setDiscontinuedDate(Date discontinuedDate) {
		this.discontinuedDate = discontinuedDate;
	}

	public void setDiscontinuedDate(
		UnsafeSupplier<Date, Exception> discontinuedDateUnsafeSupplier) {

		try {
			discontinuedDate = discontinuedDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date discontinuedDate;

	public Date getDisplayDate() {
		return displayDate;
	}

	public void setDisplayDate(Date displayDate) {
		this.displayDate = displayDate;
	}

	public void setDisplayDate(
		UnsafeSupplier<Date, Exception> displayDateUnsafeSupplier) {

		try {
			displayDate = displayDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date displayDate;

	public Boolean getDisplayDiscountLevels() {
		return displayDiscountLevels;
	}

	public void setDisplayDiscountLevels(Boolean displayDiscountLevels) {
		this.displayDiscountLevels = displayDiscountLevels;
	}

	public void setDisplayDiscountLevels(
		UnsafeSupplier<Boolean, Exception>
			displayDiscountLevelsUnsafeSupplier) {

		try {
			displayDiscountLevels = displayDiscountLevelsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean displayDiscountLevels;

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public void setExpirationDate(
		UnsafeSupplier<Date, Exception> expirationDateUnsafeSupplier) {

		try {
			expirationDate = expirationDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date expirationDate;

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

	public String getGtin() {
		return gtin;
	}

	public void setGtin(String gtin) {
		this.gtin = gtin;
	}

	public void setGtin(UnsafeSupplier<String, Exception> gtinUnsafeSupplier) {
		try {
			gtin = gtinUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String gtin;

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public void setHeight(
		UnsafeSupplier<Double, Exception> heightUnsafeSupplier) {

		try {
			height = heightUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double height;

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

	public String getIncomingQuantityLabel() {
		return incomingQuantityLabel;
	}

	public void setIncomingQuantityLabel(String incomingQuantityLabel) {
		this.incomingQuantityLabel = incomingQuantityLabel;
	}

	public void setIncomingQuantityLabel(
		UnsafeSupplier<String, Exception> incomingQuantityLabelUnsafeSupplier) {

		try {
			incomingQuantityLabel = incomingQuantityLabelUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String incomingQuantityLabel;

	public String getManufacturerPartNumber() {
		return manufacturerPartNumber;
	}

	public void setManufacturerPartNumber(String manufacturerPartNumber) {
		this.manufacturerPartNumber = manufacturerPartNumber;
	}

	public void setManufacturerPartNumber(
		UnsafeSupplier<String, Exception>
			manufacturerPartNumberUnsafeSupplier) {

		try {
			manufacturerPartNumber = manufacturerPartNumberUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String manufacturerPartNumber;

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

	public Boolean getNeverExpire() {
		return neverExpire;
	}

	public void setNeverExpire(Boolean neverExpire) {
		this.neverExpire = neverExpire;
	}

	public void setNeverExpire(
		UnsafeSupplier<Boolean, Exception> neverExpireUnsafeSupplier) {

		try {
			neverExpire = neverExpireUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean neverExpire;

	public Price getPrice() {
		return price;
	}

	public void setPrice(Price price) {
		this.price = price;
	}

	public void setPrice(UnsafeSupplier<Price, Exception> priceUnsafeSupplier) {
		try {
			price = priceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Price price;

	public ProductConfiguration getProductConfiguration() {
		return productConfiguration;
	}

	public void setProductConfiguration(
		ProductConfiguration productConfiguration) {

		this.productConfiguration = productConfiguration;
	}

	public void setProductConfiguration(
		UnsafeSupplier<ProductConfiguration, Exception>
			productConfigurationUnsafeSupplier) {

		try {
			productConfiguration = productConfigurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ProductConfiguration productConfiguration;

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public void setProductId(
		UnsafeSupplier<Long, Exception> productIdUnsafeSupplier) {

		try {
			productId = productIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long productId;

	public Boolean getPublished() {
		return published;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	}

	public void setPublished(
		UnsafeSupplier<Boolean, Exception> publishedUnsafeSupplier) {

		try {
			published = publishedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean published;

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

	public ReplacementSku getReplacementSku() {
		return replacementSku;
	}

	public void setReplacementSku(ReplacementSku replacementSku) {
		this.replacementSku = replacementSku;
	}

	public void setReplacementSku(
		UnsafeSupplier<ReplacementSku, Exception>
			replacementSkuUnsafeSupplier) {

		try {
			replacementSku = replacementSkuUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ReplacementSku replacementSku;

	public String getReplacementSkuExternalReferenceCode() {
		return replacementSkuExternalReferenceCode;
	}

	public void setReplacementSkuExternalReferenceCode(
		String replacementSkuExternalReferenceCode) {

		this.replacementSkuExternalReferenceCode =
			replacementSkuExternalReferenceCode;
	}

	public void setReplacementSkuExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			replacementSkuExternalReferenceCodeUnsafeSupplier) {

		try {
			replacementSkuExternalReferenceCode =
				replacementSkuExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String replacementSkuExternalReferenceCode;

	public Long getReplacementSkuId() {
		return replacementSkuId;
	}

	public void setReplacementSkuId(Long replacementSkuId) {
		this.replacementSkuId = replacementSkuId;
	}

	public void setReplacementSkuId(
		UnsafeSupplier<Long, Exception> replacementSkuIdUnsafeSupplier) {

		try {
			replacementSkuId = replacementSkuIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long replacementSkuId;

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

	public SkuOption[] getSkuOptions() {
		return skuOptions;
	}

	public void setSkuOptions(SkuOption[] skuOptions) {
		this.skuOptions = skuOptions;
	}

	public void setSkuOptions(
		UnsafeSupplier<SkuOption[], Exception> skuOptionsUnsafeSupplier) {

		try {
			skuOptions = skuOptionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected SkuOption[] skuOptions;

	public SkuUnitOfMeasure[] getSkuUnitOfMeasures() {
		return skuUnitOfMeasures;
	}

	public void setSkuUnitOfMeasures(SkuUnitOfMeasure[] skuUnitOfMeasures) {
		this.skuUnitOfMeasures = skuUnitOfMeasures;
	}

	public void setSkuUnitOfMeasures(
		UnsafeSupplier<SkuUnitOfMeasure[], Exception>
			skuUnitOfMeasuresUnsafeSupplier) {

		try {
			skuUnitOfMeasures = skuUnitOfMeasuresUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected SkuUnitOfMeasure[] skuUnitOfMeasures;

	public TierPrice[] getTierPrices() {
		return tierPrices;
	}

	public void setTierPrices(TierPrice[] tierPrices) {
		this.tierPrices = tierPrices;
	}

	public void setTierPrices(
		UnsafeSupplier<TierPrice[], Exception> tierPricesUnsafeSupplier) {

		try {
			tierPrices = tierPricesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected TierPrice[] tierPrices;

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public void setWeight(
		UnsafeSupplier<Double, Exception> weightUnsafeSupplier) {

		try {
			weight = weightUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double weight;

	public Double getWidth() {
		return width;
	}

	public void setWidth(Double width) {
		this.width = width;
	}

	public void setWidth(
		UnsafeSupplier<Double, Exception> widthUnsafeSupplier) {

		try {
			width = widthUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double width;

	@Override
	public Sku clone() throws CloneNotSupportedException {
		return (Sku)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Sku)) {
			return false;
		}

		Sku sku = (Sku)object;

		return Objects.equals(toString(), sku.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SkuSerDes.toJSON(this);
	}

}