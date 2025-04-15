/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.account.item.selector.web.internal;

import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.account.item.selector.CommerceAccountItemSelectorCriterion;
import com.liferay.commerce.account.item.selector.web.internal.display.context.CommerceAccountItemSelectorViewDisplayContext;
import com.liferay.commerce.util.CommerceAccountHelper;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.Base64ItemSelectorReturnType;
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
 * @author Ethan Bustad
 */
@Component(service = ItemSelectorView.class)
public class CommerceAccountItemSelectorView
	implements ItemSelectorView<CommerceAccountItemSelectorCriterion> {

	@Override
	public Class<CommerceAccountItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return CommerceAccountItemSelectorCriterion.class;
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
		return _language.get(locale, "accounts");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			CommerceAccountItemSelectorCriterion
				commerceAccountItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		ServletContext servletContext = getServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher("/account_item_selector.jsp");

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)servletRequest;

		CommerceAccountItemSelectorViewDisplayContext
			commerceAccountItemSelectorViewDisplayContext =
				new CommerceAccountItemSelectorViewDisplayContext(
					_accountEntryLocalService, _commerceAccountHelper,
					httpServletRequest, portletURL, itemSelectedEventName);

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			commerceAccountItemSelectorViewDisplayContext);

		requestDispatcher.include(servletRequest, servletResponse);
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.unmodifiableList(
			ListUtil.fromArray(
				new Base64ItemSelectorReturnType(),
				new UUIDItemSelectorReturnType()));

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.account.item.selector.web)"
	)
	private ServletContext _servletContext;

}