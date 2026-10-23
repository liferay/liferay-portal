/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.usage;

import com.liferay.asset.kernel.model.AssetEntryTable;
import com.liferay.asset.link.model.AssetLinkTable;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.headless.cms.internal.util.CMSSearchUtil;
import com.liferay.layout.model.LayoutClassedModelUsageTable;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.petra.sql.dsl.DynamicObjectRelationshipMappingTable;
import com.liferay.object.petra.sql.dsl.DynamicObjectRelationshipMappingTableFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassNameTable;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.site.cms.site.initializer.util.CMSOutboundLinksUtil;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.model.SiteNavigationMenuItemTable;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Mikel Lorza
 */
public class UnlinkedAssetSearcher {

	public UnlinkedAssetSearcher(
		Aggregations aggregations, AssetLinkLocalService assetLinkLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		Searcher searcher,
		SearchRequestBuilderFactory searchRequestBuilderFactory,
		SiteNavigationMenuItemLocalService siteNavigationMenuItemLocalService) {

		_aggregations = aggregations;
		_assetLinkLocalService = assetLinkLocalService;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_objectRelationshipLocalService = objectRelationshipLocalService;
		_searcher = searcher;
		_searchRequestBuilderFactory = searchRequestBuilderFactory;
		_siteNavigationMenuItemLocalService =
			siteNavigationMenuItemLocalService;
	}

	public long getCount(long companyId, Long[] groupIds) {
		SearchResponse searchResponse = _searcher.search(
			_getSearchRequestBuilder(
				companyId, groupIds
			).size(
				0
			).build());

		return searchResponse.getCount();
	}

	public SearchResponse search(
		long companyId, Long[] groupIds, String languageId,
		Pagination pagination, String search, Sort[] sorts) {

		SearchRequestBuilder searchRequestBuilder = _getSearchRequestBuilder(
			companyId, groupIds);

		searchRequestBuilder.addSelectedFieldNames(
			Field.ENTRY_CLASS_PK, "objectDefinitionId", "objectEntryTitle",
			Field.getLocalizedName(languageId, "objectEntryTitle")
		).from(
			pagination.getStartPosition()
		).size(
			pagination.getPageSize()
		);

		if (ArrayUtil.isNotEmpty(sorts)) {
			searchRequestBuilder.withSearchContext(
				searchContext -> searchContext.setSorts(sorts));
		}

		if (search != null) {
			searchRequestBuilder.queryString(search);
		}

		return _searcher.search(searchRequestBuilder.build());
	}

	private void _addAssetLinkObjectEntryIds(
		long companyId, Set<String> objectEntryIds) {

		List<Long> classPKs = _assetLinkLocalService.dslQuery(
			DSLQueryFactoryUtil.selectDistinct(
				AssetEntryTable.INSTANCE.classPK
			).from(
				AssetEntryTable.INSTANCE
			).where(
				AssetEntryTable.INSTANCE.companyId.eq(
					companyId
				).and(
					AssetEntryTable.INSTANCE.classNameId.in(
						DSLQueryFactoryUtil.select(
							ClassNameTable.INSTANCE.classNameId
						).from(
							ClassNameTable.INSTANCE
						).where(
							ClassNameTable.INSTANCE.value.like(
								ObjectDefinitionConstants.
									CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION +
										StringPool.PERCENT)
						))
				).and(
					AssetEntryTable.INSTANCE.entryId.in(
						DSLQueryFactoryUtil.select(
							AssetLinkTable.INSTANCE.entryId1
						).from(
							AssetLinkTable.INSTANCE
						).where(
							AssetLinkTable.INSTANCE.companyId.eq(companyId)
						)
					).or(
						AssetEntryTable.INSTANCE.entryId.in(
							DSLQueryFactoryUtil.select(
								AssetLinkTable.INSTANCE.entryId2
							).from(
								AssetLinkTable.INSTANCE
							).where(
								AssetLinkTable.INSTANCE.companyId.eq(companyId)
							))
					).withParentheses()
				)
			));

		objectEntryIds.addAll(
			TransformUtil.transform(classPKs, String::valueOf));
	}

