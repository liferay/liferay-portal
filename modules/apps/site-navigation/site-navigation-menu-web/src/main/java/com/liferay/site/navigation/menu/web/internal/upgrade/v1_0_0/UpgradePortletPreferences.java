/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.menu.web.internal.upgrade.v1_0_0;

import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.upgrade.BasePortletPreferencesUpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portlet.display.template.constants.PortletDisplayTemplateConstants;
import com.liferay.site.navigation.constants.SiteNavigationMenuPortletKeys;

import jakarta.portlet.PortletPreferences;

import java.util.Arrays;
import java.util.List;

/**
 * @author Eduardo García
 */
public class UpgradePortletPreferences
	extends BasePortletPreferencesUpgradeProcess {

	@Override
	protected String[] getPortletIds() {
		return new String[] {
			SiteNavigationMenuPortletKeys.SITE_NAVIGATION_MENU + "%"
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

	private void _persistSupportedProperties(
			PortletPreferences portletPreferences, String displayStyle)
		throws Exception {

		String includedLayouts = "auto";
		String rootLayoutLevel = "1";
		String rootLayoutType = "absolute";

		if (displayStyle.equals("[custom]")) {
			includedLayouts = portletPreferences.getValue(
				"includedLayouts", includedLayouts);
			rootLayoutLevel = portletPreferences.getValue(
				"rootLayoutLevel", rootLayoutLevel);
			rootLayoutType = portletPreferences.getValue(
				"rootLayoutType", rootLayoutType);
		}
		else {
			String[] displayStyleDefinition = PropsUtil.getArray(
				"navigation.display.style", new Filter(displayStyle));

			if ((displayStyleDefinition != null) &&
				(displayStyleDefinition.length != 0)) {

				includedLayouts = displayStyleDefinition[3];
				rootLayoutLevel = displayStyleDefinition[2];
				rootLayoutType = displayStyleDefinition[1];
			}
			else {
				if (displayStyle.equals("from-level-0")) {
					rootLayoutLevel = "0";
				}
				else if (displayStyle.equals("from-level-1-to-all-sublevels")) {
					includedLayouts = "all";
				}
				else if (displayStyle.equals("from-level-2-with-title")) {
					rootLayoutLevel = "2";
				}
				else if (displayStyle.equals("relative-with-breadcrumb")) {
					rootLayoutLevel = "0";
					rootLayoutType = "relative";
				}
			}
		}

		portletPreferences.setValue("includedLayouts", includedLayouts);
		portletPreferences.setValue("rootLayoutLevel", rootLayoutLevel);
		portletPreferences.setValue("rootLayoutType", rootLayoutType);
	}

	private void _removeUnsupportedPreferences(
			PortletPreferences portletPreferences)
		throws Exception {

		portletPreferences.reset("bulletStyle");
		portletPreferences.reset("headerType");
		portletPreferences.reset("nestedChildren");
	}

	private void _upgradeDisplayStyle(PortletPreferences portletPreferences)
		throws Exception {

		String displayStyle = GetterUtil.getString(
			portletPreferences.getValue("displayStyle", null));

		List<String> displayStyleOutOfTheBox = Arrays.asList(
			"[custom]", "relative-with-breadcrumb", "from-level-2-with-title",
			"from-level-1-with-title", "from-level-1",
			"from-level-1-to-all-sublevels", "from-level-0");

		if (Validator.isNull(displayStyle) ||
			displayStyle.startsWith(
				PortletDisplayTemplateConstants.DISPLAY_STYLE_PREFIX) ||
			!displayStyleOutOfTheBox.contains(displayStyle)) {

			return;
		}

		portletPreferences.setValue(
			"displayStyle",
			PortletDisplayTemplateConstants.DISPLAY_STYLE_PREFIX +
				"list-menu-ftl");

		_persistSupportedProperties(portletPreferences, displayStyle);

		_removeUnsupportedPreferences(portletPreferences);
	}

}