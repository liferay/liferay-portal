/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.machine.learning.internal.dispatch.executor;

import com.liferay.analytics.dxp.entity.rest.dto.v1_0.AnalyticsUserContentRecommendation;
import com.liferay.analytics.machine.learning.internal.recommendation.constants.RecommendationIndexNames;
import com.liferay.dispatch.executor.DispatchTaskExecutor;
import com.liferay.dispatch.executor.DispatchTaskExecutorOutput;
import com.liferay.dispatch.executor.DispatchTaskStatus;
import com.liferay.dispatch.model.DispatchLog;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.portal.kernel.util.HashMapBuilder;

import org.osgi.service.component.annotations.Component;

/**
 * @author Riccardo Ferrari
 */
@Component(
	property = {
		"dispatch.task.executor.name=" + AnalyticsDownloadUserContentRecommendationDispatchTaskExecutor.KEY,
		"dispatch.task.executor.type=" + AnalyticsDownloadUserContentRecommendationDispatchTaskExecutor.KEY
	},
	service = DispatchTaskExecutor.class
)
public class AnalyticsDownloadUserContentRecommendationDispatchTaskExecutor
	extends BaseRecommendationDispatchTaskExecutor {

	public static final String KEY =
		"analytics-download-user-content-recommendation";

	@Override
	public void doExecute(
			DispatchTrigger dispatchTrigger,
			DispatchTaskExecutorOutput dispatchTaskExecutorOutput)
		throws Exception {

		DispatchLog dispatchLog =
			dispatchLogLocalService.fetchLatestDispatchLog(
				dispatchTrigger.getDispatchTriggerId(),
				DispatchTaskStatus.IN_PROGRESS);

		analyticsBatchExportImportManager.importFromAnalyticsCloud(
			null, dispatchLog.getCompanyId(),
			HashMapBuilder.put(
				"assetCategoryIds", "assetCategoryIds"
			).put(
				"createDate", "createDate"
			).put(
				"entryClassPK", "userId"
			).put(
				"jobId", "jobId"
			).put(
				"recommendedEntryClassPK", "recommendedAssetEntryId"
			).put(
				"score", "score"
			).build(),
			message -> updateDispatchLog(
				dispatchLog.getDispatchLogId(), dispatchTaskExecutorOutput,
				message),
			getResourceLastModifiedDate(
				dispatchLog.getCompanyId(),
				dispatchTrigger.getDispatchTriggerId()),
			AnalyticsUserContentRecommendation.class.getName(),
			dispatchLog.getUserId());
	}

	@Override
	public String getName() {
		return KEY;
	}

	@Override
	protected String getRecommendationIndexName() {
		return RecommendationIndexNames.USER_CONTENT_RECOMMENDATION;
	}

}