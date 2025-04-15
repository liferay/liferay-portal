/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.bar.web.internal.portlet.action;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.staging.bar.web.internal.portlet.constants.StagingBarPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.io.IOException;

/**
 * @author Máté Thurzó
 */
public class ActionUtil {

	public static void addLayoutBranchSessionMessages(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException {

		if (SessionErrors.isEmpty(actionRequest)) {
			SessionMessages.add(
				actionRequest,
				PortalUtil.getPortletId(actionRequest) +
					SessionMessages.KEY_SUFFIX_REFRESH_PORTLET,
				StagingBarPortletKeys.STAGING_BAR);

			SessionMessages.add(
				actionRequest,
				PortalUtil.getPortletId(actionRequest) +
					SessionMessages.KEY_SUFFIX_REFRESH_PORTLET_DATA,
				HashMapBuilder.put(
					"preventNotification", Boolean.TRUE.toString()
				).build());
		}

		String redirect = PortalUtil.escapeRedirect(
			ParamUtil.getString(actionRequest, "redirect"));

		actionResponse.sendRedirect(redirect);
	}

}