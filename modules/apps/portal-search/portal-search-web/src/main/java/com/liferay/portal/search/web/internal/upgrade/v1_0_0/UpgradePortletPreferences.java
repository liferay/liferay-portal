/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.upgrade.v1_0_0;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.upgrade.BasePortletPreferencesUpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.web.constants.SearchPortletKeys;
import com.liferay.portal.search.web.internal.display.context.SearchScopePreference;

import jakarta.portlet.PortletPreferences;

/**
 * @author Julio Camarero
 */
public class UpgradePortletPreferences
	extends BasePortletPreferencesUpgradeProcess {

	@Override
	protected String[] getPortletIds() {
		return new String[] {SearchPortletKeys.SEARCH};
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		portletPreferences.setValue(
			"searchScope",
			SearchScopePreference.LET_THE_USER_CHOOSE.getPreferenceString());

		_upgradeSearchConfiguration(portletPreferences);

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

	private void _upgradeSearchConfiguration(
			PortletPreferences portletPreferences)
		throws Exception {

		String searchConfiguration = portletPreferences.getValue(
			"searchConfiguration", StringPool.BLANK);

		for (String[] classNames : _CLASS_NAMES) {
			searchConfiguration = StringUtil.replace(
				searchConfiguration, classNames[0], classNames[1]);
		}

		portletPreferences.setValue("searchConfiguration", searchConfiguration);
	}

	private static final String[][] _CLASS_NAMES = {
		{
			"com.liferay.portlet.bookmarks.model.BookmarksEntry",
			"com.liferay.bookmarks.model.BookmarksEntry"
		},
		{
			"com.liferay.portlet.bookmarks.model.BookmarksFolder",
			"com.liferay.bookmarks.model.BookmarksFolder"
		},
		{
			"com.liferay.portlet.dynamicdatalists.model.DDLRecord",
			"com.liferay.dynamic.data.list.model.DDLRecord"
		},
		{
			"com.liferay.portlet.documentlibrary.model.DLFileEntry",
			"com.liferay.document.library.kernel.model.DLFileEntry"
		},
		{
			"com.liferay.portlet.documentlibrary.model.DLFolder",
			"com.liferay.document.library.kernel.model.DLFolder"
		},
		{
			"com.liferay.portlet.journal.model.JournalArticle",
			"com.liferay.journal.model.JournalArticle"
		},
		{
			"com.liferay.portlet.journal.model.JournalFolder",
			"com.liferay.journal.model.JournalFolder"
		},
		{
			"com.liferay.portlet.messageboards.model.MBCategory",
			"com.liferay.message.boards.kernel.model.MBCategory"
		},
		{
			"com.liferay.portlet.messageboards.model.MBMessage",
			"com.liferay.message.boards.kernel.model.MBMessage"
		},
		{
			"com.liferay.portlet.wiki.model.WikiPage",
			"com.liferay.wiki.model.WikiPage"
		}
	};

}