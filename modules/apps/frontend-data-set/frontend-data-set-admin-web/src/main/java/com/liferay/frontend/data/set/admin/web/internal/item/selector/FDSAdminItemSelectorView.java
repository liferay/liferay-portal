/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.admin.web.internal.item.selector;

import com.liferay.frontend.data.set.admin.web.internal.constants.FDSAdminWebKeys;
import com.liferay.frontend.data.set.admin.web.internal.display.context.FDSAdminItemSelectorDisplayContext;
import com.liferay.info.item.selector.InfoItemSelectorView;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.ThemeDisplay;

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
 * @author Marko Cikos
 */
@Component(service = ItemSelectorView.class)
public class FDSAdminItemSelectorView
	implements InfoItemSelectorView,
			   ItemSelectorView<InfoItemItemSelectorCriterion> {

	@Override
	public String getClassName() {
		return "FDSView";
	}

	@Override
	public Class<InfoItemItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return InfoItemItemSelectorCriterion.class;
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
		return _language.get(locale, "data-set-views");
	}

	@Override
	public boolean isVisible(
		InfoItemItemSelectorCriterion infoItemSelectorCriterion,
		ThemeDisplay themeDisplay) {

		if (!FeatureFlagManagerUtil.isEnabled("LPS-164563")) {
			return false;
		}

		return isVisible(themeDisplay);
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			InfoItemItemSelectorCriterion infoItemItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		ServletContext servletContext = getServletContext();

		servletRequest.setAttribute(
			FDSAdminWebKeys.FDS_ADMIN_ITEM_SELECTOR_DISPLAY_CONTEXT,
			new FDSAdminItemSelectorDisplayContext(
				(HttpServletRequest)servletRequest));

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher(
				"/item/selector/fds_admin_item_selector.jsp");

		requestDispatcher.include(servletRequest, servletResponse);
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new InfoItemItemSelectorReturnType());

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.frontend.data.set.admin.web)"
	)
	private ServletContext _servletContext;

}