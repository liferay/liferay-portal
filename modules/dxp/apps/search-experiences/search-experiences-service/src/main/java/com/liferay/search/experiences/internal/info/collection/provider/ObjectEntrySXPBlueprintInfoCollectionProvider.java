/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.info.collection.provider;

import com.liferay.asset.util.AssetHelper;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.FilteredInfoCollectionProvider;
import com.liferay.info.collection.provider.SingleFormVariationInfoCollectionProvider;
import com.liferay.info.pagination.InfoPage;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.search.experiences.model.SXPBlueprint;

import java.util.Collections;
import java.util.List;

/**
 * @author Joshua Cords
 */
public class ObjectEntrySXPBlueprintInfoCollectionProvider
	extends SXPBlueprintInfoCollectionProvider<ObjectEntry>
	implements FilteredInfoCollectionProvider<ObjectEntry>,
			   SingleFormVariationInfoCollectionProvider<ObjectEntry> {

	public ObjectEntrySXPBlueprintInfoCollectionProvider(
		AssetHelper assetHelper, String className,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService, Searcher searcher,
		SearchRequestBuilderFactory searchRequestBuilderFactory,
		SXPBlueprint sxpBlueprint) {

		super(assetHelper, searcher, searchRequestBuilderFactory, sxpBlueprint);

		_objectDefinition =
			objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				sxpBlueprint.getCompanyId(), className);

		_objectEntryLocalService = objectEntryLocalService;
	}

	@Override
	public InfoPage<ObjectEntry> getCollectionInfoPage(
		CollectionQuery collectionQuery) {

		try {
			SearchResponse searchResponse = getCollectionQuerySearchResponse(
				collectionQuery);

			return InfoPage.of(
				_getObjectEntries(searchResponse.getSearchHits()),
				collectionQuery.getPagination(), searchResponse.getTotalHits());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get object entry", exception);
			}
		}

		return InfoPage.of(
			Collections.emptyList(), collectionQuery.getPagination(), 0);
	}

	@Override
	public String getCollectionItemClassName() {
		return _objectDefinition.getClassName();
	}

	@Override
	public boolean isAvailable() {
		if (_objectDefinition.getCompanyId() !=
				CompanyThreadLocal.getCompanyId()) {

			return false;
		}

		return super.isAvailable();
	}

	private List<ObjectEntry> _getObjectEntries(SearchHits searchHits) {
		return TransformUtil.transform(
			searchHits.getSearchHits(),
			searchHit -> {
				Document document = searchHit.getDocument();

				return _objectEntryLocalService.fetchObjectEntry(
					document.getLong(Field.ENTRY_CLASS_PK));
			});
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntrySXPBlueprintInfoCollectionProvider.class);

	private final ObjectDefinition _objectDefinition;
	private final ObjectEntryLocalService _objectEntryLocalService;

}