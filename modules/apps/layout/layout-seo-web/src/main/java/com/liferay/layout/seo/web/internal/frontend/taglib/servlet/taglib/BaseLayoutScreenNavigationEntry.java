/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.item.selector.ItemSelector;
import com.liferay.layout.admin.constants.LayoutScreenNavigationEntryConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.seo.canonical.url.LayoutSEOCanonicalURLProvider;
import com.liferay.layout.seo.kernel.LayoutSEOLinkManager;
import com.liferay.layout.seo.service.LayoutSEOSiteLocalService;
import com.liferay.layout.seo.web.internal.constants.LayoutSEOWebKeys;
import com.liferay.layout.seo.web.internal.display.context.LayoutsSEODisplayContext;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
public abstract class BaseLayoutScreenNavigationEntry
	implements ScreenNavigationEntry<Layout> {

	@Override
	public String getCategoryKey() {
		return LayoutScreenNavigationEntryConstants.CATEGORY_KEY_GENERAL;
	}

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(locale, getEntryKey());
	}

	@Override
	public String getScreenNavigationKey() {
		return LayoutScreenNavigationEntryConstants.
			SCREEN_NAVIGATION_KEY_LAYOUT;
	}

	@Override
	public boolean isVisible(User user, Layout layout) {
		Group group = layout.getGroup();

		if (group.isLayoutPrototype() || group.isLayoutSetPrototype() ||
			((layout.isTypeAssetDisplay() || layout.isTypeContent()) &&
			 (layout.fetchDraftLayout() == null))) {

			return false;
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

		if ((layoutPageTemplateEntry != null) || layout.isTypeUtility()) {
			return false;
		}

		return true;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		httpServletRequest.setAttribute(
			LayoutSEOWebKeys.LAYOUT_PAGE_LAYOUT_SEO_DISPLAY_CONTEXT,
			new LayoutsSEODisplayContext(
				dlAppService, dlurlHelper, infoItemServiceRegistry,
				itemSelector, layoutLocalService,
				layoutPageTemplateEntryLocalService,
				layoutSEOCanonicalURLProvider, layoutSEOLinkManager,
				layoutSEOSiteLocalService,
				portal.getLiferayPortletRequest(
					(PortletRequest)httpServletRequest.getAttribute(
						JavaConstants.JAKARTA_PORTLET_REQUEST)),
				portal.getLiferayPortletResponse(
					(RenderResponse)httpServletRequest.getAttribute(
						JavaConstants.JAKARTA_PORTLET_RESPONSE))));

		jspRenderer.renderJSP(
			servletContext, httpServletRequest, httpServletResponse,
			getJspPath());
	}

	protected abstract String getJspPath();

	@Reference
	protected DLAppService dlAppService;

	@Reference
	protected DLURLHelper dlurlHelper;

	@Reference
	protected InfoItemServiceRegistry infoItemServiceRegistry;

	@Reference
	protected ItemSelector itemSelector;

	@Reference
	protected JSPRenderer jspRenderer;

	@Reference
	protected LayoutLocalService layoutLocalService;

	@Reference
	protected LayoutPageTemplateEntryLocalService
		layoutPageTemplateEntryLocalService;

	@Reference
	protected LayoutSEOCanonicalURLProvider layoutSEOCanonicalURLProvider;

	@Reference
	protected LayoutSEOLinkManager layoutSEOLinkManager;

	@Reference
	protected LayoutSEOSiteLocalService layoutSEOSiteLocalService;

	@Reference
	protected Portal portal;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.layout.seo.web)")
	protected ServletContext servletContext;

}