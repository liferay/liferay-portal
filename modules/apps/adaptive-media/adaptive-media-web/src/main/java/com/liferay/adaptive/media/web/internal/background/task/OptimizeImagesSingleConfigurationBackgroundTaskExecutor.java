/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.web.internal.background.task;

import com.liferay.adaptive.media.constants.AMOptimizeImagesBackgroundTaskConstants;
import com.liferay.adaptive.media.web.internal.optimizer.AMImageOptimizerUtil;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;

import org.osgi.service.component.annotations.Component;

/**
 * @author Sergio González
 */
@Component(
	property = "background.task.executor.class.name=com.liferay.adaptive.media.web.internal.background.task.OptimizeImagesSingleConfigurationBackgroundTaskExecutor",
	service = BackgroundTaskExecutor.class
)
public class OptimizeImagesSingleConfigurationBackgroundTaskExecutor
	extends BaseOptimizeImagesBackgroundTaskExecutor {

	@Override
	public BackgroundTaskExecutor clone() {
		return new OptimizeImagesSingleConfigurationBackgroundTaskExecutor();
	}

	@Override
	protected void optimizeImages(String configurationEntryUuid, long companyId)
		throws Exception {

		OptimizeImagesStatusMessageSenderUtil.sendStatusMessage(
			AMOptimizeImagesBackgroundTaskConstants.SINGLE_START, companyId,
			configurationEntryUuid);

		AMImageOptimizerUtil.optimize(companyId, configurationEntryUuid);

		OptimizeImagesStatusMessageSenderUtil.sendStatusMessage(
			AMOptimizeImagesBackgroundTaskConstants.SINGLE_END, companyId,
			configurationEntryUuid);
	}

}