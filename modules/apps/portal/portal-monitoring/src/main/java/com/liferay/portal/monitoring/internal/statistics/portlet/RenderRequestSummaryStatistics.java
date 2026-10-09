/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.monitoring.internal.statistics.portlet;

import com.liferay.portal.kernel.monitoring.MonitoringException;
import com.liferay.portal.monitoring.internal.statistics.RequestStatistics;

import java.util.Set;

/**
 * @author Michael C. Han
 * @author Brian Wing Shun Chan
 */
public class RenderRequestSummaryStatistics
	implements PortletSummaryStatistics {

	public RenderRequestSummaryStatistics(
		ServerStatisticsHelper serverStatisticsHelper) {

		_serverStatisticsHelper = serverStatisticsHelper;
	}

	@Override
	public long getAverageTime() {
		long averageTime = 0;

		long count = 0;

		for (CompanyStatistics companyStatistics :
				_serverStatisticsHelper.getCompanyStatisticsSet()) {

			for (RequestStatistics requestStatistics :
					companyStatistics.getRenderRequestStatisticsSet()) {

				averageTime += requestStatistics.getAverageTime();

				count++;
			}
		}

		if (count > 0) {
			return averageTime / count;
		}

		return 0;
	}

	@Override
	public long getAverageTimeByCompany(long companyId)
		throws MonitoringException {

		return getAverageTimeByCompany(
			_serverStatisticsHelper.getCompanyStatistics(companyId));
	}

	@Override
	public long getAverageTimeByCompany(String webId)
		throws MonitoringException {

		return getAverageTimeByCompany(
			_serverStatisticsHelper.getCompanyStatistics(webId));
	}

	@Override
	public long getAverageTimeByPortlet(String portletId)
		throws MonitoringException {

		long averageTime = 0;

		Set<CompanyStatistics> companyStatisticsSet =
			_serverStatisticsHelper.getCompanyStatisticsSet();

		for (CompanyStatistics companyStatistics : companyStatisticsSet) {
			RequestStatistics requestStatistics =
				companyStatistics.getRenderRequestStatistics(portletId);

			averageTime += requestStatistics.getAverageTime();
		}

		if (!companyStatisticsSet.isEmpty()) {
			return averageTime / companyStatisticsSet.size();
		}

		return averageTime;
	}

	@Override
	public long getAverageTimeByPortlet(String portletId, long companyId)
		throws MonitoringException {

		CompanyStatistics companyStatistics =
			_serverStatisticsHelper.getCompanyStatistics(companyId);

		RequestStatistics requestStatistics =
			companyStatistics.getRenderRequestStatistics(portletId);

		return requestStatistics.getAverageTime();
	}

	@Override
	public long getAverageTimeByPortlet(String portletId, String webId)
		throws MonitoringException {

		CompanyStatistics companyStatistics =
			_serverStatisticsHelper.getCompanyStatistics(webId);

		RequestStatistics requestStatistics =
			companyStatistics.getRenderRequestStatistics(portletId);

		return requestStatistics.getAverageTime();
	}

	@Override
	public long getErrorCount() {
		long errorCount = 0;

		for (CompanyStatistics companyStatistics :
				_serverStatisticsHelper.getCompanyStatisticsSet()) {

			errorCount += getErrorCountByCompany(companyStatistics);
		}

		return errorCount;
	}

	@Override
	public long getErrorCountByCompany(long companyId)
		throws MonitoringException {

		return getErrorCountByCompany(
			_serverStatisticsHelper.getCompanyStatistics(companyId));
	}

	@Override
	public long getErrorCountByCompany(String webId)
		throws MonitoringException {

		return getErrorCountByCompany(
			_serverStatisticsHelper.getCompanyStatistics(webId));
	}

	@Override
	public long getErrorCountByPortlet(String portletId)
		throws MonitoringException {

		long errorCount = 0;

		for (CompanyStatistics companyStatistics :
				_serverStatisticsHelper.getCompanyStatisticsSet()) {

			errorCount += getErrorCountByPortlet(portletId, companyStatistics);
		}

		return errorCount;
	}

	@Override
	public long getErrorCountByPortlet(String portletId, long companyId)
		throws MonitoringException {

		return getErrorCountByPortlet(
			portletId, _serverStatisticsHelper.getCompanyStatistics(companyId));
	}

	@Override
	public long getErrorCountByPortlet(String portletId, String webId)
		throws MonitoringException {

		return getErrorCountByPortlet(
			portletId, _serverStatisticsHelper.getCompanyStatistics(webId));
	}

	@Override
	public long getMaxTime() {
		long maxTime = 0;

		for (CompanyStatistics companyStatistics :
				_serverStatisticsHelper.getCompanyStatisticsSet()) {

			for (RequestStatistics requestStatistics :
					companyStatistics.getRenderRequestStatisticsSet()) {

				if (requestStatistics.getMaxTime() <= maxTime) {
					continue;
				}

				maxTime = requestStatistics.getMaxTime();
			}
		}

		return maxTime;
	}

	@Override
	public long getMaxTimeByCompany(long companyId) throws MonitoringException {
		CompanyStatistics companyStatistics =
			_serverStatisticsHelper.getCompanyStatistics(companyId);

		return companyStatistics.getMaxTime();
	}

	@Override
	public long getMaxTimeByCompany(String webId) throws MonitoringException {
		CompanyStatistics companyStatistics =
			_serverStatisticsHelper.getCompanyStatistics(webId);

		return companyStatistics.getMaxTime();
	}

	@Override
	public long getMaxTimeByPortlet(String portletId)
		throws MonitoringException {

		long maxTime = 0;

		for (CompanyStatistics companyStatistics :
				_serverStatisticsHelper.getCompanyStatisticsSet()) {

			long curMaxTime = getMaxTimeByPortlet(portletId, companyStatistics);

			if (curMaxTime <= maxTime) {
				continue;
			}

			maxTime = curMaxTime;
		}

		return maxTime;
	}

	@Override
	public long getMaxTimeByPortlet(String portletId, long companyId)
		throws MonitoringException {

		return getMaxTimeByPortlet(
			portletId, _serverStatisticsHelper.getCompanyStatistics(companyId));
	}

	@Override
	public long getMaxTimeByPortlet(String portletId, String webId)
		throws MonitoringException {

		return getMaxTimeByPortlet(
			portletId, _serverStatisticsHelper.getCompanyStatistics(webId));
	}

	@Override
	public long getMinTime() {
		long minTime = 0;

		for (CompanyStatistics companyStatistics :
				_serverStatisticsHelper.getCompanyStatisticsSet()) {

			for (RequestStatistics requestStatistics :
					companyStatistics.getRenderRequestStatisticsSet()) {

				if (requestStatistics.getMinTime() >= minTime) {
					continue;
				}

				minTime = requestStatistics.getMinTime();
			}
		}

		return minTime;
	}

	@Override
	public long getMinTimeByCompany(long companyId) throws MonitoringException {
		CompanyStatistics companyStatistics =
			_serverStatisticsHelper.getCompanyStatistics(companyId);

		return companyStatistics.getMinTime();
	}

	@Override
	public long getMinTimeByCompany(String webId) throws MonitoringException {
		CompanyStatistics companyStatistics =
			_serverStatisticsHelper.getCompanyStatistics(webId);

		return companyStatistics.getMinTime();
	}

	@Override
	public long getMinTimeByPortlet(String portletId)
		throws MonitoringException {

		long minTime = 0;

		for (CompanyStatistics companyStatistics :
				_serverStatisticsHelper.getCompanyStatisticsSet()) {

			long curMinTime = getMinTimeByPortlet(portletId, companyStatistics);

			if (curMinTime >= minTime) {
				continue;
			}

			minTime = curMinTime;
		}

		return minTime;
	}

	@Override
	public long getMinTimeByPortlet(String portletId, long companyId)
		throws MonitoringException {

		return getMinTimeByPortlet(
			portletId, _serverStatisticsHelper.getCompanyStatistics(companyId));
	}

	@Override
	public long getMinTimeByPortlet(String portletId, String webId)
		throws MonitoringException {

		return getMinTimeByPortlet(
			portletId, _serverStatisticsHelper.getCompanyStatistics(webId));
	}

	@Override
	public long getRequestCount() {
		long requestCount = 0;

		for (CompanyStatistics companyStatistics :
				_serverStatisticsHelper.getCompanyStatisticsSet()) {

			requestCount += getRequestCountByCompany(companyStatistics);
		}

		return requestCount;
	}

	@Override
	public long getRequestCountByCompany(long companyId)
		throws MonitoringException {

		return getRequestCountByCompany(
			_serverStatisticsHelper.getCompanyStatistics(companyId));
	}

	@Override
	public long getRequestCountByCompany(String webId)
		throws MonitoringException {

		return getRequestCountByCompany(
			_serverStatisticsHelper.getCompanyStatistics(webId));
	}

	@Override
	public long getRequestCountByPortlet(String portletId)
		throws MonitoringException {

		long requestCount = 0;

		for (CompanyStatistics companyStatistics :
				_serverStatisticsHelper.getCompanyStatisticsSet()) {

			requestCount += getRequestCountByPortlet(
				portletId, companyStatistics);
		}

		return requestCount;
	}

	@Override
	public long getRequestCountByPortlet(String portletId, long companyId)
		throws MonitoringException {

		return getRequestCountByPortlet(
			portletId, _serverStatisticsHelper.getCompanyStatistics(companyId));
	}

	@Override
	public long getRequestCountByPortlet(String portletId, String webId)
		throws MonitoringException {

		return getRequestCountByPortlet(
			portletId, _serverStatisticsHelper.getCompanyStatistics(webId));
	}

	@Override
	public long getSuccessCount() {
		long successCount = 0;

		for (CompanyStatistics companyStatistics :
				_serverStatisticsHelper.getCompanyStatisticsSet()) {

			successCount += getSuccessCountByCompany(companyStatistics);
		}

		return successCount;
	}

	@Override
	public long getSuccessCountByCompany(long companyId)
		throws MonitoringException {

		return getSuccessCountByCompany(
			_serverStatisticsHelper.getCompanyStatistics(companyId));
	}

	@Override
	public long getSuccessCountByCompany(String webId)
		throws MonitoringException {

		return getSuccessCountByCompany(
			_serverStatisticsHelper.getCompanyStatistics(webId));
	}

	@Override
	public long getSuccessCountByPortlet(String portletId)
		throws MonitoringException {

		long successCount = 0;

		for (CompanyStatistics companyStatistics :
				_serverStatisticsHelper.getCompanyStatisticsSet()) {

			successCount += getSuccessCountByPortlet(
				portletId, companyStatistics);
		}

		return successCount;
	}

	@Override
	public long getSuccessCountByPortlet(String portletId, long companyId)
		throws MonitoringException {

		return getSuccessCountByPortlet(
			portletId, _serverStatisticsHelper.getCompanyStatistics(companyId));
	}

	@Override
	public long getSuccessCountByPortlet(String portletId, String webId)
		throws MonitoringException {

		return getSuccessCountByPortlet(
			portletId, _serverStatisticsHelper.getCompanyStatistics(webId));
	}

	@Override
	public long getTimeoutCount() {
		long timeoutCount = 0;

		for (CompanyStatistics companyStatistics :
				_serverStatisticsHelper.getCompanyStatisticsSet()) {

			timeoutCount += getTimeoutCountByCompany(companyStatistics);
		}

		return timeoutCount;
	}

	@Override
	public long getTimeoutCountByCompany(long companyId)
		throws MonitoringException {

		return getTimeoutCountByCompany(
			_serverStatisticsHelper.getCompanyStatistics(companyId));
	}

	@Override
	public long getTimeoutCountByCompany(String webId)
		throws MonitoringException {

		return getTimeoutCountByCompany(
			_serverStatisticsHelper.getCompanyStatistics(webId));
	}

	@Override
	public long getTimeoutCountByPortlet(String portletId)
		throws MonitoringException {

		long timeoutCount = 0;

		for (CompanyStatistics companyStatistics :
				_serverStatisticsHelper.getCompanyStatisticsSet()) {

			timeoutCount += getTimeoutCountByPortlet(
				portletId, companyStatistics);
		}

		return timeoutCount;
	}

	@Override
	public long getTimeoutCountByPortlet(String portletId, long companyId)
		throws MonitoringException {

		return getTimeoutCountByPortlet(
			portletId, _serverStatisticsHelper.getCompanyStatistics(companyId));
	}

	@Override
	public long getTimeoutCountByPortlet(String portletId, String webId)
		throws MonitoringException {

		return getTimeoutCountByPortlet(
			portletId, _serverStatisticsHelper.getCompanyStatistics(webId));
	}

	protected long getAverageTimeByCompany(
		CompanyStatistics companyStatistics) {

		long averageTime = 0;

		Set<RequestStatistics> requestStatisticsSet =
			companyStatistics.getRenderRequestStatisticsSet();

		for (RequestStatistics requestStatistics : requestStatisticsSet) {
			averageTime += requestStatistics.getAverageTime();
		}

		if (!requestStatisticsSet.isEmpty()) {
			return averageTime / requestStatisticsSet.size();
		}

		return averageTime;
	}

	protected long getErrorCountByCompany(CompanyStatistics companyStatistics) {
		long errorCount = 0;

		for (RequestStatistics requestStatistics :
				companyStatistics.getRenderRequestStatisticsSet()) {

			errorCount += requestStatistics.getErrorCount();
		}

		return errorCount;
	}

	protected long getErrorCountByPortlet(
			String portletId, CompanyStatistics companyStatistics)
		throws MonitoringException {

		RequestStatistics requestStatistics =
			companyStatistics.getRenderRequestStatistics(portletId);

		return requestStatistics.getErrorCount();
	}

	protected long getMaxTimeByPortlet(
			String portletId, CompanyStatistics companyStatistics)
		throws MonitoringException {

		long maxTime = 0;

		RequestStatistics requestStatistics =
			companyStatistics.getRenderRequestStatistics(portletId);

		if (requestStatistics.getMaxTime() > maxTime) {
			maxTime = requestStatistics.getMaxTime();
		}

		return maxTime;
	}

	protected long getMinTimeByPortlet(
			String portletId, CompanyStatistics companyStatistics)
		throws MonitoringException {

		long minTime = 0;

		RequestStatistics requestStatistics =
			companyStatistics.getRenderRequestStatistics(portletId);

		if (requestStatistics.getMinTime() < minTime) {
			minTime = requestStatistics.getMinTime();
		}

		return minTime;
	}

	protected long getRequestCountByCompany(
		CompanyStatistics companyStatistics) {

		long requestCount = 0;

		for (RequestStatistics requestStatistics :
				companyStatistics.getRenderRequestStatisticsSet()) {

			requestCount += requestStatistics.getRequestCount();
		}

		return requestCount;
	}

	protected long getRequestCountByPortlet(
			String portletId, CompanyStatistics companyStatistics)
		throws MonitoringException {

		RequestStatistics requestStatistics =
			companyStatistics.getRenderRequestStatistics(portletId);

		return requestStatistics.getRequestCount();
	}

	protected long getSuccessCountByCompany(
		CompanyStatistics companyStatistics) {

		long successCount = 0;

		for (RequestStatistics requestStatistics :
				companyStatistics.getRenderRequestStatisticsSet()) {

			successCount += requestStatistics.getSuccessCount();
		}

		return successCount;
	}

	protected long getSuccessCountByPortlet(
			String portletId, CompanyStatistics companyStatistics)
		throws MonitoringException {

		RequestStatistics requestStatistics =
			companyStatistics.getRenderRequestStatistics(portletId);

		return requestStatistics.getSuccessCount();
	}

	protected long getTimeoutCountByCompany(
		CompanyStatistics companyStatistics) {

		long timeoutCount = 0;

		for (RequestStatistics requestStatistics :
				companyStatistics.getRenderRequestStatisticsSet()) {

			timeoutCount += requestStatistics.getTimeoutCount();
		}

		return timeoutCount;
	}

	protected long getTimeoutCountByPortlet(
			String portletId, CompanyStatistics companyStatistics)
		throws MonitoringException {

		RequestStatistics requestStatistics =
			companyStatistics.getRenderRequestStatistics(portletId);

		return requestStatistics.getTimeoutCount();
	}

	private final ServerStatisticsHelper _serverStatisticsHelper;

}