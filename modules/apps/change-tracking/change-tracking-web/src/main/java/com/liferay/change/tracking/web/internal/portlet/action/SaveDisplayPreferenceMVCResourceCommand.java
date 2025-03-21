/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;

/**
 * @author Samuel Trong Tran
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/save_display_preference"
	},
	service = MVCResourceCommand.class
)
public class SaveDisplayPreferenceMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse) {

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(resourceRequest);

		String key = ParamUtil.getString(resourceRequest, "key");
		String value = ParamUtil.getString(resourceRequest, "value");

		if (Objects.equals(key, "hideContextChangeWarningDuration")) {
			key = "hideContextChangeWarningExpiryTime";

			long hideContextChangeWarningExpiryTime = GetterUtil.getLong(value);

			if (hideContextChangeWarningExpiryTime > 0) {
				long currentTime = System.currentTimeMillis();

				value = String.valueOf(
					currentTime +
						TimeUnit.HOURS.toMillis(
							hideContextChangeWarningExpiryTime));
			}
		}

		portalPreferences.setValue(CTPortletKeys.PUBLICATIONS, key, value);
	}

}