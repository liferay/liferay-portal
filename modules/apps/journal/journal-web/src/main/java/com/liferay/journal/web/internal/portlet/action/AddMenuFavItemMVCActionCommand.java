/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.exception.MaxAddMenuFavItemsException;
import com.liferay.journal.util.JournalHelper;
import com.liferay.journal.web.internal.configuration.JournalWebConfiguration;
import com.liferay.journal.web.internal.util.JournalPortletUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	configurationPid = "com.liferay.journal.web.internal.configuration.JournalWebConfiguration",
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/add_menu_fav_item"
	},
	service = MVCActionCommand.class
)
public class AddMenuFavItemMVCActionCommand extends BaseMVCActionCommand {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_journalWebConfiguration = ConfigurableUtil.createConfigurable(
			JournalWebConfiguration.class, properties);
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(actionRequest);

		String key = JournalPortletUtil.getAddMenuFavItemKey(
			_journalHelper, actionRequest);

		String[] addMenuFavItems = portalPreferences.getValues(
			JournalPortletKeys.JOURNAL, key, new String[0]);

		if (addMenuFavItems.length >=
				_journalWebConfiguration.maxAddMenuItems()) {

			hideDefaultErrorMessage(actionRequest);

			throw new MaxAddMenuFavItemsException();
		}

		long ddmStructureId = ParamUtil.getLong(
			actionRequest, "ddmStructureId");

		portalPreferences.setValues(
			JournalPortletKeys.JOURNAL, key,
			ArrayUtil.append(addMenuFavItems, String.valueOf(ddmStructureId)));
	}

	@Reference
	private JournalHelper _journalHelper;

	private volatile JournalWebConfiguration _journalWebConfiguration;

}