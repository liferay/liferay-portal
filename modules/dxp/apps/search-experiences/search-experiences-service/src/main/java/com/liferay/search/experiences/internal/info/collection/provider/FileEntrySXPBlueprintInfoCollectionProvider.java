/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.info.collection.provider;

import com.liferay.asset.util.AssetHelper;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.FilteredInfoCollectionProvider;
import com.liferay.info.collection.provider.SingleFormVariationInfoCollectionProvider;
import com.liferay.info.pagination.InfoPage;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.asset.AssetSubtypeIdentifier;
import com.liferay.portal.search.asset.AssetSubtypeIdentifierBuilder;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.search.experiences.model.SXPBlueprint;
import com.liferay.search.experiences.rest.dto.v1_0.Configuration;
import com.liferay.search.experiences.rest.dto.v1_0.GeneralConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Joshua Cords
 */
public class FileEntrySXPBlueprintInfoCollectionProvider
	extends SXPBlueprintInfoCollectionProvider<FileEntry>
	implements FilteredInfoCollectionProvider<FileEntry>,
			   SingleFormVariationInfoCollectionProvider<FileEntry> {

	public FileEntrySXPBlueprintInfoCollectionProvider(
		AssetHelper assetHelper,
		AssetSubtypeIdentifierBuilder assetSubtypeIdentifierBuilder,
		DLAppLocalService dlAppLocalService,
		DLFileEntryTypeLocalService dlFileEntryTypeLocalService,
		GroupService groupService, Searcher searcher,
		SearchRequestBuilderFactory searchRequestBuilderFactory,
		SXPBlueprint sxpBlueprint) {

		super(assetHelper, searcher, searchRequestBuilderFactory, sxpBlueprint);

		_assetSubtypeIdentifierBuilder = assetSubtypeIdentifierBuilder;
		_dlAppLocalService = dlAppLocalService;
		_dlFileEntryTypeLocalService = dlFileEntryTypeLocalService;
		_groupService = groupService;
	}

	@Override
	public InfoPage<FileEntry> getCollectionInfoPage(
		CollectionQuery collectionQuery) {

		try {
			SearchResponse searchResponse = getCollectionQuerySearchResponse(
				collectionQuery);

			return InfoPage.of(
				_getDLFileEntries(searchResponse.getSearchHits()),
				collectionQuery.getPagination(), searchResponse.getTotalHits());
		}
		catch (Exception exception) {
			_log.error("Unable to get document library file entry", exception);
		}

		return InfoPage.of(
			Collections.emptyList(), collectionQuery.getPagination(), 0);
	}

	@Override
	public String getFormVariationKey() {
		Configuration configuration = Configuration.unsafeToDTO(
			sxpBlueprint.getConfigurationJSON());

		GeneralConfiguration generalConfiguration =
			configuration.getGeneralConfiguration();

		AssetSubtypeIdentifier assetSubtypeIdentifier =
			_assetSubtypeIdentifierBuilder.searchableAssetType(
				generalConfiguration.getCollectionProviderType()
			).build();

		if (Validator.isBlank(
				assetSubtypeIdentifier.getSubtypeExternalReferenceCode())) {

			return StringPool.BLANK;
		}

		if (Validator.isBlank(
				assetSubtypeIdentifier.getGroupExternalReferenceCode())) {

			return "0";
		}

		Group group;

		try {
			group = _groupService.fetchGroupByExternalReferenceCode(
				assetSubtypeIdentifier.getGroupExternalReferenceCode(),
				sxpBlueprint.getCompanyId());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get group with external reference code " +
						assetSubtypeIdentifier.getGroupExternalReferenceCode(),
					exception);
			}

			return StringPool.BLANK;
		}

		String subtypeExternalReferenceCode =
			assetSubtypeIdentifier.getSubtypeExternalReferenceCode();

		try {
			DLFileEntryType dlFileEntryType =
				_dlFileEntryTypeLocalService.
					getDLFileEntryTypeByExternalReferenceCode(
						subtypeExternalReferenceCode, group.getGroupId());

			return String.valueOf(dlFileEntryType.getFileEntryTypeId());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get document library file entry type with " +
						"external reference code " +
							subtypeExternalReferenceCode,
					exception);
			}
		}

		return StringPool.BLANK;
	}

	private List<FileEntry> _getDLFileEntries(SearchHits searchHits)
		throws PortalException {

		List<FileEntry> fileEntries = new ArrayList<>();

		for (SearchHit searchHit : searchHits.getSearchHits()) {
			Document document = searchHit.getDocument();

			fileEntries.add(
				_dlAppLocalService.getFileEntry(
					document.getLong(Field.ENTRY_CLASS_PK)));
		}

		return fileEntries;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FileEntrySXPBlueprintInfoCollectionProvider.class);

	private final AssetSubtypeIdentifierBuilder _assetSubtypeIdentifierBuilder;
	private final DLAppLocalService _dlAppLocalService;
	private final DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;
	private final GroupService _groupService;

}