/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.filter;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.object.model.ObjectEntry;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.TermsAggregationResult;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.ALL_SECTION,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.FILES_SECTION,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.RECYCLE_BIN_SECTION,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.VIEW_FILES_FOLDER,
		"service.ranking:Integer=97"
	},
	service = FDSFilter.class
)
public class ExtensionSelectionFDSFilter extends BaseSelectionFDSFilter {

	@Override
	public String getEntityFieldType() {
		return FDSEntityFieldTypes.STRING;
	}

	@Override
	public String getId() {
		return "extension";
	}

	@Override
	public String getLabel() {
		return "extension";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		return TransformUtil.transform(
			_getExistingFileExtensions(),
			fileExtension -> new SelectionFDSFilterItem(
				fileExtension, fileExtension));
	}

	private List<String> _getExistingFileExtensions() {
		SearchContext searchContext = new SearchContext();

		long companyId = CompanyThreadLocal.getCompanyId();

		searchContext.setCompanyId(companyId);

		searchContext.setEnd(QueryUtil.ALL_POS);

		long[] groupIds = TransformUtil.transformToLongArray(
			_depotEntryLocalService.getDepotEntries(
				companyId, DepotConstants.TYPE_SPACE),
			DepotEntry::getGroupId);

		if ((groupIds != null) && ArrayUtil.isNotEmpty(groupIds)) {
			searchContext.setGroupIds(groupIds);
		}

		searchContext.setStart(0);

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
				searchContext
			).emptySearchEnabled(
				true
			).entryClassNames(
				ObjectEntry.class.getName()
			).fields(
				Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK, Field.UID
			).highlightEnabled(
				false
			);

		SearchResponse searchResponse = _searcher.search(
			searchRequestBuilder.addAggregation(
				_aggregations.terms("extensions", "extension")
			).size(
				0
			).build());

		TermsAggregationResult termsAggregationResult =
			(TermsAggregationResult)searchResponse.getAggregationResult(
				"extensions");

		return TransformUtil.transform(
			termsAggregationResult.getBuckets(), Bucket::getKey);
	}

	@Reference
	private Aggregations _aggregations;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private Searcher _searcher;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

}