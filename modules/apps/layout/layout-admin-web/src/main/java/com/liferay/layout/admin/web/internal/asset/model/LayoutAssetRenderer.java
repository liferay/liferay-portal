/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.asset.model;

import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Eduardo García
 */
public class LayoutAssetRenderer extends BaseJSPAssetRenderer<Layout> {

	public LayoutAssetRenderer(Layout layout) {
		_layout = layout;
	}

	@Override
	public Layout getAssetObject() {
		return _layout;
	}

	@Override
	public String getClassName() {
		return Layout.class.getName();
	}

	@Override
	public long getClassPK() {
		return _layout.getPlid();
	}

	@Override
	public long getGroupId() {
		return _layout.getGroupId();
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
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		Locale locale = getLocale(portletRequest);

		String summary = StringBundler.concat(
			LanguageUtil.get(locale, "page"), ": ",
			_layout.getHTMLTitle(locale));

		if (_layout.isTypeContent() &&
			(_layout.isDenied() || _layout.isPending())) {

			return HtmlUtil.stripHtml(summary);
		}

		return summary;
	}

	@Override
	public String getTitle(Locale locale) {
		return _layout.getHTMLTitle(locale);
	}

	@Override
	public String getURLViewInContext(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			String noSuchEntryRedirect)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return getURLViewInContext(themeDisplay, noSuchEntryRedirect);
	}

	@Override
	public String getURLViewInContext(
			ThemeDisplay themeDisplay, String noSuchEntryRedirect)
		throws Exception {

		return PortalUtil.getLayoutFriendlyURL(_layout, themeDisplay);
	}

	@Override
	public long getUserId() {
		return _layout.getUserId();
	}

	@Override
	public String getUserName() {
		return _layout.getUserName();
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

		httpServletRequest.setAttribute(WebKeys.LAYOUT, _layout);

		return super.include(httpServletRequest, httpServletResponse, template);
	}

	@Override
	public boolean isPreviewInContext() {
		if (_layout.isTypeContent()) {
			return true;
		}

		return super.isPreviewInContext();
	}

	private final Layout _layout;

}