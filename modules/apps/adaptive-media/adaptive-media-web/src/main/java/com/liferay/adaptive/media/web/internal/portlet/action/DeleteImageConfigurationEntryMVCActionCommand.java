/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.web.internal.portlet.action;

import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.web.internal.constants.AMPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AMPortletKeys.ADAPTIVE_MEDIA,
		"mvc.command.name=/adaptive_media/delete_image_configuration_entry"
	},
	service = MVCActionCommand.class
)
public class DeleteImageConfigurationEntryMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doPermissionCheckedProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String[] deleteAMImageConfigurationEntryUuids =
			ParamUtil.getStringValues(
				actionRequest, "rowIdsAMImageConfigurationEntry");

		List<AMImageConfigurationEntry> deletedAMImageConfigurationEntries =
			new ArrayList<>();

		for (String deleteAMImageConfigurationEntryUuid :
				deleteAMImageConfigurationEntryUuids) {

			AMImageConfigurationEntry amImageConfigurationEntry =
				_amImageConfigurationHelper.getAMImageConfigurationEntry(
					themeDisplay.getCompanyId(),
					deleteAMImageConfigurationEntryUuid);

			_amImageConfigurationHelper.deleteAMImageConfigurationEntry(
				themeDisplay.getCompanyId(),
				deleteAMImageConfigurationEntryUuid);

			if (amImageConfigurationEntry != null) {
				deletedAMImageConfigurationEntries.add(
					amImageConfigurationEntry);
			}
		}

		SessionMessages.add(
			actionRequest, "configurationEntriesDeleted",
			deletedAMImageConfigurationEntries);
	}

	@Reference
	private AMImageConfigurationHelper _amImageConfigurationHelper;

}