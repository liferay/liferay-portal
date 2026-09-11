/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.dto.v1_0;

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
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Cart line item. Binds a purchasable SKU and a quantity to its parent cart, with unit pricing, selected options, the resolved unit-of-measure tier, an optional per-item shipping address, and any bundle children.",
	value = "CartItem"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Cart line item. Binds a purchasable SKU and a quantity to its parent cart, with unit pricing, selected options, the resolved unit-of-measure tier, an optional per-item shipping address, and any bundle children.",
	requiredProperties = {"skuId"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "CartItem")
public class CartItem implements Serializable {

	public static CartItem toDTO(String json) {
		return ObjectMapperUtil.readValue(CartItem.class, json);
	}

	public static CartItem unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(CartItem.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Pre-rendered HTML `img` tag for the SKU's adaptive media thumbnail. Read-only.",
		example = "<img src='https://example.com/thumbnail.png' alt='Hand Saw'/>"
	)
	public String getAdaptiveMediaImageHTMLTag() {
		if (_adaptiveMediaImageHTMLTagSupplier != null) {
			adaptiveMediaImageHTMLTag =
				_adaptiveMediaImageHTMLTagSupplier.get();

			_adaptiveMediaImageHTMLTagSupplier = null;
		}

		return adaptiveMediaImageHTMLTag;
	}

	public void setAdaptiveMediaImageHTMLTag(String adaptiveMediaImageHTMLTag) {
		this.adaptiveMediaImageHTMLTag = adaptiveMediaImageHTMLTag;

		_adaptiveMediaImageHTMLTagSupplier = null;
	}

