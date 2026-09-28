/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.shipment.internal.resource.v1_0;

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
	info = @Info(description = "Liferay Commerce Admin Shipment REST API. Manages the fulfillment of orders -- shipment headers, the shipment item rows that pick stock from a warehouse, the address that the parcel ships to, and the workflow that advances a shipment from PROCESSING (0) to READY_TO_BE_SHIPPED (1) to SHIPPED (2) to DELIVERED (3). The API targets back-office fulfillment operators and integration tooling; storefront-side ordering belongs to the headless-commerce-delivery-order module. Most resources are addressable both by internal ID and by external reference code; PUT against the external-reference-code path is upsert (create when the ERC is unknown, update otherwise). PATCH applies JSON Merge Patch (only the supplied fields are modified). For the order, account, and inventory data that this module refers to see the matching headless-commerce-admin-order, headless-commerce-admin-account, and headless-commerce-admin-inventory modules. -- Common workflows: Ship an order in one parcel -- (1) POST /shipments with orderId (or orderExternalReferenceCode) and an inline shipmentItems array to create the shipment in PROCESSING status and deduct stock from the chosen warehouse; (2) POST /shipments/<shipmentId>/status-finish-processing to advance the shipment to READY_TO_BE_SHIPPED once picking is complete; (3) POST /shipments/<shipmentId>/status-shipped after the carrier accepts the parcel; (4) POST /shipments/<shipmentId>/status-delivered after the customer receives it. -- Partial fulfillment -- (1) POST /shipments referencing the order to open an empty shipment; (2) POST /shipments/<shipmentId>/items per order item to add a subset of the ordered quantity, choosing the picking warehouse per row; (3) repeat the open-then-add cycle for each subsequent parcel until the remaining order quantity is zero. -- Correct a shipment in flight -- (1) PATCH /shipments/<shipmentId> to adjust carrier, trackingNumber, trackingURL, expectedDate, shippingDate, or shippingMethodId without changing the line items; (2) PATCH /shipments/<shipmentId>/shipping-address to retarget the parcel before it leaves the warehouse; (3) DELETE /shipments/<shipmentId> to roll the shipment back. A Java client JAR is available for use with the group ID 'com.liferay', artifact ID 'com.liferay.headless.commerce.admin.shipment.client', and version '1.0.43'.", license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html"), title = "Liferay Commerce Admin Shipment API", version = "v1.0")
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
			add(ShipmentResourceImpl.class);

			add(ShipmentItemResourceImpl.class);

			add(ShippingAddressResourceImpl.class);

			add(OpenAPIResourceImpl.class);
		}
	};

}
// LIFERAY-REST-BUILDER-HASH:1163871617