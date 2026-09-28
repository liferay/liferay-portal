/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.payment.internal.resource.v1_0;

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
	info = @Info(description = "Administrative REST API for Liferay Commerce payment entries. It manages the payment and refund records that the commerce checkout and back-office flows produce when a buyer pays for an order or subscription, when a gateway returns a transaction outcome, and when an administrator issues a refund. The API targets back-office administrators and integration tooling -- it is not a payment gateway and does not by itself authorize, capture, or settle funds; gateway interaction is driven through the configured payment integration. Payments are addressable both by internal ID (`/payments/<id>`) and by external reference code (`/payments/by-externalReferenceCode/<externalReferenceCode>`); POST against `/payments` is upsert by external reference code. PATCH applies JSON Merge Patch (only the supplied fields are modified), while PUT replaces the payment record. For order, channel, account, and currency data see the matching `headless-commerce-admin-*` modules; for the storefront, customer-facing checkout see the `headless-commerce-delivery-*` modules. -- Common workflows: Record a successful payment for an existing order -- (1) GET /headless-commerce-admin-order/v1.0/orders/<id> or by-externalReferenceCode to confirm the order. (2) POST /payments with relatedItemName=com.liferay.commerce.model.CommerceOrder, relatedItemId=<orderId>, channelId=<channelId>, currencyCode=<USD>, amount=<orderTotal>, paymentIntegrationKey=<money-order>, type=0 (Payment), externalReferenceCode=<gateway-txn-id> -- the call upserts by ERC, creating the record in Pending. (3) PATCH /payments/by-externalReferenceCode/<gateway-txn-id> with paymentStatus=0 (Completed) and transactionCode=<final-id> once the gateway confirms settlement. -- Issue a refund against a completed payment -- (1) GET /payments/by-externalReferenceCode/<original-txn-id> to confirm the source payment is in status 0 (Completed) and type 0 (Payment). (2) POST /payments with type=1 (Refund), relatedItemName= com.liferay.commerce.model.CommercePaymentEntry, relatedItemId=<original-paymentId>, amount=<refundAmount>, currencyCode=<USD>, channelId=<channelId>, reasonKey=<product-defect>, externalReferenceCode=<refund-txn-id> -- the create yields a refund payment in status 18 (Created). (3) POST /payments/by-externalReferenceCode/<refund-txn-id>/refund -- triggers the configured payment gateway's refund() to push the refund through the integration; the gateway transitions the record to status 17 (Refunded) on success. -- Inspect payment history for an order -- (1) GET /payments?filter=relatedItemId eq <orderId> and type eq 0 to list the payment records linked to the order, sorted by createDate desc. (2) For each, GET /payments/<paymentId> to inspect the gateway transaction code, payload, and status transitions captured in `actions`. A Java client JAR is available for use with the group ID 'com.liferay', artifact ID 'com.liferay.headless.commerce.admin.payment.client', and version '1.0.19'.", license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html"), title = "Liferay Commerce Admin Payment API", version = "v1.0")
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
			add(PaymentResourceImpl.class);

			add(OpenAPIResourceImpl.class);
		}
	};

}
// LIFERAY-REST-BUILDER-HASH:305644186