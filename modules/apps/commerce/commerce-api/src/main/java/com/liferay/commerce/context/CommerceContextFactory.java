/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.context;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Marco Leo
 */
public interface CommerceContextFactory {

	public CommerceContext create(HttpServletRequest httpServletRequest);

	public CommerceContext create(
		long commerceAccountId, long commerceChannelGroupId,
		String commerceCurrencyCode, long commerceOrderId, long companyId);

}