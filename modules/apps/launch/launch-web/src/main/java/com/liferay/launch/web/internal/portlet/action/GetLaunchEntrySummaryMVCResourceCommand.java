/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.web.internal.portlet.action;

import com.liferay.launch.entry.type.LaunchEntryType;
import com.liferay.launch.entry.type.registry.LaunchEntryTypeRegistry;
import com.liferay.launch.web.internal.constants.LaunchPortletKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LaunchPortletKeys.LAUNCH,
		"mvc.command.name=/launch/get_launch_entry_summary"
	},
	service = MVCResourceCommand.class
)
public class GetLaunchEntrySummaryMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		String className = ParamUtil.getString(resourceRequest, "className");

		LaunchEntryType launchEntryType =
			_launchEntryTypeRegistry.getLaunchEntryType(className);

		if (launchEntryType == null) {
			resourceResponse.setProperty(
				ResourceResponse.HTTP_STATUS_CODE,
				String.valueOf(HttpServletResponse.SC_NOT_FOUND));

			return;
		}

		long classPK = ParamUtil.getLong(resourceRequest, "classPK");

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Locale locale = themeDisplay.getLocale();

		try {
			JSONObject jsonObject = _getJSONObject(
				classPK, launchEntryType, locale, resourceRequest);

			if (jsonObject == null) {
				resourceResponse.setProperty(
					ResourceResponse.HTTP_STATUS_CODE,
					String.valueOf(HttpServletResponse.SC_NOT_FOUND));

				return;
			}

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse, jsonObject);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Unable to resolve launch entry summary for class ",
						className, " ", classPK),
					portalException);
			}

			resourceResponse.setProperty(
				ResourceResponse.HTTP_STATUS_CODE,
				String.valueOf(HttpServletResponse.SC_NOT_FOUND));
		}
	}

	private JSONObject _getJSONObject(
			long classPK, LaunchEntryType launchEntryType, Locale locale,
			ResourceRequest resourceRequest)
		throws PortalException {

		LaunchEntryType.Item item = launchEntryType.fetchItem(classPK);

		if (item == null) {
			return null;
		}

		Group group = _groupLocalService.getGroup(item.getGroupId());

		JSONObject jsonObject = JSONUtil.put(
			"space", group.getDescriptiveName(locale)
		).put(
			"title", item.getTitle(locale)
		).put(
			"type", item.getTypeName(locale)
		);

		LaunchEntryType.Draft draft = launchEntryType.fetchDraft(
			classPK, ParamUtil.getLong(resourceRequest, "draftId"));

		if (draft != null) {
			return jsonObject.put(
				"author", draft.getUserName()
			).put(
				"editURL",
				draft.getEditURL(
					ParamUtil.getString(resourceRequest, "redirect"),
					RequestBackedPortletURLFactoryUtil.create(resourceRequest))
			).put(
				"modified", draft.getModifiedDate()
			).put(
				"status", draft.getStatus()
			).put(
				"version", _language.get(locale, "draft")
			);
		}

		LaunchEntryType.Version version = launchEntryType.fetchVersion(
			classPK, ParamUtil.getString(resourceRequest, "versionKey"));

		if (version == null) {
			return null;
		}

		return jsonObject.put(
			"author", version.getUserName()
		).put(
			"modified", version.getModifiedDate()
		).put(
			"status", version.getStatus()
		).put(
			"version", version.getLabel(locale)
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetLaunchEntrySummaryMVCResourceCommand.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private LaunchEntryTypeRegistry _launchEntryTypeRegistry;

}