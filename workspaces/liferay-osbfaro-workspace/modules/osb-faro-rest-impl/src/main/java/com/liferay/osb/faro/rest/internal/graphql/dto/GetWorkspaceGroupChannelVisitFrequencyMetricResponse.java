/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.graphql.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * @author Caio Pinheiro
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetWorkspaceGroupChannelVisitFrequencyMetricResponse {

	public VisitFrequencyBag getVisitFrequencyBag() {
		return _visitFrequencyBag;
	}

	public void setVisitFrequencyBag(VisitFrequencyBag visitFrequencyBag) {
		_visitFrequencyBag = visitFrequencyBag;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Composition {

		public Long getCount() {
			return _count;
		}

		public String getName() {
			return _name;
		}

		public void setCount(Long count) {
			_count = count;
		}

		public void setName(String name) {
			_name = name;
		}

		private Long _count;
		private String _name;

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class VisitFrequencyBag {

		public Long getTotalCount() {
			return _totalCount;
		}

		public List<Composition> getVisitFrequency() {
			return _visitFrequency;
		}

		public void setTotalCount(Long totalCount) {
			_totalCount = totalCount;
		}

		public void setVisitFrequency(List<Composition> visitFrequency) {
			_visitFrequency = visitFrequency;
		}

		private Long _totalCount;
		private List<Composition> _visitFrequency;

	}

	private VisitFrequencyBag _visitFrequencyBag;

}