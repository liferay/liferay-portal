/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.fragment.internal.frontend.data.set.url;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.frontend.data.set.url.FDSAPIURLResolver;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.rest.application.key=/headless-commerce-delivery-catalog/v1.0/Account",
	service = FDSAPIURLResolver.class
)
public class AccountEntryFDSAPIURLResolver implements FDSAPIURLResolver {

	@Override
	public String getSchema() {
		return "Account";
	}

	@Override
	public String resolve(String baseURL, HttpServletRequest httpServletRequest)
		throws PortalException {

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		if (commerceContext == null) {
			return StringPool.BLANK;
		}

		return StringUtil.replace(
			baseURL, new String[] {"{channelId}", "{filter}"},
			new String[] {
				String.valueOf(commerceContext.getCommerceChannelId()),
				_getFilterString(commerceContext.getAccountEntryAllowedTypes())
			});
	}

	private String _getFilterString(String[] accountEntryAllowedTypes) {
		if (accountEntryAllowedTypes == null) {
			return null;
		}

		if (accountEntryAllowedTypes.length == 0) {
			return StringPool.BLANK;
		}

		if (accountEntryAllowedTypes.length == 1) {
			return StringBundler.concat(
				StringPool.APOSTROPHE, accountEntryAllowedTypes[0],
				StringPool.APOSTROPHE);
		}

		StringBundler sb = new StringBundler(
			(4 * accountEntryAllowedTypes.length) - 1);

		for (int i = 0; i < accountEntryAllowedTypes.length; i++) {
			if (i != 0) {
				sb.append(", ");
			}

			sb.append(StringPool.APOSTROPHE);

			String value = String.valueOf(accountEntryAllowedTypes[i]);

			sb.append(value.trim());

			sb.append(StringPool.APOSTROPHE);
		}

		return sb.toString();
	}

}