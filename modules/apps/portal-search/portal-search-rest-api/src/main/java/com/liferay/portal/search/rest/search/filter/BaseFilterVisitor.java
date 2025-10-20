/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.search.filter;

import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.DateRangeTermFilter;
import com.liferay.portal.kernel.search.filter.ExistsFilter;
import com.liferay.portal.kernel.search.filter.FilterVisitor;
import com.liferay.portal.kernel.search.filter.GeoBoundingBoxFilter;
import com.liferay.portal.kernel.search.filter.GeoDistanceFilter;
import com.liferay.portal.kernel.search.filter.GeoDistanceRangeFilter;
import com.liferay.portal.kernel.search.filter.GeoPolygonFilter;
import com.liferay.portal.kernel.search.filter.MissingFilter;
import com.liferay.portal.kernel.search.filter.PrefixFilter;
import com.liferay.portal.kernel.search.filter.QueryFilter;
import com.liferay.portal.kernel.search.filter.RangeTermFilter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;

/**
 * @author Adolfo Pérez
 */
public abstract class BaseFilterVisitor<T> implements FilterVisitor<T> {

	@Override
	public T visit(BooleanFilter booleanFilter) {
		return null;
	}

	@Override
	public T visit(DateRangeTermFilter dateRangeTermFilter) {
		return null;
	}

	@Override
	public T visit(ExistsFilter existsFilter) {
		return null;
	}

	@Override
	public T visit(GeoBoundingBoxFilter geoBoundingBoxFilter) {
		return null;
	}

	@Override
	public T visit(GeoDistanceFilter geoDistanceFilter) {
		return null;
	}

	@Override
	public T visit(GeoDistanceRangeFilter geoDistanceRangeFilter) {
		return null;
	}

	@Override
	public T visit(GeoPolygonFilter geoPolygonFilter) {
		return null;
	}

	@Override
	public T visit(MissingFilter missingFilter) {
		return null;
	}

	@Override
	public T visit(PrefixFilter prefixFilter) {
		return null;
	}

	@Override
	public T visit(QueryFilter queryFilter) {
		return null;
	}

	@Override
	public T visit(RangeTermFilter rangeTermFilter) {
		return null;
	}

	@Override
	public T visit(TermFilter termFilter) {
		return null;
	}

	@Override
	public T visit(TermsFilter termsFilter) {
		return null;
	}

}