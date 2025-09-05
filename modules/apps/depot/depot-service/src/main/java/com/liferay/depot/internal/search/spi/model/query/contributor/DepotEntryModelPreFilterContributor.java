/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.search.spi.model.query.contributor;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.spi.model.query.contributor.ModelPreFilterContributor;
import com.liferay.portal.search.spi.model.registrar.ModelSearchSettings;

import org.osgi.service.component.annotations.Component;

/**
 * @author Roberto Díaz
 */
@Component(
	property = "indexer.class.name=com.liferay.depot.model.DepotEntry",
	service = ModelPreFilterContributor.class
)
public class DepotEntryModelPreFilterContributor
	implements ModelPreFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, ModelSearchSettings modelSearchSettings,
		SearchContext searchContext) {

		int type = GetterUtil.getInteger(
			searchContext.getAttribute(Field.TYPE), DepotConstants.TYPE_ANY);

		if (type != DepotConstants.TYPE_ANY) {
			TermsFilter typeTermsFilter = new TermsFilter(Field.TYPE);

			typeTermsFilter.addValue(String.valueOf(type));

			booleanFilter.add(typeTermsFilter, BooleanClauseOccur.MUST);
		}
	}

}