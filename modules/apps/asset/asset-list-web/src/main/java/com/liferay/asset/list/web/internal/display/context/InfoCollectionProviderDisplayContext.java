/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.display.context;

import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Comparator;
import java.util.List;

/**
 * @author Jürgen Kappler
 */
public class InfoCollectionProviderDisplayContext {

	public InfoCollectionProviderDisplayContext(
		InfoItemServiceRegistry infoItemServiceRegistry,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_infoItemServiceRegistry = infoItemServiceRegistry;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);

		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public SearchContainer<InfoCollectionProvider<?>> getSearchContainer() {
		SearchContainer<InfoCollectionProvider<?>> searchContainer =
			new SearchContainer<>(
				_renderRequest, _getPortletURL(), null,
				LanguageUtil.get(
					_httpServletRequest, "there-are-no-collection-providers"));

		searchContainer.setResultsAndTotal(_getInfoCollectionProviders());

		return searchContainer;
	}

	public String getSubtitle(
		InfoCollectionProvider<?> infoCollectionProvider) {

		String className = infoCollectionProvider.getCollectionItemClassName();

		if (Validator.isNotNull(className)) {
			return ResourceActionsUtil.getModelResource(
				_themeDisplay.getLocale(), className);
		}

		return StringPool.BLANK;
	}

	public String getTitle(InfoCollectionProvider<?> infoCollectionProvider) {
		return infoCollectionProvider.getLabel(_themeDisplay.getLocale());
	}

	private List<InfoCollectionProvider<?>> _getInfoCollectionProviders() {
		List<InfoCollectionProvider<?>> infoCollectionProviders =
			(List<InfoCollectionProvider<?>>)
				(List<?>)_infoItemServiceRegistry.getAllInfoItemServices(
					InfoCollectionProvider.class);

		return ListUtil.sort(
			ListUtil.filter(
				infoCollectionProviders, InfoCollectionProvider::isAvailable),
			Comparator.comparing(
				infoCollectionProvider -> infoCollectionProvider.getLabel(
					_themeDisplay.getLocale()),
				String.CASE_INSENSITIVE_ORDER));
	}

	private PortletURL _getPortletURL() {
		PortletURL currentURLObj = PortletURLUtil.getCurrent(
			_renderRequest, _renderResponse);

		try {
			return PortletURLUtil.clone(currentURLObj, _renderResponse);
		}
		catch (PortletException portletException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portletException);
			}

			return PortletURLBuilder.createRenderURL(
				_renderResponse
			).setParameters(
				currentURLObj.getParameterMap()
			).buildPortletURL();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		InfoCollectionProviderDisplayContext.class);

	private final HttpServletRequest _httpServletRequest;
	private final InfoItemServiceRegistry _infoItemServiceRegistry;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}