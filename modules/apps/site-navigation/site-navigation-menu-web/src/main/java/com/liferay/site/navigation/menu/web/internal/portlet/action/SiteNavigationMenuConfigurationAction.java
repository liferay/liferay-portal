/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.menu.web.internal.portlet.action;

import com.liferay.item.selector.ItemSelector;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.display.template.PortletDisplayTemplate;
import com.liferay.portlet.display.template.portlet.action.BaseConfigurationAction;
import com.liferay.site.navigation.constants.SiteNavigationMenuPortletKeys;
import com.liferay.site.navigation.menu.web.internal.constants.SiteNavigationMenuWebKeys;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuService;
import com.liferay.site.navigation.type.SiteNavigationMenuItemTypeRegistry;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.ReadOnlyException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Douglas Wong
 * @author Raymond Augé
 */
@Component(
	property = "jakarta.portlet.name=" + SiteNavigationMenuPortletKeys.SITE_NAVIGATION_MENU,
	service = ConfigurationAction.class
)
public class SiteNavigationMenuConfigurationAction
	extends BaseConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/configuration.jsp";
	}

	@Override
	public void include(
			PortletConfig portletConfig, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		httpServletRequest.setAttribute(
			SiteNavigationMenuWebKeys.ITEM_SELECTOR, _itemSelector);
		httpServletRequest.setAttribute(
			SiteNavigationMenuWebKeys.SITE_NAVIGATION_MENU_ITEM_TYPE_REGISTRY,
			_siteNavigationMenuItemTypeRegistry);

		super.include(portletConfig, httpServletRequest, httpServletResponse);
	}

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_TEMPLATE, _portletDisplayTemplate);

		super.doDispatch(renderRequest, renderResponse);
	}

	@Override
	protected void postProcess(
			long companyId, PortletRequest portletRequest,
			PortletPreferences portletPreferences)
		throws PortalException {

		super.postProcess(companyId, portletRequest, portletPreferences);

		try {
			portletPreferences.reset("included-layouts");

			_updateRootMenuItemPreferences(portletPreferences, portletRequest);
			_updateSiteNavigationMenuPreferences(
				portletPreferences, portletRequest);
		}
		catch (ReadOnlyException readOnlyException) {
			throw new SystemException(readOnlyException);
		}
	}

	@Reference
	protected GroupLocalService groupLocalService;

	@Reference
	protected SiteNavigationMenuItemLocalService
		siteNavigationMenuItemLocalService;

	@Reference
	protected SiteNavigationMenuService siteNavigationMenuService;

	private void _updateRootMenuItemPreferences(
			PortletPreferences portletPreferences,
			PortletRequest portletRequest)
		throws ReadOnlyException {

		long siteNavigationMenuId = GetterUtil.getLong(
			portletPreferences.getValue("siteNavigationMenuId", null));

		if (siteNavigationMenuId > 0) {
			long rootMenuItemId = GetterUtil.getLong(
				portletPreferences.getValue("rootMenuItemId", null));
			String rootMenuItemType = portletPreferences.getValue(
				"rootMenuItemType", StringPool.BLANK);

			if ((rootMenuItemId == 0) ||
				!Objects.equals(rootMenuItemType, "select")) {

				portletPreferences.reset("rootMenuItemExternalReferenceCode");
				portletPreferences.reset("rootMenuItemId");
			}

			SiteNavigationMenuItem siteNavigationMenuItem =
				siteNavigationMenuItemLocalService.fetchSiteNavigationMenuItem(
					rootMenuItemId);

			if (siteNavigationMenuItem != null) {
				portletPreferences.setValue(
					"rootMenuItemExternalReferenceCode",
					siteNavigationMenuItem.getExternalReferenceCode());

				return;
			}

			portletPreferences.reset("rootMenuItemExternalReferenceCode");
		}
		else {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			String rootMenuItemId = portletPreferences.getValue(
				"rootMenuItemId", null);

			Layout rootLayout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
				rootMenuItemId, themeDisplay.getScopeGroupId(), false);

			if (rootLayout == null) {
				rootLayout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
					rootMenuItemId, themeDisplay.getScopeGroupId(), true);
			}

			if (rootLayout != null) {
				portletPreferences.setValue(
					"rootMenuItemExternalReferenceCode", rootLayout.getUuid());
			}
			else {
				portletPreferences.reset("rootMenuItemExternalReferenceCode");
			}
		}
	}

	private void _updateSiteNavigationMenuPreferences(
			PortletPreferences portletPreferences,
			PortletRequest portletRequest)
		throws PortalException, ReadOnlyException {

		long siteNavigationMenuId = GetterUtil.getLong(
			portletPreferences.getValue("siteNavigationMenuId", null));

		if (siteNavigationMenuId == 0) {
			portletPreferences.reset("siteNavigationMenuExternalReferenceCode");
			portletPreferences.reset(
				"siteNavigationMenuGroupExternalReferenceCode");

			return;
		}

		SiteNavigationMenu siteNavigationMenu =
			siteNavigationMenuService.fetchSiteNavigationMenu(
				siteNavigationMenuId);

		if (siteNavigationMenu != null) {
			portletPreferences.setValue(
				"siteNavigationMenuExternalReferenceCode",
				siteNavigationMenu.getExternalReferenceCode());

			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			if (siteNavigationMenu.getGroupId() ==
					themeDisplay.getScopeGroupId()) {

				portletPreferences.reset(
					"siteNavigationMenuGroupExternalReferenceCode");
			}
			else {
				Group group = groupLocalService.getGroup(
					siteNavigationMenu.getGroupId());

				portletPreferences.setValue(
					"siteNavigationMenuGroupExternalReferenceCode",
					group.getExternalReferenceCode());
			}

			return;
		}

		portletPreferences.reset("siteNavigationMenuExternalReferenceCode");
		portletPreferences.reset(
			"siteNavigationMenuGroupExternalReferenceCode");
	}

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private PortletDisplayTemplate _portletDisplayTemplate;

	@Reference
	private SiteNavigationMenuItemTypeRegistry
		_siteNavigationMenuItemTypeRegistry;

}