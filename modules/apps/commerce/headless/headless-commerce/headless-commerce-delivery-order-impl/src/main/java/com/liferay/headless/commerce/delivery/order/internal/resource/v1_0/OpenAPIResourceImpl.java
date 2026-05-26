/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.internal.resource.v1_0;

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
	info = @Info(description = "Buyer-facing read and update API for placed Liferay Commerce orders -- every operation in this module operates on a CommerceOrder whose status is no longer the open cart-draft state, so the surface is a finished order's view rather than a cart-edit surface. Primary entities -- PlacedOrder, PlacedOrderItem, PlacedOrderAddress, PlacedOrderComment, PlacedOrderItemShipment, Shipment, OrderTransition, Term, and Attachment. The audience is the storefront and order-history consumers in a channel; the admin counterpart for open carts lives in headless-commerce-delivery-cart and for managing orders end-to-end in headless-commerce-admin-order. Common workflows -- (1) list a buyer's orders in a channel with GET /channels/<channelId>/placed-orders or its by-externalReferenceCode variants, (2) drill into one order with GET /placed-orders/<placedOrderId> or its by-externalReferenceCode variant and follow the embedded ids into the billing-address, shipping-address, items, shipments, and comments sub-resources, (3) inspect or execute the workflow transitions available to the buyer with GET and POST /placed-orders/<placedOrderId>/order-transitions, including the synthetic process-quote and reorder transitions, (4) attach a supporting document to an order with POST /placed-orders/<placedOrderId>/attachments/by-base64 and list or delete attachments from the same sub-resource, (5) edit limited buyer-facing fields (name, purchase order number, printed note, custom fields) with PATCH /placed-orders/<placedOrderId>, (6) resume payment for an order via GET /placed-orders/<placedOrderId>/payment-url with an optional callbackURL. A Java client JAR is available for use with the group ID 'com.liferay', artifact ID 'com.liferay.headless.commerce.delivery.order.client', and version '1.0.35'.", license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html"), title = "Liferay Commerce Delivery Order API", version = "v1.0")
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

			add(OrderTransitionResourceImpl.class);

			add(PlacedOrderResourceImpl.class);

			add(PlacedOrderAddressResourceImpl.class);

			add(PlacedOrderCommentResourceImpl.class);

			add(PlacedOrderItemResourceImpl.class);

			add(PlacedOrderItemShipmentResourceImpl.class);

			add(ShipmentResourceImpl.class);

			add(TermResourceImpl.class);

			add(OpenAPIResourceImpl.class);
		}
	};

}
// LIFERAY-REST-BUILDER-HASH:-454798300