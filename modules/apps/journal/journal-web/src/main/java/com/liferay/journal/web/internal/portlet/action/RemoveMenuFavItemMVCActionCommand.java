/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.util.JournalHelper;
import com.liferay.journal.web.internal.util.JournalPortletUtil;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/remove_menu_fav_item"
	},
	service = MVCActionCommand.class
)
public class RemoveMenuFavItemMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long ddmStructureId = ParamUtil.getLong(
			actionRequest, "ddmStructureId");

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(actionRequest);

		String key = JournalPortletUtil.getAddMenuFavItemKey(
			_journalHelper, actionRequest);

		String[] addMenuFavItems = portalPreferences.getValues(
			JournalPortletKeys.JOURNAL, key);

		portalPreferences.setValues(
			JournalPortletKeys.JOURNAL, key,
			ArrayUtil.remove(addMenuFavItems, String.valueOf(ddmStructureId)));
	}

	@Reference
	private JournalHelper _journalHelper;

}