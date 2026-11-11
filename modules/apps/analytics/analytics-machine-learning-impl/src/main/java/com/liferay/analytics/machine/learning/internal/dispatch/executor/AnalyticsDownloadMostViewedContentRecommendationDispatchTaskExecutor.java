/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.machine.learning.internal.dispatch.executor;

import com.liferay.analytics.dxp.entity.rest.dto.v1_0.AnalyticsMostViewedContentRecommendation;
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
		"dispatch.task.executor.name=" + AnalyticsDownloadMostViewedContentRecommendationDispatchTaskExecutor.KEY,
		"dispatch.task.executor.type=" + AnalyticsDownloadMostViewedContentRecommendationDispatchTaskExecutor.KEY
	},
	service = DispatchTaskExecutor.class
)
public class
	AnalyticsDownloadMostViewedContentRecommendationDispatchTaskExecutor
		extends BaseRecommendationDispatchTaskExecutor {

	public static final String KEY =
		"analytics-download-most-viewed-content-recommendation";

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
				"jobId", "jobId"
			).put(
				"rank", "rank"
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
			AnalyticsMostViewedContentRecommendation.class.getName(),
			dispatchLog.getUserId());
	}

	@Override
	public String getName() {
		return KEY;
	}

	@Override
	protected String getRecommendationIndexName() {
		return RecommendationIndexNames.MOST_VIEWED_CONTENT_RECOMMENDATION;
	}

}