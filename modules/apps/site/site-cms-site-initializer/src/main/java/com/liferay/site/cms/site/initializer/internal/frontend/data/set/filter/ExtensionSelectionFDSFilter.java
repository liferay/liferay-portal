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
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.TermsAggregationResult;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.ALL_RELATED_ASSETS_SECTION,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.ALL_SECTION,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.FILES_SECTION,
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
		long companyId = CompanyThreadLocal.getCompanyId();

		long[] groupIds = TransformUtil.transformToLongArray(
			_depotEntryLocalService.getDepotEntries(
				companyId, DepotConstants.TYPE_SPACE),
			DepotEntry::getGroupId);

		List<ObjectDefinition> objectDefinitions =
			_objectDefinitionService.getCMSObjectDefinitions(
				companyId,
				new String[] {
					ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES
				});

		if (objectDefinitions.isEmpty()) {
			return Collections.emptyList();
		}

		SearchResponse searchResponse = _searcher.search(
			_searchRequestBuilderFactory.builder(
			).addAggregation(
				_aggregations.terms("extensions", "extension")
			).addComplexQueryPart(
				_complexQueryPartBuilderFactory.builder(
				).query(
					QueriesUtil.matchAll()
				).build()
			).companyId(
				companyId
			).emptySearchEnabled(
				true
			).entryClassNames(
				TransformUtil.transformToArray(
					objectDefinitions, ObjectDefinition::getClassName,
					String.class)
			).groupIds(
				groupIds
			).size(
				0
			).build());

		TermsAggregationResult termsAggregationResult =
			(TermsAggregationResult)searchResponse.getAggregationResult(
				"extensions");

		if (termsAggregationResult == null) {
			return Collections.emptyList();
		}

		return TransformUtil.transform(
			termsAggregationResult.getBuckets(), Bucket::getKey);
	}

	@Reference
	private Aggregations _aggregations;

	@Reference
	private ComplexQueryPartBuilderFactory _complexQueryPartBuilderFactory;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private ObjectDefinitionService _objectDefinitionService;

	@Reference
	private Searcher _searcher;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

}