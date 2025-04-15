/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.item.selector.web.internal;

import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.item.selector.criterion.CPDefinitionItemSelectorCriterion;
import com.liferay.commerce.product.item.selector.web.internal.display.context.CPDefinitionItemSelectorViewDisplayContext;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.type.CPTypeRegistry;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
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
public class CPDefinitionItemSelectorView
	implements ItemSelectorView<CPDefinitionItemSelectorCriterion> {

	@Override
	public Class<CPDefinitionItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return CPDefinitionItemSelectorCriterion.class;
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
		return _language.get(locale, "products");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			CPDefinitionItemSelectorCriterion cpDefinitionItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		ServletContext servletContext = getServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher(
				"/definition_item_selector.jsp");

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)servletRequest;

		long commerceChannelGroupId = ParamUtil.getLong(
			httpServletRequest, CPField.COMMERCE_CHANNEL_GROUP_ID);

		httpServletRequest.setAttribute(
			CPField.COMMERCE_CHANNEL_GROUP_ID,
			String.valueOf(commerceChannelGroupId));

		long cpDefinitionId = ParamUtil.getLong(
			httpServletRequest, CPField.CP_DEFINITION_ID);

		httpServletRequest.setAttribute(
			CPField.CP_DEFINITION_ID, String.valueOf(cpDefinitionId));

		CPDefinitionItemSelectorViewDisplayContext
			cpDefinitionItemSelectorViewDisplayContext =
				new CPDefinitionItemSelectorViewDisplayContext(
					httpServletRequest, portletURL, itemSelectedEventName,
					_cpDefinitionService, _cpTypeRegistry);

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			cpDefinitionItemSelectorViewDisplayContext);

		requestDispatcher.include(servletRequest, servletResponse);
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.unmodifiableList(
			ListUtil.fromArray(new UUIDItemSelectorReturnType()));

	@Reference
	private CPDefinitionService _cpDefinitionService;

	@Reference
	private CPTypeRegistry _cpTypeRegistry;

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.product.item.selector.web)"
	)
	private ServletContext _servletContext;

}