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

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AMPortletKeys.ADAPTIVE_MEDIA,
		"mvc.command.name=/", "mvc.command.name=/adaptive_media/view"
	},
	service = MVCRenderCommand.class
)
public class ViewMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Collection<AMImageConfigurationEntry> amImageConfigurationEntries =
			_getAMImageConfigurationEntries(renderRequest, themeDisplay);

		renderRequest.setAttribute(
			AMWebKeys.CONFIGURATION_ENTRIES_LIST,
			new ArrayList<>(amImageConfigurationEntries));

		renderRequest.setAttribute(
			AMWebKeys.TOTAL_IMAGES,
			_amImageEntryLocalService.getExpectedAMImageEntriesCount(
				themeDisplay.getCompanyId()));

		return "/adaptive_media/view.jsp";
	}

	private Collection<AMImageConfigurationEntry>
		_getAMImageConfigurationEntries(
			RenderRequest renderRequest, ThemeDisplay themeDisplay) {

		String entriesNavigation = ParamUtil.getString(
			renderRequest, "entriesNavigation", "all");

		Predicate<AMImageConfigurationEntry> predicate = null;

		if (entriesNavigation.equals("enabled")) {
			predicate =
				amImageConfigurationEntry ->
					amImageConfigurationEntry.isEnabled();
		}
		else if (entriesNavigation.equals("disabled")) {
			predicate =
				amImageConfigurationEntry ->
					!amImageConfigurationEntry.isEnabled();
		}
		else {
			predicate = amImageConfigurationEntry -> true;
		}

		return _amImageConfigurationHelper.getAMImageConfigurationEntries(
			themeDisplay.getCompanyId(), predicate);
	}

	@Reference
	private AMImageConfigurationHelper _amImageConfigurationHelper;

	@Reference
	private AMImageEntryLocalService _amImageEntryLocalService;

}