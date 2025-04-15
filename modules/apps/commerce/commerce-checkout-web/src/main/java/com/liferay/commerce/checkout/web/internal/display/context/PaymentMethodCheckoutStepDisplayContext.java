/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.checkout.web.internal.display.context;

import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.payment.engine.CommercePaymentEngine;
import com.liferay.commerce.payment.integration.CommercePaymentIntegration;
import com.liferay.commerce.payment.integration.CommercePaymentIntegrationRegistry;
import com.liferay.commerce.payment.method.CommercePaymentMethod;
import com.liferay.commerce.payment.method.CommercePaymentMethodRegistry;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRelQualifier;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelQualifierLocalService;
import com.liferay.commerce.payment.util.comparator.CommercePaymentMethodPriorityComparator;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.ListUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Andrea Di Giorgi
 */
public class PaymentMethodCheckoutStepDisplayContext {

	public PaymentMethodCheckoutStepDisplayContext(
		CommercePaymentEngine commercePaymentEngine,
		CommercePaymentIntegrationRegistry commercePaymentIntegrationRegistry,
		CommercePaymentMethodGroupRelLocalService
			commercePaymentMethodGroupRelLocalService,
		CommercePaymentMethodGroupRelQualifierLocalService
			commercePaymentMethodGroupRelQualifierLocalService,
		CommercePaymentMethodRegistry commercePaymentMethodRegistry,
		HttpServletRequest httpServletRequest) {

		_commercePaymentEngine = commercePaymentEngine;
		_commercePaymentIntegrationRegistry =
			commercePaymentIntegrationRegistry;
		_commercePaymentMethodGroupRelLocalService =
			commercePaymentMethodGroupRelLocalService;
		_commercePaymentMethodGroupRelQualifierLocalService =
			commercePaymentMethodGroupRelQualifierLocalService;
		_commercePaymentMethodRegistry = commercePaymentMethodRegistry;

		_commerceOrder = (CommerceOrder)httpServletRequest.getAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER);
	}

	public CommerceOrder getCommerceOrder() {
		return _commerceOrder;
	}

	public List<CommercePaymentMethodGroupRel>
			getCommercePaymentMethodGroupRels()
		throws PortalException {

		List<CommercePaymentMethodGroupRel> commercePaymentMethodGroupRels =
			new ArrayList<>();

		CommerceOrder commerceOrder = getCommerceOrder();

		CommerceAddress commerceAddress = commerceOrder.getBillingAddress();

		if (commerceAddress == null) {
			commerceAddress = commerceOrder.getShippingAddress();
		}

		if (commerceAddress != null) {
			commercePaymentMethodGroupRels.addAll(
				_commercePaymentMethodGroupRelLocalService.
					getCommercePaymentMethodGroupRels(
						commerceOrder.getGroupId(),
						commerceAddress.getCountryId(), true));
		}
		else {
			commercePaymentMethodGroupRels.addAll(
				_commercePaymentMethodGroupRelLocalService.
					getCommercePaymentMethodGroupRels(
						commerceOrder.getGroupId(), true));
		}

		return _filterCommercePaymentMethodGroupRels(
			commercePaymentMethodGroupRels,
			commerceOrder.getCommerceOrderTypeId(),
			commerceOrder.isSubscriptionOrder());
	}

	private List<CommercePaymentMethodGroupRel>
		_filterCommercePaymentMethodGroupRels(
			List<CommercePaymentMethodGroupRel> commercePaymentMethodGroupRels,
			long commerceOrderTypeId, boolean subscriptionOrder) {

		List<CommercePaymentMethodGroupRel>
			filteredCommercePaymentMethodGroupRels = new LinkedList<>();

		ListUtil.sort(
			commercePaymentMethodGroupRels,
			new CommercePaymentMethodPriorityComparator());

		for (CommercePaymentMethodGroupRel commercePaymentMethodGroupRel :
				commercePaymentMethodGroupRels) {

			List<CommercePaymentMethodGroupRelQualifier>
				commercePaymentMethodGroupRelQualifiers =
					_commercePaymentMethodGroupRelQualifierLocalService.
						getCommercePaymentMethodGroupRelQualifiers(
							CommerceOrderType.class.getName(),
							commercePaymentMethodGroupRel.
								getCommercePaymentMethodGroupRelId());

			if ((commerceOrderTypeId > 0) &&
				ListUtil.isNotEmpty(commercePaymentMethodGroupRelQualifiers) &&
				!ListUtil.exists(
					commercePaymentMethodGroupRelQualifiers,
					commercePaymentMethodGroupRelQualifier -> {
						long classPK =
							commercePaymentMethodGroupRelQualifier.getClassPK();

						return classPK == commerceOrderTypeId;
					})) {

				continue;
			}

			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			CommercePaymentMethod commercePaymentMethod =
				_commercePaymentMethodRegistry.getCommercePaymentMethod(
					commercePaymentMethodGroupRel.getPaymentIntegrationKey());

			CommercePaymentIntegration commercePaymentIntegration =
				_commercePaymentIntegrationRegistry.
					getCommercePaymentIntegration(
						commercePaymentMethodGroupRel.
							getPaymentIntegrationKey());

			if (((commercePaymentMethod == null) &&
				 (commercePaymentIntegration == null)) ||
				!permissionChecker.hasPermission(
					commercePaymentMethodGroupRel.getGroupId(),
					CommercePaymentMethodGroupRel.class.getName(),
					commercePaymentMethodGroupRel.
						getCommercePaymentMethodGroupRelId(),
					ActionKeys.VIEW) ||
				((commercePaymentMethod == null) && subscriptionOrder) ||
				((commercePaymentMethod != null) && subscriptionOrder &&
				 !commercePaymentMethod.isProcessRecurringEnabled()) ||
				((commercePaymentMethod != null) && !subscriptionOrder &&
				 !commercePaymentMethod.isProcessPaymentEnabled())) {

				continue;
			}

			filteredCommercePaymentMethodGroupRels.add(
				commercePaymentMethodGroupRel);
		}

		return filteredCommercePaymentMethodGroupRels;
	}

	private final CommerceOrder _commerceOrder;
	private final CommercePaymentEngine _commercePaymentEngine;
	private final CommercePaymentIntegrationRegistry
		_commercePaymentIntegrationRegistry;
	private final CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;
	private final CommercePaymentMethodGroupRelQualifierLocalService
		_commercePaymentMethodGroupRelQualifierLocalService;
	private final CommercePaymentMethodRegistry _commercePaymentMethodRegistry;

}