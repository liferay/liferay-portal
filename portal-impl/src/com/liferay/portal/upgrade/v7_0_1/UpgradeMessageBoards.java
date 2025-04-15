/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_0_1;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.upgrade.BasePortletPreferencesUpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.PortletPreferences;

import java.util.Locale;
import java.util.Set;

/**
 * @author Roberto Díaz
 */
public class UpgradeMessageBoards extends BasePortletPreferencesUpgradeProcess {

	@Override
	protected String[] getPortletIds() {
		return new String[] {
			PortletKeys.MESSAGE_BOARDS, PortletKeys.MESSAGE_BOARDS_ADMIN
		};
	}

	protected void upgradeLocalizedThreadPriorities(
			PortletPreferences portletPreferences)
		throws Exception {

		Set<Locale> availableLocales = LanguageUtil.getAvailableLocales();

		for (Locale availableLocale : availableLocales) {
			String key =
				"priorities_" + LanguageUtil.getLanguageId(availableLocale);

			String[] oldThreadPriorities = portletPreferences.getValues(
				key, StringPool.EMPTY_ARRAY);

			if (ArrayUtil.isEmpty(oldThreadPriorities)) {
				continue;
			}

			String[] newThreadPriorities =
				new String[oldThreadPriorities.length];

			for (int i = 0; i < oldThreadPriorities.length; i++) {
				newThreadPriorities[i] = StringUtil.replace(
					oldThreadPriorities[i], CharPool.COMMA, CharPool.PIPE);
			}

			portletPreferences.setValues(key, newThreadPriorities);
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

		upgradeLocalizedThreadPriorities(portletPreferences);

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

}