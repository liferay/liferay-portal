/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.resource.v1_0;

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
 * @author Andrea Sbarra
 * @generated
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/openapi.properties",
	service = OpenAPIResourceImpl.class
)
@Generated("")
@OpenAPIDefinition(
	info = @Info(description = "Storefront REST API for the Liferay Commerce delivery (buyer-facing) catalog domain. Exposes channel-scoped read access to products, SKUs, prices, availability, attachments, categories, specifications, related and linked products, shop-by-diagram pins and mapped products, currencies, accounts, and wish lists, and supports write access for wish-list lifecycle and account creation under a channel. -- Primary entities: Channel, Account, Product, Sku, ProductOption, ProductOptionValue, ProductSpecification, RelatedProduct, LinkedProduct, MappedProduct, Pin, Attachment, Category, Currency, WishList, WishListItem. -- Perspective: buyer-facing (delivery) storefront. Every product, SKU, price, and availability response is computed under a CommerceContext keyed by the addressed channel, the resolved account (via AccountUtil) and the request locale. For the merchant-facing (admin) side of the same domain see headless-commerce-admin-catalog; for cart and checkout see headless-commerce-delivery-cart and headless-commerce-delivery-order. -- Common workflows: Browse the storefront catalog -- (1) GET /channels to enumerate CommerceChannel entries visible to the caller and pick a channelId; (2) GET /channels/<channelId>/products with `filter`, `search`, `sort`, `page`, and `pageSize` to page through the channel's published catalog; (3) GET /channels/<channelId>/products/<productId> (or /channels/<channelId>/products/by-friendly-url-path/<path>) for the product detail, then GET the product's attachments, images, categories, product-specifications, related products, linked products, and product-options to assemble the product page. -- Resolve a configurable product to a purchasable SKU -- (1) GET /channels/<channelId>/products/<productId>/product-options to enumerate the product's option groups and GET the product-option-values for each option; (2) POST /channels/<channelId>/products/<productId>/skus/by-sku-option with the selected option-value IDs to resolve the matching Sku, including channel- and account-scoped price and availability; (3) GET /channels/<channelId>/products/<productId>/skus/<skuId> to refetch the resolved SKU on demand. -- Browse a shop-by-diagram product -- (1) GET /channels/<channelId>/products/<productId>/pins to enumerate the diagram hotspots defined on a parent product; (2) GET /channels/<channelId>/products/<productId>/mapped-products to fetch the child products mapped behind each pin and feed them back into the product detail flow above. -- Onboard a buyer account under a channel -- (1) GET /channels/<channelId>/accounts to confirm the caller does not already have a matching account; (2) POST /channels/<channelId>/accounts with the buyer profile to create the Account that becomes the CommerceContext account on subsequent price and availability calls. -- Manage a buyer wish list -- (1) POST /channels/<channelId>/wishlists (or POST /channels/by-externalReferenceCode/<externalReferenceCode>/wishlists) to create a wish list under the channel; (2) POST /wishlists/<wishListId>/wishlist-items with a productId/skuId pair to add an item; (3) PATCH /wishlists/<wishListId> (JSON Merge Patch) to rename the list or toggle the default flag; (4) DELETE /wishlist-items/<wishListItemId> to remove a single item or DELETE /wishlists/<wishListId> to remove the entire list. A Java client JAR is available for use with the group ID 'com.liferay', artifact ID 'com.liferay.headless.commerce.delivery.catalog.client', and version '4.0.68'.", license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html"), title = "Liferay Commerce Delivery Catalog API", version = "v1.0")
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
			add(AccountResourceImpl.class);

			add(AttachmentResourceImpl.class);

			add(CategoryResourceImpl.class);

			add(ChannelResourceImpl.class);

			add(CurrencyResourceImpl.class);

			add(LinkedProductResourceImpl.class);

			add(MappedProductResourceImpl.class);

			add(PinResourceImpl.class);

			add(ProductResourceImpl.class);

			add(ProductOptionResourceImpl.class);

			add(ProductOptionValueResourceImpl.class);

			add(ProductSpecificationResourceImpl.class);

			add(RelatedProductResourceImpl.class);

			add(SkuResourceImpl.class);

			add(WishListResourceImpl.class);

			add(WishListItemResourceImpl.class);

			add(OpenAPIResourceImpl.class);
		}
	};

}
// LIFERAY-REST-BUILDER-HASH:-1407894710