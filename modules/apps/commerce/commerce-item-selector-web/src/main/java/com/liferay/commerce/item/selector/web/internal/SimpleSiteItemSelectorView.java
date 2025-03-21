/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.item.selector.web.internal;

import com.liferay.commerce.item.selector.criterion.SimpleSiteItemSelectorCriterion;
import com.liferay.commerce.item.selector.web.internal.display.context.SimpleSiteItemSelectorViewDisplayContext;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.GroupService;
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
 * @author Alec Sloan
 */
@Component(service = ItemSelectorView.class)
public class SimpleSiteItemSelectorView
	implements ItemSelectorView<SimpleSiteItemSelectorCriterion> {

	@Override
	public Class<SimpleSiteItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return SimpleSiteItemSelectorCriterion.class;
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
		return _language.get(locale, "sites");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			SimpleSiteItemSelectorCriterion simpleSiteItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		ServletContext servletContext = getServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher("/site_item_selector.jsp");

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)servletRequest;

		SimpleSiteItemSelectorViewDisplayContext
			simpleSiteItemSelectorViewDisplayContext =
				new SimpleSiteItemSelectorViewDisplayContext(
					_commerceChannelLocalService, _groupService,
					httpServletRequest, portletURL, itemSelectedEventName,
					search);

		servletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			simpleSiteItemSelectorViewDisplayContext);

		requestDispatcher.include(servletRequest, servletResponse);
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new UUIDItemSelectorReturnType());

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private GroupService _groupService;

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.item.selector.web)"
	)
	private ServletContext _servletContext;

}