	private void _addLayoutObjectEntryIds(
		long companyId, Set<String> objectEntryIds) {

		List<Long> classPKs = _objectEntryLocalService.dslQuery(
			DSLQueryFactoryUtil.selectDistinct(
				LayoutClassedModelUsageTable.INSTANCE.classPK
			).from(
				LayoutClassedModelUsageTable.INSTANCE
			).where(
				LayoutClassedModelUsageTable.INSTANCE.companyId.eq(
					companyId
				).and(
					LayoutClassedModelUsageTable.INSTANCE.classNameId.in(
						DSLQueryFactoryUtil.select(
							ClassNameTable.INSTANCE.classNameId
						).from(
							ClassNameTable.INSTANCE
						).where(
							ClassNameTable.INSTANCE.value.like(
								ObjectDefinitionConstants.
									CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION +
										StringPool.PERCENT)
						))
				)
			));

		objectEntryIds.addAll(
			TransformUtil.transform(classPKs, String::valueOf));
	}

	private void _addManyToManyObjectEntryIds(
			long companyId, Set<String> objectEntryIds)
		throws Exception {

		Set<Long> objectRelationshipIds = new HashSet<>();

		for (ObjectDefinition objectDefinition :
				_objectDefinitionLocalService.getObjectDefinitions(
					companyId, true, WorkflowConstants.STATUS_APPROVED)) {

			if (!objectDefinition.isCMS()) {
				continue;
			}

			for (ObjectRelationship objectRelationship :
					_objectRelationshipLocalService.getObjectRelationships(
						objectDefinition.getObjectDefinitionId(),
						ObjectRelationshipConstants.TYPE_MANY_TO_MANY)) {

				if (!objectRelationshipIds.add(
						objectRelationship.getObjectRelationshipId())) {

					continue;
				}

				DynamicObjectRelationshipMappingTable
					dynamicObjectRelationshipMappingTable =
						DynamicObjectRelationshipMappingTableFactory.create(
							objectRelationship);

				List<Object[]> rows = _objectEntryLocalService.dslQuery(
					DSLQueryFactoryUtil.select(
						dynamicObjectRelationshipMappingTable.
							getPrimaryKeyColumn1(),
						dynamicObjectRelationshipMappingTable.
							getPrimaryKeyColumn2()
					).from(
						dynamicObjectRelationshipMappingTable
					));

				for (Object[] row : rows) {
					objectEntryIds.add(String.valueOf(row[0]));
					objectEntryIds.add(String.valueOf(row[1]));
				}
			}
		}
	}

	private void _addNavigationMenuExternalReferenceCodes(
		long companyId, Set<String> externalReferenceCodes) {

		List<SiteNavigationMenuItem> siteNavigationMenuItems =
			_siteNavigationMenuItemLocalService.dslQuery(
				DSLQueryFactoryUtil.select(
					SiteNavigationMenuItemTable.INSTANCE
				).from(
					SiteNavigationMenuItemTable.INSTANCE
				).where(
					SiteNavigationMenuItemTable.INSTANCE.companyId.eq(
						companyId
					).and(
						SiteNavigationMenuItemTable.INSTANCE.type.like(
							ObjectDefinitionConstants.
								CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION +
									StringPool.PERCENT)
					)
				));

		for (SiteNavigationMenuItem siteNavigationMenuItem :
				siteNavigationMenuItems) {

			UnicodeProperties unicodeProperties =
				UnicodePropertiesBuilder.create(
					true
				).fastLoad(
					siteNavigationMenuItem.getTypeSettings()
				).build();

			String externalReferenceCode = unicodeProperties.getProperty(
				"externalReferenceCode");

			if (Validator.isNull(externalReferenceCode)) {
				continue;
			}

			externalReferenceCodes.add(externalReferenceCode);
		}
	}

	private void _addOutboundLinks(
		long companyId, String outboundLinkPrefix, Set<String> outboundLinks) {

		CMSSearchUtil.addBucketKeys(
			includeExcludeClause -> CMSSearchUtil.getTermsAggregationResult(
				_aggregations, "outboundLinks", includeExcludeClause, _searcher,
				_getSearchRequestBuilder(
					CMSSearchUtil.getReferrersBooleanQuery(), companyId),
				10000),
			outboundLinks, outboundLinkPrefix);
	}

