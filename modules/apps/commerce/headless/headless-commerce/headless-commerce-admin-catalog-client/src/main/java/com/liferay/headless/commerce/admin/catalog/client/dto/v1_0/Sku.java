/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.dto.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.SkuSerDes;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class Sku implements Cloneable, Serializable {

	public static Sku toDTO(String json) {
		return SkuSerDes.toDTO(json);
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public void setCost(
		UnsafeSupplier<BigDecimal, Exception> costUnsafeSupplier) {

		try {
			cost = costUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal cost;

	public com.liferay.headless.commerce.admin.catalog.client.custom.field.
		CustomField[] getCustomFields() {

		return customFields;
	}

	public void setCustomFields(
		com.liferay.headless.commerce.admin.catalog.client.custom.field.
			CustomField[] customFields) {

		this.customFields = customFields;
	}

	public void setCustomFields(
		UnsafeSupplier
			<com.liferay.headless.commerce.admin.catalog.client.custom.field.
				CustomField[],
			 Exception> customFieldsUnsafeSupplier) {

		try {
			customFields = customFieldsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected com.liferay.headless.commerce.admin.catalog.client.custom.field.
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

	public Integer getInventoryLevel() {
		return inventoryLevel;
	}

	public void setInventoryLevel(Integer inventoryLevel) {
		this.inventoryLevel = inventoryLevel;
	}

	public void setInventoryLevel(
		UnsafeSupplier<Integer, Exception> inventoryLevelUnsafeSupplier) {

		try {
			inventoryLevel = inventoryLevelUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer inventoryLevel;

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

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public void setPrice(
		UnsafeSupplier<BigDecimal, Exception> priceUnsafeSupplier) {

		try {
			price = priceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal price;

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

	public Map<String, String> getProductName() {
		return productName;
	}

	public void setProductName(Map<String, String> productName) {
		this.productName = productName;
	}

	public void setProductName(
		UnsafeSupplier<Map<String, String>, Exception>
			productNameUnsafeSupplier) {

		try {
			productName = productNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> productName;

	public BigDecimal getPromoPrice() {
		return promoPrice;
	}

	public void setPromoPrice(BigDecimal promoPrice) {
		this.promoPrice = promoPrice;
	}

	public void setPromoPrice(
		UnsafeSupplier<BigDecimal, Exception> promoPriceUnsafeSupplier) {

		try {
			promoPrice = promoPriceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal promoPrice;

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

	public SkuSubscriptionConfiguration getSkuSubscriptionConfiguration() {
		return skuSubscriptionConfiguration;
	}

	public void setSkuSubscriptionConfiguration(
		SkuSubscriptionConfiguration skuSubscriptionConfiguration) {

		this.skuSubscriptionConfiguration = skuSubscriptionConfiguration;
	}

	public void setSkuSubscriptionConfiguration(
		UnsafeSupplier<SkuSubscriptionConfiguration, Exception>
			skuSubscriptionConfigurationUnsafeSupplier) {

		try {
			skuSubscriptionConfiguration =
				skuSubscriptionConfigurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected SkuSubscriptionConfiguration skuSubscriptionConfiguration;

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

	public SkuVirtualSettings getSkuVirtualSettings() {
		return skuVirtualSettings;
	}

	public void setSkuVirtualSettings(SkuVirtualSettings skuVirtualSettings) {
		this.skuVirtualSettings = skuVirtualSettings;
	}

	public void setSkuVirtualSettings(
		UnsafeSupplier<SkuVirtualSettings, Exception>
			skuVirtualSettingsUnsafeSupplier) {

		try {
			skuVirtualSettings = skuVirtualSettingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected SkuVirtualSettings skuVirtualSettings;

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

	public Map<String, String> getUnitOfMeasureName() {
		return unitOfMeasureName;
	}

	public void setUnitOfMeasureName(Map<String, String> unitOfMeasureName) {
		this.unitOfMeasureName = unitOfMeasureName;
	}

	public void setUnitOfMeasureName(
		UnsafeSupplier<Map<String, String>, Exception>
			unitOfMeasureNameUnsafeSupplier) {

		try {
			unitOfMeasureName = unitOfMeasureNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> unitOfMeasureName;

	public String getUnitOfMeasureSkuId() {
		return unitOfMeasureSkuId;
	}

	public void setUnitOfMeasureSkuId(String unitOfMeasureSkuId) {
		this.unitOfMeasureSkuId = unitOfMeasureSkuId;
	}

	public void setUnitOfMeasureSkuId(
		UnsafeSupplier<String, Exception> unitOfMeasureSkuIdUnsafeSupplier) {

		try {
			unitOfMeasureSkuId = unitOfMeasureSkuIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String unitOfMeasureSkuId;

	public String getUnspsc() {
		return unspsc;
	}

	public void setUnspsc(String unspsc) {
		this.unspsc = unspsc;
	}

	public void setUnspsc(
		UnsafeSupplier<String, Exception> unspscUnsafeSupplier) {

		try {
			unspsc = unspscUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String unspsc;

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