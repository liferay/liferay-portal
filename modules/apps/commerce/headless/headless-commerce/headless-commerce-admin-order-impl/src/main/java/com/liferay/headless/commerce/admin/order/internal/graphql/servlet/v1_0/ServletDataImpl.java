/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.graphql.servlet.v1_0;

import com.liferay.headless.commerce.admin.order.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.headless.commerce.admin.order.internal.graphql.query.v1_0.Query;
import com.liferay.headless.commerce.admin.order.internal.resource.v1_0.AccountResourceImpl;
import com.liferay.headless.commerce.admin.order.internal.resource.v1_0.BillingAddressResourceImpl;
import com.liferay.headless.commerce.admin.order.internal.resource.v1_0.ChannelResourceImpl;
import com.liferay.headless.commerce.admin.order.internal.resource.v1_0.OrderAccountGroupResourceImpl;
import com.liferay.headless.commerce.admin.order.internal.resource.v1_0.OrderItemResourceImpl;
import com.liferay.headless.commerce.admin.order.internal.resource.v1_0.OrderNoteResourceImpl;
import com.liferay.headless.commerce.admin.order.internal.resource.v1_0.OrderResourceImpl;
import com.liferay.headless.commerce.admin.order.internal.resource.v1_0.OrderRuleAccountGroupResourceImpl;
import com.liferay.headless.commerce.admin.order.internal.resource.v1_0.OrderRuleAccountResourceImpl;
import com.liferay.headless.commerce.admin.order.internal.resource.v1_0.OrderRuleChannelResourceImpl;
import com.liferay.headless.commerce.admin.order.internal.resource.v1_0.OrderRuleOrderTypeResourceImpl;
import com.liferay.headless.commerce.admin.order.internal.resource.v1_0.OrderRuleResourceImpl;
import com.liferay.headless.commerce.admin.order.internal.resource.v1_0.OrderTypeChannelResourceImpl;
import com.liferay.headless.commerce.admin.order.internal.resource.v1_0.OrderTypeResourceImpl;
import com.liferay.headless.commerce.admin.order.internal.resource.v1_0.ShippingAddressResourceImpl;
import com.liferay.headless.commerce.admin.order.internal.resource.v1_0.TermOrderTypeResourceImpl;
import com.liferay.headless.commerce.admin.order.internal.resource.v1_0.TermResourceImpl;
import com.liferay.headless.commerce.admin.order.resource.v1_0.AccountResource;
import com.liferay.headless.commerce.admin.order.resource.v1_0.BillingAddressResource;
import com.liferay.headless.commerce.admin.order.resource.v1_0.ChannelResource;
import com.liferay.headless.commerce.admin.order.resource.v1_0.OrderAccountGroupResource;
import com.liferay.headless.commerce.admin.order.resource.v1_0.OrderItemResource;
import com.liferay.headless.commerce.admin.order.resource.v1_0.OrderNoteResource;
import com.liferay.headless.commerce.admin.order.resource.v1_0.OrderResource;
import com.liferay.headless.commerce.admin.order.resource.v1_0.OrderRuleAccountGroupResource;
import com.liferay.headless.commerce.admin.order.resource.v1_0.OrderRuleAccountResource;
import com.liferay.headless.commerce.admin.order.resource.v1_0.OrderRuleChannelResource;
import com.liferay.headless.commerce.admin.order.resource.v1_0.OrderRuleOrderTypeResource;
import com.liferay.headless.commerce.admin.order.resource.v1_0.OrderRuleResource;
import com.liferay.headless.commerce.admin.order.resource.v1_0.OrderTypeChannelResource;
import com.liferay.headless.commerce.admin.order.resource.v1_0.OrderTypeResource;
import com.liferay.headless.commerce.admin.order.resource.v1_0.ShippingAddressResource;
import com.liferay.headless.commerce.admin.order.resource.v1_0.TermOrderTypeResource;
import com.liferay.headless.commerce.admin.order.resource.v1_0.TermResource;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Generated;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setBillingAddressResourceComponentServiceObjects(
			_billingAddressResourceComponentServiceObjects);
		Mutation.setOrderResourceComponentServiceObjects(
			_orderResourceComponentServiceObjects);
		Mutation.setOrderItemResourceComponentServiceObjects(
			_orderItemResourceComponentServiceObjects);
		Mutation.setOrderNoteResourceComponentServiceObjects(
			_orderNoteResourceComponentServiceObjects);
		Mutation.setOrderRuleResourceComponentServiceObjects(
			_orderRuleResourceComponentServiceObjects);
		Mutation.setOrderRuleAccountResourceComponentServiceObjects(
			_orderRuleAccountResourceComponentServiceObjects);
		Mutation.setOrderRuleAccountGroupResourceComponentServiceObjects(
			_orderRuleAccountGroupResourceComponentServiceObjects);
		Mutation.setOrderRuleChannelResourceComponentServiceObjects(
			_orderRuleChannelResourceComponentServiceObjects);
		Mutation.setOrderRuleOrderTypeResourceComponentServiceObjects(
			_orderRuleOrderTypeResourceComponentServiceObjects);
		Mutation.setOrderTypeResourceComponentServiceObjects(
			_orderTypeResourceComponentServiceObjects);
		Mutation.setOrderTypeChannelResourceComponentServiceObjects(
			_orderTypeChannelResourceComponentServiceObjects);
		Mutation.setShippingAddressResourceComponentServiceObjects(
			_shippingAddressResourceComponentServiceObjects);
		Mutation.setTermResourceComponentServiceObjects(
			_termResourceComponentServiceObjects);
		Mutation.setTermOrderTypeResourceComponentServiceObjects(
			_termOrderTypeResourceComponentServiceObjects);

		Query.setAccountResourceComponentServiceObjects(
			_accountResourceComponentServiceObjects);
		Query.setBillingAddressResourceComponentServiceObjects(
			_billingAddressResourceComponentServiceObjects);
		Query.setChannelResourceComponentServiceObjects(
			_channelResourceComponentServiceObjects);
		Query.setOrderResourceComponentServiceObjects(
			_orderResourceComponentServiceObjects);
		Query.setOrderAccountGroupResourceComponentServiceObjects(
			_orderAccountGroupResourceComponentServiceObjects);
		Query.setOrderItemResourceComponentServiceObjects(
			_orderItemResourceComponentServiceObjects);
		Query.setOrderNoteResourceComponentServiceObjects(
			_orderNoteResourceComponentServiceObjects);
		Query.setOrderRuleResourceComponentServiceObjects(
			_orderRuleResourceComponentServiceObjects);
		Query.setOrderRuleAccountResourceComponentServiceObjects(
			_orderRuleAccountResourceComponentServiceObjects);
		Query.setOrderRuleAccountGroupResourceComponentServiceObjects(
			_orderRuleAccountGroupResourceComponentServiceObjects);
		Query.setOrderRuleChannelResourceComponentServiceObjects(
			_orderRuleChannelResourceComponentServiceObjects);
		Query.setOrderRuleOrderTypeResourceComponentServiceObjects(
			_orderRuleOrderTypeResourceComponentServiceObjects);
		Query.setOrderTypeResourceComponentServiceObjects(
			_orderTypeResourceComponentServiceObjects);
		Query.setOrderTypeChannelResourceComponentServiceObjects(
			_orderTypeChannelResourceComponentServiceObjects);
		Query.setShippingAddressResourceComponentServiceObjects(
			_shippingAddressResourceComponentServiceObjects);
		Query.setTermResourceComponentServiceObjects(
			_termResourceComponentServiceObjects);
		Query.setTermOrderTypeResourceComponentServiceObjects(
			_termOrderTypeResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Headless.Commerce.Admin.Order";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/headless-commerce-admin-order-graphql/v1_0";
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
						"mutation#patchOrderByExternalReferenceCodeBillingAddress",
						new ObjectValuePair<>(
							BillingAddressResourceImpl.class,
							"patchOrderByExternalReferenceCodeBillingAddress"));
					put(
						"mutation#patchOrderIdBillingAddress",
						new ObjectValuePair<>(
							BillingAddressResourceImpl.class,
							"patchOrderIdBillingAddress"));
					put(
						"mutation#createOrdersPageExportBatch",
						new ObjectValuePair<>(
							OrderResourceImpl.class,
							"postOrdersPageExportBatch"));
					put(
						"mutation#createOrder",
						new ObjectValuePair<>(
							OrderResourceImpl.class, "postOrder"));
					put(
						"mutation#createOrderBatch",
						new ObjectValuePair<>(
							OrderResourceImpl.class, "postOrderBatch"));
					put(
						"mutation#deleteOrderByExternalReferenceCode",
						new ObjectValuePair<>(
							OrderResourceImpl.class,
							"deleteOrderByExternalReferenceCode"));
					put(
						"mutation#patchOrderByExternalReferenceCode",
						new ObjectValuePair<>(
							OrderResourceImpl.class,
							"patchOrderByExternalReferenceCode"));
					put(
						"mutation#updateOrderByExternalReferenceCode",
						new ObjectValuePair<>(
							OrderResourceImpl.class,
							"putOrderByExternalReferenceCode"));
					put(
						"mutation#deleteOrder",
						new ObjectValuePair<>(
							OrderResourceImpl.class, "deleteOrder"));
					put(
						"mutation#deleteOrderBatch",
						new ObjectValuePair<>(
							OrderResourceImpl.class, "deleteOrderBatch"));
					put(
						"mutation#patchOrder",
						new ObjectValuePair<>(
							OrderResourceImpl.class, "patchOrder"));
					put(
						"mutation#createOrderItemsPageExportBatch",
						new ObjectValuePair<>(
							OrderItemResourceImpl.class,
							"postOrderItemsPageExportBatch"));
					put(
						"mutation#deleteOrderItemByExternalReferenceCode",
						new ObjectValuePair<>(
							OrderItemResourceImpl.class,
							"deleteOrderItemByExternalReferenceCode"));
					put(
						"mutation#patchOrderItemByExternalReferenceCode",
						new ObjectValuePair<>(
							OrderItemResourceImpl.class,
							"patchOrderItemByExternalReferenceCode"));
					put(
						"mutation#updateOrderItemByExternalReferenceCode",
						new ObjectValuePair<>(
							OrderItemResourceImpl.class,
							"putOrderItemByExternalReferenceCode"));
					put(
						"mutation#deleteOrderItem",
						new ObjectValuePair<>(
							OrderItemResourceImpl.class, "deleteOrderItem"));
					put(
						"mutation#deleteOrderItemBatch",
						new ObjectValuePair<>(
							OrderItemResourceImpl.class,
							"deleteOrderItemBatch"));
					put(
						"mutation#patchOrderItem",
						new ObjectValuePair<>(
							OrderItemResourceImpl.class, "patchOrderItem"));
					put(
						"mutation#updateOrderItem",
						new ObjectValuePair<>(
							OrderItemResourceImpl.class, "putOrderItem"));
					put(
						"mutation#updateOrderItemBatch",
						new ObjectValuePair<>(
							OrderItemResourceImpl.class, "putOrderItemBatch"));
					put(
						"mutation#createOrderByExternalReferenceCodeOrderItem",
						new ObjectValuePair<>(
							OrderItemResourceImpl.class,
							"postOrderByExternalReferenceCodeOrderItem"));
					put(
						"mutation#createOrderIdOrderItem",
						new ObjectValuePair<>(
							OrderItemResourceImpl.class,
							"postOrderIdOrderItem"));
					put(
						"mutation#createOrderIdOrderItemBatch",
						new ObjectValuePair<>(
							OrderItemResourceImpl.class,
							"postOrderIdOrderItemBatch"));
					put(
						"mutation#deleteOrderNoteByExternalReferenceCode",
						new ObjectValuePair<>(
							OrderNoteResourceImpl.class,
							"deleteOrderNoteByExternalReferenceCode"));
					put(
						"mutation#patchOrderNoteByExternalReferenceCode",
						new ObjectValuePair<>(
							OrderNoteResourceImpl.class,
							"patchOrderNoteByExternalReferenceCode"));
					put(
						"mutation#deleteOrderNote",
						new ObjectValuePair<>(
							OrderNoteResourceImpl.class, "deleteOrderNote"));
					put(
						"mutation#deleteOrderNoteBatch",
						new ObjectValuePair<>(
							OrderNoteResourceImpl.class,
							"deleteOrderNoteBatch"));
					put(
						"mutation#patchOrderNote",
						new ObjectValuePair<>(
							OrderNoteResourceImpl.class, "patchOrderNote"));
					put(
						"mutation#createOrderByExternalReferenceCodeOrderNote",
						new ObjectValuePair<>(
							OrderNoteResourceImpl.class,
							"postOrderByExternalReferenceCodeOrderNote"));
					put(
						"mutation#createOrderIdOrderNote",
						new ObjectValuePair<>(
							OrderNoteResourceImpl.class,
							"postOrderIdOrderNote"));
					put(
						"mutation#createOrderIdOrderNoteBatch",
						new ObjectValuePair<>(
							OrderNoteResourceImpl.class,
							"postOrderIdOrderNoteBatch"));
					put(
						"mutation#createOrderRulesPageExportBatch",
						new ObjectValuePair<>(
							OrderRuleResourceImpl.class,
							"postOrderRulesPageExportBatch"));
					put(
						"mutation#createOrderRule",
						new ObjectValuePair<>(
							OrderRuleResourceImpl.class, "postOrderRule"));
					put(
						"mutation#createOrderRuleBatch",
						new ObjectValuePair<>(
							OrderRuleResourceImpl.class, "postOrderRuleBatch"));
					put(
						"mutation#deleteOrderRuleByExternalReferenceCode",
						new ObjectValuePair<>(
							OrderRuleResourceImpl.class,
							"deleteOrderRuleByExternalReferenceCode"));
					put(
						"mutation#patchOrderRuleByExternalReferenceCode",
						new ObjectValuePair<>(
							OrderRuleResourceImpl.class,
							"patchOrderRuleByExternalReferenceCode"));
					put(
						"mutation#updateOrderRuleByExternalReferenceCode",
						new ObjectValuePair<>(
							OrderRuleResourceImpl.class,
							"putOrderRuleByExternalReferenceCode"));
					put(
						"mutation#deleteOrderRule",
						new ObjectValuePair<>(
							OrderRuleResourceImpl.class, "deleteOrderRule"));
					put(
						"mutation#deleteOrderRuleBatch",
						new ObjectValuePair<>(
							OrderRuleResourceImpl.class,
							"deleteOrderRuleBatch"));
					put(
						"mutation#patchOrderRule",
						new ObjectValuePair<>(
							OrderRuleResourceImpl.class, "patchOrderRule"));
					put(
						"mutation#deleteOrderRuleAccount",
						new ObjectValuePair<>(
							OrderRuleAccountResourceImpl.class,
							"deleteOrderRuleAccount"));
					put(
						"mutation#deleteOrderRuleAccountBatch",
						new ObjectValuePair<>(
							OrderRuleAccountResourceImpl.class,
							"deleteOrderRuleAccountBatch"));
					put(
						"mutation#createOrderRuleByExternalReferenceCodeOrderRuleAccount",
						new ObjectValuePair<>(
							OrderRuleAccountResourceImpl.class,
							"postOrderRuleByExternalReferenceCodeOrderRuleAccount"));
					put(
						"mutation#createOrderRuleIdOrderRuleAccount",
						new ObjectValuePair<>(
							OrderRuleAccountResourceImpl.class,
							"postOrderRuleIdOrderRuleAccount"));
					put(
						"mutation#createOrderRuleIdOrderRuleAccountBatch",
						new ObjectValuePair<>(
							OrderRuleAccountResourceImpl.class,
							"postOrderRuleIdOrderRuleAccountBatch"));
					put(
						"mutation#deleteOrderRuleAccountGroup",
						new ObjectValuePair<>(
							OrderRuleAccountGroupResourceImpl.class,
							"deleteOrderRuleAccountGroup"));
					put(
						"mutation#deleteOrderRuleAccountGroupBatch",
						new ObjectValuePair<>(
							OrderRuleAccountGroupResourceImpl.class,
							"deleteOrderRuleAccountGroupBatch"));
					put(
						"mutation#createOrderRuleByExternalReferenceCodeOrderRuleAccountGroup",
						new ObjectValuePair<>(
							OrderRuleAccountGroupResourceImpl.class,
							"postOrderRuleByExternalReferenceCodeOrderRuleAccountGroup"));
					put(
						"mutation#createOrderRuleIdOrderRuleAccountGroup",
						new ObjectValuePair<>(
							OrderRuleAccountGroupResourceImpl.class,
							"postOrderRuleIdOrderRuleAccountGroup"));
					put(
						"mutation#createOrderRuleIdOrderRuleAccountGroupBatch",
						new ObjectValuePair<>(
							OrderRuleAccountGroupResourceImpl.class,
							"postOrderRuleIdOrderRuleAccountGroupBatch"));
					put(
						"mutation#deleteOrderRuleChannel",
						new ObjectValuePair<>(
							OrderRuleChannelResourceImpl.class,
							"deleteOrderRuleChannel"));
					put(
						"mutation#deleteOrderRuleChannelBatch",
						new ObjectValuePair<>(
							OrderRuleChannelResourceImpl.class,
							"deleteOrderRuleChannelBatch"));
					put(
						"mutation#createOrderRuleByExternalReferenceCodeOrderRuleChannel",
						new ObjectValuePair<>(
							OrderRuleChannelResourceImpl.class,
							"postOrderRuleByExternalReferenceCodeOrderRuleChannel"));
					put(
						"mutation#createOrderRuleIdOrderRuleChannel",
						new ObjectValuePair<>(
							OrderRuleChannelResourceImpl.class,
							"postOrderRuleIdOrderRuleChannel"));
					put(
						"mutation#createOrderRuleIdOrderRuleChannelBatch",
						new ObjectValuePair<>(
							OrderRuleChannelResourceImpl.class,
							"postOrderRuleIdOrderRuleChannelBatch"));
					put(
						"mutation#deleteOrderRuleOrderType",
						new ObjectValuePair<>(
							OrderRuleOrderTypeResourceImpl.class,
							"deleteOrderRuleOrderType"));
					put(
						"mutation#deleteOrderRuleOrderTypeBatch",
						new ObjectValuePair<>(
							OrderRuleOrderTypeResourceImpl.class,
							"deleteOrderRuleOrderTypeBatch"));
					put(
						"mutation#createOrderRuleByExternalReferenceCodeOrderRuleOrderType",
						new ObjectValuePair<>(
							OrderRuleOrderTypeResourceImpl.class,
							"postOrderRuleByExternalReferenceCodeOrderRuleOrderType"));
					put(
						"mutation#createOrderRuleIdOrderRuleOrderType",
						new ObjectValuePair<>(
							OrderRuleOrderTypeResourceImpl.class,
							"postOrderRuleIdOrderRuleOrderType"));
					put(
						"mutation#createOrderRuleIdOrderRuleOrderTypeBatch",
						new ObjectValuePair<>(
							OrderRuleOrderTypeResourceImpl.class,
							"postOrderRuleIdOrderRuleOrderTypeBatch"));
					put(
						"mutation#createOrderTypesPageExportBatch",
						new ObjectValuePair<>(
							OrderTypeResourceImpl.class,
							"postOrderTypesPageExportBatch"));
					put(
						"mutation#createOrderType",
						new ObjectValuePair<>(
							OrderTypeResourceImpl.class, "postOrderType"));
					put(
						"mutation#createOrderTypeBatch",
						new ObjectValuePair<>(
							OrderTypeResourceImpl.class, "postOrderTypeBatch"));
					put(
						"mutation#deleteOrderTypeByExternalReferenceCode",
						new ObjectValuePair<>(
							OrderTypeResourceImpl.class,
							"deleteOrderTypeByExternalReferenceCode"));
					put(
						"mutation#patchOrderTypeByExternalReferenceCode",
						new ObjectValuePair<>(
							OrderTypeResourceImpl.class,
							"patchOrderTypeByExternalReferenceCode"));
					put(
						"mutation#updateOrderTypeByExternalReferenceCode",
						new ObjectValuePair<>(
							OrderTypeResourceImpl.class,
							"putOrderTypeByExternalReferenceCode"));
					put(
						"mutation#deleteOrderType",
						new ObjectValuePair<>(
							OrderTypeResourceImpl.class, "deleteOrderType"));
					put(
						"mutation#deleteOrderTypeBatch",
						new ObjectValuePair<>(
							OrderTypeResourceImpl.class,
							"deleteOrderTypeBatch"));
					put(
						"mutation#patchOrderType",
						new ObjectValuePair<>(
							OrderTypeResourceImpl.class, "patchOrderType"));
					put(
						"mutation#deleteOrderTypeChannel",
						new ObjectValuePair<>(
							OrderTypeChannelResourceImpl.class,
							"deleteOrderTypeChannel"));
					put(
						"mutation#deleteOrderTypeChannelBatch",
						new ObjectValuePair<>(
							OrderTypeChannelResourceImpl.class,
							"deleteOrderTypeChannelBatch"));
					put(
						"mutation#createOrderTypeByExternalReferenceCodeOrderTypeChannel",
						new ObjectValuePair<>(
							OrderTypeChannelResourceImpl.class,
							"postOrderTypeByExternalReferenceCodeOrderTypeChannel"));
					put(
						"mutation#createOrderTypeIdOrderTypeChannel",
						new ObjectValuePair<>(
							OrderTypeChannelResourceImpl.class,
							"postOrderTypeIdOrderTypeChannel"));
					put(
						"mutation#createOrderTypeIdOrderTypeChannelBatch",
						new ObjectValuePair<>(
							OrderTypeChannelResourceImpl.class,
							"postOrderTypeIdOrderTypeChannelBatch"));
					put(
						"mutation#patchOrderByExternalReferenceCodeShippingAddress",
						new ObjectValuePair<>(
							ShippingAddressResourceImpl.class,
							"patchOrderByExternalReferenceCodeShippingAddress"));
					put(
						"mutation#patchOrderIdShippingAddress",
						new ObjectValuePair<>(
							ShippingAddressResourceImpl.class,
							"patchOrderIdShippingAddress"));
					put(
						"mutation#createTermsPageExportBatch",
						new ObjectValuePair<>(
							TermResourceImpl.class,
							"postTermsPageExportBatch"));
					put(
						"mutation#createTerm",
						new ObjectValuePair<>(
							TermResourceImpl.class, "postTerm"));
					put(
						"mutation#createTermBatch",
						new ObjectValuePair<>(
							TermResourceImpl.class, "postTermBatch"));
					put(
						"mutation#deleteTermByExternalReferenceCode",
						new ObjectValuePair<>(
							TermResourceImpl.class,
							"deleteTermByExternalReferenceCode"));
					put(
						"mutation#patchTermByExternalReferenceCode",
						new ObjectValuePair<>(
							TermResourceImpl.class,
							"patchTermByExternalReferenceCode"));
					put(
						"mutation#updateTermByExternalReferenceCode",
						new ObjectValuePair<>(
							TermResourceImpl.class,
							"putTermByExternalReferenceCode"));
					put(
						"mutation#deleteTerm",
						new ObjectValuePair<>(
							TermResourceImpl.class, "deleteTerm"));
					put(
						"mutation#deleteTermBatch",
						new ObjectValuePair<>(
							TermResourceImpl.class, "deleteTermBatch"));
					put(
						"mutation#patchTerm",
						new ObjectValuePair<>(
							TermResourceImpl.class, "patchTerm"));
					put(
						"mutation#deleteTermOrderType",
						new ObjectValuePair<>(
							TermOrderTypeResourceImpl.class,
							"deleteTermOrderType"));
					put(
						"mutation#deleteTermOrderTypeBatch",
						new ObjectValuePair<>(
							TermOrderTypeResourceImpl.class,
							"deleteTermOrderTypeBatch"));
					put(
						"mutation#createTermByExternalReferenceCodeTermOrderType",
						new ObjectValuePair<>(
							TermOrderTypeResourceImpl.class,
							"postTermByExternalReferenceCodeTermOrderType"));
					put(
						"mutation#createTermIdTermOrderType",
						new ObjectValuePair<>(
							TermOrderTypeResourceImpl.class,
							"postTermIdTermOrderType"));
					put(
						"mutation#createTermIdTermOrderTypeBatch",
						new ObjectValuePair<>(
							TermOrderTypeResourceImpl.class,
							"postTermIdTermOrderTypeBatch"));

					put(
						"query#orderRuleAccountAccount",
						new ObjectValuePair<>(
							AccountResourceImpl.class,
							"getOrderRuleAccountAccount"));
					put(
						"query#orderByExternalReferenceCodeAccount",
						new ObjectValuePair<>(
							AccountResourceImpl.class,
							"getOrderByExternalReferenceCodeAccount"));
					put(
						"query#orderIdAccount",
						new ObjectValuePair<>(
							AccountResourceImpl.class, "getOrderIdAccount"));
					put(
						"query#orderByExternalReferenceCodeBillingAddress",
						new ObjectValuePair<>(
							BillingAddressResourceImpl.class,
							"getOrderByExternalReferenceCodeBillingAddress"));
					put(
						"query#orderIdBillingAddress",
						new ObjectValuePair<>(
							BillingAddressResourceImpl.class,
							"getOrderIdBillingAddress"));
					put(
						"query#orderRuleChannelChannel",
						new ObjectValuePair<>(
							ChannelResourceImpl.class,
							"getOrderRuleChannelChannel"));
					put(
						"query#orderTypeChannelChannel",
						new ObjectValuePair<>(
							ChannelResourceImpl.class,
							"getOrderTypeChannelChannel"));
					put(
						"query#orderByExternalReferenceCodeChannel",
						new ObjectValuePair<>(
							ChannelResourceImpl.class,
							"getOrderByExternalReferenceCodeChannel"));
					put(
						"query#orderIdChannel",
						new ObjectValuePair<>(
							ChannelResourceImpl.class, "getOrderIdChannel"));
					put(
						"query#orders",
						new ObjectValuePair<>(
							OrderResourceImpl.class, "getOrdersPage"));
					put(
						"query#orderByExternalReferenceCode",
						new ObjectValuePair<>(
							OrderResourceImpl.class,
							"getOrderByExternalReferenceCode"));
					put(
						"query#order",
						new ObjectValuePair<>(
							OrderResourceImpl.class, "getOrder"));
					put(
						"query#orderRuleAccountGroupAccountGroup",
						new ObjectValuePair<>(
							OrderAccountGroupResourceImpl.class,
							"getOrderRuleAccountGroupAccountGroup"));
					put(
						"query#orderItems",
						new ObjectValuePair<>(
							OrderItemResourceImpl.class, "getOrderItemsPage"));
					put(
						"query#orderItemByExternalReferenceCode",
						new ObjectValuePair<>(
							OrderItemResourceImpl.class,
							"getOrderItemByExternalReferenceCode"));
					put(
						"query#orderItem",
						new ObjectValuePair<>(
							OrderItemResourceImpl.class, "getOrderItem"));
					put(
						"query#orderByExternalReferenceCodeOrderItems",
						new ObjectValuePair<>(
							OrderItemResourceImpl.class,
							"getOrderByExternalReferenceCodeOrderItemsPage"));
					put(
						"query#orderIdOrderItems",
						new ObjectValuePair<>(
							OrderItemResourceImpl.class,
							"getOrderIdOrderItemsPage"));
					put(
						"query#orderNoteByExternalReferenceCode",
						new ObjectValuePair<>(
							OrderNoteResourceImpl.class,
							"getOrderNoteByExternalReferenceCode"));
					put(
						"query#orderNote",
						new ObjectValuePair<>(
							OrderNoteResourceImpl.class, "getOrderNote"));
					put(
						"query#orderByExternalReferenceCodeOrderNotes",
						new ObjectValuePair<>(
							OrderNoteResourceImpl.class,
							"getOrderByExternalReferenceCodeOrderNotesPage"));
					put(
						"query#orderIdOrderNotes",
						new ObjectValuePair<>(
							OrderNoteResourceImpl.class,
							"getOrderIdOrderNotesPage"));
					put(
						"query#orderRules",
						new ObjectValuePair<>(
							OrderRuleResourceImpl.class, "getOrderRulesPage"));
					put(
						"query#orderRuleByExternalReferenceCode",
						new ObjectValuePair<>(
							OrderRuleResourceImpl.class,
							"getOrderRuleByExternalReferenceCode"));
					put(
						"query#orderRule",
						new ObjectValuePair<>(
							OrderRuleResourceImpl.class, "getOrderRule"));
					put(
						"query#orderRuleByExternalReferenceCodeOrderRuleAccounts",
						new ObjectValuePair<>(
							OrderRuleAccountResourceImpl.class,
							"getOrderRuleByExternalReferenceCodeOrderRuleAccountsPage"));
					put(
						"query#orderRuleIdOrderRuleAccounts",
						new ObjectValuePair<>(
							OrderRuleAccountResourceImpl.class,
							"getOrderRuleIdOrderRuleAccountsPage"));
					put(
						"query#orderRuleByExternalReferenceCodeOrderRuleAccountGroups",
						new ObjectValuePair<>(
							OrderRuleAccountGroupResourceImpl.class,
							"getOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage"));
					put(
						"query#orderRuleIdOrderRuleAccountGroups",
						new ObjectValuePair<>(
							OrderRuleAccountGroupResourceImpl.class,
							"getOrderRuleIdOrderRuleAccountGroupsPage"));
					put(
						"query#orderRuleByExternalReferenceCodeOrderRuleChannels",
						new ObjectValuePair<>(
							OrderRuleChannelResourceImpl.class,
							"getOrderRuleByExternalReferenceCodeOrderRuleChannelsPage"));
					put(
						"query#orderRuleIdOrderRuleChannels",
						new ObjectValuePair<>(
							OrderRuleChannelResourceImpl.class,
							"getOrderRuleIdOrderRuleChannelsPage"));
					put(
						"query#orderRuleByExternalReferenceCodeOrderRuleOrderTypes",
						new ObjectValuePair<>(
							OrderRuleOrderTypeResourceImpl.class,
							"getOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage"));
					put(
						"query#orderRuleIdOrderRuleOrderTypes",
						new ObjectValuePair<>(
							OrderRuleOrderTypeResourceImpl.class,
							"getOrderRuleIdOrderRuleOrderTypesPage"));
					put(
						"query#orderRuleOrderTypeOrderType",
						new ObjectValuePair<>(
							OrderTypeResourceImpl.class,
							"getOrderRuleOrderTypeOrderType"));
					put(
						"query#orderTypes",
						new ObjectValuePair<>(
							OrderTypeResourceImpl.class, "getOrderTypesPage"));
					put(
						"query#orderTypeByExternalReferenceCode",
						new ObjectValuePair<>(
							OrderTypeResourceImpl.class,
							"getOrderTypeByExternalReferenceCode"));
					put(
						"query#orderType",
						new ObjectValuePair<>(
							OrderTypeResourceImpl.class, "getOrderType"));
					put(
						"query#termOrderTypeOrderType",
						new ObjectValuePair<>(
							OrderTypeResourceImpl.class,
							"getTermOrderTypeOrderType"));
					put(
						"query#orderTypeByExternalReferenceCodeOrderTypeChannels",
						new ObjectValuePair<>(
							OrderTypeChannelResourceImpl.class,
							"getOrderTypeByExternalReferenceCodeOrderTypeChannelsPage"));
					put(
						"query#orderTypeIdOrderTypeChannels",
						new ObjectValuePair<>(
							OrderTypeChannelResourceImpl.class,
							"getOrderTypeIdOrderTypeChannelsPage"));
					put(
						"query#orderItemShippingAddress",
						new ObjectValuePair<>(
							ShippingAddressResourceImpl.class,
							"getOrderItemShippingAddress"));
					put(
						"query#orderByExternalReferenceCodeShippingAddress",
						new ObjectValuePair<>(
							ShippingAddressResourceImpl.class,
							"getOrderByExternalReferenceCodeShippingAddress"));
					put(
						"query#orderIdShippingAddress",
						new ObjectValuePair<>(
							ShippingAddressResourceImpl.class,
							"getOrderIdShippingAddress"));
					put(
						"query#terms",
						new ObjectValuePair<>(
							TermResourceImpl.class, "getTermsPage"));
					put(
						"query#termByExternalReferenceCode",
						new ObjectValuePair<>(
							TermResourceImpl.class,
							"getTermByExternalReferenceCode"));
					put(
						"query#term",
						new ObjectValuePair<>(
							TermResourceImpl.class, "getTerm"));
					put(
						"query#termByExternalReferenceCodeTermOrderTypes",
						new ObjectValuePair<>(
							TermOrderTypeResourceImpl.class,
							"getTermByExternalReferenceCodeTermOrderTypesPage"));
					put(
						"query#termIdTermOrderTypes",
						new ObjectValuePair<>(
							TermOrderTypeResourceImpl.class,
							"getTermIdTermOrderTypesPage"));

					put(
						"query#Order.itemByExternalReferenceCode",
						new ObjectValuePair<>(
							OrderItemResourceImpl.class,
							"getOrderItemByExternalReferenceCode"));
					put(
						"query#Order.byExternalReferenceCodeChannel",
						new ObjectValuePair<>(
							ChannelResourceImpl.class,
							"getOrderByExternalReferenceCodeChannel"));
					put(
						"query#Order.ruleByExternalReferenceCode",
						new ObjectValuePair<>(
							OrderRuleResourceImpl.class,
							"getOrderRuleByExternalReferenceCode"));
					put(
						"query#Order.byExternalReferenceCodeAccount",
						new ObjectValuePair<>(
							AccountResourceImpl.class,
							"getOrderByExternalReferenceCodeAccount"));
					put(
						"query#OrderItem.orderByExternalReferenceCode",
						new ObjectValuePair<>(
							OrderResourceImpl.class,
							"getOrderByExternalReferenceCode"));
					put(
						"query#Order.ruleByExternalReferenceCodeOrderRuleAccountGroups",
						new ObjectValuePair<>(
							OrderRuleAccountGroupResourceImpl.class,
							"getOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage"));
					put(
						"query#Order.noteByExternalReferenceCode",
						new ObjectValuePair<>(
							OrderNoteResourceImpl.class,
							"getOrderNoteByExternalReferenceCode"));
					put(
						"query#Order.byExternalReferenceCodeBillingAddress",
						new ObjectValuePair<>(
							BillingAddressResourceImpl.class,
							"getOrderByExternalReferenceCodeBillingAddress"));
					put(
						"query#Order.typeByExternalReferenceCode",
						new ObjectValuePair<>(
							OrderTypeResourceImpl.class,
							"getOrderTypeByExternalReferenceCode"));
					put(
						"query#Order.ruleByExternalReferenceCodeOrderRuleChannels",
						new ObjectValuePair<>(
							OrderRuleChannelResourceImpl.class,
							"getOrderRuleByExternalReferenceCodeOrderRuleChannelsPage"));
					put(
						"query#Order.byExternalReferenceCodeOrderNotes",
						new ObjectValuePair<>(
							OrderNoteResourceImpl.class,
							"getOrderByExternalReferenceCodeOrderNotesPage"));
					put(
						"query#Order.byExternalReferenceCodeShippingAddress",
						new ObjectValuePair<>(
							ShippingAddressResourceImpl.class,
							"getOrderByExternalReferenceCodeShippingAddress"));
					put(
						"query#Order.ruleByExternalReferenceCodeOrderRuleAccounts",
						new ObjectValuePair<>(
							OrderRuleAccountResourceImpl.class,
							"getOrderRuleByExternalReferenceCodeOrderRuleAccountsPage"));
					put(
						"query#Order.termByExternalReferenceCode",
						new ObjectValuePair<>(
							TermResourceImpl.class,
							"getTermByExternalReferenceCode"));
					put(
						"query#Order.termByExternalReferenceCodeTermOrderTypes",
						new ObjectValuePair<>(
							TermOrderTypeResourceImpl.class,
							"getTermByExternalReferenceCodeTermOrderTypesPage"));
					put(
						"query#Order.typeByExternalReferenceCodeOrderTypeChannels",
						new ObjectValuePair<>(
							OrderTypeChannelResourceImpl.class,
							"getOrderTypeByExternalReferenceCodeOrderTypeChannelsPage"));
					put(
						"query#Order.ruleByExternalReferenceCodeOrderRuleOrderTypes",
						new ObjectValuePair<>(
							OrderRuleOrderTypeResourceImpl.class,
							"getOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage"));
					put(
						"query#Order.byExternalReferenceCodeOrderItems",
						new ObjectValuePair<>(
							OrderItemResourceImpl.class,
							"getOrderByExternalReferenceCodeOrderItemsPage"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<BillingAddressResource>
		_billingAddressResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<OrderResource>
		_orderResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<OrderItemResource>
		_orderItemResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<OrderNoteResource>
		_orderNoteResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<OrderRuleResource>
		_orderRuleResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<OrderRuleAccountResource>
		_orderRuleAccountResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<OrderRuleAccountGroupResource>
		_orderRuleAccountGroupResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<OrderRuleChannelResource>
		_orderRuleChannelResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<OrderRuleOrderTypeResource>
		_orderRuleOrderTypeResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<OrderTypeResource>
		_orderTypeResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<OrderTypeChannelResource>
		_orderTypeChannelResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ShippingAddressResource>
		_shippingAddressResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TermResource>
		_termResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TermOrderTypeResource>
		_termOrderTypeResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AccountResource>
		_accountResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ChannelResource>
		_channelResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<OrderAccountGroupResource>
		_orderAccountGroupResourceComponentServiceObjects;

}