/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.provisioning.client.subscription;

/**
 * @author Matthew Kong
 */
public class FaroSubscriptionPlan {

	public FaroSubscriptionPlan(
		int apiActivationDailyLimit, int apiActivationMonthlyLimit,
		String baseSubscriptionPlan, int batchSegmentsLimit,
		int billableEventsMonthlyLimit, int connectorsLimit,
		int eventAnalysisLimit, int individualsLimit, String name,
		int pageViewsLimit, int realTimeSegmentsLimit) {

		_apiActivationDailyLimit = apiActivationDailyLimit;
		_apiActivationMonthlyLimit = apiActivationMonthlyLimit;
		_baseSubscriptionPlan = baseSubscriptionPlan;
		_batchSegmentsLimit = batchSegmentsLimit;
		_billableEventsMonthlyLimit = billableEventsMonthlyLimit;
		_connectorsLimit = connectorsLimit;
		_eventAnalysisLimit = eventAnalysisLimit;
		_individualsLimit = individualsLimit;
		_name = name;
		_pageViewsLimit = pageViewsLimit;
		_realTimeSegmentsLimit = realTimeSegmentsLimit;
	}

	public FaroSubscriptionPlan(
		String baseSubscriptionPlan, String name, int individualsLimit,
		int pageViewsLimit) {

		_baseSubscriptionPlan = baseSubscriptionPlan;
		_name = name;
		_individualsLimit = individualsLimit;
		_pageViewsLimit = pageViewsLimit;
	}

	public int getApiActivationDailyLimit() {
		return _apiActivationDailyLimit;
	}

	public int getApiActivationMonthlyLimit() {
		return _apiActivationMonthlyLimit;
	}

	public String getBaseSubscriptionPlan() {
		return _baseSubscriptionPlan;
	}

	public int getBatchSegmentsLimit() {
		return _batchSegmentsLimit;
	}

	public int getBillableEventsMonthlyLimit() {
		return _billableEventsMonthlyLimit;
	}

	public int getConnectorsLimit() {
		return _connectorsLimit;
	}

	public int getEventAnalysisLimit() {
		return _eventAnalysisLimit;
	}

	public int getIndividualsLimit() {
		return _individualsLimit;
	}

	public String getName() {
		return _name;
	}

	public int getPageViewsLimit() {
		return _pageViewsLimit;
	}

	public int getRealTimeSegmentsLimit() {
		return _realTimeSegmentsLimit;
	}

	public void setApiActivationDailyLimit(int apiActivationDailyLimit) {
		_apiActivationDailyLimit = apiActivationDailyLimit;
	}

	public void setApiActivationMonthlyLimit(int apiActivationMonthlyLimit) {
		_apiActivationMonthlyLimit = apiActivationMonthlyLimit;
	}

	public void setBaseSubscriptionPlan(String baseSubscriptionPlan) {
		_baseSubscriptionPlan = baseSubscriptionPlan;
	}

	public void setBatchSegmentsLimit(int batchSegmentsLimit) {
		_batchSegmentsLimit = batchSegmentsLimit;
	}

	public void setBillableEventsMonthlyLimit(int billableEventsMonthlyLimit) {
		_billableEventsMonthlyLimit = billableEventsMonthlyLimit;
	}

	public void setConnectorsLimit(int connectorsLimit) {
		_connectorsLimit = connectorsLimit;
	}

	public void setEventAnalysisLimit(int eventAnalysisLimit) {
		_eventAnalysisLimit = eventAnalysisLimit;
	}

	public void setIndividualsLimit(int individualsLimit) {
		_individualsLimit = individualsLimit;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setPageViewsLimit(int pageViewsLimit) {
		_pageViewsLimit = pageViewsLimit;
	}

	public void setRealTimeSegmentsLimit(int realTimeSegmentsLimit) {
		_realTimeSegmentsLimit = realTimeSegmentsLimit;
	}

	private int _apiActivationDailyLimit = -1;
	private int _apiActivationMonthlyLimit = -1;
	private String _baseSubscriptionPlan;
	private int _batchSegmentsLimit = -1;
	private int _billableEventsMonthlyLimit = -1;
	private int _connectorsLimit = -1;
	private int _eventAnalysisLimit = -1;
	private int _individualsLimit;
	private String _name;
	private int _pageViewsLimit;
	private int _realTimeSegmentsLimit = -1;

}