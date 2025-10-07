/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.fragment.internal.display.context;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Alessio Antonio Rendina
 */
public class PendingAccountOrdersDataSetDisplayContext
	extends BaseDisplayContext {

	public PendingAccountOrdersDataSetDisplayContext(
		Map<String, Object> configurationValues,
		HttpServletRequest httpServletRequest, Language language, Portal portal,
		PortletURLFactory portletURLFactory) {

		super(configurationValues, httpServletRequest);

		_language = language;
		_portal = portal;
		_portletURLFactory = portletURLFactory;

		_commerceContext = (CommerceContext)httpServletRequest.getAttribute(
			CommerceWebKeys.COMMERCE_CONTEXT);
	}

	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"setCurrentOrderURL", () -> _getEditOrderURL()
		).build();
	}

	public String getAPIURL() throws PortalException {
		return StringBundler.concat(
			"/o/headless-commerce-delivery-cart/v1.0/channels/",
			_commerceContext.getCommerceChannelId(), "/carts");
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return Collections.singletonList(
			new FDSActionDropdownItem(
				StringPool.BLANK, "view", "view",
				_language.get(httpServletRequest, "view"), null, null, "link"));
	}

	private String _getEditOrderURL() throws PortalException {
		long plid = _portal.getPlidFromPortletId(
			_portal.getScopeGroupId(httpServletRequest),
			CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT);

		if ((plid > 0) || FeatureFlagManagerUtil.isEnabled("LPD-20379")) {
			return PortletURLBuilder.create(
				_getPortletURL()
			).setActionName(
				"/commerce_open_order_content/edit_commerce_order"
			).setCMD(
				"setCurrent"
			).setParameter(
				"commerceOrderId", "{id}"
			).buildString();
		}

		return StringPool.BLANK;
	}

	private PortletURL _getPortletURL() throws PortalException {
		long plid = _portal.getPlidFromPortletId(
			_portal.getScopeGroupId(httpServletRequest),
			CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT);

		if (plid > 0) {
			return _portletURLFactory.create(
				httpServletRequest,
				CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT, plid,
				PortletRequest.ACTION_PHASE);
		}

		return _portletURLFactory.create(
			httpServletRequest, CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT,
			PortletRequest.ACTION_PHASE);
	}

	private final CommerceContext _commerceContext;
	private final Language _language;
	private final Portal _portal;
	private final PortletURLFactory _portletURLFactory;

}