/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.item.selector.web.internal;

import com.liferay.commerce.product.item.selector.criterion.CommerceChannelItemSelectorCriterion;
import com.liferay.commerce.product.item.selector.web.internal.display.context.CommerceChannelItemSelectorViewDisplayContext;
import com.liferay.commerce.product.service.CommerceChannelRelService;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = ItemSelectorView.class)
public class CommerceChannelItemSelectorView
	implements ItemSelectorView<CommerceChannelItemSelectorCriterion> {

	@Override
	public Class<CommerceChannelItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return CommerceChannelItemSelectorCriterion.class;
	}

	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return _language.get(locale, "channels");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			CommerceChannelItemSelectorCriterion
				commerceChannelItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		ServletContext servletContext = getServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher("/channel_item_selector.jsp");

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)servletRequest;

		CommerceChannelItemSelectorViewDisplayContext
			commerceChannelItemSelectorViewDisplayContext =
				new CommerceChannelItemSelectorViewDisplayContext(
					_commerceChannelRelService, _commerceChannelService,
					httpServletRequest, portletURL, itemSelectedEventName);

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			commerceChannelItemSelectorViewDisplayContext);

		requestDispatcher.include(servletRequest, servletResponse);
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.unmodifiableList(
			ListUtil.fromArray(new UUIDItemSelectorReturnType()));

	@Reference
	private CommerceChannelRelService _commerceChannelRelService;

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.product.item.selector.web)"
	)
	private ServletContext _servletContext;

}