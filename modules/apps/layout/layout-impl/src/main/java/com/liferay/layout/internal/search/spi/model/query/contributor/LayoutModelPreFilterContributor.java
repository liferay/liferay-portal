/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.search.spi.model.query.contributor;

import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.spi.model.query.contributor.ModelPreFilterContributor;
import com.liferay.portal.search.spi.model.registrar.ModelSearchSettings;

import org.osgi.service.component.annotations.Component;

/**
 * @author Vagner B.C
 */
@Component(
	property = "indexer.class.name=com.liferay.portal.kernel.model.Layout",
	service = ModelPreFilterContributor.class
)
public class LayoutModelPreFilterContributor
	implements ModelPreFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, ModelSearchSettings modelSearchSettings,
		SearchContext searchContext) {

		String[] types = GetterUtil.getStringValues(
			searchContext.getAttribute(Field.TYPE),
			new String[] {
				LayoutConstants.TYPE_CONTENT, LayoutConstants.TYPE_EMBEDDED,
				LayoutConstants.TYPE_EMPTY, LayoutConstants.TYPE_LINK_TO_LAYOUT,
				LayoutConstants.TYPE_FULL_PAGE_APPLICATION,
				LayoutConstants.TYPE_PANEL, LayoutConstants.TYPE_PORTLET,
				LayoutConstants.TYPE_URL, LayoutConstants.TYPE_UTILITY
			});

		if (ArrayUtil.isNotEmpty(types)) {
			TermsFilter typeTermsFilter = new TermsFilter(Field.TYPE);

			typeTermsFilter.addValues(types);

			booleanFilter.add(typeTermsFilter, BooleanClauseOccur.MUST);
		}

		String privateLayout = (String)searchContext.getAttribute(
			"privateLayout");

		if (Validator.isNotNull(privateLayout)) {
			TermFilter privateLayoutTermFilter = new TermFilter(
				"privateLayout", privateLayout);

			booleanFilter.add(privateLayoutTermFilter, BooleanClauseOccur.MUST);
		}

		String systemLayout = (String)searchContext.getAttribute(
			"systemLayout");

		if (Validator.isNotNull(systemLayout)) {
			TermFilter systemLayoutTermFilter = new TermFilter(
				"systemLayout", systemLayout);

			booleanFilter.add(systemLayoutTermFilter, BooleanClauseOccur.MUST);
		}

		int[] statuses = GetterUtil.getIntegerValues(
			searchContext.getAttribute(Field.STATUS));

		if (ArrayUtil.isEmpty(statuses)) {
			int status = GetterUtil.getInteger(
				searchContext.getAttribute(Field.STATUS),
				WorkflowConstants.STATUS_APPROVED);

			statuses = new int[] {status};
		}

		if (!ArrayUtil.contains(statuses, WorkflowConstants.STATUS_ANY)) {
			TermsFilter statusesTermsFilter = new TermsFilter(Field.STATUS);

			statusesTermsFilter.addValues(ArrayUtil.toStringArray(statuses));

			booleanFilter.add(statusesTermsFilter, BooleanClauseOccur.MUST);
		}
	}

}