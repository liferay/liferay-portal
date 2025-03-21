/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.internal.graphql.servlet.v1_0;

import com.liferay.headless.commerce.delivery.order.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.headless.commerce.delivery.order.internal.graphql.query.v1_0.Query;
import com.liferay.headless.commerce.delivery.order.internal.resource.v1_0.AttachmentResourceImpl;
import com.liferay.headless.commerce.delivery.order.internal.resource.v1_0.OrderTransitionResourceImpl;
import com.liferay.headless.commerce.delivery.order.internal.resource.v1_0.PlacedOrderAddressResourceImpl;
import com.liferay.headless.commerce.delivery.order.internal.resource.v1_0.PlacedOrderCommentResourceImpl;
import com.liferay.headless.commerce.delivery.order.internal.resource.v1_0.PlacedOrderItemResourceImpl;
import com.liferay.headless.commerce.delivery.order.internal.resource.v1_0.PlacedOrderItemShipmentResourceImpl;
import com.liferay.headless.commerce.delivery.order.internal.resource.v1_0.PlacedOrderResourceImpl;
import com.liferay.headless.commerce.delivery.order.internal.resource.v1_0.ShipmentResourceImpl;
import com.liferay.headless.commerce.delivery.order.internal.resource.v1_0.TermResourceImpl;
import com.liferay.headless.commerce.delivery.order.resource.v1_0.AttachmentResource;
import com.liferay.headless.commerce.delivery.order.resource.v1_0.OrderTransitionResource;
import com.liferay.headless.commerce.delivery.order.resource.v1_0.PlacedOrderAddressResource;
import com.liferay.headless.commerce.delivery.order.resource.v1_0.PlacedOrderCommentResource;
import com.liferay.headless.commerce.delivery.order.resource.v1_0.PlacedOrderItemResource;
import com.liferay.headless.commerce.delivery.order.resource.v1_0.PlacedOrderItemShipmentResource;
import com.liferay.headless.commerce.delivery.order.resource.v1_0.PlacedOrderResource;
import com.liferay.headless.commerce.delivery.order.resource.v1_0.ShipmentResource;
import com.liferay.headless.commerce.delivery.order.resource.v1_0.TermResource;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;

