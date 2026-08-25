/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.batch.exportimport.internal.test;

import com.liferay.analytics.batch.exportimport.AnalyticsDXPEntityBatchExporter;
import com.liferay.analytics.batch.exportimport.constants.AnalyticsDXPEntityBatchExporterConstants;
import com.liferay.analytics.settings.security.constants.AnalyticsSecurityConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dispatch.executor.DispatchTaskClusterMode;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.portal.kernel.cluster.ClusterExecutorUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class AnalyticsDXPEntityBatchExporterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testScheduleExportTriggersCreatesAnalyticsAdminWhenMissing()
		throws Exception {

		User existingUser = _userLocalService.fetchUserByScreenName(
			TestPropsValues.getCompanyId(),
			AnalyticsSecurityConstants.SCREEN_NAME_ANALYTICS_ADMIN);

		if (existingUser != null) {
			_userLocalService.deleteUser(existingUser);
		}

		_analyticsDXPEntityBatchExporter.scheduleExportTriggers(
			TestPropsValues.getCompanyId(),
			new String[] {
				AnalyticsDXPEntityBatchExporterConstants.
					DISPATCH_TRIGGER_NAME_DXP_ENTITIES
			});

		Assert.assertNotNull(
			_userLocalService.fetchUserByScreenName(
				TestPropsValues.getCompanyId(),
				AnalyticsSecurityConstants.SCREEN_NAME_ANALYTICS_ADMIN));
		Assert.assertNotNull(
			_dispatchTriggerLocalService.fetchDispatchTrigger(
				TestPropsValues.getCompanyId(),
				AnalyticsDXPEntityBatchExporterConstants.
					DISPATCH_TRIGGER_NAME_DXP_ENTITIES));
	}

	@Test
	public void testScheduleExportTriggersSchedulesOnSingleNode()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();

		_analyticsDXPEntityBatchExporter.unscheduleExportTriggers(
			companyId,
			new String[] {
				AnalyticsDXPEntityBatchExporterConstants.
					DISPATCH_TRIGGER_NAME_DXP_ENTITIES
			});

		_analyticsDXPEntityBatchExporter.scheduleExportTriggers(
			companyId,
			new String[] {
				AnalyticsDXPEntityBatchExporterConstants.
					DISPATCH_TRIGGER_NAME_DXP_ENTITIES
			});

		DispatchTrigger dispatchTrigger =
			_dispatchTriggerLocalService.fetchDispatchTrigger(
				companyId,
				AnalyticsDXPEntityBatchExporterConstants.
					DISPATCH_TRIGGER_NAME_DXP_ENTITIES);

		DispatchTaskClusterMode dispatchTaskClusterMode =
			DispatchTaskClusterMode.NOT_APPLICABLE;

		if (ClusterExecutorUtil.isEnabled()) {
			dispatchTaskClusterMode =
				DispatchTaskClusterMode.SINGLE_NODE_PERSISTED;
		}

		Assert.assertEquals(
			dispatchTaskClusterMode.getMode(),
			dispatchTrigger.getDispatchTaskClusterMode());

		_analyticsDXPEntityBatchExporter.unscheduleExportTriggers(
			companyId,
			new String[] {
				AnalyticsDXPEntityBatchExporterConstants.
					DISPATCH_TRIGGER_NAME_DXP_ENTITIES
			});
	}

	@Inject
	private AnalyticsDXPEntityBatchExporter _analyticsDXPEntityBatchExporter;

	@Inject
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

	@Inject
	private UserLocalService _userLocalService;

}