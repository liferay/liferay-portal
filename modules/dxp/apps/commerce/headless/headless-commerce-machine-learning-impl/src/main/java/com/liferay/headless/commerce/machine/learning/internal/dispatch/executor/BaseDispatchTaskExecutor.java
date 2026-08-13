/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.internal.dispatch.executor;

import com.liferay.analytics.batch.exportimport.manager.AnalyticsBatchExportImportManager;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.dispatch.executor.DispatchTaskExecutorOutput;
import com.liferay.dispatch.executor.DispatchTaskStatus;
import com.liferay.dispatch.model.DispatchLog;
import com.liferay.dispatch.service.DispatchLogLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Ferrari
 */
public abstract class BaseDispatchTaskExecutor
	extends com.liferay.dispatch.executor.BaseDispatchTaskExecutor {

	protected String getCommerceChannelFilterString(
			long companyId, Function<Long, String> filterFunction)
		throws Exception {

		List<String> filterStrings = new ArrayList<>();

		AnalyticsConfiguration analyticsConfiguration =
			analyticsSettingsManager.getAnalyticsConfiguration(companyId);

		for (long commerceChannelId :
				analyticsSettingsManager.getCommerceChannelIds(
					companyId,
					GetterUtil.getLongValues(
						analyticsConfiguration.syncedGroupIds()))) {

			filterStrings.add(filterFunction.apply(commerceChannelId));
		}

		return StringUtil.merge(filterStrings, " or ");
	}

	protected Date getLatestSuccessfulDispatchLogEndDate(
		long dispatchTriggerId) {

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
	protected AnalyticsSettingsManager analyticsSettingsManager;

	@Reference
	protected DispatchLogLocalService dispatchLogLocalService;

	private static final DateFormat _dateFormat = new SimpleDateFormat(
		"yyyy-MM-dd'T'HH:mm:ss.SSSZ");

}