/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.channel.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.commerce.channel.web.internal.display.context.CommerceChannelCountryDisplayContext;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelRelService;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class CommerceChannelCountryScreenNavigationEntry
	extends CommerceChannelCountryScreenNavigationCategory
	implements ScreenNavigationEntry<CommerceChannel> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			new CommerceChannelCountryDisplayContext(
				_commerceChannelModelResourcePermission,
				_commerceChannelRelService, _commerceChannelService,
				httpServletRequest, _itemSelector));

		_jspRenderer.renderJSP(
			httpServletRequest, httpServletResponse,
			"/commerce_channel/countries.jsp");
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceChannel)"
	)
	private ModelResourcePermission<CommerceChannel>
		_commerceChannelModelResourcePermission;

	@Reference
	private CommerceChannelRelService _commerceChannelRelService;

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private JSPRenderer _jspRenderer;

}