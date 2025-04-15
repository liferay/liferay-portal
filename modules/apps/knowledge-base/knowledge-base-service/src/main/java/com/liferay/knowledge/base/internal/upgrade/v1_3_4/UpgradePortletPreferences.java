/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.internal.upgrade.v1_3_4;

import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.upgrade.BasePortletPreferencesUpgradeProcess;

import jakarta.portlet.PortletPreferences;

/**
 * @author Adolfo Pérez
 */
public class UpgradePortletPreferences
	extends BasePortletPreferencesUpgradeProcess {

	@Override
	protected String[] getPortletIds() {
		return _PORTLET_IDS;
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		for (String[] preferenceName : _PREFERENCE_NAMES) {
			String sourcePreferenceName = preferenceName[0];
			String targetPreferenceName = preferenceName[1];

			String value = portletPreferences.getValue(
				sourcePreferenceName, null);

			portletPreferences.setValue(targetPreferenceName, value);

			portletPreferences.reset(sourcePreferenceName);
		}

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

	private static final String[] _PORTLET_IDS = {"1_WAR_knowledgebaseportlet"};

	private static final String[][] _PREFERENCE_NAMES = {
		{
			"emailKBArticleFeedbackInProgressEnabled",
			"emailKBArticleSuggestionInProgressEnabled"
		},
		{
			"emailKBArticleFeedbackInProgressSubject",
			"emailKBArticleSuggestionInProgressSubject"
		},
		{
			"emailKBArticleFeedbackInProgressBody",
			"emailKBArticleSuggestionInProgressBody"
		},
		{
			"emailKBArticleFeedbackReceivedEnabled",
			"emailKBArticleSuggestionReceivedEnabled"
		},
		{
			"emailKBArticleFeedbackReceivedSubject",
			"emailKBArticleSuggestionReceivedSubject"
		},
		{
			"emailKBArticleFeedbackReceivedBody",
			"emailKBArticleSuggestionReceivedBody"
		},
		{
			"emailKBArticleFeedbackResolvedEnabled",
			"emailKBArticleSuggestionResolvedEnabled"
		},
		{
			"emailKBArticleFeedbackResolvedSubject",
			"emailKBArticleSuggestionResolvedSubject"
		},
		{
			"emailKBArticleFeedbackResolvedBody",
			"emailKBArticleSuggestionResolvedBody"
		}
	};

}