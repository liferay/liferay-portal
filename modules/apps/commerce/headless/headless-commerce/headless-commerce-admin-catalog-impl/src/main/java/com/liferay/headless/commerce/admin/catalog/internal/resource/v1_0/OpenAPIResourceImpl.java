/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.resource.v1_0;

import com.liferay.portal.vulcan.resource.OpenAPIResource;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.lang.reflect.Method;

import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Zoltán Takács
 * @generated
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/openapi.properties",
	service = OpenAPIResourceImpl.class
)
@Generated("")
@OpenAPIDefinition(
	info = @Info(description = "Administrative REST API for the Liferay Commerce catalog. It manages every entity that defines what is for sale -- catalogs, currencies, categories, products, SKUs, options and option values, specifications, attachments, diagrams, pins, and the configuration that ties them to channels, accounts, and order types. The API targets back-office administrators and integration tooling; storefront browsing belongs to the headless-commerce-delivery-catalog module instead. Most resources are addressable both by internal ID (`/<id>`) and by external reference code (`/by-externalReferenceCode/<externalReferenceCode>`); POST against the external-reference-code path is upsert. PATCH applies JSON Merge Patch (only the supplied fields are modified), while PUT replaces the resource. For the storefront, customer-facing counterpart see `headless-commerce-delivery-catalog`; for pricing, account, channel, order, and inventory data see the matching `headless-commerce-admin-*` modules. -- Common workflows: Provision a catalog with its base currency -- (1) GET /currencies to confirm the desired CommerceCurrency is provisioned, otherwise POST /currencies (or POST /currencies/by-externalReferenceCode/<externalReferenceCode>) to add it; (2) POST /catalogs with name and commerceCurrencyCode to create the CommerceCatalog every product, SKU, and category will live under; (3) PATCH /catalog/<id> (JSON Merge Patch) to rename it or change the default currency. -- Author a configurable product end-to-end -- (1) POST /options to define a reusable Option master, then POST /options/<id>/optionValues to declare its allowed OptionValues; (2) POST /products with productType, catalogId, and base fields to create the CPDefinition (POST /products/by-externalReferenceCode/<externalReferenceCode> is upsert); (3) POST /products/<id>/productOptions to attach the option to the product, then POST /productOptions/<id>/productOptionValues to whitelist the values that apply; (4) POST /products/<id>/skus per option-value combination to create the purchasable Sku variants; (5) POST /products/<id>/images and /products/<id>/attachments (also the by-base64 and by-url variants) to upload product media. -- Configure shop-by-diagram navigation -- (1) POST /products/<id>/diagrams to upload the diagram image to the parent product; (2) POST /products/<id>/pins per hotspot to anchor coordinates on the diagram; (3) POST /products/<id>/mapped-products to bind each pin to a child product or SKU so the storefront can route from the hotspot to the purchasable variant. -- Publish a product to channels and account groups -- (1) POST /products/<id>/product-channels to expose the product on each CommerceChannel; (2) POST /products/<id>/product-account-groups to restrict visibility to specific AccountGroups; (3) POST /product-configuration-lists, POST /product-configuration-lists/<id>/product-configurations, and POST /product-configuration-lists/<id>/product-configuration-list-channels (and the matching -accounts, -account-groups, and -order-types endpoints) to define a reusable configuration bundle, then PATCH /products/<id>/configuration to attach the list. -- Define reusable product specifications and option categories -- (1) POST /optionCategories to group specifications and options under a category; (2) POST /specifications, optionally followed by POST /specifications/<id>/list-type-definitions to lock the allowed values to a ListTypeDefinition; (3) POST /products/<id>/productSpecifications to attach a specification value to a product. -- Manage SKU virtual delivery and unit-of-measure tiers -- (1) POST /skus/<id>/sku-virtual-settings followed by POST /sku-virtual-settings/<id>/sku-virtual-settings-file-entries to enable digital-good delivery for downloadable SKUs; (2) POST /skus/<id>/sku-unit-of-measures to define tiered units (for example, case or pallet) on top of the base unit, and GET /unit-of-measure-skus to enumerate SKUs by a unit-of-measure key. A Java client JAR is available for use with the group ID 'com.liferay', artifact ID 'com.liferay.headless.commerce.admin.catalog.client', and version '4.0.80'.", license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html"), title = "Liferay Commerce Admin Catalog API", version = "v1.0")
)
@Path("/v1.0")
public class OpenAPIResourceImpl {

	@GET
	@Path("/openapi.{type:json|yaml}")
	@Produces({MediaType.APPLICATION_JSON, "application/yaml"})
	public Response getOpenAPI(
			@Context HttpServletRequest httpServletRequest,
			@PathParam("type") String type, @Context UriInfo uriInfo)
		throws Exception {

		Class<? extends OpenAPIResource> clazz = _openAPIResource.getClass();

		try {
			Method method = clazz.getMethod(
				"getOpenAPI", HttpServletRequest.class, Set.class, String.class,
				UriInfo.class);

			return (Response)method.invoke(
				_openAPIResource, httpServletRequest, _resourceClasses, type,
				uriInfo);
		}
		catch (NoSuchMethodException noSuchMethodException1) {
			try {
				Method method = clazz.getMethod(
					"getOpenAPI", Set.class, String.class, UriInfo.class);

				return (Response)method.invoke(
					_openAPIResource, _resourceClasses, type, uriInfo);
			}
			catch (NoSuchMethodException noSuchMethodException2) {
				return _openAPIResource.getOpenAPI(_resourceClasses, type);
			}
		}
	}

	@Reference
	private OpenAPIResource _openAPIResource;

	private final Set<Class<?>> _resourceClasses = new HashSet<Class<?>>() {
		{
			add(AttachmentResourceImpl.class);

			add(CatalogResourceImpl.class);

			add(CategoryResourceImpl.class);

			add(CurrencyResourceImpl.class);

			add(DiagramResourceImpl.class);

			add(GroupedProductResourceImpl.class);

			add(LinkedProductResourceImpl.class);

			add(ListTypeDefinitionResourceImpl.class);

			add(LowStockActionResourceImpl.class);

			add(MappedProductResourceImpl.class);

			add(OptionResourceImpl.class);

			add(OptionCategoryResourceImpl.class);

			add(OptionValueResourceImpl.class);

			add(PinResourceImpl.class);

			add(ProductResourceImpl.class);

			add(ProductAccountGroupResourceImpl.class);

			add(ProductChannelResourceImpl.class);

			add(ProductConfigurationResourceImpl.class);

			add(ProductConfigurationListResourceImpl.class);

			add(ProductConfigurationListAccountResourceImpl.class);

			add(ProductConfigurationListAccountGroupResourceImpl.class);

			add(ProductConfigurationListChannelResourceImpl.class);

			add(ProductConfigurationListOrderTypeResourceImpl.class);

			add(ProductGroupResourceImpl.class);

			add(ProductGroupProductResourceImpl.class);

			add(ProductOptionResourceImpl.class);

			add(ProductOptionValueResourceImpl.class);

			add(ProductShippingConfigurationResourceImpl.class);

			add(ProductSpecificationResourceImpl.class);

			add(ProductSubscriptionConfigurationResourceImpl.class);

			add(ProductTaxConfigurationResourceImpl.class);

			add(ProductVirtualSettingsResourceImpl.class);

			add(ProductVirtualSettingsFileEntryResourceImpl.class);

			add(RelatedProductResourceImpl.class);

			add(SkuResourceImpl.class);

			add(SkuSubscriptionConfigurationResourceImpl.class);

			add(SkuUnitOfMeasureResourceImpl.class);

			add(SkuVirtualSettingsResourceImpl.class);

			add(SkuVirtualSettingsFileEntryResourceImpl.class);

			add(SpecificationResourceImpl.class);

			add(OpenAPIResourceImpl.class);
		}
	};

}
// LIFERAY-REST-BUILDER-HASH:-1710379677