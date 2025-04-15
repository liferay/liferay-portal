/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.content;

import com.liferay.fragment.renderer.FragmentRendererController;
import com.liferay.layout.content.LayoutContentProvider;
import com.liferay.layout.crawler.LayoutCrawler;
import com.liferay.layout.internal.search.util.LayoutPageTemplateStructureRenderUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.RenderLayoutContentThreadLocal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = LayoutContentProvider.class)
public class LayoutContentProviderImpl implements LayoutContentProvider {

	@Override
	public String getLayoutContent(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, Layout layout, Locale locale) {

		if ((httpServletRequest == null) || (httpServletResponse == null)) {
			return StringPool.BLANK;
		}

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					layout.getGroupId(), layout.getPlid());

		if (layoutPageTemplateStructure == null) {
			return StringPool.BLANK;
		}

		boolean originalRenderLayoutContent =
			RenderLayoutContentThreadLocal.isRenderLayoutContent();

		try {
			RenderLayoutContentThreadLocal.setRenderLayoutContent(true);

			if (_isUseLayoutCrawler(layout)) {
				String content = StringPool.BLANK;

				try {
					LayoutCrawler layoutCrawler = _layoutCrawlerSnapshot.get();

					content = layoutCrawler.getLayoutContent(layout, locale);
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn("Unable to get layout content", exception);
					}
				}

				content = _getWrapper(content);

				if (Validator.isNotNull(content)) {
					return content;
				}
			}

			httpServletRequest = DynamicServletRequest.addQueryString(
				httpServletRequest,
				StringBundler.concat(
					"p_l_id=", layout.getPlid(), "&p_l_mode=",
					Constants.SEARCH),
				false);

			Layout originalRequestLayout =
				(Layout)httpServletRequest.getAttribute(WebKeys.LAYOUT);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			HttpServletRequest originalThemeDisplayHttpServletRequest =
				themeDisplay.getRequest();

			String originalLanguageId = themeDisplay.getLanguageId();
			Locale originalLocale = themeDisplay.getLocale();
			Layout originalThemeDisplayLayout = themeDisplay.getLayout();
			long originalThemeDisplayPlid = themeDisplay.getPlid();

			try {
				httpServletRequest.setAttribute(
					WebKeys.SHOW_PORTLET_TOPPER, Boolean.FALSE);

				if ((layout != originalRequestLayout) ||
					(layout != themeDisplay.getLayout())) {

					httpServletRequest.setAttribute(WebKeys.LAYOUT, layout);

					themeDisplay.setLayout(layout);
					themeDisplay.setPlid(layout.getPlid());
				}

				themeDisplay.setLanguageId(LocaleUtil.toLanguageId(locale));
				themeDisplay.setLocale(locale);
				themeDisplay.setRequest(httpServletRequest);

				long segmentsExperienceId =
					_segmentsExperienceLocalService.
						fetchDefaultSegmentsExperienceId(layout.getPlid());

				String content = StringPool.BLANK;

				try {
					content =
						LayoutPageTemplateStructureRenderUtil.
							renderLayoutContent(
								_fragmentRendererController, httpServletRequest,
								httpServletResponse,
								layoutPageTemplateStructure, locale,
								segmentsExperienceId);
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn("Unable to get layout content", exception);
					}
				}

				return _htmlParser.extractText(content);
			}
			finally {
				httpServletRequest.removeAttribute(WebKeys.SHOW_PORTLET_TOPPER);

				if ((layout != originalRequestLayout) ||
					(layout != themeDisplay.getLayout())) {

					httpServletRequest.setAttribute(
						WebKeys.LAYOUT, originalRequestLayout);

					themeDisplay.setLayout(originalThemeDisplayLayout);
					themeDisplay.setPlid(originalThemeDisplayPlid);
				}

				themeDisplay.setLanguageId(originalLanguageId);
				themeDisplay.setLocale(originalLocale);
				themeDisplay.setRequest(originalThemeDisplayHttpServletRequest);
			}
		}
		finally {
			RenderLayoutContentThreadLocal.setRenderLayoutContent(
				originalRenderLayoutContent);
		}
	}

	private String _getWrapper(String layoutContent) {
		int wrapperIndex = layoutContent.indexOf(_WRAPPER_ELEMENT);

		if (wrapperIndex == -1) {
			return layoutContent;
		}

		return _htmlParser.extractText(
			layoutContent.substring(wrapperIndex + _WRAPPER_ELEMENT.length()));
	}

	private boolean _isUseLayoutCrawler(Layout layout) {
		LayoutCrawler layoutCrawler = _layoutCrawlerSnapshot.get();

		if ((layoutCrawler == null) || layout.isPrivateLayout()) {
			return false;
		}

		Role role = _roleLocalService.fetchRole(
			layout.getCompanyId(), RoleConstants.GUEST);

		if (role == null) {
			return false;
		}

		ResourcePermission resourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				role.getCompanyId(), Layout.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(layout.getPlid()), role.getRoleId());

		if ((resourcePermission != null) &&
			resourcePermission.isViewActionId()) {

			return true;
		}

		return false;
	}

	private static final String _WRAPPER_ELEMENT = "id=\"wrapper\">";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutContentProviderImpl.class);

	private static final Snapshot<LayoutCrawler> _layoutCrawlerSnapshot =
		new Snapshot<>(
			LayoutContentProviderImpl.class, LayoutCrawler.class, null, true);

	@Reference
	private FragmentRendererController _fragmentRendererController;

	@Reference
	private HtmlParser _htmlParser;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}