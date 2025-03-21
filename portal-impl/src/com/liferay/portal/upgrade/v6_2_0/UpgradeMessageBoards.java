/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v6_2_0;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.upgrade.v6_2_0.util.RSSUtil;

import jakarta.portlet.PortletPreferences;

/**
 * @author Eduardo García
 * @author Daniel Kocsis
 */
public class UpgradeMessageBoards extends BaseUpgradePortletPreferences {

	@Override
	protected void doUpgrade() throws Exception {
		super.doUpgrade();
	}

	@Override
	protected String[] getPortletIds() {
		return new String[] {"19"};
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		String rssFormat = GetterUtil.getString(
			portletPreferences.getValue("rssFormat", null));

		if (Validator.isNotNull(rssFormat)) {
			String rssFeedType = RSSUtil.getFeedType(
				RSSUtil.getFormatType(rssFormat),
				RSSUtil.getFormatVersion(rssFormat));

			portletPreferences.setValue("rssFeedType", rssFeedType);
		}

		portletPreferences.reset("rssFormat");

		portletPreferences = upgradeSubscriptionSubject(
			"emailMessageAddedSubject", "emailMessageAddedSubjectPrefix",
			portletPreferences);

		portletPreferences = upgradeSubscriptionSubject(
			"emailMessageUpdatedSubject", "emailMessageUpdatedSubjectPrefix",
			portletPreferences);

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

	protected PortletPreferences upgradeSubscriptionSubject(
			String subjectName, String subjectPrefixName,
			PortletPreferences portletPreferences)
		throws Exception {

		String subjectPrefix = GetterUtil.getString(
			portletPreferences.getValue(subjectPrefixName, StringPool.BLANK));

		if (Validator.isNotNull(subjectPrefix)) {
			String subject = subjectPrefix;

			if (!subjectPrefix.contains("[$MESSAGE_SUBJECT$]")) {
				subject = subject.concat(" [$MESSAGE_SUBJECT$]");
			}

			portletPreferences.setValue(subjectName, subject);
		}

		portletPreferences.reset(subjectPrefixName);

		return portletPreferences;
	}

}