	private BooleanQuery _getBaseBooleanQuery() {
		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		booleanQuery.addFilterQueryClauses(
			CMSSearchUtil.getTermsQuery("cms_section", "contents", "files"),
			QueriesUtil.term(Field.STATUS, WorkflowConstants.STATUS_APPROVED),
			QueriesUtil.term("rootDescendantNode", false));

		return booleanQuery;
	}

	private SearchRequestBuilder _getSearchRequestBuilder(
		BooleanQuery booleanQuery, long companyId) {

		return _searchRequestBuilderFactory.builder(
		).companyId(
			companyId
		).emptySearchEnabled(
			true
		).query(
			booleanQuery
		).withSearchContext(
			searchContext -> searchContext.setAttribute(
				Field.STATUS, WorkflowConstants.STATUS_ANY)
		);
	}

	private SearchRequestBuilder _getSearchRequestBuilder(
		long companyId, Long[] groupIds) {

		BooleanQuery booleanQuery = _getBaseBooleanQuery();

		Set<String> objectEntryIds = new HashSet<>();

		_addAssetLinkObjectEntryIds(companyId, objectEntryIds);
		_addLayoutObjectEntryIds(companyId, objectEntryIds);

		try {
			_addManyToManyObjectEntryIds(companyId, objectEntryIds);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get the object entries of many to many " +
						"relationships",
					exception);
			}
		}

		Set<String> externalReferenceCodes = new HashSet<>();

		_addNavigationMenuExternalReferenceCodes(
			companyId, externalReferenceCodes);

		Set<String> outboundLinks = new HashSet<>();

		_addOutboundLinks(
			companyId, _OBJECT_ENTRY_EXTERNAL_REFERENCE_CODE_TOKEN_PREFIX,
			outboundLinks);
		_addOutboundLinks(
			companyId, _OBJECT_ENTRY_ID_TOKEN_PREFIX, outboundLinks);

		for (String outboundLink : outboundLinks) {
			if (outboundLink.startsWith(
					_OBJECT_ENTRY_EXTERNAL_REFERENCE_CODE_TOKEN_PREFIX)) {

				externalReferenceCodes.add(
					outboundLink.substring(
						_OBJECT_ENTRY_EXTERNAL_REFERENCE_CODE_TOKEN_PREFIX.
							length()));
			}
			else if (outboundLink.startsWith(_OBJECT_ENTRY_ID_TOKEN_PREFIX)) {
				objectEntryIds.add(
					outboundLink.substring(
						_OBJECT_ENTRY_ID_TOKEN_PREFIX.length()));
			}
		}

		booleanQuery.addMustNotQueryClauses(
			CMSSearchUtil.getTermsQuery(
				Field.ENTRY_CLASS_PK, objectEntryIds.toArray(new String[0])),
			CMSSearchUtil.getTermsQuery(
				"externalReferenceCode",
				externalReferenceCodes.toArray(new String[0])));

		return _getSearchRequestBuilder(
			booleanQuery, companyId
		).groupIds(
			ArrayUtil.toArray(groupIds)
		);
	}

	private static final String
		_OBJECT_ENTRY_EXTERNAL_REFERENCE_CODE_TOKEN_PREFIX =
			CMSOutboundLinksUtil.getObjectEntryExternalReferenceCodeToken(
				StringPool.BLANK);

	private static final String _OBJECT_ENTRY_ID_TOKEN_PREFIX =
		StringUtil.replaceLast(
			CMSOutboundLinksUtil.getObjectEntryIdToken(0), '0',
			StringPool.BLANK);

	private static final Log _log = LogFactoryUtil.getLog(
		UnlinkedAssetSearcher.class);

	private final Aggregations _aggregations;
	private final AssetLinkLocalService _assetLinkLocalService;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;
	private final SearchRequestBuilderFactory _searchRequestBuilderFactory;
	private final Searcher _searcher;
	private final SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

}