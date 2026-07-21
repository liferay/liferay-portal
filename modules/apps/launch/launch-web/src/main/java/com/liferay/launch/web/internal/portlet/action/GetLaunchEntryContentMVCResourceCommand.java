/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.web.internal.portlet.action;

import com.liferay.launch.web.internal.constants.LaunchPortletKeys;
import com.liferay.launch.web.internal.item.LaunchEntryContent;
import com.liferay.launch.web.internal.item.LaunchEntryContentResolver;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
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

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LaunchPortletKeys.LAUNCH,
		"mvc.command.name=/launch/get_launch_entry_content"
	},
	service = MVCResourceCommand.class
)
public class GetLaunchEntryContentMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, LaunchEntryContentResolver.class,
			"launch.entry.content.resolver.class.name");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		String className = ParamUtil.getString(resourceRequest, "className");

		LaunchEntryContentResolver launchEntryContentResolver =
			_serviceTrackerMap.getService(className);

		if (launchEntryContentResolver == null) {
			resourceResponse.setProperty(
				ResourceResponse.HTTP_STATUS_CODE,
				String.valueOf(HttpServletResponse.SC_NOT_FOUND));

			return;
		}

		long classPK = ParamUtil.getLong(resourceRequest, "classPK");
		String classVersion = ParamUtil.getString(
			resourceRequest, "classVersion");

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			LaunchEntryContent launchEntryContent =
				launchEntryContentResolver.resolve(
					classPK, classVersion, themeDisplay.getLocale());

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				_toJSONObject(
					classVersion, launchEntryContent,
					themeDisplay.getLocale()));
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Unable to resolve launch entry content for class ",
						className, " ", classPK),
					portalException);
			}

			resourceResponse.setProperty(
				ResourceResponse.HTTP_STATUS_CODE,
				String.valueOf(HttpServletResponse.SC_NOT_FOUND));
		}
	}

	private JSONObject _toJSONObject(
			String classVersion, LaunchEntryContent launchEntryContent,
			Locale locale)
		throws PortalException {

		Group group = _groupLocalService.getGroup(
			launchEntryContent.getGroupId());

		return JSONUtil.put(
			"author", launchEntryContent.getUserName()
		).put(
			"modified", launchEntryContent.getModifiedDate()
		).put(
			"space", group.getDescriptiveName(locale)
		).put(
			"status", launchEntryContent.getStatus()
		).put(
			"title", launchEntryContent.getTitle()
		).put(
			"type", launchEntryContent.getTypeName()
		).put(
			"version", classVersion
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetLaunchEntryContentMVCResourceCommand.class);

	@Reference
	private GroupLocalService _groupLocalService;

	private ServiceTrackerMap<String, LaunchEntryContentResolver>
		_serviceTrackerMap;

}