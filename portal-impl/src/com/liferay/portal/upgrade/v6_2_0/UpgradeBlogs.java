/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v6_2_0;

import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.upgrade.v6_2_0.util.RSSUtil;

import jakarta.portlet.PortletPreferences;

/**
 * @author Sergio González
 * @author Eduardo García
 */
public class UpgradeBlogs extends BaseUpgradePortletPreferences {

	@Override
	protected void doUpgrade() throws Exception {
		super.doUpgrade();

		updateEntries();
		updateStatus();
	}

	@Override
	protected String[] getPortletIds() {
		return new String[] {"33"};
	}

	protected void updateEntries() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			alterColumnType("BlogsEntry", "description", "STRING null");
		}
	}

	protected void updateStatus() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			runSQL(
				"update BlogsEntry set status = " +
					WorkflowConstants.STATUS_APPROVED +
						" where status is null");
		}
	}

	protected void upgradeDisplayStyle(PortletPreferences portletPreferences)
		throws Exception {

		String pageDisplayStyle = GetterUtil.getString(
			portletPreferences.getValue("pageDisplayStyle", null));

		if (Validator.isNotNull(pageDisplayStyle)) {
			portletPreferences.setValue("displayStyle", pageDisplayStyle);
		}

		portletPreferences.reset("pageDisplayStyle");
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
		upgradeRss(portletPreferences);

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

	protected void upgradeRss(PortletPreferences portletPreferences)
		throws Exception {

		String rssFormat = GetterUtil.getString(
			portletPreferences.getValue("rssFormat", null));

		if (Validator.isNotNull(rssFormat)) {
			String rssFormatType = RSSUtil.getFormatType(rssFormat);
			double rssFormatVersion = RSSUtil.getFormatVersion(rssFormat);

			String rssFeedType = RSSUtil.getFeedType(
				rssFormatType, rssFormatVersion);

			portletPreferences.setValue("rssFeedType", rssFeedType);
		}

		portletPreferences.reset("rssFormat");
	}

}