/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.fragment.renderer;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.util.configuration.FragmentEntryMenuDisplayConfiguration;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.NavItem;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.taglib.servlet.taglib.NavigationMenuTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michele Vigilante
 */
@Component(service = FragmentRenderer.class)
public class CommerceClassicNavigationFragmentRenderer
	implements FragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "commerce";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "commerce-classic-navigation");
	}

	@Override
	public boolean isSelectable(HttpServletRequest httpServletRequest) {
		return false;
	}

	@Override
	public void render(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			if ((themeDisplay == null) || !themeDisplay.isSignedIn()) {
				return;
			}

			PrintWriter printWriter = httpServletResponse.getWriter();

			printWriter.write(
				"<div id=\"" + fragmentRendererContext.getFragmentElementId() +
					"\">");

			long scopeGroupId = themeDisplay.getScopeGroupId();

			DDMTemplate ddmTemplate = _ddmTemplateLocalService.fetchTemplate(
				scopeGroupId, _portal.getClassNameId(NavItem.class),
				"COMMERCE-CLASSIC-NAVBAR-FTL");

			NavigationMenuTag navigationMenuTag = _getNavigationMenuTag(
				ddmTemplate, themeDisplay, scopeGroupId);

			navigationMenuTag.doTag(httpServletRequest, httpServletResponse);

			printWriter.write("</div>");
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private NavigationMenuTag _getNavigationMenuTag(
		DDMTemplate ddmTemplate, ThemeDisplay themeDisplay, long scopeGroupId) {

		NavigationMenuTag navigationMenuTag = new NavigationMenuTag();

		if (ddmTemplate != null) {
			navigationMenuTag.setDdmTemplateGroupId(ddmTemplate.getGroupId());
			navigationMenuTag.setDdmTemplateKey(ddmTemplate.getTemplateKey());
		}

		navigationMenuTag.setDisplayDepth(0);

		FragmentEntryMenuDisplayConfiguration
			fragmentEntryMenuDisplayConfiguration =
				new FragmentEntryMenuDisplayConfiguration(
					themeDisplay.getCompanyId(), null, scopeGroupId);

		navigationMenuTag.setNavigationMenuMode(
			fragmentEntryMenuDisplayConfiguration.getNavigationMenuMode());
		navigationMenuTag.setRootItemId(
			fragmentEntryMenuDisplayConfiguration.getRootItemId());
		navigationMenuTag.setRootItemLevel(
			fragmentEntryMenuDisplayConfiguration.getRootItemLevel());
		navigationMenuTag.setRootItemType(
			fragmentEntryMenuDisplayConfiguration.getRootItemType());
		navigationMenuTag.setSiteNavigationMenuId(
			fragmentEntryMenuDisplayConfiguration.getSiteNavigationMenuId());

		return navigationMenuTag;
	}

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}