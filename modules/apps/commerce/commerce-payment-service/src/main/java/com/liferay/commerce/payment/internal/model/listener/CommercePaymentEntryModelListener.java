/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.internal.model.listener;

import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.constants.CommercePaymentEntryConstants;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.engine.CommerceOrderEngine;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceOrderPaymentLocalService;
import com.liferay.commerce.util.CommerceGroupThreadLocal;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.StringUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(service = ModelListener.class)
public class CommercePaymentEntryModelListener
	extends BaseModelListener<CommercePaymentEntry> {

	@Override
	public void onAfterUpdate(
			CommercePaymentEntry originalCommercePaymentEntry,
			CommercePaymentEntry commercePaymentEntry)
		throws ModelListenerException {

		try {
			if (StringUtil.equals(
					commercePaymentEntry.getClassName(),
					CommerceOrder.class.getName()) &&
				(originalCommercePaymentEntry.getPaymentStatus() !=
					commercePaymentEntry.getPaymentStatus())) {

				CommerceOrder commerceOrder =
					_commerceOrderLocalService.getCommerceOrder(
						commercePaymentEntry.getClassPK());

				CommerceGroupThreadLocal.set(
					_groupLocalService.fetchGroup(commerceOrder.getGroupId()));

				long commerceOrderId = commerceOrder.getCommerceOrderId();

				int paymentStatus = commercePaymentEntry.getPaymentStatus();

				if (commercePaymentEntry.getPaymentStatus() !=
						CommercePaymentEntryConstants.STATUS_CREATED) {

					commerceOrder =
						_commerceOrderLocalService.
							updatePaymentStatusAndTransactionId(
								commerceOrder.getUserId(), commerceOrderId,
								paymentStatus,
								commercePaymentEntry.getTransactionCode());

					_commerceOrderPaymentLocalService.addCommerceOrderPayment(
						commerceOrderId, paymentStatus,
						commercePaymentEntry.getErrorMessages());
				}

				if ((paymentStatus ==
						CommerceOrderPaymentConstants.STATUS_COMPLETED) &&
					(commerceOrder.getOrderStatus() !=
						CommerceOrderConstants.ORDER_STATUS_PENDING)) {

					long userId = commerceOrder.getUserId();

					PermissionChecker permissionChecker =
						PermissionThreadLocal.getPermissionChecker();

					if (permissionChecker != null) {
						userId = permissionChecker.getUserId();
					}

					_commerceOrderEngine.transitionCommerceOrder(
						commerceOrder,
						CommerceOrderConstants.ORDER_STATUS_PENDING, userId,
						true);
				}
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePaymentEntryModelListener.class);

	@Reference
	private CommerceOrderEngine _commerceOrderEngine;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private CommerceOrderPaymentLocalService _commerceOrderPaymentLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

}