/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.batch.exportimport.internal;

import com.liferay.analytics.batch.exportimport.AnalyticsDXPEntityBatchExporter;
import com.liferay.analytics.settings.security.constants.AnalyticsSecurityConstants;
import com.liferay.dispatch.constants.DispatchConstants;
import com.liferay.dispatch.executor.DispatchTaskClusterMode;
import com.liferay.dispatch.executor.DispatchTaskExecutor;
import com.liferay.dispatch.executor.DispatchTaskExecutorRegistry;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchLogLocalService;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.portal.kernel.cluster.ClusterExecutorUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcos Martins
 */
@Component(service = AnalyticsDXPEntityBatchExporter.class)
public class AnalyticsDXPEntityBatchExporterImpl
	implements AnalyticsDXPEntityBatchExporter {

	@Override
	public void export(long companyId, String[] dispatchTriggerNames)
		throws Exception {

		for (String dispatchTriggerName : dispatchTriggerNames) {
			DispatchTrigger dispatchTrigger =
				_dispatchTriggerLocalService.fetchDispatchTrigger(
					companyId, dispatchTriggerName);

			if (dispatchTrigger == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to find dispatch trigger with name " +
							dispatchTriggerName);
				}

				continue;
			}

			Message message = new Message();

			message.setPayload(
				JSONUtil.put(
					"dispatchTriggerId", dispatchTrigger.getDispatchTriggerId()
				).toString());

			_messageBus.sendMessage(
				DispatchConstants.EXECUTOR_DESTINATION_NAME, message);
		}
	}

	@Override
	public void refreshExportTriggers(
			long companyId, String[] dispatchTriggerNames)
		throws Exception {

		for (String dispatchTriggerName : dispatchTriggerNames) {
			DispatchTrigger dispatchTrigger =
				_dispatchTriggerLocalService.fetchDispatchTrigger(
					companyId, dispatchTriggerName);

			if (dispatchTrigger == null) {
				scheduleExportTriggers(
					companyId, new String[] {dispatchTriggerName});

				continue;
			}

			_dispatchLogLocalService.deleteDispatchLogs(
				dispatchTrigger.getDispatchTriggerId());

			Date nextFireDate = dispatchTrigger.getNextFireDate();

			Instant instant = null;

			if (nextFireDate == null) {
				Date date = new Date();

				instant = date.toInstant();
			}
			else {
				instant = nextFireDate.toInstant();
			}

			ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("UTC"));

			_dispatchTriggerLocalService.deleteDispatchTrigger(dispatchTrigger);

			_addDispatchTrigger(
				companyId, dispatchTriggerName,
				zonedDateTime.toLocalDateTime());
		}
	}

	@Override
	public void scheduleExportTriggers(
			long companyId, String[] dispatchTriggerNames)
		throws Exception {

		for (String dispatchTriggerName : dispatchTriggerNames) {
			DispatchTrigger dispatchTrigger =
				_dispatchTriggerLocalService.fetchDispatchTrigger(
					companyId, dispatchTriggerName);

			if (dispatchTrigger != null) {
				continue;
			}

			_addDispatchTrigger(
				companyId, dispatchTriggerName, LocalDateTime.now());
		}
	}

	@Override
	public void unscheduleExportTriggers(
			long companyId, String[] dispatchTriggerNames)
		throws Exception {

		for (String dispatchTriggerName : dispatchTriggerNames) {
			DispatchTrigger dispatchTrigger =
				_dispatchTriggerLocalService.fetchDispatchTrigger(
					companyId, dispatchTriggerName);

			if (dispatchTrigger == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to find dispatch trigger with name " +
							dispatchTriggerName);
				}

				continue;
			}

			_dispatchTriggerLocalService.deleteDispatchTrigger(dispatchTrigger);
		}
	}

	private User _addAnalyticsAdministratorUser(long companyId)
		throws Exception {

		Company company = _companyLocalService.getCompany(companyId);

		Role role = _roleLocalService.getRole(
			companyId, "Analytics Administrator");

		User user = _userLocalService.addUser(
			0, companyId, true, null, null, false,
			AnalyticsSecurityConstants.SCREEN_NAME_ANALYTICS_ADMIN,
			"analytics.administrator@" + company.getMx(),
			LocaleUtil.getDefault(), "Analytics", "", "Administrator", 0, 0,
			true, 0, 1, 1970, "", UserConstants.TYPE_REGULAR, null, null,
			new long[] {role.getRoleId()}, null, false, new ServiceContext());

		return _userLocalService.updateUser(user);
	}

	private DispatchTrigger _addDispatchTrigger(
			long companyId, String dispatchTriggerName,
			LocalDateTime localDateTime)
		throws Exception {

		DispatchTaskExecutor dispatchTaskExecutor =
			_dispatchTaskExecutorRegistry.fetchDispatchTaskExecutor(
				dispatchTriggerName);

		if (dispatchTaskExecutor == null) {
			return null;
		}

		User user = _userLocalService.fetchUserByScreenName(
			companyId, AnalyticsSecurityConstants.SCREEN_NAME_ANALYTICS_ADMIN);

		if (user == null) {
			user = _addAnalyticsAdministratorUser(companyId);
		}

		DispatchTrigger dispatchTrigger =
			_dispatchTriggerLocalService.addDispatchTrigger(
				null, user.getUserId(), dispatchTaskExecutor,
				dispatchTriggerName, null, dispatchTriggerName, false);

		DispatchTaskClusterMode dispatchTaskClusterMode =
			DispatchTaskClusterMode.NOT_APPLICABLE;

		if (ClusterExecutorUtil.isEnabled()) {
			dispatchTaskClusterMode =
				DispatchTaskClusterMode.SINGLE_NODE_PERSISTED;
		}

		return _dispatchTriggerLocalService.updateDispatchTrigger(
			dispatchTrigger.getDispatchTriggerId(), true, _CRON_EXPRESSION,
			dispatchTaskClusterMode, 0, 0, 0, 0, 0, true, false,
			localDateTime.getMonthValue() - 1, localDateTime.getDayOfMonth(),
			localDateTime.getYear(), localDateTime.getHour(),
			localDateTime.getMinute(), "UTC");
	}

	private static final String _CRON_EXPRESSION = "0 0 * * * ?";

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsDXPEntityBatchExporterImpl.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private DispatchLogLocalService _dispatchLogLocalService;

	@Reference
	private DispatchTaskExecutorRegistry _dispatchTaskExecutorRegistry;

	@Reference
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

	@Reference
	private MessageBus _messageBus;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}