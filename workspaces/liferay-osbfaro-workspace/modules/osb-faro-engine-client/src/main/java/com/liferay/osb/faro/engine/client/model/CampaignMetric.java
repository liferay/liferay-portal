/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

/**
 * @author Riccardo Ferrari
 */
public class CampaignMetric {

	public String getMetricType() {
		return _metricType;
	}

	public Trend getTrend() {
		return _trend;
	}

	public Double getValue() {
		return _value;
	}

	public void setMetricType(String metricType) {
		_metricType = metricType;
	}

	public void setTrend(Trend trend) {
		_trend = trend;
	}

	public void setValue(Double value) {
		_value = value;
	}

	private String _metricType;
	private Trend _trend;
	private Double _value;

}