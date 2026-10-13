/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.link;

import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.model.ObjectEntryVersionTable;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.IncludeExcludeClause;
import com.liferay.portal.search.aggregation.bucket.TermsAggregation;
import com.liferay.portal.search.aggregation.bucket.TermsAggregationResult;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Jürgen Kappler
 */
public class BrokenLinkAssetSearcher {

	public BrokenLinkAssetSearcher(
		Aggregations aggregations,
		ObjectEntryLocalService objectEntryLocalService, Searcher searcher,
		SearchRequestBuilderFactory searchRequestBuilderFactory) {

		_aggregations = aggregations;
		_objectEntryLocalService = objectEntryLocalService;
		_searcher = searcher;
		_searchRequestBuilderFactory = searchRequestBuilderFactory;
	}

	public Map<String, BrokenLinkTarget> getBrokenLinkTargetsMap(
		long companyId, Long[] objectDefinitionIds,
		Long[] selectedSpaceGroupIds, Long[] spaceGroupIds) {

		Map<String, BrokenLinkTarget> brokenLinkTargetsMap =
			new LinkedHashMap<>();

		_putBrokenLinkTargets(
			brokenLinkTargetsMap, companyId, objectDefinitionIds,
			_getDraftPredicate(), spaceGroupIds,
			WorkflowConstants.STATUS_DRAFT);
		_putBrokenLinkTargets(
			brokenLinkTargetsMap, companyId, objectDefinitionIds,
			_getExpiredPredicate(), spaceGroupIds,
			WorkflowConstants.STATUS_EXPIRED);
		_putBrokenLinkTargets(
			brokenLinkTargetsMap, companyId, objectDefinitionIds,
			ObjectEntryTable.INSTANCE.status.eq(
				WorkflowConstants.STATUS_IN_TRASH),
			spaceGroupIds, WorkflowConstants.STATUS_IN_TRASH);

		_putDeletedBrokenLinkTargets(
			brokenLinkTargetsMap, companyId, selectedSpaceGroupIds);

		return brokenLinkTargetsMap;
	}

	public long getCount(
		long companyId, Long[] groupIds, Set<String> outboundLinkTokens) {

		SearchResponse searchResponse = _searcher.search(
			_getSearchRequestBuilder(
				companyId, groupIds, outboundLinkTokens
			).size(
				0
			).build());

		return searchResponse.getCount();
	}

