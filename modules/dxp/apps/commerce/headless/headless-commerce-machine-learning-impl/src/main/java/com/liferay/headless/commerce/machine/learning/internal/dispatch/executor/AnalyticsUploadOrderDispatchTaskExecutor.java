/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.internal.dispatch.executor;

import com.liferay.analytics.settings.rest.constants.FieldOrderConstants;
import com.liferay.dispatch.executor.DispatchTaskExecutor;
import com.liferay.dispatch.executor.DispatchTaskExecutorOutput;
import com.liferay.dispatch.executor.DispatchTaskStatus;
import com.liferay.dispatch.model.DispatchLog;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.headless.commerce.machine.learning.dto.v1_0.Order;
import com.liferay.headless.commerce.machine.learning.internal.batch.engine.v1_0.OrderBatchEngineTaskItemDelegate;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.util.Arrays;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * @author Riccardo Ferrari
 */
@Component(
	property = {
		"dispatch.task.executor.name=" + AnalyticsUploadOrderDispatchTaskExecutor.KEY,
		"dispatch.task.executor.type=" + AnalyticsUploadOrderDispatchTaskExecutor.KEY
	},
	service = DispatchTaskExecutor.class
)
public class AnalyticsUploadOrderDispatchTaskExecutor
	extends BaseDispatchTaskExecutor {

	public static final String KEY = "analytics-upload-order";

	@Override
	public void doExecute(
			DispatchTrigger dispatchTrigger,
			DispatchTaskExecutorOutput dispatchTaskExecutorOutput)
		throws Exception {

		DispatchLog dispatchLog =
			dispatchLogLocalService.fetchLatestDispatchLog(
				dispatchTrigger.getDispatchTriggerId(),
				DispatchTaskStatus.IN_PROGRESS);
		String filterString = getCommerceChannelFilterString(
			dispatchTrigger.getCompanyId(),
			commerceChannelId ->
				"commerceChannelId eq '" + commerceChannelId + "'");

		if (Objects.equals(StringPool.BLANK, filterString)) {
			updateDispatchLog(
				dispatchLog.getDispatchLogId(), dispatchTaskExecutorOutput,
				"No commerce channels enabled for synchronisation");

			return;
		}

		analyticsBatchExportImportManager.exportToAnalyticsCloud(
			OrderBatchEngineTaskItemDelegate.KEY,
			dispatchTrigger.getCompanyId(),
			Arrays.asList(
				ArrayUtil.append(
					FieldOrderConstants.FIELD_ORDER_NAMES,
					FieldOrderConstants.FIELD_ORDER_ITEM_NAMES)),
			filterString,
			message -> updateDispatchLog(
				dispatchLog.getDispatchLogId(), dispatchTaskExecutorOutput,
				message),
			getLatestSuccessfulDispatchLogEndDate(
				dispatchTrigger.getDispatchTriggerId()),
			Order.class.getName(), dispatchTrigger.getUserId());
	}

	@Override
	public String getName() {
		return KEY;
	}

}