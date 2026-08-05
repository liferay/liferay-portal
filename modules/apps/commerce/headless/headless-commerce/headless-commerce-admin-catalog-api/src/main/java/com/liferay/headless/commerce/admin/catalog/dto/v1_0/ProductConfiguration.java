/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

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
	description = "Inventory, ordering, and fulfillment configuration for a single product or for a reusable configuration template; bundles inventory engine selection, order quantity rules, low-stock behavior, shipping dimensions, and tax classification under one configuration list entry.",
	value = "ProductConfiguration"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Inventory, ordering, and fulfillment configuration for a single product or for a reusable configuration template; bundles inventory engine selection, order quantity rules, low-stock behavior, shipping dimensions, and tax classification under one configuration list entry."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ProductConfiguration")
public class ProductConfiguration implements Serializable {

	public static ProductConfiguration toDTO(String json) {
		return ObjectMapperUtil.readValue(ProductConfiguration.class, json);
	}

	public static ProductConfiguration unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			ProductConfiguration.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Map of available operations for the current user keyed by action name such as `get`, `update`, and `delete`; each entry carries the URL template and HTTP method; the `delete` action is omitted on the master entry; read-only."
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
		description = "Map of available operations for the current user keyed by action name such as `get`, `update`, and `delete`; each entry carries the URL template and HTTP method; the `delete` action is omitted on the master entry; read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether customers can place orders when the item is out of stock.",
		example = "true"
	)
	public Boolean getAllowBackOrder() {
		if (_allowBackOrderSupplier != null) {
			allowBackOrder = _allowBackOrderSupplier.get();

			_allowBackOrderSupplier = null;
		}

		return allowBackOrder;
	}

	public void setAllowBackOrder(Boolean allowBackOrder) {
		this.allowBackOrder = allowBackOrder;

		_allowBackOrderSupplier = null;
	}

	@JsonIgnore
	public void setAllowBackOrder(
		UnsafeSupplier<Boolean, Exception> allowBackOrderUnsafeSupplier) {

		_allowBackOrderSupplier = () -> {
			try {
				return allowBackOrderUnsafeSupplier.get();
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
		description = "Whether customers can place orders when the item is out of stock."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean allowBackOrder;

	@JsonIgnore
	private Supplier<Boolean> _allowBackOrderSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Discrete order quantities the customer may pick from (for example 10, 20, 30); supplied as a space-separated list of numbers and exposed as a numeric array; when set, this list overrides the minimum, maximum, and step combination.",
		example = "[10, 20, 30, 40]"
	)
	@Valid
	public BigDecimal[] getAllowedOrderQuantities() {
		if (_allowedOrderQuantitiesSupplier != null) {
			allowedOrderQuantities = _allowedOrderQuantitiesSupplier.get();

			_allowedOrderQuantitiesSupplier = null;
		}

		return allowedOrderQuantities;
	}

	public void setAllowedOrderQuantities(BigDecimal[] allowedOrderQuantities) {
		this.allowedOrderQuantities = allowedOrderQuantities;

		_allowedOrderQuantitiesSupplier = null;
	}

	@JsonIgnore
	public void setAllowedOrderQuantities(
		UnsafeSupplier<BigDecimal[], Exception>
			allowedOrderQuantitiesUnsafeSupplier) {

		_allowedOrderQuantitiesSupplier = () -> {
			try {
				return allowedOrderQuantitiesUnsafeSupplier.get();
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
		description = "Discrete order quantities the customer may pick from (for example 10, 20, 30); supplied as a space-separated list of numbers and exposed as a numeric array; when set, this list overrides the minimum, maximum, and step combination."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected BigDecimal[] allowedOrderQuantities;

	@JsonIgnore
	private Supplier<BigDecimal[]> _allowedOrderQuantitiesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the linked availability estimate; resolved before `availabilityEstimateId` and never falling back to it. An unresolved code fails the request, except during an import, where it creates an empty availability estimate to be completed later.",
		example = "AB-34098-789-N"
	)
	public String getAvailabilityEstimateExternalReferenceCode() {
		if (_availabilityEstimateExternalReferenceCodeSupplier != null) {
			availabilityEstimateExternalReferenceCode =
				_availabilityEstimateExternalReferenceCodeSupplier.get();

			_availabilityEstimateExternalReferenceCodeSupplier = null;
		}

		return availabilityEstimateExternalReferenceCode;
	}

	public void setAvailabilityEstimateExternalReferenceCode(
		String availabilityEstimateExternalReferenceCode) {

		this.availabilityEstimateExternalReferenceCode =
			availabilityEstimateExternalReferenceCode;

		_availabilityEstimateExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setAvailabilityEstimateExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			availabilityEstimateExternalReferenceCodeUnsafeSupplier) {

		_availabilityEstimateExternalReferenceCodeSupplier = () -> {
			try {
				return availabilityEstimateExternalReferenceCodeUnsafeSupplier.
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

	@GraphQLField(
		description = "External reference code of the linked availability estimate; resolved before `availabilityEstimateId` and never falling back to it. An unresolved code fails the request, except during an import, where it creates an empty availability estimate to be completed later."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String availabilityEstimateExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _availabilityEstimateExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Identifier of the availability estimate label assigned on create or update and used to render `availabilityEstimateName`.",
		example = "31130"
	)
	public Long getAvailabilityEstimateId() {
		if (_availabilityEstimateIdSupplier != null) {
			availabilityEstimateId = _availabilityEstimateIdSupplier.get();

			_availabilityEstimateIdSupplier = null;
		}

		return availabilityEstimateId;
	}

	public void setAvailabilityEstimateId(Long availabilityEstimateId) {
		this.availabilityEstimateId = availabilityEstimateId;

		_availabilityEstimateIdSupplier = null;
	}

	@JsonIgnore
	public void setAvailabilityEstimateId(
		UnsafeSupplier<Long, Exception> availabilityEstimateIdUnsafeSupplier) {

		_availabilityEstimateIdSupplier = () -> {
			try {
				return availabilityEstimateIdUnsafeSupplier.get();
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
		description = "Identifier of the availability estimate label assigned on create or update and used to render `availabilityEstimateName`."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long availabilityEstimateId;

	@JsonIgnore
	private Supplier<Long> _availabilityEstimateIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Localized label for the availability estimate; map keys are locale codes such as `en_US` or `it_IT` and values are the translated strings.",
		example = "{en_US=3-5 Days, it_IT=3-5 Giorni}"
	)
	@Valid
	public Map<String, String> getAvailabilityEstimateName() {
		if (_availabilityEstimateNameSupplier != null) {
			availabilityEstimateName = _availabilityEstimateNameSupplier.get();

			_availabilityEstimateNameSupplier = null;
		}

		return availabilityEstimateName;
	}

	public void setAvailabilityEstimateName(
		Map<String, String> availabilityEstimateName) {

		this.availabilityEstimateName = availabilityEstimateName;

		_availabilityEstimateNameSupplier = null;
	}

	@JsonIgnore
	public void setAvailabilityEstimateName(
		UnsafeSupplier<Map<String, String>, Exception>
			availabilityEstimateNameUnsafeSupplier) {

		_availabilityEstimateNameSupplier = () -> {
			try {
				return availabilityEstimateNameUnsafeSupplier.get();
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
		description = "Localized label for the availability estimate; map keys are locale codes such as `en_US` or `it_IT` and values are the translated strings."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> availabilityEstimateName;

	@JsonIgnore
	private Supplier<Map<String, String>> _availabilityEstimateNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Names of the fields whose stored values differ from the parent template snapshot; populated only when the `showDifferences` query parameter is true and the entry has a parent template; read-only.",
		example = "[minOrderQuantity, maxOrderQuantity]"
	)
	public String[] getDifferences() {
		if (_differencesSupplier != null) {
			differences = _differencesSupplier.get();

			_differencesSupplier = null;
		}

		return differences;
	}

	public void setDifferences(String[] differences) {
		this.differences = differences;

		_differencesSupplier = null;
	}

	@JsonIgnore
	public void setDifferences(
		UnsafeSupplier<String[], Exception> differencesUnsafeSupplier) {

		_differencesSupplier = () -> {
			try {
				return differencesUnsafeSupplier.get();
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
		description = "Names of the fields whose stored values differ from the parent template snapshot; populated only when the `showDifferences` query parameter is true and the entry has a parent template; read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] differences;

	@JsonIgnore
	private Supplier<String[]> _differencesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether the storefront displays availability information such as in stock, out of stock, or estimate to customers.",
		example = "true"
	)
	public Boolean getDisplayAvailability() {
		if (_displayAvailabilitySupplier != null) {
			displayAvailability = _displayAvailabilitySupplier.get();

			_displayAvailabilitySupplier = null;
		}

		return displayAvailability;
	}

	public void setDisplayAvailability(Boolean displayAvailability) {
		this.displayAvailability = displayAvailability;

		_displayAvailabilitySupplier = null;
	}

	@JsonIgnore
	public void setDisplayAvailability(
		UnsafeSupplier<Boolean, Exception> displayAvailabilityUnsafeSupplier) {

		_displayAvailabilitySupplier = () -> {
			try {
				return displayAvailabilityUnsafeSupplier.get();
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
		description = "Whether the storefront displays availability information such as in stock, out of stock, or estimate to customers."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean displayAvailability;

	@JsonIgnore
	private Supplier<Boolean> _displayAvailabilitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether the storefront displays the exact remaining stock count to customers.",
		example = "true"
	)
	public Boolean getDisplayStockQuantity() {
		if (_displayStockQuantitySupplier != null) {
			displayStockQuantity = _displayStockQuantitySupplier.get();

			_displayStockQuantitySupplier = null;
		}

		return displayStockQuantity;
	}

	public void setDisplayStockQuantity(Boolean displayStockQuantity) {
		this.displayStockQuantity = displayStockQuantity;

		_displayStockQuantitySupplier = null;
	}

	@JsonIgnore
	public void setDisplayStockQuantity(
		UnsafeSupplier<Boolean, Exception> displayStockQuantityUnsafeSupplier) {

		_displayStockQuantitySupplier = () -> {
			try {
				return displayStockQuantityUnsafeSupplier.get();
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
		description = "Whether the storefront displays the exact remaining stock count to customers."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean displayStockQuantity;

	@JsonIgnore
	private Supplier<Boolean> _displayStockQuantitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the target entity; the product's external reference code when the entry points at a product, or null when it points at a template; read-only.",
		example = "AB-34098-789-N"
	)
	public String getEntityExternalReferenceCode() {
		if (_entityExternalReferenceCodeSupplier != null) {
			entityExternalReferenceCode =
				_entityExternalReferenceCodeSupplier.get();

			_entityExternalReferenceCodeSupplier = null;
		}

		return entityExternalReferenceCode;
	}

	public void setEntityExternalReferenceCode(
		String entityExternalReferenceCode) {

		this.entityExternalReferenceCode = entityExternalReferenceCode;

		_entityExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setEntityExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			entityExternalReferenceCodeUnsafeSupplier) {

		_entityExternalReferenceCodeSupplier = () -> {
			try {
				return entityExternalReferenceCodeUnsafeSupplier.get();
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
		description = "External reference code of the target entity; the product's external reference code when the entry points at a product, or null when it points at a template; read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String entityExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _entityExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Identifier of the target entity; the product identifier when the entry points at a product or the configuration list identifier when it points at a template.",
		example = "30130"
	)
	public Long getEntityId() {
		if (_entityIdSupplier != null) {
			entityId = _entityIdSupplier.get();

			_entityIdSupplier = null;
		}

		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;

		_entityIdSupplier = null;
	}

	@JsonIgnore
	public void setEntityId(
		UnsafeSupplier<Long, Exception> entityIdUnsafeSupplier) {

		_entityIdSupplier = () -> {
			try {
				return entityIdUnsafeSupplier.get();
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
		description = "Identifier of the target entity; the product identifier when the entry points at a product or the configuration list identifier when it points at a template."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long entityId;

	@JsonIgnore
	private Supplier<Long> _entityIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Localized name of the target product for the request locale; null when the entry points at a template; read-only.",
		example = "ABS Sensor"
	)
	public String getEntityName() {
		if (_entityNameSupplier != null) {
			entityName = _entityNameSupplier.get();

			_entityNameSupplier = null;
		}

		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;

		_entityNameSupplier = null;
	}

	@JsonIgnore
	public void setEntityName(
		UnsafeSupplier<String, Exception> entityNameUnsafeSupplier) {

		_entityNameSupplier = () -> {
			try {
				return entityNameUnsafeSupplier.get();
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
		description = "Localized name of the target product for the request locale; null when the entry points at a template; read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String entityName;

	@JsonIgnore
	private Supplier<String> _entityNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Discriminator on create indicating which entity the new entry targets; `product` resolves a product by external reference code or identifier, while `template` targets the parent configuration list; defaults to `product` when omitted.",
		example = "product"
	)
	@JsonGetter("entityType")
	@Valid
	public EntityType getEntityType() {
		if (_entityTypeSupplier != null) {
			entityType = _entityTypeSupplier.get();

			_entityTypeSupplier = null;
		}

		return entityType;
	}

	@JsonIgnore
	public String getEntityTypeAsString() {
		EntityType entityType = getEntityType();

		if (entityType == null) {
			return null;
		}

		return entityType.toString();
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;

		_entityTypeSupplier = null;
	}

	@JsonIgnore
	public void setEntityType(
		UnsafeSupplier<EntityType, Exception> entityTypeUnsafeSupplier) {

		_entityTypeSupplier = () -> {
			try {
				return entityTypeUnsafeSupplier.get();
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
		description = "Discriminator on create indicating which entity the new entry targets; `product` resolves a product by external reference code or identifier, while `template` targets the parent configuration list; defaults to `product` when omitted."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected EntityType entityType;

	@JsonIgnore
	private Supplier<EntityType> _entityTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Idempotency key for create and update; must be unique per product configuration within the company.",
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
		description = "Idempotency key for create and update; must be unique per product configuration within the company."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Identifier of the product configuration; read-only.",
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
		description = "Identifier of the product configuration; read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Key of the inventory engine that resolves stock for this product; defaults to `default`, which uses the embedded inventory record; custom engines may delegate to external inventory systems.",
		example = "default"
	)
	public String getInventoryEngine() {
		if (_inventoryEngineSupplier != null) {
			inventoryEngine = _inventoryEngineSupplier.get();

			_inventoryEngineSupplier = null;
		}

		return inventoryEngine;
	}

	public void setInventoryEngine(String inventoryEngine) {
		this.inventoryEngine = inventoryEngine;

		_inventoryEngineSupplier = null;
	}

	@JsonIgnore
	public void setInventoryEngine(
		UnsafeSupplier<String, Exception> inventoryEngineUnsafeSupplier) {

		_inventoryEngineSupplier = () -> {
			try {
				return inventoryEngineUnsafeSupplier.get();
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
		description = "Key of the inventory engine that resolves stock for this product; defaults to `default`, which uses the embedded inventory record; custom engines may delegate to external inventory systems."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String inventoryEngine;

	@JsonIgnore
	private Supplier<String> _inventoryEngineSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Key of the activity that runs when stock falls to or below the minimum stock quantity; the built-in `default` activity unpublishes the SKU.",
		example = "default"
	)
	public String getLowStockAction() {
		if (_lowStockActionSupplier != null) {
			lowStockAction = _lowStockActionSupplier.get();

			_lowStockActionSupplier = null;
		}

		return lowStockAction;
	}

	public void setLowStockAction(String lowStockAction) {
		this.lowStockAction = lowStockAction;

		_lowStockActionSupplier = null;
	}

	@JsonIgnore
	public void setLowStockAction(
		UnsafeSupplier<String, Exception> lowStockActionUnsafeSupplier) {

		_lowStockActionSupplier = () -> {
			try {
				return lowStockActionUnsafeSupplier.get();
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
		description = "Key of the activity that runs when stock falls to or below the minimum stock quantity; the built-in `default` activity unpublishes the SKU."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String lowStockAction;

	@JsonIgnore
	private Supplier<String> _lowStockActionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Maximum quantity a customer may include for this product in a single order; must be greater than zero; defaults to 10000 on create when omitted.",
		example = "10.1"
	)
	@Valid
	public BigDecimal getMaxOrderQuantity() {
		if (_maxOrderQuantitySupplier != null) {
			maxOrderQuantity = _maxOrderQuantitySupplier.get();

			_maxOrderQuantitySupplier = null;
		}

		return maxOrderQuantity;
	}

	public void setMaxOrderQuantity(BigDecimal maxOrderQuantity) {
		this.maxOrderQuantity = maxOrderQuantity;

		_maxOrderQuantitySupplier = null;
	}

	@JsonIgnore
	public void setMaxOrderQuantity(
		UnsafeSupplier<BigDecimal, Exception> maxOrderQuantityUnsafeSupplier) {

		_maxOrderQuantitySupplier = () -> {
			try {
				return maxOrderQuantityUnsafeSupplier.get();
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
		description = "Maximum quantity a customer may include for this product in a single order; must be greater than zero; defaults to 10000 on create when omitted."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected BigDecimal maxOrderQuantity;

	@JsonIgnore
	private Supplier<BigDecimal> _maxOrderQuantitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Minimum quantity a customer must include for this product in a single order; must be greater than zero; defaults to 1.",
		example = "10.1"
	)
	@Valid
	public BigDecimal getMinOrderQuantity() {
		if (_minOrderQuantitySupplier != null) {
			minOrderQuantity = _minOrderQuantitySupplier.get();

			_minOrderQuantitySupplier = null;
		}

		return minOrderQuantity;
	}

	public void setMinOrderQuantity(BigDecimal minOrderQuantity) {
		this.minOrderQuantity = minOrderQuantity;

		_minOrderQuantitySupplier = null;
	}

	@JsonIgnore
	public void setMinOrderQuantity(
		UnsafeSupplier<BigDecimal, Exception> minOrderQuantityUnsafeSupplier) {

		_minOrderQuantitySupplier = () -> {
			try {
				return minOrderQuantityUnsafeSupplier.get();
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
		description = "Minimum quantity a customer must include for this product in a single order; must be greater than zero; defaults to 1."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected BigDecimal minOrderQuantity;

	@JsonIgnore
	private Supplier<BigDecimal> _minOrderQuantitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Threshold at which the configured low-stock action is triggered; defaults to 1 on create; trailing zeros are stripped on read.",
		example = "10.1"
	)
	@Valid
	public BigDecimal getMinStockQuantity() {
		if (_minStockQuantitySupplier != null) {
			minStockQuantity = _minStockQuantitySupplier.get();

			_minStockQuantitySupplier = null;
		}

		return minStockQuantity;
	}

	public void setMinStockQuantity(BigDecimal minStockQuantity) {
		this.minStockQuantity = minStockQuantity;

		_minStockQuantitySupplier = null;
	}

	@JsonIgnore
	public void setMinStockQuantity(
		UnsafeSupplier<BigDecimal, Exception> minStockQuantityUnsafeSupplier) {

		_minStockQuantitySupplier = () -> {
			try {
				return minStockQuantityUnsafeSupplier.get();
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
		description = "Threshold at which the configured low-stock action is triggered; defaults to 1 on create; trailing zeros are stripped on read."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected BigDecimal minStockQuantity;

	@JsonIgnore
	private Supplier<BigDecimal> _minStockQuantitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Step increment for order quantity; ordered quantities must be multiples of this value; must be greater than zero; defaults to 1.",
		example = "10.1"
	)
	@Valid
	public BigDecimal getMultipleOrderQuantity() {
		if (_multipleOrderQuantitySupplier != null) {
			multipleOrderQuantity = _multipleOrderQuantitySupplier.get();

			_multipleOrderQuantitySupplier = null;
		}

		return multipleOrderQuantity;
	}

	public void setMultipleOrderQuantity(BigDecimal multipleOrderQuantity) {
		this.multipleOrderQuantity = multipleOrderQuantity;

		_multipleOrderQuantitySupplier = null;
	}

	@JsonIgnore
	public void setMultipleOrderQuantity(
		UnsafeSupplier<BigDecimal, Exception>
			multipleOrderQuantityUnsafeSupplier) {

		_multipleOrderQuantitySupplier = () -> {
			try {
				return multipleOrderQuantityUnsafeSupplier.get();
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
		description = "Step increment for order quantity; ordered quantities must be multiples of this value; must be greater than zero; defaults to 1."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected BigDecimal multipleOrderQuantity;

	@JsonIgnore
	private Supplier<BigDecimal> _multipleOrderQuantitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ProductShippingConfiguration getProductShippingConfiguration() {
		if (_productShippingConfigurationSupplier != null) {
			productShippingConfiguration =
				_productShippingConfigurationSupplier.get();

			_productShippingConfigurationSupplier = null;
		}

		return productShippingConfiguration;
	}

	public void setProductShippingConfiguration(
		ProductShippingConfiguration productShippingConfiguration) {

		this.productShippingConfiguration = productShippingConfiguration;

		_productShippingConfigurationSupplier = null;
	}

	@JsonIgnore
	public void setProductShippingConfiguration(
		UnsafeSupplier<ProductShippingConfiguration, Exception>
			productShippingConfigurationUnsafeSupplier) {

		_productShippingConfigurationSupplier = () -> {
			try {
				return productShippingConfigurationUnsafeSupplier.get();
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
	protected ProductShippingConfiguration productShippingConfiguration;

	@JsonIgnore
	private Supplier<ProductShippingConfiguration>
		_productShippingConfigurationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ProductTaxConfiguration getProductTaxConfiguration() {
		if (_productTaxConfigurationSupplier != null) {
			productTaxConfiguration = _productTaxConfigurationSupplier.get();

			_productTaxConfigurationSupplier = null;
		}

		return productTaxConfiguration;
	}

	public void setProductTaxConfiguration(
		ProductTaxConfiguration productTaxConfiguration) {

		this.productTaxConfiguration = productTaxConfiguration;

		_productTaxConfigurationSupplier = null;
	}

	@JsonIgnore
	public void setProductTaxConfiguration(
		UnsafeSupplier<ProductTaxConfiguration, Exception>
			productTaxConfigurationUnsafeSupplier) {

		_productTaxConfigurationSupplier = () -> {
			try {
				return productTaxConfigurationUnsafeSupplier.get();
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
	protected ProductTaxConfiguration productTaxConfiguration;

	@JsonIgnore
	private Supplier<ProductTaxConfiguration> _productTaxConfigurationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether the product or SKU can be added to a cart and checked out; defaults to true on create.",
		example = "true"
	)
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

	@GraphQLField(
		description = "Whether the product or SKU can be added to a cart and checked out; defaults to true on create."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean purchasable;

	@JsonIgnore
	private Supplier<Boolean> _purchasableSupplier;

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

		Boolean allowBackOrder = getAllowBackOrder();

		if (allowBackOrder != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"allowBackOrder\": ");

			sb.append(allowBackOrder);
		}

		BigDecimal[] allowedOrderQuantities = getAllowedOrderQuantities();

		if (allowedOrderQuantities != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"allowedOrderQuantities\": ");

			sb.append("[");

			for (int i = 0; i < allowedOrderQuantities.length; i++) {
				sb.append(allowedOrderQuantities[i]);

				if ((i + 1) < allowedOrderQuantities.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String availabilityEstimateExternalReferenceCode =
			getAvailabilityEstimateExternalReferenceCode();

		if (availabilityEstimateExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"availabilityEstimateExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(availabilityEstimateExternalReferenceCode));

			sb.append("\"");
		}

		Long availabilityEstimateId = getAvailabilityEstimateId();

		if (availabilityEstimateId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"availabilityEstimateId\": ");

			sb.append(availabilityEstimateId);
		}

		Map<String, String> availabilityEstimateName =
			getAvailabilityEstimateName();

		if (availabilityEstimateName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"availabilityEstimateName\": ");

			sb.append(_toJSON(availabilityEstimateName));
		}

		String[] differences = getDifferences();

		if (differences != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"differences\": ");

			sb.append("[");

			for (int i = 0; i < differences.length; i++) {
				sb.append("\"");

				sb.append(_escape(differences[i]));

				sb.append("\"");

				if ((i + 1) < differences.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean displayAvailability = getDisplayAvailability();

		if (displayAvailability != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayAvailability\": ");

			sb.append(displayAvailability);
		}

		Boolean displayStockQuantity = getDisplayStockQuantity();

		if (displayStockQuantity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayStockQuantity\": ");

			sb.append(displayStockQuantity);
		}

		String entityExternalReferenceCode = getEntityExternalReferenceCode();

		if (entityExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"entityExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(entityExternalReferenceCode));

			sb.append("\"");
		}

		Long entityId = getEntityId();

		if (entityId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"entityId\": ");

			sb.append(entityId);
		}

		String entityName = getEntityName();

		if (entityName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"entityName\": ");

			sb.append("\"");

			sb.append(_escape(entityName));

			sb.append("\"");
		}

		EntityType entityType = getEntityType();

		if (entityType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"entityType\": ");

			sb.append("\"");
			sb.append(entityType);
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

		String inventoryEngine = getInventoryEngine();

		if (inventoryEngine != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"inventoryEngine\": ");

			sb.append("\"");

			sb.append(_escape(inventoryEngine));

			sb.append("\"");
		}

		String lowStockAction = getLowStockAction();

		if (lowStockAction != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lowStockAction\": ");

			sb.append("\"");

			sb.append(_escape(lowStockAction));

			sb.append("\"");
		}

		BigDecimal maxOrderQuantity = getMaxOrderQuantity();

		if (maxOrderQuantity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxOrderQuantity\": ");

			sb.append(maxOrderQuantity);
		}

		BigDecimal minOrderQuantity = getMinOrderQuantity();

		if (minOrderQuantity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"minOrderQuantity\": ");

			sb.append(minOrderQuantity);
		}

		BigDecimal minStockQuantity = getMinStockQuantity();

		if (minStockQuantity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"minStockQuantity\": ");

			sb.append(minStockQuantity);
		}

		BigDecimal multipleOrderQuantity = getMultipleOrderQuantity();

		if (multipleOrderQuantity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"multipleOrderQuantity\": ");

			sb.append(multipleOrderQuantity);
		}

		ProductShippingConfiguration productShippingConfiguration =
			getProductShippingConfiguration();

		if (productShippingConfiguration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productShippingConfiguration\": ");

			sb.append(String.valueOf(productShippingConfiguration));
		}

		ProductTaxConfiguration productTaxConfiguration =
			getProductTaxConfiguration();

		if (productTaxConfiguration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productTaxConfiguration\": ");

			sb.append(String.valueOf(productTaxConfiguration));
		}

		Boolean purchasable = getPurchasable();

		if (purchasable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"purchasable\": ");

			sb.append(purchasable);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfiguration",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("EntityType")
	public static enum EntityType {

		PRODUCT("product"), TEMPLATE("template");

		@JsonCreator
		public static EntityType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (EntityType entityType : values()) {
				if (Objects.equals(entityType.getValue(), value)) {
					return entityType;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
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
// LIFERAY-REST-BUILDER-HASH:-1062706431