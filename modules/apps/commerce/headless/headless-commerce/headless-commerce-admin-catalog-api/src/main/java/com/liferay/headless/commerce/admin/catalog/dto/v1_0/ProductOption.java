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
import jakarta.validation.constraints.NotNull;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

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
	description = "An option attached to a specific product, carrying per-product overrides for the global option template; the link captures localized labels, required and SKU-contributor flags, price modifier settings, and the list of selectable values for that product. The global option template is never created from this payload; it must already exist (create it first with POST /options) and is referenced by `optionId` (or by `optionExternalReferenceCode` when `optionId` is set to 0). Referencing an option that does not exist fails the request with a 404 No Such Option error.",
	value = "ProductOption"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "An option attached to a specific product, carrying per-product overrides for the global option template; the link captures localized labels, required and SKU-contributor flags, price modifier settings, and the list of selectable values for that product. The global option template is never created from this payload; it must already exist (create it first with POST /options) and is referenced by `optionId` (or by `optionExternalReferenceCode` when `optionId` is set to 0). Referencing an option that does not exist fails the request with a 404 No Such Option error.",
	requiredProperties = {"fieldType", "key", "name", "optionId"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ProductOption")
public class ProductOption implements Serializable {

	public static ProductOption toDTO(String json) {
		return ObjectMapperUtil.readValue(ProductOption.class, json);
	}

	public static ProductOption unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ProductOption.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Not populated on reads and ignored on writes; the product option link has no direct catalog reference.",
		example = "30130"
	)
	public Long getCatalogId() {
		if (_catalogIdSupplier != null) {
			catalogId = _catalogIdSupplier.get();

			_catalogIdSupplier = null;
		}

		return catalogId;
	}

	public void setCatalogId(Long catalogId) {
		this.catalogId = catalogId;

		_catalogIdSupplier = null;
	}

	@JsonIgnore
	public void setCatalogId(
		UnsafeSupplier<Long, Exception> catalogIdUnsafeSupplier) {

		_catalogIdSupplier = () -> {
			try {
				return catalogIdUnsafeSupplier.get();
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
		description = "Not populated on reads and ignored on writes; the product option link has no direct catalog reference."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long catalogId;

	@JsonIgnore
	private Supplier<Long> _catalogIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Custom field values stored against the product option link through the expando bridge; localized when the request accepts all languages."
	)
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

	@GraphQLField(
		description = "Custom field values stored against the product option link through the expando bridge; localized when the request accepts all languages."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected com.liferay.portal.vulcan.custom.field.CustomField[] customFields;

	@JsonIgnore
	private Supplier<com.liferay.portal.vulcan.custom.field.CustomField[]>
		_customFieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether the option and its values are sourced from an external system rather than the catalog; when true the price type must be dynamic; writable through patch but currently not echoed back on reads.",
		example = "true"
	)
	public Boolean getDefinedExternally() {
		if (_definedExternallySupplier != null) {
			definedExternally = _definedExternallySupplier.get();

			_definedExternallySupplier = null;
		}

		return definedExternally;
	}

	public void setDefinedExternally(Boolean definedExternally) {
		this.definedExternally = definedExternally;

		_definedExternallySupplier = null;
	}

	@JsonIgnore
	public void setDefinedExternally(
		UnsafeSupplier<Boolean, Exception> definedExternallyUnsafeSupplier) {

		_definedExternallySupplier = () -> {
			try {
				return definedExternallyUnsafeSupplier.get();
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
		description = "Whether the option and its values are sourced from an external system rather than the catalog; when true the price type must be dynamic; writable through patch but currently not echoed back on reads."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean definedExternally;

	@JsonIgnore
	private Supplier<Boolean> _definedExternallySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Per-product localized description that overrides the global option description on this product only; map keys are locale codes and values are the translated strings.",
		example = "{en_US=Description, hr_HR=Description HR, hu_HU=Description HU}"
	)
	@Valid
	public Map<String, String> getDescription() {
		if (_descriptionSupplier != null) {
			description = _descriptionSupplier.get();

			_descriptionSupplier = null;
		}

		return description;
	}

	public void setDescription(Map<String, String> description) {
		this.description = description;

		_descriptionSupplier = null;
	}

	@JsonIgnore
	public void setDescription(
		UnsafeSupplier<Map<String, String>, Exception>
			descriptionUnsafeSupplier) {

		_descriptionSupplier = () -> {
			try {
				return descriptionUnsafeSupplier.get();
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
		description = "Per-product localized description that overrides the global option description on this product only; map keys are locale codes and values are the translated strings."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> description;

	@JsonIgnore
	private Supplier<Map<String, String>> _descriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Idempotency key of the product option link itself, unique per company; assigned from the entity UUID when omitted on create. Addresses the link across environments, so the nested product option values stay reachable after an export and import.",
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
		description = "Idempotency key of the product option link itself, unique per company; assigned from the entity UUID when omitted on create. Addresses the link across environments, so the nested product option values stay reachable after an export and import."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Per-product override of whether this option is exposed as a search facet for the parent product; defaults to the global option flag on first attach.",
		example = "true"
	)
	public Boolean getFacetable() {
		if (_facetableSupplier != null) {
			facetable = _facetableSupplier.get();

			_facetableSupplier = null;
		}

		return facetable;
	}

	public void setFacetable(Boolean facetable) {
		this.facetable = facetable;

		_facetableSupplier = null;
	}

	@JsonIgnore
	public void setFacetable(
		UnsafeSupplier<Boolean, Exception> facetableUnsafeSupplier) {

		_facetableSupplier = () -> {
			try {
				return facetableUnsafeSupplier.get();
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
		description = "Per-product override of whether this option is exposed as a search facet for the parent product; defaults to the global option flag on first attach."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean facetable;

	@JsonIgnore
	private Supplier<Boolean> _facetableSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Per-product input widget for this option; validated against the configured allow list and, when `skuContributor` is true, restricted to select, select_date, and radio; required.",
		example = "checkbox, checkbox_multiple, date, numeric, radio, select"
	)
	public String getFieldType() {
		if (_fieldTypeSupplier != null) {
			fieldType = _fieldTypeSupplier.get();

			_fieldTypeSupplier = null;
		}

		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;

		_fieldTypeSupplier = null;
	}

	@JsonIgnore
	public void setFieldType(
		UnsafeSupplier<String, Exception> fieldTypeUnsafeSupplier) {

		_fieldTypeSupplier = () -> {
			try {
				return fieldTypeUnsafeSupplier.get();
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
		description = "Per-product input widget for this option; validated against the configured allow list and, when `skuContributor` is true, restricted to select, select_date, and radio; required."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String fieldType;

	@JsonIgnore
	private Supplier<String> _fieldTypeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Primary key; read-only and assigned by the service on create.",
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
		description = "Primary key; read-only and assigned by the service on create."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Service key identifying an external info-item provider that supplies option data from outside the catalog; used together with `definedExternally`; writable but currently not echoed back on reads."
	)
	public String getInfoItemServiceKey() {
		if (_infoItemServiceKeySupplier != null) {
			infoItemServiceKey = _infoItemServiceKeySupplier.get();

			_infoItemServiceKeySupplier = null;
		}

		return infoItemServiceKey;
	}

	public void setInfoItemServiceKey(String infoItemServiceKey) {
		this.infoItemServiceKey = infoItemServiceKey;

		_infoItemServiceKeySupplier = null;
	}

	@JsonIgnore
	public void setInfoItemServiceKey(
		UnsafeSupplier<String, Exception> infoItemServiceKeyUnsafeSupplier) {

		_infoItemServiceKeySupplier = () -> {
			try {
				return infoItemServiceKeyUnsafeSupplier.get();
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
		description = "Service key identifying an external info-item provider that supplies option data from outside the catalog; used together with `definedExternally`; writable but currently not echoed back on reads."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String infoItemServiceKey;

	@JsonIgnore
	private Supplier<String> _infoItemServiceKeySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Machine identifier on the product link; copied from the linked global option's key when the link is created and must be unique within the product; required.",
		example = "color"
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
		description = "Machine identifier on the product link; copied from the linked global option's key when the link is created and must be unique within the product; required."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String key;

	@JsonIgnore
	private Supplier<String> _keySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Per-product localized label that overrides the global option name on this product only; map keys are locale codes and values are the translated strings; required.",
		example = "{en_US=Color, hr_HR=Color HR, hu_HU=Color HU}"
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
		description = "Per-product localized label that overrides the global option name on this product only; map keys are locale codes and values are the translated strings; required."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotNull
	protected Map<String, String> name;

	@JsonIgnore
	private Supplier<Map<String, String>> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the linked global option; used as an alternative to `optionId` when wiring a product to an existing option, and consulted only when `optionId` is set to 0. The option must already exist; it is not created from this code.",
		example = "AB-34098-789-N"
	)
	public String getOptionExternalReferenceCode() {
		if (_optionExternalReferenceCodeSupplier != null) {
			optionExternalReferenceCode =
				_optionExternalReferenceCodeSupplier.get();

			_optionExternalReferenceCodeSupplier = null;
		}

		return optionExternalReferenceCode;
	}

	public void setOptionExternalReferenceCode(
		String optionExternalReferenceCode) {

		this.optionExternalReferenceCode = optionExternalReferenceCode;

		_optionExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setOptionExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			optionExternalReferenceCodeUnsafeSupplier) {

		_optionExternalReferenceCodeSupplier = () -> {
			try {
				return optionExternalReferenceCodeUnsafeSupplier.get();
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
		description = "External reference code of the linked global option; used as an alternative to `optionId` when wiring a product to an existing option, and consulted only when `optionId` is set to 0. The option must already exist; it is not created from this code."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String optionExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _optionExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to an already-existing global option template this product link is based on; the option is looked up, never created, so it must be provisioned first (POST /options). This field is required and cannot be omitted; set it to 0 to wire the product by `optionExternalReferenceCode` instead. Referencing an option that does not exist fails the request with a 404 No Such Option error.",
		example = "30080"
	)
	public Long getOptionId() {
		if (_optionIdSupplier != null) {
			optionId = _optionIdSupplier.get();

			_optionIdSupplier = null;
		}

		return optionId;
	}

	public void setOptionId(Long optionId) {
		this.optionId = optionId;

		_optionIdSupplier = null;
	}

	@JsonIgnore
	public void setOptionId(
		UnsafeSupplier<Long, Exception> optionIdUnsafeSupplier) {

		_optionIdSupplier = () -> {
			try {
				return optionIdUnsafeSupplier.get();
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
		description = "Reference to an already-existing global option template this product link is based on; the option is looked up, never created, so it must be provisioned first (POST /options). This field is required and cannot be omitted; set it to 0 to wire the product by `optionExternalReferenceCode` instead. Referencing an option that does not exist fails the request with a 404 No Such Option error."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotNull
	protected Long optionId;

	@JsonIgnore
	private Supplier<Long> _optionIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "How a selected value affects the product price; one of static (delta added to the base price) or dynamic (delta computed by an external pricing engine); when `definedExternally` is true the value is forced to dynamic.",
		example = "dynamic, static"
	)
	public String getPriceType() {
		if (_priceTypeSupplier != null) {
			priceType = _priceTypeSupplier.get();

			_priceTypeSupplier = null;
		}

		return priceType;
	}

	public void setPriceType(String priceType) {
		this.priceType = priceType;

		_priceTypeSupplier = null;
	}

	@JsonIgnore
	public void setPriceType(
		UnsafeSupplier<String, Exception> priceTypeUnsafeSupplier) {

		_priceTypeSupplier = () -> {
			try {
				return priceTypeUnsafeSupplier.get();
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
		description = "How a selected value affects the product price; one of static (delta added to the base price) or dynamic (delta computed by an external pricing engine); when `definedExternally` is true the value is forced to dynamic."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String priceType;

	@JsonIgnore
	private Supplier<String> _priceTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Display order used when listing options inside the product; lower values appear first.",
		example = "1.2"
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
		description = "Display order used when listing options inside the product; lower values appear first."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Double priority;

	@JsonIgnore
	private Supplier<Double> _prioritySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Per-product values for this option; returned only when the request asks for them, otherwise null."
	)
	@Valid
	public ProductOptionValue[] getProductOptionValues() {
		if (_productOptionValuesSupplier != null) {
			productOptionValues = _productOptionValuesSupplier.get();

			_productOptionValuesSupplier = null;
		}

		return productOptionValues;
	}

	public void setProductOptionValues(
		ProductOptionValue[] productOptionValues) {

		this.productOptionValues = productOptionValues;

		_productOptionValuesSupplier = null;
	}

	@JsonIgnore
	public void setProductOptionValues(
		UnsafeSupplier<ProductOptionValue[], Exception>
			productOptionValuesUnsafeSupplier) {

		_productOptionValuesSupplier = () -> {
			try {
				return productOptionValuesUnsafeSupplier.get();
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
		description = "Per-product values for this option; returned only when the request asks for them, otherwise null."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ProductOptionValue[] productOptionValues;

	@JsonIgnore
	private Supplier<ProductOptionValue[]> _productOptionValuesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether the shopper must select a value for this option before the product can be added to the cart.",
		example = "true"
	)
	public Boolean getRequired() {
		if (_requiredSupplier != null) {
			required = _requiredSupplier.get();

			_requiredSupplier = null;
		}

		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;

		_requiredSupplier = null;
	}

	@JsonIgnore
	public void setRequired(
		UnsafeSupplier<Boolean, Exception> requiredUnsafeSupplier) {

		_requiredSupplier = () -> {
			try {
				return requiredUnsafeSupplier.get();
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
		description = "Whether the shopper must select a value for this option before the product can be added to the cart."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean required;

	@JsonIgnore
	private Supplier<Boolean> _requiredSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether selecting a value on this option produces a distinct SKU for the parent product; enabling it restricts `fieldType` to select, select_date, or radio.",
		example = "true"
	)
	public Boolean getSkuContributor() {
		if (_skuContributorSupplier != null) {
			skuContributor = _skuContributorSupplier.get();

			_skuContributorSupplier = null;
		}

		return skuContributor;
	}

	public void setSkuContributor(Boolean skuContributor) {
		this.skuContributor = skuContributor;

		_skuContributorSupplier = null;
	}

	@JsonIgnore
	public void setSkuContributor(
		UnsafeSupplier<Boolean, Exception> skuContributorUnsafeSupplier) {

		_skuContributorSupplier = () -> {
			try {
				return skuContributorUnsafeSupplier.get();
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
		description = "Whether selecting a value on this option produces a distinct SKU for the parent product; enabling it restricts `fieldType` to select, select_date, or radio."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean skuContributor;

	@JsonIgnore
	private Supplier<Boolean> _skuContributorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Opaque, type-specific configuration string persisted on the product option link; format is determined by the field type implementation.",
		example = "22.50"
	)
	public String getTypeSettings() {
		if (_typeSettingsSupplier != null) {
			typeSettings = _typeSettingsSupplier.get();

			_typeSettingsSupplier = null;
		}

		return typeSettings;
	}

	public void setTypeSettings(String typeSettings) {
		this.typeSettings = typeSettings;

		_typeSettingsSupplier = null;
	}

	@JsonIgnore
	public void setTypeSettings(
		UnsafeSupplier<String, Exception> typeSettingsUnsafeSupplier) {

		_typeSettingsSupplier = () -> {
			try {
				return typeSettingsUnsafeSupplier.get();
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
		description = "Opaque, type-specific configuration string persisted on the product option link; format is determined by the field type implementation."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String typeSettings;

	@JsonIgnore
	private Supplier<String> _typeSettingsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProductOption)) {
			return false;
		}

		ProductOption productOption = (ProductOption)object;

		return Objects.equals(toString(), productOption.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Long catalogId = getCatalogId();

		if (catalogId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"catalogId\": ");

			sb.append(catalogId);
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

		Boolean definedExternally = getDefinedExternally();

		if (definedExternally != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"definedExternally\": ");

			sb.append(definedExternally);
		}

		Map<String, String> description = getDescription();

		if (description != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append(_toJSON(description));
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

		Boolean facetable = getFacetable();

		if (facetable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"facetable\": ");

			sb.append(facetable);
		}

		String fieldType = getFieldType();

		if (fieldType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fieldType\": ");

			sb.append("\"");

			sb.append(_escape(fieldType));

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

		String infoItemServiceKey = getInfoItemServiceKey();

		if (infoItemServiceKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"infoItemServiceKey\": ");

			sb.append("\"");

			sb.append(_escape(infoItemServiceKey));

			sb.append("\"");
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

		String optionExternalReferenceCode = getOptionExternalReferenceCode();

		if (optionExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"optionExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(optionExternalReferenceCode));

			sb.append("\"");
		}

		Long optionId = getOptionId();

		if (optionId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"optionId\": ");

			sb.append(optionId);
		}

		String priceType = getPriceType();

		if (priceType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceType\": ");

			sb.append("\"");

			sb.append(_escape(priceType));

			sb.append("\"");
		}

		Double priority = getPriority();

		if (priority != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(priority);
		}

		ProductOptionValue[] productOptionValues = getProductOptionValues();

		if (productOptionValues != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productOptionValues\": ");

			sb.append("[");

			for (int i = 0; i < productOptionValues.length; i++) {
				sb.append(String.valueOf(productOptionValues[i]));

				if ((i + 1) < productOptionValues.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean required = getRequired();

		if (required != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"required\": ");

			sb.append(required);
		}

		Boolean skuContributor = getSkuContributor();

		if (skuContributor != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuContributor\": ");

			sb.append(skuContributor);
		}

		String typeSettings = getTypeSettings();

		if (typeSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"typeSettings\": ");

			sb.append("\"");

			sb.append(_escape(typeSettings));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductOption",
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
// LIFERAY-REST-BUILDER-HASH:639322298