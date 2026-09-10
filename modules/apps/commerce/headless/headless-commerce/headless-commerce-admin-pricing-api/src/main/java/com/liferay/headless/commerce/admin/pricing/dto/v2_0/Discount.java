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
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Collection;
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
@GraphQLName(
	description = "Promotional discount program scoped to a single company. Backed by discount; carries the target slice (subtotal, total, products, categories, skus, product-groups, order-types, or shipping), the limitation policy that caps reuse, an optional couponCode gate, the percentage tiers (or fixed-amount cap), and the cascading rel collections (account-group, account, category, channel, order-type, product, product-group, sku, rule). Workflow-aware -- the runtime only honours discounts whose status is approved (Approved).",
	value = "Discount"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Promotional discount program scoped to a single company. Backed by discount; carries the target slice (subtotal, total, products, categories, skus, product-groups, order-types, or shipping), the limitation policy that caps reuse, an optional couponCode gate, the percentage tiers (or fixed-amount cap), and the cascading rel collections (account-group, account, category, channel, order-type, product, product-group, sku, rule). Workflow-aware -- the runtime only honours discounts whose status is approved (Approved).",
	requiredProperties = {
		"level", "limitationType", "target", "title", "usePercentage"
	}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Discount")
public class Discount implements Serializable {

	public static Discount toDTO(String json) {
		return ObjectMapperUtil.readValue(Discount.class, json);
	}

	public static Discount unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Discount.class, json);
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
		description = "Whether the discount is currently enabled. When false the price calculation engine skips the discount during cart evaluation.",
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
		description = "Whether the discount is currently enabled. When false the price calculation engine skips the discount during cart evaluation."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean active;

	@JsonIgnore
	private Supplier<Boolean> _activeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Pre-formatted display string of the monetary amount, in the parent record's currency. Read-only.",
		example = "$10.00"
	)
	public String getAmountFormatted() {
		if (_amountFormattedSupplier != null) {
			amountFormatted = _amountFormattedSupplier.get();

			_amountFormattedSupplier = null;
		}

		return amountFormatted;
	}

	public void setAmountFormatted(String amountFormatted) {
		this.amountFormatted = amountFormatted;

		_amountFormattedSupplier = null;
	}

	@JsonIgnore
	public void setAmountFormatted(
		UnsafeSupplier<String, Exception> amountFormattedUnsafeSupplier) {

		_amountFormattedSupplier = () -> {
			try {
				return amountFormattedUnsafeSupplier.get();
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
		description = "Pre-formatted display string of the monetary amount, in the parent record's currency. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String amountFormatted;

	@JsonIgnore
	private Supplier<String> _amountFormattedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Optional coupon code that the buyer must supply at checkout when `useCouponCode` is true. Case-sensitive; ignored when `useCouponCode` is false.",
		example = "SAVE20"
	)
	public String getCouponCode() {
		if (_couponCodeSupplier != null) {
			couponCode = _couponCodeSupplier.get();

			_couponCodeSupplier = null;
		}

		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;

		_couponCodeSupplier = null;
	}

	@JsonIgnore
	public void setCouponCode(
		UnsafeSupplier<String, Exception> couponCodeUnsafeSupplier) {

		_couponCodeSupplier = () -> {
			try {
				return couponCodeUnsafeSupplier.get();
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
		description = "Optional coupon code that the buyer must supply at checkout when `useCouponCode` is true. Case-sensitive; ignored when `useCouponCode` is false."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String couponCode;

	@JsonIgnore
	private Supplier<String> _couponCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Free-form Expando custom fields attached to the underlying discount entity. Keys are Expando attribute names; values follow each attribute's declared column type.",
		example = "{priority=high, segment=B2B}"
	)
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

	@GraphQLField(
		description = "Free-form Expando custom fields attached to the underlying discount entity. Keys are Expando attribute names; values follow each attribute's declared column type."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, ?> customFields;

	@JsonIgnore
	private Supplier<Map<String, ?>> _customFieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Account-group bindings cascaded on upsert. Each entry attaches the discount to one AccountGroup; supplying this array on POST/PUT replaces the previous bindings."
	)
	@Valid
	public DiscountAccountGroup[] getDiscountAccountGroups() {
		if (_discountAccountGroupsSupplier != null) {
			discountAccountGroups = _discountAccountGroupsSupplier.get();

			_discountAccountGroupsSupplier = null;
		}

		return discountAccountGroups;
	}

	public void setDiscountAccountGroups(
		DiscountAccountGroup[] discountAccountGroups) {

		this.discountAccountGroups = discountAccountGroups;

		_discountAccountGroupsSupplier = null;
	}

	@JsonIgnore
	public void setDiscountAccountGroups(
		UnsafeSupplier<DiscountAccountGroup[], Exception>
			discountAccountGroupsUnsafeSupplier) {

		_discountAccountGroupsSupplier = () -> {
			try {
				return discountAccountGroupsUnsafeSupplier.get();
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
		description = "Account-group bindings cascaded on upsert. Each entry attaches the discount to one AccountGroup; supplying this array on POST/PUT replaces the previous bindings."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected DiscountAccountGroup[] discountAccountGroups;

	@JsonIgnore
	private Supplier<DiscountAccountGroup[]> _discountAccountGroupsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Account bindings cascaded on upsert. Each entry attaches the discount to one Account; supplying this array on POST/PUT replaces the previous bindings."
	)
	@Valid
	public DiscountAccount[] getDiscountAccounts() {
		if (_discountAccountsSupplier != null) {
			discountAccounts = _discountAccountsSupplier.get();

			_discountAccountsSupplier = null;
		}

		return discountAccounts;
	}

	public void setDiscountAccounts(DiscountAccount[] discountAccounts) {
		this.discountAccounts = discountAccounts;

		_discountAccountsSupplier = null;
	}

	@JsonIgnore
	public void setDiscountAccounts(
		UnsafeSupplier<DiscountAccount[], Exception>
			discountAccountsUnsafeSupplier) {

		_discountAccountsSupplier = () -> {
			try {
				return discountAccountsUnsafeSupplier.get();
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
		description = "Account bindings cascaded on upsert. Each entry attaches the discount to one Account; supplying this array on POST/PUT replaces the previous bindings."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected DiscountAccount[] discountAccounts;

	@JsonIgnore
	private Supplier<DiscountAccount[]> _discountAccountsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "AssetCategory bindings cascaded on upsert. Each entry restricts the discount to one category; supplying this array on POST/PUT replaces the previous bindings."
	)
	@Valid
	public DiscountCategory[] getDiscountCategories() {
		if (_discountCategoriesSupplier != null) {
			discountCategories = _discountCategoriesSupplier.get();

			_discountCategoriesSupplier = null;
		}

		return discountCategories;
	}

	public void setDiscountCategories(DiscountCategory[] discountCategories) {
		this.discountCategories = discountCategories;

		_discountCategoriesSupplier = null;
	}

	@JsonIgnore
	public void setDiscountCategories(
		UnsafeSupplier<DiscountCategory[], Exception>
			discountCategoriesUnsafeSupplier) {

		_discountCategoriesSupplier = () -> {
			try {
				return discountCategoriesUnsafeSupplier.get();
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
		description = "AssetCategory bindings cascaded on upsert. Each entry restricts the discount to one category; supplying this array on POST/PUT replaces the previous bindings."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected DiscountCategory[] discountCategories;

	@JsonIgnore
	private Supplier<DiscountCategory[]> _discountCategoriesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Channel bindings cascaded on upsert. Each entry restricts the discount to one channel; supplying this array on POST/PUT replaces the previous bindings."
	)
	@Valid
	public DiscountChannel[] getDiscountChannels() {
		if (_discountChannelsSupplier != null) {
			discountChannels = _discountChannelsSupplier.get();

			_discountChannelsSupplier = null;
		}

		return discountChannels;
	}

	public void setDiscountChannels(DiscountChannel[] discountChannels) {
		this.discountChannels = discountChannels;

		_discountChannelsSupplier = null;
	}

	@JsonIgnore
	public void setDiscountChannels(
		UnsafeSupplier<DiscountChannel[], Exception>
			discountChannelsUnsafeSupplier) {

		_discountChannelsSupplier = () -> {
			try {
				return discountChannelsUnsafeSupplier.get();
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
		description = "Channel bindings cascaded on upsert. Each entry restricts the discount to one channel; supplying this array on POST/PUT replaces the previous bindings."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected DiscountChannel[] discountChannels;

	@JsonIgnore
	private Supplier<DiscountChannel[]> _discountChannelsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Order-type bindings cascaded on upsert. Each entry restricts the discount to one order type; supplying this array on POST/PUT replaces the previous bindings."
	)
	@Valid
	public DiscountOrderType[] getDiscountOrderTypes() {
		if (_discountOrderTypesSupplier != null) {
			discountOrderTypes = _discountOrderTypesSupplier.get();

			_discountOrderTypesSupplier = null;
		}

		return discountOrderTypes;
	}

	public void setDiscountOrderTypes(DiscountOrderType[] discountOrderTypes) {
		this.discountOrderTypes = discountOrderTypes;

		_discountOrderTypesSupplier = null;
	}

	@JsonIgnore
	public void setDiscountOrderTypes(
		UnsafeSupplier<DiscountOrderType[], Exception>
			discountOrderTypesUnsafeSupplier) {

		_discountOrderTypesSupplier = () -> {
			try {
				return discountOrderTypesUnsafeSupplier.get();
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
		description = "Order-type bindings cascaded on upsert. Each entry restricts the discount to one order type; supplying this array on POST/PUT replaces the previous bindings."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected DiscountOrderType[] discountOrderTypes;

	@JsonIgnore
	private Supplier<DiscountOrderType[]> _discountOrderTypesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "product group bindings cascaded on upsert. Each entry restricts the discount to one product group; supplying this array on POST/PUT replaces the previous bindings."
	)
	@Valid
	public DiscountProductGroup[] getDiscountProductGroups() {
		if (_discountProductGroupsSupplier != null) {
			discountProductGroups = _discountProductGroupsSupplier.get();

			_discountProductGroupsSupplier = null;
		}

		return discountProductGroups;
	}

	public void setDiscountProductGroups(
		DiscountProductGroup[] discountProductGroups) {

		this.discountProductGroups = discountProductGroups;

		_discountProductGroupsSupplier = null;
	}

	@JsonIgnore
	public void setDiscountProductGroups(
		UnsafeSupplier<DiscountProductGroup[], Exception>
			discountProductGroupsUnsafeSupplier) {

		_discountProductGroupsSupplier = () -> {
			try {
				return discountProductGroupsUnsafeSupplier.get();
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
		description = "product group bindings cascaded on upsert. Each entry restricts the discount to one product group; supplying this array on POST/PUT replaces the previous bindings."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected DiscountProductGroup[] discountProductGroups;

	@JsonIgnore
	private Supplier<DiscountProductGroup[]> _discountProductGroupsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "product bindings cascaded on upsert. Each entry restricts the discount to one product; supplying this array on POST/PUT replaces the previous bindings."
	)
	@Valid
	public DiscountProduct[] getDiscountProducts() {
		if (_discountProductsSupplier != null) {
			discountProducts = _discountProductsSupplier.get();

			_discountProductsSupplier = null;
		}

		return discountProducts;
	}

	public void setDiscountProducts(DiscountProduct[] discountProducts) {
		this.discountProducts = discountProducts;

		_discountProductsSupplier = null;
	}

	@JsonIgnore
	public void setDiscountProducts(
		UnsafeSupplier<DiscountProduct[], Exception>
			discountProductsUnsafeSupplier) {

		_discountProductsSupplier = () -> {
			try {
				return discountProductsUnsafeSupplier.get();
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
		description = "product bindings cascaded on upsert. Each entry restricts the discount to one product; supplying this array on POST/PUT replaces the previous bindings."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected DiscountProduct[] discountProducts;

	@JsonIgnore
	private Supplier<DiscountProduct[]> _discountProductsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Validator rules cascaded on upsert. Each entry attaches a pre-qualification, post-qualification, or target validator; supplying this array on POST/PUT replaces the previous rules."
	)
	@Valid
	public DiscountRule[] getDiscountRules() {
		if (_discountRulesSupplier != null) {
			discountRules = _discountRulesSupplier.get();

			_discountRulesSupplier = null;
		}

		return discountRules;
	}

	public void setDiscountRules(DiscountRule[] discountRules) {
		this.discountRules = discountRules;

		_discountRulesSupplier = null;
	}

	@JsonIgnore
	public void setDiscountRules(
		UnsafeSupplier<DiscountRule[], Exception> discountRulesUnsafeSupplier) {

		_discountRulesSupplier = () -> {
			try {
				return discountRulesUnsafeSupplier.get();
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
		description = "Validator rules cascaded on upsert. Each entry attaches a pre-qualification, post-qualification, or target validator; supplying this array on POST/PUT replaces the previous rules."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected DiscountRule[] discountRules;

	@JsonIgnore
	private Supplier<DiscountRule[]> _discountRulesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Date and time when the discount becomes effective. ISO 8601.",
		example = "2017-07-21"
	)
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

	@GraphQLField(
		description = "Date and time when the discount becomes effective. ISO 8601."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date displayDate;

	@JsonIgnore
	private Supplier<Date> _displayDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Date and time when the discount expires. ISO 8601; the runtime stops honoring the discount past this date.",
		example = "2017-08-21"
	)
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

	@GraphQLField(
		description = "Date and time when the discount expires. ISO 8601; the runtime stops honoring the discount past this date."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date expirationDate;

	@JsonIgnore
	private Supplier<Date> _expirationDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Idempotency key for create and update; must be unique per discount within the company.",
		example = "AB-34098-789-N"
	)
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

	@GraphQLField(
		description = "Idempotency key for create and update; must be unique per discount within the company."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Internal numeric identifier of the discount. Server-assigned and stable.",
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
		description = "Internal numeric identifier of the discount. Server-assigned and stable."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Level qualifier (`L1`, `L2`, `L3`, `L4`) that selects which percentageLevel applies when usePercentage is true.",
		example = "L1"
	)
	public String getLevel() {
		if (_levelSupplier != null) {
			level = _levelSupplier.get();

			_levelSupplier = null;
		}

		return level;
	}

	public void setLevel(String level) {
		this.level = level;

		_levelSupplier = null;
	}

	@JsonIgnore
	public void setLevel(
		UnsafeSupplier<String, Exception> levelUnsafeSupplier) {

		_levelSupplier = () -> {
			try {
				return levelUnsafeSupplier.get();
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
		description = "Level qualifier (`L1`, `L2`, `L3`, `L4`) that selects which percentageLevel applies when usePercentage is true."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String level;

	@JsonIgnore
	private Supplier<String> _levelSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Maximum number of times the discount may be used in total when limitationType is `limited`. Zero means unlimited.",
		example = "0"
	)
	public Integer getLimitationTimes() {
		if (_limitationTimesSupplier != null) {
			limitationTimes = _limitationTimesSupplier.get();

			_limitationTimesSupplier = null;
		}

		return limitationTimes;
	}

	public void setLimitationTimes(Integer limitationTimes) {
		this.limitationTimes = limitationTimes;

		_limitationTimesSupplier = null;
	}

	@JsonIgnore
	public void setLimitationTimes(
		UnsafeSupplier<Integer, Exception> limitationTimesUnsafeSupplier) {

		_limitationTimesSupplier = () -> {
			try {
				return limitationTimesUnsafeSupplier.get();
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
		description = "Maximum number of times the discount may be used in total when limitationType is `limited`. Zero means unlimited."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer limitationTimes;

	@JsonIgnore
	private Supplier<Integer> _limitationTimesSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Maximum number of times the discount may be used per account when limitationType is `limited-for-accounts` or `limited-for-accounts-and-total`.",
		example = "0"
	)
	public Integer getLimitationTimesPerAccount() {
		if (_limitationTimesPerAccountSupplier != null) {
			limitationTimesPerAccount =
				_limitationTimesPerAccountSupplier.get();

			_limitationTimesPerAccountSupplier = null;
		}

		return limitationTimesPerAccount;
	}

	public void setLimitationTimesPerAccount(
		Integer limitationTimesPerAccount) {

		this.limitationTimesPerAccount = limitationTimesPerAccount;

		_limitationTimesPerAccountSupplier = null;
	}

	@JsonIgnore
	public void setLimitationTimesPerAccount(
		UnsafeSupplier<Integer, Exception>
			limitationTimesPerAccountUnsafeSupplier) {

		_limitationTimesPerAccountSupplier = () -> {
			try {
				return limitationTimesPerAccountUnsafeSupplier.get();
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
		description = "Maximum number of times the discount may be used per account when limitationType is `limited-for-accounts` or `limited-for-accounts-and-total`."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer limitationTimesPerAccount;

	@JsonIgnore
	private Supplier<Integer> _limitationTimesPerAccountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Caps how often the discount may be redeemed. One of `unlimited`, `limited`, `limited-for-accounts`, `limited-for-accounts-and-total`.",
		example = "unlimited"
	)
	public String getLimitationType() {
		if (_limitationTypeSupplier != null) {
			limitationType = _limitationTypeSupplier.get();

			_limitationTypeSupplier = null;
		}

		return limitationType;
	}

	public void setLimitationType(String limitationType) {
		this.limitationType = limitationType;

		_limitationTypeSupplier = null;
	}

	@JsonIgnore
	public void setLimitationType(
		UnsafeSupplier<String, Exception> limitationTypeUnsafeSupplier) {

		_limitationTypeSupplier = () -> {
			try {
				return limitationTypeUnsafeSupplier.get();
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
		description = "Caps how often the discount may be redeemed. One of `unlimited`, `limited`, `limited-for-accounts`, `limited-for-accounts-and-total`."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String limitationType;

	@JsonIgnore
	private Supplier<String> _limitationTypeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Fixed-amount cap on the discount payout, in the order's currency. Mutually exclusive with the percentage levels.",
		example = "25"
	)
	@Valid
	public BigDecimal getMaximumDiscountAmount() {
		if (_maximumDiscountAmountSupplier != null) {
			maximumDiscountAmount = _maximumDiscountAmountSupplier.get();

			_maximumDiscountAmountSupplier = null;
		}

		return maximumDiscountAmount;
	}

	public void setMaximumDiscountAmount(BigDecimal maximumDiscountAmount) {
		this.maximumDiscountAmount = maximumDiscountAmount;

		_maximumDiscountAmountSupplier = null;
	}

	@JsonIgnore
	public void setMaximumDiscountAmount(
		UnsafeSupplier<BigDecimal, Exception>
			maximumDiscountAmountUnsafeSupplier) {

		_maximumDiscountAmountSupplier = () -> {
			try {
				return maximumDiscountAmountUnsafeSupplier.get();
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
		description = "Fixed-amount cap on the discount payout, in the order's currency. Mutually exclusive with the percentage levels."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected BigDecimal maximumDiscountAmount;

	@JsonIgnore
	private Supplier<BigDecimal> _maximumDiscountAmountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Date value. ISO 8601.", example = "2017-07-21"
	)
	public Date getModifiedDate() {
		if (_modifiedDateSupplier != null) {
			modifiedDate = _modifiedDateSupplier.get();

			_modifiedDateSupplier = null;
		}

		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;

		_modifiedDateSupplier = null;
	}

	@JsonIgnore
	public void setModifiedDate(
		UnsafeSupplier<Date, Exception> modifiedDateUnsafeSupplier) {

		_modifiedDateSupplier = () -> {
			try {
				return modifiedDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Date value. ISO 8601.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date modifiedDate;

	@JsonIgnore
	private Supplier<Date> _modifiedDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When true, the discount has no expiration date and expirationDate is ignored.",
		example = "true"
	)
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

	@GraphQLField(
		description = "When true, the discount has no expiration date and expirationDate is ignored."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean neverExpire;

	@JsonIgnore
	private Supplier<Boolean> _neverExpireSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Read-only counter of how many times the discount has been used.",
		example = "5"
	)
	public Integer getNumberOfUse() {
		if (_numberOfUseSupplier != null) {
			numberOfUse = _numberOfUseSupplier.get();

			_numberOfUseSupplier = null;
		}

		return numberOfUse;
	}

	public void setNumberOfUse(Integer numberOfUse) {
		this.numberOfUse = numberOfUse;

		_numberOfUseSupplier = null;
	}

	@JsonIgnore
	public void setNumberOfUse(
		UnsafeSupplier<Integer, Exception> numberOfUseUnsafeSupplier) {

		_numberOfUseSupplier = () -> {
			try {
				return numberOfUseUnsafeSupplier.get();
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
		description = "Read-only counter of how many times the discount has been used."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer numberOfUse;

	@JsonIgnore
	private Supplier<Integer> _numberOfUseSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Percentage discount applied when `level` is `L1` and usePercentage is true.",
		example = "20"
	)
	@Valid
	public BigDecimal getPercentageLevel1() {
		if (_percentageLevel1Supplier != null) {
			percentageLevel1 = _percentageLevel1Supplier.get();

			_percentageLevel1Supplier = null;
		}

		return percentageLevel1;
	}

	public void setPercentageLevel1(BigDecimal percentageLevel1) {
		this.percentageLevel1 = percentageLevel1;

		_percentageLevel1Supplier = null;
	}

	@JsonIgnore
	public void setPercentageLevel1(
		UnsafeSupplier<BigDecimal, Exception> percentageLevel1UnsafeSupplier) {

		_percentageLevel1Supplier = () -> {
			try {
				return percentageLevel1UnsafeSupplier.get();
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
		description = "Percentage discount applied when `level` is `L1` and usePercentage is true."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected BigDecimal percentageLevel1;

	@JsonIgnore
	private Supplier<BigDecimal> _percentageLevel1Supplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Percentage discount applied when `level` is `L2` and usePercentage is true.",
		example = "0"
	)
	@Valid
	public BigDecimal getPercentageLevel2() {
		if (_percentageLevel2Supplier != null) {
			percentageLevel2 = _percentageLevel2Supplier.get();

			_percentageLevel2Supplier = null;
		}

		return percentageLevel2;
	}

	public void setPercentageLevel2(BigDecimal percentageLevel2) {
		this.percentageLevel2 = percentageLevel2;

		_percentageLevel2Supplier = null;
	}

	@JsonIgnore
	public void setPercentageLevel2(
		UnsafeSupplier<BigDecimal, Exception> percentageLevel2UnsafeSupplier) {

		_percentageLevel2Supplier = () -> {
			try {
				return percentageLevel2UnsafeSupplier.get();
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
		description = "Percentage discount applied when `level` is `L2` and usePercentage is true."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected BigDecimal percentageLevel2;

	@JsonIgnore
	private Supplier<BigDecimal> _percentageLevel2Supplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Percentage discount applied when `level` is `L3` and usePercentage is true.",
		example = "0"
	)
	@Valid
	public BigDecimal getPercentageLevel3() {
		if (_percentageLevel3Supplier != null) {
			percentageLevel3 = _percentageLevel3Supplier.get();

			_percentageLevel3Supplier = null;
		}

		return percentageLevel3;
	}

	public void setPercentageLevel3(BigDecimal percentageLevel3) {
		this.percentageLevel3 = percentageLevel3;

		_percentageLevel3Supplier = null;
	}

	@JsonIgnore
	public void setPercentageLevel3(
		UnsafeSupplier<BigDecimal, Exception> percentageLevel3UnsafeSupplier) {

		_percentageLevel3Supplier = () -> {
			try {
				return percentageLevel3UnsafeSupplier.get();
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
		description = "Percentage discount applied when `level` is `L3` and usePercentage is true."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected BigDecimal percentageLevel3;

	@JsonIgnore
	private Supplier<BigDecimal> _percentageLevel3Supplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Percentage discount applied when `level` is `L4` and usePercentage is true.",
		example = "0"
	)
	@Valid
	public BigDecimal getPercentageLevel4() {
		if (_percentageLevel4Supplier != null) {
			percentageLevel4 = _percentageLevel4Supplier.get();

			_percentageLevel4Supplier = null;
		}

		return percentageLevel4;
	}

	public void setPercentageLevel4(BigDecimal percentageLevel4) {
		this.percentageLevel4 = percentageLevel4;

		_percentageLevel4Supplier = null;
	}

	@JsonIgnore
	public void setPercentageLevel4(
		UnsafeSupplier<BigDecimal, Exception> percentageLevel4UnsafeSupplier) {

		_percentageLevel4Supplier = () -> {
			try {
				return percentageLevel4UnsafeSupplier.get();
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
		description = "Percentage discount applied when `level` is `L4` and usePercentage is true."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected BigDecimal percentageLevel4;

	@JsonIgnore
	private Supplier<BigDecimal> _percentageLevel4Supplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When true, all attached discount rules must pass for the discount to apply; when false, any single passing rule activates the discount.",
		example = "true"
	)
	public Boolean getRulesConjunction() {
		if (_rulesConjunctionSupplier != null) {
			rulesConjunction = _rulesConjunctionSupplier.get();

			_rulesConjunctionSupplier = null;
		}

		return rulesConjunction;
	}

	public void setRulesConjunction(Boolean rulesConjunction) {
		this.rulesConjunction = rulesConjunction;

		_rulesConjunctionSupplier = null;
	}

	@JsonIgnore
	public void setRulesConjunction(
		UnsafeSupplier<Boolean, Exception> rulesConjunctionUnsafeSupplier) {

		_rulesConjunctionSupplier = () -> {
			try {
				return rulesConjunctionUnsafeSupplier.get();
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
		description = "When true, all attached discount rules must pass for the discount to apply; when false, any single passing rule activates the discount."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean rulesConjunction;

	@JsonIgnore
	private Supplier<Boolean> _rulesConjunctionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Which order slice the discount applies to. One of `subtotal`, `total`, `products`, `categories`, `skus`, `product-groups`, `shipping`.",
		example = "subtotal"
	)
	public String getTarget() {
		if (_targetSupplier != null) {
			target = _targetSupplier.get();

			_targetSupplier = null;
		}

		return target;
	}

	public void setTarget(String target) {
		this.target = target;

		_targetSupplier = null;
	}

	@JsonIgnore
	public void setTarget(
		UnsafeSupplier<String, Exception> targetUnsafeSupplier) {

		_targetSupplier = () -> {
			try {
				return targetUnsafeSupplier.get();
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
		description = "Which order slice the discount applies to. One of `subtotal`, `total`, `products`, `categories`, `skus`, `product-groups`, `shipping`."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String target;

	@JsonIgnore
	private Supplier<String> _targetSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Human-readable name of the discount; shown in admin and used as the fallback label at checkout.",
		example = "20% Off"
	)
	public String getTitle() {
		if (_titleSupplier != null) {
			title = _titleSupplier.get();

			_titleSupplier = null;
		}

		return title;
	}

	public void setTitle(String title) {
		this.title = title;

		_titleSupplier = null;
	}

	@JsonIgnore
	public void setTitle(
		UnsafeSupplier<String, Exception> titleUnsafeSupplier) {

		_titleSupplier = () -> {
			try {
				return titleUnsafeSupplier.get();
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
		description = "Human-readable name of the discount; shown in admin and used as the fallback label at checkout."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String title;

	@JsonIgnore
	private Supplier<String> _titleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When true, the discount is gated by `couponCode` and is only applied when the buyer supplies the matching code at checkout.",
		example = "true"
	)
	public Boolean getUseCouponCode() {
		if (_useCouponCodeSupplier != null) {
			useCouponCode = _useCouponCodeSupplier.get();

			_useCouponCodeSupplier = null;
		}

		return useCouponCode;
	}

	public void setUseCouponCode(Boolean useCouponCode) {
		this.useCouponCode = useCouponCode;

		_useCouponCodeSupplier = null;
	}

	@JsonIgnore
	public void setUseCouponCode(
		UnsafeSupplier<Boolean, Exception> useCouponCodeUnsafeSupplier) {

		_useCouponCodeSupplier = () -> {
			try {
				return useCouponCodeUnsafeSupplier.get();
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
		description = "When true, the discount is gated by `couponCode` and is only applied when the buyer supplies the matching code at checkout."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean useCouponCode;

	@JsonIgnore
	private Supplier<Boolean> _useCouponCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When true, the discount payout is calculated using `percentageLevel1..4` keyed by `level`; when false, the payout is `maximumDiscountAmount`.",
		example = "true"
	)
	public Boolean getUsePercentage() {
		if (_usePercentageSupplier != null) {
			usePercentage = _usePercentageSupplier.get();

			_usePercentageSupplier = null;
		}

		return usePercentage;
	}

	public void setUsePercentage(Boolean usePercentage) {
		this.usePercentage = usePercentage;

		_usePercentageSupplier = null;
	}

	@JsonIgnore
	public void setUsePercentage(
		UnsafeSupplier<Boolean, Exception> usePercentageUnsafeSupplier) {

		_usePercentageSupplier = () -> {
			try {
				return usePercentageUnsafeSupplier.get();
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
		description = "When true, the discount payout is calculated using `percentageLevel1..4` keyed by `level`; when false, the payout is `maximumDiscountAmount`."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotNull
	protected Boolean usePercentage;

	@JsonIgnore
	private Supplier<Boolean> _usePercentageSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Discount)) {
			return false;
		}

		Discount discount = (Discount)object;

		return Objects.equals(toString(), discount.toString());
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

		String amountFormatted = getAmountFormatted();

		if (amountFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"amountFormatted\": ");

			sb.append("\"");

			sb.append(_escape(amountFormatted));

			sb.append("\"");
		}

		String couponCode = getCouponCode();

		if (couponCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"couponCode\": ");

			sb.append("\"");

			sb.append(_escape(couponCode));

			sb.append("\"");
		}

		Map<String, ?> customFields = getCustomFields();

		if (customFields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append(_toJSON(customFields));
		}

		DiscountAccountGroup[] discountAccountGroups =
			getDiscountAccountGroups();

		if (discountAccountGroups != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountAccountGroups\": ");

			sb.append("[");

			for (int i = 0; i < discountAccountGroups.length; i++) {
				sb.append(String.valueOf(discountAccountGroups[i]));

				if ((i + 1) < discountAccountGroups.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		DiscountAccount[] discountAccounts = getDiscountAccounts();

		if (discountAccounts != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountAccounts\": ");

			sb.append("[");

			for (int i = 0; i < discountAccounts.length; i++) {
				sb.append(String.valueOf(discountAccounts[i]));

				if ((i + 1) < discountAccounts.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		DiscountCategory[] discountCategories = getDiscountCategories();

		if (discountCategories != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountCategories\": ");

			sb.append("[");

			for (int i = 0; i < discountCategories.length; i++) {
				sb.append(String.valueOf(discountCategories[i]));

				if ((i + 1) < discountCategories.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		DiscountChannel[] discountChannels = getDiscountChannels();

		if (discountChannels != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountChannels\": ");

			sb.append("[");

			for (int i = 0; i < discountChannels.length; i++) {
				sb.append(String.valueOf(discountChannels[i]));

				if ((i + 1) < discountChannels.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		DiscountOrderType[] discountOrderTypes = getDiscountOrderTypes();

		if (discountOrderTypes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountOrderTypes\": ");

			sb.append("[");

			for (int i = 0; i < discountOrderTypes.length; i++) {
				sb.append(String.valueOf(discountOrderTypes[i]));

				if ((i + 1) < discountOrderTypes.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		DiscountProductGroup[] discountProductGroups =
			getDiscountProductGroups();

		if (discountProductGroups != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountProductGroups\": ");

			sb.append("[");

			for (int i = 0; i < discountProductGroups.length; i++) {
				sb.append(String.valueOf(discountProductGroups[i]));

				if ((i + 1) < discountProductGroups.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		DiscountProduct[] discountProducts = getDiscountProducts();

		if (discountProducts != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountProducts\": ");

			sb.append("[");

			for (int i = 0; i < discountProducts.length; i++) {
				sb.append(String.valueOf(discountProducts[i]));

				if ((i + 1) < discountProducts.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		DiscountRule[] discountRules = getDiscountRules();

		if (discountRules != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountRules\": ");

			sb.append("[");

			for (int i = 0; i < discountRules.length; i++) {
				sb.append(String.valueOf(discountRules[i]));

				if ((i + 1) < discountRules.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		String level = getLevel();

		if (level != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"level\": ");

			sb.append("\"");

			sb.append(_escape(level));

			sb.append("\"");
		}

		Integer limitationTimes = getLimitationTimes();

		if (limitationTimes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"limitationTimes\": ");

			sb.append(limitationTimes);
		}

		Integer limitationTimesPerAccount = getLimitationTimesPerAccount();

		if (limitationTimesPerAccount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"limitationTimesPerAccount\": ");

			sb.append(limitationTimesPerAccount);
		}

		String limitationType = getLimitationType();

		if (limitationType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"limitationType\": ");

			sb.append("\"");

			sb.append(_escape(limitationType));

			sb.append("\"");
		}

		BigDecimal maximumDiscountAmount = getMaximumDiscountAmount();

		if (maximumDiscountAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maximumDiscountAmount\": ");

			sb.append(maximumDiscountAmount);
		}

		Date modifiedDate = getModifiedDate();

		if (modifiedDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modifiedDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(modifiedDate));

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

		Integer numberOfUse = getNumberOfUse();

		if (numberOfUse != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfUse\": ");

			sb.append(numberOfUse);
		}

		BigDecimal percentageLevel1 = getPercentageLevel1();

		if (percentageLevel1 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"percentageLevel1\": ");

			sb.append(percentageLevel1);
		}

		BigDecimal percentageLevel2 = getPercentageLevel2();

		if (percentageLevel2 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"percentageLevel2\": ");

			sb.append(percentageLevel2);
		}

		BigDecimal percentageLevel3 = getPercentageLevel3();

		if (percentageLevel3 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"percentageLevel3\": ");

			sb.append(percentageLevel3);
		}

		BigDecimal percentageLevel4 = getPercentageLevel4();

		if (percentageLevel4 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"percentageLevel4\": ");

			sb.append(percentageLevel4);
		}

		Boolean rulesConjunction = getRulesConjunction();

		if (rulesConjunction != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"rulesConjunction\": ");

			sb.append(rulesConjunction);
		}

		String target = getTarget();

		if (target != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"target\": ");

			sb.append("\"");

			sb.append(_escape(target));

			sb.append("\"");
		}

		String title = getTitle();

		if (title != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(title));

			sb.append("\"");
		}

		Boolean useCouponCode = getUseCouponCode();

		if (useCouponCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"useCouponCode\": ");

			sb.append(useCouponCode);
		}

		Boolean usePercentage = getUsePercentage();

		if (usePercentage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"usePercentage\": ");

			sb.append(usePercentage);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.pricing.dto.v2_0.Discount",
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
// LIFERAY-REST-BUILDER-HASH:553668513