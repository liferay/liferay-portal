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
import com.liferay.portal.kernel.json.JSONFactoryUtil;
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

import java.util.Arrays;
import java.util.Collection;
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
@GraphQLName(
	description = "Alternative unit in which the parent SKU can be sold, with its own conversion rate to the base unit, pricing, and ordering rules.",
	value = "SkuUnitOfMeasure"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Alternative unit in which the parent SKU can be sold, with its own conversion rate to the base unit, pricing, and ordering rules."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "SkuUnitOfMeasure")
public class SkuUnitOfMeasure implements Serializable {

	public static SkuUnitOfMeasure toDTO(String json) {
		return ObjectMapperUtil.readValue(SkuUnitOfMeasure.class, json);
	}

	public static SkuUnitOfMeasure unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(SkuUnitOfMeasure.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Map of HATEOAS actions available to the current user, keyed by action name. Each value carries the href template and HTTP method, computed dynamically from user permissions. Read-only."
	)
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

	@GraphQLField(
		description = "Map of HATEOAS actions available to the current user, keyed by action name. Each value carries the href template and HTTP method, computed dynamically from user permissions. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether this unit of measure is selectable for ordering.",
		example = "true"
	)
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

	@GraphQLField(
		description = "Whether this unit of measure is selectable for ordering."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean active;

	@JsonIgnore
	private Supplier<Boolean> _activeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Price per `pricingQuantity` for this unit of measure, taken from the base price list entry keyed by the SKU and this unit-of-measure key; rounded to the currency precision and read-only.",
		example = "10.0"
	)
	@Valid
	public BigDecimal getBasePrice() {
		if (_basePriceSupplier != null) {
			basePrice = _basePriceSupplier.get();

			_basePriceSupplier = null;
		}

		return basePrice;
	}

	public void setBasePrice(BigDecimal basePrice) {
		this.basePrice = basePrice;

		_basePriceSupplier = null;
	}

	@JsonIgnore
	public void setBasePrice(
		UnsafeSupplier<BigDecimal, Exception> basePriceUnsafeSupplier) {

		_basePriceSupplier = () -> {
			try {
				return basePriceUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Price per `pricingQuantity` for this unit of measure, taken from the base price list entry keyed by the SKU and this unit-of-measure key; rounded to the currency precision and read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected BigDecimal basePrice;

	@JsonIgnore
	private Supplier<BigDecimal> _basePriceSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Internal numeric identifier of the unit-of-measure entry; read-only and assigned by the service on create.",
		example = "30130"
	)
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

	@GraphQLField(
		description = "Internal numeric identifier of the unit-of-measure entry; read-only and assigned by the service on create."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Minimum increment in which orders are accepted for this unit of measure; required, must be greater than zero, and its decimal scale must not exceed `precision`. The service normalizes the value to the configured precision.",
		example = "1.5"
	)
	@Valid
	public BigDecimal getIncrementalOrderQuantity() {
		if (_incrementalOrderQuantitySupplier != null) {
			incrementalOrderQuantity = _incrementalOrderQuantitySupplier.get();

			_incrementalOrderQuantitySupplier = null;
		}

		return incrementalOrderQuantity;
	}

	public void setIncrementalOrderQuantity(
		BigDecimal incrementalOrderQuantity) {

		this.incrementalOrderQuantity = incrementalOrderQuantity;

		_incrementalOrderQuantitySupplier = null;
	}

	@JsonIgnore
	public void setIncrementalOrderQuantity(
		UnsafeSupplier<BigDecimal, Exception>
			incrementalOrderQuantityUnsafeSupplier) {

		_incrementalOrderQuantitySupplier = () -> {
			try {
				return incrementalOrderQuantityUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Minimum increment in which orders are accepted for this unit of measure; required, must be greater than zero, and its decimal scale must not exceed `precision`. The service normalizes the value to the configured precision."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected BigDecimal incrementalOrderQuantity;

	@JsonIgnore
	private Supplier<BigDecimal> _incrementalOrderQuantitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Stable string key identifying the unit of measure within the parent SKU; required and must be unique among the SKU's units of measure.",
		example = "pl"
	)
	public String getKey() {
		if (_keySupplier != null) {
			key = _keySupplier.get();

			_keySupplier = null;
		}

		return key;
	}

	public void setKey(String key) {
		this.key = key;

		_keySupplier = null;
	}

	@JsonIgnore
	public void setKey(UnsafeSupplier<String, Exception> keyUnsafeSupplier) {
		_keySupplier = () -> {
			try {
				return keyUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Stable string key identifying the unit of measure within the parent SKU; required and must be unique among the SKU's units of measure."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String key;

	@JsonIgnore
	private Supplier<String> _keySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Localized text. Map keys are locale codes; values are the translated strings. Required.",
		example = "{en_US=Pallet, hr_HR=Pallet HR, hu_HU=Pallet HU}"
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

	@GraphQLField(
		description = "Localized text. Map keys are locale codes; values are the translated strings. Required."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> name;

	@JsonIgnore
	private Supplier<Map<String, String>> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Number of decimal places allowed for quantities expressed in this unit of measure; required and used to scale `incrementalOrderQuantity`, `pricingQuantity`, and `rate` with half-up rounding.",
		example = "3"
	)
	public Integer getPrecision() {
		if (_precisionSupplier != null) {
			precision = _precisionSupplier.get();

			_precisionSupplier = null;
		}

		return precision;
	}

	public void setPrecision(Integer precision) {
		this.precision = precision;

		_precisionSupplier = null;
	}

	@JsonIgnore
	public void setPrecision(
		UnsafeSupplier<Integer, Exception> precisionUnsafeSupplier) {

		_precisionSupplier = () -> {
			try {
				return precisionUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Number of decimal places allowed for quantities expressed in this unit of measure; required and used to scale `incrementalOrderQuantity`, `pricingQuantity`, and `rate` with half-up rounding."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer precision;

	@JsonIgnore
	private Supplier<Integer> _precisionSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Quantity in this unit of measure for which `basePrice` and `promoPrice` apply; scaled to `precision` with half-up rounding.",
		example = "1.5"
	)
	@Valid
	public BigDecimal getPricingQuantity() {
		if (_pricingQuantitySupplier != null) {
			pricingQuantity = _pricingQuantitySupplier.get();

			_pricingQuantitySupplier = null;
		}

		return pricingQuantity;
	}

	public void setPricingQuantity(BigDecimal pricingQuantity) {
		this.pricingQuantity = pricingQuantity;

		_pricingQuantitySupplier = null;
	}

	@JsonIgnore
	public void setPricingQuantity(
		UnsafeSupplier<BigDecimal, Exception> pricingQuantityUnsafeSupplier) {

		_pricingQuantitySupplier = () -> {
			try {
				return pricingQuantityUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Quantity in this unit of measure for which `basePrice` and `promoPrice` apply; scaled to `precision` with half-up rounding."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected BigDecimal pricingQuantity;

	@JsonIgnore
	private Supplier<BigDecimal> _pricingQuantitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether this is the default unit of measure for the SKU; on update the service automatically clears the primary flag on the SKU's other units of measure.",
		example = "true"
	)
	public Boolean getPrimary() {
		if (_primarySupplier != null) {
			primary = _primarySupplier.get();

			_primarySupplier = null;
		}

		return primary;
	}

	public void setPrimary(Boolean primary) {
		this.primary = primary;

		_primarySupplier = null;
	}

	@JsonIgnore
	public void setPrimary(
		UnsafeSupplier<Boolean, Exception> primaryUnsafeSupplier) {

		_primarySupplier = () -> {
			try {
				return primaryUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Whether this is the default unit of measure for the SKU; on update the service automatically clears the primary flag on the SKU's other units of measure."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean primary;

	@JsonIgnore
	private Supplier<Boolean> _primarySupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Display order among the SKU's units of measure; lower values surface first.",
		example = "1.1"
	)
	public Double getPriority() {
		if (_prioritySupplier != null) {
			priority = _prioritySupplier.get();

			_prioritySupplier = null;
		}

		return priority;
	}

	public void setPriority(Double priority) {
		this.priority = priority;

		_prioritySupplier = null;
	}

	@JsonIgnore
	public void setPriority(
		UnsafeSupplier<Double, Exception> priorityUnsafeSupplier) {

		_prioritySupplier = () -> {
			try {
				return priorityUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Display order among the SKU's units of measure; lower values surface first."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Double priority;

	@JsonIgnore
	private Supplier<Double> _prioritySupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Promotional price per `pricingQuantity` for this unit of measure, taken from the promotion price list entry keyed by the SKU and this unit-of-measure key; rounded to the currency precision and read-only.",
		example = "10.0"
	)
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

	@GraphQLField(
		description = "Promotional price per `pricingQuantity` for this unit of measure, taken from the promotion price list entry keyed by the SKU and this unit-of-measure key; rounded to the currency precision and read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected BigDecimal promoPrice;

	@JsonIgnore
	private Supplier<BigDecimal> _promoPriceSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Conversion factor from this unit to the SKU's base unit; required, must be greater than zero, and scaled to `precision` with half-up rounding.",
		example = "1.5"
	)
	@Valid
	public BigDecimal getRate() {
		if (_rateSupplier != null) {
			rate = _rateSupplier.get();

			_rateSupplier = null;
		}

		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;

		_rateSupplier = null;
	}

	@JsonIgnore
	public void setRate(
		UnsafeSupplier<BigDecimal, Exception> rateUnsafeSupplier) {

		_rateSupplier = () -> {
			try {
				return rateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Conversion factor from this unit to the SKU's base unit; required, must be greater than zero, and scaled to `precision` with half-up rounding."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected BigDecimal rate;

	@JsonIgnore
	private Supplier<BigDecimal> _rateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "SKU code of the parent variant; mirrored on the unit-of-measure entry for indexing and lookups. Read-only.",
		example = "SKU-0001"
	)
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

	@GraphQLField(
		description = "SKU code of the parent variant; mirrored on the unit-of-measure entry for indexing and lookups. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String sku;

	@JsonIgnore
	private Supplier<String> _skuSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Internal numeric identifier of the parent SKU; read-only.",
		example = "30130"
	)
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

	@GraphQLField(
		description = "Internal numeric identifier of the parent SKU; read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long skuId;

	@JsonIgnore
	private Supplier<Long> _skuIdSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SkuUnitOfMeasure)) {
			return false;
		}

		SkuUnitOfMeasure skuUnitOfMeasure = (SkuUnitOfMeasure)object;

		return Objects.equals(toString(), skuUnitOfMeasure.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

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

		BigDecimal basePrice = getBasePrice();

		if (basePrice != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"basePrice\": ");

			sb.append(basePrice);
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		BigDecimal incrementalOrderQuantity = getIncrementalOrderQuantity();

		if (incrementalOrderQuantity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"incrementalOrderQuantity\": ");

			sb.append(incrementalOrderQuantity);
		}

		String key = getKey();

		if (key != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(key));

			sb.append("\"");
		}

		Map<String, String> name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(name));
		}

		Integer precision = getPrecision();

		if (precision != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"precision\": ");

			sb.append(precision);
		}

		BigDecimal pricingQuantity = getPricingQuantity();

		if (pricingQuantity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pricingQuantity\": ");

			sb.append(pricingQuantity);
		}

		Boolean primary = getPrimary();

		if (primary != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"primary\": ");

			sb.append(primary);
		}

		Double priority = getPriority();

		if (priority != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(priority);
		}

		BigDecimal promoPrice = getPromoPrice();

		if (promoPrice != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"promoPrice\": ");

			sb.append(promoPrice);
		}

		BigDecimal rate = getRate();

		if (rate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"rate\": ");

			sb.append(rate);
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

		Long skuId = getSkuId();

		if (skuId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuId\": ");

			sb.append(skuId);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.catalog.dto.v1_0.SkuUnitOfMeasure",
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

	private static String _toJSON(Object value) {
		if (value instanceof Collection) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray((Collection<?>)value));
		}
		else if (value instanceof Map) {
			return String.valueOf(
				JSONFactoryUtil.createJSONObject((Map<?, ?>)value));
		}
		else if (value instanceof Object[]) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray(
					Arrays.asList((Object[])value)));
		}
		else if (value instanceof String) {
			return StringBundler.concat("\"", _escape(value), "\"");
		}

		return String.valueOf(value);
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:422334351