	@JsonIgnore
	public void setAdaptiveMediaImageHTMLTag(
		UnsafeSupplier<String, Exception>
			adaptiveMediaImageHTMLTagUnsafeSupplier) {

		_adaptiveMediaImageHTMLTagSupplier = () -> {
			try {
				return adaptiveMediaImageHTMLTagUnsafeSupplier.get();
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
		description = "Pre-rendered HTML `img` tag for the SKU's adaptive media thumbnail. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String adaptiveMediaImageHTMLTag;

	@JsonIgnore
	private Supplier<String> _adaptiveMediaImageHTMLTagSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Nested child cart items when this item is a bundle parent."
	)
	@Valid
	public CartItem[] getCartItems() {
		if (_cartItemsSupplier != null) {
			cartItems = _cartItemsSupplier.get();

			_cartItemsSupplier = null;
		}

		return cartItems;
	}

	public void setCartItems(CartItem[] cartItems) {
		this.cartItems = cartItems;

		_cartItemsSupplier = null;
	}

	@JsonIgnore
	public void setCartItems(
		UnsafeSupplier<CartItem[], Exception> cartItemsUnsafeSupplier) {

		_cartItemsSupplier = () -> {
			try {
				return cartItemsUnsafeSupplier.get();
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
		description = "Nested child cart items when this item is a bundle parent."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected CartItem[] cartItems;

	@JsonIgnore
	private Supplier<CartItem[]> _cartItemsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Custom attribute bag for the cart item. Keys are the configured custom-field names; values are typed by the custom-field definition.",
		example = "{engravingText=For Alex, giftWrap=true}"
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
		description = "Custom attribute bag for the cart item. Keys are the configured custom-field names; values are typed by the custom-field definition."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, ?> customFields;

	@JsonIgnore
	private Supplier<Map<String, ?>> _customFieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		deprecated = true,
		description = "Deprecated -- use the delivery group name field instead. Retained for backward compatibility; both fields are written but only the new field is honored on create and update.",
		example = "warehouse-east"
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
	@GraphQLField(
		description = "Deprecated -- use the delivery group name field instead. Retained for backward compatibility; both fields are written but only the new field is honored on create and update."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String deliveryGroup;

	@JsonIgnore
	private Supplier<String> _deliveryGroupSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Warehouse or fulfillment group name used to split shipments across origins.",
		example = "warehouse-east"
	)
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

	@GraphQLField(
		description = "Warehouse or fulfillment group name used to split shipments across origins."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String deliveryGroupName;

	@JsonIgnore
	private Supplier<String> _deliveryGroupNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Validation messages collected during item-level validation -- for example, quantity outside the configured bounds.",
		example = "[Quantity must be at least 1., SKU is no longer available.]"
	)
	public String[] getErrorMessages() {
		if (_errorMessagesSupplier != null) {
			errorMessages = _errorMessagesSupplier.get();

			_errorMessagesSupplier = null;
		}

		return errorMessages;
	}

	public void setErrorMessages(String[] errorMessages) {
		this.errorMessages = errorMessages;

		_errorMessagesSupplier = null;
	}

	@JsonIgnore
	public void setErrorMessages(
		UnsafeSupplier<String[], Exception> errorMessagesUnsafeSupplier) {

		_errorMessagesSupplier = () -> {
			try {
				return errorMessagesUnsafeSupplier.get();
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
		description = "Validation messages collected during item-level validation -- for example, quantity outside the configured bounds."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] errorMessages;

	@JsonIgnore
	private Supplier<String[]> _errorMessagesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Idempotency key for the cart item; unique per item within the company. Read-only on the wire and set automatically when omitted.",
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
		description = "Idempotency key for the cart item; unique per item within the company. Read-only on the wire and set automatically when omitted."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the cart item (FK identifier). Read-only.",
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
		description = "Reference to the cart item (FK identifier). Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Localized product display name as of the item snapshot. Read-only.",
		example = "Hand Saw"
	)
	public String getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(String name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
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
		description = "Localized product display name as of the item snapshot. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "JSON-serialized snapshot of the buyer-selected product option values.",
		example = "[{key:color,value:red}]"
	)
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

	@GraphQLField(
		description = "JSON-serialized snapshot of the buyer-selected product option values."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String options;

	@JsonIgnore
	private Supplier<String> _optionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the parent cart item when this row is a bundle child (FK identifier); 0 for top-level items. Read-only.",
		example = "0"
	)
	public Long getParentCartItemId() {
		if (_parentCartItemIdSupplier != null) {
			parentCartItemId = _parentCartItemIdSupplier.get();

			_parentCartItemIdSupplier = null;
		}

		return parentCartItemId;
	}

	public void setParentCartItemId(Long parentCartItemId) {
		this.parentCartItemId = parentCartItemId;

		_parentCartItemIdSupplier = null;
	}

	@JsonIgnore
	public void setParentCartItemId(
		UnsafeSupplier<Long, Exception> parentCartItemIdUnsafeSupplier) {

		_parentCartItemIdSupplier = () -> {
			try {
				return parentCartItemIdUnsafeSupplier.get();
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
		description = "Reference to the parent cart item when this row is a bundle child (FK identifier); 0 for top-level items. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long parentCartItemId;

	@JsonIgnore
	private Supplier<Long> _parentCartItemIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Price getPrice() {
		if (_priceSupplier != null) {
			price = _priceSupplier.get();

			_priceSupplier = null;
		}

		return price;
	}

	public void setPrice(Price price) {
		this.price = price;

		_priceSupplier = null;
	}

	@JsonIgnore
	public void setPrice(UnsafeSupplier<Price, Exception> priceUnsafeSupplier) {
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
	protected Price price;

	@JsonIgnore
	private Supplier<Price> _priceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the product the SKU belongs to (FK identifier).",
		example = "10130"
	)
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

	@GraphQLField(
		description = "Reference to the product the SKU belongs to (FK identifier)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long productId;

	@JsonIgnore
	private Supplier<Long> _productIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Localized text. Map keys are locale codes; values are the storefront product URLs.",
		example = "{en_US=https://example.com/products/hand-saw, hr_HR=https://example.com/hr/products/hand-saw, hu_HU=https://example.com/hu/products/hand-saw}"
	)
	@Valid
	public Map<String, String> getProductURLs() {
		if (_productURLsSupplier != null) {
			productURLs = _productURLsSupplier.get();

			_productURLsSupplier = null;
		}

		return productURLs;
	}

	public void setProductURLs(Map<String, String> productURLs) {
		this.productURLs = productURLs;

		_productURLsSupplier = null;
	}

	@JsonIgnore
	public void setProductURLs(
		UnsafeSupplier<Map<String, String>, Exception>
			productURLsUnsafeSupplier) {

		_productURLsSupplier = () -> {
			try {
				return productURLsUnsafeSupplier.get();
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
		description = "Localized text. Map keys are locale codes; values are the storefront product URLs."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, String> productURLs;

	@JsonIgnore
	private Supplier<Map<String, String>> _productURLsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Ordered quantity expressed in the item's unit of measure. Validated against the item settings -- minimum, maximum, multiple, allowed quantities.",
		example = "10.1"
	)
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

	@GraphQLField(
		description = "Ordered quantity expressed in the item's unit of measure. Validated against the item settings -- minimum, maximum, multiple, allowed quantities."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected BigDecimal quantity;

	@JsonIgnore
	private Supplier<BigDecimal> _quantitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Original SKU code when this line replaces another SKU after an out-of-stock substitution or similar swap. Read-only.",
		example = "12341234"
	)
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

	@GraphQLField(
		description = "Original SKU code when this line replaces another SKU after an out-of-stock substitution or similar swap. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String replacedSku;

	@JsonIgnore
	private Supplier<String> _replacedSkuSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the replaced SKU. Alternative to the replaced-SKU ID.",
		example = "AB-34098-789-N"
	)
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

	@GraphQLField(
		description = "External reference code of the replaced SKU. Alternative to the replaced-SKU ID."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String replacedSkuExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _replacedSkuExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the SKU the original line pointed to before the replacement (FK identifier).",
		example = "10131"
	)
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

	@GraphQLField(
		description = "Reference to the SKU the original line pointed to before the replacement (FK identifier)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long replacedSkuId;

	@JsonIgnore
	private Supplier<Long> _replacedSkuIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Item-specific requested delivery date and time in ISO 8601. Overrides the cart-level requested delivery date and time when set.",
		example = "2017-07-21"
	)
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

	@GraphQLField(
		description = "Item-specific requested delivery date and time in ISO 8601. Overrides the cart-level requested delivery date and time when set."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date requestedDeliveryDate;

	@JsonIgnore
	private Supplier<Date> _requestedDeliveryDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Settings getSettings() {
		if (_settingsSupplier != null) {
			settings = _settingsSupplier.get();

			_settingsSupplier = null;
		}

		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;

		_settingsSupplier = null;
	}

	@JsonIgnore
	public void setSettings(
		UnsafeSupplier<Settings, Exception> settingsUnsafeSupplier) {

		_settingsSupplier = () -> {
			try {
				return settingsUnsafeSupplier.get();
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
	protected Settings settings;

	@JsonIgnore
	private Supplier<Settings> _settingsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Address getShippingAddress() {
		if (_shippingAddressSupplier != null) {
			shippingAddress = _shippingAddressSupplier.get();

			_shippingAddressSupplier = null;
		}

		return shippingAddress;
	}

	public void setShippingAddress(Address shippingAddress) {
		this.shippingAddress = shippingAddress;

		_shippingAddressSupplier = null;
	}

	@JsonIgnore
	public void setShippingAddress(
		UnsafeSupplier<Address, Exception> shippingAddressUnsafeSupplier) {

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
	protected Address shippingAddress;

	@JsonIgnore
	private Supplier<Address> _shippingAddressSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the address to use as the item-level shipping address.",
		example = "AB-34098-789-N"
	)
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

	@GraphQLField(
		description = "External reference code of the address to use as the item-level shipping address."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String shippingAddressExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _shippingAddressExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the address used as item-level shipping address (FK identifier). Overrides the cart-level shipping address for this line.",
		example = "10131"
	)
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

	@GraphQLField(
		description = "Reference to the address used as item-level shipping address (FK identifier). Overrides the cart-level shipping address for this line."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long shippingAddressId;

	@JsonIgnore
	private Supplier<Long> _shippingAddressIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "SKU code of the purchased variant. Read-only.",
		example = "HS-001"
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

	@GraphQLField(description = "SKU code of the purchased variant. Read-only.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String sku;

	@JsonIgnore
	private Supplier<String> _skuSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the SKU (FK identifier). Required on create.",
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
		description = "Reference to the SKU (FK identifier). Required on create."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotNull
	protected Long skuId;

	@JsonIgnore
	private Supplier<Long> _skuIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public SkuUnitOfMeasure getSkuUnitOfMeasure() {
		if (_skuUnitOfMeasureSupplier != null) {
			skuUnitOfMeasure = _skuUnitOfMeasureSupplier.get();

			_skuUnitOfMeasureSupplier = null;
		}

		return skuUnitOfMeasure;
	}

	public void setSkuUnitOfMeasure(SkuUnitOfMeasure skuUnitOfMeasure) {
		this.skuUnitOfMeasure = skuUnitOfMeasure;

		_skuUnitOfMeasureSupplier = null;
	}

	@JsonIgnore
	public void setSkuUnitOfMeasure(
		UnsafeSupplier<SkuUnitOfMeasure, Exception>
			skuUnitOfMeasureUnsafeSupplier) {

		_skuUnitOfMeasureSupplier = () -> {
			try {
				return skuUnitOfMeasureUnsafeSupplier.get();
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
	protected SkuUnitOfMeasure skuUnitOfMeasure;

	@JsonIgnore
	private Supplier<SkuUnitOfMeasure> _skuUnitOfMeasureSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When true, the SKU is configured for subscription-based delivery. Read-only.",
		example = "true"
	)
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

	@GraphQLField(
		description = "When true, the SKU is configured for subscription-based delivery. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Boolean subscription;

	@JsonIgnore
	private Supplier<Boolean> _subscriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "URL of the SKU's adaptive media thumbnail. Read-only.",
		example = "https://example.com/thumbnail.png"
	)
	public String getThumbnail() {
		if (_thumbnailSupplier != null) {
			thumbnail = _thumbnailSupplier.get();

			_thumbnailSupplier = null;
		}

		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;

		_thumbnailSupplier = null;
	}

	@JsonIgnore
	public void setThumbnail(
		UnsafeSupplier<String, Exception> thumbnailUnsafeSupplier) {

		_thumbnailSupplier = () -> {
			try {
				return thumbnailUnsafeSupplier.get();
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
		description = "URL of the SKU's adaptive media thumbnail. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String thumbnail;

	@JsonIgnore
	private Supplier<String> _thumbnailSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Localized unit-of-measure label. Read-only; derived from the resolved unit-of-measure tier name.",
		example = "Pallet"
	)
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

	@GraphQLField(
		description = "Localized unit-of-measure label. Read-only; derived from the resolved unit-of-measure tier name."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String unitOfMeasure;

	@JsonIgnore
	private Supplier<String> _unitOfMeasureSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Aggregate validation flag for the item. Read-only.",
		example = "true"
	)
	public Boolean getValid() {
		if (_validSupplier != null) {
			valid = _validSupplier.get();

			_validSupplier = null;
		}

		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;

		_validSupplier = null;
	}

	@JsonIgnore
	public void setValid(
		UnsafeSupplier<Boolean, Exception> validUnsafeSupplier) {

		_validSupplier = () -> {
			try {
				return validUnsafeSupplier.get();
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
		description = "Aggregate validation flag for the item. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Boolean valid;

	@JsonIgnore
	private Supplier<Boolean> _validSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CartItem)) {
			return false;
		}

		CartItem cartItem = (CartItem)object;

		return Objects.equals(toString(), cartItem.toString());
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

		String adaptiveMediaImageHTMLTag = getAdaptiveMediaImageHTMLTag();

		if (adaptiveMediaImageHTMLTag != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"adaptiveMediaImageHTMLTag\": ");

			sb.append("\"");

			sb.append(_escape(adaptiveMediaImageHTMLTag));

			sb.append("\"");
		}

		CartItem[] cartItems = getCartItems();

		if (cartItems != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cartItems\": ");

			sb.append("[");

			for (int i = 0; i < cartItems.length; i++) {
				sb.append(String.valueOf(cartItems[i]));

				if ((i + 1) < cartItems.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Map<String, ?> customFields = getCustomFields();

		if (customFields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append(_toJSON(customFields));
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

		String[] errorMessages = getErrorMessages();

		if (errorMessages != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errorMessages\": ");

			sb.append("[");

			for (int i = 0; i < errorMessages.length; i++) {
				sb.append("\"");

				sb.append(_escape(errorMessages[i]));

				sb.append("\"");

				if ((i + 1) < errorMessages.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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

		String name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(name));

			sb.append("\"");
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

		Long parentCartItemId = getParentCartItemId();

		if (parentCartItemId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentCartItemId\": ");

			sb.append(parentCartItemId);
		}

		Price price = getPrice();

		if (price != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"price\": ");

			sb.append(String.valueOf(price));
		}

		Long productId = getProductId();

		if (productId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productId\": ");

			sb.append(productId);
		}

		Map<String, String> productURLs = getProductURLs();

		if (productURLs != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productURLs\": ");

			sb.append(_toJSON(productURLs));
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

		Settings settings = getSettings();

		if (settings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"settings\": ");

			sb.append(String.valueOf(settings));
		}

		Address shippingAddress = getShippingAddress();

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

		Long skuId = getSkuId();

		if (skuId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuId\": ");

			sb.append(skuId);
		}

		SkuUnitOfMeasure skuUnitOfMeasure = getSkuUnitOfMeasure();

		if (skuUnitOfMeasure != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuUnitOfMeasure\": ");

			sb.append(String.valueOf(skuUnitOfMeasure));
		}

		Boolean subscription = getSubscription();

		if (subscription != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subscription\": ");

			sb.append(subscription);
		}

		String thumbnail = getThumbnail();

		if (thumbnail != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"thumbnail\": ");

			sb.append("\"");

			sb.append(_escape(thumbnail));

			sb.append("\"");
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

		Boolean valid = getValid();

		if (valid != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"valid\": ");

			sb.append(valid);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.delivery.cart.dto.v1_0.CartItem",
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
// LIFERAY-REST-BUILDER-HASH:-2036201989