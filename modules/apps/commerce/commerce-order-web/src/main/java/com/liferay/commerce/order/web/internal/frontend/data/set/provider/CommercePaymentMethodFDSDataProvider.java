/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.frontend.data.set.provider;

import com.liferay.commerce.frontend.model.ImageField;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.order.web.internal.constants.CommerceOrderFDSNames;
import com.liferay.commerce.order.web.internal.model.PaymentMethod;
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
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceOrderFDSNames.PAYMENT_METHODS,
	service = FDSDataProvider.class
)
public class CommercePaymentMethodFDSDataProvider
	implements FDSDataProvider<PaymentMethod> {

	@Override
	public List<PaymentMethod> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long commerceOrderId = ParamUtil.getLong(
			httpServletRequest, "commerceOrderId");

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrderId);

		List<CommercePaymentMethodGroupRel> commercePaymentMethodGroupRels =
			new ArrayList<>();

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

		return TransformUtil.transform(
			_filterCommercePaymentMethodGroupRels(
				commercePaymentMethodGroupRels,
				commerceOrder.getCommerceOrderTypeId(),
				commerceOrder.isSubscriptionOrder()),
			commercePaymentMethodGroupRel -> new PaymentMethod(
				commercePaymentMethodGroupRel.getDescription(
					themeDisplay.getLocale()),
				commercePaymentMethodGroupRel.getPaymentIntegrationKey(),
				_getThumbnail(commercePaymentMethodGroupRel, themeDisplay),
				commercePaymentMethodGroupRel.getName(
					themeDisplay.getLocale())));
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commerceOrderId = ParamUtil.getLong(
			httpServletRequest, "commerceOrderId");

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrderId);

		return _commercePaymentEngine.getCommercePaymentMethodGroupRelsCount(
			commerceOrder.getGroupId());
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

	private ImageField _getThumbnail(
		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel,
		ThemeDisplay themeDisplay) {

		String imageURL = commercePaymentMethodGroupRel.getImageURL(
			themeDisplay);

		if (Validator.isNull(imageURL)) {
			return null;
		}

		return new ImageField(
			commercePaymentMethodGroupRel.getName(themeDisplay.getLanguageId()),
			"rounded", "sm", imageURL);
	}

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CommercePaymentEngine _commercePaymentEngine;

	@Reference
	private CommercePaymentIntegrationRegistry
		_commercePaymentIntegrationRegistry;

	@Reference
	private CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;

	@Reference
	private CommercePaymentMethodGroupRelQualifierLocalService
		_commercePaymentMethodGroupRelQualifierLocalService;

	@Reference
	private CommercePaymentMethodRegistry _commercePaymentMethodRegistry;

}