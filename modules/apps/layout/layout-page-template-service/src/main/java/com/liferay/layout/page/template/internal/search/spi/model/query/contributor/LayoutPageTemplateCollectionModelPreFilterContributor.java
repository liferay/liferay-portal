/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.search.spi.model.query.contributor;

import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.spi.model.query.contributor.ModelPreFilterContributor;
import com.liferay.portal.search.spi.model.registrar.ModelSearchSettings;

import org.osgi.service.component.annotations.Component;

/**
 * @author Roselaine Marques
 */
@Component(
	property = "indexer.class.name=com.liferay.layout.page.template.model.LayoutPageTemplateCollection",
	service = ModelPreFilterContributor.class
)
public class LayoutPageTemplateCollectionModelPreFilterContributor
	implements ModelPreFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, ModelSearchSettings modelSearchSettings,
		SearchContext searchContext) {

		_filterByType(booleanFilter, searchContext);
	}

	private void _filterByType(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		String type = GetterUtil.getString(
			searchContext.getAttribute(Field.TYPE));

		if (Validator.isNotNull(type)) {
			booleanFilter.add(
				new TermFilter(Field.TYPE, type), BooleanClauseOccur.MUST);
		}
	}

}