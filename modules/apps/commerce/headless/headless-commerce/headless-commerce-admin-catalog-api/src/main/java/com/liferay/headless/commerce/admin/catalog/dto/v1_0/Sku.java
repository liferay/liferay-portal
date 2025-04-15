/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
@GraphQLName("Sku")
@io.swagger.v3.oas.annotations.media.Schema(requiredProperties = {"sku"})
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Sku")
public class Sku implements Serializable {

	public static Sku toDTO(String json) {
		return ObjectMapperUtil.readValue(Sku.class, json);
	}

	public static Sku unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Sku.class, json);
	}

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "101")
	@Valid
	public BigDecimal getCost() {
		if (_costSupplier != null) {
			cost = _costSupplier.get();

			_costSupplier = null;
		}

		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;

		_costSupplier = null;
	}

	@JsonIgnore
	public void setCost(
		UnsafeSupplier<BigDecimal, Exception> costUnsafeSupplier) {

		_costSupplier = () -> {
			try {
				return costUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected BigDecimal cost;

	@JsonIgnore
	private Supplier<BigDecimal> _costSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public com.liferay.portal.vulcan.custom.field.CustomField[]
		getCustomFields() {

		if (_customFieldsSupplier != null) {
			customFields = _customFieldsSupplier.get();

			_customFieldsSupplier = null;
		}

		return customFields;
	}

	public void setCustomFields(
		com.liferay.portal.vulcan.custom.field.CustomField[] customFields) {

		this.customFields = customFields;

		_customFieldsSupplier = null;
	}

	@JsonIgnore
	public void setCustomFields(
		UnsafeSupplier
			<com.liferay.portal.vulcan.custom.field.CustomField[], Exception>
				customFieldsUnsafeSupplier) {

		_customFieldsSupplier = () -> {
			try {
				return customFieldsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected com.liferay.portal.vulcan.custom.field.CustomField[] customFields;

	@JsonIgnore
	private Supplier<com.liferay.portal.vulcan.custom.field.CustomField[]>
		_customFieldsSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1.1")
	public Double getDepth() {
		if (_depthSupplier != null) {
			depth = _depthSupplier.get();

			_depthSupplier = null;
		}

		return depth;
	}

	public void setDepth(Double depth) {
		this.depth = depth;

		_depthSupplier = null;
	}

	@JsonIgnore
	public void setDepth(
		UnsafeSupplier<Double, Exception> depthUnsafeSupplier) {

		_depthSupplier = () -> {
			try {
				return depthUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Double depth;

	@JsonIgnore
	private Supplier<Double> _depthSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "false")
	public Boolean getDiscontinued() {
		if (_discontinuedSupplier != null) {
			discontinued = _discontinuedSupplier.get();

			_discontinuedSupplier = null;
		}

		return discontinued;
	}

	public void setDiscontinued(Boolean discontinued) {
		this.discontinued = discontinued;

		_discontinuedSupplier = null;
	}

	@JsonIgnore
	public void setDiscontinued(
		UnsafeSupplier<Boolean, Exception> discontinuedUnsafeSupplier) {

		_discontinuedSupplier = () -> {
			try {
				return discontinuedUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean discontinued;

	@JsonIgnore
	private Supplier<Boolean> _discontinuedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "2017-07-21")
	public Date getDiscontinuedDate() {
		if (_discontinuedDateSupplier != null) {
			discontinuedDate = _discontinuedDateSupplier.get();

			_discontinuedDateSupplier = null;
		}

		return discontinuedDate;
	}

	public void setDiscontinuedDate(Date discontinuedDate) {
		this.discontinuedDate = discontinuedDate;

		_discontinuedDateSupplier = null;
	}

	@JsonIgnore
	public void setDiscontinuedDate(
		UnsafeSupplier<Date, Exception> discontinuedDateUnsafeSupplier) {

		_discontinuedDateSupplier = () -> {
			try {
				return discontinuedDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date discontinuedDate;

	@JsonIgnore
	private Supplier<Date> _discontinuedDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "2017-07-21")
	public Date getDisplayDate() {
		if (_displayDateSupplier != null) {
			displayDate = _displayDateSupplier.get();

			_displayDateSupplier = null;
		}

		return displayDate;
	}

	public void setDisplayDate(Date displayDate) {
		this.displayDate = displayDate;

		_displayDateSupplier = null;
	}

	@JsonIgnore
	public void setDisplayDate(
		UnsafeSupplier<Date, Exception> displayDateUnsafeSupplier) {

		_displayDateSupplier = () -> {
			try {
				return displayDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date displayDate;

	@JsonIgnore
	private Supplier<Date> _displayDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "2017-08-21")
	public Date getExpirationDate() {
		if (_expirationDateSupplier != null) {
			expirationDate = _expirationDateSupplier.get();

			_expirationDateSupplier = null;
		}

		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;

		_expirationDateSupplier = null;
	}

	@JsonIgnore
	public void setExpirationDate(
		UnsafeSupplier<Date, Exception> expirationDateUnsafeSupplier) {

		_expirationDateSupplier = () -> {
			try {
				return expirationDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date expirationDate;

	@JsonIgnore
	private Supplier<Date> _expirationDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "AB-34098-789-N")
	public String getExternalReferenceCode() {
		if (_externalReferenceCodeSupplier != null) {
			externalReferenceCode = _externalReferenceCodeSupplier.get();

			_externalReferenceCodeSupplier = null;
		}

		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;

		_externalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		_externalReferenceCodeSupplier = () -> {
			try {
				return externalReferenceCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "12341234")
	public String getGtin() {
		if (_gtinSupplier != null) {
			gtin = _gtinSupplier.get();

			_gtinSupplier = null;
		}

		return gtin;
	}

	public void setGtin(String gtin) {
		this.gtin = gtin;

		_gtinSupplier = null;
	}

	@JsonIgnore
	public void setGtin(UnsafeSupplier<String, Exception> gtinUnsafeSupplier) {
		_gtinSupplier = () -> {
			try {
				return gtinUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String gtin;

	@JsonIgnore
	private Supplier<String> _gtinSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "20.2")
	public Double getHeight() {
		if (_heightSupplier != null) {
			height = _heightSupplier.get();

			_heightSupplier = null;
		}

		return height;
	}

	public void setHeight(Double height) {
		this.height = height;

		_heightSupplier = null;
	}

	@JsonIgnore
	public void setHeight(
		UnsafeSupplier<Double, Exception> heightUnsafeSupplier) {

		_heightSupplier = () -> {
			try {
				return heightUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Double height;

	@JsonIgnore
	private Supplier<Double> _heightSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	public Long getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(Long id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		_idSupplier = () -> {
			try {
				return idUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "29")
	public Integer getInventoryLevel() {
		if (_inventoryLevelSupplier != null) {
			inventoryLevel = _inventoryLevelSupplier.get();

			_inventoryLevelSupplier = null;
		}

		return inventoryLevel;
	}

	public void setInventoryLevel(Integer inventoryLevel) {
		this.inventoryLevel = inventoryLevel;

		_inventoryLevelSupplier = null;
	}

	@JsonIgnore
	public void setInventoryLevel(
		UnsafeSupplier<Integer, Exception> inventoryLevelUnsafeSupplier) {

		_inventoryLevelSupplier = () -> {
			try {
				return inventoryLevelUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Integer inventoryLevel;

	@JsonIgnore
	private Supplier<Integer> _inventoryLevelSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "12341234")
	public String getManufacturerPartNumber() {
		if (_manufacturerPartNumberSupplier != null) {
			manufacturerPartNumber = _manufacturerPartNumberSupplier.get();

			_manufacturerPartNumberSupplier = null;
		}

		return manufacturerPartNumber;
	}

	public void setManufacturerPartNumber(String manufacturerPartNumber) {
		this.manufacturerPartNumber = manufacturerPartNumber;

		_manufacturerPartNumberSupplier = null;
	}

	@JsonIgnore
	public void setManufacturerPartNumber(
		UnsafeSupplier<String, Exception>
			manufacturerPartNumberUnsafeSupplier) {

		_manufacturerPartNumberSupplier = () -> {
			try {
				return manufacturerPartNumberUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String manufacturerPartNumber;

	@JsonIgnore
	private Supplier<String> _manufacturerPartNumberSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getNeverExpire() {
		if (_neverExpireSupplier != null) {
			neverExpire = _neverExpireSupplier.get();

			_neverExpireSupplier = null;
		}

		return neverExpire;
	}

	public void setNeverExpire(Boolean neverExpire) {
		this.neverExpire = neverExpire;

		_neverExpireSupplier = null;
	}

	@JsonIgnore
	public void setNeverExpire(
		UnsafeSupplier<Boolean, Exception> neverExpireUnsafeSupplier) {

		_neverExpireSupplier = () -> {
			try {
				return neverExpireUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean neverExpire;

	@JsonIgnore
	private Supplier<Boolean> _neverExpireSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "101")
	@Valid
	public BigDecimal getPrice() {
		if (_priceSupplier != null) {
			price = _priceSupplier.get();

			_priceSupplier = null;
		}

		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;

		_priceSupplier = null;
	}

	@JsonIgnore
	public void setPrice(
		UnsafeSupplier<BigDecimal, Exception> priceUnsafeSupplier) {

		_priceSupplier = () -> {
			try {
				return priceUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected BigDecimal price;

	@JsonIgnore
	private Supplier<BigDecimal> _priceSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30129")
	public Long getProductId() {
		if (_productIdSupplier != null) {
			productId = _productIdSupplier.get();

			_productIdSupplier = null;
		}

		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;

		_productIdSupplier = null;
	}

	@JsonIgnore
	public void setProductId(
		UnsafeSupplier<Long, Exception> productIdUnsafeSupplier) {

		_productIdSupplier = () -> {
			try {
				return productIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long productId;

	@JsonIgnore
	private Supplier<Long> _productIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "{en_US=Croatia, hr_HR=Hrvatska, hu_HU=Horvatorszag}"
	)
	@Valid
	public Map<String, String> getProductName() {
		if (_productNameSupplier != null) {
			productName = _productNameSupplier.get();

			_productNameSupplier = null;
		}

		return productName;
	}

	public void setProductName(Map<String, String> productName) {
		this.productName = productName;

		_productNameSupplier = null;
	}

	@JsonIgnore
	public void setProductName(
		UnsafeSupplier<Map<String, String>, Exception>
			productNameUnsafeSupplier) {

		_productNameSupplier = () -> {
			try {
				return productNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, String> productName;

	@JsonIgnore
	private Supplier<Map<String, String>> _productNameSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "202")
	@Valid
	public BigDecimal getPromoPrice() {
		if (_promoPriceSupplier != null) {
			promoPrice = _promoPriceSupplier.get();

			_promoPriceSupplier = null;
		}

		return promoPrice;
	}

	public void setPromoPrice(BigDecimal promoPrice) {
		this.promoPrice = promoPrice;

		_promoPriceSupplier = null;
	}

	@JsonIgnore
	public void setPromoPrice(
		UnsafeSupplier<BigDecimal, Exception> promoPriceUnsafeSupplier) {

		_promoPriceSupplier = () -> {
			try {
				return promoPriceUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected BigDecimal promoPrice;

	@JsonIgnore
	private Supplier<BigDecimal> _promoPriceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getPublished() {
		if (_publishedSupplier != null) {
			published = _publishedSupplier.get();

			_publishedSupplier = null;
		}

		return published;
	}

	public void setPublished(Boolean published) {
		this.published = published;

		_publishedSupplier = null;
	}

	@JsonIgnore
	public void setPublished(
		UnsafeSupplier<Boolean, Exception> publishedUnsafeSupplier) {

		_publishedSupplier = () -> {
			try {
				return publishedUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean published;

	@JsonIgnore
	private Supplier<Boolean> _publishedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getPurchasable() {
		if (_purchasableSupplier != null) {
			purchasable = _purchasableSupplier.get();

			_purchasableSupplier = null;
		}

		return purchasable;
	}

	public void setPurchasable(Boolean purchasable) {
		this.purchasable = purchasable;

		_purchasableSupplier = null;
	}

	@JsonIgnore
	public void setPurchasable(
		UnsafeSupplier<Boolean, Exception> purchasableUnsafeSupplier) {

		_purchasableSupplier = () -> {
			try {
				return purchasableUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean purchasable;

	@JsonIgnore
	private Supplier<Boolean> _purchasableSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "SKU0111")
	public String getReplacementSkuExternalReferenceCode() {
		if (_replacementSkuExternalReferenceCodeSupplier != null) {
			replacementSkuExternalReferenceCode =
				_replacementSkuExternalReferenceCodeSupplier.get();

			_replacementSkuExternalReferenceCodeSupplier = null;
		}

		return replacementSkuExternalReferenceCode;
	}

	public void setReplacementSkuExternalReferenceCode(
		String replacementSkuExternalReferenceCode) {

		this.replacementSkuExternalReferenceCode =
			replacementSkuExternalReferenceCode;

		_replacementSkuExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setReplacementSkuExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			replacementSkuExternalReferenceCodeUnsafeSupplier) {

		_replacementSkuExternalReferenceCodeSupplier = () -> {
			try {
				return replacementSkuExternalReferenceCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String replacementSkuExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _replacementSkuExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "33135")
	public Long getReplacementSkuId() {
		if (_replacementSkuIdSupplier != null) {
			replacementSkuId = _replacementSkuIdSupplier.get();

			_replacementSkuIdSupplier = null;
		}

		return replacementSkuId;
	}

	public void setReplacementSkuId(Long replacementSkuId) {
		this.replacementSkuId = replacementSkuId;

		_replacementSkuIdSupplier = null;
	}

	@JsonIgnore
	public void setReplacementSkuId(
		UnsafeSupplier<Long, Exception> replacementSkuIdUnsafeSupplier) {

		_replacementSkuIdSupplier = () -> {
			try {
				return replacementSkuIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long replacementSkuId;

	@JsonIgnore
	private Supplier<Long> _replacementSkuIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "12341234")
	public String getSku() {
		if (_skuSupplier != null) {
			sku = _skuSupplier.get();

			_skuSupplier = null;
		}

		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;

		_skuSupplier = null;
	}

	@JsonIgnore
	public void setSku(UnsafeSupplier<String, Exception> skuUnsafeSupplier) {
		_skuSupplier = () -> {
			try {
				return skuUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String sku;

	@JsonIgnore
	private Supplier<String> _skuSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public SkuOption[] getSkuOptions() {
		if (_skuOptionsSupplier != null) {
			skuOptions = _skuOptionsSupplier.get();

			_skuOptionsSupplier = null;
		}

		return skuOptions;
	}

	public void setSkuOptions(SkuOption[] skuOptions) {
		this.skuOptions = skuOptions;

		_skuOptionsSupplier = null;
	}

	@JsonIgnore
	public void setSkuOptions(
		UnsafeSupplier<SkuOption[], Exception> skuOptionsUnsafeSupplier) {

		_skuOptionsSupplier = () -> {
			try {
				return skuOptionsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected SkuOption[] skuOptions;

	@JsonIgnore
	private Supplier<SkuOption[]> _skuOptionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public SkuSubscriptionConfiguration getSkuSubscriptionConfiguration() {
		if (_skuSubscriptionConfigurationSupplier != null) {
			skuSubscriptionConfiguration =
				_skuSubscriptionConfigurationSupplier.get();

			_skuSubscriptionConfigurationSupplier = null;
		}

		return skuSubscriptionConfiguration;
	}

	public void setSkuSubscriptionConfiguration(
		SkuSubscriptionConfiguration skuSubscriptionConfiguration) {

		this.skuSubscriptionConfiguration = skuSubscriptionConfiguration;

		_skuSubscriptionConfigurationSupplier = null;
	}

	@JsonIgnore
	public void setSkuSubscriptionConfiguration(
		UnsafeSupplier<SkuSubscriptionConfiguration, Exception>
			skuSubscriptionConfigurationUnsafeSupplier) {

		_skuSubscriptionConfigurationSupplier = () -> {
			try {
				return skuSubscriptionConfigurationUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected SkuSubscriptionConfiguration skuSubscriptionConfiguration;

	@JsonIgnore
	private Supplier<SkuSubscriptionConfiguration>
		_skuSubscriptionConfigurationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public SkuUnitOfMeasure[] getSkuUnitOfMeasures() {
		if (_skuUnitOfMeasuresSupplier != null) {
			skuUnitOfMeasures = _skuUnitOfMeasuresSupplier.get();

			_skuUnitOfMeasuresSupplier = null;
		}

		return skuUnitOfMeasures;
	}

	public void setSkuUnitOfMeasures(SkuUnitOfMeasure[] skuUnitOfMeasures) {
		this.skuUnitOfMeasures = skuUnitOfMeasures;

		_skuUnitOfMeasuresSupplier = null;
	}

	@JsonIgnore
	public void setSkuUnitOfMeasures(
		UnsafeSupplier<SkuUnitOfMeasure[], Exception>
			skuUnitOfMeasuresUnsafeSupplier) {

		_skuUnitOfMeasuresSupplier = () -> {
			try {
				return skuUnitOfMeasuresUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected SkuUnitOfMeasure[] skuUnitOfMeasures;

	@JsonIgnore
	private Supplier<SkuUnitOfMeasure[]> _skuUnitOfMeasuresSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public SkuVirtualSettings getSkuVirtualSettings() {
		if (_skuVirtualSettingsSupplier != null) {
			skuVirtualSettings = _skuVirtualSettingsSupplier.get();

			_skuVirtualSettingsSupplier = null;
		}

		return skuVirtualSettings;
	}

	public void setSkuVirtualSettings(SkuVirtualSettings skuVirtualSettings) {
		this.skuVirtualSettings = skuVirtualSettings;

		_skuVirtualSettingsSupplier = null;
	}

	@JsonIgnore
	public void setSkuVirtualSettings(
		UnsafeSupplier<SkuVirtualSettings, Exception>
			skuVirtualSettingsUnsafeSupplier) {

		_skuVirtualSettingsSupplier = () -> {
			try {
				return skuVirtualSettingsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected SkuVirtualSettings skuVirtualSettings;

	@JsonIgnore
	private Supplier<SkuVirtualSettings> _skuVirtualSettingsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "pl")
	public String getUnitOfMeasureKey() {
		if (_unitOfMeasureKeySupplier != null) {
			unitOfMeasureKey = _unitOfMeasureKeySupplier.get();

			_unitOfMeasureKeySupplier = null;
		}

		return unitOfMeasureKey;
	}

	public void setUnitOfMeasureKey(String unitOfMeasureKey) {
		this.unitOfMeasureKey = unitOfMeasureKey;

		_unitOfMeasureKeySupplier = null;
	}

	@JsonIgnore
	public void setUnitOfMeasureKey(
		UnsafeSupplier<String, Exception> unitOfMeasureKeyUnsafeSupplier) {

		_unitOfMeasureKeySupplier = () -> {
			try {
				return unitOfMeasureKeyUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String unitOfMeasureKey;

	@JsonIgnore
	private Supplier<String> _unitOfMeasureKeySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "{en_US=Pallet, hr_HR=Pallet HR, hu_HU=Pallet HU}"
	)
	@Valid
	public Map<String, String> getUnitOfMeasureName() {
		if (_unitOfMeasureNameSupplier != null) {
			unitOfMeasureName = _unitOfMeasureNameSupplier.get();

			_unitOfMeasureNameSupplier = null;
		}

		return unitOfMeasureName;
	}

	public void setUnitOfMeasureName(Map<String, String> unitOfMeasureName) {
		this.unitOfMeasureName = unitOfMeasureName;

		_unitOfMeasureNameSupplier = null;
	}

	@JsonIgnore
	public void setUnitOfMeasureName(
		UnsafeSupplier<Map<String, String>, Exception>
			unitOfMeasureNameUnsafeSupplier) {

		_unitOfMeasureNameSupplier = () -> {
			try {
				return unitOfMeasureNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, String> unitOfMeasureName;

	@JsonIgnore
	private Supplier<Map<String, String>> _unitOfMeasureNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getUnitOfMeasureSkuId() {
		if (_unitOfMeasureSkuIdSupplier != null) {
			unitOfMeasureSkuId = _unitOfMeasureSkuIdSupplier.get();

			_unitOfMeasureSkuIdSupplier = null;
		}

		return unitOfMeasureSkuId;
	}

	public void setUnitOfMeasureSkuId(String unitOfMeasureSkuId) {
		this.unitOfMeasureSkuId = unitOfMeasureSkuId;

		_unitOfMeasureSkuIdSupplier = null;
	}

	@JsonIgnore
	public void setUnitOfMeasureSkuId(
		UnsafeSupplier<String, Exception> unitOfMeasureSkuIdUnsafeSupplier) {

		_unitOfMeasureSkuIdSupplier = () -> {
			try {
				return unitOfMeasureSkuIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String unitOfMeasureSkuId;

	@JsonIgnore
	private Supplier<String> _unitOfMeasureSkuIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "1234567890")
	public String getUnspsc() {
		if (_unspscSupplier != null) {
			unspsc = _unspscSupplier.get();

			_unspscSupplier = null;
		}

		return unspsc;
	}

	public void setUnspsc(String unspsc) {
		this.unspsc = unspsc;

		_unspscSupplier = null;
	}

	@JsonIgnore
	public void setUnspsc(
		UnsafeSupplier<String, Exception> unspscUnsafeSupplier) {

		_unspscSupplier = () -> {
			try {
				return unspscUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String unspsc;

	@JsonIgnore
	private Supplier<String> _unspscSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1.1")
	public Double getWeight() {
		if (_weightSupplier != null) {
			weight = _weightSupplier.get();

			_weightSupplier = null;
		}

		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;

		_weightSupplier = null;
	}

	@JsonIgnore
	public void setWeight(
		UnsafeSupplier<Double, Exception> weightUnsafeSupplier) {

		_weightSupplier = () -> {
			try {
				return weightUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Double weight;

	@JsonIgnore
	private Supplier<Double> _weightSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "20.2")
	public Double getWidth() {
		if (_widthSupplier != null) {
			width = _widthSupplier.get();

			_widthSupplier = null;
		}

		return width;
	}

	public void setWidth(Double width) {
		this.width = width;

		_widthSupplier = null;
	}

	@JsonIgnore
	public void setWidth(
		UnsafeSupplier<Double, Exception> widthUnsafeSupplier) {

		_widthSupplier = () -> {
			try {
				return widthUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Double width;

	@JsonIgnore
	private Supplier<Double> _widthSupplier;

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
		StringBundler sb = new StringBundler();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		BigDecimal cost = getCost();

		if (cost != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cost\": ");

			sb.append(cost);
		}

		com.liferay.portal.vulcan.custom.field.CustomField[] customFields =
			getCustomFields();

		if (customFields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < customFields.length; i++) {
				sb.append(customFields[i]);

				if ((i + 1) < customFields.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Double depth = getDepth();

		if (depth != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"depth\": ");

			sb.append(depth);
		}

		Boolean discontinued = getDiscontinued();

		if (discontinued != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discontinued\": ");

			sb.append(discontinued);
		}

		Date discontinuedDate = getDiscontinuedDate();

		if (discontinuedDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discontinuedDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(discontinuedDate));

			sb.append("\"");
		}

		Date displayDate = getDisplayDate();

		if (displayDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(displayDate));

			sb.append("\"");
		}

		Date expirationDate = getExpirationDate();

		if (expirationDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expirationDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(expirationDate));

			sb.append("\"");
		}

		String externalReferenceCode = getExternalReferenceCode();

		if (externalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(externalReferenceCode));

			sb.append("\"");
		}

		String gtin = getGtin();

		if (gtin != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"gtin\": ");

			sb.append("\"");

			sb.append(_escape(gtin));

			sb.append("\"");
		}

		Double height = getHeight();

		if (height != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"height\": ");

			sb.append(height);
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		Integer inventoryLevel = getInventoryLevel();

		if (inventoryLevel != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"inventoryLevel\": ");

			sb.append(inventoryLevel);
		}

		String manufacturerPartNumber = getManufacturerPartNumber();

		if (manufacturerPartNumber != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"manufacturerPartNumber\": ");

			sb.append("\"");

			sb.append(_escape(manufacturerPartNumber));

			sb.append("\"");
		}

		Boolean neverExpire = getNeverExpire();

		if (neverExpire != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"neverExpire\": ");

			sb.append(neverExpire);
		}

		BigDecimal price = getPrice();

		if (price != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"price\": ");

			sb.append(price);
		}

		Long productId = getProductId();

		if (productId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productId\": ");

			sb.append(productId);
		}

		Map<String, String> productName = getProductName();

		if (productName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productName\": ");

			sb.append(_toJSON(productName));
		}

		BigDecimal promoPrice = getPromoPrice();

		if (promoPrice != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"promoPrice\": ");

			sb.append(promoPrice);
		}

		Boolean published = getPublished();

		if (published != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"published\": ");

			sb.append(published);
		}

		Boolean purchasable = getPurchasable();

		if (purchasable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"purchasable\": ");

			sb.append(purchasable);
		}

		String replacementSkuExternalReferenceCode =
			getReplacementSkuExternalReferenceCode();

		if (replacementSkuExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"replacementSkuExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(replacementSkuExternalReferenceCode));

			sb.append("\"");
		}

		Long replacementSkuId = getReplacementSkuId();

		if (replacementSkuId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"replacementSkuId\": ");

			sb.append(replacementSkuId);
		}

		String sku = getSku();

		if (sku != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sku\": ");

			sb.append("\"");

			sb.append(_escape(sku));

			sb.append("\"");
		}

		SkuOption[] skuOptions = getSkuOptions();

		if (skuOptions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuOptions\": ");

			sb.append("[");

			for (int i = 0; i < skuOptions.length; i++) {
				sb.append(String.valueOf(skuOptions[i]));

				if ((i + 1) < skuOptions.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		SkuSubscriptionConfiguration skuSubscriptionConfiguration =
			getSkuSubscriptionConfiguration();

		if (skuSubscriptionConfiguration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuSubscriptionConfiguration\": ");

			sb.append(String.valueOf(skuSubscriptionConfiguration));
		}

		SkuUnitOfMeasure[] skuUnitOfMeasures = getSkuUnitOfMeasures();

		if (skuUnitOfMeasures != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuUnitOfMeasures\": ");

			sb.append("[");

			for (int i = 0; i < skuUnitOfMeasures.length; i++) {
				sb.append(String.valueOf(skuUnitOfMeasures[i]));

				if ((i + 1) < skuUnitOfMeasures.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		SkuVirtualSettings skuVirtualSettings = getSkuVirtualSettings();

		if (skuVirtualSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuVirtualSettings\": ");

			sb.append(String.valueOf(skuVirtualSettings));
		}

		String unitOfMeasureKey = getUnitOfMeasureKey();

		if (unitOfMeasureKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unitOfMeasureKey\": ");

			sb.append("\"");

			sb.append(_escape(unitOfMeasureKey));

			sb.append("\"");
		}

		Map<String, String> unitOfMeasureName = getUnitOfMeasureName();

		if (unitOfMeasureName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unitOfMeasureName\": ");

			sb.append(_toJSON(unitOfMeasureName));
		}

		String unitOfMeasureSkuId = getUnitOfMeasureSkuId();

		if (unitOfMeasureSkuId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unitOfMeasureSkuId\": ");

			sb.append("\"");

			sb.append(_escape(unitOfMeasureSkuId));

			sb.append("\"");
		}

		String unspsc = getUnspsc();

		if (unspsc != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unspsc\": ");

			sb.append("\"");

			sb.append(_escape(unspsc));

			sb.append("\"");
		}

		Double weight = getWeight();

		if (weight != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"weight\": ");

			sb.append(weight);
		}

		Double width = getWidth();

		if (width != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"width\": ");

			sb.append(width);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.catalog.dto.v1_0.Sku",
		name = "x-class-name"
	)
	public String xClassName;

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}