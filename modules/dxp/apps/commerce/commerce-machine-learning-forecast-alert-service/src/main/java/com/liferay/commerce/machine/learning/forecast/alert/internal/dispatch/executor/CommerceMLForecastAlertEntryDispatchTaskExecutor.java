/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.machine.learning.forecast.alert.internal.dispatch.executor;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.machine.learning.forecast.CommerceAccountCommerceMLForecast;
import com.liferay.commerce.machine.learning.forecast.CommerceAccountCommerceMLForecastManager;
import com.liferay.commerce.machine.learning.forecast.alert.constants.CommerceMLForecastAlertConstants;
import com.liferay.commerce.machine.learning.forecast.alert.service.CommerceMLForecastAlertEntryLocalService;
import com.liferay.dispatch.executor.BaseDispatchTaskExecutor;
import com.liferay.dispatch.executor.DispatchTaskExecutor;
import com.liferay.dispatch.executor.DispatchTaskExecutorOutput;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.io.IOException;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Ferrari
 */
@Component(
	property = {
		"dispatch.task.executor.name=" + CommerceMLForecastAlertEntryDispatchTaskExecutor.KEY,
		"dispatch.task.executor.type=" + CommerceMLForecastAlertEntryDispatchTaskExecutor.KEY
	},
	service = DispatchTaskExecutor.class
)
public class CommerceMLForecastAlertEntryDispatchTaskExecutor
	extends BaseDispatchTaskExecutor {

	public static final String KEY = "commerce-ml-forecast-alert-entry";

	@Override
	public void doExecute(
			DispatchTrigger dispatchTrigger,
			DispatchTaskExecutorOutput dispatchTaskExecutorOutput)
		throws IOException, PortalException {

		UnicodeProperties unicodeProperties =
			dispatchTrigger.getDispatchTaskSettingsUnicodeProperties();

		Date commerceMLForecastAlertEntryCheckDate = GetterUtil.getDate(
			unicodeProperties.getProperty(
				CommerceMLForecastAlertConstants.
					COMMERCE_ML_FORECAST_ALERT_ENTRY_CHECK_DATE),
			DateFormatFactoryUtil.getSimpleDateFormat(_DATE_FORMAT));

		if (commerceMLForecastAlertEntryCheckDate != null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Using manually set Forecast Alert Check Date");
			}
		}
		else {
			commerceMLForecastAlertEntryCheckDate = new Date();
		}

		float commerceMLForecastAlertEntryThreshold = GetterUtil.getFloat(
			unicodeProperties.get(
				CommerceMLForecastAlertConstants.
					COMMERCE_ML_FORECAST_ALERT_ENTRY_THRESHOLD),
			_DEFAULT_COMMERCE_ML_FORECAST_ALERT_ENTRY_THRESHOLD);

		long[] accountEntryIds = ListUtil.toLongArray(
			_accountEntryLocalService.getAccountEntries(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			AccountEntry::getAccountEntryId);

		List<CommerceAccountCommerceMLForecast>
			commerceAccountCommerceMLForecasts =
				_commerceAccountCommerceMLForecastManager.
					getMonthlyRevenueCommerceAccountCommerceMLForecasts(
						dispatchTrigger.getCompanyId(), accountEntryIds,
						commerceMLForecastAlertEntryCheckDate, 1, 0);

		for (CommerceAccountCommerceMLForecast
				commerceAccountCommerceMLForecast :
					commerceAccountCommerceMLForecasts) {

			float actual = commerceAccountCommerceMLForecast.getActual();

			float forecast = commerceAccountCommerceMLForecast.getForecast();

			if ((actual == Float.MIN_VALUE) || (forecast == Float.MIN_VALUE)) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						String.format(
							"Missing actual or forecast value for %s, skipping",
							commerceAccountCommerceMLForecast.getForecastId()));
				}

				continue;
			}

			float delta = actual - forecast;

			float percentChange = (delta / forecast) * 100;

			if (!(percentChange > commerceMLForecastAlertEntryThreshold) &&
				!(percentChange < -commerceMLForecastAlertEntryThreshold)) {

				continue;
			}

			if (_log.isWarnEnabled()) {
				_log.warn(
					String.format(
						"Actual value exceed threshold %s: change percent: %s",
						commerceMLForecastAlertEntryThreshold, percentChange));
			}

			_commerceMLForecastAlertEntryLocalService.
				addOrUpdateCommerceMLForecastAlertEntry(
					dispatchTrigger.getCompanyId(), dispatchTrigger.getUserId(),
					commerceAccountCommerceMLForecast.getCommerceAccountId(),
					commerceAccountCommerceMLForecast.getTimestamp(), actual,
					forecast, percentChange);
		}
	}

	@Override
	public String getName() {
		return KEY;
	}

	private static final String _DATE_FORMAT = "yyyyMMdd";

	private static final float
		_DEFAULT_COMMERCE_ML_FORECAST_ALERT_ENTRY_THRESHOLD = 20.0F;

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceMLForecastAlertEntryDispatchTaskExecutor.class);

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private CommerceAccountCommerceMLForecastManager
		_commerceAccountCommerceMLForecastManager;

	@Reference
	private CommerceMLForecastAlertEntryLocalService
		_commerceMLForecastAlertEntryLocalService;

}