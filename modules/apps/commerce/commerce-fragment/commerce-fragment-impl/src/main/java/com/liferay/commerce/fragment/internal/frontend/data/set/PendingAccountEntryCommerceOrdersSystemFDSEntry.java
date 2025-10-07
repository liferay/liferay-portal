/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.fragment.internal.frontend.data.set;

import com.liferay.commerce.fragment.internal.constants.CommerceFragmentFDSNames;
import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.LocaleThreadLocal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "frontend.data.set.name=" + CommerceFragmentFDSNames.PENDING_ACCOUNT_ORDERS,
	service = SystemFDSEntry.class
)
public class PendingAccountEntryCommerceOrdersSystemFDSEntry
	implements SystemFDSEntry {

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getName() {
		return CommerceFragmentFDSNames.PENDING_ACCOUNT_ORDERS;
	}

	@Override
	public String getRESTApplication() {
		return "/headless-commerce-delivery-cart/v1.0";
	}

	@Override
	public String getRESTEndpoint() {
		return "/v1.0/channels/{channelId}/account/{accountId}/carts";
	}

	@Override
	public String getRESTSchema() {
		return "Cart";
	}

	@Override
	public String getTitle() {
		return _language.get(
			LocaleThreadLocal.getThemeDisplayLocale(),
			"pending-account-orders");
	}

	@Reference
	private Language _language;

}