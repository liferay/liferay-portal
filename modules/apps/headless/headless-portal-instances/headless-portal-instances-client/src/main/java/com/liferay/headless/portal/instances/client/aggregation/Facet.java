/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.client.aggregation;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Generated;

/**
 * @author Alberto Chaparro
 * @generated
 */
@Generated("")
public class Facet {

	public Facet() {
	}

	public Facet(String facetCriteria, List<FacetValue> facetValues) {
		_facetCriteria = facetCriteria;
		_facetValues = facetValues;
	}

	public String getFacetCriteria() {
		return _facetCriteria;
	}

	public List<FacetValue> getFacetValues() {
		return _facetValues;
	}

	public void setFacetCriteria(String facetCriteria) {
		_facetCriteria = facetCriteria;
	}

	public void setFacetValues(List<FacetValue> facetValues) {
		_facetValues = facetValues;
	}

	public static class FacetValue {

		public FacetValue() {
		}

		public FacetValue(Integer numberOfOccurrences, String term) {
			_numberOfOccurrences = numberOfOccurrences;
			_term = term;
		}

		public Integer getNumberOfOccurrences() {
			return _numberOfOccurrences;
		}

		public String getTerm() {
			return _term;
		}

		private Integer _numberOfOccurrences;
		private String _term;

	}

	private String _facetCriteria;
	private List<FacetValue> _facetValues = new ArrayList<>();

}