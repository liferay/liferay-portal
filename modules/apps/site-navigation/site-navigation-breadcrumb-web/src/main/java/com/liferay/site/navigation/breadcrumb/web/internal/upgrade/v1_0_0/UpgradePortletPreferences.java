/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.breadcrumb.web.internal.upgrade.v1_0_0;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.upgrade.BasePortletPreferencesUpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.display.template.constants.PortletDisplayTemplateConstants;
import com.liferay.site.navigation.breadcrumb.web.internal.constants.SiteNavigationBreadcrumbPortletKeys;

import jakarta.portlet.PortletPreferences;

/**
 * @author Julio Camarero
 */
public class UpgradePortletPreferences
	extends BasePortletPreferencesUpgradeProcess {

	@Override
	protected String[] getPortletIds() {
		return new String[] {
			StringUtil.quote(
				SiteNavigationBreadcrumbPortletKeys.SITE_NAVIGATION_BREADCRUMB,
				CharPool.PERCENT)
		};
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		_upgradeDisplayStyle(portletPreferences);

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

	private void _upgradeDisplayStyle(PortletPreferences portletPreferences)
		throws Exception {

		String displayStyle = GetterUtil.getString(
			portletPreferences.getValue("displayStyle", null));

		if (Validator.isNull(displayStyle) ||
			displayStyle.startsWith(
				PortletDisplayTemplateConstants.DISPLAY_STYLE_PREFIX)) {

			return;
		}

		if (displayStyle.equals("horizontal") || displayStyle.equals("1")) {
			portletPreferences.setValue(
				"displayStyle",
				PortletDisplayTemplateConstants.DISPLAY_STYLE_PREFIX +
					"breadcrumb-horizontal-ftl");
		}
		else if (displayStyle.equals("vertical") || displayStyle.equals("2")) {
			portletPreferences.setValue(
				"displayStyle",
				PortletDisplayTemplateConstants.DISPLAY_STYLE_PREFIX +
					"breadcrumb-vertical-ftl");
		}
		else {
			portletPreferences.reset("displayStyle");

			if (_log.isWarnEnabled()) {
				_log.warn(
					"Display styles for breadcrumbs are deprecated in favor " +
						"of widget templates");
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradePortletPreferences.class);

}