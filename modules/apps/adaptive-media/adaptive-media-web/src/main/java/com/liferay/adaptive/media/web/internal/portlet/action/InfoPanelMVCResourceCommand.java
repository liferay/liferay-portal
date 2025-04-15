/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.web.internal.portlet.action;

import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.web.internal.constants.AMPortletKeys;
import com.liferay.adaptive.media.web.internal.constants.AMWebKeys;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ambrín Chaudhary
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AMPortletKeys.ADAPTIVE_MEDIA,
		"mvc.command.name=/adaptive_media/info_panel"
	},
	service = MVCResourceCommand.class
)
public class InfoPanelMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		resourceRequest.setAttribute(
			AMWebKeys.CONFIGURATION_ENTRIES_LIST,
			_getAMImageConfigurationEntries(resourceRequest));
		resourceRequest.setAttribute(
			AMWebKeys.SELECTED_CONFIGURATION_ENTRIES,
			_getSelectedAMImageConfigurationEntries(resourceRequest));
		resourceRequest.setAttribute(
			AMWebKeys.TOTAL_IMAGES,
			ParamUtil.getInteger(resourceRequest, "totalImages"));

		include(
			resourceRequest, resourceResponse,
			"/adaptive_media/info_panel.jsp");
	}

	private List<AMImageConfigurationEntry> _getAMImageConfigurationEntries(
		ResourceRequest resourceRequest) {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Collection<AMImageConfigurationEntry> amImageConfigurationEntries =
			_amImageConfigurationHelper.getAMImageConfigurationEntries(
				themeDisplay.getCompanyId(), amImageConfigurationEntry -> true);

		return new ArrayList<>(amImageConfigurationEntries);
	}

	private List<AMImageConfigurationEntry>
		_getSelectedAMImageConfigurationEntries(
			ResourceRequest resourceRequest) {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String[] rowIdsAMImageConfigurationEntry = ParamUtil.getStringValues(
			resourceRequest, "rowIdsAMImageConfigurationEntry");

		return TransformUtil.transformToList(
			rowIdsAMImageConfigurationEntry,
			entryUuid ->
				_amImageConfigurationHelper.getAMImageConfigurationEntry(
					themeDisplay.getCompanyId(), entryUuid));
	}

	@Reference
	private AMImageConfigurationHelper _amImageConfigurationHelper;

}