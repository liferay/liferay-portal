/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.resource.v1_0;

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
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/openapi.properties",
	service = OpenAPIResourceImpl.class
)
@Generated("")
@OpenAPIDefinition(
	info = @Info(description = "Administrative REST API for the Liferay Commerce order domain. It manages the core order entity end-to-end -- the order header (account, channel, currency, billing and shipping addresses, payment and delivery terms, totals and tax breakdown), the order items it contains, the order notes attached to it, the attachments uploaded against it, the order rules (COREntry) that drive discounting and validation, the order types that classify it, and the delivery and payment terms that bind to those order types. The API targets back-office administrators and integration tooling; the buyer-facing checkout and order-lookup surface lives in the headless-commerce-delivery-order module. Most resources are addressable both by internal ID (/`<id>`) and by external reference code (/by-externalReferenceCode/`<externalReferenceCode>`); POST against the external-reference-code path is upsert. PATCH applies JSON Merge Patch (only the supplied fields are modified), while PUT replaces the resource. For the storefront, customer-facing counterpart see headless-commerce-delivery-order; for catalog, pricing, account, channel, and inventory data see the matching headless-commerce-admin-* modules. -- Common workflows: Provision an order header end-to-end -- (1) POST /orders with accountId or accountExternalReferenceCode, channelId or channelExternalReferenceCode, currencyCode, and orderTypeId to create the order; (2) PATCH /orders/`<id>`/billing-address (or the by-externalReferenceCode variant) with a BillingAddress payload to set the billing address; (3) PATCH /orders/`<id>`/shipping-address to set the shipping address. -- Add and price line items -- (1) POST /orders/`<id>`/orderItems with sku, quantity, and optional options to add a order item; (2) PATCH /orderItems/`<id>` to adjust quantity, options, or unitPrice (price overrides require the MANAGE_COMMERCE_ORDER_PRICES permission); (3) DELETE /orderItems/`<id>` to remove a line. -- Attach notes and files to an order -- (1) POST /orders/`<id>`/orderNotes with content and the restricted flag for internal-only notes; (2) POST /orders/`<id>`/attachments with a base64-encoded payload, title, and type to upload a file. -- Manage order rules and their bindings -- (1) POST /orderRules with name, type, priority, dates, and active flag to create the COREntry; (2) POST /orderRules/`<id>`/orderRuleChannels, /orderRuleAccountGroups, /orderRuleAccounts, and /orderRuleOrderTypes to bind the rule to channels, account groups, accounts, and order types respectively; (3) PATCH /orderRules/`<id>` to update fields or nested bindings (upsert semantics for nested arrays). -- Manage order types and their channels -- (1) POST /orderTypes with localized name and description, active flag, and displayOrder to create the order type; (2) POST /orderTypes/`<id>`/orderTypeChannels to expose the order type on specific commerce channels. -- Manage delivery and payment terms -- (1) POST /terms with type (delivery or payment), label, name, priority, and active flag to create the term; (2) POST /terms/`<id>`/termOrderTypes to bind the term to specific order types so it applies only when those order types are in use. A Java client JAR is available for use with the group ID 'com.liferay', artifact ID 'com.liferay.headless.commerce.admin.order.client', and version '4.0.57'.", license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html"), title = "Liferay Commerce Admin Order API", version = "v1.0")
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

			add(BillingAddressResourceImpl.class);

			add(ChannelResourceImpl.class);

			add(OrderResourceImpl.class);

			add(OrderAccountGroupResourceImpl.class);

			add(OrderItemResourceImpl.class);

			add(OrderNoteResourceImpl.class);

			add(OrderRuleResourceImpl.class);

			add(OrderRuleAccountResourceImpl.class);

			add(OrderRuleAccountGroupResourceImpl.class);

			add(OrderRuleChannelResourceImpl.class);

			add(OrderRuleOrderTypeResourceImpl.class);

			add(OrderTypeResourceImpl.class);

			add(OrderTypeChannelResourceImpl.class);

			add(ShippingAddressResourceImpl.class);

			add(TermResourceImpl.class);

			add(TermOrderTypeResourceImpl.class);

			add(OpenAPIResourceImpl.class);
		}
	};

}
// LIFERAY-REST-BUILDER-HASH:1572761329