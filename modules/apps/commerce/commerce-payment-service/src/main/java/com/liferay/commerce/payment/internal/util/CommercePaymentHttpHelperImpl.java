/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.internal.util;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.payment.util.CommercePaymentHttpHelper;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.encryptor.Encryptor;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.security.Key;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 */
@Component(service = CommercePaymentHttpHelper.class)
public class CommercePaymentHttpHelperImpl
	implements CommercePaymentHttpHelper {

	@Override
	public CommerceOrder getCommerceOrder(HttpServletRequest httpServletRequest)
		throws Exception {

		CommerceOrder commerceOrder = null;

		long groupId = ParamUtil.getLong(httpServletRequest, "groupId");
		String uuid = ParamUtil.getString(httpServletRequest, "uuid");

		String guestToken = ParamUtil.getString(
			httpServletRequest, "guestToken");

		if (Validator.isNotNull(guestToken)) {
			guestToken = guestToken.replaceAll(
				StringPool.SPACE, StringPool.PLUS);

			commerceOrder =
				_commerceOrderLocalService.getCommerceOrderByUuidAndGroupId(
					uuid, groupId);

			Company company = _portal.getCompany(httpServletRequest);

			User guestUser = company.getGuestUser();

			String orderGuestToken = _getGuestToken(
				company, commerceOrder.getCommerceOrderId());

			if (!guestToken.equals(orderGuestToken)) {
				throw new PrincipalException.MustHavePermission(
					guestUser.getUserId(), CommerceOrder.class.getName(),
					commerceOrder.getCommerceOrderId(), ActionKeys.VIEW);
			}

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(guestUser));
		}
		else {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(
					_portal.getUser(httpServletRequest)));

			commerceOrder =
				_commerceOrderService.getCommerceOrderByUuidAndGroupId(
					uuid, groupId);
		}

		return commerceOrder;
	}

	private String _getGuestToken(Company company, long commerceOrderId)
		throws Exception {

		Key key = company.getKeyObj();

		return _encryptor.encrypt(key, String.valueOf(commerceOrderId));
	}

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private Encryptor _encryptor;

	@Reference
	private Portal _portal;

}