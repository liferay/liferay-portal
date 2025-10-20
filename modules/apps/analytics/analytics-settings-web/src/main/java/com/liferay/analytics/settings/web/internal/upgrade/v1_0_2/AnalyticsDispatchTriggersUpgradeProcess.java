/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.web.internal.upgrade.v1_0_2;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.security.constants.AnalyticsSecurityConstants;
import com.liferay.dispatch.executor.DispatchTaskClusterMode;
import com.liferay.dispatch.executor.DispatchTaskExecutor;
import com.liferay.dispatch.executor.DispatchTaskStatus;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchLogLocalService;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.time.LocalDateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Rachael Koestartyo
 */
public class AnalyticsDispatchTriggersUpgradeProcess extends UpgradeProcess {

	public AnalyticsDispatchTriggersUpgradeProcess(
		ConfigurationAdmin configurationAdmin,
		DispatchLogLocalService dispatchLogLocalService,
		DispatchTaskExecutor dispatchTaskExecutor,
		DispatchTriggerLocalService dispatchTriggerLocalService,
		UserLocalService userLocalService) {

		_configurationAdmin = configurationAdmin;
		_dispatchLogLocalService = dispatchLogLocalService;
		_dispatchTaskExecutor = dispatchTaskExecutor;
		_dispatchTriggerLocalService = dispatchTriggerLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		Configuration[] configurations;

		if (PropsValues.DATABASE_PARTITION_ENABLED) {
			configurations = _configurationAdmin.listConfigurations(
				StringBundler.concat(
					"(&(companyId=", CompanyThreadLocal.getCompanyId(),
					")(service.pid=", AnalyticsConfiguration.class.getName(),
					"*))"));
		}
		else {
			configurations = _configurationAdmin.listConfigurations(
				"(service.pid=" + AnalyticsConfiguration.class.getName() +
					"*)");
		}

		if (ArrayUtil.isEmpty(configurations)) {
			return;
		}

		for (Configuration configuration : configurations) {
			Dictionary<String, Object> properties =
				configuration.getProperties();

			if (properties == null) {
				continue;
			}

			long companyId = GetterUtil.getLong(properties.get("companyId"));

			DispatchTrigger dispatchTrigger =
				_dispatchTriggerLocalService.fetchDispatchTrigger(
					companyId, "export-analytics-dxp-entities");

			if (dispatchTrigger != null) {
				continue;
			}

			User user = _userLocalService.fetchUserByScreenName(
				companyId,
				AnalyticsSecurityConstants.SCREEN_NAME_ANALYTICS_ADMIN);

			if (user == null) {
				continue;
			}

			dispatchTrigger = _dispatchTriggerLocalService.addDispatchTrigger(
				null, user.getUserId(), _dispatchTaskExecutor,
				"export-analytics-dxp-entities", null,
				"export-analytics-dxp-entities", false);

			LocalDateTime localDateTime = LocalDateTime.now();

			_dispatchTriggerLocalService.updateDispatchTrigger(
				dispatchTrigger.getDispatchTriggerId(), true, "0 0 * * * ?",
				DispatchTaskClusterMode.NOT_APPLICABLE, 0, 0, 0, 0, 0, true,
				false, localDateTime.getMonthValue() - 1,
				localDateTime.getDayOfMonth(), localDateTime.getYear(),
				localDateTime.getHour(), localDateTime.getMinute(), "UTC");

			Calendar calendar = Calendar.getInstance();

			calendar.setTime(new Date());
			calendar.add(Calendar.HOUR, -2);

			Date endDate = calendar.getTime();

			calendar.add(Calendar.MINUTE, -5);

			Date startDate = calendar.getTime();

			_dispatchLogLocalService.addDispatchLog(
				user.getUserId(), dispatchTrigger.getDispatchTriggerId(),
				endDate, null, null, startDate, DispatchTaskStatus.SUCCESSFUL);
		}
	}

	private final ConfigurationAdmin _configurationAdmin;
	private final DispatchLogLocalService _dispatchLogLocalService;
	private final DispatchTaskExecutor _dispatchTaskExecutor;
	private final DispatchTriggerLocalService _dispatchTriggerLocalService;
	private final UserLocalService _userLocalService;

}