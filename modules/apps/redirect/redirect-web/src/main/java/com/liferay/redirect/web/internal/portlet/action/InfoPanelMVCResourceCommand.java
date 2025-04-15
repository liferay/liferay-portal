/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.redirect.model.RedirectEntry;
import com.liferay.redirect.service.RedirectEntryLocalService;
import com.liferay.redirect.web.internal.constants.RedirectPortletKeys;
import com.liferay.redirect.web.internal.display.context.RedirectEntryInfoPanelDisplayContext;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"jakarta.portlet.name=" + RedirectPortletKeys.REDIRECT,
		"mvc.command.name=/redirect/info_panel"
	},
	service = MVCResourceCommand.class
)
public class InfoPanelMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		List<RedirectEntry> redirectEntries = new ArrayList<>();

		for (long redirectEntryId :
				ParamUtil.getLongValues(resourceRequest, "rowIds")) {

			redirectEntries.add(
				_redirectEntryLocalService.fetchRedirectEntry(redirectEntryId));
		}

		resourceRequest.setAttribute(
			RedirectEntryInfoPanelDisplayContext.class.getName(),
			new RedirectEntryInfoPanelDisplayContext(
				_portal.getLiferayPortletRequest(resourceRequest),
				redirectEntries));

		include(resourceRequest, resourceResponse, "/info_panel.jsp");
	}

	@Reference
	private Portal _portal;

	@Reference
	private RedirectEntryLocalService _redirectEntryLocalService;

}