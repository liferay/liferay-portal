/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.search.spi.model.query.contributor;

import com.liferay.commerce.product.constants.CPField;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.spi.model.query.contributor.ModelPreFilterContributor;
import com.liferay.portal.search.spi.model.registrar.ModelSearchSettings;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "indexer.class.name=com.liferay.commerce.product.model.CPSpecificationOption",
	service = ModelPreFilterContributor.class
)
public class CPSpecificationOptionModelPreFilterContributor
	implements ModelPreFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, ModelSearchSettings modelSearchSettings,
		SearchContext searchContext) {

		_filterByFacetable(booleanFilter, searchContext);
		_filterByVisible(booleanFilter, searchContext);
	}

	private void _filterByFacetable(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		Map<String, Serializable> attributes = searchContext.getAttributes();

		Serializable facetable = attributes.get(CPField.FACETABLE);

		if (facetable != null) {
			booleanFilter.addRequiredTerm(
				CPField.FACETABLE, GetterUtil.getBoolean(facetable));
		}
	}

	private void _filterByVisible(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		Map<String, Serializable> attributes = searchContext.getAttributes();

		Serializable visible = attributes.get(CPField.VISIBLE);

		if (visible != null) {
			booleanFilter.addRequiredTerm(
				CPField.VISIBLE, GetterUtil.getBoolean(visible));
		}
	}

}