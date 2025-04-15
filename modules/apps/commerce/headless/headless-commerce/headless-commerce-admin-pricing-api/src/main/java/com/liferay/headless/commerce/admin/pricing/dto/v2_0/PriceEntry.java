/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.dto.v2_0;

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
import jakarta.validation.constraints.NotNull;

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
@GraphQLName("PriceEntry")
@io.swagger.v3.oas.annotations.media.Schema(
	requiredProperties = {"price", "priceListId", "skuId"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PriceEntry")
public class PriceEntry implements Serializable {

	public static PriceEntry toDTO(String json) {
		return ObjectMapperUtil.readValue(PriceEntry.class, json);
	}

	public static PriceEntry unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(PriceEntry.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, Map<String, String>> getActions() {
		if (_actionsSupplier != null) {
			actions = _actionsSupplier.get();

			_actionsSupplier = null;
		}

		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;

		_actionsSupplier = null;
	}

	@JsonIgnore
	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		_actionsSupplier = () -> {
			try {
				return actionsUnsafeSupplier.get();
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
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getActive() {
		if (_activeSupplier != null) {
			active = _activeSupplier.get();

			_activeSupplier = null;
		}

		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;

		_activeSupplier = null;
	}

	@JsonIgnore
	public void setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		_activeSupplier = () -> {
			try {
				return activeUnsafeSupplier.get();
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
	protected Boolean active;

	@JsonIgnore
	private Supplier<Boolean> _activeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getBulkPricing() {
		if (_bulkPricingSupplier != null) {
			bulkPricing = _bulkPricingSupplier.get();

			_bulkPricingSupplier = null;
		}

		return bulkPricing;
	}

	public void setBulkPricing(Boolean bulkPricing) {
		this.bulkPricing = bulkPricing;

		_bulkPricingSupplier = null;
	}

	@JsonIgnore
	public void setBulkPricing(
		UnsafeSupplier<Boolean, Exception> bulkPricingUnsafeSupplier) {

		_bulkPricingSupplier = () -> {
			try {
				return bulkPricingUnsafeSupplier.get();
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
	protected Boolean bulkPricing;

	@JsonIgnore
	private Supplier<Boolean> _bulkPricingSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, ?> getCustomFields() {
		if (_customFieldsSupplier != null) {
			customFields = _customFieldsSupplier.get();

			_customFieldsSupplier = null;
		}

		return customFields;
	}

	public void setCustomFields(Map<String, ?> customFields) {
		this.customFields = customFields;

		_customFieldsSupplier = null;
	}

	@JsonIgnore
	public void setCustomFields(
		UnsafeSupplier<Map<String, ?>, Exception> customFieldsUnsafeSupplier) {

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
	protected Map<String, ?> customFields;

	@JsonIgnore
	private Supplier<Map<String, ?>> _customFieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getDiscountDiscovery() {
		if (_discountDiscoverySupplier != null) {
			discountDiscovery = _discountDiscoverySupplier.get();

			_discountDiscoverySupplier = null;
		}

		return discountDiscovery;
	}

	public void setDiscountDiscovery(Boolean discountDiscovery) {
		this.discountDiscovery = discountDiscovery;

		_discountDiscoverySupplier = null;
	}

	@JsonIgnore
	public void setDiscountDiscovery(
		UnsafeSupplier<Boolean, Exception> discountDiscoveryUnsafeSupplier) {

		_discountDiscoverySupplier = () -> {
			try {
				return discountDiscoveryUnsafeSupplier.get();
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
	protected Boolean discountDiscovery;

	@JsonIgnore
	private Supplier<Boolean> _discountDiscoverySupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	@Valid
	public BigDecimal getDiscountLevel1() {
		if (_discountLevel1Supplier != null) {
			discountLevel1 = _discountLevel1Supplier.get();

			_discountLevel1Supplier = null;
		}

		return discountLevel1;
	}

	public void setDiscountLevel1(BigDecimal discountLevel1) {
		this.discountLevel1 = discountLevel1;

		_discountLevel1Supplier = null;
	}

	@JsonIgnore
	public void setDiscountLevel1(
		UnsafeSupplier<BigDecimal, Exception> discountLevel1UnsafeSupplier) {

		_discountLevel1Supplier = () -> {
			try {
				return discountLevel1UnsafeSupplier.get();
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
	protected BigDecimal discountLevel1;

	@JsonIgnore
	private Supplier<BigDecimal> _discountLevel1Supplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	@Valid
	public BigDecimal getDiscountLevel2() {
		if (_discountLevel2Supplier != null) {
			discountLevel2 = _discountLevel2Supplier.get();

			_discountLevel2Supplier = null;
		}

		return discountLevel2;
	}

	public void setDiscountLevel2(BigDecimal discountLevel2) {
		this.discountLevel2 = discountLevel2;

		_discountLevel2Supplier = null;
	}

	@JsonIgnore
	public void setDiscountLevel2(
		UnsafeSupplier<BigDecimal, Exception> discountLevel2UnsafeSupplier) {

		_discountLevel2Supplier = () -> {
			try {
				return discountLevel2UnsafeSupplier.get();
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
	protected BigDecimal discountLevel2;

	@JsonIgnore
	private Supplier<BigDecimal> _discountLevel2Supplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	@Valid
	public BigDecimal getDiscountLevel3() {
		if (_discountLevel3Supplier != null) {
			discountLevel3 = _discountLevel3Supplier.get();

			_discountLevel3Supplier = null;
		}

		return discountLevel3;
	}

	public void setDiscountLevel3(BigDecimal discountLevel3) {
		this.discountLevel3 = discountLevel3;

		_discountLevel3Supplier = null;
	}

	@JsonIgnore
	public void setDiscountLevel3(
		UnsafeSupplier<BigDecimal, Exception> discountLevel3UnsafeSupplier) {

		_discountLevel3Supplier = () -> {
			try {
				return discountLevel3UnsafeSupplier.get();
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
	protected BigDecimal discountLevel3;

	@JsonIgnore
	private Supplier<BigDecimal> _discountLevel3Supplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	@Valid
	public BigDecimal getDiscountLevel4() {
		if (_discountLevel4Supplier != null) {
			discountLevel4 = _discountLevel4Supplier.get();

			_discountLevel4Supplier = null;
		}

		return discountLevel4;
	}

	public void setDiscountLevel4(BigDecimal discountLevel4) {
		this.discountLevel4 = discountLevel4;

		_discountLevel4Supplier = null;
	}

	@JsonIgnore
	public void setDiscountLevel4(
		UnsafeSupplier<BigDecimal, Exception> discountLevel4UnsafeSupplier) {

		_discountLevel4Supplier = () -> {
			try {
				return discountLevel4UnsafeSupplier.get();
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
	protected BigDecimal discountLevel4;

	@JsonIgnore
	private Supplier<BigDecimal> _discountLevel4Supplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "10 | 10 | 10 | 10")
	public String getDiscountLevelsFormatted() {
		if (_discountLevelsFormattedSupplier != null) {
			discountLevelsFormatted = _discountLevelsFormattedSupplier.get();

			_discountLevelsFormattedSupplier = null;
		}

		return discountLevelsFormatted;
	}

	public void setDiscountLevelsFormatted(String discountLevelsFormatted) {
		this.discountLevelsFormatted = discountLevelsFormatted;

		_discountLevelsFormattedSupplier = null;
	}

	@JsonIgnore
	public void setDiscountLevelsFormatted(
		UnsafeSupplier<String, Exception>
			discountLevelsFormattedUnsafeSupplier) {

		_discountLevelsFormattedSupplier = () -> {
			try {
				return discountLevelsFormattedUnsafeSupplier.get();
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
	protected String discountLevelsFormatted;

	@JsonIgnore
	private Supplier<String> _discountLevelsFormattedSupplier;

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

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getHasTierPrice() {
		if (_hasTierPriceSupplier != null) {
			hasTierPrice = _hasTierPriceSupplier.get();

			_hasTierPriceSupplier = null;
		}

		return hasTierPrice;
	}

	public void setHasTierPrice(Boolean hasTierPrice) {
		this.hasTierPrice = hasTierPrice;

		_hasTierPriceSupplier = null;
	}

	@JsonIgnore
	public void setHasTierPrice(
		UnsafeSupplier<Boolean, Exception> hasTierPriceUnsafeSupplier) {

		_hasTierPriceSupplier = () -> {
			try {
				return hasTierPriceUnsafeSupplier.get();
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
	protected Boolean hasTierPrice;

	@JsonIgnore
	private Supplier<Boolean> _hasTierPriceSupplier;

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

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getPrice() {
		if (_priceSupplier != null) {
			price = _priceSupplier.get();

			_priceSupplier = null;
		}

		return price;
	}

	public void setPrice(Double price) {
		this.price = price;

		_priceSupplier = null;
	}

	@JsonIgnore
	public void setPrice(
		UnsafeSupplier<Double, Exception> priceUnsafeSupplier) {

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
	@NotNull
	protected Double price;

	@JsonIgnore
	private Supplier<Double> _priceSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	public Long getPriceEntryId() {
		if (_priceEntryIdSupplier != null) {
			priceEntryId = _priceEntryIdSupplier.get();

			_priceEntryIdSupplier = null;
		}

		return priceEntryId;
	}

	public void setPriceEntryId(Long priceEntryId) {
		this.priceEntryId = priceEntryId;

		_priceEntryIdSupplier = null;
	}

	@JsonIgnore
	public void setPriceEntryId(
		UnsafeSupplier<Long, Exception> priceEntryIdUnsafeSupplier) {

		_priceEntryIdSupplier = () -> {
			try {
				return priceEntryIdUnsafeSupplier.get();
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
	protected Long priceEntryId;

	@JsonIgnore
	private Supplier<Long> _priceEntryIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getPriceFormatted() {
		if (_priceFormattedSupplier != null) {
			priceFormatted = _priceFormattedSupplier.get();

			_priceFormattedSupplier = null;
		}

		return priceFormatted;
	}

	public void setPriceFormatted(String priceFormatted) {
		this.priceFormatted = priceFormatted;

		_priceFormattedSupplier = null;
	}

	@JsonIgnore
	public void setPriceFormatted(
		UnsafeSupplier<String, Exception> priceFormattedUnsafeSupplier) {

		_priceFormattedSupplier = () -> {
			try {
				return priceFormattedUnsafeSupplier.get();
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
	protected String priceFormatted;

	@JsonIgnore
	private Supplier<String> _priceFormattedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "PLAB-34098-789-N")
	public String getPriceListExternalReferenceCode() {
		if (_priceListExternalReferenceCodeSupplier != null) {
			priceListExternalReferenceCode =
				_priceListExternalReferenceCodeSupplier.get();

			_priceListExternalReferenceCodeSupplier = null;
		}

		return priceListExternalReferenceCode;
	}

	public void setPriceListExternalReferenceCode(
		String priceListExternalReferenceCode) {

		this.priceListExternalReferenceCode = priceListExternalReferenceCode;

		_priceListExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setPriceListExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			priceListExternalReferenceCodeUnsafeSupplier) {

		_priceListExternalReferenceCodeSupplier = () -> {
			try {
				return priceListExternalReferenceCodeUnsafeSupplier.get();
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
	protected String priceListExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _priceListExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "20078")
	public Long getPriceListId() {
		if (_priceListIdSupplier != null) {
			priceListId = _priceListIdSupplier.get();

			_priceListIdSupplier = null;
		}

		return priceListId;
	}

	public void setPriceListId(Long priceListId) {
		this.priceListId = priceListId;

		_priceListIdSupplier = null;
	}

	@JsonIgnore
	public void setPriceListId(
		UnsafeSupplier<Long, Exception> priceListIdUnsafeSupplier) {

		_priceListIdSupplier = () -> {
			try {
				return priceListIdUnsafeSupplier.get();
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
	@NotNull
	protected Long priceListId;

	@JsonIgnore
	private Supplier<Long> _priceListIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getPriceOnApplication() {
		if (_priceOnApplicationSupplier != null) {
			priceOnApplication = _priceOnApplicationSupplier.get();

			_priceOnApplicationSupplier = null;
		}

		return priceOnApplication;
	}

	public void setPriceOnApplication(Boolean priceOnApplication) {
		this.priceOnApplication = priceOnApplication;

		_priceOnApplicationSupplier = null;
	}

	@JsonIgnore
	public void setPriceOnApplication(
		UnsafeSupplier<Boolean, Exception> priceOnApplicationUnsafeSupplier) {

		_priceOnApplicationSupplier = () -> {
			try {
				return priceOnApplicationUnsafeSupplier.get();
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
	protected Boolean priceOnApplication;

	@JsonIgnore
	private Supplier<Boolean> _priceOnApplicationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Product getProduct() {
		if (_productSupplier != null) {
			product = _productSupplier.get();

			_productSupplier = null;
		}

		return product;
	}

	public void setProduct(Product product) {
		this.product = product;

		_productSupplier = null;
	}

	@JsonIgnore
	public void setProduct(
		UnsafeSupplier<Product, Exception> productUnsafeSupplier) {

		_productSupplier = () -> {
			try {
				return productUnsafeSupplier.get();
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
	protected Product product;

	@JsonIgnore
	private Supplier<Product> _productSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "10.1")
	@Valid
	public BigDecimal getQuantity() {
		if (_quantitySupplier != null) {
			quantity = _quantitySupplier.get();

			_quantitySupplier = null;
		}

		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;

		_quantitySupplier = null;
	}

	@JsonIgnore
	public void setQuantity(
		UnsafeSupplier<BigDecimal, Exception> quantityUnsafeSupplier) {

		_quantitySupplier = () -> {
			try {
				return quantityUnsafeSupplier.get();
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
	protected BigDecimal quantity;

	@JsonIgnore
	private Supplier<BigDecimal> _quantitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Sku getSku() {
		if (_skuSupplier != null) {
			sku = _skuSupplier.get();

			_skuSupplier = null;
		}

		return sku;
	}

	public void setSku(Sku sku) {
		this.sku = sku;

		_skuSupplier = null;
	}

	@JsonIgnore
	public void setSku(UnsafeSupplier<Sku, Exception> skuUnsafeSupplier) {
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Sku sku;

	@JsonIgnore
	private Supplier<Sku> _skuSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "CAB-34098-789-N")
	public String getSkuExternalReferenceCode() {
		if (_skuExternalReferenceCodeSupplier != null) {
			skuExternalReferenceCode = _skuExternalReferenceCodeSupplier.get();

			_skuExternalReferenceCodeSupplier = null;
		}

		return skuExternalReferenceCode;
	}

	public void setSkuExternalReferenceCode(String skuExternalReferenceCode) {
		this.skuExternalReferenceCode = skuExternalReferenceCode;

		_skuExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setSkuExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			skuExternalReferenceCodeUnsafeSupplier) {

		_skuExternalReferenceCodeSupplier = () -> {
			try {
				return skuExternalReferenceCodeUnsafeSupplier.get();
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
	protected String skuExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _skuExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	public Long getSkuId() {
		if (_skuIdSupplier != null) {
			skuId = _skuIdSupplier.get();

			_skuIdSupplier = null;
		}

		return skuId;
	}

	public void setSkuId(Long skuId) {
		this.skuId = skuId;

		_skuIdSupplier = null;
	}

	@JsonIgnore
	public void setSkuId(UnsafeSupplier<Long, Exception> skuIdUnsafeSupplier) {
		_skuIdSupplier = () -> {
			try {
				return skuIdUnsafeSupplier.get();
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
	@NotNull
	protected Long skuId;

	@JsonIgnore
	private Supplier<Long> _skuIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public TierPrice[] getTierPrices() {
		if (_tierPricesSupplier != null) {
			tierPrices = _tierPricesSupplier.get();

			_tierPricesSupplier = null;
		}

		return tierPrices;
	}

	public void setTierPrices(TierPrice[] tierPrices) {
		this.tierPrices = tierPrices;

		_tierPricesSupplier = null;
	}

	@JsonIgnore
	public void setTierPrices(
		UnsafeSupplier<TierPrice[], Exception> tierPricesUnsafeSupplier) {

		_tierPricesSupplier = () -> {
			try {
				return tierPricesUnsafeSupplier.get();
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
	protected TierPrice[] tierPrices;

	@JsonIgnore
	private Supplier<TierPrice[]> _tierPricesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "m")
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String unitOfMeasureKey;

	@JsonIgnore
	private Supplier<String> _unitOfMeasureKeySupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PriceEntry)) {
			return false;
		}

		PriceEntry priceEntry = (PriceEntry)object;

		return Objects.equals(toString(), priceEntry.toString());
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

		Map<String, Map<String, String>> actions = getActions();

		if (actions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(actions));
		}

		Boolean active = getActive();

		if (active != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(active);
		}

		Boolean bulkPricing = getBulkPricing();

		if (bulkPricing != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"bulkPricing\": ");

			sb.append(bulkPricing);
		}

		Map<String, ?> customFields = getCustomFields();

		if (customFields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append(_toJSON(customFields));
		}

		Boolean discountDiscovery = getDiscountDiscovery();

		if (discountDiscovery != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountDiscovery\": ");

			sb.append(discountDiscovery);
		}

		BigDecimal discountLevel1 = getDiscountLevel1();

		if (discountLevel1 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountLevel1\": ");

			sb.append(discountLevel1);
		}

		BigDecimal discountLevel2 = getDiscountLevel2();

		if (discountLevel2 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountLevel2\": ");

			sb.append(discountLevel2);
		}

		BigDecimal discountLevel3 = getDiscountLevel3();

		if (discountLevel3 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountLevel3\": ");

			sb.append(discountLevel3);
		}

		BigDecimal discountLevel4 = getDiscountLevel4();

		if (discountLevel4 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountLevel4\": ");

			sb.append(discountLevel4);
		}

		String discountLevelsFormatted = getDiscountLevelsFormatted();

		if (discountLevelsFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountLevelsFormatted\": ");

			sb.append("\"");

			sb.append(_escape(discountLevelsFormatted));

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

		Boolean hasTierPrice = getHasTierPrice();

		if (hasTierPrice != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hasTierPrice\": ");

			sb.append(hasTierPrice);
		}

		Boolean neverExpire = getNeverExpire();

		if (neverExpire != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"neverExpire\": ");

			sb.append(neverExpire);
		}

		Double price = getPrice();

		if (price != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"price\": ");

			sb.append(price);
		}

		Long priceEntryId = getPriceEntryId();

		if (priceEntryId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceEntryId\": ");

			sb.append(priceEntryId);
		}

		String priceFormatted = getPriceFormatted();

		if (priceFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceFormatted\": ");

			sb.append("\"");

			sb.append(_escape(priceFormatted));

			sb.append("\"");
		}

		String priceListExternalReferenceCode =
			getPriceListExternalReferenceCode();

		if (priceListExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceListExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(priceListExternalReferenceCode));

			sb.append("\"");
		}

		Long priceListId = getPriceListId();

		if (priceListId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceListId\": ");

			sb.append(priceListId);
		}

		Boolean priceOnApplication = getPriceOnApplication();

		if (priceOnApplication != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceOnApplication\": ");

			sb.append(priceOnApplication);
		}

		Product product = getProduct();

		if (product != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"product\": ");

			sb.append(String.valueOf(product));
		}

		BigDecimal quantity = getQuantity();

		if (quantity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"quantity\": ");

			sb.append(quantity);
		}

		Sku sku = getSku();

		if (sku != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sku\": ");

			sb.append(String.valueOf(sku));
		}

		String skuExternalReferenceCode = getSkuExternalReferenceCode();

		if (skuExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(skuExternalReferenceCode));

			sb.append("\"");
		}

		Long skuId = getSkuId();

		if (skuId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuId\": ");

			sb.append(skuId);
		}

		TierPrice[] tierPrices = getTierPrices();

		if (tierPrices != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tierPrices\": ");

			sb.append("[");

			for (int i = 0; i < tierPrices.length; i++) {
				sb.append(String.valueOf(tierPrices[i]));

				if ((i + 1) < tierPrices.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceEntry",
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