/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.control.menu.web.internal;

import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.control.menu.BaseProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"product.navigation.control.menu.category.key=" + ProductNavigationControlMenuCategoryKeys.SITES,
		"product.navigation.control.menu.entry.order:Integer=200"
	},
	service = ProductNavigationControlMenuEntry.class
)
public class LayoutBackLinkProductNavigationControlMenuEntry
	extends BaseProductNavigationControlMenuEntry
	implements ProductNavigationControlMenuEntry {

	@Override
	public String getIcon(HttpServletRequest httpServletRequest) {
		return "angle-left";
	}

	@Override
	public String getIconCssClass(HttpServletRequest httpServletRequest) {
		return "icon-monospaced";
	}

	@Override
	public String getLabel(Locale locale) {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if ((serviceContext == null) || (serviceContext.getRequest() == null)) {
			return _language.get(locale, "back");
		}

		String backURLTitle = ParamUtil.getString(
			serviceContext.getRequest(), "p_l_back_url_title");

		if (Validator.isNotNull(backURLTitle)) {
			return _language.format(
				locale, "go-to-x",
				new String[] {HtmlUtil.escape(backURLTitle)});
		}

		return _language.get(locale, "back");
	}

	@Override
	public String getLinkCssClass(HttpServletRequest httpServletRequest) {
		return "control-menu-nav-link lfr-back-link";
	}

	@Override
	public String getURL(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = _layoutLocalService.fetchLayout(themeDisplay.getPlid());

		if (!Objects.equals(
				ParamUtil.getString(
					httpServletRequest, "p_l_mode", Constants.VIEW),
				Constants.EDIT) ||
			(layout == null) || !layout.isDraftLayout()) {

			return _portal.escapeRedirect(
				ParamUtil.getString(httpServletRequest, "p_l_back_url"));
		}

		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				httpServletRequest,
				ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
				themeDisplay.getPlid(), PortletRequest.ACTION_PHASE)
		).setActionName(
			"/layout_content_page_editor/unlock_draft_layout"
		).setRedirect(
			() -> _portal.escapeRedirect(
				ParamUtil.getString(httpServletRequest, "p_l_back_url"))
		).buildString();
	}

	@Override
	public boolean isShow(HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		if (layout.isTypeControlPanel()) {
			return false;
		}

		String layoutBackURL = _portal.escapeRedirect(
			ParamUtil.getString(httpServletRequest, "p_l_back_url"));

		if (Validator.isNull(layoutBackURL)) {
			return false;
		}

		return super.isShow(httpServletRequest);
	}

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

}