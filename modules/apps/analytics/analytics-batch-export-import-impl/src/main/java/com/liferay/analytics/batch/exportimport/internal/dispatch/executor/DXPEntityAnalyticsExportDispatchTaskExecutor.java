/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.batch.exportimport.internal.dispatch.executor;

import com.liferay.analytics.batch.exportimport.manager.AnalyticsBatchExportImportManager;
import com.liferay.analytics.dxp.entity.rest.dto.v1_0.DXPEntity;
import com.liferay.analytics.message.storage.service.AnalyticsAssociationLocalService;
import com.liferay.analytics.message.storage.service.AnalyticsDeleteMessageLocalService;
import com.liferay.analytics.settings.configuration.AnalyticsConfigurationRegistry;
import com.liferay.dispatch.executor.DispatchTaskExecutor;
import com.liferay.dispatch.executor.DispatchTaskExecutorOutput;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = {
		"dispatch.task.executor.name=" + DXPEntityAnalyticsExportDispatchTaskExecutor.KEY,
		"dispatch.task.executor.type=" + DXPEntityAnalyticsExportDispatchTaskExecutor.KEY
	},
	service = DispatchTaskExecutor.class
)
public class DXPEntityAnalyticsExportDispatchTaskExecutor
	extends BaseAnalyticsDispatchTaskExecutor {

	public static final String KEY = "export-analytics-dxp-entities";

	@Override
	public void doExecute(
			DispatchTrigger dispatchTrigger,
			DispatchTaskExecutorOutput dispatchTaskExecutorOutput)
		throws Exception {

		if (!_analyticsConfigurationRegistry.isActive()) {
			return;
		}

		Date resourceLastModifiedDate = null;

		UnicodeProperties dispatchTaskSettingsUnicodeProperties =
			dispatchTrigger.getDispatchTaskSettingsUnicodeProperties();

		boolean forceFullExport = GetterUtil.getBoolean(
			dispatchTaskSettingsUnicodeProperties.getProperty(
				"forceFullExport", StringPool.FALSE));

		if (forceFullExport) {
			_analyticsAssociationLocalService.deleteAnalyticsAssociations(
				dispatchTrigger.getCompanyId());
			_analyticsDeleteMessageLocalService.deleteAnalyticsDeleteMessages(
				dispatchTrigger.getCompanyId());
		}
		else {
			resourceLastModifiedDate = getResourceLastModifiedDate(
				dispatchTrigger.getDispatchTriggerId());
		}

		_analyticsBatchExportImportManager.exportToAnalyticsCloud(
			_batchEngineExportTaskItemDelegateNames,
			dispatchTrigger.getCompanyId(),
			getNotificationUnsafeConsumer(
				dispatchTrigger.getDispatchTriggerId(),
				dispatchTaskExecutorOutput),
			resourceLastModifiedDate, DXPEntity.class.getName(),
			dispatchTrigger.getUserId());

		resourceLastModifiedDate = getResourceLastModifiedDate(
			dispatchTrigger.getDispatchTriggerId());

		if (resourceLastModifiedDate != null) {
			_analyticsAssociationLocalService.deleteAnalyticsAssociations(
				dispatchTrigger.getCompanyId(), resourceLastModifiedDate);
			_analyticsDeleteMessageLocalService.deleteAnalyticsDeleteMessages(
				dispatchTrigger.getCompanyId(), resourceLastModifiedDate);
		}

		if (forceFullExport) {
			dispatchTaskSettingsUnicodeProperties.remove("forceFullExport");

			dispatchTrigger.setDispatchTaskSettingsUnicodeProperties(
				dispatchTaskSettingsUnicodeProperties);

			_dispatchTriggerLocalService.updateDispatchTrigger(dispatchTrigger);
		}
	}

	@Override
	public String getName() {
		return KEY;
	}

	private static final List<String> _batchEngineExportTaskItemDelegateNames =
		Arrays.asList(
			"account-entry-analytics-dxp-entities",
			"account-group-analytics-dxp-entities",
			"analytics-association-analytics-dxp-entities",
			"analytics-delete-message-analytics-dxp-entities",
			"expando-column-analytics-dxp-entities",
			"group-analytics-dxp-entities",
			"organization-analytics-dxp-entities",
			"role-analytics-dxp-entities", "team-analytics-dxp-entities",
			"user-analytics-dxp-entities", "user-group-analytics-dxp-entities");

	@Reference
	private AnalyticsAssociationLocalService _analyticsAssociationLocalService;

	@Reference
	private AnalyticsBatchExportImportManager
		_analyticsBatchExportImportManager;

	@Reference
	private AnalyticsConfigurationRegistry _analyticsConfigurationRegistry;

	@Reference
	private AnalyticsDeleteMessageLocalService
		_analyticsDeleteMessageLocalService;

	@Reference
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

}