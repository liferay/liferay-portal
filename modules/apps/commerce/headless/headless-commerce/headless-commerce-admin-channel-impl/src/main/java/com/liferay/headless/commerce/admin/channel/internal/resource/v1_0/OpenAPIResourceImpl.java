/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.internal.resource.v1_0;

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
	info = @Info(description = "Configure commerce channels (storefronts) bound to Liferay sites. Each channel pins a currency, an account-visibility set, fulfilment rules, and SEO display pages. Enables multi-currency / multi-region storefronts on a single product catalog. Primary entities: Channel, ChannelAccount, AccountAddressChannel, OrderType, CategoryDisplayPage, DefaultProductDisplayPage, DefaultCategoryDisplayPage. Operates admin-side (merchandiser or system administrator configuring storefront topology); the buyer-facing surfaces of these entities are served by the headless-commerce-delivery-* module family. Cross-references to sibling admin modules: full account and address management lives in headless-commerce-admin-account-impl; shipping-method configuration in headless-commerce-admin-shipment-impl; OrderType definitions and order lifecycle in headless-commerce-admin-order-impl; payment-method groups and term entries in headless-commerce-admin-payment-impl; tax-category administration in headless-commerce-admin-site-setting-impl. Common workflows: Launch a regional storefront by posting to /channels with currencyCode (for example EUR), siteGroupId, and type (for example site, the channel-taxonomy key registered by the default deployment); then post to /channels/<id>/channel-accounts (or by-externalReferenceCode) for each eligible Account; then post to /channels/<id>/default-product-display-pages (or by-externalReferenceCode) with the SEO template pageUuid, and repeat with /default-category-display-pages for categories. -- OrderType entries (e.g. Quote-to-Order or Direct Purchase), created via the order admin API, are referenced by Channel-bound bindings such as PaymentMethodGroupRelOrderType and ShippingFixedOptionOrderType to govern downstream processing. A Java client JAR is available for use with the group ID 'com.liferay', artifact ID 'com.liferay.headless.commerce.admin.channel.client', and version '4.0.48'.", license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html"), title = "Liferay Commerce Admin Channel API", version = "v1.0")
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

			add(AccountAddressChannelResourceImpl.class);

			add(CategoryDisplayPageResourceImpl.class);

			add(ChannelResourceImpl.class);

			add(ChannelAccountResourceImpl.class);

			add(DefaultCategoryDisplayPageResourceImpl.class);

			add(DefaultProductDisplayPageResourceImpl.class);

			add(OrderTypeResourceImpl.class);

			add(PaymentMethodGroupRelOrderTypeResourceImpl.class);

			add(PaymentMethodGroupRelTermResourceImpl.class);

			add(ProductDisplayPageResourceImpl.class);

			add(ShippingFixedOptionOrderTypeResourceImpl.class);

			add(ShippingFixedOptionTermResourceImpl.class);

			add(ShippingMethodResourceImpl.class);

			add(TaxCategoryResourceImpl.class);

			add(TermResourceImpl.class);

			add(OpenAPIResourceImpl.class);
		}
	};

}
// LIFERAY-REST-BUILDER-HASH:1065338083