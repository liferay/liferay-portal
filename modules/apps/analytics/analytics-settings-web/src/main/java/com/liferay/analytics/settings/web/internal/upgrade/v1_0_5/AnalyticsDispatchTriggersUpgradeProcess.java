/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.web.internal.upgrade.v1_0_5;

import com.liferay.dispatch.executor.DispatchTaskClusterMode;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.time.LocalDateTime;

import java.util.TimeZone;

/**
 * @author Eudaldo Alonso
 */
public class AnalyticsDispatchTriggersUpgradeProcess extends UpgradeProcess {

	public AnalyticsDispatchTriggersUpgradeProcess(
		CompanyLocalService companyLocalService,
		DispatchTriggerLocalService dispatchTriggerLocalService) {

		_companyLocalService = companyLocalService;
		_dispatchTriggerLocalService = dispatchTriggerLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompany(
			company -> {
				DispatchTrigger dispatchTrigger =
					_dispatchTriggerLocalService.fetchDispatchTrigger(
						company.getCompanyId(),
						"export-analytics-dxp-entities");

				if ((dispatchTrigger == null) || dispatchTrigger.isActive()) {
					return;
				}

				TimeZone timeZone = company.getTimeZone();

				LocalDateTime localDateTime = LocalDateTime.now(
					timeZone.toZoneId());

				_dispatchTriggerLocalService.updateDispatchTrigger(
					dispatchTrigger.getDispatchTriggerId(), true, "0 0 * * * ?",
					DispatchTaskClusterMode.valueOf(
						dispatchTrigger.getDispatchTaskClusterMode()),
					0, 0, 0, 0, 0, true, false,
					localDateTime.getMonthValue() - 1,
					localDateTime.getDayOfMonth(), localDateTime.getYear(),
					localDateTime.getHour(), localDateTime.getMinute(),
					timeZone.getID());
			});
	}

	private final CompanyLocalService _companyLocalService;
	private final DispatchTriggerLocalService _dispatchTriggerLocalService;

}