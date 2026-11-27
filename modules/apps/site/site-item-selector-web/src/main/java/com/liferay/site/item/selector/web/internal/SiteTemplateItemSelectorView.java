/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.GroupItemSelectorReturnType;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.site.constants.SiteWebKeys;
import com.liferay.site.item.selector.SiteItemSelectorCriterion;
import com.liferay.site.item.selector.web.internal.constants.SitesItemSelectorWebKeys;
import com.liferay.site.item.selector.web.internal.display.context.SiteTemplateItemSelectorViewDisplayContext;
import com.liferay.site.provider.GroupURLProvider;

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
 * @author Roberto Díaz
 */
@Component(
	property = "item.selector.view.order:Integer=400",
	service = ItemSelectorView.class
)
public class SiteTemplateItemSelectorView
	implements ItemSelectorView<SiteItemSelectorCriterion> {

	@Override
	public Class<SiteItemSelectorCriterion> getItemSelectorCriterionClass() {
		return SiteItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return ResourceBundleUtil.getString(
			_portal.getResourceBundle(locale), "site-templates");
	}

	@Override
	public boolean isVisible(
		SiteItemSelectorCriterion siteItemSelectorCriterion,
		ThemeDisplay themeDisplay) {

		return siteItemSelectorCriterion.isIncludeLayoutSetPrototypes();
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			SiteItemSelectorCriterion siteItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher("/view_sites.jsp");

		servletRequest.setAttribute(
			SiteWebKeys.GROUP_URL_PROVIDER, _groupURLProvider);

		SiteTemplateItemSelectorViewDisplayContext
			siteTemplateItemSelectorViewDisplayContext =
				new SiteTemplateItemSelectorViewDisplayContext(
					(HttpServletRequest)servletRequest,
					siteItemSelectorCriterion, itemSelectedEventName,
					portletURL);

		servletRequest.setAttribute(
			SitesItemSelectorWebKeys.SITES_ITEM_SELECTOR_DISPLAY_CONTEXT,
			siteTemplateItemSelectorViewDisplayContext);

		requestDispatcher.include(servletRequest, servletResponse);
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.unmodifiableList(
			ListUtil.fromArray(
				new GroupItemSelectorReturnType(),
				new URLItemSelectorReturnType(),
				new UUIDItemSelectorReturnType()));

	@Reference
	private GroupURLProvider _groupURLProvider;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.site.item.selector.web)"
	)
	private ServletContext _servletContext;

}