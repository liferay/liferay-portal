/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.fragment.internal.display.context;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Alessio Antonio Rendina
 */
public class AccountsDataSetDisplayContext extends BaseDisplayContext {

	public AccountsDataSetDisplayContext(
		Map<String, Object> configurationValues,
		HttpServletRequest httpServletRequest, Language language,
		Portal portal) {

		super(configurationValues, httpServletRequest);

		_language = language;
		_portal = portal;

		_commerceContext = (CommerceContext)httpServletRequest.getAttribute(
			CommerceWebKeys.COMMERCE_CONTEXT);
	}

	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"setCurrentAccountURL",
			StringBundler.concat(
				_portal.getPortalURL(httpServletRequest),
				_portal.getPathContext(), "/o/commerce-ui/set-current-account")
		).build();
	}

	public String getAPIURL() throws PortalException {
		return StringBundler.concat(
			"/o/headless-commerce-delivery-catalog/v1.0/channels/",
			_commerceContext.getCommerceChannelId(), "/accounts");
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return Collections.singletonList(
			new FDSActionDropdownItem(
				StringPool.BLANK, "view", "view",
				_language.get(httpServletRequest, "view"), null, null, "link"));
	}

	private final CommerceContext _commerceContext;
	private final Language _language;
	private final Portal _portal;

}