/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.internal.resource.v1_0;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.bulk.rest.dto.v1_0.BulkAction;
import com.liferay.bulk.rest.dto.v1_0.DocumentBulkSelection;
import com.liferay.bulk.rest.dto.v1_0.Keyword;
import com.liferay.bulk.rest.dto.v1_0.KeywordBulkSelection;
import com.liferay.bulk.rest.internal.selection.v1_0.BulkActionBulkSelectionFactory;
import com.liferay.bulk.rest.internal.selection.v1_0.DocumentBulkSelectionFactory;
import com.liferay.bulk.rest.resource.v1_0.KeywordResource;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.bulk.selection.BulkSelectionFactoryRegistry;
import com.liferay.bulk.selection.BulkSelectionInputParameters;
import com.liferay.bulk.selection.BulkSelectionRunner;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.vulcan.pagination.Page;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alejandro Tardín
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/keyword.properties",
	scope = ServiceScope.PROTOTYPE, service = KeywordResource.class
)
@CTAware
public class KeywordResourceImpl extends BaseKeywordResourceImpl {

	@Override
	public void patchKeywordBatch(KeywordBulkSelection keywordBulkSelection)
		throws Exception {

		_update(true, keywordBulkSelection);
	}

	@Override
	public Page<Keyword> postKeywordsCommonPageObject(
			String blueprintExternalReferenceCode, Boolean emptySearch,
			String entryClassNames, String scope, String search, Filter filter,
			Sort[] sorts, Object object)
		throws Exception {

		return Page.of(
			transform(
				_getAssetTagNames(
					_getBulkSelection(
						blueprintExternalReferenceCode, emptySearch,
						entryClassNames, scope, search, filter, sorts, object),
					PermissionCheckerFactoryUtil.create(contextUser)),
				this::_toTag));
	}

	@Override
	public void putKeywordBatch(KeywordBulkSelection keywordBulkSelection)
		throws Exception {

		_update(false, keywordBulkSelection);
	}

	private Set<String> _getAssetTagNames(
			BulkSelection<?> bulkSelection, PermissionChecker permissionChecker)
		throws Exception {

		Set<String> assetTagNames = new HashSet<>();

		AtomicBoolean flag = new AtomicBoolean(true);

		BulkSelection<AssetEntry> assetEntryBulkSelection =
			bulkSelection.toAssetEntryBulkSelection();

		assetEntryBulkSelection.forEach(
			assetEntry -> {
				if (ModelResourcePermissionUtil.contains(
						permissionChecker, assetEntry.getGroupId(),
						assetEntry.getClassName(), assetEntry.getClassPK(),
						ActionKeys.UPDATE)) {

					String[] assetEntryAssetTagNames =
						_assetTagLocalService.getTagNames(
							assetEntry.getClassName(), assetEntry.getClassPK());

					if (flag.get()) {
						flag.set(false);

						Collections.addAll(
							assetTagNames, assetEntryAssetTagNames);
					}
					else {
						assetTagNames.retainAll(
							Arrays.asList(assetEntryAssetTagNames));
					}
				}
			});

		return assetTagNames;
	}

	private BulkActionBulkSelectionFactory _getBulkActionBulkSelectionFactory(
		String blueprintExternalReferenceCode, BulkAction bulkAction,
		Boolean emptySearch, String entryClassNames, Filter filter,
		String scope, String search, Sort[] sorts) {

		return new BulkActionBulkSelectionFactory.Builder(
		).acceptLanguage(
			contextAcceptLanguage
		).blueprintExternalReferenceCode(
			blueprintExternalReferenceCode
		).bulkAction(
			bulkAction
		).bulkSelectionFactoryRegistry(
			_bulkSelectionFactoryRegistry
		).company(
			contextCompany
		).emptySearch(
			emptySearch
		).entryClassNames(
			entryClassNames
		).filter(
			filter
		).filterFactory(
			_filterFactory
		).groupLocalService(
			_groupLocalService
		).httpServletRequest(
			contextHttpServletRequest
		).localization(
			_localization
		).objectDefinitionLocalService(
			_objectDefinitionLocalService
		).objectEntryLocalService(
			_objectEntryLocalService
		).scope(
			scope
		).search(
			search
		).searcher(
			_searcher
		).searchRequestBuilderFactory(
			_searchRequestBuilderFactory
		).sorts(
			sorts
		).user(
			contextUser
		).build();
	}

	private BulkSelection<?> _getBulkSelection(
		String blueprintExternalReferenceCode, Boolean emptySearch,
		String entryClassNames, String scope, String search, Filter filter,
		Sort[] sorts, Object object) {

		Map<String, Object> map = (Map<String, Object>)object;

		if (map.containsKey("documentIds")) {
			return _documentBulkSelectionFactory.create(
				DocumentBulkSelection.toDTO(
					_jsonFactory.toString(_jsonFactory.createJSONObject(map))));
		}

		BulkActionBulkSelectionFactory bulkActionBulkSelectionFactory =
			_getBulkActionBulkSelectionFactory(
				blueprintExternalReferenceCode,
				BulkAction.toDTO(
					_jsonFactory.toString(_jsonFactory.createJSONObject(map))),
				emptySearch, entryClassNames, filter, scope, search, sorts);

		return bulkActionBulkSelectionFactory.create();
	}

	private Keyword _toTag(String assetTagName) {
		return new Keyword() {
			{
				setName(() -> assetTagName);
			}
		};
	}

	private void _update(
			boolean append, KeywordBulkSelection keywordBulkSelection)
		throws Exception {

		BulkSelection<?> bulkSelection = _documentBulkSelectionFactory.create(
			keywordBulkSelection.getDocumentBulkSelection());

		_bulkSelectionRunner.run(
			contextUser, bulkSelection.toAssetEntryBulkSelection(),
			_editTagsBulkSelectionAction,
			HashMapBuilder.<String, Serializable>put(
				BulkSelectionInputParameters.ASSET_ENTRY_BULK_SELECTION, true
			).put(
				"append", append
			).put(
				"toAddTagNames", keywordBulkSelection.getKeywordsToAdd()
			).put(
				"toRemoveTagNames", keywordBulkSelection.getKeywordsToRemove()
			).build());
	}

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private BulkSelectionFactoryRegistry _bulkSelectionFactoryRegistry;

	@Reference
	private BulkSelectionRunner _bulkSelectionRunner;

	@Reference
	private DocumentBulkSelectionFactory _documentBulkSelectionFactory;

	@Reference(target = "(bulk.selection.action.key=edit.tags)")
	private BulkSelectionAction<AssetEntry> _editTagsBulkSelectionAction;

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Localization _localization;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private Searcher _searcher;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

}