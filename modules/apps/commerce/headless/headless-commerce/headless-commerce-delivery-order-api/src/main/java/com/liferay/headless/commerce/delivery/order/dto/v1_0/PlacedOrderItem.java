/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.dto.v1_0;

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
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "A top-level CommerceOrderItem on a placed order. Child items are exposed inline via the placedOrderItems collection, shipment splits via placedOrderItemShipments, and virtual downloads via virtualItems and virtualItemURLs.",
	value = "PlacedOrderItem"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PlacedOrderItem")
public class PlacedOrderItem implements Serializable {

	public static PlacedOrderItem toDTO(String json) {
		return ObjectMapperUtil.readValue(PlacedOrderItem.class, json);
	}

	public static PlacedOrderItem unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(PlacedOrderItem.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Pre-rendered Adaptive Media img tag for the item's CPInstance."
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
		description = "Pre-rendered Adaptive Media img tag for the item's CPInstance."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String adaptiveMediaImageHTMLTag;

	@JsonIgnore
	private Supplier<String> _adaptiveMediaImageHTMLTagSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Expando attributes from the CommerceOrderItem ExpandoBridge -- map of attribute name to attribute value."
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
		description = "Expando attributes from the CommerceOrderItem ExpandoBridge -- map of attribute name to attribute value."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, ?> customFields;

	@JsonIgnore
	private Supplier<Map<String, ?>> _customFieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		deprecated = true,
		description = "Deprecated -- use deliveryGroupName instead. Both properties share the same backing value, CommerceOrderItem.getDeliveryGroupName()."
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
		description = "Deprecated -- use deliveryGroupName instead. Both properties share the same backing value, CommerceOrderItem.getDeliveryGroupName()."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String deliveryGroup;

	@JsonIgnore
	private Supplier<String> _deliveryGroupSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Delivery group name from CommerceOrderItem.getDeliveryGroupName()."
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
		description = "Delivery group name from CommerceOrderItem.getDeliveryGroupName()."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String deliveryGroupName;

	@JsonIgnore
	private Supplier<String> _deliveryGroupNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When the underlying CPInstance no longer exists, the single message the-product-is-no-longer-available. Null otherwise."
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
		description = "When the underlying CPInstance no longer exists, the single message the-product-is-no-longer-available. Null otherwise."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String[] errorMessages;

	@JsonIgnore
	private Supplier<String[]> _errorMessagesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Idempotency key on the CommerceOrderItem -- the caller-supplied external reference code.",
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
		description = "Idempotency key on the CommerceOrderItem -- the caller-supplied external reference code."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Primary key -- the CommerceOrderItem id."
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

	@GraphQLField(description = "Primary key -- the CommerceOrderItem id.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Localized item name in the requested locale."
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

	@GraphQLField(description = "Localized item name in the requested locale.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "JSON blob of the item's selected product options, from CommerceOrderItem.getJson()."
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
		description = "JSON blob of the item's selected product options, from CommerceOrderItem.getJson()."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String options;

	@JsonIgnore
	private Supplier<String> _optionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the parent CommerceOrderItem entity (FK). Zero when this is a top-level item."
	)
	public Long getParentOrderItemId() {
		if (_parentOrderItemIdSupplier != null) {
			parentOrderItemId = _parentOrderItemIdSupplier.get();

			_parentOrderItemIdSupplier = null;
		}

		return parentOrderItemId;
	}

	public void setParentOrderItemId(Long parentOrderItemId) {
		this.parentOrderItemId = parentOrderItemId;

		_parentOrderItemIdSupplier = null;
	}

	@JsonIgnore
	public void setParentOrderItemId(
		UnsafeSupplier<Long, Exception> parentOrderItemIdUnsafeSupplier) {

		_parentOrderItemIdSupplier = () -> {
			try {
				return parentOrderItemIdUnsafeSupplier.get();
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
		description = "Reference to the parent CommerceOrderItem entity (FK). Zero when this is a top-level item."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long parentOrderItemId;

	@JsonIgnore
	private Supplier<Long> _parentOrderItemIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Embedded collection of shipments for this item -- populated by the PlacedOrderItemShipment nested field resolver."
	)
	@Valid
	public PlacedOrderItemShipment[] getPlacedOrderItemShipments() {
		if (_placedOrderItemShipmentsSupplier != null) {
			placedOrderItemShipments = _placedOrderItemShipmentsSupplier.get();

			_placedOrderItemShipmentsSupplier = null;
		}

		return placedOrderItemShipments;
	}

	public void setPlacedOrderItemShipments(
		PlacedOrderItemShipment[] placedOrderItemShipments) {

		this.placedOrderItemShipments = placedOrderItemShipments;

		_placedOrderItemShipmentsSupplier = null;
	}

	@JsonIgnore
	public void setPlacedOrderItemShipments(
		UnsafeSupplier<PlacedOrderItemShipment[], Exception>
			placedOrderItemShipmentsUnsafeSupplier) {

		_placedOrderItemShipmentsSupplier = () -> {
			try {
				return placedOrderItemShipmentsUnsafeSupplier.get();
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
		description = "Embedded collection of shipments for this item -- populated by the PlacedOrderItemShipment nested field resolver."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected PlacedOrderItemShipment[] placedOrderItemShipments;

	@JsonIgnore
	private Supplier<PlacedOrderItemShipment[]>
		_placedOrderItemShipmentsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Embedded collection of child placed order items, transformed from CommerceOrderItem.getChildCommerceOrderItems()."
	)
	@Valid
	public PlacedOrderItem[] getPlacedOrderItems() {
		if (_placedOrderItemsSupplier != null) {
			placedOrderItems = _placedOrderItemsSupplier.get();

			_placedOrderItemsSupplier = null;
		}

		return placedOrderItems;
	}

	public void setPlacedOrderItems(PlacedOrderItem[] placedOrderItems) {
		this.placedOrderItems = placedOrderItems;

		_placedOrderItemsSupplier = null;
	}

	@JsonIgnore
	public void setPlacedOrderItems(
		UnsafeSupplier<PlacedOrderItem[], Exception>
			placedOrderItemsUnsafeSupplier) {

		_placedOrderItemsSupplier = () -> {
			try {
				return placedOrderItemsUnsafeSupplier.get();
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
		description = "Embedded collection of child placed order items, transformed from CommerceOrderItem.getChildCommerceOrderItems()."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected PlacedOrderItem[] placedOrderItems;

	@JsonIgnore
	private Supplier<PlacedOrderItem[]> _placedOrderItemsSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Price price;

	@JsonIgnore
	private Supplier<Price> _priceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the parent CProduct entity (FK)."
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

	@GraphQLField(description = "Reference to the parent CProduct entity (FK).")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long productId;

	@JsonIgnore
	private Supplier<Long> _productIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Localized product URL title. Map keys are locale codes (for example, en_US, it_IT); values are the URL title strings sourced from CPDefinitionLocalService.getUrlTitleMap.",
		example = "{en_US=product-url-us, hr_HR=product-url-hr, hu_HU=product-url-hu}"
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
		description = "Localized product URL title. Map keys are locale codes (for example, en_US, it_IT); values are the URL title strings sourced from CPDefinitionLocalService.getUrlTitleMap."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, String> productURLs;

	@JsonIgnore
	private Supplier<Map<String, String>> _productURLsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Localized formatted quantity, computed by CommerceQuantityFormatter.format against the item's CPInstance and unit of measure.",
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
		description = "Localized formatted quantity, computed by CommerceQuantityFormatter.format against the item's CPInstance and unit of measure."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected BigDecimal quantity;

	@JsonIgnore
	private Supplier<BigDecimal> _quantitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "SKU of the original item this row replaced when the item was swapped at fulfillment."
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
		description = "SKU of the original item this row replaced when the item was swapped at fulfillment."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String replacedSku;

	@JsonIgnore
	private Supplier<String> _replacedSkuSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Requested delivery date on the item."
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

	@GraphQLField(description = "Requested delivery date on the item.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Settings settings;

	@JsonIgnore
	private Supplier<Settings> _settingsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the CommerceAddress linked to the item via shippingAddressId.",
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
		description = "External reference code of the CommerceAddress linked to the item via shippingAddressId."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String shippingAddressExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _shippingAddressExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the per-item CommerceAddress entity (FK)."
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
		description = "Reference to the per-item CommerceAddress entity (FK)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long shippingAddressId;

	@JsonIgnore
	private Supplier<Long> _shippingAddressIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "SKU string from CommerceOrderItem.getSku()."
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

	@GraphQLField(description = "SKU string from CommerceOrderItem.getSku().")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String sku;

	@JsonIgnore
	private Supplier<String> _skuSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the CPInstance entity (FK) for the SKU."
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
		description = "Reference to the CPInstance entity (FK) for the SKU."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long skuId;

	@JsonIgnore
	private Supplier<Long> _skuIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When true the item is part of a subscription, from CommerceOrderItem.isSubscription().",
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
		description = "When true the item is part of a subscription, from CommerceOrderItem.isSubscription()."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Boolean subscription;

	@JsonIgnore
	private Supplier<Boolean> _subscriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "URL of the item thumbnail, from CPInstanceHelper.getCPInstanceThumbnailSrc."
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
		description = "URL of the item thumbnail, from CPInstanceHelper.getCPInstanceThumbnailSrc."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String thumbnail;

	@JsonIgnore
	private Supplier<String> _thumbnailSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Localized name of the chosen CPInstanceUnitOfMeasure."
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
		description = "Localized name of the chosen CPInstanceUnitOfMeasure."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String unitOfMeasure;

	@JsonIgnore
	private Supplier<String> _unitOfMeasureSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Key of the chosen CPInstanceUnitOfMeasure.",
		example = "m"
	)
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

	@GraphQLField(description = "Key of the chosen CPInstanceUnitOfMeasure.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String unitOfMeasureKey;

	@JsonIgnore
	private Supplier<String> _unitOfMeasureKeySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Declared validity flag. Declared on the schema; not currently populated by this converter."
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
		description = "Declared validity flag. Declared on the schema; not currently populated by this converter."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Boolean valid;

	@JsonIgnore
	private Supplier<Boolean> _validSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "URLs of the first virtual-item file entry attached to the order item. Falls back to CommerceMediaResolver.getDownloadVirtualOrderItemURL when no entry stores a direct URL."
	)
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

	@GraphQLField(
		description = "URLs of the first virtual-item file entry attached to the order item. Falls back to CommerceMediaResolver.getDownloadVirtualOrderItemURL when no entry stores a direct URL."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String[] virtualItemURLs;

	@JsonIgnore
	private Supplier<String[]> _virtualItemURLsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Full list of virtual-item file entries on the order item. See the VirtualItem schema."
	)
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

	@GraphQLField(
		description = "Full list of virtual-item file entries on the order item. See the VirtualItem schema."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected VirtualItem[] virtualItems;

	@JsonIgnore
	private Supplier<VirtualItem[]> _virtualItemsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PlacedOrderItem)) {
			return false;
		}

		PlacedOrderItem placedOrderItem = (PlacedOrderItem)object;

		return Objects.equals(toString(), placedOrderItem.toString());
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

		Long parentOrderItemId = getParentOrderItemId();

		if (parentOrderItemId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentOrderItemId\": ");

			sb.append(parentOrderItemId);
		}

		PlacedOrderItemShipment[] placedOrderItemShipments =
			getPlacedOrderItemShipments();

		if (placedOrderItemShipments != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"placedOrderItemShipments\": ");

			sb.append("[");

			for (int i = 0; i < placedOrderItemShipments.length; i++) {
				sb.append(String.valueOf(placedOrderItemShipments[i]));

				if ((i + 1) < placedOrderItemShipments.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		PlacedOrderItem[] placedOrderItems = getPlacedOrderItems();

		if (placedOrderItems != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"placedOrderItems\": ");

			sb.append("[");

			for (int i = 0; i < placedOrderItems.length; i++) {
				sb.append(String.valueOf(placedOrderItems[i]));

				if ((i + 1) < placedOrderItems.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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

		Boolean valid = getValid();

		if (valid != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"valid\": ");

			sb.append(valid);
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
		defaultValue = "com.liferay.headless.commerce.delivery.order.dto.v1_0.PlacedOrderItem",
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
// LIFERAY-REST-BUILDER-HASH:1689457304