import jakarta.annotation.Generated;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setAttachmentResourceComponentServiceObjects(
			_attachmentResourceComponentServiceObjects);
		Mutation.setOrderTransitionResourceComponentServiceObjects(
			_orderTransitionResourceComponentServiceObjects);
		Mutation.setPlacedOrderResourceComponentServiceObjects(
			_placedOrderResourceComponentServiceObjects);
		Mutation.setPlacedOrderCommentResourceComponentServiceObjects(
			_placedOrderCommentResourceComponentServiceObjects);
		Mutation.setPlacedOrderItemResourceComponentServiceObjects(
			_placedOrderItemResourceComponentServiceObjects);
		Mutation.setPlacedOrderItemShipmentResourceComponentServiceObjects(
			_placedOrderItemShipmentResourceComponentServiceObjects);
		Mutation.setShipmentResourceComponentServiceObjects(
			_shipmentResourceComponentServiceObjects);

		Query.setAttachmentResourceComponentServiceObjects(
			_attachmentResourceComponentServiceObjects);
		Query.setOrderTransitionResourceComponentServiceObjects(
			_orderTransitionResourceComponentServiceObjects);
		Query.setPlacedOrderResourceComponentServiceObjects(
			_placedOrderResourceComponentServiceObjects);
		Query.setPlacedOrderAddressResourceComponentServiceObjects(
			_placedOrderAddressResourceComponentServiceObjects);
		Query.setPlacedOrderCommentResourceComponentServiceObjects(
			_placedOrderCommentResourceComponentServiceObjects);
		Query.setPlacedOrderItemResourceComponentServiceObjects(
			_placedOrderItemResourceComponentServiceObjects);
		Query.setPlacedOrderItemShipmentResourceComponentServiceObjects(
			_placedOrderItemShipmentResourceComponentServiceObjects);
		Query.setShipmentResourceComponentServiceObjects(
			_shipmentResourceComponentServiceObjects);
		Query.setTermResourceComponentServiceObjects(
			_termResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Headless.Commerce.Delivery.Order";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/headless-commerce-delivery-order-graphql/v1_0";
	}

	@Override
	public Query getQuery() {
		return new Query();
	}

	public ObjectValuePair<Class<?>, String> getResourceMethodObjectValuePair(
		String methodName, boolean mutation) {

		if (mutation) {
			return _resourceMethodObjectValuePairs.get(
				"mutation#" + methodName);
		}

		return _resourceMethodObjectValuePairs.get("query#" + methodName);
	}

	private static final Map<String, ObjectValuePair<Class<?>, String>>
		_resourceMethodObjectValuePairs =
			new HashMap<String, ObjectValuePair<Class<?>, String>>() {
				{
					put(
						"mutation#createPlacedOrderByExternalReferenceCodeAttachmentByBase64",
						new ObjectValuePair<>(
							AttachmentResourceImpl.class,
							"postPlacedOrderByExternalReferenceCodeAttachmentByBase64"));
					put(
						"mutation#deletePlacedOrderByExternalReferenceCodeAttachmentByExternalReferenceCodeAttachmentExternalReferenceCode",
						new ObjectValuePair<>(
							AttachmentResourceImpl.class,
							"deletePlacedOrderByExternalReferenceCodeAttachmentByExternalReferenceCodeAttachmentExternalReferenceCode"));
					put(
						"mutation#createPlacedOrderAttachmentsPageExportBatch",
						new ObjectValuePair<>(
							AttachmentResourceImpl.class,
							"postPlacedOrderAttachmentsPageExportBatch"));
					put(
						"mutation#createPlacedOrderAttachmentByBase64",
						new ObjectValuePair<>(
							AttachmentResourceImpl.class,
							"postPlacedOrderAttachmentByBase64"));
					put(
						"mutation#deletePlacedOrderAttachment",
						new ObjectValuePair<>(
							AttachmentResourceImpl.class,
							"deletePlacedOrderAttachment"));
					put(
						"mutation#createPlacedOrderOrderTransitionsPageExportBatch",
						new ObjectValuePair<>(
							OrderTransitionResourceImpl.class,
							"postPlacedOrderOrderTransitionsPageExportBatch"));
					put(
						"mutation#createPlacedOrderOrderTransition",
						new ObjectValuePair<>(
							OrderTransitionResourceImpl.class,
							"postPlacedOrderOrderTransition"));
					put(
						"mutation#createPlacedOrderOrderTransitionBatch",
						new ObjectValuePair<>(
							OrderTransitionResourceImpl.class,
							"postPlacedOrderOrderTransitionBatch"));
					put(
						"mutation#patchPlacedOrderByExternalReferenceCode",
						new ObjectValuePair<>(
							PlacedOrderResourceImpl.class,
							"patchPlacedOrderByExternalReferenceCode"));
					put(
						"mutation#patchPlacedOrder",
						new ObjectValuePair<>(
							PlacedOrderResourceImpl.class, "patchPlacedOrder"));
					put(
						"mutation#createPlacedOrderPlacedOrderCommentsPageExportBatch",
						new ObjectValuePair<>(
							PlacedOrderCommentResourceImpl.class,
							"postPlacedOrderPlacedOrderCommentsPageExportBatch"));
					put(
						"mutation#createPlacedOrderPlacedOrderItemsPageExportBatch",
						new ObjectValuePair<>(
							PlacedOrderItemResourceImpl.class,
							"postPlacedOrderPlacedOrderItemsPageExportBatch"));
					put(
						"mutation#createPlacedOrderItemPlacedOrderItemShipmentsPageExportBatch",
						new ObjectValuePair<>(
							PlacedOrderItemShipmentResourceImpl.class,
							"postPlacedOrderItemPlacedOrderItemShipmentsPageExportBatch"));
					put(
						"mutation#createPlacedOrderShipmentsPageExportBatch",
						new ObjectValuePair<>(
							ShipmentResourceImpl.class,
							"postPlacedOrderShipmentsPageExportBatch"));

					put(
						"query#placedOrderByExternalReferenceCodeAttachments",
						new ObjectValuePair<>(
							AttachmentResourceImpl.class,
							"getPlacedOrderByExternalReferenceCodeAttachmentsPage"));
					put(
						"query#placedOrderAttachments",
						new ObjectValuePair<>(
							AttachmentResourceImpl.class,
							"getPlacedOrderAttachmentsPage"));
					put(
						"query#placedOrderOrderTransitions",
						new ObjectValuePair<>(
							OrderTransitionResourceImpl.class,
							"getPlacedOrderOrderTransitionsPage"));
					put(
						"query#channelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrders",
						new ObjectValuePair<>(
							PlacedOrderResourceImpl.class,
							"getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage"));
					put(
						"query#channelByExternalReferenceCodePlacedOrders",
						new ObjectValuePair<>(
							PlacedOrderResourceImpl.class,
							"getChannelByExternalReferenceCodePlacedOrdersPage"));
					put(
						"query#channelAccountPlacedOrders",
						new ObjectValuePair<>(
							PlacedOrderResourceImpl.class,
							"getChannelAccountPlacedOrdersPage"));
					put(
						"query#channelPlacedOrders",
						new ObjectValuePair<>(
							PlacedOrderResourceImpl.class,
							"getChannelPlacedOrdersPage"));
					put(
						"query#placedOrderByExternalReferenceCode",
						new ObjectValuePair<>(
							PlacedOrderResourceImpl.class,
							"getPlacedOrderByExternalReferenceCode"));
					put(
						"query#placedOrderByExternalReferenceCodePaymentURL",
						new ObjectValuePair<>(
							PlacedOrderResourceImpl.class,
							"getPlacedOrderByExternalReferenceCodePaymentURL"));
					put(
						"query#placedOrder",
						new ObjectValuePair<>(
							PlacedOrderResourceImpl.class, "getPlacedOrder"));
					put(
						"query#placedOrderPaymentURL",
						new ObjectValuePair<>(
							PlacedOrderResourceImpl.class,
							"getPlacedOrderPaymentURL"));
					put(
						"query#placedOrderByExternalReferenceCodePlacedOrderBillingAddress",
						new ObjectValuePair<>(
							PlacedOrderAddressResourceImpl.class,
							"getPlacedOrderByExternalReferenceCodePlacedOrderBillingAddress"));
					put(
						"query#placedOrderByExternalReferenceCodePlacedOrderShippingAddress",
						new ObjectValuePair<>(
							PlacedOrderAddressResourceImpl.class,
							"getPlacedOrderByExternalReferenceCodePlacedOrderShippingAddress"));
					put(
						"query#placedOrderPlacedOrderBillingAddress",
						new ObjectValuePair<>(
							PlacedOrderAddressResourceImpl.class,
							"getPlacedOrderPlacedOrderBillingAddress"));
					put(
						"query#placedOrderPlacedOrderShippingAddress",
						new ObjectValuePair<>(
							PlacedOrderAddressResourceImpl.class,
							"getPlacedOrderPlacedOrderShippingAddress"));
					put(
						"query#placedOrderCommentByExternalReferenceCode",
						new ObjectValuePair<>(
							PlacedOrderCommentResourceImpl.class,
							"getPlacedOrderCommentByExternalReferenceCode"));
					put(
						"query#placedOrderComment",
						new ObjectValuePair<>(
							PlacedOrderCommentResourceImpl.class,
							"getPlacedOrderComment"));
					put(
						"query#placedOrderByExternalReferenceCodePlacedOrderComments",
						new ObjectValuePair<>(
							PlacedOrderCommentResourceImpl.class,
							"getPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage"));
					put(
						"query#placedOrderPlacedOrderComments",
						new ObjectValuePair<>(
							PlacedOrderCommentResourceImpl.class,
							"getPlacedOrderPlacedOrderCommentsPage"));
					put(
						"query#placedOrderItemByExternalReferenceCode",
						new ObjectValuePair<>(
							PlacedOrderItemResourceImpl.class,
							"getPlacedOrderItemByExternalReferenceCode"));
					put(
						"query#placedOrderItem",
						new ObjectValuePair<>(
							PlacedOrderItemResourceImpl.class,
							"getPlacedOrderItem"));
					put(
						"query#placedOrderByExternalReferenceCodePlacedOrderItems",
						new ObjectValuePair<>(
							PlacedOrderItemResourceImpl.class,
							"getPlacedOrderByExternalReferenceCodePlacedOrderItemsPage"));
					put(
						"query#placedOrderPlacedOrderItems",
						new ObjectValuePair<>(
							PlacedOrderItemResourceImpl.class,
							"getPlacedOrderPlacedOrderItemsPage"));
					put(
						"query#placedOrderItemByExternalReferenceCodePlacedOrderItemShipments",
						new ObjectValuePair<>(
							PlacedOrderItemShipmentResourceImpl.class,
							"getPlacedOrderItemByExternalReferenceCodePlacedOrderItemShipmentsPage"));
					put(
						"query#placedOrderItemPlacedOrderItemShipments",
						new ObjectValuePair<>(
							PlacedOrderItemShipmentResourceImpl.class,
							"getPlacedOrderItemPlacedOrderItemShipmentsPage"));
					put(
						"query#placedOrderByExternalReferenceCodeShipments",
						new ObjectValuePair<>(
							ShipmentResourceImpl.class,
							"getPlacedOrderByExternalReferenceCodeShipmentsPage"));
					put(
						"query#placedOrderShipments",
						new ObjectValuePair<>(
							ShipmentResourceImpl.class,
							"getPlacedOrderShipmentsPage"));
					put(
						"query#placedOrderByExternalReferenceCodeDeliveryTerm",
						new ObjectValuePair<>(
							TermResourceImpl.class,
							"getPlacedOrderByExternalReferenceCodeDeliveryTerm"));
					put(
						"query#placedOrderByExternalReferenceCodePaymentTerm",
						new ObjectValuePair<>(
							TermResourceImpl.class,
							"getPlacedOrderByExternalReferenceCodePaymentTerm"));
					put(
						"query#placedOrderDeliveryTerm",
						new ObjectValuePair<>(
							TermResourceImpl.class,
							"getPlacedOrderDeliveryTerm"));
					put(
						"query#placedOrderPaymentTerm",
						new ObjectValuePair<>(
							TermResourceImpl.class,
							"getPlacedOrderPaymentTerm"));

					put(
						"query#PlacedOrder.byExternalReferenceCodePlacedOrderBillingAddress",
						new ObjectValuePair<>(
							PlacedOrderAddressResourceImpl.class,
							"getPlacedOrderByExternalReferenceCodePlacedOrderBillingAddress"));
					put(
						"query#PlacedOrder.byExternalReferenceCodePaymentTerm",
						new ObjectValuePair<>(
							TermResourceImpl.class,
							"getPlacedOrderByExternalReferenceCodePaymentTerm"));
					put(
						"query#PlacedOrder.paymentURL",
						new ObjectValuePair<>(
							PlacedOrderResourceImpl.class,
							"getPlacedOrderPaymentURL"));
					put(
						"query#PlacedOrder.deliveryTerm",
						new ObjectValuePair<>(
							TermResourceImpl.class,
							"getPlacedOrderDeliveryTerm"));
					put(
						"query#PlacedOrder.channelByExternalReferenceCodePlacedOrders",
						new ObjectValuePair<>(
							PlacedOrderResourceImpl.class,
							"getChannelByExternalReferenceCodePlacedOrdersPage"));
					put(
						"query#PlacedOrder.byExternalReferenceCodePlacedOrderItems",
						new ObjectValuePair<>(
							PlacedOrderItemResourceImpl.class,
							"getPlacedOrderByExternalReferenceCodePlacedOrderItemsPage"));
					put(
						"query#PlacedOrder.paymentTerm",
						new ObjectValuePair<>(
							TermResourceImpl.class,
							"getPlacedOrderPaymentTerm"));
					put(
						"query#PlacedOrder.byExternalReferenceCodePlacedOrderComments",
						new ObjectValuePair<>(
							PlacedOrderCommentResourceImpl.class,
							"getPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage"));
					put(
						"query#OrderTransition.placedOrder",
						new ObjectValuePair<>(
							PlacedOrderResourceImpl.class, "getPlacedOrder"));
					put(
						"query#PlacedOrder.commentByExternalReferenceCode",
						new ObjectValuePair<>(
							PlacedOrderCommentResourceImpl.class,
							"getPlacedOrderCommentByExternalReferenceCode"));
					put(
						"query#PlacedOrder.byExternalReferenceCodePaymentURL",
						new ObjectValuePair<>(
							PlacedOrderResourceImpl.class,
							"getPlacedOrderByExternalReferenceCodePaymentURL"));
					put(
						"query#PlacedOrder.itemByExternalReferenceCodePlacedOrderItemShipments",
						new ObjectValuePair<>(
							PlacedOrderItemShipmentResourceImpl.class,
							"getPlacedOrderItemByExternalReferenceCodePlacedOrderItemShipmentsPage"));
					put(
						"query#PlacedOrder.orderTransitions",
						new ObjectValuePair<>(
							OrderTransitionResourceImpl.class,
							"getPlacedOrderOrderTransitionsPage"));
					put(
						"query#PlacedOrder.itemByExternalReferenceCode",
						new ObjectValuePair<>(
							PlacedOrderItemResourceImpl.class,
							"getPlacedOrderItemByExternalReferenceCode"));
					put(
						"query#PlacedOrderComment.placedOrderByExternalReferenceCode",
						new ObjectValuePair<>(
							PlacedOrderResourceImpl.class,
							"getPlacedOrderByExternalReferenceCode"));
					put(
						"query#PlacedOrder.byExternalReferenceCodeDeliveryTerm",
						new ObjectValuePair<>(
							TermResourceImpl.class,
							"getPlacedOrderByExternalReferenceCodeDeliveryTerm"));
					put(
						"query#PlacedOrder.byExternalReferenceCodeAttachments",
						new ObjectValuePair<>(
							AttachmentResourceImpl.class,
							"getPlacedOrderByExternalReferenceCodeAttachmentsPage"));
					put(
						"query#PlacedOrder.byExternalReferenceCodePlacedOrderShippingAddress",
						new ObjectValuePair<>(
							PlacedOrderAddressResourceImpl.class,
							"getPlacedOrderByExternalReferenceCodePlacedOrderShippingAddress"));
					put(
						"query#PlacedOrder.byExternalReferenceCodeShipments",
						new ObjectValuePair<>(
							ShipmentResourceImpl.class,
							"getPlacedOrderByExternalReferenceCodeShipmentsPage"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AttachmentResource>
		_attachmentResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<OrderTransitionResource>
		_orderTransitionResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PlacedOrderResource>
		_placedOrderResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PlacedOrderCommentResource>
		_placedOrderCommentResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PlacedOrderItemResource>
		_placedOrderItemResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PlacedOrderItemShipmentResource>
		_placedOrderItemShipmentResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ShipmentResource>
		_shipmentResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PlacedOrderAddressResource>
		_placedOrderAddressResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TermResource>
		_termResourceComponentServiceObjects;

}