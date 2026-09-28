/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.internal.resource.v1_0;

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
	info = @Info(description = "Storefront REST API for buyer-facing shopping cart interactions in Liferay Commerce. Manages the cart lifecycle while it is still in the Open state -- creation under a channel, line item management (CartItem), addresses (Address) for billing and shipping, applied coupon codes (CouponCode), buyer comments (CartComment), file attachments (AttachmentBase64 upload), delivery and payment terms (Term), available payment and shipping methods (PaymentMethod, ShippingMethod), and the workflow transitions (CartTransition) that move the cart through quote and checkout flows. The API is scoped to the storefront (delivery) perspective; back-office order management belongs to the headless-commerce-admin-order module, and catalog browsing to headless-commerce-delivery-catalog. Most resources are addressable both by internal ID (for example /carts/<cartId>) and by external reference code (for example /carts/by-externalReferenceCode/<externalReferenceCode>). The ERC is the integration-supplied idempotency key, unique per resource within the company; POST against the by-externalReferenceCode path is upsert. PATCH applies JSON Merge Patch semantics (only supplied fields are modified); PUT replaces the resource. All mutating operations require the cart to be in the Open state; checkout transitions the cart to the In Progress state via the configured checkout engine and validator chain. -- Common workflows: Create and populate a cart -- (1) POST /channels/<channelId>/carts (or /channels/by-externalReferenceCode/<externalReferenceCode>/carts) with accountId (or accountExternalReferenceCode) and currencyCode to create a new cart in the Open state; (2) POST /carts/<cartId>/items per line item with skuId and quantity to add CartItem rows; (3) PATCH /carts/<cartId> to set billing and shipping addresses (billingAddressId/shippingAddressId or their ERCs), delivery term (deliveryTermId), payment term (paymentTermId), and order type (orderTypeId); (4) POST /carts/<cartId>/coupon-code with a code to apply a promotional discount. -- Apply terms and choose payment/shipping -- (1) GET /carts/<cartId>/delivery-terms and /carts/<cartId>/payment-terms to list the Term entries qualified for the current cart configuration; (2) GET /carts/<cartId>/shipping-methods and /carts/<cartId>/payment-methods to enumerate available shipping method and payment method options filtered by address country and cart eligibility; (3) PATCH /carts/<cartId> to set shippingMethod, shippingOption, and paymentMethod accordingly. -- Submit, request a quote, or check out -- (1) GET /carts/<cartId>/cart-transitions to enumerate the available transitions (request-quote, submit, quick-checkout, checkout, or workflow task transitions) given the current order state; (2) POST /carts/<cartId>/cart-transitions with a CartTransition naming the transition to fire it; (3) Or POST /carts/<cartId>/checkout to run the validator chain (billing address, shipping method, payment method, items) and transition the cart to the In Progress state; (4) GET /carts/<cartId>/payment-url to retrieve the encrypted redirect URL into the payment gateway. -- Annotate a cart and attach files -- (1) POST /carts/<cartId>/comments with CartComment.content and restricted to add a comment (restricted notes are visible only to users with the manage-comments permission); (2) POST /carts/<cartId>/attachments/by-base64 with AttachmentBase64 (base64-encoded file plus title, type, restricted) to upload a file attachment linked to the cart. -- Resolve a cart for a buyer in a channel -- (1) GET /channels/<channelId>/account/<accountId>/carts (or the by-externalReferenceCode variants) to list open carts for an account in a channel, with OData filter and search; (2) GET /carts/<cartId> or /carts/by-externalReferenceCode/<externalReferenceCode> to retrieve a single cart with summary, items, addresses, and status. A Java client JAR is available for use with the group ID 'com.liferay', artifact ID 'com.liferay.headless.commerce.delivery.cart.client', and version '4.0.58'.", license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html"), title = "Liferay Commerce Delivery Cart API", version = "v1.0")
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
			add(AddressResourceImpl.class);

			add(AttachmentResourceImpl.class);

			add(CartResourceImpl.class);

			add(CartCommentResourceImpl.class);

			add(CartItemResourceImpl.class);

			add(CartTransitionResourceImpl.class);

			add(PaymentMethodResourceImpl.class);

			add(ShippingMethodResourceImpl.class);

			add(TermResourceImpl.class);

			add(OpenAPIResourceImpl.class);
		}
	};

}
// LIFERAY-REST-BUILDER-HASH:-1926458450