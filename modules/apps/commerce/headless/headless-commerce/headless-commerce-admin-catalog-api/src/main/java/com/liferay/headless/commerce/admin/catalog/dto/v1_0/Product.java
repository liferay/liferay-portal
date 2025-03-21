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

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
@GraphQLName("Product")
@io.swagger.v3.oas.annotations.media.Schema(
	requiredProperties = {"active", "name", "productType"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Product")
public class Product implements Serializable {

	public static Product toDTO(String json) {
		return ObjectMapperUtil.readValue(Product.class, json);
	}

	public static Product unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Product.class, json);
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
	@NotNull
	protected Boolean active;

	@JsonIgnore
	private Supplier<Boolean> _activeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Attachment[] getAttachments() {
		if (_attachmentsSupplier != null) {
			attachments = _attachmentsSupplier.get();

			_attachmentsSupplier = null;
		}

		return attachments;
	}

	public void setAttachments(Attachment[] attachments) {
		this.attachments = attachments;

		_attachmentsSupplier = null;
	}

	@JsonIgnore
	public void setAttachments(
		UnsafeSupplier<Attachment[], Exception> attachmentsUnsafeSupplier) {

		_attachmentsSupplier = () -> {
			try {
				return attachmentsUnsafeSupplier.get();
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
	protected Attachment[] attachments;

	@JsonIgnore
	private Supplier<Attachment[]> _attachmentsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Catalog getCatalog() {
		if (_catalogSupplier != null) {
			catalog = _catalogSupplier.get();

			_catalogSupplier = null;
		}

		return catalog;
	}

	public void setCatalog(Catalog catalog) {
		this.catalog = catalog;

		_catalogSupplier = null;
	}

	@JsonIgnore
	public void setCatalog(
		UnsafeSupplier<Catalog, Exception> catalogUnsafeSupplier) {

		_catalogSupplier = () -> {
			try {
				return catalogUnsafeSupplier.get();
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
	protected Catalog catalog;

	@JsonIgnore
	private Supplier<Catalog> _catalogSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "AB-34098-789-N")
	public String getCatalogExternalReferenceCode() {
		if (_catalogExternalReferenceCodeSupplier != null) {
			catalogExternalReferenceCode =
				_catalogExternalReferenceCodeSupplier.get();

			_catalogExternalReferenceCodeSupplier = null;
		}

		return catalogExternalReferenceCode;
	}

	public void setCatalogExternalReferenceCode(
		String catalogExternalReferenceCode) {

		this.catalogExternalReferenceCode = catalogExternalReferenceCode;

		_catalogExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setCatalogExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			catalogExternalReferenceCodeUnsafeSupplier) {

		_catalogExternalReferenceCodeSupplier = () -> {
			try {
				return catalogExternalReferenceCodeUnsafeSupplier.get();
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
	protected String catalogExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _catalogExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30054")
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long catalogId;

	@JsonIgnore
	private Supplier<Long> _catalogIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Category[] getCategories() {
		if (_categoriesSupplier != null) {
			categories = _categoriesSupplier.get();

			_categoriesSupplier = null;
		}

		return categories;
	}

	public void setCategories(Category[] categories) {
		this.categories = categories;

		_categoriesSupplier = null;
	}

	@JsonIgnore
	public void setCategories(
		UnsafeSupplier<Category[], Exception> categoriesUnsafeSupplier) {

		_categoriesSupplier = () -> {
			try {
				return categoriesUnsafeSupplier.get();
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
	protected Category[] categories;

	@JsonIgnore
	private Supplier<Category[]> _categoriesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "2017-07-21")
	public Date getCreateDate() {
		if (_createDateSupplier != null) {
			createDate = _createDateSupplier.get();

			_createDateSupplier = null;
		}

		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;

		_createDateSupplier = null;
	}

	@JsonIgnore
	public void setCreateDate(
		UnsafeSupplier<Date, Exception> createDateUnsafeSupplier) {

		_createDateSupplier = () -> {
			try {
				return createDateUnsafeSupplier.get();
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
	protected Date createDate;

	@JsonIgnore
	private Supplier<Date> _createDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public CustomField[] getCustomFields() {
		if (_customFieldsSupplier != null) {
			customFields = _customFieldsSupplier.get();

			_customFieldsSupplier = null;
		}

		return customFields;
	}

	public void setCustomFields(CustomField[] customFields) {
		this.customFields = customFields;

		_customFieldsSupplier = null;
	}

	@JsonIgnore
	public void setCustomFields(
		UnsafeSupplier<CustomField[], Exception> customFieldsUnsafeSupplier) {

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
	protected CustomField[] customFields;

	@JsonIgnore
	private Supplier<CustomField[]> _customFieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "Blue handle, 00001l, 70cm, lifetime warranty"
	)
	public String getDefaultSku() {
		if (_defaultSkuSupplier != null) {
			defaultSku = _defaultSkuSupplier.get();

			_defaultSkuSupplier = null;
		}

		return defaultSku;
	}

	public void setDefaultSku(String defaultSku) {
		this.defaultSku = defaultSku;

		_defaultSkuSupplier = null;
	}

	@JsonIgnore
	public void setDefaultSku(
		UnsafeSupplier<String, Exception> defaultSkuUnsafeSupplier) {

		_defaultSkuSupplier = () -> {
			try {
				return defaultSkuUnsafeSupplier.get();
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
	protected String defaultSku;

	@JsonIgnore
	private Supplier<String> _defaultSkuSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "{hu_HU=Product Description HU, hr_HR=Product Description HR, en_US=Professional hand stainless steel saw for wood. Made to last and saw forever. Made of best steel}"
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> description;

	@JsonIgnore
	private Supplier<Map<String, String>> _descriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Diagram getDiagram() {
		if (_diagramSupplier != null) {
			diagram = _diagramSupplier.get();

			_diagramSupplier = null;
		}

		return diagram;
	}

	public void setDiagram(Diagram diagram) {
		this.diagram = diagram;

		_diagramSupplier = null;
	}

	@JsonIgnore
	public void setDiagram(
		UnsafeSupplier<Diagram, Exception> diagramUnsafeSupplier) {

		_diagramSupplier = () -> {
			try {
				return diagramUnsafeSupplier.get();
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
	protected Diagram diagram;

	@JsonIgnore
	private Supplier<Diagram> _diagramSupplier;

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

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, ?> getExpando() {
		if (_expandoSupplier != null) {
			expando = _expandoSupplier.get();

			_expandoSupplier = null;
		}

		return expando;
	}

	public void setExpando(Map<String, ?> expando) {
		this.expando = expando;

		_expandoSupplier = null;
	}

	@JsonIgnore
	public void setExpando(
		UnsafeSupplier<Map<String, ?>, Exception> expandoUnsafeSupplier) {

		_expandoSupplier = () -> {
			try {
				return expandoUnsafeSupplier.get();
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
	protected Map<String, ?> expando;

	@JsonIgnore
	private Supplier<Map<String, ?>> _expandoSupplier;

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

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Attachment[] getImages() {
		if (_imagesSupplier != null) {
			images = _imagesSupplier.get();

			_imagesSupplier = null;
		}

		return images;
	}

	public void setImages(Attachment[] images) {
		this.images = images;

		_imagesSupplier = null;
	}

	@JsonIgnore
	public void setImages(
		UnsafeSupplier<Attachment[], Exception> imagesUnsafeSupplier) {

		_imagesSupplier = () -> {
			try {
				return imagesUnsafeSupplier.get();
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
	protected Attachment[] images;

	@JsonIgnore
	private Supplier<Attachment[]> _imagesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public LinkedProduct[] getLinkedProducts() {
		if (_linkedProductsSupplier != null) {
			linkedProducts = _linkedProductsSupplier.get();

			_linkedProductsSupplier = null;
		}

		return linkedProducts;
	}

	public void setLinkedProducts(LinkedProduct[] linkedProducts) {
		this.linkedProducts = linkedProducts;

		_linkedProductsSupplier = null;
	}

	@JsonIgnore
	public void setLinkedProducts(
		UnsafeSupplier<LinkedProduct[], Exception>
			linkedProductsUnsafeSupplier) {

		_linkedProductsSupplier = () -> {
			try {
				return linkedProductsUnsafeSupplier.get();
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
	protected LinkedProduct[] linkedProducts;

	@JsonIgnore
	private Supplier<LinkedProduct[]> _linkedProductsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public MappedProduct[] getMappedProducts() {
		if (_mappedProductsSupplier != null) {
			mappedProducts = _mappedProductsSupplier.get();

			_mappedProductsSupplier = null;
		}

		return mappedProducts;
	}

	public void setMappedProducts(MappedProduct[] mappedProducts) {
		this.mappedProducts = mappedProducts;

		_mappedProductsSupplier = null;
	}

	@JsonIgnore
	public void setMappedProducts(
		UnsafeSupplier<MappedProduct[], Exception>
			mappedProductsUnsafeSupplier) {

		_mappedProductsSupplier = () -> {
			try {
				return mappedProductsUnsafeSupplier.get();
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
	protected MappedProduct[] mappedProducts;

	@JsonIgnore
	private Supplier<MappedProduct[]> _mappedProductsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "{en_US=Meta description HU, hr_HR=Meta description HU, hu_HU=Meta description HU}"
	)
	@Valid
	public Map<String, String> getMetaDescription() {
		if (_metaDescriptionSupplier != null) {
			metaDescription = _metaDescriptionSupplier.get();

			_metaDescriptionSupplier = null;
		}

		return metaDescription;
	}

	public void setMetaDescription(Map<String, String> metaDescription) {
		this.metaDescription = metaDescription;

		_metaDescriptionSupplier = null;
	}

	@JsonIgnore
	public void setMetaDescription(
		UnsafeSupplier<Map<String, String>, Exception>
			metaDescriptionUnsafeSupplier) {

		_metaDescriptionSupplier = () -> {
			try {
				return metaDescriptionUnsafeSupplier.get();
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
	protected Map<String, String> metaDescription;

	@JsonIgnore
	private Supplier<Map<String, String>> _metaDescriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "{en_US=Meta keyword HU, hr_HR=Meta keyword HU, hu_HU=Meta keyword HU}"
	)
	@Valid
	public Map<String, String> getMetaKeyword() {
		if (_metaKeywordSupplier != null) {
			metaKeyword = _metaKeywordSupplier.get();

			_metaKeywordSupplier = null;
		}

		return metaKeyword;
	}

	public void setMetaKeyword(Map<String, String> metaKeyword) {
		this.metaKeyword = metaKeyword;

		_metaKeywordSupplier = null;
	}

	@JsonIgnore
	public void setMetaKeyword(
		UnsafeSupplier<Map<String, String>, Exception>
			metaKeywordUnsafeSupplier) {

		_metaKeywordSupplier = () -> {
			try {
				return metaKeywordUnsafeSupplier.get();
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
	protected Map<String, String> metaKeyword;

	@JsonIgnore
	private Supplier<Map<String, String>> _metaKeywordSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "{en_US=Meta title HU, hr_HR=Meta title HU, hu_HU=Meta title HU}"
	)
	@Valid
	public Map<String, String> getMetaTitle() {
		if (_metaTitleSupplier != null) {
			metaTitle = _metaTitleSupplier.get();

			_metaTitleSupplier = null;
		}

		return metaTitle;
	}

	public void setMetaTitle(Map<String, String> metaTitle) {
		this.metaTitle = metaTitle;

		_metaTitleSupplier = null;
	}

	@JsonIgnore
	public void setMetaTitle(
		UnsafeSupplier<Map<String, String>, Exception>
			metaTitleUnsafeSupplier) {

		_metaTitleSupplier = () -> {
			try {
				return metaTitleUnsafeSupplier.get();
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
	protected Map<String, String> metaTitle;

	@JsonIgnore
	private Supplier<Map<String, String>> _metaTitleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "2017-08-21")
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date modifiedDate;

	@JsonIgnore
	private Supplier<Date> _modifiedDateSupplier;

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
	@NotNull
	protected Map<String, String> name;

	@JsonIgnore
	private Supplier<Map<String, String>> _nameSupplier;

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
	@Valid
	public Pin[] getPins() {
		if (_pinsSupplier != null) {
			pins = _pinsSupplier.get();

			_pinsSupplier = null;
		}

		return pins;
	}

	public void setPins(Pin[] pins) {
		this.pins = pins;

		_pinsSupplier = null;
	}

	@JsonIgnore
	public void setPins(UnsafeSupplier<Pin[], Exception> pinsUnsafeSupplier) {
		_pinsSupplier = () -> {
			try {
				return pinsUnsafeSupplier.get();
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
	protected Pin[] pins;

	@JsonIgnore
	private Supplier<Pin[]> _pinsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getProductAccountGroupFilter() {
		if (_productAccountGroupFilterSupplier != null) {
			productAccountGroupFilter =
				_productAccountGroupFilterSupplier.get();

			_productAccountGroupFilterSupplier = null;
		}

		return productAccountGroupFilter;
	}

	public void setProductAccountGroupFilter(
		Boolean productAccountGroupFilter) {

		this.productAccountGroupFilter = productAccountGroupFilter;

		_productAccountGroupFilterSupplier = null;
	}

	@JsonIgnore
	public void setProductAccountGroupFilter(
		UnsafeSupplier<Boolean, Exception>
			productAccountGroupFilterUnsafeSupplier) {

		_productAccountGroupFilterSupplier = () -> {
			try {
				return productAccountGroupFilterUnsafeSupplier.get();
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
	protected Boolean productAccountGroupFilter;

	@JsonIgnore
	private Supplier<Boolean> _productAccountGroupFilterSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ProductAccountGroup[] getProductAccountGroups() {
		if (_productAccountGroupsSupplier != null) {
			productAccountGroups = _productAccountGroupsSupplier.get();

			_productAccountGroupsSupplier = null;
		}

		return productAccountGroups;
	}

	public void setProductAccountGroups(
		ProductAccountGroup[] productAccountGroups) {

		this.productAccountGroups = productAccountGroups;

		_productAccountGroupsSupplier = null;
	}

	@JsonIgnore
	public void setProductAccountGroups(
		UnsafeSupplier<ProductAccountGroup[], Exception>
			productAccountGroupsUnsafeSupplier) {

		_productAccountGroupsSupplier = () -> {
			try {
				return productAccountGroupsUnsafeSupplier.get();
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
	protected ProductAccountGroup[] productAccountGroups;

	@JsonIgnore
	private Supplier<ProductAccountGroup[]> _productAccountGroupsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getProductChannelFilter() {
		if (_productChannelFilterSupplier != null) {
			productChannelFilter = _productChannelFilterSupplier.get();

			_productChannelFilterSupplier = null;
		}

		return productChannelFilter;
	}

	public void setProductChannelFilter(Boolean productChannelFilter) {
		this.productChannelFilter = productChannelFilter;

		_productChannelFilterSupplier = null;
	}

	@JsonIgnore
	public void setProductChannelFilter(
		UnsafeSupplier<Boolean, Exception> productChannelFilterUnsafeSupplier) {

		_productChannelFilterSupplier = () -> {
			try {
				return productChannelFilterUnsafeSupplier.get();
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
	protected Boolean productChannelFilter;

	@JsonIgnore
	private Supplier<Boolean> _productChannelFilterSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ProductChannel[] getProductChannels() {
		if (_productChannelsSupplier != null) {
			productChannels = _productChannelsSupplier.get();

			_productChannelsSupplier = null;
		}

		return productChannels;
	}

	public void setProductChannels(ProductChannel[] productChannels) {
		this.productChannels = productChannels;

		_productChannelsSupplier = null;
	}

	@JsonIgnore
	public void setProductChannels(
		UnsafeSupplier<ProductChannel[], Exception>
			productChannelsUnsafeSupplier) {

		_productChannelsSupplier = () -> {
			try {
				return productChannelsUnsafeSupplier.get();
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
	protected ProductChannel[] productChannels;

	@JsonIgnore
	private Supplier<ProductChannel[]> _productChannelsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ProductConfiguration getProductConfiguration() {
		if (_productConfigurationSupplier != null) {
			productConfiguration = _productConfigurationSupplier.get();

			_productConfigurationSupplier = null;
		}

		return productConfiguration;
	}

	public void setProductConfiguration(
		ProductConfiguration productConfiguration) {

		this.productConfiguration = productConfiguration;

		_productConfigurationSupplier = null;
	}

	@JsonIgnore
	public void setProductConfiguration(
		UnsafeSupplier<ProductConfiguration, Exception>
			productConfigurationUnsafeSupplier) {

		_productConfigurationSupplier = () -> {
			try {
				return productConfigurationUnsafeSupplier.get();
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
	protected ProductConfiguration productConfiguration;

	@JsonIgnore
	private Supplier<ProductConfiguration> _productConfigurationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ProductOption[] getProductOptions() {
		if (_productOptionsSupplier != null) {
			productOptions = _productOptionsSupplier.get();

			_productOptionsSupplier = null;
		}

		return productOptions;
	}

	public void setProductOptions(ProductOption[] productOptions) {
		this.productOptions = productOptions;

		_productOptionsSupplier = null;
	}

	@JsonIgnore
	public void setProductOptions(
		UnsafeSupplier<ProductOption[], Exception>
			productOptionsUnsafeSupplier) {

		_productOptionsSupplier = () -> {
			try {
				return productOptionsUnsafeSupplier.get();
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
	protected ProductOption[] productOptions;

	@JsonIgnore
	private Supplier<ProductOption[]> _productOptionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ProductSpecification[] getProductSpecifications() {
		if (_productSpecificationsSupplier != null) {
			productSpecifications = _productSpecificationsSupplier.get();

			_productSpecificationsSupplier = null;
		}

		return productSpecifications;
	}

	public void setProductSpecifications(
		ProductSpecification[] productSpecifications) {

		this.productSpecifications = productSpecifications;

		_productSpecificationsSupplier = null;
	}

	@JsonIgnore
	public void setProductSpecifications(
		UnsafeSupplier<ProductSpecification[], Exception>
			productSpecificationsUnsafeSupplier) {

		_productSpecificationsSupplier = () -> {
			try {
				return productSpecificationsUnsafeSupplier.get();
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
	protected ProductSpecification[] productSpecifications;

	@JsonIgnore
	private Supplier<ProductSpecification[]> _productSpecificationsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getProductStatus() {
		if (_productStatusSupplier != null) {
			productStatus = _productStatusSupplier.get();

			_productStatusSupplier = null;
		}

		return productStatus;
	}

	public void setProductStatus(Integer productStatus) {
		this.productStatus = productStatus;

		_productStatusSupplier = null;
	}

	@JsonIgnore
	public void setProductStatus(
		UnsafeSupplier<Integer, Exception> productStatusUnsafeSupplier) {

		_productStatusSupplier = () -> {
			try {
				return productStatusUnsafeSupplier.get();
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
	protected Integer productStatus;

	@JsonIgnore
	private Supplier<Integer> _productStatusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "simple")
	public String getProductType() {
		if (_productTypeSupplier != null) {
			productType = _productTypeSupplier.get();

			_productTypeSupplier = null;
		}

		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;

		_productTypeSupplier = null;
	}

	@JsonIgnore
	public void setProductType(
		UnsafeSupplier<String, Exception> productTypeUnsafeSupplier) {

		_productTypeSupplier = () -> {
			try {
				return productTypeUnsafeSupplier.get();
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
	protected String productType;

	@JsonIgnore
	private Supplier<String> _productTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "simple")
	public String getProductTypeI18n() {
		if (_productTypeI18nSupplier != null) {
			productTypeI18n = _productTypeI18nSupplier.get();

			_productTypeI18nSupplier = null;
		}

		return productTypeI18n;
	}

	public void setProductTypeI18n(String productTypeI18n) {
		this.productTypeI18n = productTypeI18n;

		_productTypeI18nSupplier = null;
	}

	@JsonIgnore
	public void setProductTypeI18n(
		UnsafeSupplier<String, Exception> productTypeI18nUnsafeSupplier) {

		_productTypeI18nSupplier = () -> {
			try {
				return productTypeI18nUnsafeSupplier.get();
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
	protected String productTypeI18n;

	@JsonIgnore
	private Supplier<String> _productTypeI18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ProductVirtualSettings getProductVirtualSettings() {
		if (_productVirtualSettingsSupplier != null) {
			productVirtualSettings = _productVirtualSettingsSupplier.get();

			_productVirtualSettingsSupplier = null;
		}

		return productVirtualSettings;
	}

	public void setProductVirtualSettings(
		ProductVirtualSettings productVirtualSettings) {

		this.productVirtualSettings = productVirtualSettings;

		_productVirtualSettingsSupplier = null;
	}

	@JsonIgnore
	public void setProductVirtualSettings(
		UnsafeSupplier<ProductVirtualSettings, Exception>
			productVirtualSettingsUnsafeSupplier) {

		_productVirtualSettingsSupplier = () -> {
			try {
				return productVirtualSettingsUnsafeSupplier.get();
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
	protected ProductVirtualSettings productVirtualSettings;

	@JsonIgnore
	private Supplier<ProductVirtualSettings> _productVirtualSettingsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public RelatedProduct[] getRelatedProducts() {
		if (_relatedProductsSupplier != null) {
			relatedProducts = _relatedProductsSupplier.get();

			_relatedProductsSupplier = null;
		}

		return relatedProducts;
	}

	public void setRelatedProducts(RelatedProduct[] relatedProducts) {
		this.relatedProducts = relatedProducts;

		_relatedProductsSupplier = null;
	}

	@JsonIgnore
	public void setRelatedProducts(
		UnsafeSupplier<RelatedProduct[], Exception>
			relatedProductsUnsafeSupplier) {

		_relatedProductsSupplier = () -> {
			try {
				return relatedProductsUnsafeSupplier.get();
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
	protected RelatedProduct[] relatedProducts;

	@JsonIgnore
	private Supplier<RelatedProduct[]> _relatedProductsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ProductShippingConfiguration getShippingConfiguration() {
		if (_shippingConfigurationSupplier != null) {
			shippingConfiguration = _shippingConfigurationSupplier.get();

			_shippingConfigurationSupplier = null;
		}

		return shippingConfiguration;
	}

	public void setShippingConfiguration(
		ProductShippingConfiguration shippingConfiguration) {

		this.shippingConfiguration = shippingConfiguration;

		_shippingConfigurationSupplier = null;
	}

	@JsonIgnore
	public void setShippingConfiguration(
		UnsafeSupplier<ProductShippingConfiguration, Exception>
			shippingConfigurationUnsafeSupplier) {

		_shippingConfigurationSupplier = () -> {
			try {
				return shippingConfigurationUnsafeSupplier.get();
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
	protected ProductShippingConfiguration shippingConfiguration;

	@JsonIgnore
	private Supplier<ProductShippingConfiguration>
		_shippingConfigurationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "{en_US=Hand stainless steel saw for wood, hr_HR=Product Short Description HR, hu_HU=Product Short Description HU}"
	)
	@Valid
	public Map<String, String> getShortDescription() {
		if (_shortDescriptionSupplier != null) {
			shortDescription = _shortDescriptionSupplier.get();

			_shortDescriptionSupplier = null;
		}

		return shortDescription;
	}

	public void setShortDescription(Map<String, String> shortDescription) {
		this.shortDescription = shortDescription;

		_shortDescriptionSupplier = null;
	}

	@JsonIgnore
	public void setShortDescription(
		UnsafeSupplier<Map<String, String>, Exception>
			shortDescriptionUnsafeSupplier) {

		_shortDescriptionSupplier = () -> {
			try {
				return shortDescriptionUnsafeSupplier.get();
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
	protected Map<String, String> shortDescription;

	@JsonIgnore
	private Supplier<Map<String, String>> _shortDescriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "default")
	public String getSkuFormatted() {
		if (_skuFormattedSupplier != null) {
			skuFormatted = _skuFormattedSupplier.get();

			_skuFormattedSupplier = null;
		}

		return skuFormatted;
	}

	public void setSkuFormatted(String skuFormatted) {
		this.skuFormatted = skuFormatted;

		_skuFormattedSupplier = null;
	}

	@JsonIgnore
	public void setSkuFormatted(
		UnsafeSupplier<String, Exception> skuFormattedUnsafeSupplier) {

		_skuFormattedSupplier = () -> {
			try {
				return skuFormattedUnsafeSupplier.get();
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
	protected String skuFormatted;

	@JsonIgnore
	private Supplier<String> _skuFormattedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Sku[] getSkus() {
		if (_skusSupplier != null) {
			skus = _skusSupplier.get();

			_skusSupplier = null;
		}

		return skus;
	}

	public void setSkus(Sku[] skus) {
		this.skus = skus;

		_skusSupplier = null;
	}

	@JsonIgnore
	public void setSkus(UnsafeSupplier<Sku[], Exception> skusUnsafeSupplier) {
		_skusSupplier = () -> {
			try {
				return skusUnsafeSupplier.get();
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
	protected Sku[] skus;

	@JsonIgnore
	private Supplier<Sku[]> _skusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ProductSubscriptionConfiguration getSubscriptionConfiguration() {
		if (_subscriptionConfigurationSupplier != null) {
			subscriptionConfiguration =
				_subscriptionConfigurationSupplier.get();

			_subscriptionConfigurationSupplier = null;
		}

		return subscriptionConfiguration;
	}

	public void setSubscriptionConfiguration(
		ProductSubscriptionConfiguration subscriptionConfiguration) {

		this.subscriptionConfiguration = subscriptionConfiguration;

		_subscriptionConfigurationSupplier = null;
	}

	@JsonIgnore
	public void setSubscriptionConfiguration(
		UnsafeSupplier<ProductSubscriptionConfiguration, Exception>
			subscriptionConfigurationUnsafeSupplier) {

		_subscriptionConfigurationSupplier = () -> {
			try {
				return subscriptionConfigurationUnsafeSupplier.get();
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
	protected ProductSubscriptionConfiguration subscriptionConfiguration;

	@JsonIgnore
	private Supplier<ProductSubscriptionConfiguration>
		_subscriptionConfigurationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "[tag1, tag2, tag3]")
	public String[] getTags() {
		if (_tagsSupplier != null) {
			tags = _tagsSupplier.get();

			_tagsSupplier = null;
		}

		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;

		_tagsSupplier = null;
	}

	@JsonIgnore
	public void setTags(
		UnsafeSupplier<String[], Exception> tagsUnsafeSupplier) {

		_tagsSupplier = () -> {
			try {
				return tagsUnsafeSupplier.get();
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
	protected String[] tags;

	@JsonIgnore
	private Supplier<String[]> _tagsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ProductTaxConfiguration getTaxConfiguration() {
		if (_taxConfigurationSupplier != null) {
			taxConfiguration = _taxConfigurationSupplier.get();

			_taxConfigurationSupplier = null;
		}

		return taxConfiguration;
	}

	public void setTaxConfiguration(ProductTaxConfiguration taxConfiguration) {
		this.taxConfiguration = taxConfiguration;

		_taxConfigurationSupplier = null;
	}

	@JsonIgnore
	public void setTaxConfiguration(
		UnsafeSupplier<ProductTaxConfiguration, Exception>
			taxConfigurationUnsafeSupplier) {

		_taxConfigurationSupplier = () -> {
			try {
				return taxConfigurationUnsafeSupplier.get();
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
	protected ProductTaxConfiguration taxConfiguration;

	@JsonIgnore
	private Supplier<ProductTaxConfiguration> _taxConfigurationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "simple")
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String thumbnail;

	@JsonIgnore
	private Supplier<String> _thumbnailSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "{en_US=product-url-us, hr_HR=product-url-hr, hu_HU=product-url-hu}"
	)
	@Valid
	public Map<String, String> getUrls() {
		if (_urlsSupplier != null) {
			urls = _urlsSupplier.get();

			_urlsSupplier = null;
		}

		return urls;
	}

	public void setUrls(Map<String, String> urls) {
		this.urls = urls;

		_urlsSupplier = null;
	}

	@JsonIgnore
	public void setUrls(
		UnsafeSupplier<Map<String, String>, Exception> urlsUnsafeSupplier) {

		_urlsSupplier = () -> {
			try {
				return urlsUnsafeSupplier.get();
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
	protected Map<String, String> urls;

	@JsonIgnore
	private Supplier<Map<String, String>> _urlsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getVersion() {
		if (_versionSupplier != null) {
			version = _versionSupplier.get();

			_versionSupplier = null;
		}

		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;

		_versionSupplier = null;
	}

	@JsonIgnore
	public void setVersion(
		UnsafeSupplier<Integer, Exception> versionUnsafeSupplier) {

		_versionSupplier = () -> {
			try {
				return versionUnsafeSupplier.get();
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
	protected Integer version;

	@JsonIgnore
	private Supplier<Integer> _versionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Status getWorkflowStatusInfo() {
		if (_workflowStatusInfoSupplier != null) {
			workflowStatusInfo = _workflowStatusInfoSupplier.get();

			_workflowStatusInfoSupplier = null;
		}

		return workflowStatusInfo;
	}

	public void setWorkflowStatusInfo(Status workflowStatusInfo) {
		this.workflowStatusInfo = workflowStatusInfo;

		_workflowStatusInfoSupplier = null;
	}

	@JsonIgnore
	public void setWorkflowStatusInfo(
		UnsafeSupplier<Status, Exception> workflowStatusInfoUnsafeSupplier) {

		_workflowStatusInfoSupplier = () -> {
			try {
				return workflowStatusInfoUnsafeSupplier.get();
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
	protected Status workflowStatusInfo;

	@JsonIgnore
	private Supplier<Status> _workflowStatusInfoSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Product)) {
			return false;
		}

		Product product = (Product)object;

		return Objects.equals(toString(), product.toString());
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

		Attachment[] attachments = getAttachments();

		if (attachments != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attachments\": ");

			sb.append("[");

			for (int i = 0; i < attachments.length; i++) {
				sb.append(String.valueOf(attachments[i]));

				if ((i + 1) < attachments.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Catalog catalog = getCatalog();

		if (catalog != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"catalog\": ");

			sb.append(String.valueOf(catalog));
		}

		String catalogExternalReferenceCode = getCatalogExternalReferenceCode();

		if (catalogExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"catalogExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(catalogExternalReferenceCode));

			sb.append("\"");
		}

		Long catalogId = getCatalogId();

		if (catalogId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"catalogId\": ");

			sb.append(catalogId);
		}

		Category[] categories = getCategories();

		if (categories != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"categories\": ");

			sb.append("[");

			for (int i = 0; i < categories.length; i++) {
				sb.append(String.valueOf(categories[i]));

				if ((i + 1) < categories.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Date createDate = getCreateDate();

		if (createDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"createDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(createDate));

			sb.append("\"");
		}

		CustomField[] customFields = getCustomFields();

		if (customFields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < customFields.length; i++) {
				sb.append(String.valueOf(customFields[i]));

				if ((i + 1) < customFields.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String defaultSku = getDefaultSku();

		if (defaultSku != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultSku\": ");

			sb.append("\"");

			sb.append(_escape(defaultSku));

			sb.append("\"");
		}

		Map<String, String> description = getDescription();

		if (description != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append(_toJSON(description));
		}

		Diagram diagram = getDiagram();

		if (diagram != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"diagram\": ");

			sb.append(String.valueOf(diagram));
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

		Map<String, ?> expando = getExpando();

		if (expando != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expando\": ");

			sb.append(_toJSON(expando));
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

		Attachment[] images = getImages();

		if (images != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"images\": ");

			sb.append("[");

			for (int i = 0; i < images.length; i++) {
				sb.append(String.valueOf(images[i]));

				if ((i + 1) < images.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		LinkedProduct[] linkedProducts = getLinkedProducts();

		if (linkedProducts != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"linkedProducts\": ");

			sb.append("[");

			for (int i = 0; i < linkedProducts.length; i++) {
				sb.append(String.valueOf(linkedProducts[i]));

				if ((i + 1) < linkedProducts.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		MappedProduct[] mappedProducts = getMappedProducts();

		if (mappedProducts != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"mappedProducts\": ");

			sb.append("[");

			for (int i = 0; i < mappedProducts.length; i++) {
				sb.append(String.valueOf(mappedProducts[i]));

				if ((i + 1) < mappedProducts.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Map<String, String> metaDescription = getMetaDescription();

		if (metaDescription != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metaDescription\": ");

			sb.append(_toJSON(metaDescription));
		}

		Map<String, String> metaKeyword = getMetaKeyword();

		if (metaKeyword != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metaKeyword\": ");

			sb.append(_toJSON(metaKeyword));
		}

		Map<String, String> metaTitle = getMetaTitle();

		if (metaTitle != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metaTitle\": ");

			sb.append(_toJSON(metaTitle));
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

		Map<String, String> name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(name));
		}

		Boolean neverExpire = getNeverExpire();

		if (neverExpire != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"neverExpire\": ");

			sb.append(neverExpire);
		}

		Pin[] pins = getPins();

		if (pins != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pins\": ");

			sb.append("[");

			for (int i = 0; i < pins.length; i++) {
				sb.append(String.valueOf(pins[i]));

				if ((i + 1) < pins.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean productAccountGroupFilter = getProductAccountGroupFilter();

		if (productAccountGroupFilter != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productAccountGroupFilter\": ");

			sb.append(productAccountGroupFilter);
		}

		ProductAccountGroup[] productAccountGroups = getProductAccountGroups();

		if (productAccountGroups != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productAccountGroups\": ");

			sb.append("[");

			for (int i = 0; i < productAccountGroups.length; i++) {
				sb.append(String.valueOf(productAccountGroups[i]));

				if ((i + 1) < productAccountGroups.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean productChannelFilter = getProductChannelFilter();

		if (productChannelFilter != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productChannelFilter\": ");

			sb.append(productChannelFilter);
		}

		ProductChannel[] productChannels = getProductChannels();

		if (productChannels != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productChannels\": ");

			sb.append("[");

			for (int i = 0; i < productChannels.length; i++) {
				sb.append(String.valueOf(productChannels[i]));

				if ((i + 1) < productChannels.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		ProductConfiguration productConfiguration = getProductConfiguration();

		if (productConfiguration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfiguration\": ");

			sb.append(String.valueOf(productConfiguration));
		}

		Long productId = getProductId();

		if (productId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productId\": ");

			sb.append(productId);
		}

		ProductOption[] productOptions = getProductOptions();

		if (productOptions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productOptions\": ");

			sb.append("[");

			for (int i = 0; i < productOptions.length; i++) {
				sb.append(String.valueOf(productOptions[i]));

				if ((i + 1) < productOptions.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		ProductSpecification[] productSpecifications =
			getProductSpecifications();

		if (productSpecifications != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productSpecifications\": ");

			sb.append("[");

			for (int i = 0; i < productSpecifications.length; i++) {
				sb.append(String.valueOf(productSpecifications[i]));

				if ((i + 1) < productSpecifications.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Integer productStatus = getProductStatus();

		if (productStatus != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productStatus\": ");

			sb.append(productStatus);
		}

		String productType = getProductType();

		if (productType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productType\": ");

			sb.append("\"");

			sb.append(_escape(productType));

			sb.append("\"");
		}

		String productTypeI18n = getProductTypeI18n();

		if (productTypeI18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productTypeI18n\": ");

			sb.append("\"");

			sb.append(_escape(productTypeI18n));

			sb.append("\"");
		}

		ProductVirtualSettings productVirtualSettings =
			getProductVirtualSettings();

		if (productVirtualSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productVirtualSettings\": ");

			sb.append(String.valueOf(productVirtualSettings));
		}

		RelatedProduct[] relatedProducts = getRelatedProducts();

		if (relatedProducts != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"relatedProducts\": ");

			sb.append("[");

			for (int i = 0; i < relatedProducts.length; i++) {
				sb.append(String.valueOf(relatedProducts[i]));

				if ((i + 1) < relatedProducts.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		ProductShippingConfiguration shippingConfiguration =
			getShippingConfiguration();

		if (shippingConfiguration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingConfiguration\": ");

			sb.append(String.valueOf(shippingConfiguration));
		}

		Map<String, String> shortDescription = getShortDescription();

		if (shortDescription != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shortDescription\": ");

			sb.append(_toJSON(shortDescription));
		}

		String skuFormatted = getSkuFormatted();

		if (skuFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuFormatted\": ");

			sb.append("\"");

			sb.append(_escape(skuFormatted));

			sb.append("\"");
		}

		Sku[] skus = getSkus();

		if (skus != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skus\": ");

			sb.append("[");

			for (int i = 0; i < skus.length; i++) {
				sb.append(String.valueOf(skus[i]));

				if ((i + 1) < skus.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		ProductSubscriptionConfiguration subscriptionConfiguration =
			getSubscriptionConfiguration();

		if (subscriptionConfiguration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subscriptionConfiguration\": ");

			sb.append(String.valueOf(subscriptionConfiguration));
		}

		String[] tags = getTags();

		if (tags != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tags\": ");

			sb.append("[");

			for (int i = 0; i < tags.length; i++) {
				sb.append("\"");

				sb.append(_escape(tags[i]));

				sb.append("\"");

				if ((i + 1) < tags.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		ProductTaxConfiguration taxConfiguration = getTaxConfiguration();

		if (taxConfiguration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxConfiguration\": ");

			sb.append(String.valueOf(taxConfiguration));
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

		Map<String, String> urls = getUrls();

		if (urls != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"urls\": ");

			sb.append(_toJSON(urls));
		}

		Integer version = getVersion();

		if (version != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"version\": ");

			sb.append(version);
		}

		Status workflowStatusInfo = getWorkflowStatusInfo();

		if (workflowStatusInfo != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowStatusInfo\": ");

			sb.append(String.valueOf(workflowStatusInfo));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product",
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