	public SearchResponse search(
		long companyId, Long[] groupIds, String languageId,
		Set<String> outboundLinkTokens, Pagination pagination, String search,
		Sort[] sorts) {

		SearchRequestBuilder searchRequestBuilder = _getSearchRequestBuilder(
			companyId, groupIds, outboundLinkTokens);

		searchRequestBuilder.addSelectedFieldNames(
			"outboundLinks", Field.ENTRY_CLASS_PK, "objectDefinitionId",
			"objectEntryTitle",
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

	private void _addOutboundLinks(
		long companyId, String outboundLinkPrefix, List<String> outboundLinks,
		Long[] selectedSpaceGroupIds) {

		Set<String> excludedNextCharacters = new TreeSet<>();

		while (true) {
			TermsAggregationResult termsAggregationResult =
				_getTermsAggregationResult(
					companyId, excludedNextCharacters, outboundLinkPrefix,
					selectedSpaceGroupIds);

			if (termsAggregationResult == null) {
				return;
			}

			Set<String> nextCharacters = _getNextCharacters(
				outboundLinkPrefix, termsAggregationResult);

			if ((termsAggregationResult.getOtherDocCounts() == 0) ||
				nextCharacters.isEmpty()) {

				for (Bucket bucket : termsAggregationResult.getBuckets()) {
					outboundLinks.add(bucket.getKey());
				}

				return;
			}

			for (String nextCharacter : nextCharacters) {
				_addOutboundLinks(
					companyId, outboundLinkPrefix + nextCharacter,
					outboundLinks, selectedSpaceGroupIds);
			}

			excludedNextCharacters.addAll(nextCharacters);
		}
	}

	private String _escapeRegex(String value) {
		StringBundler sb = new StringBundler(value.length() * 2);

		for (int i = 0; i < value.length();) {
			int codePoint = value.codePointAt(i);

			if ((codePoint > 127) || !Character.isLetterOrDigit(codePoint)) {
				sb.append(StringPool.BACK_SLASH);
			}

			sb.append(new String(Character.toChars(codePoint)));

			i += Character.charCount(codePoint);
		}

		return sb.toString();
	}

	private Predicate _getDraftPredicate() {
		return ObjectEntryTable.INSTANCE.status.eq(
			WorkflowConstants.STATUS_DRAFT
		).and(
			ObjectEntryTable.INSTANCE.objectEntryId.notIn(
				_getObjectEntryIdsDSLQuery(WorkflowConstants.STATUS_APPROVED))
		).and(
			ObjectEntryTable.INSTANCE.objectEntryId.notIn(
				_getObjectEntryIdsDSLQuery(WorkflowConstants.STATUS_EXPIRED))
		);
	}

	private String _getExcludeRegex(
		String escapedOutboundLinkPrefix, Set<String> excludedNextCharacters) {

		if (excludedNextCharacters.isEmpty()) {
			return null;
		}

		return StringBundler.concat(
			escapedOutboundLinkPrefix, "[",
			_escapeRegex(
				StringUtil.merge(excludedNextCharacters, StringPool.BLANK)),
			"].*");
	}

	private Set<String> _getExistingExternalReferenceCodes(
		long companyId, String[] externalReferenceCodes) {

		Set<String> existingExternalReferenceCodes = new HashSet<>();

		for (int i = 0; i < externalReferenceCodes.length;
			 i += _EXTERNAL_REFERENCE_CODES_CHUNK_SIZE) {

			List<String> chunkExternalReferenceCodes =
				_objectEntryLocalService.dslQuery(
					DSLQueryFactoryUtil.selectDistinct(
						ObjectEntryTable.INSTANCE.externalReferenceCode
					).from(
						ObjectEntryTable.INSTANCE
					).where(
						ObjectEntryTable.INSTANCE.companyId.eq(
							companyId
						).and(
							ObjectEntryTable.INSTANCE.externalReferenceCode.in(
								ArrayUtil.subset(
									externalReferenceCodes, i,
									Math.min(
										i +
											_EXTERNAL_REFERENCE_CODES_CHUNK_SIZE,
										externalReferenceCodes.length)))
						)
					));

			existingExternalReferenceCodes.addAll(chunkExternalReferenceCodes);
		}

		return existingExternalReferenceCodes;
	}

	private Predicate _getExpiredPredicate() {
		return ObjectEntryTable.INSTANCE.status.eq(
			WorkflowConstants.STATUS_EXPIRED
		).or(
			ObjectEntryTable.INSTANCE.status.eq(
				WorkflowConstants.STATUS_DRAFT
			).and(
				ObjectEntryTable.INSTANCE.objectEntryId.in(
					_getObjectEntryIdsDSLQuery(
						WorkflowConstants.STATUS_EXPIRED))
			).and(
				ObjectEntryTable.INSTANCE.objectEntryId.notIn(
					_getObjectEntryIdsDSLQuery(
						WorkflowConstants.STATUS_APPROVED))
			)
		);
	}

	private String _getExternalReferenceCode(String outboundLink) {
		return outboundLink.substring(
			_OBJECT_ENTRY_EXTERNAL_REFERENCE_CODE_TOKEN_PREFIX.length());
	}

	private Set<String> _getNextCharacters(
		String outboundLinkPrefix,
		TermsAggregationResult termsAggregationResult) {

		Set<String> nextCharacters = new TreeSet<>();

		for (Bucket bucket : termsAggregationResult.getBuckets()) {
			String key = bucket.getKey();

			if (key.length() > outboundLinkPrefix.length()) {
				nextCharacters.add(
					new String(
						Character.toChars(
							key.codePointAt(outboundLinkPrefix.length()))));
			}
		}

		return nextCharacters;
	}

	private DSLQuery _getObjectEntryIdsDSLQuery(int status) {
		return DSLQueryFactoryUtil.select(
			ObjectEntryVersionTable.INSTANCE.objectEntryId
		).from(
			ObjectEntryVersionTable.INSTANCE
		).where(
			ObjectEntryVersionTable.INSTANCE.objectEntryId.eq(
				ObjectEntryTable.INSTANCE.objectEntryId
			).and(
				ObjectEntryVersionTable.INSTANCE.status.eq(status)
			)
		);
	}

	private List<Object[]> _getObjectEntryObjectsList(
		long companyId, Long[] objectDefinitionIds, Predicate predicate,
		Long[] spaceGroupIds) {

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
					ObjectEntryTable.INSTANCE.headObjectEntryId.eq(
						ObjectEntryTable.INSTANCE.objectEntryId)
				).and(
					ObjectEntryTable.INSTANCE.objectDefinitionId.in(
						objectDefinitionIds)
				).and(
					predicate.withParentheses()
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

	private BooleanQuery _getReferrersBooleanQuery() {
		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		booleanQuery.addFilterQueryClauses(
			_getTermsQuery("cms_section", "contents", "files"),
			_getTermsQuery(
				Field.STATUS,
				ArrayUtil.toStringArray(CMSWorkflowConstants.STATUSES)),
			QueriesUtil.term("rootDescendantNode", false));
		booleanQuery.addMustNotQueryClauses(
			QueriesUtil.term(Field.STATUS, WorkflowConstants.STATUS_EXPIRED));

		return booleanQuery;
	}

	private SearchRequestBuilder _getSearchRequestBuilder(
		BooleanQuery booleanQuery, long companyId, Long[] groupIds) {

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

	private SearchRequestBuilder _getSearchRequestBuilder(
		long companyId, Long[] groupIds, Set<String> outboundLinkTokens) {

		BooleanQuery booleanQuery = _getReferrersBooleanQuery();

		booleanQuery.addFilterQueryClauses(
			_getOutboundLinksBooleanQuery(outboundLinkTokens));

		return _getSearchRequestBuilder(booleanQuery, companyId, groupIds);
	}

	private TermsAggregationResult _getTermsAggregationResult(
		long companyId, Set<String> excludedNextCharacters,
		String outboundLinkPrefix, Long[] selectedSpaceGroupIds) {

		String escapedOutboundLinkPrefix = _escapeRegex(outboundLinkPrefix);

		String excludeRegex = _getExcludeRegex(
			escapedOutboundLinkPrefix, excludedNextCharacters);
		String includeRegex = escapedOutboundLinkPrefix + ".*";

		TermsAggregation termsAggregation = _aggregations.terms(
			"outboundLinks", "outboundLinks");

		termsAggregation.setIncludeExcludeClause(
			new IncludeExcludeClause() {

				@Override
				public String getExcludeRegex() {
					return excludeRegex;
				}

				@Override
				public String[] getExcludedValues() {
					return null;
				}

				@Override
				public String getIncludeRegex() {
					return includeRegex;
				}

				@Override
				public String[] getIncludedValues() {
					return null;
				}

			});
		termsAggregation.setSize(10000);

		SearchRequestBuilder searchRequestBuilder = _getSearchRequestBuilder(
			_getReferrersBooleanQuery(), companyId, selectedSpaceGroupIds);

		SearchResponse searchResponse = _searcher.search(
			searchRequestBuilder.addAggregation(
				termsAggregation
			).size(
				0
			).build());

		return (TermsAggregationResult)searchResponse.getAggregationResult(
			"outboundLinks");
	}

	private TermsQuery _getTermsQuery(String fieldName, String... values) {
		TermsQuery termsQuery = QueriesUtil.terms(fieldName);

		termsQuery.addValues(values);

		return termsQuery;
	}

	private void _putBrokenLinkTargets(
		Map<String, BrokenLinkTarget> brokenLinkTargetsMap, long companyId,
		Long[] objectDefinitionIds, Predicate predicate, Long[] spaceGroupIds,
		int status) {

		for (Object[] objects :
				_getObjectEntryObjectsList(
					companyId, objectDefinitionIds, predicate, spaceGroupIds)) {

			long objectEntryId = GetterUtil.getLong(objects[1]);

			BrokenLinkTarget brokenLinkTarget = new BrokenLinkTarget(
				objectEntryId, status);

			brokenLinkTargetsMap.put(
				CMSOutboundLinksUtil.getObjectEntryExternalReferenceCodeToken(
					GetterUtil.getString(objects[0])),
				brokenLinkTarget);
			brokenLinkTargetsMap.put(
				CMSOutboundLinksUtil.getObjectEntryIdToken(objectEntryId),
				brokenLinkTarget);
		}
	}

	private void _putDeletedBrokenLinkTargets(
		Map<String, BrokenLinkTarget> brokenLinkTargetsMap, long companyId,
		Long[] selectedSpaceGroupIds) {

		List<String> outboundLinks = new ArrayList<>();

		_addOutboundLinks(
			companyId, _OBJECT_ENTRY_EXTERNAL_REFERENCE_CODE_TOKEN_PREFIX,
			outboundLinks, selectedSpaceGroupIds);

		outboundLinks.removeIf(brokenLinkTargetsMap::containsKey);

		if (outboundLinks.isEmpty()) {
			return;
		}

		Set<String> existingExternalReferenceCodes =
			_getExistingExternalReferenceCodes(
				companyId,
				TransformUtil.transformToArray(
					outboundLinks, this::_getExternalReferenceCode,
					String.class));

		for (String outboundLink : outboundLinks) {
			if (!existingExternalReferenceCodes.contains(
					_getExternalReferenceCode(outboundLink))) {

				brokenLinkTargetsMap.put(
					outboundLink, BrokenLinkTarget.deleted());
			}
		}
	}

	private static final int _EXTERNAL_REFERENCE_CODES_CHUNK_SIZE = 1000;

	private static final String
		_OBJECT_ENTRY_EXTERNAL_REFERENCE_CODE_TOKEN_PREFIX =
			CMSOutboundLinksUtil.getObjectEntryExternalReferenceCodeToken(
				StringPool.BLANK);

	private static final int _TERMS_QUERY_CHUNK_SIZE = 4096;

	private final Aggregations _aggregations;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final SearchRequestBuilderFactory _searchRequestBuilderFactory;
	private final Searcher _searcher;

}