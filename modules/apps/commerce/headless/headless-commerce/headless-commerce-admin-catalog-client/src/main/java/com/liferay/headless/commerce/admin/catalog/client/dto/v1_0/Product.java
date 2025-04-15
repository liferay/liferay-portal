/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.dto.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.ProductSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class Product implements Cloneable, Serializable {

	public static Product toDTO(String json) {
		return ProductSerDes.toDTO(json);
	}

	public Map<String, Map<String, String>> getActions() {
		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;
	}

	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		try {
			actions = actionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Map<String, String>> actions;

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		try {
			active = activeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean active;

	public Attachment[] getAttachments() {
		return attachments;
	}

	public void setAttachments(Attachment[] attachments) {
		this.attachments = attachments;
	}

	public void setAttachments(
		UnsafeSupplier<Attachment[], Exception> attachmentsUnsafeSupplier) {

		try {
			attachments = attachmentsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Attachment[] attachments;

	public Catalog getCatalog() {
		return catalog;
	}

	public void setCatalog(Catalog catalog) {
		this.catalog = catalog;
	}

	public void setCatalog(
		UnsafeSupplier<Catalog, Exception> catalogUnsafeSupplier) {

		try {
			catalog = catalogUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Catalog catalog;

	public String getCatalogExternalReferenceCode() {
		return catalogExternalReferenceCode;
	}

	public void setCatalogExternalReferenceCode(
		String catalogExternalReferenceCode) {

		this.catalogExternalReferenceCode = catalogExternalReferenceCode;
	}

	public void setCatalogExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			catalogExternalReferenceCodeUnsafeSupplier) {

		try {
			catalogExternalReferenceCode =
				catalogExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String catalogExternalReferenceCode;

	public Long getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(Long catalogId) {
		this.catalogId = catalogId;
	}

	public void setCatalogId(
		UnsafeSupplier<Long, Exception> catalogIdUnsafeSupplier) {

		try {
			catalogId = catalogIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long catalogId;

	public Category[] getCategories() {
		return categories;
	}

	public void setCategories(Category[] categories) {
		this.categories = categories;
	}

	public void setCategories(
		UnsafeSupplier<Category[], Exception> categoriesUnsafeSupplier) {

		try {
			categories = categoriesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Category[] categories;

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public void setCreateDate(
		UnsafeSupplier<Date, Exception> createDateUnsafeSupplier) {

		try {
			createDate = createDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date createDate;

	public com.liferay.headless.commerce.admin.catalog.client.custom.field.
		CustomField[] getCustomFields() {

		return customFields;
	}

	public void setCustomFields(
		com.liferay.headless.commerce.admin.catalog.client.custom.field.
			CustomField[] customFields) {

		this.customFields = customFields;
	}

	public void setCustomFields(
		UnsafeSupplier
			<com.liferay.headless.commerce.admin.catalog.client.custom.field.
				CustomField[],
			 Exception> customFieldsUnsafeSupplier) {

		try {
			customFields = customFieldsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected com.liferay.headless.commerce.admin.catalog.client.custom.field.
		CustomField[] customFields;

	public String getDefaultSku() {
		return defaultSku;
	}

	public void setDefaultSku(String defaultSku) {
		this.defaultSku = defaultSku;
	}

	public void setDefaultSku(
		UnsafeSupplier<String, Exception> defaultSkuUnsafeSupplier) {

		try {
			defaultSku = defaultSkuUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String defaultSku;

	public Map<String, String> getDescription() {
		return description;
	}

	public void setDescription(Map<String, String> description) {
		this.description = description;
	}

	public void setDescription(
		UnsafeSupplier<Map<String, String>, Exception>
			descriptionUnsafeSupplier) {

		try {
			description = descriptionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> description;

	public Diagram getDiagram() {
		return diagram;
	}

	public void setDiagram(Diagram diagram) {
		this.diagram = diagram;
	}

	public void setDiagram(
		UnsafeSupplier<Diagram, Exception> diagramUnsafeSupplier) {

		try {
			diagram = diagramUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Diagram diagram;

	public Date getDisplayDate() {
		return displayDate;
	}

	public void setDisplayDate(Date displayDate) {
		this.displayDate = displayDate;
	}

	public void setDisplayDate(
		UnsafeSupplier<Date, Exception> displayDateUnsafeSupplier) {

		try {
			displayDate = displayDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date displayDate;

	public Map<String, ?> getExpando() {
		return expando;
	}

	public void setExpando(Map<String, ?> expando) {
		this.expando = expando;
	}

	public void setExpando(
		UnsafeSupplier<Map<String, ?>, Exception> expandoUnsafeSupplier) {

		try {
			expando = expandoUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, ?> expando;

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public void setExpirationDate(
		UnsafeSupplier<Date, Exception> expirationDateUnsafeSupplier) {

		try {
			expirationDate = expirationDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date expirationDate;

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public Attachment[] getImages() {
		return images;
	}

	public void setImages(Attachment[] images) {
		this.images = images;
	}

	public void setImages(
		UnsafeSupplier<Attachment[], Exception> imagesUnsafeSupplier) {

		try {
			images = imagesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Attachment[] images;

	public LinkedProduct[] getLinkedProducts() {
		return linkedProducts;
	}

	public void setLinkedProducts(LinkedProduct[] linkedProducts) {
		this.linkedProducts = linkedProducts;
	}

	public void setLinkedProducts(
		UnsafeSupplier<LinkedProduct[], Exception>
			linkedProductsUnsafeSupplier) {

		try {
			linkedProducts = linkedProductsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected LinkedProduct[] linkedProducts;

	public MappedProduct[] getMappedProducts() {
		return mappedProducts;
	}

	public void setMappedProducts(MappedProduct[] mappedProducts) {
		this.mappedProducts = mappedProducts;
	}

	public void setMappedProducts(
		UnsafeSupplier<MappedProduct[], Exception>
			mappedProductsUnsafeSupplier) {

		try {
			mappedProducts = mappedProductsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected MappedProduct[] mappedProducts;

	public Map<String, String> getMetaDescription() {
		return metaDescription;
	}

	public void setMetaDescription(Map<String, String> metaDescription) {
		this.metaDescription = metaDescription;
	}

	public void setMetaDescription(
		UnsafeSupplier<Map<String, String>, Exception>
			metaDescriptionUnsafeSupplier) {

		try {
			metaDescription = metaDescriptionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> metaDescription;

	public Map<String, String> getMetaKeyword() {
		return metaKeyword;
	}

	public void setMetaKeyword(Map<String, String> metaKeyword) {
		this.metaKeyword = metaKeyword;
	}

	public void setMetaKeyword(
		UnsafeSupplier<Map<String, String>, Exception>
			metaKeywordUnsafeSupplier) {

		try {
			metaKeyword = metaKeywordUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> metaKeyword;

	public Map<String, String> getMetaTitle() {
		return metaTitle;
	}

	public void setMetaTitle(Map<String, String> metaTitle) {
		this.metaTitle = metaTitle;
	}

	public void setMetaTitle(
		UnsafeSupplier<Map<String, String>, Exception>
			metaTitleUnsafeSupplier) {

		try {
			metaTitle = metaTitleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> metaTitle;

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public void setModifiedDate(
		UnsafeSupplier<Date, Exception> modifiedDateUnsafeSupplier) {

		try {
			modifiedDate = modifiedDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date modifiedDate;

	public Map<String, String> getName() {
		return name;
	}

	public void setName(Map<String, String> name) {
		this.name = name;
	}

	public void setName(
		UnsafeSupplier<Map<String, String>, Exception> nameUnsafeSupplier) {

		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> name;

	public Boolean getNeverExpire() {
		return neverExpire;
	}

	public void setNeverExpire(Boolean neverExpire) {
		this.neverExpire = neverExpire;
	}

	public void setNeverExpire(
		UnsafeSupplier<Boolean, Exception> neverExpireUnsafeSupplier) {

		try {
			neverExpire = neverExpireUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean neverExpire;

	public Pin[] getPins() {
		return pins;
	}

	public void setPins(Pin[] pins) {
		this.pins = pins;
	}

	public void setPins(UnsafeSupplier<Pin[], Exception> pinsUnsafeSupplier) {
		try {
			pins = pinsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Pin[] pins;

	public Boolean getProductAccountGroupFilter() {
		return productAccountGroupFilter;
	}

	public void setProductAccountGroupFilter(
		Boolean productAccountGroupFilter) {

		this.productAccountGroupFilter = productAccountGroupFilter;
	}

	public void setProductAccountGroupFilter(
		UnsafeSupplier<Boolean, Exception>
			productAccountGroupFilterUnsafeSupplier) {

		try {
			productAccountGroupFilter =
				productAccountGroupFilterUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean productAccountGroupFilter;

	public ProductAccountGroup[] getProductAccountGroups() {
		return productAccountGroups;
	}

	public void setProductAccountGroups(
		ProductAccountGroup[] productAccountGroups) {

		this.productAccountGroups = productAccountGroups;
	}

	public void setProductAccountGroups(
		UnsafeSupplier<ProductAccountGroup[], Exception>
			productAccountGroupsUnsafeSupplier) {

		try {
			productAccountGroups = productAccountGroupsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ProductAccountGroup[] productAccountGroups;

	public Boolean getProductChannelFilter() {
		return productChannelFilter;
	}

	public void setProductChannelFilter(Boolean productChannelFilter) {
		this.productChannelFilter = productChannelFilter;
	}

	public void setProductChannelFilter(
		UnsafeSupplier<Boolean, Exception> productChannelFilterUnsafeSupplier) {

		try {
			productChannelFilter = productChannelFilterUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean productChannelFilter;

	public ProductChannel[] getProductChannels() {
		return productChannels;
	}

	public void setProductChannels(ProductChannel[] productChannels) {
		this.productChannels = productChannels;
	}

	public void setProductChannels(
		UnsafeSupplier<ProductChannel[], Exception>
			productChannelsUnsafeSupplier) {

		try {
			productChannels = productChannelsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ProductChannel[] productChannels;

	public ProductConfiguration getProductConfiguration() {
		return productConfiguration;
	}

	public void setProductConfiguration(
		ProductConfiguration productConfiguration) {

		this.productConfiguration = productConfiguration;
	}

	public void setProductConfiguration(
		UnsafeSupplier<ProductConfiguration, Exception>
			productConfigurationUnsafeSupplier) {

		try {
			productConfiguration = productConfigurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ProductConfiguration productConfiguration;

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public void setProductId(
		UnsafeSupplier<Long, Exception> productIdUnsafeSupplier) {

		try {
			productId = productIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long productId;

	public ProductOption[] getProductOptions() {
		return productOptions;
	}

	public void setProductOptions(ProductOption[] productOptions) {
		this.productOptions = productOptions;
	}

	public void setProductOptions(
		UnsafeSupplier<ProductOption[], Exception>
			productOptionsUnsafeSupplier) {

		try {
			productOptions = productOptionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ProductOption[] productOptions;

	public ProductSpecification[] getProductSpecifications() {
		return productSpecifications;
	}

	public void setProductSpecifications(
		ProductSpecification[] productSpecifications) {

		this.productSpecifications = productSpecifications;
	}

	public void setProductSpecifications(
		UnsafeSupplier<ProductSpecification[], Exception>
			productSpecificationsUnsafeSupplier) {

		try {
			productSpecifications = productSpecificationsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ProductSpecification[] productSpecifications;

	public Integer getProductStatus() {
		return productStatus;
	}

	public void setProductStatus(Integer productStatus) {
		this.productStatus = productStatus;
	}

	public void setProductStatus(
		UnsafeSupplier<Integer, Exception> productStatusUnsafeSupplier) {

		try {
			productStatus = productStatusUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer productStatus;

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public void setProductType(
		UnsafeSupplier<String, Exception> productTypeUnsafeSupplier) {

		try {
			productType = productTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String productType;

	public String getProductTypeI18n() {
		return productTypeI18n;
	}

	public void setProductTypeI18n(String productTypeI18n) {
		this.productTypeI18n = productTypeI18n;
	}

	public void setProductTypeI18n(
		UnsafeSupplier<String, Exception> productTypeI18nUnsafeSupplier) {

		try {
			productTypeI18n = productTypeI18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String productTypeI18n;

	public ProductVirtualSettings getProductVirtualSettings() {
		return productVirtualSettings;
	}

	public void setProductVirtualSettings(
		ProductVirtualSettings productVirtualSettings) {

		this.productVirtualSettings = productVirtualSettings;
	}

	public void setProductVirtualSettings(
		UnsafeSupplier<ProductVirtualSettings, Exception>
			productVirtualSettingsUnsafeSupplier) {

		try {
			productVirtualSettings = productVirtualSettingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ProductVirtualSettings productVirtualSettings;

	public RelatedProduct[] getRelatedProducts() {
		return relatedProducts;
	}

	public void setRelatedProducts(RelatedProduct[] relatedProducts) {
		this.relatedProducts = relatedProducts;
	}

	public void setRelatedProducts(
		UnsafeSupplier<RelatedProduct[], Exception>
			relatedProductsUnsafeSupplier) {

		try {
			relatedProducts = relatedProductsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected RelatedProduct[] relatedProducts;

	public ProductShippingConfiguration getShippingConfiguration() {
		return shippingConfiguration;
	}

	public void setShippingConfiguration(
		ProductShippingConfiguration shippingConfiguration) {

		this.shippingConfiguration = shippingConfiguration;
	}

	public void setShippingConfiguration(
		UnsafeSupplier<ProductShippingConfiguration, Exception>
			shippingConfigurationUnsafeSupplier) {

		try {
			shippingConfiguration = shippingConfigurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ProductShippingConfiguration shippingConfiguration;

	public Map<String, String> getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(Map<String, String> shortDescription) {
		this.shortDescription = shortDescription;
	}

	public void setShortDescription(
		UnsafeSupplier<Map<String, String>, Exception>
			shortDescriptionUnsafeSupplier) {

		try {
			shortDescription = shortDescriptionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> shortDescription;

	public String getSkuFormatted() {
		return skuFormatted;
	}

	public void setSkuFormatted(String skuFormatted) {
		this.skuFormatted = skuFormatted;
	}

	public void setSkuFormatted(
		UnsafeSupplier<String, Exception> skuFormattedUnsafeSupplier) {

		try {
			skuFormatted = skuFormattedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String skuFormatted;

	public Sku[] getSkus() {
		return skus;
	}

	public void setSkus(Sku[] skus) {
		this.skus = skus;
	}

	public void setSkus(UnsafeSupplier<Sku[], Exception> skusUnsafeSupplier) {
		try {
			skus = skusUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Sku[] skus;

	public ProductSubscriptionConfiguration getSubscriptionConfiguration() {
		return subscriptionConfiguration;
	}

	public void setSubscriptionConfiguration(
		ProductSubscriptionConfiguration subscriptionConfiguration) {

		this.subscriptionConfiguration = subscriptionConfiguration;
	}

	public void setSubscriptionConfiguration(
		UnsafeSupplier<ProductSubscriptionConfiguration, Exception>
			subscriptionConfigurationUnsafeSupplier) {

		try {
			subscriptionConfiguration =
				subscriptionConfigurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ProductSubscriptionConfiguration subscriptionConfiguration;

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public void setTags(
		UnsafeSupplier<String[], Exception> tagsUnsafeSupplier) {

		try {
			tags = tagsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] tags;

	public ProductTaxConfiguration getTaxConfiguration() {
		return taxConfiguration;
	}

	public void setTaxConfiguration(ProductTaxConfiguration taxConfiguration) {
		this.taxConfiguration = taxConfiguration;
	}

	public void setTaxConfiguration(
		UnsafeSupplier<ProductTaxConfiguration, Exception>
			taxConfigurationUnsafeSupplier) {

		try {
			taxConfiguration = taxConfigurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ProductTaxConfiguration taxConfiguration;

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public void setThumbnail(
		UnsafeSupplier<String, Exception> thumbnailUnsafeSupplier) {

		try {
			thumbnail = thumbnailUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String thumbnail;

	public Map<String, String> getUrls() {
		return urls;
	}

	public void setUrls(Map<String, String> urls) {
		this.urls = urls;
	}

	public void setUrls(
		UnsafeSupplier<Map<String, String>, Exception> urlsUnsafeSupplier) {

		try {
			urls = urlsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> urls;

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public void setVersion(
		UnsafeSupplier<Integer, Exception> versionUnsafeSupplier) {

		try {
			version = versionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer version;

	public Status getWorkflowStatusInfo() {
		return workflowStatusInfo;
	}

	public void setWorkflowStatusInfo(Status workflowStatusInfo) {
		this.workflowStatusInfo = workflowStatusInfo;
	}

	public void setWorkflowStatusInfo(
		UnsafeSupplier<Status, Exception> workflowStatusInfoUnsafeSupplier) {

		try {
			workflowStatusInfo = workflowStatusInfoUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Status workflowStatusInfo;

	@Override
	public Product clone() throws CloneNotSupportedException {
		return (Product)super.clone();
	}

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
		return ProductSerDes.toJSON(this);
	}

}