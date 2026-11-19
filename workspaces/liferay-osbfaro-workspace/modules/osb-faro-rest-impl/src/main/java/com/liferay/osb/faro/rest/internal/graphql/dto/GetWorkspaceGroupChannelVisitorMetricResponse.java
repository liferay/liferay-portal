/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.graphql.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Caio Pinheiro
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetWorkspaceGroupChannelVisitorMetricResponse {

	public VisitorMetric getVisitorMetric() {
		return _visitorMetric;
	}

	public void setVisitorMetric(VisitorMetric visitorMetric) {
		_visitorMetric = visitorMetric;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class VisitorMetric {

		public Long getAverageSessionDuration() {
			return _averageSessionDuration;
		}

		public Long getKnownVisitors() {
			return _knownVisitors;
		}

		public Double getTotalSessionDuration() {
			return _totalSessionDuration;
		}

		public Long getVisitors() {
			return _visitors;
		}

		public void setAverageSessionDuration(Long averageSessionDuration) {
			_averageSessionDuration = averageSessionDuration;
		}

		public void setKnownVisitors(Long knownVisitors) {
			_knownVisitors = knownVisitors;
		}

		public void setTotalSessionDuration(Double totalSessionDuration) {
			_totalSessionDuration = totalSessionDuration;
		}

		public void setVisitors(Long visitors) {
			_visitors = visitors;
		}

		private Long _averageSessionDuration;
		private Long _knownVisitors;
		private Double _totalSessionDuration;
		private Long _visitors;

	}

	private VisitorMetric _visitorMetric;

}