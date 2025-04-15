/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.item.selector.web.internal;

import com.liferay.commerce.product.item.selector.criterion.CPOptionItemSelectorCriterion;
import com.liferay.commerce.product.item.selector.web.internal.display.context.CPOptionItemSelectorViewDisplayContext;
import com.liferay.commerce.product.service.CPOptionService;
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
 * @author Marco Leo
 */
@Component(service = ItemSelectorView.class)
public class CPOptionItemSelectorView
	implements ItemSelectorView<CPOptionItemSelectorCriterion> {

	@Override
	public Class<CPOptionItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return CPOptionItemSelectorCriterion.class;
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
		return _language.get(locale, "options");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			CPOptionItemSelectorCriterion cpOptionItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		ServletContext servletContext = getServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher("/option_item_selector.jsp");

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)servletRequest;

		CPOptionItemSelectorViewDisplayContext
			cpOptionItemSelectorViewDisplayContext =
				new CPOptionItemSelectorViewDisplayContext(
					httpServletRequest, portletURL, itemSelectedEventName,
					_cpOptionService);

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			cpOptionItemSelectorViewDisplayContext);

		requestDispatcher.include(servletRequest, servletResponse);
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.unmodifiableList(
			ListUtil.fromArray(new UUIDItemSelectorReturnType()));

	@Reference
	private CPOptionService _cpOptionService;

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.product.item.selector.web)"
	)
	private ServletContext _servletContext;

}