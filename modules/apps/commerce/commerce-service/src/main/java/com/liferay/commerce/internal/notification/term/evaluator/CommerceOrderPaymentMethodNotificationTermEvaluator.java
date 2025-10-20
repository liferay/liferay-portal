/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.notification.term.evaluator;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.notification.term.evaluator.NotificationTermEvaluator;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

/**
 * @author Crescenzo Rega
 */
public class CommerceOrderPaymentMethodNotificationTermEvaluator
	implements NotificationTermEvaluator {

	public CommerceOrderPaymentMethodNotificationTermEvaluator(
		CommerceOrderLocalService commerceOrderLocalService,
		CommercePaymentMethodGroupRelLocalService
			commercePaymentMethodGroupRelLocalService,
		ObjectDefinition objectDefinition, UserLocalService userLocalService) {

		_commerceOrderLocalService = commerceOrderLocalService;
		_commercePaymentMethodGroupRelLocalService =
			commercePaymentMethodGroupRelLocalService;
		_objectDefinition = objectDefinition;
		_userLocalService = userLocalService;
	}

	@Override
	public String evaluate(Context context, Object object, String termName)
		throws PortalException {

		if (!(object instanceof Map) ||
			!termName.equals("[%COMMERCEORDER_PAYMENT_METHOD_NAME%]") ||
			!"CommerceOrder".equalsIgnoreCase(
				_objectDefinition.getShortName())) {

			return termName;
		}

		Map<String, Object> termValues = (Map<String, Object>)object;

		return _getPaymentMethodName(termValues);
	}

	private String _getPaymentMethodName(Map<String, Object> termValues)
		throws PortalException {

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(
				GetterUtil.getLong(termValues.get("id")));

		String paymentMethodKey = commerceOrder.getCommercePaymentMethodKey();

		if (Validator.isNull(paymentMethodKey)) {
			return null;
		}

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			_commercePaymentMethodGroupRelLocalService.
				getCommercePaymentMethodGroupRel(
					commerceOrder.getGroupId(), paymentMethodKey);

		User user = _userLocalService.getUser(commerceOrder.getUserId());

		return commercePaymentMethodGroupRel.getName(user.getLocale());
	}

	private final CommerceOrderLocalService _commerceOrderLocalService;
	private final CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;
	private final ObjectDefinition _objectDefinition;
	private final UserLocalService _userLocalService;

}