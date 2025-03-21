/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.background.task.display;

import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Map;

/**
 * @author Akos Thurzo
 */
public class LayoutStagingBackgroundTaskDisplay
	extends ExportImportBackgroundTaskDisplay {

	public LayoutStagingBackgroundTaskDisplay(BackgroundTask backgroundTask) {
		super(backgroundTask);

		Map<String, Serializable> contextMap =
			backgroundTask.getTaskContextMap();

		ExportImportConfiguration exportImportConfiguration =
			ExportImportConfigurationLocalServiceUtil.
				fetchExportImportConfiguration(
					MapUtil.getLong(contextMap, "exportImportConfigurationId"));

		if (exportImportConfiguration != null) {
			if ((exportImportConfiguration.getType() !=
					ExportImportConfigurationConstants.
						TYPE_PUBLISH_LAYOUT_LOCAL) &&
				(exportImportConfiguration.getType() !=
					ExportImportConfigurationConstants.
						TYPE_SCHEDULED_PUBLISH_LAYOUT_LOCAL)) {

				return;
			}

			contextMap = exportImportConfiguration.getSettingsMap();
		}

		long sourceGroupId = MapUtil.getLong(contextMap, "sourceGroupId");

		sourceGroup = GroupLocalServiceUtil.fetchGroup(sourceGroupId);
	}

	@Override
	public String getDisplayName(HttpServletRequest httpServletRequest) {
		if ((sourceGroup != null) && !sourceGroup.isStagingGroup() &&
			(backgroundTask.getGroupId() == sourceGroup.getGroupId())) {

			return LanguageUtil.get(
				httpServletRequest, "initial-publish-process");
		}

		if (Validator.isNull(backgroundTask.getName())) {
			return LanguageUtil.get(httpServletRequest, "untitled");
		}

		return backgroundTask.getName();
	}

	protected Group sourceGroup;

}