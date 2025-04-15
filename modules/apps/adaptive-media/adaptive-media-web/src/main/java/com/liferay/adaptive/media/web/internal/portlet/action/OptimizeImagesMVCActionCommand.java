/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.web.internal.portlet.action;

import com.liferay.adaptive.media.constants.AMOptimizeImagesBackgroundTaskConstants;
import com.liferay.adaptive.media.web.internal.background.task.OptimizeImagesAllConfigurationsBackgroundTaskExecutor;
import com.liferay.adaptive.media.web.internal.background.task.OptimizeImagesSingleConfigurationBackgroundTaskExecutor;
import com.liferay.adaptive.media.web.internal.constants.AMPortletKeys;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskContextMapConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AMPortletKeys.ADAPTIVE_MEDIA,
		"mvc.command.name=/adaptive_media/optimize_images"
	},
	service = MVCActionCommand.class
)
public class OptimizeImagesMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doPermissionCheckedProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String entryUuid = ParamUtil.getString(actionRequest, "entryUuid");

		String jobName = "optimizeImages-".concat(PortalUUIDUtil.generate());

		if (Validator.isNotNull(entryUuid)) {
			_optimizeImagesSingleConfiguration(
				themeDisplay.getUserId(), jobName, entryUuid);
		}
		else {
			_optimizeImages(themeDisplay.getUserId(), jobName);
		}

		SessionMessages.add(actionRequest, "optimizeImages");
	}

	private BackgroundTask _optimizeImages(long userId, String jobName)
		throws Exception {

		Map<String, Serializable> taskContextMap =
			HashMapBuilder.<String, Serializable>put(
				BackgroundTaskContextMapConstants.DELETE_ON_SUCCESS, true
			).build();

		try {
			return _backgroundTaskManager.addBackgroundTask(
				userId, CompanyConstants.SYSTEM, jobName,
				OptimizeImagesAllConfigurationsBackgroundTaskExecutor.class.
					getName(),
				taskContextMap, new ServiceContext());
		}
		catch (PortalException portalException) {
			throw new PortalException(
				"Unable to schedule adaptive media images optimization",
				portalException);
		}
	}

	private BackgroundTask _optimizeImagesSingleConfiguration(
			long userId, String jobName, String configurationEntryUuid)
		throws Exception {

		Map<String, Serializable> taskContextMap =
			HashMapBuilder.<String, Serializable>put(
				AMOptimizeImagesBackgroundTaskConstants.
					CONFIGURATION_ENTRY_UUID,
				configurationEntryUuid
			).put(
				BackgroundTaskContextMapConstants.DELETE_ON_SUCCESS, true
			).build();

		try {
			return _backgroundTaskManager.addBackgroundTask(
				userId, CompanyConstants.SYSTEM, jobName,
				OptimizeImagesSingleConfigurationBackgroundTaskExecutor.class.
					getName(),
				taskContextMap, new ServiceContext());
		}
		catch (PortalException portalException) {
			throw new PortalException(
				"Unable to schedule adaptive media images optimization for " +
					"configuration " + configurationEntryUuid,
				portalException);
		}
	}

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

}