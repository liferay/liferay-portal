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
	info = @Info(description = "Buyer-facing API for placed orders -- read order history, line items, addresses, comments, shipments, and attachments, and trigger the workflow and storefront transitions allowed to the buyer (for example, cancel a pending order, request an RMA, process a quote, reorder). The API exposes the buyer or delegated account-user slice of the placed-order surface; an order is visible only to its owner or to users with the MANAGE_ALL_ACCOUNTS permission on the channel scope, and orders in the OPEN draft state are excluded from every read endpoint -- the open cart side of the flow lives in headless-commerce-delivery-cart, and back-office administration belongs to headless-commerce-admin-order. Primary entities -- PlacedOrder, PlacedOrderItem, PlacedOrderAddress, PlacedOrderComment, Shipment, PlacedOrderItemShipment, OrderTransition, Attachment, Term, VirtualItem. Most resources are addressable both by internal ID and by external reference code; PATCH applies JSON Merge Patch (only supplied fields are modified). For the cart-side counterpart see headless-commerce-delivery-cart; for admin-side order management see headless-commerce-admin-order. -- Common workflows: Order history view -- (1) GET /channels/<channelId>/placed-orders?sort=createDate desc with paging to enumerate the buyer's committed orders on a channel, then GET /placed-orders/<placedOrderId> for the full detail. -- Re-order -- (1) GET /placed-orders/<placedOrderId> to fetch the order and its line items; (2) POST /placed-orders/<placedOrderId>/order-transitions with name=reorder to spawn a new draft cart whose identifier is returned in OrderTransition.orderId, then continue on the headless-commerce-delivery-cart endpoints to edit and check out the new cart. -- Track shipments -- (1) GET /placed-orders/<placedOrderId>/shipments?sort=createDate desc to list every dispatched shipment with carrier, trackingNumber, and trackingURL; (2) GET /placed-order-items/<placedOrderItemId>/placed-order-item-shipments to drill into the shipments that fulfill a specific line item. -- Invoice download -- (1) GET /placed-orders/<placedOrderId>/attachments to list invoices and delivery notes; (2) follow Attachment.url for each entry to download the PDF. -- Cancel or transition a pending order -- (1) GET /placed-orders/<placedOrderId>/order-transitions to enumerate the transitions the buyer is permitted to trigger; (2) POST /placed-orders/<placedOrderId>/order-transitions with the chosen name (and the workflowTaskId when the order is in a workflow) to apply the transition. -- Virtual item download -- (1) GET /placed-orders/<placedOrderId> and inspect PlacedOrderItem.virtualItems[] on each line item; (2) follow VirtualItem.url for each downloadable file, observing the remaining usages quota and the optional version label. A Java client JAR is available for use with the group ID 'com.liferay', artifact ID 'com.liferay.headless.commerce.delivery.order.client', and version '1.0.35'.", license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html"), title = "Liferay Commerce Delivery Order API", version = "v1.0")
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
// LIFERAY-REST-BUILDER-HASH:-1518381684