/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.asset.model;

import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.LayoutBranch;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetBranchLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Raymond Augé
 */
public class LayoutRevisionAssetRenderer
	extends BaseJSPAssetRenderer<LayoutRevision> {

	public LayoutRevisionAssetRenderer(LayoutRevision layoutRevision) {
		_layoutRevision = layoutRevision;

		try {
			_layoutBranch = layoutRevision.getLayoutBranch();
			_layoutSetBranch =
				LayoutSetBranchLocalServiceUtil.getLayoutSetBranch(
					layoutRevision.getLayoutSetBranchId());
		}
		catch (Exception exception) {
			throw new IllegalStateException(exception);
		}
	}

	@Override
	public LayoutRevision getAssetObject() {
		return _layoutRevision;
	}

	@Override
	public String getClassName() {
		return LayoutRevision.class.getName();
	}

	@Override
	public long getClassPK() {
		return _layoutRevision.getLayoutRevisionId();
	}

	@Override
	public long getGroupId() {
		return _layoutRevision.getGroupId();
	}

	@Override
	public String getJspPath(
		HttpServletRequest httpServletRequest, String template) {

		if (template.equals(TEMPLATE_FULL_CONTENT)) {
			return "/asset/" + template + ".jsp";
		}

		return null;
	}

	@Override
	public int getStatus() {
		return _layoutRevision.getStatus();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		Locale locale = getLocale(portletRequest);

		return StringBundler.concat(
			LanguageUtil.get(locale, "page"), ": ",
			_layoutRevision.getHTMLTitle(locale), "\n",
			LanguageUtil.get(locale, "site-pages-variation"), ": ",
			LanguageUtil.get(locale, _layoutSetBranch.getName()), "\n",
			LanguageUtil.get(locale, "page-variation"), ": ",
			LanguageUtil.get(locale, _layoutBranch.getName()), "\n",
			LanguageUtil.get(locale, "revision-id"), ": ",
			_layoutRevision.getLayoutRevisionId());
	}

	@Override
	public String getTitle(Locale locale) {
		return _layoutRevision.getHTMLTitle(locale);
	}

	@Override
	public String getURLViewInContext(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		String noSuchEntryRedirect) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return getURLViewInContext(themeDisplay, noSuchEntryRedirect);
	}

	@Override
	public String getURLViewInContext(
		ThemeDisplay themeDisplay, String noSuchEntryRedirect) {

		try {
			String layoutURL = PortalUtil.getLayoutURL(
				LayoutLocalServiceUtil.getLayout(_layoutRevision.getPlid()),
				themeDisplay);

			layoutURL = HttpComponentsUtil.addParameter(
				layoutURL, "layoutSetBranchId",
				_layoutRevision.getLayoutSetBranchId());
			layoutURL = HttpComponentsUtil.addParameter(
				layoutURL, "layoutRevisionId",
				_layoutRevision.getLayoutRevisionId());

			return layoutURL;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return StringPool.BLANK;
		}
	}

	@Override
	public long getUserId() {
		return _layoutRevision.getUserId();
	}

	@Override
	public String getUserName() {
		return _layoutRevision.getUserName();
	}

	@Override
	public String getUuid() {
		return null;
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		httpServletRequest.setAttribute(
			WebKeys.LAYOUT_REVISION, _layoutRevision);

		return super.include(httpServletRequest, httpServletResponse, template);
	}

	@Override
	public boolean isPreviewInContext() {
		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutRevisionAssetRenderer.class);

	private final LayoutBranch _layoutBranch;
	private final LayoutRevision _layoutRevision;
	private final LayoutSetBranch _layoutSetBranch;

}