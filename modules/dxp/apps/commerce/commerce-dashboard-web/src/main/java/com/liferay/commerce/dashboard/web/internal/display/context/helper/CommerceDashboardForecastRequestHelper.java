/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.dashboard.web.internal.display.context.helper;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.portal.kernel.display.context.helper.BaseRequestHelper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Riccardo Ferrari
 */
public class CommerceDashboardForecastRequestHelper extends BaseRequestHelper {

	public CommerceDashboardForecastRequestHelper(
		HttpServletRequest httpServletRequest) {

		super(httpServletRequest);

		_commerceContext = (CommerceContext)httpServletRequest.getAttribute(
			CommerceWebKeys.COMMERCE_CONTEXT);
	}

	public long getAccountEntryId() {
		try {
			AccountEntry accountEntry = _commerceContext.getAccountEntry();

			return accountEntry.getAccountEntryId();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return 0;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceDashboardForecastRequestHelper.class);

	private final CommerceContext _commerceContext;

}