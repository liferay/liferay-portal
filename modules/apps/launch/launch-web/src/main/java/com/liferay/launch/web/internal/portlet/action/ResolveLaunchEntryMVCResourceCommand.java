/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.web.internal.portlet.action;

import com.liferay.launch.entry.type.LaunchEntryType;
import com.liferay.launch.entry.type.registry.LaunchEntryTypeRegistry;
import com.liferay.launch.web.internal.constants.LaunchPortletKeys;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.TreeMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LaunchPortletKeys.LAUNCH,
		"mvc.command.name=/launch/resolve_launch_entry"
	},
	service = MVCResourceCommand.class
)
public class ResolveLaunchEntryMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		LaunchEntryType launchEntryType =
			_launchEntryTypeRegistry.getLaunchEntryType(
				ParamUtil.getString(resourceRequest, "className"));

		if (launchEntryType == null) {
			resourceResponse.setProperty(
				ResourceResponse.HTTP_STATUS_CODE,
				String.valueOf(HttpServletResponse.SC_NOT_FOUND));

			return;
		}

		String selectedItemData = ParamUtil.getString(
			resourceRequest, "selectedItemData");

		JSONObject jsonObject = _jsonFactory.createJSONObject(selectedItemData);

		Map<String, String> selectedItemDataMap = new TreeMap<>(
			String.CASE_INSENSITIVE_ORDER);

		for (String key : jsonObject.keySet()) {
			selectedItemDataMap.put(key, jsonObject.getString(key));
		}

		long classPK = launchEntryType.getClassPK(selectedItemDataMap);

		LaunchEntryType.Version publishedVersion = null;

		if (classPK != 0) {
			publishedVersion = launchEntryType.fetchPublishedVersion(classPK);
		}

		if (publishedVersion == null) {
			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse, JSONUtil.put("classPK", 0));

			return;
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"baseVersionKey", publishedVersion.getVersionKey()
			).put(
				"classPK", classPK
			));
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LaunchEntryTypeRegistry _launchEntryTypeRegistry;

}