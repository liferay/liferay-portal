/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.monitoring.internal.statistics.portal;

import com.liferay.portal.kernel.monitoring.MonitoringException;
import com.liferay.portal.monitoring.internal.statistics.RequestStatistics;
import com.liferay.portal.monitoring.internal.statistics.SummaryStatistics;

import java.util.Set;

/**
 * @author Michael C. Han
 * @author Brian Wing Shun Chan
 */
public class ServerSummaryStatistics implements SummaryStatistics {

	public ServerSummaryStatistics(
		ServerStatisticsHelper serverStatisticsHelper) {

		_serverStatisticsHelper = serverStatisticsHelper;
	}

	@Override
	public long getAverageTime() {
		Set<CompanyStatistics> companyStatisticsSet =
			_serverStatisticsHelper.getCompanyStatisticsSet();

		if (companyStatisticsSet.isEmpty()) {
			return 0;
		}

		long averageTime = 0;

		for (CompanyStatistics companyStatistics : companyStatisticsSet) {
			RequestStatistics requestStatistics =
				companyStatistics.getRequestStatistics();

			averageTime += requestStatistics.getAverageTime();
		}

		return averageTime / companyStatisticsSet.size();
	}

	@Override
	public long getAverageTimeByCompany(long companyId)
		throws MonitoringException {

		RequestStatistics requestStatistics = getRequestStatistics(companyId);

		return requestStatistics.getAverageTime();
	}

	@Override
	public long getAverageTimeByCompany(String webId)
		throws MonitoringException {

		RequestStatistics requestStatistics = getRequestStatistics(webId);

		return requestStatistics.getAverageTime();
	}

	@Override
	public long getErrorCount() {
		int errorCount = 0;

		for (CompanyStatistics companyStatistics :
				_serverStatisticsHelper.getCompanyStatisticsSet()) {

			RequestStatistics requestStatistics =
				companyStatistics.getRequestStatistics();

			errorCount += requestStatistics.getErrorCount();
		}

		return errorCount;
	}

	@Override
	public long getErrorCountByCompany(long companyId)
		throws MonitoringException {

		RequestStatistics requestStatistics = getRequestStatistics(companyId);

		return requestStatistics.getErrorCount();
	}

	@Override
	public long getErrorCountByCompany(String webId)
		throws MonitoringException {

		RequestStatistics requestStatistics = getRequestStatistics(webId);

		return requestStatistics.getErrorCount();
	}

	@Override
	public long getMaxTime() {
		long maxTime = 0;

		for (CompanyStatistics companyStatistics :
				_serverStatisticsHelper.getCompanyStatisticsSet()) {

			if (companyStatistics.getMaxTime() <= maxTime) {
				continue;
			}

			maxTime = companyStatistics.getMaxTime();
		}

		return maxTime;
	}

	@Override
	public long getMaxTimeByCompany(long companyId) throws MonitoringException {
		RequestStatistics requestStatistics = getRequestStatistics(companyId);

		return requestStatistics.getMaxTime();
	}

	@Override
	public long getMaxTimeByCompany(String webId) throws MonitoringException {
		RequestStatistics requestStatistics = getRequestStatistics(webId);

		return requestStatistics.getMaxTime();
	}

	@Override
	public long getMinTime() {
		long minTime = 0;

		for (CompanyStatistics companyStatistics :
				_serverStatisticsHelper.getCompanyStatisticsSet()) {

			if (companyStatistics.getMinTime() >= minTime) {
				continue;
			}

			minTime = companyStatistics.getMinTime();
		}

		return minTime;
	}

	@Override
	public long getMinTimeByCompany(long companyId) throws MonitoringException {
		RequestStatistics requestStatistics = getRequestStatistics(companyId);

		return requestStatistics.getMinTime();
	}

	@Override
	public long getMinTimeByCompany(String webId) throws MonitoringException {
		RequestStatistics requestStatistics = getRequestStatistics(webId);

		return requestStatistics.getMinTime();
	}

	@Override
	public long getRequestCount() {
		int requestCount = 0;

		for (CompanyStatistics companyStatistics :
				_serverStatisticsHelper.getCompanyStatisticsSet()) {

			RequestStatistics requestStatistics =
				companyStatistics.getRequestStatistics();

			requestCount += requestStatistics.getRequestCount();
		}

		return requestCount;
	}

	@Override
	public long getRequestCountByCompany(long companyId)
		throws MonitoringException {

		RequestStatistics requestStatistics = getRequestStatistics(companyId);

		return requestStatistics.getRequestCount();
	}

	@Override
	public long getRequestCountByCompany(String webId)
		throws MonitoringException {

		RequestStatistics requestStatistics = getRequestStatistics(webId);

		return requestStatistics.getRequestCount();
	}

	@Override
	public long getSuccessCount() {
		int successCount = 0;

		for (CompanyStatistics companyStatistics :
				_serverStatisticsHelper.getCompanyStatisticsSet()) {

			RequestStatistics requestStatistics =
				companyStatistics.getRequestStatistics();

			successCount += requestStatistics.getSuccessCount();
		}

		return successCount;
	}

	@Override
	public long getSuccessCountByCompany(long companyId)
		throws MonitoringException {

		RequestStatistics requestStatistics = getRequestStatistics(companyId);

		return requestStatistics.getSuccessCount();
	}

	@Override
	public long getSuccessCountByCompany(String webId)
		throws MonitoringException {

		RequestStatistics requestStatistics = getRequestStatistics(webId);

		return requestStatistics.getSuccessCount();
	}

	@Override
	public long getTimeoutCount() {
		int timeoutCount = 0;

		for (CompanyStatistics companyStatistics :
				_serverStatisticsHelper.getCompanyStatisticsSet()) {

			RequestStatistics requestStatistics =
				companyStatistics.getRequestStatistics();

			timeoutCount += requestStatistics.getTimeoutCount();
		}

		return timeoutCount;
	}

	@Override
	public long getTimeoutCountByCompany(long companyId)
		throws MonitoringException {

		RequestStatistics requestStatistics = getRequestStatistics(companyId);

		return requestStatistics.getTimeoutCount();
	}

	@Override
	public long getTimeoutCountByCompany(String webId)
		throws MonitoringException {

		RequestStatistics requestStatistics = getRequestStatistics(webId);

		return requestStatistics.getTimeoutCount();
	}

	protected RequestStatistics getRequestStatistics(long companyId)
		throws MonitoringException {

		try {
			CompanyStatistics companyStatistics =
				_serverStatisticsHelper.getCompanyStatistics(companyId);

			return companyStatistics.getRequestStatistics();
		}
		catch (Exception exception) {
			throw new MonitoringException(
				"Unable to get company with company ID " + companyId,
				exception);
		}
	}

	protected RequestStatistics getRequestStatistics(String webId)
		throws MonitoringException {

		try {
			CompanyStatistics companyStatistics =
				_serverStatisticsHelper.getCompanyStatistics(webId);

			return companyStatistics.getRequestStatistics();
		}
		catch (Exception exception) {
			throw new MonitoringException(
				"Unable to get company with web ID " + webId, exception);
		}
	}

	private final ServerStatisticsHelper _serverStatisticsHelper;

}