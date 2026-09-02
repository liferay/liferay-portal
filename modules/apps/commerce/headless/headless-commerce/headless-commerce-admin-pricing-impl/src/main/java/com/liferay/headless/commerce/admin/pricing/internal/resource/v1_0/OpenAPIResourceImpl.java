/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.resource.v1_0;

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
	info = @Info(description = "Administrative REST API for the Liferay Commerce pricing domain. It manages discount programs and price-list catalogs used to price products for buyers -- discounts (with their coupon codes, tier percentages, expiration windows, and account-group/category/product/rule scope), price lists (with their per-SKU price entries, tier-quantity break prices, and account-group eligibility), and the rules that decide when each program applies. The API targets back-office administrators and integration tooling; storefront price resolution belongs to headless-commerce-delivery-cart and the runtime the price calculation engine services instead. Most resources are addressable both by internal ID (`/<id>`) and by external reference code (`/by-externalReferenceCode/<externalReferenceCode>`); POST against the external-reference-code path is upsert. PATCH applies JSON Merge Patch (only the supplied fields are modified), while PUT replaces the resource. For the catalog, account, channel, order, and payment surfaces see the matching `headless-commerce-admin-*` modules. -- Common workflows: Define a tiered discount end-to-end -- (1) POST /discounts with title, target (for example `products` or `subtotal`), limitationType, and the percentage levels (or a fixed maximumDiscountAmount) to create the discount (POST /discounts/by-externalReferenceCode/<externalReferenceCode> is upsert); (2) POST /discounts/<id>/discountAccountGroups, /discountCategories, and /discountProducts to scope eligibility; (3) POST /discounts/<id>/discountRules to attach pre-qualification, post-qualification, or target validators carrying a JSON typeSettings payload; (4) PATCH /discounts/<id> to flip `active`, rotate the couponCode, or adjust the displayDate/expirationDate window. -- Publish a contract or promotion price list -- (1) POST /priceLists with name, currencyCode, catalogId, and priority to create the price list; (2) POST /priceLists/<id>/priceEntries per SKU with sku (or skuExternalReferenceCode), price, and optional promoPrice to set the per-SKU price; (3) POST /priceEntries/<id>/tierPrices to add quantity-break overrides keyed by minimumQuantity; (4) POST /priceLists/<id>/priceListAccountGroups to limit visibility to specific AccountGroups with an ordering priority. -- Maintain an existing price entry -- (1) GET /priceLists/<id>/priceEntries (or /priceLists/by-externalReferenceCode/<externalReferenceCode>/priceEntries) to enumerate the per-SKU entries; (2) PATCH /priceEntries/<id> (or /priceEntries/by-externalReferenceCode/<externalReferenceCode>) to adjust price or promoPrice; (3) PATCH /tierPrices/<id> or DELETE /tierPrices/<id> to refresh or retire a quantity-break override. A Java client JAR is available for use with the group ID 'com.liferay', artifact ID 'com.liferay.headless.commerce.admin.pricing.client', and version '4.0.50'.", license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html"), title = "Liferay Commerce Admin Pricing API", version = "v1.0")
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
			add(DiscountResourceImpl.class);

			add(DiscountAccountGroupResourceImpl.class);

			add(DiscountCategoryResourceImpl.class);

			add(DiscountProductResourceImpl.class);

			add(DiscountRuleResourceImpl.class);

			add(PriceEntryResourceImpl.class);

			add(PriceListResourceImpl.class);

			add(PriceListAccountGroupResourceImpl.class);

			add(TierPriceResourceImpl.class);

			add(OpenAPIResourceImpl.class);
		}
	};

}
// LIFERAY-REST-BUILDER-HASH:-1810531192