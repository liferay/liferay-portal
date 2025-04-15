/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.microblogs.web.internal.portlet.action;

import com.liferay.microblogs.constants.MicroblogsPortletKeys;
import com.liferay.microblogs.web.internal.util.MicroblogsWebUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author István András Dézsi
 */
@Component(
	property = {
		"jakarta.portlet.name=" + MicroblogsPortletKeys.MICROBLOGS,
		"mvc.command.name=/microblogs/autocomplete_user_mentions"
	},
	service = MVCResourceCommand.class
)
public class AutocompleteUserMentionsMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	public void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		try {
			HttpServletRequest httpServletRequest =
				_portal.getOriginalServletRequest(
					_portal.getHttpServletRequest(resourceRequest));

			long userId = ParamUtil.getLong(httpServletRequest, "userId");

			ThemeDisplay themeDisplay =
				(ThemeDisplay)resourceRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			JSONArray jsonArray = MicroblogsWebUtil.getRecipientsJSONArray(
				userId, themeDisplay);

			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(resourceResponse);

			httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);

			ServletResponseUtil.write(
				httpServletResponse, jsonArray.toString());
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AutocompleteUserMentionsMVCResourceCommand.class);

	@Reference
	private Portal _portal;

}