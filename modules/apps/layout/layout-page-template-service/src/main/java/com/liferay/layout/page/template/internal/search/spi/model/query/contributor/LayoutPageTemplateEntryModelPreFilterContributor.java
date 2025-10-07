/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.search.spi.model.query.contributor;

import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.spi.model.query.contributor.ModelPreFilterContributor;
import com.liferay.portal.search.spi.model.registrar.ModelSearchSettings;

import org.osgi.service.component.annotations.Component;

/**
 * @author Juan Pablo Montero
 */
@Component(
	property = "indexer.class.name=com.liferay.layout.page.template.model.LayoutPageTemplateEntry",
	service = ModelPreFilterContributor.class
)
public class LayoutPageTemplateEntryModelPreFilterContributor
	implements ModelPreFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, ModelSearchSettings modelSearchSettings,
		SearchContext searchContext) {

		_filterByStatuses(booleanFilter, searchContext);
		_filterByTypes(booleanFilter, searchContext);
	}

	private void _filterByStatuses(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		int[] statuses = GetterUtil.getIntegerValues(
			searchContext.getAttribute(Field.STATUS));

		if (ArrayUtil.isEmpty(statuses) ||
			ArrayUtil.contains(statuses, WorkflowConstants.STATUS_ANY)) {

			return;
		}

		TermsFilter termsFilter = new TermsFilter(Field.STATUS);

		termsFilter.addValues(ArrayUtil.toStringArray(statuses));

		booleanFilter.add(termsFilter, BooleanClauseOccur.MUST);
	}

	private void _filterByTypes(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		String[] types = GetterUtil.getStringValues(
			searchContext.getAttribute("types"));

		if (ArrayUtil.isNotEmpty(types)) {
			TermsFilter termsFilter = new TermsFilter(Field.TYPE);

			termsFilter.addValues(types);

			booleanFilter.add(termsFilter, BooleanClauseOccur.MUST);
		}
	}

}