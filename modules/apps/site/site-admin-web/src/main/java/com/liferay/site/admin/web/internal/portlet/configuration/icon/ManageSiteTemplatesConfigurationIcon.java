/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.portlet.configuration.icon;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.admin.web.internal.constants.SiteAdminPortletKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "jakarta.portlet.name=" + SiteAdminPortletKeys.SITE_ADMIN,
	service = PortletConfigurationIcon.class
)
public class ManageSiteTemplatesConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "manage-site-template");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			PortletURL manageSiteTemplateURL =
				PortletProviderUtil.getPortletURL(
					portletRequest, LayoutSetPrototype.class.getName(),
					PortletProvider.Action.VIEW);

			if (manageSiteTemplateURL == null) {
				return StringPool.BLANK;
			}

			manageSiteTemplateURL.setParameter(
				"redirect", themeDisplay.getURLCurrent());
			manageSiteTemplateURL.setParameter(
				"backURL", themeDisplay.getURLCurrent());

			return manageSiteTemplateURL.toString();
		}
		catch (PortalException portalException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return StringPool.BLANK;
	}

	@Override
	public double getWeight() {
		return 100;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return PortalPermissionUtil.contains(
			themeDisplay.getPermissionChecker(),
			ActionKeys.ADD_LAYOUT_SET_PROTOTYPE);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ManageSiteTemplatesConfigurationIcon.class);

	@Reference
	private Language _language;

}