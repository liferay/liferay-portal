/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.impl;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderPayment;
import com.liferay.commerce.service.base.CommerceOrderPaymentLocalServiceBaseImpl;
import com.liferay.commerce.service.persistence.CommerceOrderPersistence;
import com.liferay.commerce.util.comparator.CommerceOrderPaymentCreateDateComparator;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.model.CommerceOrderPayment",
	service = AopService.class
)
public class CommerceOrderPaymentLocalServiceImpl
	extends CommerceOrderPaymentLocalServiceBaseImpl {

	@Override
	public CommerceOrderPayment addCommerceOrderPayment(
			long commerceOrderId, int status, String result)
		throws PortalException {

		CommerceOrder commerceOrder =
			_commerceOrderPersistence.findByPrimaryKey(commerceOrderId);

		User user = _userLocalService.getUser(commerceOrder.getUserId());

		return _getCommerceOrderPayment(status, result, commerceOrder, user);
	}

	@Override
	public void deleteCommerceOrderPayments(long commerceOrderId) {
		commerceOrderPaymentPersistence.removeByCommerceOrderId(
			commerceOrderId);
	}

	@Override
	public CommerceOrderPayment fetchLatestCommerceOrderPayment(
			long commerceOrderId)
		throws PortalException {

		return commerceOrderPaymentPersistence.fetchByCommerceOrderId_First(
			commerceOrderId,
			CommerceOrderPaymentCreateDateComparator.getInstance(false));
	}

	@Override
	public List<CommerceOrderPayment> getCommerceOrderPayments(
		long commerceOrderId, int start, int end,
		OrderByComparator<CommerceOrderPayment> orderByComparator) {

		return commerceOrderPaymentPersistence.findByCommerceOrderId(
			commerceOrderId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceOrderPaymentsCount(long commerceOrderId) {
		return commerceOrderPaymentPersistence.countByCommerceOrderId(
			commerceOrderId);
	}

	private CommerceOrderPayment _getCommerceOrderPayment(
		int status, String result, CommerceOrder commerceOrder, User user) {

		long commerceOrderPaymentId = counterLocalService.increment();

		CommerceOrderPayment commerceOrderPayment =
			commerceOrderPaymentPersistence.create(commerceOrderPaymentId);

		commerceOrderPayment.setGroupId(commerceOrder.getGroupId());
		commerceOrderPayment.setCompanyId(user.getCompanyId());
		commerceOrderPayment.setUserId(user.getUserId());
		commerceOrderPayment.setUserName(user.getFullName());
		commerceOrderPayment.setCommerceOrderId(
			commerceOrder.getCommerceOrderId());
		commerceOrderPayment.setCommercePaymentMethodKey(
			commerceOrder.getCommercePaymentMethodKey());
		commerceOrderPayment.setContent(result);
		commerceOrderPayment.setStatus(status);

		return commerceOrderPaymentPersistence.update(commerceOrderPayment);
	}

	@Reference
	private CommerceOrderPersistence _commerceOrderPersistence;

	@Reference
	private UserLocalService _userLocalService;

}