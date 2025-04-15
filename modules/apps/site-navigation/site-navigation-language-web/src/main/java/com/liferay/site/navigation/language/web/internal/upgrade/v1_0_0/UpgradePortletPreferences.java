/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.language.web.internal.upgrade.v1_0_0;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.upgrade.BasePortletPreferencesUpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.display.template.constants.PortletDisplayTemplateConstants;
import com.liferay.site.navigation.language.constants.SiteNavigationLanguagePortletKeys;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.ReadOnlyException;

/**
 * @author Eduardo García
 */
public class UpgradePortletPreferences
	extends BasePortletPreferencesUpgradeProcess {

	@Override
	protected String[] getPortletIds() {
		return new String[] {
			SiteNavigationLanguagePortletKeys.SITE_NAVIGATION_LANGUAGE
		};
	}

	@SuppressWarnings("deprecation")
	protected void upgradeDisplayStyle(PortletPreferences portletPreferences)
		throws ReadOnlyException {

		String displayStyleString = portletPreferences.getValue(
			"displayStyle", null);

		if (Validator.isNull(displayStyleString)) {
			return;
		}

		int displayStyle = GetterUtil.getInteger(displayStyleString);

		if (displayStyle == LIST_ICON) {
			portletPreferences.setValue(
				"displayStyle",
				PortletDisplayTemplateConstants.DISPLAY_STYLE_PREFIX +
					"language-icon-ftl");
		}
		else if (displayStyle == LIST_LONG_TEXT) {
			portletPreferences.setValue(
				"displayStyle",
				PortletDisplayTemplateConstants.DISPLAY_STYLE_PREFIX +
					"language-long-text-ftl");
		}
		else if (displayStyle == LIST_SHORT_TEXT) {
			portletPreferences.setValue(
				"displayStyle",
				PortletDisplayTemplateConstants.DISPLAY_STYLE_PREFIX +
					"language-short-text-ftl");
		}
		else if (displayStyle == SELECT_BOX) {
			portletPreferences.setValue(
				"displayStyle",
				PortletDisplayTemplateConstants.DISPLAY_STYLE_PREFIX +
					"language-select-box-ftl");
		}
		else {
			portletPreferences.reset("displayStyle");

			if (_log.isWarnEnabled()) {
				_log.warn(
					"Display styles for languages are deprecated in favor of " +
						"widget templates");
			}
		}
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		upgradeDisplayStyle(portletPreferences);

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

	protected final int LIST_ICON = 0;

	protected final int LIST_LONG_TEXT = 1;

	protected final int LIST_SHORT_TEXT = 2;

	protected final int SELECT_BOX = 3;

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradePortletPreferences.class);

}