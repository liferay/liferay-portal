/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.item.selector;

import com.liferay.dynamic.data.mapping.form.item.selector.DDMUserPersonalFolderItemSelectorCriterion;
import com.liferay.dynamic.data.mapping.form.web.internal.display.context.DDMUserPersonalFolderItemSelectorViewDisplayContext;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorReturnTypeResolverHandler;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ListUtil;

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
 * @author Eudaldo Alonso
 */
@Component(
	property = "item.selector.view.order:Integer=100",
	service = ItemSelectorView.class
)
public class DDMUserPersonalFolderItemSelectorView
	implements ItemSelectorView<DDMUserPersonalFolderItemSelectorCriterion> {

	@Override
	public Class<DDMUserPersonalFolderItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return DDMUserPersonalFolderItemSelectorCriterion.class;
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
		return _language.get(locale, "user-personal-folder");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			DDMUserPersonalFolderItemSelectorCriterion
				ddmUserPersonalFolderItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		ServletContext servletContext = getServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher(
				"/item_selector/user_personal_folder.jsp");

		DDMUserPersonalFolderItemSelectorViewDisplayContext
			ddmUserPersonalFolderItemSelectorViewDisplayContext =
				new DDMUserPersonalFolderItemSelectorViewDisplayContext(
					(HttpServletRequest)servletRequest, itemSelectedEventName,
					_itemSelectorReturnTypeResolverHandler,
					ddmUserPersonalFolderItemSelectorCriterion, this,
					portletURL, search);

		servletRequest.setAttribute(
			DDMUserPersonalFolderItemSelectorViewDisplayContext.class.getName(),
			ddmUserPersonalFolderItemSelectorViewDisplayContext);

		requestDispatcher.include(servletRequest, servletResponse);
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.unmodifiableList(
			ListUtil.fromArray(
				new FileEntryItemSelectorReturnType(),
				new URLItemSelectorReturnType()));

	@Reference
	private ItemSelectorReturnTypeResolverHandler
		_itemSelectorReturnTypeResolverHandler;

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.dynamic.data.mapping.form.web)"
	)
	private ServletContext _servletContext;

}