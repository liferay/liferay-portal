/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.machine.learning.internal.dispatch.executor;

import com.liferay.analytics.batch.exportimport.manager.AnalyticsBatchExportImportManager;
import com.liferay.analytics.machine.learning.internal.recommendation.search.RecommendationIndexer;
import com.liferay.dispatch.executor.BaseDispatchTaskExecutor;
import com.liferay.dispatch.executor.DispatchTaskExecutorOutput;
import com.liferay.dispatch.executor.DispatchTaskStatus;
import com.liferay.dispatch.model.DispatchLog;
import com.liferay.dispatch.service.DispatchLogLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.index.IndexNameBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Ferrari
 */
public abstract class BaseRecommendationDispatchTaskExecutor
	extends BaseDispatchTaskExecutor {

	@Override
	public boolean isHiddenInUI() {
		return !FeatureFlagManagerUtil.isEnabled("LRAC-14771");
	}

	@Activate
	protected void activate() {
		_recommendationIndexer = new RecommendationIndexer(
			getRecommendationIndexName(), indexNameBuilder, searchCapabilities,
			searchEngineAdapter);
	}

	protected abstract String getRecommendationIndexName();

	protected Date getResourceLastModifiedDate(
		long companyId, long dispatchTriggerId) {

		if (FeatureFlagManagerUtil.isEnabled(companyId, "LRAC-14771") &&
			_recommendationIndexer.isEmpty(companyId)) {

			return null;
		}

		DispatchLog dispatchLog =
			dispatchLogLocalService.fetchLatestDispatchLog(
				dispatchTriggerId, DispatchTaskStatus.SUCCESSFUL);

		if (dispatchLog != null) {
			return dispatchLog.getEndDate();
		}

		return null;
	}

	protected void updateDispatchLog(
			long dispatchLogId,
			DispatchTaskExecutorOutput dispatchTaskExecutorOutput,
			String message)
		throws PortalException {

		StringBundler sb = new StringBundler(5);

		if (dispatchTaskExecutorOutput.getOutput() != null) {
			sb.append(dispatchTaskExecutorOutput.getOutput());
		}

		sb.append(_dateFormat.format(new Date()));
		sb.append(StringPool.SPACE);
		sb.append(message);
		sb.append(StringPool.NEW_LINE);

		dispatchTaskExecutorOutput.setOutput(sb.toString());

		dispatchLogLocalService.updateDispatchLog(
			dispatchLogId, new Date(), dispatchTaskExecutorOutput.getError(),
			dispatchTaskExecutorOutput.getOutput(),
			DispatchTaskStatus.IN_PROGRESS);
	}

	@Reference
	protected AnalyticsBatchExportImportManager
		analyticsBatchExportImportManager;

	@Reference
	protected DispatchLogLocalService dispatchLogLocalService;

	@Reference
	protected IndexNameBuilder indexNameBuilder;

	@Reference
	protected SearchCapabilities searchCapabilities;

	@Reference
	protected SearchEngineAdapter searchEngineAdapter;

	private static final DateFormat _dateFormat = new SimpleDateFormat(
		"yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	private RecommendationIndexer _recommendationIndexer;

}