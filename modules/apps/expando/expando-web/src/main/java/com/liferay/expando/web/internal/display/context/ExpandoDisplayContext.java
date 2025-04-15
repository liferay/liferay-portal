/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.web.internal.display.context;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.util.ExpandoBridgeFactoryUtil;
import com.liferay.expando.web.internal.search.CustomFieldChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntryListBuilder;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Pei-Jung Lan
 */
public class ExpandoDisplayContext {

	public ExpandoDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<BreadcrumbEntry> getBreadcrumbEntries() {
		return BreadcrumbEntryListBuilder.add(
			breadcrumbEntry -> {
				breadcrumbEntry.setTitle(
					LanguageUtil.get(_httpServletRequest, "custom-field"));
				breadcrumbEntry.setURL(
					String.valueOf(_renderResponse.createRenderURL()));
			}
		).add(
			breadcrumbEntry -> breadcrumbEntry.setTitle(
				LanguageUtil.get(_httpServletRequest, "view-attributes"))
		).build();
	}

	public SearchContainer<String> getSearchContainer() {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		String modelResource = ParamUtil.getString(
			_httpServletRequest, "modelResource");

		String modelResourceName = ResourceActionsUtil.getModelResource(
			_httpServletRequest, modelResource);

		SearchContainer<String> searchContainer = new SearchContainer<>(
			_renderRequest, _renderResponse.createRenderURL(), null,
			LanguageUtil.format(
				_httpServletRequest, "no-custom-fields-are-defined-for-x",
				HtmlUtil.escape(modelResourceName), false));

		ExpandoBridge expandoBridge = ExpandoBridgeFactoryUtil.getExpandoBridge(
			_themeDisplay.getCompanyId(), modelResource);

		List<String> attributeNames = Collections.list(
			expandoBridge.getAttributeNames());

		searchContainer.setDelta(attributeNames.size());

		searchContainer.setId("customFields");
		searchContainer.setResultsAndTotal(attributeNames);
		searchContainer.setRowChecker(
			new CustomFieldChecker(_renderRequest, _renderResponse));

		_searchContainer = searchContainer;

		return _searchContainer;
	}

	private final HttpServletRequest _httpServletRequest;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private SearchContainer<String> _searchContainer;
	private final ThemeDisplay _themeDisplay;

}