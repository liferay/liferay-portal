/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.internal.graphql.query.v1_0;

import com.liferay.headless.commerce.delivery.cart.dto.v1_0.Address;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.Attachment;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.Cart;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.CartComment;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.CartItem;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.CartTransition;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.PaymentMethod;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.ShippingMethod;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.Term;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.AddressResource;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.AttachmentResource;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.CartCommentResource;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.CartItemResource;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.CartResource;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.CartTransitionResource;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.PaymentMethodResource;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.ShippingMethodResource;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.TermResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLTypeExtension;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.UriInfo;

import java.util.Map;
import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class Query {

	public static void setAddressResourceComponentServiceObjects(
		ComponentServiceObjects<AddressResource>
			addressResourceComponentServiceObjects) {

		_addressResourceComponentServiceObjects =
			addressResourceComponentServiceObjects;
	}

	public static void setAttachmentResourceComponentServiceObjects(
		ComponentServiceObjects<AttachmentResource>
			attachmentResourceComponentServiceObjects) {

		_attachmentResourceComponentServiceObjects =
			attachmentResourceComponentServiceObjects;
	}

	public static void setCartResourceComponentServiceObjects(
		ComponentServiceObjects<CartResource>
			cartResourceComponentServiceObjects) {

		_cartResourceComponentServiceObjects =
			cartResourceComponentServiceObjects;
	}

	public static void setCartCommentResourceComponentServiceObjects(
		ComponentServiceObjects<CartCommentResource>
			cartCommentResourceComponentServiceObjects) {

		_cartCommentResourceComponentServiceObjects =
			cartCommentResourceComponentServiceObjects;
	}

	public static void setCartItemResourceComponentServiceObjects(
		ComponentServiceObjects<CartItemResource>
			cartItemResourceComponentServiceObjects) {

		_cartItemResourceComponentServiceObjects =
			cartItemResourceComponentServiceObjects;
	}

	public static void setCartTransitionResourceComponentServiceObjects(
		ComponentServiceObjects<CartTransitionResource>
			cartTransitionResourceComponentServiceObjects) {

		_cartTransitionResourceComponentServiceObjects =
			cartTransitionResourceComponentServiceObjects;
	}

	public static void setPaymentMethodResourceComponentServiceObjects(
		ComponentServiceObjects<PaymentMethodResource>
			paymentMethodResourceComponentServiceObjects) {

		_paymentMethodResourceComponentServiceObjects =
			paymentMethodResourceComponentServiceObjects;
	}

	public static void setShippingMethodResourceComponentServiceObjects(
		ComponentServiceObjects<ShippingMethodResource>
			shippingMethodResourceComponentServiceObjects) {

		_shippingMethodResourceComponentServiceObjects =
			shippingMethodResourceComponentServiceObjects;
	}

	public static void setTermResourceComponentServiceObjects(
		ComponentServiceObjects<TermResource>
			termResourceComponentServiceObjects) {

		_termResourceComponentServiceObjects =
			termResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cartBillingAddres(cartId: ___){city, country, countryISOCode, description, externalReferenceCode, id, latitude, longitude, name, phoneNumber, region, regionISOCode, street1, street2, street3, subtype, type, typeId, vatNumber, zip}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieve cart billing address.")
	public Address cartBillingAddres(@GraphQLName("cartId") Long cartId)
		throws Exception {

		return _applyComponentServiceObjects(
			_addressResourceComponentServiceObjects,
			this::_populateResourceContext,
			addressResource -> addressResource.getCartBillingAddres(cartId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cartByExternalReferenceCodeBillingAddress(externalReferenceCode: ___){city, country, countryISOCode, description, externalReferenceCode, id, latitude, longitude, name, phoneNumber, region, regionISOCode, street1, street2, street3, subtype, type, typeId, vatNumber, zip}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieve cart billing address.")
	public Address cartByExternalReferenceCodeBillingAddress(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_addressResourceComponentServiceObjects,
			this::_populateResourceContext,
			addressResource ->
				addressResource.getCartByExternalReferenceCodeBillingAddress(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cartByExternalReferenceCodeShippingAddress(externalReferenceCode: ___){city, country, countryISOCode, description, externalReferenceCode, id, latitude, longitude, name, phoneNumber, region, regionISOCode, street1, street2, street3, subtype, type, typeId, vatNumber, zip}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieve cart billing address.")
	public Address cartByExternalReferenceCodeShippingAddress(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_addressResourceComponentServiceObjects,
			this::_populateResourceContext,
			addressResource ->
				addressResource.getCartByExternalReferenceCodeShippingAddress(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cartShippingAddres(cartId: ___){city, country, countryISOCode, description, externalReferenceCode, id, latitude, longitude, name, phoneNumber, region, regionISOCode, street1, street2, street3, subtype, type, typeId, vatNumber, zip}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieve cart billing address.")
	public Address cartShippingAddres(@GraphQLName("cartId") Long cartId)
		throws Exception {

		return _applyComponentServiceObjects(
			_addressResourceComponentServiceObjects,
			this::_populateResourceContext,
			addressResource -> addressResource.getCartShippingAddres(cartId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cartAttachments(cartId: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AttachmentPage cartAttachments(
			@GraphQLName("cartId") Long cartId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource -> new AttachmentPage(
				attachmentResource.getCartAttachmentsPage(
					cartId, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cartByExternalReferenceCodeAttachments(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AttachmentPage cartByExternalReferenceCodeAttachments(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource -> new AttachmentPage(
				attachmentResource.
					getCartByExternalReferenceCodeAttachmentsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cart(cartId: ___){account, accountId, attachments, author, billingAddress, billingAddressExternalReferenceCode, billingAddressId, cartItems, channelId, couponCode, createDate, currencyCode, currencyExternalReferenceCode, currencyId, customFields, deliveryTermId, deliveryTermLabel, errorMessages, externalReferenceCode, friendlyURLSeparator, id, lastPriceUpdateDate, modifiedDate, name, notes, orderStatusInfo, orderType, orderTypeExternalReferenceCode, orderTypeId, orderUUID, paymentMethod, paymentMethodLabel, paymentMethodType, paymentStatus, paymentStatusInfo, paymentStatusLabel, paymentTermId, paymentTermLabel, printedNote, purchaseOrderNumber, requestedDeliveryDate, shippingAddress, shippingAddressExternalReferenceCode, shippingAddressId, shippingMethod, shippingOption, status, steps, summary, useAsBilling, valid, workflowStatusInfo}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieve information of the given Cart.")
	public Cart cart(@GraphQLName("cartId") Long cartId) throws Exception {
		return _applyComponentServiceObjects(
			_cartResourceComponentServiceObjects,
			this::_populateResourceContext,
			cartResource -> cartResource.getCart(cartId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cartByExternalReferenceCode(externalReferenceCode: ___){account, accountId, attachments, author, billingAddress, billingAddressExternalReferenceCode, billingAddressId, cartItems, channelId, couponCode, createDate, currencyCode, currencyExternalReferenceCode, currencyId, customFields, deliveryTermId, deliveryTermLabel, errorMessages, externalReferenceCode, friendlyURLSeparator, id, lastPriceUpdateDate, modifiedDate, name, notes, orderStatusInfo, orderType, orderTypeExternalReferenceCode, orderTypeId, orderUUID, paymentMethod, paymentMethodLabel, paymentMethodType, paymentStatus, paymentStatusInfo, paymentStatusLabel, paymentTermId, paymentTermLabel, printedNote, purchaseOrderNumber, requestedDeliveryDate, shippingAddress, shippingAddressExternalReferenceCode, shippingAddressId, shippingMethod, shippingOption, status, steps, summary, useAsBilling, valid, workflowStatusInfo}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieve information of the given Cart by external reference code."
	)
	public Cart cartByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_cartResourceComponentServiceObjects,
			this::_populateResourceContext,
			cartResource -> cartResource.getCartByExternalReferenceCode(
				externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cartByExternalReferenceCodePaymentUrl(callbackURL: ___, externalReferenceCode: ___){}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public String cartByExternalReferenceCodePaymentUrl(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("callbackURL") String callbackURL)
		throws Exception {

		return _applyComponentServiceObjects(
			_cartResourceComponentServiceObjects,
			this::_populateResourceContext,
			cartResource ->
				cartResource.getCartByExternalReferenceCodePaymentUrl(
					externalReferenceCode, callbackURL));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cartPaymentURL(callbackURL: ___, cartId: ___){}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public String cartPaymentURL(
			@GraphQLName("cartId") Long cartId,
			@GraphQLName("callbackURL") String callbackURL)
		throws Exception {

		return _applyComponentServiceObjects(
			_cartResourceComponentServiceObjects,
			this::_populateResourceContext,
			cartResource -> cartResource.getCartPaymentURL(
				cartId, callbackURL));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelAccountCarts(accountId: ___, channelId: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves carts for specific account in the given channel."
	)
	public CartPage channelAccountCarts(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("channelId") Long channelId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_cartResourceComponentServiceObjects,
			this::_populateResourceContext,
			cartResource -> new CartPage(
				cartResource.getChannelAccountCartsPage(
					accountId, channelId, search,
					_filterBiFunction.apply(cartResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(cartResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCarts(accountExternalReferenceCode: ___, channelExternalReferenceCode: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves carts for specific account in the given channel."
	)
	public CartPage
			channelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCarts(
				@GraphQLName("accountExternalReferenceCode") String
					accountExternalReferenceCode,
				@GraphQLName("channelExternalReferenceCode") String
					channelExternalReferenceCode,
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_cartResourceComponentServiceObjects,
			this::_populateResourceContext,
			cartResource -> new CartPage(
				cartResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage(
						accountExternalReferenceCode,
						channelExternalReferenceCode, search,
						_filterBiFunction.apply(cartResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(cartResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelCarts(channelId: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves carts in the given channel.")
	public CartPage channelCarts(
			@GraphQLName("channelId") Long channelId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_cartResourceComponentServiceObjects,
			this::_populateResourceContext,
			cartResource -> new CartPage(
				cartResource.getChannelCartsPage(
					channelId, search,
					_filterBiFunction.apply(cartResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(cartResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cartByExternalReferenceCodeComments(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CartCommentPage cartByExternalReferenceCodeComments(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_cartCommentResourceComponentServiceObjects,
			this::_populateResourceContext,
			cartCommentResource -> new CartCommentPage(
				cartCommentResource.getCartByExternalReferenceCodeCommentsPage(
					externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cartComment(cartCommentId: ___){author, authorId, authorPortraitURL, content, externalReferenceCode, id, modifiedDate, orderId, restricted}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CartComment cartComment(
			@GraphQLName("cartCommentId") Long cartCommentId)
		throws Exception {

		return _applyComponentServiceObjects(
			_cartCommentResourceComponentServiceObjects,
			this::_populateResourceContext,
			cartCommentResource -> cartCommentResource.getCartComment(
				cartCommentId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cartCommentByExternalReferenceCode(externalReferenceCode: ___){author, authorId, authorPortraitURL, content, externalReferenceCode, id, modifiedDate, orderId, restricted}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieve information of the given Cart Comment by external reference code."
	)
	public CartComment cartCommentByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_cartCommentResourceComponentServiceObjects,
			this::_populateResourceContext,
			cartCommentResource ->
				cartCommentResource.getCartCommentByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cartComments(cartId: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CartCommentPage cartComments(
			@GraphQLName("cartId") Long cartId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_cartCommentResourceComponentServiceObjects,
			this::_populateResourceContext,
			cartCommentResource -> new CartCommentPage(
				cartCommentResource.getCartCommentsPage(
					cartId, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cartByExternalReferenceCodeItems(externalReferenceCode: ___, page: ___, pageSize: ___, search: ___, skuId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieve cart items of a Cart.")
	public CartItemPage cartByExternalReferenceCodeItems(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("search") String search,
			@GraphQLName("skuId") Long skuId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_cartItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			cartItemResource -> new CartItemPage(
				cartItemResource.getCartByExternalReferenceCodeItemsPage(
					externalReferenceCode, search, skuId,
					Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cartItem(cartItemId: ___){adaptiveMediaImageHTMLTag, cartItems, customFields, deliveryGroup, deliveryGroupName, errorMessages, externalReferenceCode, id, name, options, parentCartItemId, price, productId, productURLs, quantity, replacedSku, replacedSkuExternalReferenceCode, replacedSkuId, requestedDeliveryDate, settings, shippingAddress, shippingAddressExternalReferenceCode, shippingAddressId, sku, skuId, skuUnitOfMeasure, subscription, thumbnail, unitOfMeasure, valid}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieve information of the given Cart")
	public CartItem cartItem(@GraphQLName("cartItemId") Long cartItemId)
		throws Exception {

		return _applyComponentServiceObjects(
			_cartItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			cartItemResource -> cartItemResource.getCartItem(cartItemId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cartItemByExternalReferenceCode(externalReferenceCode: ___){adaptiveMediaImageHTMLTag, cartItems, customFields, deliveryGroup, deliveryGroupName, errorMessages, externalReferenceCode, id, name, options, parentCartItemId, price, productId, productURLs, quantity, replacedSku, replacedSkuExternalReferenceCode, replacedSkuId, requestedDeliveryDate, settings, shippingAddress, shippingAddressExternalReferenceCode, shippingAddressId, sku, skuId, skuUnitOfMeasure, subscription, thumbnail, unitOfMeasure, valid}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieve information of the given Cart Item by external reference code."
	)
	public CartItem cartItemByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_cartItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			cartItemResource ->
				cartItemResource.getCartItemByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cartItems(cartId: ___, page: ___, pageSize: ___, search: ___, skuId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieve cart items of a Cart.")
	public CartItemPage cartItems(
			@GraphQLName("cartId") Long cartId,
			@GraphQLName("search") String search,
			@GraphQLName("skuId") Long skuId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_cartItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			cartItemResource -> new CartItemPage(
				cartItemResource.getCartItemsPage(
					cartId, search, skuId, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cartCartTransitions(cartId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieve cart transitions of the given Cart.")
	public CartTransitionPage cartCartTransitions(
			@GraphQLName("cartId") Long cartId)
		throws Exception {

		return _applyComponentServiceObjects(
			_cartTransitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			cartTransitionResource -> new CartTransitionPage(
				cartTransitionResource.getCartCartTransitionsPage(cartId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cartByExternalReferenceCodePaymentMethods(externalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieve payment methods available for the Cart."
	)
	public PaymentMethodPage cartByExternalReferenceCodePaymentMethods(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_paymentMethodResourceComponentServiceObjects,
			this::_populateResourceContext,
			paymentMethodResource -> new PaymentMethodPage(
				paymentMethodResource.
					getCartByExternalReferenceCodePaymentMethodsPage(
						externalReferenceCode)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cartPaymentMethods(cartId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieve payment methods available for the Cart."
	)
	public PaymentMethodPage cartPaymentMethods(
			@GraphQLName("cartId") Long cartId)
		throws Exception {

		return _applyComponentServiceObjects(
			_paymentMethodResourceComponentServiceObjects,
			this::_populateResourceContext,
			paymentMethodResource -> new PaymentMethodPage(
				paymentMethodResource.getCartPaymentMethodsPage(cartId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cartByExternalReferenceCodeShippingMethods(externalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieve payment methods available for the Cart."
	)
	public ShippingMethodPage cartByExternalReferenceCodeShippingMethods(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_shippingMethodResourceComponentServiceObjects,
			this::_populateResourceContext,
			shippingMethodResource -> new ShippingMethodPage(
				shippingMethodResource.
					getCartByExternalReferenceCodeShippingMethodsPage(
						externalReferenceCode)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cartShippingMethods(cartId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieve payment methods available for the Cart."
	)
	public ShippingMethodPage cartShippingMethods(
			@GraphQLName("cartId") Long cartId)
		throws Exception {

		return _applyComponentServiceObjects(
			_shippingMethodResourceComponentServiceObjects,
			this::_populateResourceContext,
			shippingMethodResource -> new ShippingMethodPage(
				shippingMethodResource.getCartShippingMethodsPage(cartId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cartByExternalReferenceCodeDeliveryTerms(externalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieve delivery terms available for the Cart."
	)
	public TermPage cartByExternalReferenceCodeDeliveryTerms(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_termResourceComponentServiceObjects,
			this::_populateResourceContext,
			termResource -> new TermPage(
				termResource.getCartByExternalReferenceCodeDeliveryTermsPage(
					externalReferenceCode)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cartByExternalReferenceCodePaymentTerms(externalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieve payment terms available for the Cart."
	)
	public TermPage cartByExternalReferenceCodePaymentTerms(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_termResourceComponentServiceObjects,
			this::_populateResourceContext,
			termResource -> new TermPage(
				termResource.getCartByExternalReferenceCodePaymentTermsPage(
					externalReferenceCode)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cartDeliveryTerms(cartId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieve delivery terms available for the Cart."
	)
	public TermPage cartDeliveryTerms(@GraphQLName("cartId") Long cartId)
		throws Exception {

		return _applyComponentServiceObjects(
			_termResourceComponentServiceObjects,
			this::_populateResourceContext,
			termResource -> new TermPage(
				termResource.getCartDeliveryTermsPage(cartId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cartPaymentTerms(cartId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieve payment terms available for the Cart."
	)
	public TermPage cartPaymentTerms(@GraphQLName("cartId") Long cartId)
		throws Exception {

		return _applyComponentServiceObjects(
			_termResourceComponentServiceObjects,
			this::_populateResourceContext,
			termResource -> new TermPage(
				termResource.getCartPaymentTermsPage(cartId)));
	}

	@GraphQLTypeExtension(Cart.class)
	public class GetCartPaymentMethodsPageTypeExtension {

		public GetCartPaymentMethodsPageTypeExtension(Cart cart) {
			_cart = cart;
		}

		@GraphQLField(
			description = "Retrieve payment methods available for the Cart."
		)
		public PaymentMethodPage paymentMethods() throws Exception {
			return _applyComponentServiceObjects(
				_paymentMethodResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				paymentMethodResource -> new PaymentMethodPage(
					paymentMethodResource.getCartPaymentMethodsPage(
						_cart.getId())));
		}

		private Cart _cart;

	}

	@GraphQLTypeExtension(Cart.class)
	public class GetCartPaymentTermsPageTypeExtension {

		public GetCartPaymentTermsPageTypeExtension(Cart cart) {
			_cart = cart;
		}

		@GraphQLField(
			description = "Retrieve payment terms available for the Cart."
		)
		public TermPage paymentTerms() throws Exception {
			return _applyComponentServiceObjects(
				_termResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				termResource -> new TermPage(
					termResource.getCartPaymentTermsPage(_cart.getId())));
		}

		private Cart _cart;

	}

	@GraphQLTypeExtension(Cart.class)
	public class GetCartDeliveryTermsPageTypeExtension {

		public GetCartDeliveryTermsPageTypeExtension(Cart cart) {
			_cart = cart;
		}

		@GraphQLField(
			description = "Retrieve delivery terms available for the Cart."
		)
		public TermPage deliveryTerms() throws Exception {
			return _applyComponentServiceObjects(
				_termResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				termResource -> new TermPage(
					termResource.getCartDeliveryTermsPage(_cart.getId())));
		}

		private Cart _cart;

	}

	@GraphQLTypeExtension(Cart.class)
	public class GetCartShippingAddresTypeExtension {

		public GetCartShippingAddresTypeExtension(Cart cart) {
			_cart = cart;
		}

		@GraphQLField(description = "Retrieve cart billing address.")
		public Address shippingAddres() throws Exception {
			return _applyComponentServiceObjects(
				_addressResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				addressResource -> addressResource.getCartShippingAddres(
					_cart.getId()));
		}

		private Cart _cart;

	}

	@GraphQLTypeExtension(Cart.class)
	public class GetCartShippingMethodsPageTypeExtension {

		public GetCartShippingMethodsPageTypeExtension(Cart cart) {
			_cart = cart;
		}

		@GraphQLField(
			description = "Retrieve payment methods available for the Cart."
		)
		public ShippingMethodPage shippingMethods() throws Exception {
			return _applyComponentServiceObjects(
				_shippingMethodResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				shippingMethodResource -> new ShippingMethodPage(
					shippingMethodResource.getCartShippingMethodsPage(
						_cart.getId())));
		}

		private Cart _cart;

	}

	@GraphQLTypeExtension(Cart.class)
	public class GetCartByExternalReferenceCodePaymentUrlTypeExtension {

		public GetCartByExternalReferenceCodePaymentUrlTypeExtension(
			Cart cart) {

			_cart = cart;
		}

		@GraphQLField
		public String byExternalReferenceCodePaymentUrl(
				@GraphQLName("callbackURL") String callbackURL)
			throws Exception {

			return _applyComponentServiceObjects(
				_cartResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				cartResource ->
					cartResource.getCartByExternalReferenceCodePaymentUrl(
						_cart.getExternalReferenceCode(), callbackURL));
		}

		private Cart _cart;

	}

	@GraphQLTypeExtension(CartTransition.class)
	public class GetCartTypeExtension {

		public GetCartTypeExtension(CartTransition cartTransition) {
			_cartTransition = cartTransition;
		}

		@GraphQLField(description = "Retrieve information of the given Cart.")
		public Cart cart() throws Exception {
			return _applyComponentServiceObjects(
				_cartResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				cartResource -> cartResource.getCart(
					_cartTransition.getCartId()));
		}

		private CartTransition _cartTransition;

	}

	@GraphQLTypeExtension(Cart.class)
	public class GetCartPaymentURLTypeExtension {

		public GetCartPaymentURLTypeExtension(Cart cart) {
			_cart = cart;
		}

		@GraphQLField
		public String paymentURL(@GraphQLName("callbackURL") String callbackURL)
			throws Exception {

			return _applyComponentServiceObjects(
				_cartResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				cartResource -> cartResource.getCartPaymentURL(
					_cart.getId(), callbackURL));
		}

		private Cart _cart;

	}

	@GraphQLTypeExtension(Cart.class)
	public class GetCartItemsPageTypeExtension {

		public GetCartItemsPageTypeExtension(Cart cart) {
			_cart = cart;
		}

		@GraphQLField(description = "Retrieve cart items of a Cart.")
		public CartItemPage items(
				@GraphQLName("search") String search,
				@GraphQLName("skuId") Long skuId,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_cartItemResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				cartItemResource -> new CartItemPage(
					cartItemResource.getCartItemsPage(
						_cart.getId(), search, skuId,
						Pagination.of(page, pageSize))));
		}

		private Cart _cart;

	}

	@GraphQLTypeExtension(Cart.class)
	public class GetCartBillingAddresTypeExtension {

		public GetCartBillingAddresTypeExtension(Cart cart) {
			_cart = cart;
		}

		@GraphQLField(description = "Retrieve cart billing address.")
		public Address billingAddres() throws Exception {
			return _applyComponentServiceObjects(
				_addressResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				addressResource -> addressResource.getCartBillingAddres(
					_cart.getId()));
		}

		private Cart _cart;

	}

	@GraphQLTypeExtension(Cart.class)
	public class GetCartCommentByExternalReferenceCodeTypeExtension {

		public GetCartCommentByExternalReferenceCodeTypeExtension(Cart cart) {
			_cart = cart;
		}

		@GraphQLField(
			description = "Retrieve information of the given Cart Comment by external reference code."
		)
		public CartComment commentByExternalReferenceCode() throws Exception {
			return _applyComponentServiceObjects(
				_cartCommentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				cartCommentResource ->
					cartCommentResource.getCartCommentByExternalReferenceCode(
						_cart.getExternalReferenceCode()));
		}

		private Cart _cart;

	}

	@GraphQLTypeExtension(Cart.class)
	public class GetCartByExternalReferenceCodeBillingAddressTypeExtension {

		public GetCartByExternalReferenceCodeBillingAddressTypeExtension(
			Cart cart) {

			_cart = cart;
		}

		@GraphQLField(description = "Retrieve cart billing address.")
		public Address byExternalReferenceCodeBillingAddress()
			throws Exception {

			return _applyComponentServiceObjects(
				_addressResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				addressResource ->
					addressResource.
						getCartByExternalReferenceCodeBillingAddress(
							_cart.getExternalReferenceCode()));
		}

		private Cart _cart;

	}

	@GraphQLTypeExtension(Cart.class)
	public class GetCartByExternalReferenceCodeShippingAddressTypeExtension {

		public GetCartByExternalReferenceCodeShippingAddressTypeExtension(
			Cart cart) {

			_cart = cart;
		}

		@GraphQLField(description = "Retrieve cart billing address.")
		public Address byExternalReferenceCodeShippingAddress()
			throws Exception {

			return _applyComponentServiceObjects(
				_addressResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				addressResource ->
					addressResource.
						getCartByExternalReferenceCodeShippingAddress(
							_cart.getExternalReferenceCode()));
		}

		private Cart _cart;

	}

	@GraphQLTypeExtension(Cart.class)
	public class GetCartCommentsPageTypeExtension {

		public GetCartCommentsPageTypeExtension(Cart cart) {
			_cart = cart;
		}

		@GraphQLField
		public CartCommentPage comments(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_cartCommentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				cartCommentResource -> new CartCommentPage(
					cartCommentResource.getCartCommentsPage(
						_cart.getId(), Pagination.of(page, pageSize))));
		}

		private Cart _cart;

	}

	@GraphQLTypeExtension(Cart.class)
	public class GetCartByExternalReferenceCodePaymentTermsPageTypeExtension {

		public GetCartByExternalReferenceCodePaymentTermsPageTypeExtension(
			Cart cart) {

			_cart = cart;
		}

		@GraphQLField(
			description = "Retrieve payment terms available for the Cart."
		)
		public TermPage byExternalReferenceCodePaymentTerms() throws Exception {
			return _applyComponentServiceObjects(
				_termResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				termResource -> new TermPage(
					termResource.getCartByExternalReferenceCodePaymentTermsPage(
						_cart.getExternalReferenceCode())));
		}

		private Cart _cart;

	}

	@GraphQLTypeExtension(Cart.class)
	public class GetCartByExternalReferenceCodeDeliveryTermsPageTypeExtension {

		public GetCartByExternalReferenceCodeDeliveryTermsPageTypeExtension(
			Cart cart) {

			_cart = cart;
		}

		@GraphQLField(
			description = "Retrieve delivery terms available for the Cart."
		)
		public TermPage byExternalReferenceCodeDeliveryTerms()
			throws Exception {

			return _applyComponentServiceObjects(
				_termResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				termResource -> new TermPage(
					termResource.
						getCartByExternalReferenceCodeDeliveryTermsPage(
							_cart.getExternalReferenceCode())));
		}

		private Cart _cart;

	}

	@GraphQLTypeExtension(Cart.class)
	public class GetCartItemByExternalReferenceCodeTypeExtension {

		public GetCartItemByExternalReferenceCodeTypeExtension(Cart cart) {
			_cart = cart;
		}

		@GraphQLField(
			description = "Retrieve information of the given Cart Item by external reference code."
		)
		public CartItem itemByExternalReferenceCode() throws Exception {
			return _applyComponentServiceObjects(
				_cartItemResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				cartItemResource ->
					cartItemResource.getCartItemByExternalReferenceCode(
						_cart.getExternalReferenceCode()));
		}

		private Cart _cart;

	}

	@GraphQLTypeExtension(Cart.class)
	public class GetCartCartTransitionsPageTypeExtension {

		public GetCartCartTransitionsPageTypeExtension(Cart cart) {
			_cart = cart;
		}

		@GraphQLField(
			description = "Retrieve cart transitions of the given Cart."
		)
		public CartTransitionPage cartTransitions() throws Exception {
			return _applyComponentServiceObjects(
				_cartTransitionResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				cartTransitionResource -> new CartTransitionPage(
					cartTransitionResource.getCartCartTransitionsPage(
						_cart.getId())));
		}

		private Cart _cart;

	}

	@GraphQLTypeExtension(Cart.class)
	public class GetCartByExternalReferenceCodePaymentMethodsPageTypeExtension {

		public GetCartByExternalReferenceCodePaymentMethodsPageTypeExtension(
			Cart cart) {

			_cart = cart;
		}

		@GraphQLField(
			description = "Retrieve payment methods available for the Cart."
		)
		public PaymentMethodPage byExternalReferenceCodePaymentMethods()
			throws Exception {

			return _applyComponentServiceObjects(
				_paymentMethodResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				paymentMethodResource -> new PaymentMethodPage(
					paymentMethodResource.
						getCartByExternalReferenceCodePaymentMethodsPage(
							_cart.getExternalReferenceCode())));
		}

		private Cart _cart;

	}

	@GraphQLTypeExtension(Cart.class)
	public class GetCartByExternalReferenceCodeCommentsPageTypeExtension {

		public GetCartByExternalReferenceCodeCommentsPageTypeExtension(
			Cart cart) {

			_cart = cart;
		}

		@GraphQLField
		public CartCommentPage byExternalReferenceCodeComments(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_cartCommentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				cartCommentResource -> new CartCommentPage(
					cartCommentResource.
						getCartByExternalReferenceCodeCommentsPage(
							_cart.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Cart _cart;

	}

	@GraphQLTypeExtension(Cart.class)
	public class GetCartByExternalReferenceCodeAttachmentsPageTypeExtension {

		public GetCartByExternalReferenceCodeAttachmentsPageTypeExtension(
			Cart cart) {

			_cart = cart;
		}

		@GraphQLField
		public AttachmentPage byExternalReferenceCodeAttachments(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_attachmentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				attachmentResource -> new AttachmentPage(
					attachmentResource.
						getCartByExternalReferenceCodeAttachmentsPage(
							_cart.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Cart _cart;

	}

	@GraphQLTypeExtension(Cart.class)
	public class
		GetCartByExternalReferenceCodeShippingMethodsPageTypeExtension {

		public GetCartByExternalReferenceCodeShippingMethodsPageTypeExtension(
			Cart cart) {

			_cart = cart;
		}

		@GraphQLField(
			description = "Retrieve payment methods available for the Cart."
		)
		public ShippingMethodPage byExternalReferenceCodeShippingMethods()
			throws Exception {

			return _applyComponentServiceObjects(
				_shippingMethodResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				shippingMethodResource -> new ShippingMethodPage(
					shippingMethodResource.
						getCartByExternalReferenceCodeShippingMethodsPage(
							_cart.getExternalReferenceCode())));
		}

		private Cart _cart;

	}

	@GraphQLTypeExtension(CartComment.class)
	public class GetCartByExternalReferenceCodeTypeExtension {

		public GetCartByExternalReferenceCodeTypeExtension(
			CartComment cartComment) {

			_cartComment = cartComment;
		}

		@GraphQLField(
			description = "Retrieve information of the given Cart by external reference code."
		)
		public Cart cartByExternalReferenceCode() throws Exception {
			return _applyComponentServiceObjects(
				_cartResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				cartResource -> cartResource.getCartByExternalReferenceCode(
					_cartComment.getExternalReferenceCode()));
		}

		private CartComment _cartComment;

	}

	@GraphQLTypeExtension(Cart.class)
	public class GetCartByExternalReferenceCodeItemsPageTypeExtension {

		public GetCartByExternalReferenceCodeItemsPageTypeExtension(Cart cart) {
			_cart = cart;
		}

		@GraphQLField(description = "Retrieve cart items of a Cart.")
		public CartItemPage byExternalReferenceCodeItems(
				@GraphQLName("search") String search,
				@GraphQLName("skuId") Long skuId,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_cartItemResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				cartItemResource -> new CartItemPage(
					cartItemResource.getCartByExternalReferenceCodeItemsPage(
						_cart.getExternalReferenceCode(), search, skuId,
						Pagination.of(page, pageSize))));
		}

		private Cart _cart;

	}

	@GraphQLName("AddressPage")
	public class AddressPage {

		public AddressPage(Page addressPage) {
			actions = addressPage.getActions();

			items = addressPage.getItems();
			lastPage = addressPage.getLastPage();
			page = addressPage.getPage();
			pageSize = addressPage.getPageSize();
			totalCount = addressPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Address> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("AttachmentPage")
	public class AttachmentPage {

		public AttachmentPage(Page attachmentPage) {
			actions = attachmentPage.getActions();

			items = attachmentPage.getItems();
			lastPage = attachmentPage.getLastPage();
			page = attachmentPage.getPage();
			pageSize = attachmentPage.getPageSize();
			totalCount = attachmentPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Attachment> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("CartPage")
	public class CartPage {

		public CartPage(Page cartPage) {
			actions = cartPage.getActions();

			items = cartPage.getItems();
			lastPage = cartPage.getLastPage();
			page = cartPage.getPage();
			pageSize = cartPage.getPageSize();
			totalCount = cartPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Cart> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("CartCommentPage")
	public class CartCommentPage {

		public CartCommentPage(Page cartCommentPage) {
			actions = cartCommentPage.getActions();

			items = cartCommentPage.getItems();
			lastPage = cartCommentPage.getLastPage();
			page = cartCommentPage.getPage();
			pageSize = cartCommentPage.getPageSize();
			totalCount = cartCommentPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<CartComment> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("CartItemPage")
	public class CartItemPage {

		public CartItemPage(Page cartItemPage) {
			actions = cartItemPage.getActions();

			items = cartItemPage.getItems();
			lastPage = cartItemPage.getLastPage();
			page = cartItemPage.getPage();
			pageSize = cartItemPage.getPageSize();
			totalCount = cartItemPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<CartItem> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("CartTransitionPage")
	public class CartTransitionPage {

		public CartTransitionPage(Page cartTransitionPage) {
			actions = cartTransitionPage.getActions();

			items = cartTransitionPage.getItems();
			lastPage = cartTransitionPage.getLastPage();
			page = cartTransitionPage.getPage();
			pageSize = cartTransitionPage.getPageSize();
			totalCount = cartTransitionPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<CartTransition> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("PaymentMethodPage")
	public class PaymentMethodPage {

		public PaymentMethodPage(Page paymentMethodPage) {
			actions = paymentMethodPage.getActions();

			items = paymentMethodPage.getItems();
			lastPage = paymentMethodPage.getLastPage();
			page = paymentMethodPage.getPage();
			pageSize = paymentMethodPage.getPageSize();
			totalCount = paymentMethodPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<PaymentMethod> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ShippingMethodPage")
	public class ShippingMethodPage {

		public ShippingMethodPage(Page shippingMethodPage) {
			actions = shippingMethodPage.getActions();

			items = shippingMethodPage.getItems();
			lastPage = shippingMethodPage.getLastPage();
			page = shippingMethodPage.getPage();
			pageSize = shippingMethodPage.getPageSize();
			totalCount = shippingMethodPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ShippingMethod> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("TermPage")
	public class TermPage {

		public TermPage(Page termPage) {
			actions = termPage.getActions();

			items = termPage.getItems();
			lastPage = termPage.getLastPage();
			page = termPage.getPage();
			pageSize = termPage.getPageSize();
			totalCount = termPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Term> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLTypeExtension(CartItem.class)
	public class ParentCartItemCartItemIdTypeExtension {

		public ParentCartItemCartItemIdTypeExtension(CartItem cartItem) {
			_cartItem = cartItem;
		}

		@GraphQLField(description = "Retrieve information of the given Cart")
		public CartItem parentCartItem() throws Exception {
			if (_cartItem.getParentCartItemId() == null) {
				return null;
			}

			return _applyComponentServiceObjects(
				_cartItemResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				cartItemResource -> cartItemResource.getCartItem(
					_cartItem.getParentCartItemId()));
		}

		private CartItem _cartItem;

	}

	private <T, R, E1 extends Throwable, E2 extends Throwable> R
			_applyComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeFunction<T, R, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			return unsafeFunction.apply(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private void _populateResourceContext(AddressResource addressResource)
		throws Exception {

		addressResource.setContextAcceptLanguage(_acceptLanguage);
		addressResource.setContextCompany(_company);
		addressResource.setContextHttpServletRequest(_httpServletRequest);
		addressResource.setContextHttpServletResponse(_httpServletResponse);
		addressResource.setContextUriInfo(_uriInfo);
		addressResource.setContextUser(_user);
		addressResource.setGroupLocalService(_groupLocalService);
		addressResource.setResourceActionLocalService(
			_resourceActionLocalService);
		addressResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		addressResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(AttachmentResource attachmentResource)
		throws Exception {

		attachmentResource.setContextAcceptLanguage(_acceptLanguage);
		attachmentResource.setContextCompany(_company);
		attachmentResource.setContextHttpServletRequest(_httpServletRequest);
		attachmentResource.setContextHttpServletResponse(_httpServletResponse);
		attachmentResource.setContextUriInfo(_uriInfo);
		attachmentResource.setContextUser(_user);
		attachmentResource.setGroupLocalService(_groupLocalService);
		attachmentResource.setResourceActionLocalService(
			_resourceActionLocalService);
		attachmentResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		attachmentResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(CartResource cartResource)
		throws Exception {

		cartResource.setContextAcceptLanguage(_acceptLanguage);
		cartResource.setContextCompany(_company);
		cartResource.setContextHttpServletRequest(_httpServletRequest);
		cartResource.setContextHttpServletResponse(_httpServletResponse);
		cartResource.setContextUriInfo(_uriInfo);
		cartResource.setContextUser(_user);
		cartResource.setGroupLocalService(_groupLocalService);
		cartResource.setResourceActionLocalService(_resourceActionLocalService);
		cartResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		cartResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			CartCommentResource cartCommentResource)
		throws Exception {

		cartCommentResource.setContextAcceptLanguage(_acceptLanguage);
		cartCommentResource.setContextCompany(_company);
		cartCommentResource.setContextHttpServletRequest(_httpServletRequest);
		cartCommentResource.setContextHttpServletResponse(_httpServletResponse);
		cartCommentResource.setContextUriInfo(_uriInfo);
		cartCommentResource.setContextUser(_user);
		cartCommentResource.setGroupLocalService(_groupLocalService);
		cartCommentResource.setResourceActionLocalService(
			_resourceActionLocalService);
		cartCommentResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		cartCommentResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(CartItemResource cartItemResource)
		throws Exception {

		cartItemResource.setContextAcceptLanguage(_acceptLanguage);
		cartItemResource.setContextCompany(_company);
		cartItemResource.setContextHttpServletRequest(_httpServletRequest);
		cartItemResource.setContextHttpServletResponse(_httpServletResponse);
		cartItemResource.setContextUriInfo(_uriInfo);
		cartItemResource.setContextUser(_user);
		cartItemResource.setGroupLocalService(_groupLocalService);
		cartItemResource.setResourceActionLocalService(
			_resourceActionLocalService);
		cartItemResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		cartItemResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			CartTransitionResource cartTransitionResource)
		throws Exception {

		cartTransitionResource.setContextAcceptLanguage(_acceptLanguage);
		cartTransitionResource.setContextCompany(_company);
		cartTransitionResource.setContextHttpServletRequest(
			_httpServletRequest);
		cartTransitionResource.setContextHttpServletResponse(
			_httpServletResponse);
		cartTransitionResource.setContextUriInfo(_uriInfo);
		cartTransitionResource.setContextUser(_user);
		cartTransitionResource.setGroupLocalService(_groupLocalService);
		cartTransitionResource.setResourceActionLocalService(
			_resourceActionLocalService);
		cartTransitionResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		cartTransitionResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			PaymentMethodResource paymentMethodResource)
		throws Exception {

		paymentMethodResource.setContextAcceptLanguage(_acceptLanguage);
		paymentMethodResource.setContextCompany(_company);
		paymentMethodResource.setContextHttpServletRequest(_httpServletRequest);
		paymentMethodResource.setContextHttpServletResponse(
			_httpServletResponse);
		paymentMethodResource.setContextUriInfo(_uriInfo);
		paymentMethodResource.setContextUser(_user);
		paymentMethodResource.setGroupLocalService(_groupLocalService);
		paymentMethodResource.setResourceActionLocalService(
			_resourceActionLocalService);
		paymentMethodResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		paymentMethodResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ShippingMethodResource shippingMethodResource)
		throws Exception {

		shippingMethodResource.setContextAcceptLanguage(_acceptLanguage);
		shippingMethodResource.setContextCompany(_company);
		shippingMethodResource.setContextHttpServletRequest(
			_httpServletRequest);
		shippingMethodResource.setContextHttpServletResponse(
			_httpServletResponse);
		shippingMethodResource.setContextUriInfo(_uriInfo);
		shippingMethodResource.setContextUser(_user);
		shippingMethodResource.setGroupLocalService(_groupLocalService);
		shippingMethodResource.setResourceActionLocalService(
			_resourceActionLocalService);
		shippingMethodResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		shippingMethodResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(TermResource termResource)
		throws Exception {

		termResource.setContextAcceptLanguage(_acceptLanguage);
		termResource.setContextCompany(_company);
		termResource.setContextHttpServletRequest(_httpServletRequest);
		termResource.setContextHttpServletResponse(_httpServletResponse);
		termResource.setContextUriInfo(_uriInfo);
		termResource.setContextUser(_user);
		termResource.setGroupLocalService(_groupLocalService);
		termResource.setResourceActionLocalService(_resourceActionLocalService);
		termResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		termResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<AddressResource>
		_addressResourceComponentServiceObjects;
	private static ComponentServiceObjects<AttachmentResource>
		_attachmentResourceComponentServiceObjects;
	private static ComponentServiceObjects<CartResource>
		_cartResourceComponentServiceObjects;
	private static ComponentServiceObjects<CartCommentResource>
		_cartCommentResourceComponentServiceObjects;
	private static ComponentServiceObjects<CartItemResource>
		_cartItemResourceComponentServiceObjects;
	private static ComponentServiceObjects<CartTransitionResource>
		_cartTransitionResourceComponentServiceObjects;
	private static ComponentServiceObjects<PaymentMethodResource>
		_paymentMethodResourceComponentServiceObjects;
	private static ComponentServiceObjects<ShippingMethodResource>
		_shippingMethodResourceComponentServiceObjects;
	private static ComponentServiceObjects<TermResource>
		_termResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction
		<Object, String, com.liferay.portal.kernel.search.filter.Filter>
			_filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private ResourceActionLocalService _resourceActionLocalService;
	private ResourcePermissionLocalService _resourcePermissionLocalService;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]>
		_sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;

}