/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.web.internal.portlet.action;

import com.liferay.adaptive.media.constants.AMOptimizeImagesBackgroundTaskConstants;
import com.liferay.adaptive.media.image.service.AMImageEntryLocalService;
import com.liferay.adaptive.media.web.internal.background.task.OptimizeImagesAllConfigurationsBackgroundTaskExecutor;
import com.liferay.adaptive.media.web.internal.background.task.OptimizeImagesSingleConfigurationBackgroundTaskExecutor;
import com.liferay.adaptive.media.web.internal.constants.AMPortletKeys;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ambrín Chaudhary
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AMPortletKeys.ADAPTIVE_MEDIA,
		"mvc.command.name=/adaptive_media/adapted_images_percentage"
	},
	service = MVCResourceCommand.class
)
public class AdaptedImagesPercentageMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String entryUuid = ParamUtil.getString(resourceRequest, "entryUuid");

		int entriesCount = _amImageEntryLocalService.getAMImageEntriesCount(
			themeDisplay.getCompanyId(), entryUuid);

		int expectedEntriesCount =
			_amImageEntryLocalService.getExpectedAMImageEntriesCount(
				themeDisplay.getCompanyId());

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			_getJSONObject(entryUuid, entriesCount, expectedEntriesCount));
	}

	private JSONObject _getJSONObject(
		String entryUuid, int entriesCount, int expectedEntriesCount) {

		if (_isTaskInProgress(entryUuid)) {
			return JSONUtil.put(
				"adaptedImages", entriesCount
			).put(
				"errors", 0
			).put(
				"totalImages", expectedEntriesCount
			);
		}

		return JSONUtil.put(
			"adaptedImages", entriesCount
		).put(
			"errors", expectedEntriesCount - entriesCount
		).put(
			"totalImages", expectedEntriesCount
		);
	}

	private boolean _isTaskInProgress(String entryUuid) {
		List<BackgroundTask> backgroundTasks =
			_backgroundTaskManager.getBackgroundTasks(
				CompanyConstants.SYSTEM,
				OptimizeImagesSingleConfigurationBackgroundTaskExecutor.class.
					getName(),
				BackgroundTaskConstants.STATUS_IN_PROGRESS);

		for (BackgroundTask backgroundTask : backgroundTasks) {
			Map<String, Serializable> taskContextMap =
				backgroundTask.getTaskContextMap();

			if (entryUuid.equals(
					taskContextMap.get(
						AMOptimizeImagesBackgroundTaskConstants.
							CONFIGURATION_ENTRY_UUID))) {

				return true;
			}
		}

		backgroundTasks = _backgroundTaskManager.getBackgroundTasks(
			CompanyConstants.SYSTEM,
			OptimizeImagesAllConfigurationsBackgroundTaskExecutor.class.
				getName(),
			BackgroundTaskConstants.STATUS_IN_PROGRESS);

		return ListUtil.isNotEmpty(backgroundTasks);
	}

	@Reference
	private AMImageEntryLocalService _amImageEntryLocalService;

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

}