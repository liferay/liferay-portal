/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.web.internal.portlet.action;

import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.image.service.AMImageEntryLocalService;
import com.liferay.adaptive.media.web.internal.constants.AMPortletKeys;
import com.liferay.adaptive.media.web.internal.constants.AMWebKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AMPortletKeys.ADAPTIVE_MEDIA,
		"mvc.command.name=/adaptive_media/edit_image_configuration_entry"
	},
	service = MVCRenderCommand.class
)
public class EditImageConfigurationEntryMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String entryUuid = ParamUtil.getString(renderRequest, "entryUuid");

		AMImageConfigurationEntry amImageConfigurationEntry =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				themeDisplay.getCompanyId(), entryUuid);

		boolean configurationEntryEditable = true;

		if (amImageConfigurationEntry != null) {
			int entriesCount = _amImageEntryLocalService.getAMImageEntriesCount(
				themeDisplay.getCompanyId(),
				amImageConfigurationEntry.getUUID());

			if (entriesCount != 0) {
				configurationEntryEditable = false;
			}
		}

		renderRequest.setAttribute(
			AMWebKeys.CONFIGURATION_ENTRY, amImageConfigurationEntry);
		renderRequest.setAttribute(
			AMWebKeys.CONFIGURATION_ENTRY_EDITABLE, configurationEntryEditable);

		return "/adaptive_media/edit_image_configuration_entry.jsp";
	}

	@Reference
	private AMImageConfigurationHelper _amImageConfigurationHelper;

	@Reference
	private AMImageEntryLocalService _amImageEntryLocalService;

}