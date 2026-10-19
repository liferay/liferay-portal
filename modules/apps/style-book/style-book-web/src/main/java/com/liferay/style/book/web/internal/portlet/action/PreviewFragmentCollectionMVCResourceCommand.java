/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.portlet.action;

import com.liferay.client.extension.type.ThemeCSSCET;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.frontend.token.definition.constants.FrontendTokenDefinitionConstants;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.theme.ThemeUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.web.internal.constants.StyleBookWebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 */
@Component(
	property = {
		"jakarta.portlet.name=" + StyleBookPortletKeys.STYLE_BOOK,
		"mvc.command.name=/style_book/preview_fragment_collection"
	},
	service = MVCResourceCommand.class
)
public class PreviewFragmentCollectionMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			resourceRequest);

		httpServletRequest.setAttribute(
			StyleBookWebKeys.FRAGMENT_COLLECTION_CONTRIBUTOR_TRACKER,
			_fragmentCollectionContributorRegistry);

		HttpServletResponse httpServletResponse =
			_portal.getHttpServletResponse(resourceResponse);

		String renderedPreviewFragmentCollection =
			_renderPreviewFragmentCollection(
				httpServletRequest, httpServletResponse);

		String renderedPortalNormal = _renderPortalNormal(
			httpServletRequest, httpServletResponse);

		ServletResponseUtil.write(
			httpServletResponse,
			ThemeUtil.replaceBodyContent(
				renderedPortalNormal, renderedPreviewFragmentCollection));
	}

	private String _renderPortalNormal(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			themeDisplay.getScopeGroupId(), false);

		Theme theme = layoutSet.getTheme();

		String themeId = ParamUtil.getString(
			httpServletRequest, "styleBookEntryThemeId");

		FrontendTokenDefinition frontendTokenDefinition =
			_frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
				themeDisplay.getCompanyId(), themeId);

		if ((frontendTokenDefinition != null) &&
			Objects.equals(
				frontendTokenDefinition.getThemeType(),
				FrontendTokenDefinitionConstants.THEME_TYPE_THEME_CSS_CET)) {

			themeId = theme.getThemeId();

			ThemeCSSCET themeCSSCET = (ThemeCSSCET)_cetManager.getCET(
				themeDisplay.getCompanyId(), themeId);

			if (themeCSSCET != null) {
				if (_portal.isRightToLeft(httpServletRequest)) {
					themeDisplay.setClayCSSURL(themeCSSCET.getClayRTLURL());
					themeDisplay.setMainCSSURL(themeCSSCET.getMainRTLURL());
				}
				else {
					themeDisplay.setClayCSSURL(themeCSSCET.getClayURL());
					themeDisplay.setMainCSSURL(themeCSSCET.getMainURL());
				}
			}
		}

		theme = _themeLocalService.fetchTheme(
			themeDisplay.getCompanyId(), themeId);

		themeDisplay.setLookAndFeel(
			theme,
			_themeLocalService.getColorScheme(
				themeDisplay.getCompanyId(), theme.getThemeId(),
				StringPool.BLANK));

		return ThemeUtil.include(
			ServletContextPool.get(StringPool.BLANK), httpServletRequest,
			httpServletResponse, "portal_normal.ftl", theme, false);
	}

	private String _renderPreviewFragmentCollection(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher(
				"/preview_fragment_collection.jsp");

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		PipingServletResponse pipingServletResponse = new PipingServletResponse(
			httpServletResponse, unsyncStringWriter);

		requestDispatcher.include(httpServletRequest, pipingServletResponse);

		return unsyncStringWriter.toString();
	}

	@Reference
	private CETManager _cetManager;

	@Reference
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Reference
	private FrontendTokenDefinitionRegistry _frontendTokenDefinitionRegistry;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference
	private Portal _portal;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.style.book.web)")
	private ServletContext _servletContext;

	@Reference
	private ThemeLocalService _themeLocalService;

}