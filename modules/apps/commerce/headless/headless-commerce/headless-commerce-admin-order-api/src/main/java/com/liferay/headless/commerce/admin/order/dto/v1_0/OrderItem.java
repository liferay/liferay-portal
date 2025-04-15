/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.dto.v1_0;

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
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
@GraphQLName("OrderItem")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "OrderItem")
public class OrderItem implements Serializable {

	public static OrderItem toDTO(String json) {
		return ObjectMapperUtil.readValue(OrderItem.class, json);
	}

	public static OrderItem unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(OrderItem.class, json);
	}

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "32144")
	public Long getBookedQuantityId() {
		if (_bookedQuantityIdSupplier != null) {
			bookedQuantityId = _bookedQuantityIdSupplier.get();

			_bookedQuantityIdSupplier = null;
		}

		return bookedQuantityId;
	}

	public void setBookedQuantityId(Long bookedQuantityId) {
		this.bookedQuantityId = bookedQuantityId;

		_bookedQuantityIdSupplier = null;
	}

	@JsonIgnore
	public void setBookedQuantityId(
		UnsafeSupplier<Long, Exception> bookedQuantityIdUnsafeSupplier) {

		_bookedQuantityIdSupplier = () -> {
			try {
				return bookedQuantityIdUnsafeSupplier.get();
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
	protected Long bookedQuantityId;

	@JsonIgnore
	private Supplier<Long> _bookedQuantityIdSupplier;

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
	@io.swagger.v3.oas.annotations.media.Schema(example = "10.1")
	@Valid
	public BigDecimal getDecimalQuantity() {
		if (_decimalQuantitySupplier != null) {
			decimalQuantity = _decimalQuantitySupplier.get();

			_decimalQuantitySupplier = null;
		}

		return decimalQuantity;
	}

	public void setDecimalQuantity(BigDecimal decimalQuantity) {
		this.decimalQuantity = decimalQuantity;

		_decimalQuantitySupplier = null;
	}

	@JsonIgnore
	public void setDecimalQuantity(
		UnsafeSupplier<BigDecimal, Exception> decimalQuantityUnsafeSupplier) {

		_decimalQuantitySupplier = () -> {
			try {
				return decimalQuantityUnsafeSupplier.get();
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
	protected BigDecimal decimalQuantity;

	@JsonIgnore
	private Supplier<BigDecimal> _decimalQuantitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		deprecated = true, example = "separate package"
	)
	public String getDeliveryGroup() {
		if (_deliveryGroupSupplier != null) {
			deliveryGroup = _deliveryGroupSupplier.get();

			_deliveryGroupSupplier = null;
		}

		return deliveryGroup;
	}

	public void setDeliveryGroup(String deliveryGroup) {
		this.deliveryGroup = deliveryGroup;

		_deliveryGroupSupplier = null;
	}

	@JsonIgnore
	public void setDeliveryGroup(
		UnsafeSupplier<String, Exception> deliveryGroupUnsafeSupplier) {

		_deliveryGroupSupplier = () -> {
			try {
				return deliveryGroupUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@Deprecated
	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String deliveryGroup;

	@JsonIgnore
	private Supplier<String> _deliveryGroupSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "separate package")
	public String getDeliveryGroupName() {
		if (_deliveryGroupNameSupplier != null) {
			deliveryGroupName = _deliveryGroupNameSupplier.get();

			_deliveryGroupNameSupplier = null;
		}

		return deliveryGroupName;
	}

	public void setDeliveryGroupName(String deliveryGroupName) {
		this.deliveryGroupName = deliveryGroupName;

		_deliveryGroupNameSupplier = null;
	}

	@JsonIgnore
	public void setDeliveryGroupName(
		UnsafeSupplier<String, Exception> deliveryGroupNameUnsafeSupplier) {

		_deliveryGroupNameSupplier = () -> {
			try {
				return deliveryGroupNameUnsafeSupplier.get();
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
	protected String deliveryGroupName;

	@JsonIgnore
	private Supplier<String> _deliveryGroupNameSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "2")
	@Valid
	public BigDecimal getDiscountAmount() {
		if (_discountAmountSupplier != null) {
			discountAmount = _discountAmountSupplier.get();

			_discountAmountSupplier = null;
		}

		return discountAmount;
	}

	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;

		_discountAmountSupplier = null;
	}

	@JsonIgnore
	public void setDiscountAmount(
		UnsafeSupplier<BigDecimal, Exception> discountAmountUnsafeSupplier) {

		_discountAmountSupplier = () -> {
			try {
				return discountAmountUnsafeSupplier.get();
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
	protected BigDecimal discountAmount;

	@JsonIgnore
	private Supplier<BigDecimal> _discountAmountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getDiscountManuallyAdjusted() {
		if (_discountManuallyAdjustedSupplier != null) {
			discountManuallyAdjusted = _discountManuallyAdjustedSupplier.get();

			_discountManuallyAdjustedSupplier = null;
		}

		return discountManuallyAdjusted;
	}

	public void setDiscountManuallyAdjusted(Boolean discountManuallyAdjusted) {
		this.discountManuallyAdjusted = discountManuallyAdjusted;

		_discountManuallyAdjustedSupplier = null;
	}

	@JsonIgnore
	public void setDiscountManuallyAdjusted(
		UnsafeSupplier<Boolean, Exception>
			discountManuallyAdjustedUnsafeSupplier) {

		_discountManuallyAdjustedSupplier = () -> {
			try {
				return discountManuallyAdjustedUnsafeSupplier.get();
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
	protected Boolean discountManuallyAdjusted;

	@JsonIgnore
	private Supplier<Boolean> _discountManuallyAdjustedSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "20")
	@Valid
	public BigDecimal getDiscountPercentageLevel1() {
		if (_discountPercentageLevel1Supplier != null) {
			discountPercentageLevel1 = _discountPercentageLevel1Supplier.get();

			_discountPercentageLevel1Supplier = null;
		}

		return discountPercentageLevel1;
	}

	public void setDiscountPercentageLevel1(
		BigDecimal discountPercentageLevel1) {

		this.discountPercentageLevel1 = discountPercentageLevel1;

		_discountPercentageLevel1Supplier = null;
	}

	@JsonIgnore
	public void setDiscountPercentageLevel1(
		UnsafeSupplier<BigDecimal, Exception>
			discountPercentageLevel1UnsafeSupplier) {

		_discountPercentageLevel1Supplier = () -> {
			try {
				return discountPercentageLevel1UnsafeSupplier.get();
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
	protected BigDecimal discountPercentageLevel1;

	@JsonIgnore
	private Supplier<BigDecimal> _discountPercentageLevel1Supplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "20")
	@Valid
	public BigDecimal getDiscountPercentageLevel1WithTaxAmount() {
		if (_discountPercentageLevel1WithTaxAmountSupplier != null) {
			discountPercentageLevel1WithTaxAmount =
				_discountPercentageLevel1WithTaxAmountSupplier.get();

			_discountPercentageLevel1WithTaxAmountSupplier = null;
		}

		return discountPercentageLevel1WithTaxAmount;
	}

	public void setDiscountPercentageLevel1WithTaxAmount(
		BigDecimal discountPercentageLevel1WithTaxAmount) {

		this.discountPercentageLevel1WithTaxAmount =
			discountPercentageLevel1WithTaxAmount;

		_discountPercentageLevel1WithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setDiscountPercentageLevel1WithTaxAmount(
		UnsafeSupplier<BigDecimal, Exception>
			discountPercentageLevel1WithTaxAmountUnsafeSupplier) {

		_discountPercentageLevel1WithTaxAmountSupplier = () -> {
			try {
				return discountPercentageLevel1WithTaxAmountUnsafeSupplier.
					get();
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
	protected BigDecimal discountPercentageLevel1WithTaxAmount;

	@JsonIgnore
	private Supplier<BigDecimal> _discountPercentageLevel1WithTaxAmountSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	@Valid
	public BigDecimal getDiscountPercentageLevel2() {
		if (_discountPercentageLevel2Supplier != null) {
			discountPercentageLevel2 = _discountPercentageLevel2Supplier.get();

			_discountPercentageLevel2Supplier = null;
		}

		return discountPercentageLevel2;
	}

	public void setDiscountPercentageLevel2(
		BigDecimal discountPercentageLevel2) {

		this.discountPercentageLevel2 = discountPercentageLevel2;

		_discountPercentageLevel2Supplier = null;
	}

	@JsonIgnore
	public void setDiscountPercentageLevel2(
		UnsafeSupplier<BigDecimal, Exception>
			discountPercentageLevel2UnsafeSupplier) {

		_discountPercentageLevel2Supplier = () -> {
			try {
				return discountPercentageLevel2UnsafeSupplier.get();
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
	protected BigDecimal discountPercentageLevel2;

	@JsonIgnore
	private Supplier<BigDecimal> _discountPercentageLevel2Supplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	@Valid
	public BigDecimal getDiscountPercentageLevel2WithTaxAmount() {
		if (_discountPercentageLevel2WithTaxAmountSupplier != null) {
			discountPercentageLevel2WithTaxAmount =
				_discountPercentageLevel2WithTaxAmountSupplier.get();

			_discountPercentageLevel2WithTaxAmountSupplier = null;
		}

		return discountPercentageLevel2WithTaxAmount;
	}

	public void setDiscountPercentageLevel2WithTaxAmount(
		BigDecimal discountPercentageLevel2WithTaxAmount) {

		this.discountPercentageLevel2WithTaxAmount =
			discountPercentageLevel2WithTaxAmount;

		_discountPercentageLevel2WithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setDiscountPercentageLevel2WithTaxAmount(
		UnsafeSupplier<BigDecimal, Exception>
			discountPercentageLevel2WithTaxAmountUnsafeSupplier) {

		_discountPercentageLevel2WithTaxAmountSupplier = () -> {
			try {
				return discountPercentageLevel2WithTaxAmountUnsafeSupplier.
					get();
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
	protected BigDecimal discountPercentageLevel2WithTaxAmount;

	@JsonIgnore
	private Supplier<BigDecimal> _discountPercentageLevel2WithTaxAmountSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	@Valid
	public BigDecimal getDiscountPercentageLevel3() {
		if (_discountPercentageLevel3Supplier != null) {
			discountPercentageLevel3 = _discountPercentageLevel3Supplier.get();

			_discountPercentageLevel3Supplier = null;
		}

		return discountPercentageLevel3;
	}

	public void setDiscountPercentageLevel3(
		BigDecimal discountPercentageLevel3) {

		this.discountPercentageLevel3 = discountPercentageLevel3;

		_discountPercentageLevel3Supplier = null;
	}

	@JsonIgnore
	public void setDiscountPercentageLevel3(
		UnsafeSupplier<BigDecimal, Exception>
			discountPercentageLevel3UnsafeSupplier) {

		_discountPercentageLevel3Supplier = () -> {
			try {
				return discountPercentageLevel3UnsafeSupplier.get();
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
	protected BigDecimal discountPercentageLevel3;

	@JsonIgnore
	private Supplier<BigDecimal> _discountPercentageLevel3Supplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	@Valid
	public BigDecimal getDiscountPercentageLevel3WithTaxAmount() {
		if (_discountPercentageLevel3WithTaxAmountSupplier != null) {
			discountPercentageLevel3WithTaxAmount =
				_discountPercentageLevel3WithTaxAmountSupplier.get();

			_discountPercentageLevel3WithTaxAmountSupplier = null;
		}

		return discountPercentageLevel3WithTaxAmount;
	}

	public void setDiscountPercentageLevel3WithTaxAmount(
		BigDecimal discountPercentageLevel3WithTaxAmount) {

		this.discountPercentageLevel3WithTaxAmount =
			discountPercentageLevel3WithTaxAmount;

		_discountPercentageLevel3WithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setDiscountPercentageLevel3WithTaxAmount(
		UnsafeSupplier<BigDecimal, Exception>
			discountPercentageLevel3WithTaxAmountUnsafeSupplier) {

		_discountPercentageLevel3WithTaxAmountSupplier = () -> {
			try {
				return discountPercentageLevel3WithTaxAmountUnsafeSupplier.
					get();
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
	protected BigDecimal discountPercentageLevel3WithTaxAmount;

	@JsonIgnore
	private Supplier<BigDecimal> _discountPercentageLevel3WithTaxAmountSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	@Valid
	public BigDecimal getDiscountPercentageLevel4() {
		if (_discountPercentageLevel4Supplier != null) {
			discountPercentageLevel4 = _discountPercentageLevel4Supplier.get();

			_discountPercentageLevel4Supplier = null;
		}

		return discountPercentageLevel4;
	}

	public void setDiscountPercentageLevel4(
		BigDecimal discountPercentageLevel4) {

		this.discountPercentageLevel4 = discountPercentageLevel4;

		_discountPercentageLevel4Supplier = null;
	}

	@JsonIgnore
	public void setDiscountPercentageLevel4(
		UnsafeSupplier<BigDecimal, Exception>
			discountPercentageLevel4UnsafeSupplier) {

		_discountPercentageLevel4Supplier = () -> {
			try {
				return discountPercentageLevel4UnsafeSupplier.get();
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
	protected BigDecimal discountPercentageLevel4;

	@JsonIgnore
	private Supplier<BigDecimal> _discountPercentageLevel4Supplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	@Valid
	public BigDecimal getDiscountPercentageLevel4WithTaxAmount() {
		if (_discountPercentageLevel4WithTaxAmountSupplier != null) {
			discountPercentageLevel4WithTaxAmount =
				_discountPercentageLevel4WithTaxAmountSupplier.get();

			_discountPercentageLevel4WithTaxAmountSupplier = null;
		}

		return discountPercentageLevel4WithTaxAmount;
	}

	public void setDiscountPercentageLevel4WithTaxAmount(
		BigDecimal discountPercentageLevel4WithTaxAmount) {

		this.discountPercentageLevel4WithTaxAmount =
			discountPercentageLevel4WithTaxAmount;

		_discountPercentageLevel4WithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setDiscountPercentageLevel4WithTaxAmount(
		UnsafeSupplier<BigDecimal, Exception>
			discountPercentageLevel4WithTaxAmountUnsafeSupplier) {

		_discountPercentageLevel4WithTaxAmountSupplier = () -> {
			try {
				return discountPercentageLevel4WithTaxAmountUnsafeSupplier.
					get();
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
	protected BigDecimal discountPercentageLevel4WithTaxAmount;

	@JsonIgnore
	private Supplier<BigDecimal> _discountPercentageLevel4WithTaxAmountSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "2")
	@Valid
	public BigDecimal getDiscountWithTaxAmount() {
		if (_discountWithTaxAmountSupplier != null) {
			discountWithTaxAmount = _discountWithTaxAmountSupplier.get();

			_discountWithTaxAmountSupplier = null;
		}

		return discountWithTaxAmount;
	}

	public void setDiscountWithTaxAmount(BigDecimal discountWithTaxAmount) {
		this.discountWithTaxAmount = discountWithTaxAmount;

		_discountWithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setDiscountWithTaxAmount(
		UnsafeSupplier<BigDecimal, Exception>
			discountWithTaxAmountUnsafeSupplier) {

		_discountWithTaxAmountSupplier = () -> {
			try {
				return discountWithTaxAmountUnsafeSupplier.get();
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
	protected BigDecimal discountWithTaxAmount;

	@JsonIgnore
	private Supplier<BigDecimal> _discountWithTaxAmountSupplier;

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

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "200")
	@Valid
	public BigDecimal getFinalPrice() {
		if (_finalPriceSupplier != null) {
			finalPrice = _finalPriceSupplier.get();

			_finalPriceSupplier = null;
		}

		return finalPrice;
	}

	public void setFinalPrice(BigDecimal finalPrice) {
		this.finalPrice = finalPrice;

		_finalPriceSupplier = null;
	}

	@JsonIgnore
	public void setFinalPrice(
		UnsafeSupplier<BigDecimal, Exception> finalPriceUnsafeSupplier) {

		_finalPriceSupplier = () -> {
			try {
				return finalPriceUnsafeSupplier.get();
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
	protected BigDecimal finalPrice;

	@JsonIgnore
	private Supplier<BigDecimal> _finalPriceSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "200")
	@Valid
	public BigDecimal getFinalPriceWithTaxAmount() {
		if (_finalPriceWithTaxAmountSupplier != null) {
			finalPriceWithTaxAmount = _finalPriceWithTaxAmountSupplier.get();

			_finalPriceWithTaxAmountSupplier = null;
		}

		return finalPriceWithTaxAmount;
	}

	public void setFinalPriceWithTaxAmount(BigDecimal finalPriceWithTaxAmount) {
		this.finalPriceWithTaxAmount = finalPriceWithTaxAmount;

		_finalPriceWithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setFinalPriceWithTaxAmount(
		UnsafeSupplier<BigDecimal, Exception>
			finalPriceWithTaxAmountUnsafeSupplier) {

		_finalPriceWithTaxAmountSupplier = () -> {
			try {
				return finalPriceWithTaxAmountUnsafeSupplier.get();
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
	protected BigDecimal finalPriceWithTaxAmount;

	@JsonIgnore
	private Supplier<BigDecimal> _finalPriceWithTaxAmountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getFormattedQuantity() {
		if (_formattedQuantitySupplier != null) {
			formattedQuantity = _formattedQuantitySupplier.get();

			_formattedQuantitySupplier = null;
		}

		return formattedQuantity;
	}

	public void setFormattedQuantity(String formattedQuantity) {
		this.formattedQuantity = formattedQuantity;

		_formattedQuantitySupplier = null;
	}

	@JsonIgnore
	public void setFormattedQuantity(
		UnsafeSupplier<String, Exception> formattedQuantityUnsafeSupplier) {

		_formattedQuantitySupplier = () -> {
			try {
				return formattedQuantityUnsafeSupplier.get();
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
	protected String formattedQuantity;

	@JsonIgnore
	private Supplier<String> _formattedQuantitySupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "{en_US=Hand Saw, hr_HR=Product Name HR, hu_HU=Product Name HU}"
	)
	@Valid
	public Map<String, String> getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(Map<String, String> name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(
		UnsafeSupplier<Map<String, String>, Exception> nameUnsafeSupplier) {

		_nameSupplier = () -> {
			try {
				return nameUnsafeSupplier.get();
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
	protected Map<String, String> name;

	@JsonIgnore
	private Supplier<Map<String, String>> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getOptions() {
		if (_optionsSupplier != null) {
			options = _optionsSupplier.get();

			_optionsSupplier = null;
		}

		return options;
	}

	public void setOptions(String options) {
		this.options = options;

		_optionsSupplier = null;
	}

	@JsonIgnore
	public void setOptions(
		UnsafeSupplier<String, Exception> optionsUnsafeSupplier) {

		_optionsSupplier = () -> {
			try {
				return optionsUnsafeSupplier.get();
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
	protected String options;

	@JsonIgnore
	private Supplier<String> _optionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "CAB-34098-789-N")
	public String getOrderExternalReferenceCode() {
		if (_orderExternalReferenceCodeSupplier != null) {
			orderExternalReferenceCode =
				_orderExternalReferenceCodeSupplier.get();

			_orderExternalReferenceCodeSupplier = null;
		}

		return orderExternalReferenceCode;
	}

	public void setOrderExternalReferenceCode(
		String orderExternalReferenceCode) {

		this.orderExternalReferenceCode = orderExternalReferenceCode;

		_orderExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setOrderExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			orderExternalReferenceCodeUnsafeSupplier) {

		_orderExternalReferenceCodeSupplier = () -> {
			try {
				return orderExternalReferenceCodeUnsafeSupplier.get();
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
	protected String orderExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _orderExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30128")
	public Long getOrderId() {
		if (_orderIdSupplier != null) {
			orderId = _orderIdSupplier.get();

			_orderIdSupplier = null;
		}

		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;

		_orderIdSupplier = null;
	}

	@JsonIgnore
	public void setOrderId(
		UnsafeSupplier<Long, Exception> orderIdUnsafeSupplier) {

		_orderIdSupplier = () -> {
			try {
				return orderIdUnsafeSupplier.get();
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
	protected Long orderId;

	@JsonIgnore
	private Supplier<Long> _orderIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getPriceManuallyAdjusted() {
		if (_priceManuallyAdjustedSupplier != null) {
			priceManuallyAdjusted = _priceManuallyAdjustedSupplier.get();

			_priceManuallyAdjustedSupplier = null;
		}

		return priceManuallyAdjusted;
	}

	public void setPriceManuallyAdjusted(Boolean priceManuallyAdjusted) {
		this.priceManuallyAdjusted = priceManuallyAdjusted;

		_priceManuallyAdjustedSupplier = null;
	}

	@JsonIgnore
	public void setPriceManuallyAdjusted(
		UnsafeSupplier<Boolean, Exception>
			priceManuallyAdjustedUnsafeSupplier) {

		_priceManuallyAdjustedSupplier = () -> {
			try {
				return priceManuallyAdjustedUnsafeSupplier.get();
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
	protected Boolean priceManuallyAdjusted;

	@JsonIgnore
	private Supplier<Boolean> _priceManuallyAdjustedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "Order item printed note"
	)
	public String getPrintedNote() {
		if (_printedNoteSupplier != null) {
			printedNote = _printedNoteSupplier.get();

			_printedNoteSupplier = null;
		}

		return printedNote;
	}

	public void setPrintedNote(String printedNote) {
		this.printedNote = printedNote;

		_printedNoteSupplier = null;
	}

	@JsonIgnore
	public void setPrintedNote(
		UnsafeSupplier<String, Exception> printedNoteUnsafeSupplier) {

		_printedNoteSupplier = () -> {
			try {
				return printedNoteUnsafeSupplier.get();
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
	protected String printedNote;

	@JsonIgnore
	private Supplier<String> _printedNoteSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "101")
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

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "101")
	@Valid
	public BigDecimal getPromoPriceWithTaxAmount() {
		if (_promoPriceWithTaxAmountSupplier != null) {
			promoPriceWithTaxAmount = _promoPriceWithTaxAmountSupplier.get();

			_promoPriceWithTaxAmountSupplier = null;
		}

		return promoPriceWithTaxAmount;
	}

	public void setPromoPriceWithTaxAmount(BigDecimal promoPriceWithTaxAmount) {
		this.promoPriceWithTaxAmount = promoPriceWithTaxAmount;

		_promoPriceWithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setPromoPriceWithTaxAmount(
		UnsafeSupplier<BigDecimal, Exception>
			promoPriceWithTaxAmountUnsafeSupplier) {

		_promoPriceWithTaxAmountSupplier = () -> {
			try {
				return promoPriceWithTaxAmountUnsafeSupplier.get();
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
	protected BigDecimal promoPriceWithTaxAmount;

	@JsonIgnore
	private Supplier<BigDecimal> _promoPriceWithTaxAmountSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "2.1")
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

	@io.swagger.v3.oas.annotations.media.Schema(example = "1234123124")
	public String getReplacedSku() {
		if (_replacedSkuSupplier != null) {
			replacedSku = _replacedSkuSupplier.get();

			_replacedSkuSupplier = null;
		}

		return replacedSku;
	}

	public void setReplacedSku(String replacedSku) {
		this.replacedSku = replacedSku;

		_replacedSkuSupplier = null;
	}

	@JsonIgnore
	public void setReplacedSku(
		UnsafeSupplier<String, Exception> replacedSkuUnsafeSupplier) {

		_replacedSkuSupplier = () -> {
			try {
				return replacedSkuUnsafeSupplier.get();
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
	protected String replacedSku;

	@JsonIgnore
	private Supplier<String> _replacedSkuSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "AB-34098-789-N")
	public String getReplacedSkuExternalReferenceCode() {
		if (_replacedSkuExternalReferenceCodeSupplier != null) {
			replacedSkuExternalReferenceCode =
				_replacedSkuExternalReferenceCodeSupplier.get();

			_replacedSkuExternalReferenceCodeSupplier = null;
		}

		return replacedSkuExternalReferenceCode;
	}

	public void setReplacedSkuExternalReferenceCode(
		String replacedSkuExternalReferenceCode) {

		this.replacedSkuExternalReferenceCode =
			replacedSkuExternalReferenceCode;

		_replacedSkuExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setReplacedSkuExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			replacedSkuExternalReferenceCodeUnsafeSupplier) {

		_replacedSkuExternalReferenceCodeSupplier = () -> {
			try {
				return replacedSkuExternalReferenceCodeUnsafeSupplier.get();
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
	protected String replacedSkuExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _replacedSkuExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getReplacedSkuId() {
		if (_replacedSkuIdSupplier != null) {
			replacedSkuId = _replacedSkuIdSupplier.get();

			_replacedSkuIdSupplier = null;
		}

		return replacedSkuId;
	}

	public void setReplacedSkuId(Long replacedSkuId) {
		this.replacedSkuId = replacedSkuId;

		_replacedSkuIdSupplier = null;
	}

	@JsonIgnore
	public void setReplacedSkuId(
		UnsafeSupplier<Long, Exception> replacedSkuIdUnsafeSupplier) {

		_replacedSkuIdSupplier = () -> {
			try {
				return replacedSkuIdUnsafeSupplier.get();
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
	protected Long replacedSkuId;

	@JsonIgnore
	private Supplier<Long> _replacedSkuIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "2017-07-21")
	public Date getRequestedDeliveryDate() {
		if (_requestedDeliveryDateSupplier != null) {
			requestedDeliveryDate = _requestedDeliveryDateSupplier.get();

			_requestedDeliveryDateSupplier = null;
		}

		return requestedDeliveryDate;
	}

	public void setRequestedDeliveryDate(Date requestedDeliveryDate) {
		this.requestedDeliveryDate = requestedDeliveryDate;

		_requestedDeliveryDateSupplier = null;
	}

	@JsonIgnore
	public void setRequestedDeliveryDate(
		UnsafeSupplier<Date, Exception> requestedDeliveryDateUnsafeSupplier) {

		_requestedDeliveryDateSupplier = () -> {
			try {
				return requestedDeliveryDateUnsafeSupplier.get();
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
	protected Date requestedDeliveryDate;

	@JsonIgnore
	private Supplier<Date> _requestedDeliveryDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getShippable() {
		if (_shippableSupplier != null) {
			shippable = _shippableSupplier.get();

			_shippableSupplier = null;
		}

		return shippable;
	}

	public void setShippable(Boolean shippable) {
		this.shippable = shippable;

		_shippableSupplier = null;
	}

	@JsonIgnore
	public void setShippable(
		UnsafeSupplier<Boolean, Exception> shippableUnsafeSupplier) {

		_shippableSupplier = () -> {
			try {
				return shippableUnsafeSupplier.get();
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
	protected Boolean shippable;

	@JsonIgnore
	private Supplier<Boolean> _shippableSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1.1")
	@Valid
	public BigDecimal getShippedQuantity() {
		if (_shippedQuantitySupplier != null) {
			shippedQuantity = _shippedQuantitySupplier.get();

			_shippedQuantitySupplier = null;
		}

		return shippedQuantity;
	}

	public void setShippedQuantity(BigDecimal shippedQuantity) {
		this.shippedQuantity = shippedQuantity;

		_shippedQuantitySupplier = null;
	}

	@JsonIgnore
	public void setShippedQuantity(
		UnsafeSupplier<BigDecimal, Exception> shippedQuantityUnsafeSupplier) {

		_shippedQuantitySupplier = () -> {
			try {
				return shippedQuantityUnsafeSupplier.get();
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
	protected BigDecimal shippedQuantity;

	@JsonIgnore
	private Supplier<BigDecimal> _shippedQuantitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ShippingAddress getShippingAddress() {
		if (_shippingAddressSupplier != null) {
			shippingAddress = _shippingAddressSupplier.get();

			_shippingAddressSupplier = null;
		}

		return shippingAddress;
	}

	public void setShippingAddress(ShippingAddress shippingAddress) {
		this.shippingAddress = shippingAddress;

		_shippingAddressSupplier = null;
	}

	@JsonIgnore
	public void setShippingAddress(
		UnsafeSupplier<ShippingAddress, Exception>
			shippingAddressUnsafeSupplier) {

		_shippingAddressSupplier = () -> {
			try {
				return shippingAddressUnsafeSupplier.get();
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
	protected ShippingAddress shippingAddress;

	@JsonIgnore
	private Supplier<ShippingAddress> _shippingAddressSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "AB-34098-789-N")
	public String getShippingAddressExternalReferenceCode() {
		if (_shippingAddressExternalReferenceCodeSupplier != null) {
			shippingAddressExternalReferenceCode =
				_shippingAddressExternalReferenceCodeSupplier.get();

			_shippingAddressExternalReferenceCodeSupplier = null;
		}

		return shippingAddressExternalReferenceCode;
	}

	public void setShippingAddressExternalReferenceCode(
		String shippingAddressExternalReferenceCode) {

		this.shippingAddressExternalReferenceCode =
			shippingAddressExternalReferenceCode;

		_shippingAddressExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setShippingAddressExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			shippingAddressExternalReferenceCodeUnsafeSupplier) {

		_shippingAddressExternalReferenceCodeSupplier = () -> {
			try {
				return shippingAddressExternalReferenceCodeUnsafeSupplier.get();
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
	protected String shippingAddressExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _shippingAddressExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "31130")
	public Long getShippingAddressId() {
		if (_shippingAddressIdSupplier != null) {
			shippingAddressId = _shippingAddressIdSupplier.get();

			_shippingAddressIdSupplier = null;
		}

		return shippingAddressId;
	}

	public void setShippingAddressId(Long shippingAddressId) {
		this.shippingAddressId = shippingAddressId;

		_shippingAddressIdSupplier = null;
	}

	@JsonIgnore
	public void setShippingAddressId(
		UnsafeSupplier<Long, Exception> shippingAddressIdUnsafeSupplier) {

		_shippingAddressIdSupplier = () -> {
			try {
				return shippingAddressIdUnsafeSupplier.get();
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
	protected Long shippingAddressId;

	@JsonIgnore
	private Supplier<Long> _shippingAddressIdSupplier;

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
	protected String sku;

	@JsonIgnore
	private Supplier<String> _skuSupplier;

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
	@io.swagger.v3.oas.annotations.media.Schema(example = "30128")
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
	protected Long skuId;

	@JsonIgnore
	private Supplier<Long> _skuIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getSubscription() {
		if (_subscriptionSupplier != null) {
			subscription = _subscriptionSupplier.get();

			_subscriptionSupplier = null;
		}

		return subscription;
	}

	public void setSubscription(Boolean subscription) {
		this.subscription = subscription;

		_subscriptionSupplier = null;
	}

	@JsonIgnore
	public void setSubscription(
		UnsafeSupplier<Boolean, Exception> subscriptionUnsafeSupplier) {

		_subscriptionSupplier = () -> {
			try {
				return subscriptionUnsafeSupplier.get();
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
	protected Boolean subscription;

	@JsonIgnore
	private Supplier<Boolean> _subscriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "pc")
	public String getUnitOfMeasure() {
		if (_unitOfMeasureSupplier != null) {
			unitOfMeasure = _unitOfMeasureSupplier.get();

			_unitOfMeasureSupplier = null;
		}

		return unitOfMeasure;
	}

	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;

		_unitOfMeasureSupplier = null;
	}

	@JsonIgnore
	public void setUnitOfMeasure(
		UnsafeSupplier<String, Exception> unitOfMeasureUnsafeSupplier) {

		_unitOfMeasureSupplier = () -> {
			try {
				return unitOfMeasureUnsafeSupplier.get();
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
	protected String unitOfMeasure;

	@JsonIgnore
	private Supplier<String> _unitOfMeasureSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "s")
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

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "101")
	@Valid
	public BigDecimal getUnitPrice() {
		if (_unitPriceSupplier != null) {
			unitPrice = _unitPriceSupplier.get();

			_unitPriceSupplier = null;
		}

		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;

		_unitPriceSupplier = null;
	}

	@JsonIgnore
	public void setUnitPrice(
		UnsafeSupplier<BigDecimal, Exception> unitPriceUnsafeSupplier) {

		_unitPriceSupplier = () -> {
			try {
				return unitPriceUnsafeSupplier.get();
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
	protected BigDecimal unitPrice;

	@JsonIgnore
	private Supplier<BigDecimal> _unitPriceSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "101")
	@Valid
	public BigDecimal getUnitPriceWithTaxAmount() {
		if (_unitPriceWithTaxAmountSupplier != null) {
			unitPriceWithTaxAmount = _unitPriceWithTaxAmountSupplier.get();

			_unitPriceWithTaxAmountSupplier = null;
		}

		return unitPriceWithTaxAmount;
	}

	public void setUnitPriceWithTaxAmount(BigDecimal unitPriceWithTaxAmount) {
		this.unitPriceWithTaxAmount = unitPriceWithTaxAmount;

		_unitPriceWithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setUnitPriceWithTaxAmount(
		UnsafeSupplier<BigDecimal, Exception>
			unitPriceWithTaxAmountUnsafeSupplier) {

		_unitPriceWithTaxAmountSupplier = () -> {
			try {
				return unitPriceWithTaxAmountUnsafeSupplier.get();
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
	protected BigDecimal unitPriceWithTaxAmount;

	@JsonIgnore
	private Supplier<BigDecimal> _unitPriceWithTaxAmountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getVirtualItemURLs() {
		if (_virtualItemURLsSupplier != null) {
			virtualItemURLs = _virtualItemURLsSupplier.get();

			_virtualItemURLsSupplier = null;
		}

		return virtualItemURLs;
	}

	public void setVirtualItemURLs(String[] virtualItemURLs) {
		this.virtualItemURLs = virtualItemURLs;

		_virtualItemURLsSupplier = null;
	}

	@JsonIgnore
	public void setVirtualItemURLs(
		UnsafeSupplier<String[], Exception> virtualItemURLsUnsafeSupplier) {

		_virtualItemURLsSupplier = () -> {
			try {
				return virtualItemURLsUnsafeSupplier.get();
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
	protected String[] virtualItemURLs;

	@JsonIgnore
	private Supplier<String[]> _virtualItemURLsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public VirtualItem[] getVirtualItems() {
		if (_virtualItemsSupplier != null) {
			virtualItems = _virtualItemsSupplier.get();

			_virtualItemsSupplier = null;
		}

		return virtualItems;
	}

	public void setVirtualItems(VirtualItem[] virtualItems) {
		this.virtualItems = virtualItems;

		_virtualItemsSupplier = null;
	}

	@JsonIgnore
	public void setVirtualItems(
		UnsafeSupplier<VirtualItem[], Exception> virtualItemsUnsafeSupplier) {

		_virtualItemsSupplier = () -> {
			try {
				return virtualItemsUnsafeSupplier.get();
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
	protected VirtualItem[] virtualItems;

	@JsonIgnore
	private Supplier<VirtualItem[]> _virtualItemsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof OrderItem)) {
			return false;
		}

		OrderItem orderItem = (OrderItem)object;

		return Objects.equals(toString(), orderItem.toString());
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

		Long bookedQuantityId = getBookedQuantityId();

		if (bookedQuantityId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"bookedQuantityId\": ");

			sb.append(bookedQuantityId);
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

		BigDecimal decimalQuantity = getDecimalQuantity();

		if (decimalQuantity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"decimalQuantity\": ");

			sb.append(decimalQuantity);
		}

		String deliveryGroup = getDeliveryGroup();

		if (deliveryGroup != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deliveryGroup\": ");

			sb.append("\"");

			sb.append(_escape(deliveryGroup));

			sb.append("\"");
		}

		String deliveryGroupName = getDeliveryGroupName();

		if (deliveryGroupName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deliveryGroupName\": ");

			sb.append("\"");

			sb.append(_escape(deliveryGroupName));

			sb.append("\"");
		}

		BigDecimal discountAmount = getDiscountAmount();

		if (discountAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountAmount\": ");

			sb.append(discountAmount);
		}

		Boolean discountManuallyAdjusted = getDiscountManuallyAdjusted();

		if (discountManuallyAdjusted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountManuallyAdjusted\": ");

			sb.append(discountManuallyAdjusted);
		}

		BigDecimal discountPercentageLevel1 = getDiscountPercentageLevel1();

		if (discountPercentageLevel1 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountPercentageLevel1\": ");

			sb.append(discountPercentageLevel1);
		}

		BigDecimal discountPercentageLevel1WithTaxAmount =
			getDiscountPercentageLevel1WithTaxAmount();

		if (discountPercentageLevel1WithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountPercentageLevel1WithTaxAmount\": ");

			sb.append(discountPercentageLevel1WithTaxAmount);
		}

		BigDecimal discountPercentageLevel2 = getDiscountPercentageLevel2();

		if (discountPercentageLevel2 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountPercentageLevel2\": ");

			sb.append(discountPercentageLevel2);
		}

		BigDecimal discountPercentageLevel2WithTaxAmount =
			getDiscountPercentageLevel2WithTaxAmount();

		if (discountPercentageLevel2WithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountPercentageLevel2WithTaxAmount\": ");

			sb.append(discountPercentageLevel2WithTaxAmount);
		}

		BigDecimal discountPercentageLevel3 = getDiscountPercentageLevel3();

		if (discountPercentageLevel3 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountPercentageLevel3\": ");

			sb.append(discountPercentageLevel3);
		}

		BigDecimal discountPercentageLevel3WithTaxAmount =
			getDiscountPercentageLevel3WithTaxAmount();

		if (discountPercentageLevel3WithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountPercentageLevel3WithTaxAmount\": ");

			sb.append(discountPercentageLevel3WithTaxAmount);
		}

		BigDecimal discountPercentageLevel4 = getDiscountPercentageLevel4();

		if (discountPercentageLevel4 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountPercentageLevel4\": ");

			sb.append(discountPercentageLevel4);
		}

		BigDecimal discountPercentageLevel4WithTaxAmount =
			getDiscountPercentageLevel4WithTaxAmount();

		if (discountPercentageLevel4WithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountPercentageLevel4WithTaxAmount\": ");

			sb.append(discountPercentageLevel4WithTaxAmount);
		}

		BigDecimal discountWithTaxAmount = getDiscountWithTaxAmount();

		if (discountWithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountWithTaxAmount\": ");

			sb.append(discountWithTaxAmount);
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

		BigDecimal finalPrice = getFinalPrice();

		if (finalPrice != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"finalPrice\": ");

			sb.append(finalPrice);
		}

		BigDecimal finalPriceWithTaxAmount = getFinalPriceWithTaxAmount();

		if (finalPriceWithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"finalPriceWithTaxAmount\": ");

			sb.append(finalPriceWithTaxAmount);
		}

		String formattedQuantity = getFormattedQuantity();

		if (formattedQuantity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formattedQuantity\": ");

			sb.append("\"");

			sb.append(_escape(formattedQuantity));

			sb.append("\"");
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		Map<String, String> name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(name));
		}

		String options = getOptions();

		if (options != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"options\": ");

			sb.append("\"");

			sb.append(_escape(options));

			sb.append("\"");
		}

		String orderExternalReferenceCode = getOrderExternalReferenceCode();

		if (orderExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(orderExternalReferenceCode));

			sb.append("\"");
		}

		Long orderId = getOrderId();

		if (orderId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderId\": ");

			sb.append(orderId);
		}

		Boolean priceManuallyAdjusted = getPriceManuallyAdjusted();

		if (priceManuallyAdjusted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceManuallyAdjusted\": ");

			sb.append(priceManuallyAdjusted);
		}

		String printedNote = getPrintedNote();

		if (printedNote != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"printedNote\": ");

			sb.append("\"");

			sb.append(_escape(printedNote));

			sb.append("\"");
		}

		BigDecimal promoPrice = getPromoPrice();

		if (promoPrice != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"promoPrice\": ");

			sb.append(promoPrice);
		}

		BigDecimal promoPriceWithTaxAmount = getPromoPriceWithTaxAmount();

		if (promoPriceWithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"promoPriceWithTaxAmount\": ");

			sb.append(promoPriceWithTaxAmount);
		}

		BigDecimal quantity = getQuantity();

		if (quantity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"quantity\": ");

			sb.append(quantity);
		}

		String replacedSku = getReplacedSku();

		if (replacedSku != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"replacedSku\": ");

			sb.append("\"");

			sb.append(_escape(replacedSku));

			sb.append("\"");
		}

		String replacedSkuExternalReferenceCode =
			getReplacedSkuExternalReferenceCode();

		if (replacedSkuExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"replacedSkuExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(replacedSkuExternalReferenceCode));

			sb.append("\"");
		}

		Long replacedSkuId = getReplacedSkuId();

		if (replacedSkuId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"replacedSkuId\": ");

			sb.append(replacedSkuId);
		}

		Date requestedDeliveryDate = getRequestedDeliveryDate();

		if (requestedDeliveryDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"requestedDeliveryDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(requestedDeliveryDate));

			sb.append("\"");
		}

		Boolean shippable = getShippable();

		if (shippable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippable\": ");

			sb.append(shippable);
		}

		BigDecimal shippedQuantity = getShippedQuantity();

		if (shippedQuantity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippedQuantity\": ");

			sb.append(shippedQuantity);
		}

		ShippingAddress shippingAddress = getShippingAddress();

		if (shippingAddress != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAddress\": ");

			sb.append(String.valueOf(shippingAddress));
		}

		String shippingAddressExternalReferenceCode =
			getShippingAddressExternalReferenceCode();

		if (shippingAddressExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAddressExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(shippingAddressExternalReferenceCode));

			sb.append("\"");
		}

		Long shippingAddressId = getShippingAddressId();

		if (shippingAddressId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAddressId\": ");

			sb.append(shippingAddressId);
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

		Boolean subscription = getSubscription();

		if (subscription != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subscription\": ");

			sb.append(subscription);
		}

		String unitOfMeasure = getUnitOfMeasure();

		if (unitOfMeasure != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unitOfMeasure\": ");

			sb.append("\"");

			sb.append(_escape(unitOfMeasure));

			sb.append("\"");
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

		BigDecimal unitPrice = getUnitPrice();

		if (unitPrice != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unitPrice\": ");

			sb.append(unitPrice);
		}

		BigDecimal unitPriceWithTaxAmount = getUnitPriceWithTaxAmount();

		if (unitPriceWithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unitPriceWithTaxAmount\": ");

			sb.append(unitPriceWithTaxAmount);
		}

		String[] virtualItemURLs = getVirtualItemURLs();

		if (virtualItemURLs != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"virtualItemURLs\": ");

			sb.append("[");

			for (int i = 0; i < virtualItemURLs.length; i++) {
				sb.append("\"");

				sb.append(_escape(virtualItemURLs[i]));

				sb.append("\"");

				if ((i + 1) < virtualItemURLs.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		VirtualItem[] virtualItems = getVirtualItems();

		if (virtualItems != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"virtualItems\": ");

			sb.append("[");

			for (int i = 0; i < virtualItems.length; i++) {
				sb.append(String.valueOf(virtualItems[i]));

				if ((i + 1) < virtualItems.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.order.dto.v1_0.OrderItem",
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