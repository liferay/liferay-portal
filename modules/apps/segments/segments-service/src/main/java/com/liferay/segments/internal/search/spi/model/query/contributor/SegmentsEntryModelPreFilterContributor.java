/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.search.spi.model.query.contributor;

import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.spi.model.query.contributor.ModelPreFilterContributor;
import com.liferay.portal.search.spi.model.registrar.ModelSearchSettings;

import java.util.LinkedHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * @author Drew Brokke
 */
@Component(
	property = "indexer.class.name=com.liferay.segments.model.SegmentsEntry",
	service = ModelPreFilterContributor.class
)
public class SegmentsEntryModelPreFilterContributor
	implements ModelPreFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, ModelSearchSettings modelSearchSettings,
		SearchContext searchContext) {

		LinkedHashMap<String, Object> params =
			(LinkedHashMap<String, Object>)searchContext.getAttribute("params");

		if (params == null) {
			return;
		}

		long[] excludedSegmentsEntryIds = (long[])params.get(
			"excludedSegmentsEntryIds");

		if (ArrayUtil.isNotEmpty(excludedSegmentsEntryIds)) {
			TermsFilter entryClassPKTermFilter = new TermsFilter(
				Field.ENTRY_CLASS_PK);

			entryClassPKTermFilter.addValues(
				ArrayUtil.toStringArray(excludedSegmentsEntryIds));

			booleanFilter.add(
				entryClassPKTermFilter, BooleanClauseOccur.MUST_NOT);
		}

		String[] excludedSources = (String[])params.get("excludedSources");

		if (ArrayUtil.isNotEmpty(excludedSources)) {
			TermsFilter sourceTermsFilter = new TermsFilter("source");

			sourceTermsFilter.addValues(
				ArrayUtil.toStringArray(excludedSources));

			booleanFilter.add(sourceTermsFilter, BooleanClauseOccur.MUST_NOT);
		}

		String[] excludedTypes = (String[])params.get("excludedTypes");

		if (ArrayUtil.isNotEmpty(excludedTypes)) {
			TermsFilter typeTermsFilter = new TermsFilter("type");

			typeTermsFilter.addValues(ArrayUtil.toStringArray(excludedTypes));

			booleanFilter.add(typeTermsFilter, BooleanClauseOccur.MUST_NOT);
		}

		long[] roleIds = (long[])params.get("roleIds");

		if (ArrayUtil.isNotEmpty(roleIds)) {
			TermsFilter roleIdsTermsFilter = new TermsFilter("roleIds");

			roleIdsTermsFilter.addValues(ArrayUtil.toStringArray(roleIds));

			booleanFilter.add(roleIdsTermsFilter, BooleanClauseOccur.MUST);
		}
	}

}