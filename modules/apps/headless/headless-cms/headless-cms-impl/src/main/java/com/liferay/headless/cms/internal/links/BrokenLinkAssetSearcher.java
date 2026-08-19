/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.links;

import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.query.TermsQuery;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.site.cms.site.initializer.constants.CMSWorkflowConstants;
import com.liferay.site.cms.site.initializer.util.CMSOutboundLinksUtil;

import java.util.ArrayList;
import java.util.List;

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
		long companyId, Long[] groupIds, List<String> outboundLinkTokens) {

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		booleanQuery.addFilterQueryClauses(
			_getOutboundLinksBooleanQuery(outboundLinkTokens),
			_getTermsQuery("cms_section", "contents", "files"),
			_getTermsQuery(
				Field.STATUS,
				ArrayUtil.toStringArray(CMSWorkflowConstants.STATUSES)),
			QueriesUtil.term("rootDescendantNode", false));

		SearchResponse searchResponse = _searcher.search(
			_searchRequestBuilderFactory.builder(
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
			).build());

		return searchResponse.getCount();
	}

	public List<String> getExpiredAssetTokens(
		long companyId, Long[] objectDefinitionIds) {

		List<String> expiredAssetTokens = new ArrayList<>();

		List<Object[]> results = _objectEntryLocalService.dslQuery(
			DSLQueryFactoryUtil.select(
				ObjectEntryTable.INSTANCE.externalReferenceCode,
				ObjectEntryTable.INSTANCE.objectEntryId
			).from(
				ObjectEntryTable.INSTANCE
			).where(
				ObjectEntryTable.INSTANCE.companyId.eq(
					companyId
				).and(
					ObjectEntryTable.INSTANCE.objectDefinitionId.in(
						objectDefinitionIds)
				).and(
					ObjectEntryTable.INSTANCE.status.eq(
						WorkflowConstants.STATUS_EXPIRED)
				)
			));

		for (Object[] objects : results) {
			expiredAssetTokens.add(
				CMSOutboundLinksUtil.getObjectEntryExternalReferenceCodeToken(
					GetterUtil.getString(objects[0])));
			expiredAssetTokens.add(
				CMSOutboundLinksUtil.getObjectEntryIdToken(
					GetterUtil.getLong(objects[1])));
		}

		return expiredAssetTokens;
	}

	private BooleanQuery _getOutboundLinksBooleanQuery(
		List<String> outboundLinkTokens) {

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

	private TermsQuery _getTermsQuery(String fieldName, String... values) {
		TermsQuery termsQuery = QueriesUtil.terms(fieldName);

		termsQuery.addValues(values);

		return termsQuery;
	}

	private static final int _TERMS_QUERY_CHUNK_SIZE = 4096;

	private final ObjectEntryLocalService _objectEntryLocalService;
	private final Searcher _searcher;
	private final SearchRequestBuilderFactory _searchRequestBuilderFactory;

}