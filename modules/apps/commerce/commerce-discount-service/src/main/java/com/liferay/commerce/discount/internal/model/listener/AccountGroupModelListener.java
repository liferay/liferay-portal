/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.internal.model.listener;

import com.liferay.account.model.AccountGroup;
import com.liferay.commerce.discount.service.CommerceDiscountCommerceAccountGroupRelLocalService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = ModelListener.class)
public class AccountGroupModelListener extends BaseModelListener<AccountGroup> {

	@Override
	public void onBeforeRemove(AccountGroup accountGroup)
		throws ModelListenerException {

		try {
			_commerceDiscountCommerceAccountGroupRelLocalService.
				deleteCommerceDiscountCommerceAccountGroupRelsBycommerceAccountGroupId(
					accountGroup.getAccountGroupId());
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}
	}

	@Reference
	private CommerceDiscountCommerceAccountGroupRelLocalService
		_commerceDiscountCommerceAccountGroupRelLocalService;

}