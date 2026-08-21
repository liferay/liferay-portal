/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.links;

import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.query.TermsQuery;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.site.cms.site.initializer.constants.CMSWorkflowConstants;
import com.liferay.site.cms.site.initializer.util.CMSOutboundLinksUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Jürgen Kappler
 */
public class BrokenLinkAssetSearcher {

	public BrokenLinkAssetSearcher(
		ObjectEntryLocalService objectEntryLocalService, Searcher searcher,
		SearchRequestBuilderFactory searchRequestBuilderFactory) {

		_objectEntryLocalService = objectEntryLocalService;
		_searcher = searcher;
		_searchRequestBuilderFactory = searchRequestBuilderFactory;
	}

	public long getCount(
		long companyId, Long[] groupIds, Set<String> outboundLinkTokens) {

		SearchResponse searchResponse = _searcher.search(
			_getSearchRequestBuilder(
				companyId, groupIds, outboundLinkTokens
			).build());

		return searchResponse.getCount();
	}

	public Map<String, Long> getExpiredAssetObjectEntryIdsMap(
		long companyId, Long[] objectDefinitionIds, Long[] spaceGroupIds) {

		Map<String, Long> objectEntryIdsMap = new LinkedHashMap<>();

		for (Object[] objects :
				_getObjectEntryObjectsList(
					companyId, objectDefinitionIds, spaceGroupIds)) {

			long objectEntryId = GetterUtil.getLong(objects[1]);

			objectEntryIdsMap.put(
				CMSOutboundLinksUtil.getObjectEntryExternalReferenceCodeToken(
					GetterUtil.getString(objects[0])),
				objectEntryId);
			objectEntryIdsMap.put(
				CMSOutboundLinksUtil.getObjectEntryIdToken(objectEntryId),
				objectEntryId);
		}

		return objectEntryIdsMap;
	}

	public SearchResponse search(
		long companyId, Long[] groupIds, String languageId,
		Set<String> outboundLinkTokens, Pagination pagination, String search,
		Sort[] sorts) {

		SearchRequestBuilder searchRequestBuilder = _getSearchRequestBuilder(
			companyId, groupIds, outboundLinkTokens);

		int startPosition = Math.min(
			pagination.getStartPosition(), _MAX_RESULT_WINDOW);

		if ((pagination.getStartPosition() >= _MAX_RESULT_WINDOW) &&
			_log.isWarnEnabled()) {

			_log.warn(
				StringBundler.concat(
					"Requested start position ", pagination.getStartPosition(),
					" reaches the maximum result window ", _MAX_RESULT_WINDOW,
					", so the page is empty"));
		}

		searchRequestBuilder.addSelectedFieldNames(
			"outboundLinks", Field.ENTRY_CLASS_PK, "objectDefinitionId",
			"objectEntryTitle",
			Field.getLocalizedName(languageId, "objectEntryTitle")
		).from(
			startPosition
		).size(
			Math.min(
				pagination.getPageSize(), _MAX_RESULT_WINDOW - startPosition)
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

	private List<Object[]> _getObjectEntryObjectsList(
		long companyId, Long[] objectDefinitionIds, Long[] spaceGroupIds) {

		return _objectEntryLocalService.dslQuery(
			DSLQueryFactoryUtil.select(
				ObjectEntryTable.INSTANCE.externalReferenceCode,
				ObjectEntryTable.INSTANCE.objectEntryId
			).from(
				ObjectEntryTable.INSTANCE
			).where(
				ObjectEntryTable.INSTANCE.companyId.eq(
					companyId
				).and(
					ObjectEntryTable.INSTANCE.groupId.in(spaceGroupIds)
				).and(
					ObjectEntryTable.INSTANCE.objectDefinitionId.in(
						objectDefinitionIds)
				).and(
					ObjectEntryTable.INSTANCE.status.eq(
						WorkflowConstants.STATUS_EXPIRED)
				)
			));
	}

	private BooleanQuery _getOutboundLinksBooleanQuery(
		Set<String> outboundLinkTokens) {

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		String[] values = outboundLinkTokens.toArray(new String[0]);

		for (int i = 0; i < values.length; i += _TERMS_QUERY_CHUNK_SIZE) {
			booleanQuery.addShouldQueryClauses(
				_getTermsQuery(
					"outboundLinks",
					ArrayUtil.subset(
						values, i,
						Math.min(i + _TERMS_QUERY_CHUNK_SIZE, values.length))));
		}

		booleanQuery.setMinimumShouldMatch(1);

		return booleanQuery;
	}

	private SearchRequestBuilder _getSearchRequestBuilder(
		long companyId, Long[] groupIds, Set<String> outboundLinkTokens) {

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		booleanQuery.addFilterQueryClauses(
			_getOutboundLinksBooleanQuery(outboundLinkTokens),
			_getTermsQuery("cms_section", "contents", "files"),
			_getTermsQuery(
				Field.STATUS,
				ArrayUtil.toStringArray(CMSWorkflowConstants.STATUSES)),
			QueriesUtil.term("rootDescendantNode", false));
		booleanQuery.addMustNotQueryClauses(
			QueriesUtil.term(Field.STATUS, WorkflowConstants.STATUS_EXPIRED));

		return _searchRequestBuilderFactory.builder(
		).companyId(
			companyId
		).emptySearchEnabled(
			true
		).groupIds(
			ArrayUtil.toArray(groupIds)
		).query(
			booleanQuery
		).withSearchContext(
			searchContext -> searchContext.setAttribute(
				Field.STATUS, WorkflowConstants.STATUS_ANY)
		);
	}

	private TermsQuery _getTermsQuery(String fieldName, String... values) {
		TermsQuery termsQuery = QueriesUtil.terms(fieldName);

		termsQuery.addValues(values);

		return termsQuery;
	}

	private static final int _MAX_RESULT_WINDOW = 10000;

	private static final int _TERMS_QUERY_CHUNK_SIZE = 4096;

	private static final Log _log = LogFactoryUtil.getLog(
		BrokenLinkAssetSearcher.class);

	private final ObjectEntryLocalService _objectEntryLocalService;
	private final Searcher _searcher;
	private final SearchRequestBuilderFactory _searchRequestBuilderFactory;

}