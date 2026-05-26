/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.internal.graphql.query.v1_0;

import com.liferay.headless.commerce.delivery.order.dto.v1_0.Attachment;
import com.liferay.headless.commerce.delivery.order.dto.v1_0.OrderTransition;
import com.liferay.headless.commerce.delivery.order.dto.v1_0.PlacedOrder;
import com.liferay.headless.commerce.delivery.order.dto.v1_0.PlacedOrderAddress;
import com.liferay.headless.commerce.delivery.order.dto.v1_0.PlacedOrderComment;
import com.liferay.headless.commerce.delivery.order.dto.v1_0.PlacedOrderItem;
import com.liferay.headless.commerce.delivery.order.dto.v1_0.PlacedOrderItemShipment;
import com.liferay.headless.commerce.delivery.order.dto.v1_0.Shipment;
import com.liferay.headless.commerce.delivery.order.dto.v1_0.Term;
import com.liferay.headless.commerce.delivery.order.resource.v1_0.AttachmentResource;
import com.liferay.headless.commerce.delivery.order.resource.v1_0.OrderTransitionResource;
import com.liferay.headless.commerce.delivery.order.resource.v1_0.PlacedOrderAddressResource;
import com.liferay.headless.commerce.delivery.order.resource.v1_0.PlacedOrderCommentResource;
import com.liferay.headless.commerce.delivery.order.resource.v1_0.PlacedOrderItemResource;
import com.liferay.headless.commerce.delivery.order.resource.v1_0.PlacedOrderItemShipmentResource;
import com.liferay.headless.commerce.delivery.order.resource.v1_0.PlacedOrderResource;
import com.liferay.headless.commerce.delivery.order.resource.v1_0.ShipmentResource;
import com.liferay.headless.commerce.delivery.order.resource.v1_0.TermResource;
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

	public static void setAttachmentResourceComponentServiceObjects(
		ComponentServiceObjects<AttachmentResource>
			attachmentResourceComponentServiceObjects) {

		_attachmentResourceComponentServiceObjects =
			attachmentResourceComponentServiceObjects;
	}

	public static void setOrderTransitionResourceComponentServiceObjects(
		ComponentServiceObjects<OrderTransitionResource>
			orderTransitionResourceComponentServiceObjects) {

		_orderTransitionResourceComponentServiceObjects =
			orderTransitionResourceComponentServiceObjects;
	}

	public static void setPlacedOrderResourceComponentServiceObjects(
		ComponentServiceObjects<PlacedOrderResource>
			placedOrderResourceComponentServiceObjects) {

		_placedOrderResourceComponentServiceObjects =
			placedOrderResourceComponentServiceObjects;
	}

	public static void setPlacedOrderAddressResourceComponentServiceObjects(
		ComponentServiceObjects<PlacedOrderAddressResource>
			placedOrderAddressResourceComponentServiceObjects) {

		_placedOrderAddressResourceComponentServiceObjects =
			placedOrderAddressResourceComponentServiceObjects;
	}

	public static void setPlacedOrderCommentResourceComponentServiceObjects(
		ComponentServiceObjects<PlacedOrderCommentResource>
			placedOrderCommentResourceComponentServiceObjects) {

		_placedOrderCommentResourceComponentServiceObjects =
			placedOrderCommentResourceComponentServiceObjects;
	}

	public static void setPlacedOrderItemResourceComponentServiceObjects(
		ComponentServiceObjects<PlacedOrderItemResource>
			placedOrderItemResourceComponentServiceObjects) {

		_placedOrderItemResourceComponentServiceObjects =
			placedOrderItemResourceComponentServiceObjects;
	}

	public static void
		setPlacedOrderItemShipmentResourceComponentServiceObjects(
			ComponentServiceObjects<PlacedOrderItemShipmentResource>
				placedOrderItemShipmentResourceComponentServiceObjects) {

		_placedOrderItemShipmentResourceComponentServiceObjects =
			placedOrderItemShipmentResourceComponentServiceObjects;
	}

	public static void setShipmentResourceComponentServiceObjects(
		ComponentServiceObjects<ShipmentResource>
			shipmentResourceComponentServiceObjects) {

		_shipmentResourceComponentServiceObjects =
			shipmentResourceComponentServiceObjects;
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
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrderAttachments(filter: ___, page: ___, pageSize: ___, placedOrderId: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Returns a page of order attachments. When feature flag LPD-6252 is enabled the query goes through the indexed CommerceOrderAttachment, honouring filter, sort, and search (fields -- restricted, dateCreated, dateModified, priority, commerceOrderId, externalReferenceCode, title, type). Otherwise it reads the order's legacy file-entry attachments and filter, sort, search are ignored."
	)
	public AttachmentPage placedOrderAttachments(
			@GraphQLName("placedOrderId") Long placedOrderId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource -> new AttachmentPage(
				attachmentResource.getPlacedOrderAttachmentsPage(
					placedOrderId, search,
					_filterBiFunction.apply(attachmentResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(attachmentResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrderByExternalReferenceCodeAttachments(externalReferenceCode: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Resolves the order by external reference code and delegates to getPlacedOrderAttachmentsPage. When feature flag LPD-6252 is enabled the listing is an indexed CommerceOrderAttachment query honouring filter, sort, and search (fields -- restricted, dateCreated, dateModified, priority, commerceOrderId, externalReferenceCode, title, type). When the flag is disabled the attachments are read directly from the order's file entries and filter, sort, search are ignored."
	)
	public AttachmentPage placedOrderByExternalReferenceCodeAttachments(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource -> new AttachmentPage(
				attachmentResource.
					getPlacedOrderByExternalReferenceCodeAttachmentsPage(
						externalReferenceCode, search,
						_filterBiFunction.apply(
							attachmentResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							attachmentResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrderOrderTransitions(placedOrderId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Returns the workflow transitions the current user can execute for the order via CommerceWorkflowedModelHelper.getWorkflowTransitions, plus the synthetic process-quote transition when the order matches the quote-processed criteria, plus the synthetic reorder transition. Refuses with CommerceOrderStatusException on open orders."
	)
	public OrderTransitionPage placedOrderOrderTransitions(
			@GraphQLName("placedOrderId") Long placedOrderId)
		throws Exception {

		return _applyComponentServiceObjects(
			_orderTransitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			orderTransitionResource -> new OrderTransitionPage(
				orderTransitionResource.getPlacedOrderOrderTransitionsPage(
					placedOrderId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelAccountPlacedOrders(accountId: ___, channelId: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Returns a page of placed orders owned by the given account inside the given channel. The implementation resolves the channel via CommerceChannelLocalService.getCommerceChannel and runs an indexed search of CommerceOrder scoped to the channel group, the supplied account, and every order status except ORDER_STATUS_OPEN. Supported filter and sort fields come from PlacedOrderEntityModel -- accountId, orderStatus, customFields, orderStatusInfo, createDate, modifiedDate, orderDate, requestedDeliveryDate, authorId, id, account, author, externalReferenceCode, name, orderType, orderTypeExternalReferenceCode, purchaseOrderNumber."
	)
	public PlacedOrderPage channelAccountPlacedOrders(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("channelId") Long channelId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_placedOrderResourceComponentServiceObjects,
			this::_populateResourceContext,
			placedOrderResource -> new PlacedOrderPage(
				placedOrderResource.getChannelAccountPlacedOrdersPage(
					accountId, channelId, search,
					_filterBiFunction.apply(placedOrderResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(placedOrderResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrders(accountExternalReferenceCode: ___, channelExternalReferenceCode: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Resolves the channel and the account by external reference code, then delegates to getChannelAccountPlacedOrdersPage to return the same channel-scoped, account-scoped page of placed orders. Filter, sort, and search semantics match the by-id variant."
	)
	public PlacedOrderPage
			channelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrders(
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
			_placedOrderResourceComponentServiceObjects,
			this::_populateResourceContext,
			placedOrderResource -> new PlacedOrderPage(
				placedOrderResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage(
						accountExternalReferenceCode,
						channelExternalReferenceCode, search,
						_filterBiFunction.apply(
							placedOrderResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							placedOrderResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelByExternalReferenceCodePlacedOrders(externalReferenceCode: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Resolves the channel by external reference code and delegates to getChannelPlacedOrdersPage to return non-open placed orders visible to the caller in that channel."
	)
	public PlacedOrderPage channelByExternalReferenceCodePlacedOrders(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_placedOrderResourceComponentServiceObjects,
			this::_populateResourceContext,
			placedOrderResource -> new PlacedOrderPage(
				placedOrderResource.
					getChannelByExternalReferenceCodePlacedOrdersPage(
						externalReferenceCode, search,
						_filterBiFunction.apply(
							placedOrderResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							placedOrderResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelPlacedOrders(channelId: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Returns a page of non-open placed orders in the given channel. When the caller lacks the MANAGE_COMMERCE_ORDERS portlet permission the search is narrowed to commerce accounts the user belongs to via CommerceAccountHelper.getUserCommerceAccountIds. Supported filter and sort fields are the PlacedOrderEntityModel field set."
	)
	public PlacedOrderPage channelPlacedOrders(
			@GraphQLName("channelId") Long channelId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_placedOrderResourceComponentServiceObjects,
			this::_populateResourceContext,
			placedOrderResource -> new PlacedOrderPage(
				placedOrderResource.getChannelPlacedOrdersPage(
					channelId, search,
					_filterBiFunction.apply(placedOrderResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(placedOrderResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrder(placedOrderId: ___){account, accountId, attachments, author, authorId, channelId, couponCode, createDate, currencyCode, customFields, errorMessages, externalReferenceCode, friendlyURLSeparator, id, lastPriceUpdateDate, modifiedDate, name, orderStatusInfo, orderType, orderTypeExternalReferenceCode, orderTypeId, orderUUID, paymentMethod, paymentMethodLabel, paymentStatus, paymentStatusInfo, paymentStatusLabel, placedOrderBillingAddress, placedOrderBillingAddressId, placedOrderComments, placedOrderItems, placedOrderShippingAddress, placedOrderShippingAddressId, printedNote, purchaseOrderNumber, requestedDeliveryDate, shipments, shippingMethod, shippingOption, status, steps, summary, useAsBilling, valid, workflowStatusInfo}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Returns the placed order by id. Raises NoSuchOrderException when the order is still open -- open carts are exposed through headless-commerce-delivery-cart, not through this API."
	)
	public PlacedOrder placedOrder(
			@GraphQLName("placedOrderId") Long placedOrderId)
		throws Exception {

		return _applyComponentServiceObjects(
			_placedOrderResourceComponentServiceObjects,
			this::_populateResourceContext,
			placedOrderResource -> placedOrderResource.getPlacedOrder(
				placedOrderId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrderByExternalReferenceCode(externalReferenceCode: ___){account, accountId, attachments, author, authorId, channelId, couponCode, createDate, currencyCode, customFields, errorMessages, externalReferenceCode, friendlyURLSeparator, id, lastPriceUpdateDate, modifiedDate, name, orderStatusInfo, orderType, orderTypeExternalReferenceCode, orderTypeId, orderUUID, paymentMethod, paymentMethodLabel, paymentStatus, paymentStatusInfo, paymentStatusLabel, placedOrderBillingAddress, placedOrderBillingAddressId, placedOrderComments, placedOrderItems, placedOrderShippingAddress, placedOrderShippingAddressId, printedNote, purchaseOrderNumber, requestedDeliveryDate, shipments, shippingMethod, shippingOption, status, steps, summary, useAsBilling, valid, workflowStatusInfo}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Resolves the order by external reference code, refuses when it is still open, then converts it through PlacedOrderDTOConverter."
	)
	public PlacedOrder placedOrderByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_placedOrderResourceComponentServiceObjects,
			this::_populateResourceContext,
			placedOrderResource ->
				placedOrderResource.getPlacedOrderByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrderByExternalReferenceCodePaymentURL(callbackURL: ___, externalReferenceCode: ___){}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Resolves the order by external reference code and delegates to getPlacedOrderPaymentURL to build the next-step payment URL the storefront should redirect to."
	)
	public String placedOrderByExternalReferenceCodePaymentURL(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("callbackURL") String callbackURL)
		throws Exception {

		return _applyComponentServiceObjects(
			_placedOrderResourceComponentServiceObjects,
			this::_populateResourceContext,
			placedOrderResource ->
				placedOrderResource.
					getPlacedOrderByExternalReferenceCodePaymentURL(
						externalReferenceCode, callbackURL));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrderPaymentURL(callbackURL: ___, placedOrderId: ___){}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Builds the URL the storefront should redirect to in order to resume payment for the placed order. When callbackURL is supplied it is used as the next-step URL, otherwise the order-confirmation checkout step URL is used. Refuses on open orders."
	)
	public String placedOrderPaymentURL(
			@GraphQLName("placedOrderId") Long placedOrderId,
			@GraphQLName("callbackURL") String callbackURL)
		throws Exception {

		return _applyComponentServiceObjects(
			_placedOrderResourceComponentServiceObjects,
			this::_populateResourceContext,
			placedOrderResource -> placedOrderResource.getPlacedOrderPaymentURL(
				placedOrderId, callbackURL));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrderByExternalReferenceCodePlacedOrderBillingAddress(externalReferenceCode: ___){city, country, countryISOCode, description, externalReferenceCode, id, latitude, longitude, name, phoneNumber, region, regionISOCode, street1, street2, street3, subtype, type, typeId, vatNumber, zip}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Resolves the order by external reference code and returns the snapshot CommerceAddress referenced by the order's billingAddressId via PlacedOrderAddressDTOConverter. Returns an empty PlacedOrderAddress when the order has no billing address."
	)
	public PlacedOrderAddress
			placedOrderByExternalReferenceCodePlacedOrderBillingAddress(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_placedOrderAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			placedOrderAddressResource ->
				placedOrderAddressResource.
					getPlacedOrderByExternalReferenceCodePlacedOrderBillingAddress(
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrderByExternalReferenceCodePlacedOrderShippingAddress(externalReferenceCode: ___){city, country, countryISOCode, description, externalReferenceCode, id, latitude, longitude, name, phoneNumber, region, regionISOCode, street1, street2, street3, subtype, type, typeId, vatNumber, zip}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Resolves the order by external reference code and returns the snapshot CommerceAddress referenced by the order's shippingAddressId via PlacedOrderAddressDTOConverter."
	)
	public PlacedOrderAddress
			placedOrderByExternalReferenceCodePlacedOrderShippingAddress(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_placedOrderAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			placedOrderAddressResource ->
				placedOrderAddressResource.
					getPlacedOrderByExternalReferenceCodePlacedOrderShippingAddress(
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrderPlacedOrderBillingAddress(placedOrderId: ___){city, country, countryISOCode, description, externalReferenceCode, id, latitude, longitude, name, phoneNumber, region, regionISOCode, street1, street2, street3, subtype, type, typeId, vatNumber, zip}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Loads the order, refuses on open orders, and returns the snapshot CommerceAddress referenced by commerceOrder.getBillingAddressId() via PlacedOrderAddressDTOConverter. Returns an empty PlacedOrderAddress when the order has no billing address."
	)
	public PlacedOrderAddress placedOrderPlacedOrderBillingAddress(
			@GraphQLName("placedOrderId") Long placedOrderId)
		throws Exception {

		return _applyComponentServiceObjects(
			_placedOrderAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			placedOrderAddressResource ->
				placedOrderAddressResource.
					getPlacedOrderPlacedOrderBillingAddress(placedOrderId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrderPlacedOrderShippingAddress(placedOrderId: ___){city, country, countryISOCode, description, externalReferenceCode, id, latitude, longitude, name, phoneNumber, region, regionISOCode, street1, street2, street3, subtype, type, typeId, vatNumber, zip}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Loads the order, refuses on open orders, and returns the snapshot CommerceAddress referenced by commerceOrder.getShippingAddressId() via PlacedOrderAddressDTOConverter."
	)
	public PlacedOrderAddress placedOrderPlacedOrderShippingAddress(
			@GraphQLName("placedOrderId") Long placedOrderId)
		throws Exception {

		return _applyComponentServiceObjects(
			_placedOrderAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			placedOrderAddressResource ->
				placedOrderAddressResource.
					getPlacedOrderPlacedOrderShippingAddress(placedOrderId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrderByExternalReferenceCodePlacedOrderComments(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Resolves the order by external reference code and returns a page of its CommerceOrderNote rows via CommerceOrderNoteService.getCommerceOrderNotes(orderId, start, end). The endpoint accepts page and pageSize only -- filter, sort, and keyword search are not applied."
	)
	public PlacedOrderCommentPage
			placedOrderByExternalReferenceCodePlacedOrderComments(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_placedOrderCommentResourceComponentServiceObjects,
			this::_populateResourceContext,
			placedOrderCommentResource -> new PlacedOrderCommentPage(
				placedOrderCommentResource.
					getPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrderComment(placedOrderCommentId: ___){author, content, externalReferenceCode, id, orderId, restricted}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Loads the CommerceOrderNote via CommerceOrderNoteService.getCommerceOrderNote, refuses when the owning order is still open, then converts the note through PlacedOrderCommentDTOConverter."
	)
	public PlacedOrderComment placedOrderComment(
			@GraphQLName("placedOrderCommentId") Long placedOrderCommentId)
		throws Exception {

		return _applyComponentServiceObjects(
			_placedOrderCommentResourceComponentServiceObjects,
			this::_populateResourceContext,
			placedOrderCommentResource ->
				placedOrderCommentResource.getPlacedOrderComment(
					placedOrderCommentId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrderCommentByExternalReferenceCode(externalReferenceCode: ___){author, content, externalReferenceCode, id, orderId, restricted}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Loads the CommerceOrderNote by external reference code via CommerceOrderNoteService.fetchCommerceOrderNoteByExternalReferenceCode and delegates to getPlacedOrderComment. Raises NoSuchOrderNoteException when no matching note is found."
	)
	public PlacedOrderComment placedOrderCommentByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_placedOrderCommentResourceComponentServiceObjects,
			this::_populateResourceContext,
			placedOrderCommentResource ->
				placedOrderCommentResource.
					getPlacedOrderCommentByExternalReferenceCode(
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrderPlacedOrderComments(page: ___, pageSize: ___, placedOrderId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Loads the order, refuses on open orders, and returns a page of its CommerceOrderNote rows via CommerceOrderNoteService.getCommerceOrderNotes(orderId, start, end). The endpoint accepts page and pageSize only -- filter, sort, and keyword search are not applied."
	)
	public PlacedOrderCommentPage placedOrderPlacedOrderComments(
			@GraphQLName("placedOrderId") Long placedOrderId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_placedOrderCommentResourceComponentServiceObjects,
			this::_populateResourceContext,
			placedOrderCommentResource -> new PlacedOrderCommentPage(
				placedOrderCommentResource.
					getPlacedOrderPlacedOrderCommentsPage(
						placedOrderId, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrderByExternalReferenceCodePlacedOrderItems(externalReferenceCode: ___, page: ___, pageSize: ___, search: ___, skuId: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Resolves the order by external reference code and runs an indexed CommerceOrderItem search scoped to that order id with parentCommerceOrderItemId = 0 so only top-level items are returned. Search drives the keyword query and sort supports PlacedOrderItemEntityModel fields -- quantity, name, sku, unitOfMeasure. The skuId parameter is declared but not currently applied as an extra filter."
	)
	public PlacedOrderItemPage
			placedOrderByExternalReferenceCodePlacedOrderItems(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("search") String search,
				@GraphQLName("skuId") Long skuId,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_placedOrderItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			placedOrderItemResource -> new PlacedOrderItemPage(
				placedOrderItemResource.
					getPlacedOrderByExternalReferenceCodePlacedOrderItemsPage(
						externalReferenceCode, search, skuId,
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							placedOrderItemResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrderItem(placedOrderItemId: ___){adaptiveMediaImageHTMLTag, customFields, deliveryGroup, deliveryGroupName, errorMessages, externalReferenceCode, id, name, options, parentOrderItemId, placedOrderItemShipments, placedOrderItems, price, productId, productURLs, quantity, replacedSku, requestedDeliveryDate, settings, shippingAddressExternalReferenceCode, shippingAddressId, sku, skuId, subscription, thumbnail, unitOfMeasure, unitOfMeasureKey, valid, virtualItemURLs, virtualItems}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Returns the placed order item by id after verifying its parent CommerceOrder is not open, and converts it through PlacedOrderItemDTOConverter with the order's commerce account id so the thumbnail and adaptive-media tags resolve correctly."
	)
	public PlacedOrderItem placedOrderItem(
			@GraphQLName("placedOrderItemId") Long placedOrderItemId)
		throws Exception {

		return _applyComponentServiceObjects(
			_placedOrderItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			placedOrderItemResource ->
				placedOrderItemResource.getPlacedOrderItem(placedOrderItemId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrderItemByExternalReferenceCode(externalReferenceCode: ___){adaptiveMediaImageHTMLTag, customFields, deliveryGroup, deliveryGroupName, errorMessages, externalReferenceCode, id, name, options, parentOrderItemId, placedOrderItemShipments, placedOrderItems, price, productId, productURLs, quantity, replacedSku, requestedDeliveryDate, settings, shippingAddressExternalReferenceCode, shippingAddressId, sku, skuId, subscription, thumbnail, unitOfMeasure, unitOfMeasureKey, valid, virtualItemURLs, virtualItems}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Resolves the CommerceOrderItem by external reference code and delegates to getPlacedOrderItem. Raises NoSuchOrderItemException when no matching item is found."
	)
	public PlacedOrderItem placedOrderItemByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_placedOrderItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			placedOrderItemResource ->
				placedOrderItemResource.
					getPlacedOrderItemByExternalReferenceCode(
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrderPlacedOrderItems(page: ___, pageSize: ___, placedOrderId: ___, search: ___, skuId: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Loads the order, refuses on open orders, then runs an indexed CommerceOrderItem search scoped to that order id with parentCommerceOrderItemId = 0 so only top-level items appear; child items live inside each item's placedOrderItems collection. Search drives the keyword query and sort supports PlacedOrderItemEntityModel fields -- quantity, name, sku, unitOfMeasure. The skuId parameter is accepted but not currently applied as an extra filter."
	)
	public PlacedOrderItemPage placedOrderPlacedOrderItems(
			@GraphQLName("placedOrderId") Long placedOrderId,
			@GraphQLName("search") String search,
			@GraphQLName("skuId") Long skuId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_placedOrderItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			placedOrderItemResource -> new PlacedOrderItemPage(
				placedOrderItemResource.getPlacedOrderPlacedOrderItemsPage(
					placedOrderId, search, skuId, Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						placedOrderItemResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrderItemByExternalReferenceCodePlacedOrderItemShipments(externalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Resolves the CommerceOrderItem by external reference code and delegates to getPlacedOrderItemPlacedOrderItemShipmentsPage to return the shipments for that item."
	)
	public PlacedOrderItemShipmentPage
			placedOrderItemByExternalReferenceCodePlacedOrderItemShipments(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_placedOrderItemShipmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			placedOrderItemShipmentResource -> new PlacedOrderItemShipmentPage(
				placedOrderItemShipmentResource.
					getPlacedOrderItemByExternalReferenceCodePlacedOrderItemShipmentsPage(
						externalReferenceCode)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrderItemPlacedOrderItemShipments(placedOrderItemId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Loads the item, refuses when its parent order is open, then returns the CommerceShipmentItem rows via CommerceShipmentItemService.getCommerceShipmentItemsByCommerceOrderItemId. When no rows exist it falls back to shipment items from non-open supplier orders linked via CommerceOrderItemService.getSupplierCommerceOrderItems."
	)
	public PlacedOrderItemShipmentPage placedOrderItemPlacedOrderItemShipments(
			@GraphQLName("placedOrderItemId") Long placedOrderItemId)
		throws Exception {

		return _applyComponentServiceObjects(
			_placedOrderItemShipmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			placedOrderItemShipmentResource -> new PlacedOrderItemShipmentPage(
				placedOrderItemShipmentResource.
					getPlacedOrderItemPlacedOrderItemShipmentsPage(
						placedOrderItemId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrderByExternalReferenceCodeShipments(externalReferenceCode: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Resolves the order by external reference code and runs an indexed CommerceShipment query whose itemsCount and oneLineAddress are read straight from the search document. Filter, sort, and search use ShipmentEntityModel fields -- status, createDate, expectedDate, shippingDate, id, carrier, tracking."
	)
	public ShipmentPage placedOrderByExternalReferenceCodeShipments(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_shipmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			shipmentResource -> new ShipmentPage(
				shipmentResource.
					getPlacedOrderByExternalReferenceCodeShipmentsPage(
						externalReferenceCode, search,
						_filterBiFunction.apply(shipmentResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							shipmentResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrderShipments(filter: ___, page: ___, pageSize: ___, placedOrderId: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Loads the order, refuses on open orders, and runs an indexed CommerceShipment query scoped to the order; itemsCount and oneLineAddress are read straight from the search document. Filter, sort, and search use ShipmentEntityModel fields -- status, createDate, expectedDate, shippingDate, id, carrier, tracking."
	)
	public ShipmentPage placedOrderShipments(
			@GraphQLName("placedOrderId") Long placedOrderId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_shipmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			shipmentResource -> new ShipmentPage(
				shipmentResource.getPlacedOrderShipmentsPage(
					placedOrderId, search,
					_filterBiFunction.apply(shipmentResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(shipmentResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrderByExternalReferenceCodeDeliveryTerm(externalReferenceCode: ___){description, externalReferenceCode, id, name}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Resolves the order by external reference code and delegates to getPlacedOrderDeliveryTerm to return the linked CommerceTermEntry as a Term."
	)
	public Term placedOrderByExternalReferenceCodeDeliveryTerm(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_termResourceComponentServiceObjects,
			this::_populateResourceContext,
			termResource ->
				termResource.getPlacedOrderByExternalReferenceCodeDeliveryTerm(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrderByExternalReferenceCodePaymentTerm(externalReferenceCode: ___){description, externalReferenceCode, id, name}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Resolves the order by external reference code and delegates to getPlacedOrderPaymentTerm to return the linked CommerceTermEntry as a Term."
	)
	public Term placedOrderByExternalReferenceCodePaymentTerm(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_termResourceComponentServiceObjects,
			this::_populateResourceContext,
			termResource ->
				termResource.getPlacedOrderByExternalReferenceCodePaymentTerm(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrderDeliveryTerm(placedOrderId: ___){description, externalReferenceCode, id, name}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Loads the order, refuses on open orders, and returns the CommerceTermEntry referenced by commerceOrder.getDeliveryCommerceTermEntryId() projected into the Term schema."
	)
	public Term placedOrderDeliveryTerm(
			@GraphQLName("placedOrderId") Long placedOrderId)
		throws Exception {

		return _applyComponentServiceObjects(
			_termResourceComponentServiceObjects,
			this::_populateResourceContext,
			termResource -> termResource.getPlacedOrderDeliveryTerm(
				placedOrderId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {placedOrderPaymentTerm(placedOrderId: ___){description, externalReferenceCode, id, name}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Loads the order, refuses on open orders, and returns the CommerceTermEntry referenced by commerceOrder.getPaymentCommerceTermEntryId() projected into the Term schema."
	)
	public Term placedOrderPaymentTerm(
			@GraphQLName("placedOrderId") Long placedOrderId)
		throws Exception {

		return _applyComponentServiceObjects(
			_termResourceComponentServiceObjects,
			this::_populateResourceContext,
			termResource -> termResource.getPlacedOrderPaymentTerm(
				placedOrderId));
	}

	@GraphQLTypeExtension(OrderTransition.class)
	public class GetPlacedOrderTypeExtension {

		public GetPlacedOrderTypeExtension(OrderTransition orderTransition) {
			_orderTransition = orderTransition;
		}

		@GraphQLField(
			description = "Returns the placed order by id. Raises NoSuchOrderException when the order is still open -- open carts are exposed through headless-commerce-delivery-cart, not through this API."
		)
		public PlacedOrder placedOrder() throws Exception {
			return _applyComponentServiceObjects(
				_placedOrderResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				placedOrderResource -> placedOrderResource.getPlacedOrder(
					_orderTransition.getPlacedOrderId()));
		}

		private OrderTransition _orderTransition;

	}

	@GraphQLTypeExtension(PlacedOrder.class)
	public class GetPlacedOrderOrderTransitionsPageTypeExtension {

		public GetPlacedOrderOrderTransitionsPageTypeExtension(
			PlacedOrder placedOrder) {

			_placedOrder = placedOrder;
		}

		@GraphQLField(
			description = "Returns the workflow transitions the current user can execute for the order via CommerceWorkflowedModelHelper.getWorkflowTransitions, plus the synthetic process-quote transition when the order matches the quote-processed criteria, plus the synthetic reorder transition. Refuses with CommerceOrderStatusException on open orders."
		)
		public OrderTransitionPage orderTransitions() throws Exception {
			return _applyComponentServiceObjects(
				_orderTransitionResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				orderTransitionResource -> new OrderTransitionPage(
					orderTransitionResource.getPlacedOrderOrderTransitionsPage(
						_placedOrder.getId())));
		}

		private PlacedOrder _placedOrder;

	}

	@GraphQLTypeExtension(PlacedOrderComment.class)
	public class GetPlacedOrderByExternalReferenceCodeTypeExtension {

		public GetPlacedOrderByExternalReferenceCodeTypeExtension(
			PlacedOrderComment placedOrderComment) {

			_placedOrderComment = placedOrderComment;
		}

		@GraphQLField(
			description = "Resolves the order by external reference code, refuses when it is still open, then converts it through PlacedOrderDTOConverter."
		)
		public PlacedOrder placedOrderByExternalReferenceCode()
			throws Exception {

			return _applyComponentServiceObjects(
				_placedOrderResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				placedOrderResource ->
					placedOrderResource.getPlacedOrderByExternalReferenceCode(
						_placedOrderComment.getExternalReferenceCode()));
		}

		private PlacedOrderComment _placedOrderComment;

	}

	@GraphQLTypeExtension(PlacedOrder.class)
	public class GetPlacedOrderPaymentURLTypeExtension {

		public GetPlacedOrderPaymentURLTypeExtension(PlacedOrder placedOrder) {
			_placedOrder = placedOrder;
		}

		@GraphQLField(
			description = "Builds the URL the storefront should redirect to in order to resume payment for the placed order. When callbackURL is supplied it is used as the next-step URL, otherwise the order-confirmation checkout step URL is used. Refuses on open orders."
		)
		public String paymentURL(@GraphQLName("callbackURL") String callbackURL)
			throws Exception {

			return _applyComponentServiceObjects(
				_placedOrderResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				placedOrderResource ->
					placedOrderResource.getPlacedOrderPaymentURL(
						_placedOrder.getId(), callbackURL));
		}

		private PlacedOrder _placedOrder;

	}

	@GraphQLTypeExtension(PlacedOrder.class)
	public class GetPlacedOrderCommentByExternalReferenceCodeTypeExtension {

		public GetPlacedOrderCommentByExternalReferenceCodeTypeExtension(
			PlacedOrder placedOrder) {

			_placedOrder = placedOrder;
		}

		@GraphQLField(
			description = "Loads the CommerceOrderNote by external reference code via CommerceOrderNoteService.fetchCommerceOrderNoteByExternalReferenceCode and delegates to getPlacedOrderComment. Raises NoSuchOrderNoteException when no matching note is found."
		)
		public PlacedOrderComment commentByExternalReferenceCode()
			throws Exception {

			return _applyComponentServiceObjects(
				_placedOrderCommentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				placedOrderCommentResource ->
					placedOrderCommentResource.
						getPlacedOrderCommentByExternalReferenceCode(
							_placedOrder.getExternalReferenceCode()));
		}

		private PlacedOrder _placedOrder;

	}

	@GraphQLTypeExtension(PlacedOrder.class)
	public class GetPlacedOrderItemByExternalReferenceCodeTypeExtension {

		public GetPlacedOrderItemByExternalReferenceCodeTypeExtension(
			PlacedOrder placedOrder) {

			_placedOrder = placedOrder;
		}

		@GraphQLField(
			description = "Resolves the CommerceOrderItem by external reference code and delegates to getPlacedOrderItem. Raises NoSuchOrderItemException when no matching item is found."
		)
		public PlacedOrderItem itemByExternalReferenceCode() throws Exception {
			return _applyComponentServiceObjects(
				_placedOrderItemResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				placedOrderItemResource ->
					placedOrderItemResource.
						getPlacedOrderItemByExternalReferenceCode(
							_placedOrder.getExternalReferenceCode()));
		}

		private PlacedOrder _placedOrder;

	}

	@GraphQLTypeExtension(PlacedOrder.class)
	public class GetPlacedOrderDeliveryTermTypeExtension {

		public GetPlacedOrderDeliveryTermTypeExtension(
			PlacedOrder placedOrder) {

			_placedOrder = placedOrder;
		}

		@GraphQLField(
			description = "Loads the order, refuses on open orders, and returns the CommerceTermEntry referenced by commerceOrder.getDeliveryCommerceTermEntryId() projected into the Term schema."
		)
		public Term deliveryTerm() throws Exception {
			return _applyComponentServiceObjects(
				_termResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				termResource -> termResource.getPlacedOrderDeliveryTerm(
					_placedOrder.getId()));
		}

		private PlacedOrder _placedOrder;

	}

	@GraphQLTypeExtension(PlacedOrder.class)
	public class GetPlacedOrderPaymentTermTypeExtension {

		public GetPlacedOrderPaymentTermTypeExtension(PlacedOrder placedOrder) {
			_placedOrder = placedOrder;
		}

		@GraphQLField(
			description = "Loads the order, refuses on open orders, and returns the CommerceTermEntry referenced by commerceOrder.getPaymentCommerceTermEntryId() projected into the Term schema."
		)
		public Term paymentTerm() throws Exception {
			return _applyComponentServiceObjects(
				_termResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				termResource -> termResource.getPlacedOrderPaymentTerm(
					_placedOrder.getId()));
		}

		private PlacedOrder _placedOrder;

	}

	@GraphQLTypeExtension(PlacedOrder.class)
	public class
		GetPlacedOrderByExternalReferenceCodeAttachmentsPageTypeExtension {

		public GetPlacedOrderByExternalReferenceCodeAttachmentsPageTypeExtension(
			PlacedOrder placedOrder) {

			_placedOrder = placedOrder;
		}

		@GraphQLField(
			description = "Resolves the order by external reference code and delegates to getPlacedOrderAttachmentsPage. When feature flag LPD-6252 is enabled the listing is an indexed CommerceOrderAttachment query honouring filter, sort, and search (fields -- restricted, dateCreated, dateModified, priority, commerceOrderId, externalReferenceCode, title, type). When the flag is disabled the attachments are read directly from the order's file entries and filter, sort, search are ignored."
		)
		public AttachmentPage byExternalReferenceCodeAttachments(
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_attachmentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				attachmentResource -> new AttachmentPage(
					attachmentResource.
						getPlacedOrderByExternalReferenceCodeAttachmentsPage(
							_placedOrder.getExternalReferenceCode(), search,
							_filterBiFunction.apply(
								attachmentResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								attachmentResource, sortsString))));
		}

		private PlacedOrder _placedOrder;

	}

	@GraphQLTypeExtension(PlacedOrder.class)
	public class
		GetChannelByExternalReferenceCodePlacedOrdersPageTypeExtension {

		public GetChannelByExternalReferenceCodePlacedOrdersPageTypeExtension(
			PlacedOrder placedOrder) {

			_placedOrder = placedOrder;
		}

		@GraphQLField(
			description = "Resolves the channel by external reference code and delegates to getChannelPlacedOrdersPage to return non-open placed orders visible to the caller in that channel."
		)
		public PlacedOrderPage channelByExternalReferenceCodePlacedOrders(
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_placedOrderResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				placedOrderResource -> new PlacedOrderPage(
					placedOrderResource.
						getChannelByExternalReferenceCodePlacedOrdersPage(
							_placedOrder.getExternalReferenceCode(), search,
							_filterBiFunction.apply(
								placedOrderResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								placedOrderResource, sortsString))));
		}

		private PlacedOrder _placedOrder;

	}

	@GraphQLTypeExtension(PlacedOrder.class)
	public class GetPlacedOrderByExternalReferenceCodePaymentURLTypeExtension {

		public GetPlacedOrderByExternalReferenceCodePaymentURLTypeExtension(
			PlacedOrder placedOrder) {

			_placedOrder = placedOrder;
		}

		@GraphQLField(
			description = "Resolves the order by external reference code and delegates to getPlacedOrderPaymentURL to build the next-step payment URL the storefront should redirect to."
		)
		public String byExternalReferenceCodePaymentURL(
				@GraphQLName("callbackURL") String callbackURL)
			throws Exception {

			return _applyComponentServiceObjects(
				_placedOrderResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				placedOrderResource ->
					placedOrderResource.
						getPlacedOrderByExternalReferenceCodePaymentURL(
							_placedOrder.getExternalReferenceCode(),
							callbackURL));
		}

		private PlacedOrder _placedOrder;

	}

	@GraphQLTypeExtension(PlacedOrder.class)
	public class
		GetPlacedOrderByExternalReferenceCodePlacedOrderBillingAddressTypeExtension {

		public GetPlacedOrderByExternalReferenceCodePlacedOrderBillingAddressTypeExtension(
			PlacedOrder placedOrder) {

			_placedOrder = placedOrder;
		}

		@GraphQLField(
			description = "Resolves the order by external reference code and returns the snapshot CommerceAddress referenced by the order's billingAddressId via PlacedOrderAddressDTOConverter. Returns an empty PlacedOrderAddress when the order has no billing address."
		)
		public PlacedOrderAddress
				byExternalReferenceCodePlacedOrderBillingAddress()
			throws Exception {

			return _applyComponentServiceObjects(
				_placedOrderAddressResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				placedOrderAddressResource ->
					placedOrderAddressResource.
						getPlacedOrderByExternalReferenceCodePlacedOrderBillingAddress(
							_placedOrder.getExternalReferenceCode()));
		}

		private PlacedOrder _placedOrder;

	}

	@GraphQLTypeExtension(PlacedOrder.class)
	public class
		GetPlacedOrderByExternalReferenceCodePlacedOrderShippingAddressTypeExtension {

		public GetPlacedOrderByExternalReferenceCodePlacedOrderShippingAddressTypeExtension(
			PlacedOrder placedOrder) {

			_placedOrder = placedOrder;
		}

		@GraphQLField(
			description = "Resolves the order by external reference code and returns the snapshot CommerceAddress referenced by the order's shippingAddressId via PlacedOrderAddressDTOConverter."
		)
		public PlacedOrderAddress
				byExternalReferenceCodePlacedOrderShippingAddress()
			throws Exception {

			return _applyComponentServiceObjects(
				_placedOrderAddressResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				placedOrderAddressResource ->
					placedOrderAddressResource.
						getPlacedOrderByExternalReferenceCodePlacedOrderShippingAddress(
							_placedOrder.getExternalReferenceCode()));
		}

		private PlacedOrder _placedOrder;

	}

	@GraphQLTypeExtension(PlacedOrder.class)
	public class
		GetPlacedOrderByExternalReferenceCodePlacedOrderCommentsPageTypeExtension {

		public GetPlacedOrderByExternalReferenceCodePlacedOrderCommentsPageTypeExtension(
			PlacedOrder placedOrder) {

			_placedOrder = placedOrder;
		}

		@GraphQLField(
			description = "Resolves the order by external reference code and returns a page of its CommerceOrderNote rows via CommerceOrderNoteService.getCommerceOrderNotes(orderId, start, end). The endpoint accepts page and pageSize only -- filter, sort, and keyword search are not applied."
		)
		public PlacedOrderCommentPage
				byExternalReferenceCodePlacedOrderComments(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_placedOrderCommentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				placedOrderCommentResource -> new PlacedOrderCommentPage(
					placedOrderCommentResource.
						getPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage(
							_placedOrder.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private PlacedOrder _placedOrder;

	}

	@GraphQLTypeExtension(PlacedOrder.class)
	public class
		GetPlacedOrderByExternalReferenceCodePlacedOrderItemsPageTypeExtension {

		public GetPlacedOrderByExternalReferenceCodePlacedOrderItemsPageTypeExtension(
			PlacedOrder placedOrder) {

			_placedOrder = placedOrder;
		}

		@GraphQLField(
			description = "Resolves the order by external reference code and runs an indexed CommerceOrderItem search scoped to that order id with parentCommerceOrderItemId = 0 so only top-level items are returned. Search drives the keyword query and sort supports PlacedOrderItemEntityModel fields -- quantity, name, sku, unitOfMeasure. The skuId parameter is declared but not currently applied as an extra filter."
		)
		public PlacedOrderItemPage byExternalReferenceCodePlacedOrderItems(
				@GraphQLName("search") String search,
				@GraphQLName("skuId") Long skuId,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_placedOrderItemResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				placedOrderItemResource -> new PlacedOrderItemPage(
					placedOrderItemResource.
						getPlacedOrderByExternalReferenceCodePlacedOrderItemsPage(
							_placedOrder.getExternalReferenceCode(), search,
							skuId, Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								placedOrderItemResource, sortsString))));
		}

		private PlacedOrder _placedOrder;

	}

	@GraphQLTypeExtension(PlacedOrder.class)
	public class
		GetPlacedOrderItemByExternalReferenceCodePlacedOrderItemShipmentsPageTypeExtension {

		public GetPlacedOrderItemByExternalReferenceCodePlacedOrderItemShipmentsPageTypeExtension(
			PlacedOrder placedOrder) {

			_placedOrder = placedOrder;
		}

		@GraphQLField(
			description = "Resolves the CommerceOrderItem by external reference code and delegates to getPlacedOrderItemPlacedOrderItemShipmentsPage to return the shipments for that item."
		)
		public PlacedOrderItemShipmentPage
				itemByExternalReferenceCodePlacedOrderItemShipments()
			throws Exception {

			return _applyComponentServiceObjects(
				_placedOrderItemShipmentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				placedOrderItemShipmentResource ->
					new PlacedOrderItemShipmentPage(
						placedOrderItemShipmentResource.
							getPlacedOrderItemByExternalReferenceCodePlacedOrderItemShipmentsPage(
								_placedOrder.getExternalReferenceCode())));
		}

		private PlacedOrder _placedOrder;

	}

	@GraphQLTypeExtension(PlacedOrder.class)
	public class
		GetPlacedOrderByExternalReferenceCodeShipmentsPageTypeExtension {

		public GetPlacedOrderByExternalReferenceCodeShipmentsPageTypeExtension(
			PlacedOrder placedOrder) {

			_placedOrder = placedOrder;
		}

		@GraphQLField(
			description = "Resolves the order by external reference code and runs an indexed CommerceShipment query whose itemsCount and oneLineAddress are read straight from the search document. Filter, sort, and search use ShipmentEntityModel fields -- status, createDate, expectedDate, shippingDate, id, carrier, tracking."
		)
		public ShipmentPage byExternalReferenceCodeShipments(
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_shipmentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				shipmentResource -> new ShipmentPage(
					shipmentResource.
						getPlacedOrderByExternalReferenceCodeShipmentsPage(
							_placedOrder.getExternalReferenceCode(), search,
							_filterBiFunction.apply(
								shipmentResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								shipmentResource, sortsString))));
		}

		private PlacedOrder _placedOrder;

	}

	@GraphQLTypeExtension(PlacedOrder.class)
	public class
		GetPlacedOrderByExternalReferenceCodeDeliveryTermTypeExtension {

		public GetPlacedOrderByExternalReferenceCodeDeliveryTermTypeExtension(
			PlacedOrder placedOrder) {

			_placedOrder = placedOrder;
		}

		@GraphQLField(
			description = "Resolves the order by external reference code and delegates to getPlacedOrderDeliveryTerm to return the linked CommerceTermEntry as a Term."
		)
		public Term byExternalReferenceCodeDeliveryTerm() throws Exception {
			return _applyComponentServiceObjects(
				_termResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				termResource ->
					termResource.
						getPlacedOrderByExternalReferenceCodeDeliveryTerm(
							_placedOrder.getExternalReferenceCode()));
		}

		private PlacedOrder _placedOrder;

	}

	@GraphQLTypeExtension(PlacedOrder.class)
	public class GetPlacedOrderByExternalReferenceCodePaymentTermTypeExtension {

		public GetPlacedOrderByExternalReferenceCodePaymentTermTypeExtension(
			PlacedOrder placedOrder) {

			_placedOrder = placedOrder;
		}

		@GraphQLField(
			description = "Resolves the order by external reference code and delegates to getPlacedOrderPaymentTerm to return the linked CommerceTermEntry as a Term."
		)
		public Term byExternalReferenceCodePaymentTerm() throws Exception {
			return _applyComponentServiceObjects(
				_termResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				termResource ->
					termResource.
						getPlacedOrderByExternalReferenceCodePaymentTerm(
							_placedOrder.getExternalReferenceCode()));
		}

		private PlacedOrder _placedOrder;

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

	@GraphQLName("OrderTransitionPage")
	public class OrderTransitionPage {

		public OrderTransitionPage(Page orderTransitionPage) {
			actions = orderTransitionPage.getActions();

			items = orderTransitionPage.getItems();
			lastPage = orderTransitionPage.getLastPage();
			page = orderTransitionPage.getPage();
			pageSize = orderTransitionPage.getPageSize();
			totalCount = orderTransitionPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<OrderTransition> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("PlacedOrderPage")
	public class PlacedOrderPage {

		public PlacedOrderPage(Page placedOrderPage) {
			actions = placedOrderPage.getActions();

			items = placedOrderPage.getItems();
			lastPage = placedOrderPage.getLastPage();
			page = placedOrderPage.getPage();
			pageSize = placedOrderPage.getPageSize();
			totalCount = placedOrderPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<PlacedOrder> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("PlacedOrderAddressPage")
	public class PlacedOrderAddressPage {

		public PlacedOrderAddressPage(Page placedOrderAddressPage) {
			actions = placedOrderAddressPage.getActions();

			items = placedOrderAddressPage.getItems();
			lastPage = placedOrderAddressPage.getLastPage();
			page = placedOrderAddressPage.getPage();
			pageSize = placedOrderAddressPage.getPageSize();
			totalCount = placedOrderAddressPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<PlacedOrderAddress> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("PlacedOrderCommentPage")
	public class PlacedOrderCommentPage {

		public PlacedOrderCommentPage(Page placedOrderCommentPage) {
			actions = placedOrderCommentPage.getActions();

			items = placedOrderCommentPage.getItems();
			lastPage = placedOrderCommentPage.getLastPage();
			page = placedOrderCommentPage.getPage();
			pageSize = placedOrderCommentPage.getPageSize();
			totalCount = placedOrderCommentPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<PlacedOrderComment> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("PlacedOrderItemPage")
	public class PlacedOrderItemPage {

		public PlacedOrderItemPage(Page placedOrderItemPage) {
			actions = placedOrderItemPage.getActions();

			items = placedOrderItemPage.getItems();
			lastPage = placedOrderItemPage.getLastPage();
			page = placedOrderItemPage.getPage();
			pageSize = placedOrderItemPage.getPageSize();
			totalCount = placedOrderItemPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<PlacedOrderItem> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("PlacedOrderItemShipmentPage")
	public class PlacedOrderItemShipmentPage {

		public PlacedOrderItemShipmentPage(Page placedOrderItemShipmentPage) {
			actions = placedOrderItemShipmentPage.getActions();

			items = placedOrderItemShipmentPage.getItems();
			lastPage = placedOrderItemShipmentPage.getLastPage();
			page = placedOrderItemShipmentPage.getPage();
			pageSize = placedOrderItemShipmentPage.getPageSize();
			totalCount = placedOrderItemShipmentPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<PlacedOrderItemShipment> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ShipmentPage")
	public class ShipmentPage {

		public ShipmentPage(Page shipmentPage) {
			actions = shipmentPage.getActions();

			items = shipmentPage.getItems();
			lastPage = shipmentPage.getLastPage();
			page = shipmentPage.getPage();
			pageSize = shipmentPage.getPageSize();
			totalCount = shipmentPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Shipment> items;

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

	private void _populateResourceContext(
			OrderTransitionResource orderTransitionResource)
		throws Exception {

		orderTransitionResource.setContextAcceptLanguage(_acceptLanguage);
		orderTransitionResource.setContextCompany(_company);
		orderTransitionResource.setContextHttpServletRequest(
			_httpServletRequest);
		orderTransitionResource.setContextHttpServletResponse(
			_httpServletResponse);
		orderTransitionResource.setContextUriInfo(_uriInfo);
		orderTransitionResource.setContextUser(_user);
		orderTransitionResource.setGroupLocalService(_groupLocalService);
		orderTransitionResource.setResourceActionLocalService(
			_resourceActionLocalService);
		orderTransitionResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		orderTransitionResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			PlacedOrderResource placedOrderResource)
		throws Exception {

		placedOrderResource.setContextAcceptLanguage(_acceptLanguage);
		placedOrderResource.setContextCompany(_company);
		placedOrderResource.setContextHttpServletRequest(_httpServletRequest);
		placedOrderResource.setContextHttpServletResponse(_httpServletResponse);
		placedOrderResource.setContextUriInfo(_uriInfo);
		placedOrderResource.setContextUser(_user);
		placedOrderResource.setGroupLocalService(_groupLocalService);
		placedOrderResource.setResourceActionLocalService(
			_resourceActionLocalService);
		placedOrderResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		placedOrderResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			PlacedOrderAddressResource placedOrderAddressResource)
		throws Exception {

		placedOrderAddressResource.setContextAcceptLanguage(_acceptLanguage);
		placedOrderAddressResource.setContextCompany(_company);
		placedOrderAddressResource.setContextHttpServletRequest(
			_httpServletRequest);
		placedOrderAddressResource.setContextHttpServletResponse(
			_httpServletResponse);
		placedOrderAddressResource.setContextUriInfo(_uriInfo);
		placedOrderAddressResource.setContextUser(_user);
		placedOrderAddressResource.setGroupLocalService(_groupLocalService);
		placedOrderAddressResource.setResourceActionLocalService(
			_resourceActionLocalService);
		placedOrderAddressResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		placedOrderAddressResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			PlacedOrderCommentResource placedOrderCommentResource)
		throws Exception {

		placedOrderCommentResource.setContextAcceptLanguage(_acceptLanguage);
		placedOrderCommentResource.setContextCompany(_company);
		placedOrderCommentResource.setContextHttpServletRequest(
			_httpServletRequest);
		placedOrderCommentResource.setContextHttpServletResponse(
			_httpServletResponse);
		placedOrderCommentResource.setContextUriInfo(_uriInfo);
		placedOrderCommentResource.setContextUser(_user);
		placedOrderCommentResource.setGroupLocalService(_groupLocalService);
		placedOrderCommentResource.setResourceActionLocalService(
			_resourceActionLocalService);
		placedOrderCommentResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		placedOrderCommentResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			PlacedOrderItemResource placedOrderItemResource)
		throws Exception {

		placedOrderItemResource.setContextAcceptLanguage(_acceptLanguage);
		placedOrderItemResource.setContextCompany(_company);
		placedOrderItemResource.setContextHttpServletRequest(
			_httpServletRequest);
		placedOrderItemResource.setContextHttpServletResponse(
			_httpServletResponse);
		placedOrderItemResource.setContextUriInfo(_uriInfo);
		placedOrderItemResource.setContextUser(_user);
		placedOrderItemResource.setGroupLocalService(_groupLocalService);
		placedOrderItemResource.setResourceActionLocalService(
			_resourceActionLocalService);
		placedOrderItemResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		placedOrderItemResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			PlacedOrderItemShipmentResource placedOrderItemShipmentResource)
		throws Exception {

		placedOrderItemShipmentResource.setContextAcceptLanguage(
			_acceptLanguage);
		placedOrderItemShipmentResource.setContextCompany(_company);
		placedOrderItemShipmentResource.setContextHttpServletRequest(
			_httpServletRequest);
		placedOrderItemShipmentResource.setContextHttpServletResponse(
			_httpServletResponse);
		placedOrderItemShipmentResource.setContextUriInfo(_uriInfo);
		placedOrderItemShipmentResource.setContextUser(_user);
		placedOrderItemShipmentResource.setGroupLocalService(
			_groupLocalService);
		placedOrderItemShipmentResource.setResourceActionLocalService(
			_resourceActionLocalService);
		placedOrderItemShipmentResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		placedOrderItemShipmentResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(ShipmentResource shipmentResource)
		throws Exception {

		shipmentResource.setContextAcceptLanguage(_acceptLanguage);
		shipmentResource.setContextCompany(_company);
		shipmentResource.setContextHttpServletRequest(_httpServletRequest);
		shipmentResource.setContextHttpServletResponse(_httpServletResponse);
		shipmentResource.setContextUriInfo(_uriInfo);
		shipmentResource.setContextUser(_user);
		shipmentResource.setGroupLocalService(_groupLocalService);
		shipmentResource.setResourceActionLocalService(
			_resourceActionLocalService);
		shipmentResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		shipmentResource.setRoleLocalService(_roleLocalService);
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

	private static ComponentServiceObjects<AttachmentResource>
		_attachmentResourceComponentServiceObjects;
	private static ComponentServiceObjects<OrderTransitionResource>
		_orderTransitionResourceComponentServiceObjects;
	private static ComponentServiceObjects<PlacedOrderResource>
		_placedOrderResourceComponentServiceObjects;
	private static ComponentServiceObjects<PlacedOrderAddressResource>
		_placedOrderAddressResourceComponentServiceObjects;
	private static ComponentServiceObjects<PlacedOrderCommentResource>
		_placedOrderCommentResourceComponentServiceObjects;
	private static ComponentServiceObjects<PlacedOrderItemResource>
		_placedOrderItemResourceComponentServiceObjects;
	private static ComponentServiceObjects<PlacedOrderItemShipmentResource>
		_placedOrderItemShipmentResourceComponentServiceObjects;
	private static ComponentServiceObjects<ShipmentResource>
		_shipmentResourceComponentServiceObjects;
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
// LIFERAY-REST-BUILDER-HASH:467262660