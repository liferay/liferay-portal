/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.category.facet.portlet.action;

import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.category.facet.constants.CategoryFacetPortletKeys;
import com.liferay.portal.search.web.internal.category.facet.display.context.CategoryFacetConfigurationDisplayContext;
import com.liferay.portlet.display.template.portlet.action.BaseConfigurationAction;

import jakarta.portlet.PortletConfig;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lino Alves
 */
@Component(
	property = "jakarta.portlet.name=" + CategoryFacetPortletKeys.CATEGORY_FACET,
	service = ConfigurationAction.class
)
public class CategoryFacetPortletConfigurationAction
	extends BaseConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/category/facet/configuration.jsp";
	}

	@Override
	public void include(
			PortletConfig portletConfig, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		httpServletRequest.setAttribute(
			AssetVocabularyLocalService.class.getName(),
			_assetVocabularyLocalService);
		httpServletRequest.setAttribute(
			GroupLocalService.class.getName(), _groupLocalService);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			new CategoryFacetConfigurationDisplayContext(
				_assetVocabularyService, _groupLocalService, _groupService,
				_jsonFactory, themeDisplay.getLocale(),
				_siteConnectedGroupGroupProvider));

		super.include(portletConfig, httpServletRequest, httpServletResponse);
	}

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private AssetVocabularyService _assetVocabularyService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private GroupService _groupService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private SiteConnectedGroupGroupProvider _siteConnectedGroupGroupProvider;

}